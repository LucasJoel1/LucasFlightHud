package tech.lucasjoel.vanillaflighthud.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.mixin.client.rendering.InGameHudMixin;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

public class VanillaFlightHudClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        System.out.println("me do da work");
        Minecraft mc = Minecraft.getInstance();

        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            assert mc.player != null;
            if (mc.player.isFallFlying()) {
                // remove the crosshair

                int screenHeight = mc.getWindow().getGuiScaledHeight();
                int screenWidth = mc.getWindow().getGuiScaledWidth();
                double playerCameraYaw = (mc.gameRenderer.getMainCamera().getXRot() + 90) / 180;
                double adjustedPlayerCameraYaw = playerCameraYaw * (screenHeight * 0.666666667) + (screenHeight * 0.166666667);
                drawContext.fill((int) (screenWidth * 0.833333333) + 3, (int) (screenHeight * 0.5), (int) ((screenWidth * 0.833333333) + (screenWidth * 0.083333333333)), (int) adjustedPlayerCameraYaw, 0x7F008000);
                Util.drawOutlineRect(drawContext, (int) (screenWidth * 0.833333333), (int) (screenHeight * 0.166666667), (int) (screenWidth * 0.083333333333), (int) (screenHeight * 0.666666667), 3, 0xFFFFFFFF);
                drawContext.drawString(mc.font, String.format("%.2f", (-playerCameraYaw + 0.5) * 2), (int) (screenWidth * 0.833333333) + 8, (int) (screenHeight * 0.666666667) + (int) (screenHeight * 0.166666667) + 10, 0xFFFFFFFF);
                double xChange = (mc.player.xo - mc.player.getX());
                double yChange = (mc.player.yo - mc.player.getY());
                double zChange = (mc.player.zo - mc.player.getZ());
                double playerVelocity = Math.sqrt(Math.pow(xChange, 2) + Math.pow(yChange, 2) + Math.pow(zChange, 2)) * 20;
                int velCol;
                double cappedVel;
                if (playerVelocity > 40) {
                    velCol = 0x7FF00000;
                    cappedVel = 40;
                } else {
                    velCol = 0x7F008000;
                    cappedVel = playerVelocity;
                }
                drawContext.fill((int) (screenWidth * 0.185185185) - (int) (screenWidth * 0.083333333333), (int) (screenHeight * 0.166666667) + (int) (screenHeight * 0.666666667) - (int) ((screenHeight * 0.666666667) * (cappedVel / 40)), (int) (screenWidth * 0.185185185), (int) (screenHeight * 0.166666667) + (int) (screenHeight * 0.666666667), velCol);
                Util.drawOutlineRect(drawContext, (int) (screenWidth * 0.185185185) - (int) (screenWidth * 0.083333333333), (int) (screenHeight * 0.166666667), (int) (screenWidth * 0.083333333333), (int) (screenHeight * 0.666666667), 3, 0xFFFFFFFF);
                drawContext.drawString(mc.font, String.format("%.2f", playerVelocity), (int) (screenWidth * 0.185185185) - (int) (screenWidth * 0.083333333333) + 8, (int) (screenHeight * 0.666666667) + (int) (screenHeight * 0.166666667) + 10, 0xFFFFFFFF);

                assert mc.level != null;
                BlockPos map = mc.level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, mc.player.blockPosition());
                int maxBlockHeight = (int) mc.player.getY() - map.getY();

                if (maxBlockHeight < 0) {
                    BlockState block = mc.level.getBlockState(new BlockPos((int) mc.player.getX(), (int) mc.player.getY(), (int) mc.player.getZ()));
                    maxBlockHeight = 0;
                    while (block.isAir()) {
                        maxBlockHeight++;
                        block = mc.level.getBlockState(new BlockPos((int) mc.player.getX(), (int) mc.player.getY() - maxBlockHeight, (int) mc.player.getZ()));

                    }
                }
                drawContext.drawString(mc.font, String.format("%d", maxBlockHeight), (int) (screenWidth * 0.485), (int) (screenHeight * 0.685185185) + 10, 0xFFFFFFFF);

                Util.circle(drawContext, screenWidth / 2 - 1, screenHeight/ 2 - 1, 25, 0xFFFFFFFF);
                int dir = (int) -(mc.gameRenderer.getMainCamera().getYRot() + 270) % 360;
                int circleRadius = 25;
                int centerX = screenWidth / 2;
                int centerY = screenHeight / 2;

                for (int i = 0; i < 360; i += 90) {
                    double angle = Math.toRadians(i + dir); // Adjust angle based on dir
                    int labelRadius = circleRadius + 10; // Distance from the center for labels
                    int x = (int) Math.round(centerX + labelRadius * Math.cos(angle));
                    int y = (int) Math.round(centerY + labelRadius * Math.sin(angle));

                    // Draw cardinal direction letters
                    if (i == 0 || i == 180) {
                        x -= 4; // Adjust N and S positions
                        y -= 3; // Adjust N and S positions
                    } else if (i == 90 || i == 270) {
                        x -= 4; // Adjust E and W positions
                        y -= 5; // Adjust E and W positions
                    }

                    // Draw cardinal direction letters
                    switch (i) {
                        case 0:
                            drawContext.drawString(mc.font, "N", x - 1, y - 1, 0xFFFFFFFF);
                            break;
                        case 90:
                            drawContext.drawString(mc.font, "E", x - 1, y - 1, 0xFFFFFFFF);
                            break;
                        case 180:
                            drawContext.drawString(mc.font, "S", x - 1, y - 1, 0xFFFFFFFF);
                            break;
                        case 270:
                            drawContext.drawString(mc.font, "W", x - 1, y - 1, 0xFFFFFFFF);
                            break;
                    }
                }
            }
        });
    }
}
