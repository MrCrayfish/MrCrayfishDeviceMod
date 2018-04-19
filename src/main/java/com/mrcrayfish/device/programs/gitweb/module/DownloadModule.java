package com.mrcrayfish.device.programs.gitweb.module;

import com.mrcrayfish.device.api.ApplicationManager;
import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.app.Dialog;
import com.mrcrayfish.device.api.app.Icons;
import com.mrcrayfish.device.api.app.Layout;
import com.mrcrayfish.device.api.app.component.Button;
import com.mrcrayfish.device.api.io.File;
import com.mrcrayfish.device.api.utils.RenderUtil;
import com.mrcrayfish.device.core.Laptop;
import com.mrcrayfish.device.object.AppInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;

import java.awt.*;
import java.util.Map;

/**
 * Author: MrCrayfish
 */
public class DownloadModule extends Module
{
    @Override
    public String[] getRequiredData()
    {
        return new String[] { "file-app", "file-data" };
    }

    @Override
    public int calculateHeight(Map<String, String> data, int width)
    {
        return 45;
    }

    @Override
    public void generate(Application app, Layout layout, int width, Map<String, String> data)
    {
        int height = calculateHeight(data, width) - 5;
        AppInfo info = ApplicationManager.getApplication(data.get("file-app"));
        layout.setBackground((gui, mc, x, y, width1, height1, mouseX, mouseY, windowActive) ->
        {
            int section = layout.width / 6;
            int subWidth = section * 4;
            int posX = x + section;
            int posY = y + 5;
            Gui.drawRect(posX, posY, posX + subWidth, posY + height - 5, Color.BLACK.getRGB());
            Gui.drawRect(posX + 1, posY + 1, posX + subWidth - 1, posY + height - 5 - 1, Color.DARK_GRAY.getRGB());

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            Minecraft.getMinecraft().getTextureManager().bindTexture(Laptop.ICON_TEXTURES);
            int iconU = 0, iconV = 0;
            if(info != null)
            {
                iconU = info.getIconU();
                iconV = info.getIconV();
            }
            RenderUtil.drawRectWithTexture(posX + 5, posY + 3, iconU, iconV, 28, 28, 14, 14, 224, 224);

            int textWidth = subWidth - 70 - 10 - 30 - 5;
            RenderUtil.drawStringClipped(data.getOrDefault("file-name", "File"), posX + 37, posY + 7, textWidth, Color.ORANGE.getRGB(), true);
            if(data.containsKey("text"))
            {
                RenderUtil.drawStringClipped(data.get("text"), posX + 37, posY + 19, textWidth, Color.LIGHT_GRAY.getRGB(), false);
            }
        });

        int section = layout.width / 6;
        Button button = new Button(0, 10, "Download", Icons.IMPORT);
        button.left = section * 5 - 70 - 5;
        button.setSize(70, height - 15);
        button.setClickListener((mouseX, mouseY, mouseButton) ->
        {
            try
            {
                NBTTagCompound tag = JsonToNBT.getTagFromJson(data.get("file-data"));
                File file = new File(data.getOrDefault("file-name", ""), data.get("file-app"), tag);
                Dialog dialog = new Dialog.SaveFile(app, file);
                app.openDialog(dialog);
            }
            catch(NBTException e)
            {
                e.printStackTrace();
            }
        });
        layout.addComponent(button);
    }
}
