package net.blossom.utils;

import com.google.gson.JsonObject;

public interface JsonSerializer<T> {


    T create(JsonObject json);

    JsonObject serialize(T object);


}
