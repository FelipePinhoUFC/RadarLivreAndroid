package radarlivre.com.radarlivreandroid.network.api;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import radarlivre.com.radarlivreandroid.network.listener.OnReceiveListener;
import radarlivre.com.radarlivreandroid.network.serialization.ListDeserializer;
import radarlivre.com.radarlivreandroid.network.serialization.ObjectDeserializer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by felipe on 20/08/16.
 */
public abstract class ObjectAPI<T> {
    private Class<T> clazz;
    private String serverHost = "www.radarlivre.com";

    public ObjectAPI(Class<T> clazz, String serverHost) {
        this.clazz = clazz;
        this.serverHost = serverHost;
    }

    protected Retrofit getRetrofitToObject() {
        Gson gson = new GsonBuilder().registerTypeAdapter(
                clazz, new ObjectDeserializer<>(clazz)
        ).create();

        Retrofit retrofit = new Retrofit
                .Builder()
                .baseUrl(String.format("http://%s/", serverHost))
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        return retrofit;
    }

    protected Retrofit getRetrofitToObjectList() {
        Gson gson = new GsonBuilder().registerTypeAdapter(
                new TypeToken<T>() {}.getType(),
                new ListDeserializer<T>()
        ).create();

        Retrofit retrofit = new Retrofit
                .Builder()
                .baseUrl(String.format("http://%s/", serverHost))
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        return retrofit;
    }

    protected void makeCallObject(final Call<T> call, final OnReceiveListener<T> listener) {
        if (call != null) {
            call.enqueue(new Callback<T>() {

                @Override
                public void onResponse(Call<T> call, Response<T> response) {

                    if (response.code() < 400 && response.body() != null)
                        listener.onReceivedSuccessfully(response.body());
                    else
                        listener.onReceivedFailed();

                }

                @Override
                public void onFailure(Call<T> call, Throwable t) {
                    t.printStackTrace();
                    listener.onReceivedFailed();
                }
            });
        }
    }

    protected void makeCallList(final Call<List<T>> call, final OnReceiveListener<List<T>> listener) {
        if(call != null) {
            call.enqueue(new Callback<List<T>>() {
                @Override
                public void onResponse(Call<List<T>> call, Response<List<T>> response) {
                    if (response.code() < 400 && response.body() != null)
                        listener.onReceivedSuccessfully(response.body());
                    else {
                        Log.i("DEBUG", String.format("%s: %s", response.code(), response.errorBody()));
                        listener.onReceivedFailed();
                    }
                }

                @Override
                public void onFailure(Call<List<T>> call, Throwable t) {
                    t.printStackTrace();
                    listener.onReceivedFailed();
                }
            });
        }
    }

}
