package com.mrcrayfish.device.programs.system.component;

import com.mrcrayfish.device.api.ApplicationManager;
import com.mrcrayfish.device.api.app.*;
import com.mrcrayfish.device.api.app.Dialog;
import com.mrcrayfish.device.api.app.component.ItemList;
import com.mrcrayfish.device.api.app.renderer.ListItemRenderer;
import com.mrcrayfish.device.api.io.File;
import com.mrcrayfish.device.api.io.Folder;
import com.mrcrayfish.device.api.utils.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

import java.awt.*;
import java.util.Stack;

/**
 * Created by Casey on 10-Jun-17.
 */
public class FileList extends ItemList<File>
{
    private static final ResourceLocation ASSETS = new ResourceLocation("cdm:textures/gui/file_browser.png");

    private static final Color ITEM_BACKGROUND = new Color(215, 217, 224);
    private static final Color ITEM_SELECTED = new Color(221, 208, 208);

    private static final ListItemRenderer<File> ITEM_RENDERER = new ListItemRenderer<File>(18) {
        @Override
        public void render(File file, Gui gui, Minecraft mc, int x, int y, int width, int height, boolean selected)
        {
            gui.drawRect(x, y, x + width, y + height, selected ? ITEM_SELECTED.getRGB() : ITEM_BACKGROUND.getRGB());

            GlStateManager.color(1.0F, 1.0F, 1.0F);
            Minecraft.getMinecraft().getTextureManager().bindTexture(ASSETS);
            if(file.isFolder())
            {
                RenderUtil.drawRectWithTexture(x + 2, y + 2, 0, 0, 16, 14, 16, 14);
            }
            else
            {
                Application.Icon icon = ApplicationManager.getApp(file.getOpeningApp()).getIcon();
                Minecraft.getMinecraft().getTextureManager().bindTexture(icon.getResource());
                RenderUtil.drawRectWithTexture(x + 2, y + 2, icon.getU(), icon.getV(), 14, 14, 14, 14);
            }
            gui.drawString(Minecraft.getMinecraft().fontRendererObj, file.getName(), x + 22, y + 5, Color.WHITE.getRGB());
        }
    };

    private final Application app;
    private final Folder parent;

    private Folder current;
    private Folder clipboardDir;
    private File clipboardFile;

    private Stack<Folder> predecessor = new Stack<>();

    /**
     * Default constructor for the item list. Should be noted that the
     * height is determined by how many visible items there are.
     *
     * @param left         how many pixels from the left
     * @param top          how many pixels from the top
     * @param width        width of the list
     * @param visibleItems how many items are visible
     */
    public FileList(Application app, int left, int top, int width, int visibleItems, Folder parent)
    {
        super(left, top, width, visibleItems);
        this.app = app;
        this.parent = parent;
        this.current = parent;
        this.openFolder(parent, false);
    }

    @Override
    public void init(Layout layout)
    {
        super.init(layout);
        this.setListItemRenderer(ITEM_RENDERER);
        this.sortBy(File.SORT_BY_NAME);
    }

    public void openFolder(Folder folder, boolean push)
    {
        if(push) this.predecessor.push(current);
        this.current = folder;
        this.removeAll();
        this.setItems(folder.getFiles());
    }

    public void goToPreviousFolder()
    {
        if(predecessor.size() > 0)
        {
            Folder folder = predecessor.pop();
            openFolder(folder, false);
        }
    }

    public void addFile(File file)
    {
        if(current.add(file))
        {
            super.addItem(file);
        }
    }

    public void removeFile(int index)
    {
        File file = super.removeItem(index);
        if(file != null)
        {
            current.delete(file.getName());
        }
    }

    public void removeFile(File file)
    {
        current.delete(file.getName());
    }

    public void copyFile()
    {
        if(selected != -1)
        {
            clipboardDir = null;
            clipboardFile = getSelectedItem();
        }
    }

    public void cutFile()
    {
        if(selected != -1)
        {
            clipboardDir = current;
            clipboardFile = getSelectedItem();
        }
    }

    public void pasteFile()
    {
        if(clipboardFile != null)
        {
            addFile(clipboardFile.copy());
            if(clipboardDir != null)
            {
                clipboardDir.delete(clipboardFile.getName());
                clipboardDir = null;
                clipboardFile = null;
            }
        }
    }

    public boolean hasCopiedFile()
    {
        return clipboardFile != null;
    }

    public boolean isRootFolder()
    {
        return predecessor.size() == 0;
    }

    public String getPath()
    {
        StringBuilder builder = new StringBuilder(TextFormatting.GOLD + "/" + TextFormatting.RESET);
        for(int i = 1; i < predecessor.size(); i++)
        {
            builder.append(predecessor.get(i).getName());
            builder.append(TextFormatting.GOLD + "/" + TextFormatting.RESET);
        }
        if(current != parent)
        {
            builder.append(current.getName());
            builder.append(TextFormatting.GOLD + "/" + TextFormatting.RESET);
        }
        return builder.toString();
    }

    public void renameSelectedFile()
    {
        File file = getSelectedItem();
        if(file != null)
        {
            Dialog.Input dialog = new Dialog.Input("Enter a name");
            dialog.setResponseListener((success, s) ->
            {
                if(success)
                {
                    removeFile(file);
                    addFile(new File(s, file.getOpeningApp(), file.getData()));
                }
            });
            dialog.setTitle("Rename " + (file instanceof Folder ? "Folder" : "File"));
            dialog.setInputText(file.getName());
            app.openDialog(dialog);
        }
    }
}
