package br.com.ecommerce;

import com.google.gson.*;

import java.lang.reflect.Type;

public class MessageAdapter implements JsonSerializer<Message>, JsonDeserializer<Message> {
    @Override
    public JsonElement serialize(Message src, Type type, JsonSerializationContext context) {
        JsonObject object = new JsonObject();
        object.addProperty("type", src.getPayload().getClass().getName());
        object.add("payload",context.serialize(src.getPayload()));
        object.add("correlationId",context.serialize(src.getId()));
        return object;
    }

    @Override
    public Message deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        var obj = json.getAsJsonObject();
        var payloadType = obj.get("type").getAsString();
        var correlationId = (CorrelationId) context.deserialize(obj.get("correlationId"), CorrelationId.class);
        try {
            //to do use a "accept list for classes"
            var payload = context.deserialize(obj.get("payload"), Class.forName(payloadType));
            return new Message(correlationId,payload);
        } catch (ClassNotFoundException e) {
            throw new JsonParseException(e);
        }
    }
}
