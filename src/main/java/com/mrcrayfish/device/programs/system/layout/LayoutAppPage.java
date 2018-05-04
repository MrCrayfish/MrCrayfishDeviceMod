package com.mrcrayfish.device.programs.system.layout;

import com.mrcrayfish.device.MrCrayfishDeviceMod;
import com.mrcrayfish.device.api.app.Dialog;
import com.mrcrayfish.device.api.app.Icons;
import com.mrcrayfish.device.api.app.Layout;
import com.mrcrayfish.device.api.app.component.Button;
import com.mrcrayfish.device.api.app.component.Image;
import com.mrcrayfish.device.api.app.component.Label;
import com.mrcrayfish.device.api.app.component.Text;
import com.mrcrayfish.device.api.app.listener.ClickListener;
import com.mrcrayfish.device.api.utils.RenderUtil;
import com.mrcrayfish.device.core.Laptop;
import com.mrcrayfish.device.object.AppInfo;
import com.mrcrayfish.device.programs.system.component.SlideShow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.io.File;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Author: MrCrayfish
 */
public class LayoutAppPage extends Layout
{
    private Laptop laptop;
    private AppInfo info;

    private Image imageBanner;
    private Image imageIcon;
    private Label labelTitle;
    private Label labelAuthor;
    private Label labelVersion;
    private Text textDescription;
    private Button contribbutton;

    public LayoutAppPage(Laptop laptop, AppInfo info)
    {
        super(250, 150);
        this.laptop = laptop;
        this.info = info;
    }

    @Override
    public void init()
    {
        this.setBackground((gui, mc, x, y, width, height, mouseX, mouseY, windowActive) ->
        {
            Color color = new Color(Laptop.getSystem().getSettings().getColorScheme().getHeaderColor());
            Gui.drawRect(x, y + 40, x + width, y + 41, color.brighter().getRGB());
            Gui.drawRect(x, y + 41, x + width, y + 60, color.getRGB());
            Gui.drawRect(x, y + 60, x + width, y + 61, color.darker().getRGB());
        });

        imageBanner = new Image(0, 0, 250, 40);
        imageBanner.setDrawFull(true);
        imageBanner.setImage(new ResourceLocation(info.getId().getResourceDomain(), "textures/app/banner/" + info.getId().getResourcePath() + ".png"));
        this.addComponent(imageBanner);

        labelTitle = new Label(info.getName(), 38, 32);
        labelTitle.setScale(2);
        this.addComponent(labelTitle);

        String vstr = "v" + info.getVersion();
        this.labelVersion = new Label(vstr, this.width - Minecraft.getMinecraft().fontRenderer.getStringWidth(vstr), 2);

        if(info.hasSingleAuthor())
            labelAuthor = new Label("By: " + info.getAuthor(), 38, 50);
        else{
            StringBuilder sb = new StringBuilder();
            List<String> names = Arrays.asList(info.getAuthors());
            for(String a : names){
                sb.append(a);
                if(names.indexOf(a) != info.getAuthors().length - 1)
                    sb.append(", ");
            }
            labelAuthor = new Label("By: " + sb.toString(), labelTitle.left, 50);
        }
        this.addComponent(labelVersion);
        this.addComponent(labelAuthor);

        textDescription = new Text(info.getDescription(), 130, 70, 115);
        this.addComponent(textDescription);

        SlideShow slideShow = new SlideShow(5, 67, 120, 78);
        if(info.getScreenshots() != null)
        {
            for(String image : info.getScreenshots())
            {
                if(image.startsWith("http://") || image.startsWith("https://"))
                {
                    slideShow.addImage(image);
                }
                else
                {
                    slideShow.addImage(new ResourceLocation(image));
                }
            }
        }
        this.addComponent(slideShow);

        if(info.getSupport() != null)
        {
            Button btnDonate = new Button(174, 44, Icons.COIN);
            btnDonate.setToolTip("Donate", "Opens a link to donate to author of the application");
            btnDonate.setSize(14, 14);
            this.addComponent(btnDonate);
        }

        Button btnInstall = new Button(190, 44, "Install", Icons.IMPORT);
        btnInstall.setSize(55, 14);
        this.addComponent(btnInstall);

        loadScreenshots();
    }

    @Override
    public void renderOverlay(Laptop laptop, Minecraft mc, int mouseX, int mouseY, boolean windowActive)
    {
        GlStateManager.color(1.0F, 1.0F, 1.0F);
        Minecraft.getMinecraft().getTextureManager().bindTexture(Laptop.ICON_TEXTURES);
        RenderUtil.drawRectWithTexture(xPosition + 5, yPosition + 26, info.getIconU(), info.getIconV(), 28, 28, 14, 14, 224, 224);
        super.renderOverlay(laptop, mc, mouseX, mouseY, windowActive);
    }

    private void loadScreenshots()
    {
        String screenshots = "assets/" + info.getId().getResourceDomain() + "/textures/app/screenshots/" + info.getId().getResourcePath();
        URL url = LayoutAppPage.class.getResource(screenshots);
        try
        {
            if(url != null)
            {
                File file = new File(url.toURI());
                MrCrayfishDeviceMod.getLogger().info(file.exists() + " is true");
            }
        }
        catch(URISyntaxException e)
        {
            e.printStackTrace();
        }
    }
}
