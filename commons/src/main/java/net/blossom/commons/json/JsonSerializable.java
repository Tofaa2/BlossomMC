package net.blossom.commons.json;

import com.google.gson.JsonObject;

public interface JsonSerializable<T> {

    T fromJson(JsonObject json);

    JsonObject toJson();


}
