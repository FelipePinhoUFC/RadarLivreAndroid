package radarlivre.com.radarlivreandroid.network.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import radarlivre.com.radarlivreandroid.network.listener.OnReceiveListener;
import radarlivre.com.radarlivreandroid.network.serialization.ListDeserializer;
import radarlivre.com.radarlivreandroid.network.serialization.ObjectDeserializer;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by felipe on 20/08/16.
 */
public abstract class ObjectAPI<T> {
    private Class<T> clazz;
    private Class<T> apiClazz;
    private String serverHost = "localhost";
    private int serverPort = 80;

    public ObjectAPI(Class<T> clazz, Class<T> apiClazz, String serverHost, int serverPort) {
        this.clazz = clazz;
        this.apiClazz = apiClazz;
        this.serverHost = serverHost;
        this.serverPort = serverPort;
    }

    private Retrofit getRetrofitToObject() {
        Gson gson = new GsonBuilder().registerTypeAdapter(
                clazz, new ObjectDeserializer<>(clazz)
        ).create();

        Retrofit retrofit = new Retrofit
                .Builder()
                .baseUrl(serverHost)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        return retrofit;
    }

    private Retrofit getRetrofitToObjectList() {
        Gson gson = new GsonBuilder().registerTypeAdapter(
                new TypeToken<T>() {}.getType(),
                new ListDeserializer<T>()
        ).create();

        Retrofit retrofit = new Retrofit
                .Builder()
                .baseUrl(serverHost)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        return retrofit;
    }

    public void get(final OnReceiveListener<T> listener) {
        Call<T> call = onGetObject(getRetrofitToObject());
        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Response<T> response, Retrofit retrofit) {
                if(response.code() < 400 && response.body() != null)
                    listener.onReceivedSuccessfully(response.body());
                else
                    listener.onReceivedFailed();

            }

            @Override
            public void onFailure(Throwable t) {
                listener.onReceivedFailed();
            }
        });
    }

    public void getAll(final OnReceiveListener<List<T>> listener) {
        Call<List<T>> call = onGetObjectList(getRetrofitToObjectList());
        call.enqueue(new Callback<List<T>>() {
            @Override
            public void onResponse(Response<List<T>> response, Retrofit retrofit) {
                if(response.code() < 400 && response.body() != null)
                    listener.onReceivedSuccessfully(response.body());
                else
                    listener.onReceivedFailed();

            }

            @Override
            public void onFailure(Throwable t) {
                listener.onReceivedFailed();
            }
        });
    }

    public abstract Call<T> onGetObject(Retrofit retrofit);
    public abstract Call<List<T>> onGetObjectList(Retrofit retrofit);

}
