package de.ellpeck.naturesaura.events;

import de.ellpeck.naturesaura.ModConfig;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import de.ellpeck.naturesaura.chunk.AuraChunk;
import de.ellpeck.naturesaura.packet.PacketHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.world.ChunkWatchEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Iterator;

public class CommonEvents {

    @SubscribeEvent
    public void onChunkCapsAttach(AttachCapabilitiesEvent<Chunk> event) {
        Chunk chunk = event.getObject();
        IAuraType type = IAuraType.forWorld(chunk.getWorld());
        event.addCapability(new ResourceLocation(NaturesAura.MOD_ID, "aura"), new AuraChunk(chunk, type));
    }

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        if (!event.world.isRemote && event.phase == TickEvent.Phase.END) {
            if (event.world.getTotalWorldTime() % 20 == 0) {
                event.world.profiler.func_194340_a(() -> NaturesAura.MOD_ID + ":onWorldTick");
                Iterator<Chunk> chunks = event.world.getPersistentChunkIterable(((WorldServer) event.world).getPlayerChunkMap().getChunkIterator());
                while (chunks.hasNext()) {
                    Chunk chunk = chunks.next();
                    if (chunk.hasCapability(NaturesAuraAPI.capAuraChunk, null)) {
                        AuraChunk auraChunk = (AuraChunk) chunk.getCapability(NaturesAuraAPI.capAuraChunk, null);
                        auraChunk.update();
                    }
                }
                event.world.profiler.endSection();
            }
        }
    }

    @SubscribeEvent
    public void onChunkWatch(ChunkWatchEvent.Watch event) {
        Chunk chunk = event.getChunkInstance();
        if (!chunk.getWorld().isRemote && chunk.hasCapability(NaturesAuraAPI.capAuraChunk, null)) {
            AuraChunk auraChunk = (AuraChunk) chunk.getCapability(NaturesAuraAPI.capAuraChunk, null);
            PacketHandler.sendTo(event.getPlayer(), auraChunk.makePacket());
        }
    }

    @SubscribeEvent
    public void onConfigChanged(OnConfigChangedEvent event) {
        if (NaturesAura.MOD_ID.equals(event.getModID())) {
            ConfigManager.sync(NaturesAura.MOD_ID, Config.Type.INSTANCE);
            ModConfig.initOrReload();
        }
    }
}
