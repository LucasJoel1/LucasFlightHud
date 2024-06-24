package tech.lucasjoel.lucasflighthud.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.BorderStatus;
import net.minecraft.world.level.levelgen.Heightmap;
import tech.lucasjoel.lucasflighthud.client.util.Util;

public class LucasFlightHudClient implements ClientModInitializer {
    int screenHeight;
    int screenWidth;
    double playerCameraYaw;
    double adjustedPlayerCameraYaw;
    double playerVelocity;
    int velCol;
    double cappedVel;
    int maxBlockHeight;

    @Override
    public void onInitializeClient() {
        System.out.println("me do da work");
        Minecraft mc = Minecraft.getInstance();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            assert mc.player != null;
            try {
                if (mc.player.isFallFlying()) {
                    screenHeight = mc.getWindow().getGuiScaledHeight();
                    screenWidth = mc.getWindow().getGuiScaledWidth();
                    playerCameraYaw = (mc.gameRenderer.getMainCamera().getXRot() + 90) / 180;
                    adjustedPlayerCameraYaw = playerCameraYaw * (screenHeight * 0.666666667) + (screenHeight * 0.166666667);
                    double xChange = (mc.player.xo - mc.player.getX());
                    double yChange = (mc.player.yo - mc.player.getY());
                    double zChange = (mc.player.zo - mc.player.getZ());
                    playerVelocity = Math.sqrt(Math.pow(xChange, 2) + Math.pow(yChange, 2) + Math.pow(zChange, 2)) * 20;
                    if (playerVelocity > 50) {
                        velCol = 0x7FF00000;
                        cappedVel = 50;
                    } else {
                        velCol = 0x7F008000;
                        cappedVel = playerVelocity;
                    }

                    assert mc.level != null;
                    BlockPos map = mc.level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, mc.player.blockPosition());
                    maxBlockHeight = (int) mc.player.getY() - map.getY();
                    if (maxBlockHeight < 0) {
                        BlockState block = mc.level.getBlockState(new BlockPos((int) mc.player.getX(), (int) mc.player.getY(), (int) mc.player.getZ()));
                        maxBlockHeight = 0;
                        while (block.isAir() && maxBlockHeight < 256) {
                            maxBlockHeight++;
                            block = mc.level.getBlockState(new BlockPos((int) mc.player.getX(), (int) mc.player.getY() - maxBlockHeight, (int) mc.player.getZ()));
                        }
                    }
                }
            } catch (NullPointerException ignored) {

            }
        });


        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            assert mc.player != null;
            if (mc.player.isFallFlying()) {
                drawContext.fill((int) (screenWidth * 0.833333333) + 3, (int) (screenHeight * 0.5), (int) ((screenWidth * 0.833333333) + (screenWidth * 0.083333333333)), (int) adjustedPlayerCameraYaw, 0x7F008000);
                Util.drawOutlineRect(drawContext, (int) (screenWidth * 0.833333333), (int) (screenHeight * 0.166666667), (int) (screenWidth * 0.083333333333), (int) (screenHeight * 0.666666667), 3, 0xFFFFFFFF);
                drawContext.drawString(mc.font, String.format("%.2f", (-playerCameraYaw + 0.5) * 2), (int) (screenWidth * 0.833333333) + 8, (int) (screenHeight * 0.666666667) + (int) (screenHeight * 0.166666667) + 10, 0xFFFFFFFF);
                drawContext.fill((int) (screenWidth * 0.185185185) - (int) (screenWidth * 0.083333333333), (int) (screenHeight * 0.166666667) + (int) (screenHeight * 0.666666667) - (int) ((screenHeight * 0.666666667) * (cappedVel / 50)), (int) (screenWidth * 0.185185185), (int) (screenHeight * 0.166666667) + (int) (screenHeight * 0.666666667), velCol);
                Util.drawOutlineRect(drawContext, (int) (screenWidth * 0.185185185) - (int) (screenWidth * 0.083333333333), (int) (screenHeight * 0.166666667), (int) (screenWidth * 0.083333333333), (int) (screenHeight * 0.666666667), 3, 0xFFFFFFFF);
                drawContext.drawString(mc.font, String.format("%.2f", playerVelocity), (int) (screenWidth * 0.185185185) - (int) (screenWidth * 0.083333333333) + 8, (int) (screenHeight * 0.666666667) + (int) (screenHeight * 0.166666667) + 10, 0xFFFFFFFF);
                drawContext.drawString(mc.font, "Height: " + maxBlockHeight, (int) (screenWidth * 0.833333333) + 8, (int) (screenHeight * 0.666666667) + (int) (screenHeight * 0.166666667) + 20, 0xFFFFFFFF);
                drawContext.drawString(mc.font, "Direction: " + String.valueOf(mc.player.getDirection()).toUpperCase().charAt(0), (int) (screenWidth * 0.185185185) - (int) (screenWidth * 0.083333333333) + 8, (int) (screenHeight * 0.666666667) + (int) (screenHeight * 0.166666667) + 20, 0xFFFFFFFF);
            }
        });
    }
}
