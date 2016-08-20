package radarlivre.com.radarlivreandroid.network.serialization;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by felipe on 20/08/16.
 */
public class ListDeserializer<T> implements JsonDeserializer<List<T>> {
    @Override
    public List<T> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        List<T> objects = new Gson().fromJson(json, new TypeToken<List<T>>() {}.getType());

        return objects;

    }
}
