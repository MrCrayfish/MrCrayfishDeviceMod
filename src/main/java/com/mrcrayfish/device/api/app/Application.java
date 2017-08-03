package com.mrcrayfish.device.api.app;

import com.mrcrayfish.device.api.io.File;
import com.mrcrayfish.device.api.io.Folder;
import com.mrcrayfish.device.core.Laptop;
import com.mrcrayfish.device.core.TaskBar;
import com.mrcrayfish.device.core.Window;
import com.mrcrayfish.device.core.Wrappable;
import com.mrcrayfish.device.core.io.FileSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;

/**
 * The abstract base class for creating applications.
 *
 * @author MrCrayfish
 */
public abstract class Application extends Wrappable implements Info
{
    protected Icon icon = new Icon(TaskBar.APP_BAR_GUI, 0, 30);

    private int width;
    private int height;

    private final String APP_ID;
    private final String DISPLAY_NAME;
    private final Layout defaultLayout = new Layout();
    private Layout currentLayout;

    /**
     * If set to true, will update NBT data for Application
     */
    private boolean needsDataUpdate = false;

    private Window window;
    private FileSystem fileSystem;
    private BlockPos laptopPositon;

    /* If set to true, will update layout */ boolean pendingLayoutUpdate = false;

    public Application(String appId, String displayName)
    {
        this.APP_ID = appId;
        this.DISPLAY_NAME = displayName;
    }

    /**
     * Adds a component to the default layout. Don't get this confused with your
     * custom layout. You should use
     * {@link com.mrcrayfish.device.api.app.Layout#addComponent(Component)}
     * instead.
     *
     * @param c the component to add to the default layout
     */
    protected final void addComponent(Component c)
    {
        if(c != null)
        {
            defaultLayout.addComponent(c);
        }
    }

    /**
     * Sets the current layout of the application.
     *
     * @param layout
     */
    protected final void setCurrentLayout(Layout layout)
    {
        this.currentLayout = layout;
        this.width = layout.width;
        this.height = layout.height;
        this.pendingLayoutUpdate = true;
        this.currentLayout.init();
    }

    /**
     * Gets the current layout being displayed
     *
     * @return the current layout
     */
    protected final Layout getCurrentLayout()
    {
        return currentLayout;
    }

    /**
     * Restores the current layout to the default layout
     */
    protected final void restoreDefaultLayout()
    {
        this.setCurrentLayout(defaultLayout);
    }

    /**
     * The default initialization method. Clears any components in the default
     * layout and sets it as the current layout.
     */
    @Override
    public void init()
    {
        this.defaultLayout.clear();
        this.setCurrentLayout(defaultLayout);
    }

    @Override
    public void onTick()
    {
        currentLayout.handleTick();
    }

    // TODO Remove laptop instance

    /**
     * @param laptop
     * @param mc
     * @param x
     * @param y
     * @param mouseX
     * @param mouseY
     * @param active
     * @param partialTicks
     */
    @Override
    public void render(Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean active, float partialTicks)
    {
        currentLayout.render(laptop, mc, x, y, mouseX, mouseY, active, partialTicks);
        currentLayout.renderOverlay(laptop, mc, mouseX, mouseY, active);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderHelper.disableStandardItemLighting();
    }

    /**
     * Called when you press a mouse button. Note if you override, make sure you
     * call this super method.
     *
     * @param mouseX      the current x position of the mouse
     * @param mouseY      the current y position of the mouse
     * @param mouseButton the clicked mouse button
     */
    @Override
    public void handleMouseClick(int mouseX, int mouseY, int mouseButton)
    {
        currentLayout.handleMouseClick(mouseX, mouseY, mouseButton);
    }

    /**
     * Called when you drag the mouse with a button pressed down Note if you
     * override, make sure you call this super method.
     *
     * @param mouseX      the current x position of the mouse
     * @param mouseY      the current y position of the mouse
     * @param mouseButton the pressed mouse button
     */
    @Override
    public void handleMouseDrag(int mouseX, int mouseY, int mouseButton)
    {
        currentLayout.handleMouseDrag(mouseX, mouseY, mouseButton);
    }

