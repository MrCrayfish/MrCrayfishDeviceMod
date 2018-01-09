package com.mrcrayfish.device.programs.system.layout;

import com.mrcrayfish.device.api.app.Layout;
import com.mrcrayfish.device.api.app.component.Image;
import com.mrcrayfish.device.api.app.component.Label;
import com.mrcrayfish.device.api.app.component.Text;
import com.mrcrayfish.device.api.utils.RenderUtil;
import com.mrcrayfish.device.core.Laptop;
import com.mrcrayfish.device.object.AppInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;

import java.awt.*;

/**
 * Author: MrCrayfish
 */
public class LayoutAppPage extends Layout
{
    private AppInfo info;

    private Image imageBanner;
    private Image imageIcon;
    private Label labelTitle;
    private Label labelVersion;
    private Text textDescription;

    public LayoutAppPage(AppInfo info)
    {
        super(250, 150);
        this.info = info;
    }

    @Override
    public void init()
    {
        this.setBackground((gui, mc, x, y, width, height, mouseX, mouseY, windowActive) ->
        {
            Color color = new Color(Laptop.getSystem().getSettings().getColourScheme().getHeaderColour());
            Gui.drawRect(x, y + 40, x + width, y + 41, color.brighter().getRGB());
            Gui.drawRect(x, y + 41, x + width, y + 60, color.getRGB());
            Gui.drawRect(x, y + 60, x + width, y + 61, color.darker().getRGB());
        });

        imageBanner = new Image(0, 0, 250, 40, "http://aps-web-hrd.appspot.com/images/banner_landscape.jpg");
        this.addComponent(imageBanner);

        labelTitle = new Label(info.getName(), 38, 32);
        labelTitle.setScale(2);
        this.addComponent(labelTitle);

        labelVersion = new Label("v0.2.0", 38, 50);
        this.addComponent(labelVersion);

        textDescription = new Text(info.getDescription(), 150, 70, 95);
        this.addComponent(textDescription);
    }

    @Override
    public void renderOverlay(Laptop laptop, Minecraft mc, int mouseX, int mouseY, boolean windowActive)
    {
        super.renderOverlay(laptop, mc, mouseX, mouseY, windowActive);
        GlStateManager.color(1.0F, 1.0F, 1.0F);
        Minecraft.getMinecraft().getTextureManager().bindTexture(Laptop.ICON_TEXTURES);
        RenderUtil.drawRectWithTexture(xPosition + 5, yPosition + 26, info.getIconU(), info.getIconV(), 28, 28, 14, 14, 224, 224);
    }
}
