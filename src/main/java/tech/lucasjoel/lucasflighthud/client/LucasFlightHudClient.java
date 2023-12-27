package tech.lucasjoel.lucasflighthud.client;

import com.google.common.util.concurrent.AtomicDouble;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class LucasFlightHudClient implements ClientModInitializer {
    AtomicInteger screenHeight = new AtomicInteger();
    AtomicInteger screenWidth = new AtomicInteger();
    AtomicDouble playerCameraYaw = new AtomicDouble();
    AtomicDouble adjustedPlayerCameraYaw = new AtomicDouble();
    AtomicDouble playerVelocity = new AtomicDouble();
    AtomicInteger velCol = new AtomicInteger();
    AtomicDouble cappedVel = new AtomicDouble();
    AtomicInteger maxBlockHeight = new AtomicInteger();




    @Override
    public void onInitializeClient() {
        System.out.println("me do da work");
        Minecraft mc = Minecraft.getInstance();
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            assert mc.player != null;
            try {
                if (mc.player.isFallFlying()) {
                    screenHeight.set(mc.getWindow().getGuiScaledHeight());
                    screenWidth.set(mc.getWindow().getGuiScaledWidth());
                    playerCameraYaw.set((mc.gameRenderer.getMainCamera().getXRot() + 90) / 180);
                    adjustedPlayerCameraYaw.set(playerCameraYaw.get() * (screenHeight.get() * 0.666666667) + (screenHeight.get() * 0.166666667));
                    double xChange = (mc.player.xo - mc.player.getX());
                    double yChange = (mc.player.yo - mc.player.getY());
                    double zChange = (mc.player.zo - mc.player.getZ());
                    playerVelocity.set(Math.sqrt(Math.pow(xChange, 2) + Math.pow(yChange, 2) + Math.pow(zChange, 2)) * 20);
                    if (playerVelocity.get() > 50) {
                        velCol.set(0x7FF00000);
                        cappedVel.set(50);
                    } else {
                        velCol.set(0x7F008000);
                        cappedVel.set(playerVelocity.get());
                    }

                    assert mc.level != null;
                    BlockPos map = mc.level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, mc.player.blockPosition());
                    maxBlockHeight.set((int) mc.player.getY() - map.getY());
                    if (maxBlockHeight.get() < 0) {
                        BlockState block = mc.level.getBlockState(new BlockPos((int) mc.player.getX(), (int) mc.player.getY(), (int) mc.player.getZ()));
                        maxBlockHeight.set(0);
                        while (block.isAir() && maxBlockHeight.get() < 256) {
                            maxBlockHeight.getAndIncrement();
                            block = mc.level.getBlockState(new BlockPos((int) mc.player.getX(), (int) mc.player.getY() - maxBlockHeight.get(), (int) mc.player.getZ()));
                        }
                    }
                }
            } catch (NullPointerException ignored) {

            }
        });


        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            assert mc.player != null;
            if (mc.player.isFallFlying()) {
                drawContext.fill((int) (screenWidth.get() * 0.833333333) + 3, (int) (screenHeight.get() * 0.5), (int) ((screenWidth.get() * 0.833333333) + (screenWidth.get() * 0.083333333333)), (int) adjustedPlayerCameraYaw.get(), 0x7F008000);
                Util.drawOutlineRect(drawContext, (int) (screenWidth.get() * 0.833333333), (int) (screenHeight.get() * 0.166666667), (int) (screenWidth.get() * 0.083333333333), (int) (screenHeight.get() * 0.666666667), 3, 0xFFFFFFFF);
                drawContext.drawString(mc.font, String.format("%.2f", (-playerCameraYaw.get() + 0.5) * 2), (int) (screenWidth.get() * 0.833333333) + 8, (int) (screenHeight.get() * 0.666666667) + (int) (screenHeight.get() * 0.166666667) + 10, 0xFFFFFFFF);
                drawContext.fill((int) (screenWidth.get() * 0.185185185) - (int) (screenWidth.get() * 0.083333333333), (int) (screenHeight.get() * 0.166666667) + (int) (screenHeight.get() * 0.666666667) - (int) ((screenHeight.get() * 0.666666667) * (cappedVel.get() / 50)), (int) (screenWidth.get() * 0.185185185), (int) (screenHeight.get() * 0.166666667) + (int) (screenHeight.get() * 0.666666667), velCol.get());
                Util.drawOutlineRect(drawContext, (int) (screenWidth.get() * 0.185185185) - (int) (screenWidth.get() * 0.083333333333), (int) (screenHeight.get() * 0.166666667), (int) (screenWidth.get() * 0.083333333333), (int) (screenHeight.get() * 0.666666667), 3, 0xFFFFFFFF);
                drawContext.drawString(mc.font, String.format("%.2f", playerVelocity.get()), (int) (screenWidth.get() * 0.185185185) - (int) (screenWidth.get() * 0.083333333333) + 8, (int) (screenHeight.get() * 0.666666667) + (int) (screenHeight.get() * 0.166666667) + 10, 0xFFFFFFFF);

                drawContext.drawString(mc.font, "Height: " + maxBlockHeight.get(), (int) (screenWidth.get() * 0.833333333) + 8, (int) (screenHeight.get() * 0.666666667) + (int) (screenHeight.get() * 0.166666667) + 20, 0xFFFFFFFF);
                drawContext.drawString(mc.font, "Direction: " + String.valueOf(mc.player.getDirection()).toUpperCase().charAt(0), (int) (screenWidth.get() * 0.185185185) - (int) (screenWidth.get() * 0.083333333333) + 8, (int) (screenHeight.get() * 0.666666667) + (int) (screenHeight.get() * 0.166666667) + 20, 0xFFFFFFFF);
            }
        });
    }
}
