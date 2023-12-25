package tech.lucasjoel.lucasflighthud.client;

import com.mojang.blaze3d.platform.WindowEventHandler;
import net.minecraft.client.gui.GuiGraphics;

public class Util {
    public static void drawOutlineRect(GuiGraphics g, int x, int y, int width, int height, int thiccness, int color) {
        g.fill(x, y, x + width, y + thiccness, color);
        g.fill(x, y, x + thiccness, y + height, color);
        g.fill(x + width, y, x + width + thiccness, y + height + thiccness, color);
        g.fill(x, y + height, x + width, y + height + thiccness, color);
    }
}
