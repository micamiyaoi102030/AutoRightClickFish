package com.example.autorightclickfish;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * AutoRightClickFish - Forge 1.21.1 (example)
 * Behavior:
 *  - On the client, detect when the local player's fishing bobber signals a bite.
 *  - For a short duration, repeatedly call useItem to simulate right-click spam.
 *
 * Note: This is a simple example. It may need adaptions depending on mappings/Forge versions.
 */
@Mod(AutoRightClickFish.MODID)
public class AutoRightClickFish {
    public static final String MODID = "autorightclickfish";

    // how many client ticks to spam right click after bite (default 5 ticks = 0.25s)
    private int spamTicks = 5;
    private int spamRemaining = 0;

    public AutoRightClickFish() {
        MinecraftForge.EVENT_BUS.register(this);
        LogUtils.getLogger().info("AutoRightClickFish loaded");
    }

    @SubscribeEvent
    public void onClientTick(ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        // If currently spamming, continue calling useItem
        if (spamRemaining > 0) {
            // simulate right-click with main hand
            try {
                mc.gameMode.useItem(mc.player, InteractionHand.MAIN_HAND);
            } catch (Throwable t) {
                // ignore - method availability depends on mappings
            }
            spamRemaining--;
            return;
        }

        // check player's fishing hook (client-side)
        if (mc.player.fishing != null) {
            FishingHook hook = mc.player.fishing;
            // 'biting' field indicates a bite in many mappings; using getter if available would be better
            try {
                boolean biting = hook.biting;
                if (biting) {
                    // start spam
                    spamRemaining = spamTicks;
                }
            } catch (Throwable t) {
                // fallback: try to infer bite from hook.xRot change or other heuristics - omitted for brevity
            }
        }
    }
}
