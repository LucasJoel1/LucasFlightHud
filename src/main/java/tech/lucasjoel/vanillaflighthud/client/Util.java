package tech.lucasjoel.vanillaflighthud.client;

import net.minecraft.client.gui.GuiGraphics;

public class Util  {
    public static void drawOutlineRect(GuiGraphics g, int x, int y, int width, int height, int thiccness, int color) {
        g.fill(x, y, x + width, y + thiccness, color);
        g.fill(x, y, x + thiccness, y + height, color);
        g.fill(x + width, y, x + width + thiccness, y + height + thiccness, color);
        g.fill(x, y + height, x + width, y + height + thiccness, color);
    }

    public static void circle(GuiGraphics g, int x, int y, int radius, int color) {
        for (int i = 0; i < 360; i++) {
            double angle = i * Math.PI / 180;
            int x1 = (int) Math.round(x + radius * Math.cos(angle));
            int y1 = (int) Math.round(y + radius * Math.sin(angle));
            g.fill(x1, y1, x1 + 1, y1 + 1, color);
        }
    }
}