    /**
     * Called when you release the currently pressed mouse button. Note if you
     * override, make sure you call this super method.
     *
     * @param mouseX      the x position of the release
     * @param mouseY      the y position of the release
     * @param mouseButton the button that was released
     */
    @Override
    public void handleMouseRelease(int mouseX, int mouseY, int mouseButton)
    {
        currentLayout.handleMouseRelease(mouseX, mouseY, mouseButton);
    }

    /**
     * Called when you scroll the wheel on your mouse. Note if you override,
     * make sure you call this super method.
     *
     * @param mouseX    the x position of the mouse
     * @param mouseY    the y position of the mouse
     * @param direction the direction of the scroll. true is up, false is down
     */
    @Override
    public void handleMouseScroll(int mouseX, int mouseY, boolean direction)
    {
        currentLayout.handleMouseScroll(mouseX, mouseY, direction);
    }

    /**
     * Called when a key is typed from your keyboard. Note if you override, make
     * sure you call this super method.
     *
     * @param character the typed character
     * @param code      the typed character code
     */
    @Override
    public void handleKeyTyped(char character, int code)
    {
        currentLayout.handleKeyTyped(character, code);
    }

    @Override
    public void handleKeyReleased(char character, int code)
    {
        currentLayout.handleKeyReleased(character, code);
    }

    // TODO: Remove from here and put into core

    /**
     * Updates the components of the current layout to adjust to new window
     * position. There is really be no reason to call this method.
     *
     * @param x
     * @param y
     */
    @Override
    public final void updateComponents(int x, int y)
    {
        currentLayout.updateComponents(x, y);
    }

    /**
     * Called when the application is closed
     */
    @Override
    public void onClose()
    {
        restoreDefaultLayout();
        defaultLayout.components.clear();
        currentLayout = null;
    }

    /**
     * Called when you first load up your application. Allows you to read any
     * stored data you have saved. Only called if you have saved data. This
     * method is called after {{@link #init()} so you can update any
     * Components with this data.
     *
     * @param tagCompound the tag compound where you saved data is
     */
    public abstract void load(NBTTagCompound tagCompound);

    /**
     * Allows you to save data from your application. This is only called if
     * {@link #isDirty()} returns true. You can mark your application as dirty
     * by calling {@link #markDirty()}.
     *
     * @param tagCompound the tag compound to save your data to
     */
    public abstract void save(NBTTagCompound tagCompound);

    /**
     * Sets the defaults layout width. It should be noted that the width must be
     * within 20 to 362.
     *
     * @param width the width
     */
    protected final void setDefaultWidth(int width)
    {
        if(width < 20) throw new IllegalArgumentException("Width must be larger than 20");
        this.defaultLayout.width = width;
    }

    /**
     * Sets the defaults layout height. It should be noted that the height must
     * be within 20 to 164.
     *
     * @param height the height
     */
    protected final void setDefaultHeight(int height)
    {
        if(height < 20) throw new IllegalArgumentException("Height must be larger than 20");
        this.defaultLayout.height = height;
    }

    /**
     * Marks that data in this application has changed and needs to be saved.
     * You must call this otherwise your data wont be saved!
     */
    protected void markDirty()
    {
        needsDataUpdate = true;
    }

    /**
     * Gets if this application is pending for it's data to be saved.
     *
     * @return if currently requiring data to be saved
     */
    public final boolean isDirty()
    {
        return needsDataUpdate;
    }

    /**
     * Cancels the data saving for this application
     */
    public final void clean()
    {
        needsDataUpdate = false;
    }

    public final void markForLayoutUpdate()
    {
        this.pendingLayoutUpdate = true;
    }

