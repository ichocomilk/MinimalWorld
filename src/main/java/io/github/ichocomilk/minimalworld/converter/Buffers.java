package io.github.ichocomilk.minimalworld.converter;

public final class Buffers {

    public static int pushInt32(byte[] buffer, int value, int pos) {
        buffer[pos++] = (byte)(value >> 24);
        buffer[pos++] = (byte)(value >> 16);
        buffer[pos++] = (byte)(value >> 8);
        buffer[pos++] = (byte)value;
        return pos;
    }

    public static int pushInt16(byte[] buffer, char value, int pos) {
        buffer[pos++] = (byte)(value >> 8);
        buffer[pos++] = (byte)value;
        return pos;
    }

    public static int pushInt8(byte[] buffer, byte value, int pos) {
        buffer[pos++] = (byte)value;
        return pos;
    }

    public static int pullInt32(byte[] buffer, int pos) {
        int value = 0;
        value |= (buffer[pos++] & 0xFF) << 24;
        value |= (buffer[pos++] & 0xFF) << 16;
        value |= (buffer[pos++] & 0xFF) << 8;
        value |= (buffer[pos++] & 0xFF);
        return value;
    }

    public static char pullInt16(byte[] buffer, int pos) {
        return (char) ((buffer[pos] << 8) | (buffer[pos + 1] & 0xFF));
    }
}
