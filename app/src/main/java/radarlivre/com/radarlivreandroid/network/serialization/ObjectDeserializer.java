package radarlivre.com.radarlivreandroid.network.serialization;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Created by felipe on 20/08/16.
 */
public class ObjectDeserializer<T> implements JsonDeserializer<T> {

    private final Class<T> clazz;

    public ObjectDeserializer(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        T object = new Gson().fromJson(json, this.clazz);

        return object;

    }
}
