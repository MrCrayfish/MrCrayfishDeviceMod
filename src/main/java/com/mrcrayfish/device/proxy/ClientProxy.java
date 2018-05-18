package com.mrcrayfish.device.proxy;

import com.mrcrayfish.device.DeviceConfig;
import com.mrcrayfish.device.MrCrayfishDeviceMod;
import com.mrcrayfish.device.Reference;
import com.mrcrayfish.device.api.ApplicationManager;
import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.print.IPrint;
import com.mrcrayfish.device.api.print.PrintingManager;
import com.mrcrayfish.device.core.Laptop;
import com.mrcrayfish.device.core.client.ClientNotification;
import com.mrcrayfish.device.object.AppInfo;
import com.mrcrayfish.device.programs.system.SystemApplication;
import com.mrcrayfish.device.tileentity.*;
import com.mrcrayfish.device.tileentity.render.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import javax.annotation.Nullable;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class ClientProxy extends CommonProxy implements IResourceManagerReloadListener
{
    @Override
    public void preInit()
    {
        super.preInit();
        ((IReloadableResourceManager)Minecraft.getMinecraft().getResourceManager()).registerReloadListener(this);
    }

    @Override
    public void init()
    {
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityLaptop.class, new LaptopRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPrinter.class, new PrinterRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPaper.class, new PaperRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityRouter.class, new RouterRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityOfficeChair.class, new OfficeChairRenderer());

        if(MrCrayfishDeviceMod.DEVELOPER_MODE)
        {
            Laptop.addWallpaper(new ResourceLocation("cdm:textures/gui/developer_wallpaper.png"));
        }
        else
        {
            Laptop.addWallpaper(new ResourceLocation("cdm:textures/gui/laptop_wallpaper_1.png"));
            Laptop.addWallpaper(new ResourceLocation("cdm:textures/gui/laptop_wallpaper_2.png"));
            Laptop.addWallpaper(new ResourceLocation("cdm:textures/gui/laptop_wallpaper_3.png"));
            Laptop.addWallpaper(new ResourceLocation("cdm:textures/gui/laptop_wallpaper_4.png"));
            Laptop.addWallpaper(new ResourceLocation("cdm:textures/gui/laptop_wallpaper_5.png"));
            Laptop.addWallpaper(new ResourceLocation("cdm:textures/gui/laptop_wallpaper_6.png"));
            Laptop.addWallpaper(new ResourceLocation("cdm:textures/gui/laptop_wallpaper_7.png"));
        }
    }

    @Override
    public void postInit()
    {
        generateIconAtlas();
    }

    private void generateIconAtlas()
    {
        final int ICON_SIZE = 14;
        int index = 0;

        BufferedImage atlas = new BufferedImage(ICON_SIZE * 16, ICON_SIZE * 16, BufferedImage.TYPE_INT_ARGB);
        Graphics g = atlas.createGraphics();

        try
        {
            BufferedImage icon = TextureUtil.readBufferedImage(ClientProxy.class.getResourceAsStream("/assets/" + Reference.MOD_ID + "/textures/app/icon/missing.png"));
            g.drawImage(icon, 0, 0, ICON_SIZE, ICON_SIZE, null);
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        index++;

        for(AppInfo info : ApplicationManager.getAllApplications())
        {
            if(info.getIcon() == null)
                continue;

            ResourceLocation identifier = info.getId();
            ResourceLocation iconResource = new ResourceLocation(info.getIcon());
            String path = "/assets/" + iconResource.getResourceDomain() + "/" + iconResource.getResourcePath();
            try
            {
                InputStream input = ClientProxy.class.getResourceAsStream(path);
                if(input != null)
                {
                    BufferedImage icon = TextureUtil.readBufferedImage(input);
                    if(icon.getWidth() != ICON_SIZE || icon.getHeight() != ICON_SIZE)
                    {
                        MrCrayfishDeviceMod.getLogger().error("Incorrect icon size for " + identifier.toString() + " (Must be 14 by 14 pixels)");
                        continue;
                    }
                    int iconU = (index % 16) * ICON_SIZE;
                    int iconV = (index / 16) * ICON_SIZE;
                    g.drawImage(icon, iconU, iconV, ICON_SIZE, ICON_SIZE, null);
                    updateIcon(info, iconU, iconV);
                    index++;
                }
                else
                {
                    MrCrayfishDeviceMod.getLogger().error("Icon for application '" + identifier.toString() +  "' could not be found at '" + path + "'");
                }
            }
            catch(Exception e)
            {
                MrCrayfishDeviceMod.getLogger().error("Unable to load icon for " + identifier.toString());
            }
        }

        g.dispose();
        Minecraft.getMinecraft().getTextureManager().loadTexture(Laptop.ICON_TEXTURES, new DynamicTexture(atlas));
    }

    private void updateIcon(AppInfo info, int iconU, int iconV)
    {
        ReflectionHelper.setPrivateValue(AppInfo.class, info, iconU, "iconU");
        ReflectionHelper.setPrivateValue(AppInfo.class, info, iconV, "iconV");
    }

    @Nullable
    @Override
    public Application registerApplication(ResourceLocation identifier, Class<? extends Application> clazz)
    {
        if("minecraft".equals(identifier.getResourceDomain()))
        {
            throw new IllegalArgumentException("Invalid identifier domain");
        }

        try
        {
            Application application = clazz.newInstance();
            java.util.List<Application> APPS = ReflectionHelper.getPrivateValue(Laptop.class, null, "APPLICATIONS");
            APPS.add(application);

            Field field = Application.class.getDeclaredField("info");
            field.setAccessible(true);

            Field modifiers = Field.class.getDeclaredField("modifiers");
            modifiers.setAccessible(true);
            modifiers.setInt(field, field.getModifiers() & ~Modifier.FINAL);

            field.set(application, generateAppInfo(identifier, clazz));

            return application;
        }
        catch(InstantiationException | IllegalAccessException | NoSuchFieldException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    @Nullable
    private AppInfo generateAppInfo(ResourceLocation identifier, Class<? extends Application> clazz)
    {
        AppInfo info = new AppInfo(identifier, SystemApplication.class.isAssignableFrom(clazz));
        info.reload();
        return info;
    }

    @Override
    public boolean registerPrint(ResourceLocation identifier, Class<? extends IPrint> classPrint)
    {
        try
        {
            Constructor<? extends IPrint> constructor = classPrint.getConstructor();
            IPrint print = constructor.newInstance();
            Class<? extends IPrint.Renderer> classRenderer = print.getRenderer();
            try
            {
                IPrint.Renderer renderer = classRenderer.newInstance();
                Map<String, IPrint.Renderer> idToRenderer = ReflectionHelper.getPrivateValue(PrintingManager.class, null, "registeredRenders");
                if(idToRenderer == null)
                {
                    idToRenderer = new HashMap<>();
                    ReflectionHelper.setPrivateValue(PrintingManager.class, null, idToRenderer, "registeredRenders");
                }
                idToRenderer.put(identifier.toString(), renderer);
            }
            catch(InstantiationException e)
            {
                MrCrayfishDeviceMod.getLogger().error("The print renderer '" + classRenderer.getName() + "' is missing an empty constructor and could not be registered!");
                return false;
            }
            return true;
        }
        catch(Exception e)
        {
            MrCrayfishDeviceMod.getLogger().error("The print '" + classPrint.getName() + "' is missing an empty constructor and could not be registered!");
        }
        return false;
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager)
    {
        if(ApplicationManager.getAllApplications().size() > 0)
        {
            ApplicationManager.getAllApplications().forEach(AppInfo::reload);
            generateIconAtlas();
        }
    }

    @SubscribeEvent
    public void onClientDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event)
    {
        allowedApps = null;
        DeviceConfig.restore();
    }

    @Override
    public void showNotification(NBTTagCompound tag)
    {
        ClientNotification notification = ClientNotification.loadFromTag(tag);
        notification.push();
    }
}
