package net.blossom.communications;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

public final class Communications {

    public static final String SERVER_SAMPLE_IDENTIFIER = "blossom:server_sample";
    public static final String CONTENT_IDENTIFIER = "blossom:content";

    private Communications() {}

    public static byte[] kickRequest(String player, String rawReason) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("KickPlayer");
        out.writeUTF(player);
        out.writeUTF(rawReason);
        return out.toByteArray();
    }


    public static byte[] createIpFetch() {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("IP");
        return out.toByteArray();
    }

    public static byte[] creatIpFetch(String user) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("IP");
        out.writeUTF(user);
        return out.toByteArray();
    }

    public static byte[] createPlayerCountFetch(String server) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("PlayerCount");
        out.writeUTF(server);
        return out.toByteArray();
    }

    public static byte[] createPlayerListFetch(String server) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("PlayerList");
        out.writeUTF(server);
        return out.toByteArray();
    }

    public static byte[] createPlayerServerFetch(String player) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("GetPlayerServer");
        out.writeUTF(player);
        return out.toByteArray();
    }

    public static byte[] createSend(
            String server
    ) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(server);
        return out.toByteArray();
    }

    /**
     * @param player The player username
     * @param server The server name
     * @return The byte array to send to the proxy
     */
    public static byte[] createSend(
            String player,
            String server
    ) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("ConnectOther");
        out.writeUTF(player);
        out.writeUTF(server);
        return out.toByteArray();
    }

}
