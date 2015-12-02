package radarlivre.com.radarlivreandroid.network;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;
import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.WebSocket;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import radarlivre.com.radarlivreandroid.model.Airplane;
import radarlivre.com.radarlivreandroid.model.AirplaneInfo;
import radarlivre.com.radarlivreandroid.model.AirplanePosition;
import radarlivre.com.radarlivreandroid.network.interfaces.IUpdaterService;

/**
 * Created by felipe on 01/12/15.
 */
public class UpdaterService extends Service implements IUpdaterService {
    private static final int DEFAULT_UPDATE_PERIOD = 3000;
    private static final int DEFAULT_RECONNECT_DELAY = 2000;

    // private static final String DEFAULT_SERVER_URL = "ws://192.168.0.180:9999/Radar-Livre/websocket";
    private static final String DEFAULT_SERVER_URL = "ws://www.radarlivre.com:9999/Radar-Livre/websocket";
    private static final String REQUEST_GET_AIRPLANES = "GET_AIRPLANES";
    private static final String REQUEST_GET_AIRPORTS = "GET_AIRPORTS";
    private static final String REQUEST_GET_ROUTE = "GET_ROUTE";
    private static final String REQUEST_SEARCH = "SEARCH";
    private static final String RESPONSE_REQUEST_GET_AIRPLANES = "GET_AIRPLANES_RESPONSE:";
    private static final String RESPONSE_REQUEST_GET_AIRPORTS = "GET_AIRPORTS_RESPONSE:";
    private static final String RESPONSE_REQUEST_GET_ROUTE = "GET_ROUTE_RESPONSE:";
    private static final String RESPONSE_REQUEST_SEARCH = "SEARCH_RESPONSE:";

    private enum ConnectionState {DISCONNECTED, CONNECTED, CONNECTING};
    private boolean firstConnection = true;

    private WebSocket webSocket = null;
    private ConnectionState connectionState = null;

    private IUpdaterService.OnAirplanesUpdateListener onAirplanesUpdateListener = null;

    private List<Airplane> airplanes = new ArrayList<>();

