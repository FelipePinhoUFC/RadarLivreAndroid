package radarlivre.com.radarlivreandroid.network.interfaces;

import java.util.List;

import radarlivre.com.radarlivreandroid.model.Airplane;

/**
 * Created by felipe on 01/12/15.
 */
public interface IUpdaterService {
    void setOnAirplanesUpdateListener(OnAirplanesUpdateListener listener);

    void doGetRouteFrom(Airplane airplane);
    void doRemoveRouteFrom(Airplane airplane);

    interface OnAirplanesUpdateListener {
        void onAirplanesUpdated(List<Airplane> airplanes);

        void onAirplaneAdded(Airplane airplane);
        void onAirplaneUpdated(Airplane airplane);
        void onAirplaneRemoved(Airplane airplane);

        void onServerConnected();
        void onServerDisconnected();
        void onServerConnecting();
    }
}
