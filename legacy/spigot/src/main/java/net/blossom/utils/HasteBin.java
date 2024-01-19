package net.blossom.utils;

import net.blossom.core.Blossom;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.stream.Collectors;

public class HasteBin {

    public static String createPaste(String text) {
        try {
            String requestURL = "https://haste.zneix.eu/documents";
            String response = null;
            int postDataLength;
            URL url = new URL(requestURL);
            HttpsURLConnection conn = (HttpsURLConnection) Objects.requireNonNull(url).openConnection();
            byte[] postData;
            DataOutputStream dataOutputStream;
            BufferedReader bufferedReader;
            postData = Objects.requireNonNull(text).getBytes(StandardCharsets.UTF_8);
            postDataLength = postData.length;

            Objects.requireNonNull(conn).setDoOutput(true);
            conn.setInstanceFollowRedirects(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("User-Agent", "HasteBin-Creator API");
            conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
            conn.setUseCaches(false);

            dataOutputStream = new DataOutputStream(conn.getOutputStream());
            dataOutputStream.write(postData);
            bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            response = bufferedReader.readLine();

            if (Objects.requireNonNull(response).contains("\"key\"")) {
                response = "https://haste.zneix.eu/" + response.substring(response.indexOf(":") + 2, response.length() - 2);
            }

            if (response.contains("https://haste.zneix.eu")) {
                return response;
            } else {
                return "Error encountered when acquiring response from URL.";
            }
        }
        catch (IOException e ) {
            Blossom.getPlugin().getSLF4JLogger().error("Failed to paste text to Hastebin", e);
            return text;
        }

    }

    public static String pasteText(String text) {
        try {
            URL url = new URL("https://hastebin.com/documents");
            URLConnection connection = url.openConnection();

            connection.setRequestProperty("authority", "hastebin.com");
            connection.setRequestProperty("accept", "application/json, text/javascript, /; q=0.01");
            connection.setRequestProperty("x-requested-with", "XMLHttpRequest");
            connection.setRequestProperty("user-agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.92 Safari/537.36'");
            connection.setRequestProperty("content-type", "application/json; charset=UTF-8");
            connection.setDoOutput(true);

            OutputStream stream = connection.getOutputStream();
            stream.write(text.getBytes());
            stream.flush();
            stream.close();

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String response = reader.lines().collect(Collectors.joining("\n"));

            return "https://hastebin.com/" + response.split("\"")[3];
        }
        catch (IOException e) {
            Blossom.getPlugin().getSLF4JLogger().error("Failed to paste text to Hastebin", e);
            return text;
        }
    }


}
