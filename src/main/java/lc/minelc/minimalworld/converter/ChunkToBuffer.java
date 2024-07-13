package lc.minelc.minimalworld.converter;

import lc.minelc.minimalworld.MwPlugin;
import net.minecraft.server.v1_8_R3.Chunk;
import net.minecraft.server.v1_8_R3.ChunkSection;

public class ChunkToBuffer {
    
    private final Chunk[] chunks;
    private int chunksParsed = 0;

    public ChunkToBuffer(Chunk[] chunks) {
        this.chunks = chunks;
    }


    public byte[] convert() {
        final byte[] buffer = new byte[getBufferSize()];
        fillBuffer(buffer);
        return buffer;
    }

    private int getBufferSize() {
        int bufferSize = 1; // One byte for WORLD_VERSION
        bufferSize += 4; // 4 bytes = Chunk amount

        for (final Chunk chunk : chunks) {
            final ChunkSection[] sections = chunk.getSections();

            int validSections = 0;

            for (final ChunkSection section : sections) {
                if (section == null || section.a()) {
                    continue;
                }
                validSections++;
                bufferSize++; // Section ID (0-15)
                bufferSize+=8192; // 4096 * 2 = char[] to byte[]
            }
            if (validSections != 0) {
                // Chunk HEADER:
                bufferSize += 8; // Compressed ChunkX & ChunkZ
                bufferSize++; // Add one = Amount sections (0-16)
            }
        }
        return bufferSize;
    }
    
    private void fillBuffer(final byte[] buffer) {
        int i = 0;
        buffer[i++] = MwPlugin.WORLD_VERSION;
        i = pushInt32(buffer, chunks.length, i);

        for (final Chunk chunk : chunks) {
            final ChunkSection[] sections = chunk.getSections();

            int amountSections = 0;
            for (final ChunkSection section : sections) {
                if (section != null && !section.a()) {
                    amountSections++;
                    continue;
                }
            }
            if (amountSections == 0) {
                continue;
            }
            final long compressCord = chunk.locX << 32 | chunk.locZ;
            i = pushInt64(buffer, compressCord, i);

            chunksParsed++;
            i = pushInt8(buffer, (byte)amountSections, i);

            for (int j = 0; j < 16; j++) {
                final ChunkSection section = sections[j];
                if (section == null || section.a()) {
                    continue;
                }
                i = pushInt8(buffer, (byte)j, i); // Chunk Section ID
                final char[] blocks = section.getIdArray();

                for (int b = 0; b < 4096; b++) {
                    final char block = blocks[b];
                    i = pushInt16(buffer, block, i);
                }
            }
        }
    }

    public int getChunksParsed() {
        return chunksParsed;
    }

    private int pushInt32(byte[] buffer, int value, int pos) {
        buffer[pos++] = (byte)(value >> 24);
        buffer[pos++] = (byte)(value >> 16);
        buffer[pos++] = (byte)(value >> 8);
        buffer[pos++] = (byte)value;
        return pos;
    }

    private int pushInt16(byte[] buffer, char value, int pos) {
        buffer[pos++] = (byte)(value >> 8);
        buffer[pos++] = (byte)value;
        return pos;
    }

    private int pushInt8(byte[] buffer, byte value, int pos) {
        buffer[pos++] = (byte)value;
        return pos;
    }

    private int pushInt64(byte[] buffer, long value, int pos) {
        buffer[pos++] = (byte)(value >> 56);
        buffer[pos++] = (byte)(value >> 48);
        buffer[pos++] = (byte)(value >> 40);
        buffer[pos++] = (byte)(value >> 32);
        buffer[pos++] = (byte)(value >> 24);
        buffer[pos++] = (byte)(value >> 16);
        buffer[pos++] = (byte)(value >> 8);
        buffer[pos++] = (byte)value;
        return pos;
    }
}