    private Timer timer = null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new Controller();
    }

    @Override
    public void onCreate() {
        new Thread() {
            @Override
            public void run() {
                Log.i("DEBUG", "Criando serviço de atualização");

                if(timer != null)
                    timer.cancel();

                if(webSocket != null)
                    webSocket.close();

                setConnectionState(ConnectionState.DISCONNECTED);

                timer = new Timer();
                timer.schedule(
                        new TimerTask() {
                            @Override
                            public void run() {
                                doReconnectWebSocket();
                                doUpdateAirplanes();
                            }
                        },
                        0,
                        DEFAULT_UPDATE_PERIOD
                );
            }
        }.start();


        super.onCreate();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i("DEBUG", "Parando serviço de atualização");
        if(timer != null) {
            timer.cancel();
        }
        if(webSocket != null) {
            webSocket.close();
        }

        firstConnection = true;

        return super.onUnbind(intent);
    }

    @Override
    public void setOnAirplanesUpdateListener(OnAirplanesUpdateListener listener) {
        onAirplanesUpdateListener = listener;
    }

    public void setConnectionState(ConnectionState connectionState) {
        synchronized (connectionState) {
            this.connectionState = connectionState;
        }
    }

    public class Controller extends Binder {
        public IUpdaterService getUpdaterService() {
            return UpdaterService.this;
        }
    }

    // Auxiliary methods

    private void doUpdateAirplanes() {
        if(connectionState == ConnectionState.CONNECTED) {
            Log.i("DEBUG", "Requisitando Aviões");
            UpdaterService.this.webSocket.send(REQUEST_GET_AIRPLANES);
        }
    }

    @Override
    public void doGetRouteFrom(Airplane airplane) {
        if(connectionState == ConnectionState.CONNECTED) {
            Log.i("DEBUG", "Requisitando rota para o avião");
            UpdaterService.this.webSocket.send(REQUEST_GET_ROUTE + "(" + airplane.getHex() + ")");
        }
    }

    @Override
    public void doRemoveRouteFrom(Airplane airplane) {
        AirplanePosition pos = airplane.getLastPosition();
        airplane.getRoute().clear();
        airplane.addPosition(pos);
    }

    private synchronized void doReconnectWebSocket() {
        if(connectionState == ConnectionState.DISCONNECTED) {
            setConnectionState(ConnectionState.CONNECTING);

            if(onAirplanesUpdateListener != null) onAirplanesUpdateListener.onServerConnecting();

            final int delay;
            if(firstConnection) {
                firstConnection = false;
                delay = 0;
            } else {
                delay = DEFAULT_RECONNECT_DELAY;
            }

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    AsyncHttpClient.getDefaultInstance().websocket(DEFAULT_SERVER_URL, "radarlivre-protocol", new AsyncHttpClient.WebSocketConnectCallback() {
                        @Override
                        public void onCompleted(Exception ex, WebSocket webSocket) {
                            if (ex != null) {
                                setConnectionState(ConnectionState.DISCONNECTED);
                                if (onAirplanesUpdateListener != null)
                                    onAirplanesUpdateListener.onServerDisconnected();
                                ex.printStackTrace();
                            } else {
                                // On connected
                                connectionState = ConnectionState.CONNECTED;
                                if (onAirplanesUpdateListener != null)
                                    onAirplanesUpdateListener.onServerConnected();
                                if (UpdaterService.this.webSocket != null)
                                    UpdaterService.this.webSocket.close();
                                UpdaterService.this.webSocket = webSocket;
                                UpdaterService.this.webSocket.setStringCallback(new WebSocket.StringCallback() {
                                    @Override
                                    public void onStringAvailable(String s) {
                                        if(s.contains(RESPONSE_REQUEST_GET_AIRPLANES)) {
                                            s = s.replace(RESPONSE_REQUEST_GET_AIRPLANES, "");
                                            AirplaneInfo[] array = new Gson().fromJson(s, AirplaneInfo[].class);
                                            Log.i("DEBUG", "Aviões recebidos!");
                                            onAirplanesUpdate(array);
                                        } else if(s.contains(RESPONSE_REQUEST_GET_ROUTE)) {
                                            s = s.replace(RESPONSE_REQUEST_GET_ROUTE, "");
                                            AirplaneInfo[] array = new Gson().fromJson(s, AirplaneInfo[].class);
                                            Log.i("DEBUG", "Rota recebida!");
                                            onAirplanesUpdateRoute(array);
                                        }
                                    }
                                });
                                UpdaterService.this.webSocket.setClosedCallback(new CompletedCallback() {
                                    @Override
                                    public void onCompleted(Exception ex) {
                                        setConnectionState(ConnectionState.DISCONNECTED);
                                        if (onAirplanesUpdateListener != null)
                                            onAirplanesUpdateListener.onServerDisconnected();
                                    }
                                });
                            }
                        }
                    });
                }
            }, delay);
        }
    }

    private void onAirplanesUpdateRoute(AirplaneInfo[] airplaneInfos) {
        if(airplaneInfos.length > 0) {
            Airplane airplane = getAirplaneFronHex(airplaneInfos[0].getHex());
            AirplanePosition pos = airplane.getLastPosition();
            airplane.getRoute().clear();
            for(AirplaneInfo info: airplaneInfos)
                airplane.update(info);

            airplane.addPosition(pos);

            if (onAirplanesUpdateListener != null)
                onAirplanesUpdateListener.onAirplaneUpdated(airplane);
        }

    }

    private boolean infoContains(AirplaneInfo[] airplaneInfos, Airplane airplane) {
        for (AirplaneInfo info : airplaneInfos) {
            if(info.getHex().equals(airplane.getHex()))
                return true;
        }

        return false;
    }

    private void onAirplanesUpdate(AirplaneInfo[] airplaneInfos) {
        synchronized (airplanes) {
            for (AirplaneInfo info : airplaneInfos) {
                Airplane airplane = getAirplaneFronHex(info.getHex());
                if (airplane == null) {
                    airplane = new Airplane(info);
                    airplanes.add(airplane);

                    if (onAirplanesUpdateListener != null)
                        onAirplanesUpdateListener.onAirplaneAdded(airplane);
                } else {
                    airplane.update(info);
                    if (onAirplanesUpdateListener != null)
                        onAirplanesUpdateListener.onAirplaneUpdated(airplane);
                }
            }

            for (int i = 0; i < airplanes.size(); i++) {
                Airplane airplane = airplanes.get(i);

                if(!infoContains(airplaneInfos, airplane)) {
                    airplanes.remove(i--);
                    if (onAirplanesUpdateListener != null)
                        onAirplanesUpdateListener.onAirplaneRemoved(airplane);
                }
            }


        }
    }

    private Airplane getAirplaneFronHex(String hex) {
        for(Airplane airplane: airplanes)
            if(airplane.getHex().equals(hex))
                return airplane;
        return null;
    }

}
