package br.com.ecommerce;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class MessageAdapter implements JsonSerializer<Message> {
    @Override
    public JsonElement serialize(Message src, Type type, JsonSerializationContext context) {
        JsonObject object = new JsonObject();
        object.addProperty("type", src.getPayload().getClass().getName());
        object.add("payload",context.serialize(src.getPayload()));
        object.add("correlationId",context.serialize(src.getId()));
        return object;
    }
}
