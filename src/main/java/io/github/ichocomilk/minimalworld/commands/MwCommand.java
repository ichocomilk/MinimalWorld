package io.github.ichocomilk.minimalworld.commands;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.CraftChunk;

import com.github.luben.zstd.Zstd;

import io.github.ichocomilk.minimalworld.MwPlugin;
import io.github.ichocomilk.minimalworld.converter.Buffers;
import io.github.ichocomilk.minimalworld.converter.ChunkToBuffer;

public class MwCommand implements CommandExecutor {

    private final File worldFolder;
    private final int compression;

    public MwCommand(File worldFolder, int compression) {
        this.worldFolder = worldFolder;
        this.compression = compression;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length != 1) {
            sender.sendMessage("Format: /mw (worldname)");
            return true;
        }
        final World world = Bukkit.getWorld(args[0]);
        if (world == null) {
            sender.sendMessage("The world " + args[0] + " don't exist");
            return true;    
        }

        final CraftChunk[] chunks = (CraftChunk[]) world.getLoadedChunks();
        if (chunks.length >= 32750) {
            sender.sendMessage("To many chunks loaded");
            return true;
        }

        long time = System.currentTimeMillis();

        final ChunkToBuffer chunkToBuffer = new ChunkToBuffer(chunks);
        byte[] chunkBuffer = chunkToBuffer.convert();
        if (chunkBuffer == null) {
            sender.sendMessage("§cTo many chunks loaded. Try unload");
            return true;
        }

        long finish = System.currentTimeMillis() - time;
        sender.sendMessage(
            "\n  §aData collected in: " + finish + "ms" +
            "\n  §7Amount Chunks: §b" + chunkToBuffer.getChunksParsed() + 
            "\n  §7Uncompress buffer: §b" + chunkBuffer.length 
        );

        time = System.currentTimeMillis();
        chunkBuffer = Zstd.compress(chunkBuffer, compression);
        finish = System.currentTimeMillis() - time;

        sender.sendMessage(
            "\n §aCompression finish in: " + finish +
            "\n §7Buffer size: §3" + chunkBuffer.length);

        final File worldFile = new File(worldFolder, args[0] + ".minworld");
        try (FileOutputStream outputStream = new FileOutputStream(worldFile)) {
            // File header (No compressed)
            final byte[] header = new byte[5];
            header[0] = MwPlugin.WORLD_VERSION;
            Buffers.pushInt32(header, chunkToBuffer.getSize(), 1);
            outputStream.write(header);

            // Chunk data (compressed)
            outputStream.write(chunkBuffer);
        } catch (IOException e) {
            sender.sendMessage("§cError on writing " + args[0] + ".minworld file... Check the console");
            Bukkit.getLogger().warning(e.getMessage());
        }
        return true;
    }
}