    /**
     * Gets if a layout is currently pending a layout update
     *
     * @return if pending layout update
     */
    @Override
    public final boolean isPendingLayoutUpdate()
    {
        return pendingLayoutUpdate;
    }

    /**
     * Clears the pending layout update
     */
    @Override
    public final void clearPendingLayout()
    {
        this.pendingLayoutUpdate = false;
    }

    /**
     * Gets the width of this application including the border.
     *
     * @return the height
     */
    @Override
    public final int getWidth()
    {
        return width;
    }

    /**
     * Gets the height of this application including the title bar.
     *
     * @return the height
     */
    @Override
    public final int getHeight()
    {
        return height;
    }

    /**
     * Gets the text in the title bar of the application. You can change the
     * text by setting a custom title for your layout. See
     * {@link Layout#setTitle}.
     *
     * @return The display name
     */
    @Override
    public String getWindowTitle()
    {
        if(currentLayout.hasTitle())
        {
            return currentLayout.getTitle();
        }
        return DISPLAY_NAME;
    }

    @Override
    public String getID()
    {
        return APP_ID;
    }

    @Override
    public String getDisplayName()
    {
        return DISPLAY_NAME;
    }

    /**
     * Gets the resource location the icon is located in
     *
     * @return the icon resource location
     */
    @Override
    public Icon getIcon()
    {
        return icon;
    }

    /**
     * Sets the icon for the application. Icons must be 14 by 14 pixels.
     *
     * @param icon
     */
    protected void setIcon(ResourceLocation resource, int u, int v)
    {
        this.icon = new Icon(resource, u, v);
    }

    public void setLaptopPosition(BlockPos pos)
    {
        this.laptopPositon = pos.toImmutable();
    }

    public BlockPos getLaptopPositon()
    {
        return laptopPositon;
    }

    public void setFileSystem(FileSystem fileSystem)
    {
        this.fileSystem = fileSystem;
    }

    public FileSystem getFileSystem()
    {
        return fileSystem;
    }

    @Nullable
    public Folder getApplicationFolder()
    {
        Folder root = fileSystem.getRootFolder();
        if(root.hasFolder("Application Data"))
        {
            Folder apps = (Folder) root.getFile("Application Data");
            if(!apps.hasFolder(APP_ID))
            {
                apps.add(new Folder(APP_ID), true);
            }
            return (Folder) apps.getFile(APP_ID);
        }
        return null;
    }

    /**
     * Gets the active dialog window for this application
     *
     * @return
     */
    @Nullable
    public Window<Dialog> getActiveDialog()
    {
        Window<Dialog> dialogWindow = getWindow().getDialogWindow();
        if(dialogWindow != null)
        {
            while(true)
            {
                if(dialogWindow.getDialogWindow() == null)
                {
                    return dialogWindow;
                }
                dialogWindow = dialogWindow.getDialogWindow();
            }
        }
        return dialogWindow;
    }

    /**
     * Determines how a file is handled when it's opened with this application. By default, this
     * method returns true, however returning false indicates that the file could not be opened and
     * will prompt the user with a dialog.
     *
     * @param file the file attempting to be opened
     */
    public boolean handleFile(File file)
    {
        return true;
    }

    /**
     * Check if an application is equal to another. Checking the ID is
     * sufficient as they should be unique.
     */
    @Override
    public boolean equals(Object obj)
    {
        if(obj == null) return false;
        if(!(obj instanceof Application)) return false;
        Application app = (Application) obj;
        return app.getID().equals(this.getID());
    }

    public static class Icon
    {
        protected ResourceLocation icon;
        protected int u, v;

        /**
         * @param resource
         * @param u
         * @param v
         */
        public Icon(ResourceLocation resource, int u, int v)
        {
            this.icon = resource;
            this.u = u;
            this.v = v;
        }

        public ResourceLocation getResource()
        {
            return icon;
        }

        public int getU()
        {
            return u;
        }

        public int getV()
        {
            return v;
        }
    }
}
