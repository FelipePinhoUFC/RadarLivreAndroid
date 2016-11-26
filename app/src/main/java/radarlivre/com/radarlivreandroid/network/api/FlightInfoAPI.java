package radarlivre.com.radarlivreandroid.network.api;

import java.util.List;

import radarlivre.com.radarlivreandroid.model.FlightInfo;
import radarlivre.com.radarlivreandroid.network.listener.OnReceiveListener;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by felipe on 21/08/16.
 */
public class FlightInfoAPI extends ObjectAPI<FlightInfo> {

    public FlightInfoAPI(String serverHost) {
        super(FlightInfo.class, serverHost);
    }

    public void getFlightInfos(long maxUpdateDelay, double mapHeight, double mapZoom,
                               double mapTop, double mapBottom, double mapLeft, double mapRight,
                               OnReceiveListener<List<FlightInfo>> listener) {

        IFlightInfo iFlightInfo = getRetrofitToObjectList().create(IFlightInfo.class);
        makeCallList(iFlightInfo.getFlights(
                maxUpdateDelay, mapHeight, mapZoom, mapTop, mapBottom, mapLeft, mapRight), listener);
    }

    public interface IFlightInfo {
        @GET("api/flight_info/")
        Call<List<FlightInfo>> getFlights(
                @Query("max_update_delay") long maxUpdateDelay,
                @Query("map_height") double mapHeight, @Query("map_zoom") double mapZoom,
                @Query("top") double mapTop, @Query("bottom") double mapBottom,
                @Query("left") double mapLeft, @Query("right") double mapRight);

    }

}
