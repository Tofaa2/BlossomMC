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
            Blossom.LOGGER.error("Failed to paste text to Hastebin", e);
            return text;
        }

    }


}
