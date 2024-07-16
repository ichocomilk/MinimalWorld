package io.github.ichocomilk.minimalworld.converter;


import org.bukkit.craftbukkit.v1_8_R3.CraftChunk;

import net.minecraft.server.v1_8_R3.Chunk;
import net.minecraft.server.v1_8_R3.ChunkSection;

public final class ChunkToBuffer {
    
    private final CraftChunk[] chunks;
    private int chunksValid = 0;
    private int bufferSize = 0;

    public ChunkToBuffer(CraftChunk[] chunks) {
        this.chunks = chunks;
    }

    public byte[] convert() {
        bufferSize = getBufferSize();
        if (bufferSize <= 0) {// Overflow
            return null;
        }
        final byte[] buffer = new byte[bufferSize];
        fillBuffer(buffer);
        return buffer;
    }

    private int getBufferSize() {
        int bufferSize = 4; // Chunks Amount
        for (final CraftChunk craftChunk : chunks) {
            final ChunkSection[] sections = craftChunk.getHandle().getSections();

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
                chunksValid++;
            }
        }
        return bufferSize;
    }
    
    private void fillBuffer(final byte[] buffer) {
        int i = Buffers.pushInt32(buffer, chunksValid, 0);

        for (final CraftChunk craftChunk : chunks) {
            final Chunk chunk = craftChunk.getHandle();
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
;
            i = Buffers.pushInt32(buffer, craftChunk.getX(), i);
            i = Buffers.pushInt32(buffer, craftChunk.getZ(), i);
            i = Buffers.pushInt8(buffer, (byte)amountSections, i);

            for (int j = 0; j < 16; j++) {
                final ChunkSection section = sections[j];
                if (section == null || section.a()) {
                    continue;
                }
                i = Buffers.pushInt8(buffer, (byte)j, i); // Chunk Section ID
                final char[] blocks = section.getIdArray();

                for (int b = 0; b < 4096; b++) {
                    final char block = blocks[b];
                    i = Buffers.pushInt16(buffer, block, i);
                }
            }
        }
    }

    public int getSize() {
        return bufferSize;
    }

    public int getChunksParsed() {
        return chunksValid;
    }
}
