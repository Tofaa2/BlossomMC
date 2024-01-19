package net.blossom.communications;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import java.util.ArrayList;
import java.util.List;

public record ServerSample(
        String name,
        int onlinePlayers
) {

    public static byte[] toBytes(List<ServerSample> samples) {
        ByteArrayDataOutput output = ByteStreams.newDataOutput();
        output.writeInt(samples.size());
        for (ServerSample sample : samples) {
            output.writeUTF(sample.name());
            output.writeInt(sample.onlinePlayers());
        }
        return output.toByteArray();
    }

    public static List<ServerSample> fromBytes(byte[] bytes) {
        ByteArrayDataInput input = ByteStreams.newDataInput(bytes);
        int size = input.readInt();
        List<ServerSample> samples = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            String name = input.readUTF();
            int onlinePlayers = input.readInt();
            samples.add(new ServerSample(name, onlinePlayers));
        }
        return samples;
    }

}
