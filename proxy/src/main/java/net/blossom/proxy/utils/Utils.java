package net.blossom.proxy.utils;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jetbrains.annotations.Blocking;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public final class Utils {

    private static final String FROM_UUID_URL = "https://sessionserver.mojang.com/session/minecraft/profile/%s?unsigned=false";
    private static final String FROM_USERNAME_URL = "https://api.mojang.com/users/profiles/minecraft/%s";
    private static final Cache<String, JsonObject> URL_CACHE = Caffeine.newBuilder().expireAfterWrite(30L, TimeUnit.SECONDS).softValues().build();

    @Blocking
    public static @Nullable JsonObject fromUuid(@NotNull String uuid) {
        return retrieve(String.format("https://sessionserver.mojang.com/session/minecraft/profile/%s?unsigned=false", uuid));
    }

    @Blocking
    public static @Nullable JsonObject fromUsername(@NotNull String username) {
        return retrieve(String.format("https://api.mojang.com/users/profiles/minecraft/%s", username));
    }

    public static @Nullable String getUuid(@NotNull String username) {
        JsonObject jsonObject = fromUsername(username);
        return jsonObject == null ? null : jsonObject.get("id").getAsString();
    }

    public static @Nullable String getUsername(@NotNull String uuid) {
        JsonObject jsonObject = fromUuid(uuid);
        return jsonObject == null ? null : jsonObject.get("name").getAsString();
    }

    private static @Nullable JsonObject retrieve(@NotNull String url) {
        return URL_CACHE.get(url, (s) -> {
            try {
                String response = getText(url);
                if (response.isEmpty()) {
                    return null;
                } else {
                    JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
                    return jsonObject.has("errorMessage") ? null : jsonObject;
                }
            } catch (IOException var4) {
                throw new RuntimeException(var4);
            }
        });
    }

    public static String getText(String url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection)(new URL(url)).openConnection();
        int responseCode = connection.getResponseCode();
        InputStream inputStream;
        if (200 <= responseCode && responseCode <= 299) {
            inputStream = connection.getInputStream();
        } else {
            inputStream = connection.getErrorStream();
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder response = new StringBuilder();

        String currentLine;
        while((currentLine = in.readLine()) != null) {
            response.append(currentLine);
        }

        in.close();
        return response.toString();
    }
}
