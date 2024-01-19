package net.blossom.communications.content;

public interface ContentReader {

    String readSizedString();

    int readInt();

    long readLong();

    float readFloat();

    double readDouble();

}
