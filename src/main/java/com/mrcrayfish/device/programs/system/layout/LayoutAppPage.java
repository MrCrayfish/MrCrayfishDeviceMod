package com.mrcrayfish.device.programs.system.layout;

import com.mrcrayfish.device.MrCrayfishDeviceMod;
import com.mrcrayfish.device.api.app.Icons;
import com.mrcrayfish.device.api.app.Layout;
import com.mrcrayfish.device.api.app.ScrollableLayout;
import com.mrcrayfish.device.api.app.component.Button;
import com.mrcrayfish.device.api.app.component.Image;
import com.mrcrayfish.device.api.app.component.Label;
import com.mrcrayfish.device.api.app.component.Text;
import com.mrcrayfish.device.core.Laptop;
import com.mrcrayfish.device.object.AppInfo;
import com.mrcrayfish.device.programs.gitweb.component.GitWebFrame;
import com.mrcrayfish.device.programs.system.ApplicationAppStore;
import com.mrcrayfish.device.programs.system.component.SlideShow;
import com.mrcrayfish.device.programs.system.object.AppEntry;
import com.mrcrayfish.device.programs.system.object.LocalEntry;
import com.mrcrayfish.device.programs.system.object.RemoteEntry;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.net.URI;
import java.net.URL;

/**
 * Author: MrCrayfish
 */
public class LayoutAppPage extends Layout
{
    private Laptop laptop;
    private AppEntry entry;

    private Image imageBanner;
    private Image imageIcon;
    private Label labelTitle;
    private Label labelVersion;

    private boolean installed;

    public LayoutAppPage(Laptop laptop, AppEntry entry)
    {
        super(250, 150);
        this.laptop = laptop;
        this.entry = entry;
    }

    @Override
    public void init()
    {
        if(entry instanceof LocalEntry)
        {
            installed = Laptop.getSystem().getInstalledApplications().contains(((LocalEntry) entry).getInfo());
        }

        this.setBackground((gui, mc, x, y, width, height, mouseX, mouseY, windowActive) ->
        {
            Color color = new Color(Laptop.getSystem().getSettings().getColorScheme().getHeaderColor());
            Gui.drawRect(x, y + 40, x + width, y + 41, color.brighter().getRGB());
            Gui.drawRect(x, y + 41, x + width, y + 60, color.getRGB());
            Gui.drawRect(x, y + 60, x + width, y + 61, color.darker().getRGB());
        });

        ResourceLocation resource = new ResourceLocation(entry.getId());

        imageBanner = new Image(0, 0, 250, 40);
        imageBanner.setDrawFull(true);
        if(entry instanceof LocalEntry)
        {
            imageBanner.setImage(new ResourceLocation(resource.getResourceDomain(), "textures/app/banner/" + resource.getResourcePath() + ".png"));
        }
        else if(entry instanceof RemoteEntry)
        {
            imageBanner.setImage(ApplicationAppStore.CERTIFIED_APPS_URL + "/assets/" + resource.getResourceDomain() + "/" + resource.getResourcePath() + "/banner.png");
        }
        this.addComponent(imageBanner);

        if(entry instanceof LocalEntry)
        {
            LocalEntry localEntry = (LocalEntry) entry;
            AppInfo info = localEntry.getInfo();
            imageIcon = new Image(5, 26, 28, 28, info.getIconU(), info.getIconV(), 14, 14, 224, 224, Laptop.ICON_TEXTURES);
        }
        else if(entry instanceof RemoteEntry)
        {
            imageIcon = new Image(5, 26, 28, 28, ApplicationAppStore.CERTIFIED_APPS_URL + "/assets/" + resource.getResourceDomain() + "/" + resource.getResourcePath() + "/icon.png");
        }
        this.addComponent(imageIcon);

        labelTitle = new Label(entry.getName(), 38, 32);
        labelTitle.setScale(2);
        this.addComponent(labelTitle);

        String version = entry instanceof LocalEntry ? "v" + entry.getVersion() + " - " + entry.getAuthor() : entry.getAuthor();
        labelVersion = new Label(version, 38, 50);
        this.addComponent(labelVersion);

        String description = GitWebFrame.parseFormatting(entry.getDescription());
        ScrollableLayout descriptionLayout = ScrollableLayout.create(130, 67, 115, 78, description);
        this.addComponent(descriptionLayout);

        SlideShow slideShow = new SlideShow(5, 67, 120, 78);
        if(entry instanceof LocalEntry)
        {
            if(entry.getScreenshots() != null)
            {
                for(String image : entry.getScreenshots())
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
        }
        else if(entry instanceof RemoteEntry)
        {
            RemoteEntry remoteEntry = (RemoteEntry) entry;
            String screenshotUrl = ApplicationAppStore.CERTIFIED_APPS_URL + "/assets/" + resource.getResourceDomain() + "/" + resource.getResourcePath() + "/screenshots/screenshot_%d.png";
            for(int i = 0; i < remoteEntry.screenshots; i++)
            {
                slideShow.addImage(String.format(screenshotUrl, i));
            }
        }
        this.addComponent(slideShow);

        if(entry instanceof LocalEntry)
        {
            if(((LocalEntry) entry).getInfo().getSupport() != null)
            {
                Button btnDonate = new Button(174, 44, Icons.COIN);
                btnDonate.setToolTip("Donate", "Opens a link to donate to author of the application");
                btnDonate.setSize(14, 14);
                this.addComponent(btnDonate);
            }
        }

        if(entry instanceof LocalEntry)
        {
            AppInfo info = ((LocalEntry) entry).getInfo();
            Button btnInstall = new Button(190, 44, installed ? "Remove" : "Install", installed ? Icons.CROSS : Icons.PLUS);
            btnInstall.setSize(55, 14);
            btnInstall.setClickListener((mouseX, mouseY, mouseButton) ->
            {
                if(mouseButton == 0)
                {
                    if(installed)
                    {
                        laptop.removeApplication(info, (o, success) ->
                        {
                            btnInstall.setText("Install");
                            btnInstall.setIcon(Icons.PLUS);
                            installed = false;
                        });
                    }
                    else
                    {
                        laptop.installApplication(info, (o, success) ->
                        {
                            btnInstall.setText("Remove");
                            btnInstall.setIcon(Icons.CROSS);
                            installed = true;
                        });
                    }
                }
            });
            this.addComponent(btnInstall);
        }
        else if(entry instanceof RemoteEntry)
        {
            Button btnDownload = new Button(175, 44, "Download", Icons.IMPORT);
            btnDownload.setSize(70, 14);
            btnDownload.setClickListener((mouseX, mouseY, mouseButton) -> this.openWebLink("https://minecraft.curseforge.com/projects/" + ((RemoteEntry) entry).project_id));
            this.addComponent(btnDownload);
        }
    }

    private void openWebLink(String url)
    {
        try
        {
            URI uri = new URL(url).toURI();
            Class<?> class_ = Class.forName("java.awt.Desktop");
            Object object = class_.getMethod("getDesktop").invoke(null);
            class_.getMethod("browse", URI.class).invoke(object, uri);
        }
        catch (Throwable throwable1)
        {
            Throwable throwable = throwable1.getCause();
            MrCrayfishDeviceMod.getLogger().error("Couldn't open link: {}", (Object)(throwable == null ? "<UNKNOWN>" : throwable.getMessage()));
        }
    }
}
