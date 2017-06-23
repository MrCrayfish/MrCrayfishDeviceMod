package com.mrcrayfish.device.programs.system.component;

import com.mrcrayfish.device.api.ApplicationManager;
import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.Dialog;
import com.mrcrayfish.device.api.app.Layout;
import com.mrcrayfish.device.api.app.component.Button;
import com.mrcrayfish.device.api.app.component.ItemList;
import com.mrcrayfish.device.api.app.component.Label;
import com.mrcrayfish.device.api.app.component.TextField;
import com.mrcrayfish.device.api.app.listener.ItemClickListener;
import com.mrcrayfish.device.api.app.renderer.ListItemRenderer;
import com.mrcrayfish.device.api.io.File;
import com.mrcrayfish.device.api.io.Folder;
import com.mrcrayfish.device.api.utils.RenderUtil;
import com.mrcrayfish.device.core.Wrappable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

import java.awt.Color;
import java.util.Stack;

/**
 * Created by Casey on 20-Jun-17.
 */
public class FileBrowser extends Component
{
    private static final ResourceLocation ASSETS = new ResourceLocation("cdm:textures/gui/file_browser.png");

    private static final Color ITEM_BACKGROUND = new Color(215, 217, 224);
    private static final Color ITEM_SELECTED = new Color(221, 208, 208);

    private static final ListItemRenderer<File> ITEM_RENDERER = new ListItemRenderer<File>(18)
    {
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

    public static boolean refreshList = false;

    private final Wrappable wrappable;
    private final Mode mode;

    private Layout main;
    private ItemList<File> fileList;
    private Button btnPreviousFolder;
    private Button btnNewFolder;
    private Button btnRename;
    private Button btnCopy;
    private Button btnCut;
    private Button btnPaste;
    private Button btnDelete;
    private Label label;

    private Stack<Folder> predecessor = new Stack<>();
    private Folder root;
    private Folder current;
    private Folder clipboardDir;
    private File clipboardFile;

    private long lastClick = 0;

    private ItemClickListener<File> itemClickListener;

    /**
     * The default constructor for a component. For your component to
     * be laid out correctly, make sure you use the x and y parameters
     * from {@link Application#init()} and pass them into the
     * x and y arguments of this constructor.
     * <p>
     * Laying out the components is a simple relative positioning. So for left (x position),
     * specific how many pixels from the left of the application window you want
     * it to be positioned at. The top is the same, but obviously from the top (y position).
     *
     * @param left how many pixels from the left
     * @param top  how many pixels from the top
     */
    public FileBrowser(int left, int top, Wrappable wrappable, Folder root, Mode mode)
    {
        super(left, top);
        this.wrappable = wrappable;
        this.root = root;
        this.mode = mode;
    }

    @Override
    public void init(Layout layout)
    {
        main = new Layout(225, mode.getHeight());
        main.setBackground((gui, mc, x, y, width, height, mouseX, mouseY, windowActive) ->
        {
            Gui.drawRect(x, y, x + width, y + 20, Color.GRAY.getRGB());
            Gui.drawRect(x, y + 20, x + width, y + 21, Color.DARK_GRAY.getRGB());
        });

        btnPreviousFolder = new Button(5, 2, ASSETS, 40, 20, 10, 10);
        btnPreviousFolder.setClickListener((c, mouseButton) ->
        {
            if(mouseButton == 0)
            {
                goToPreviousFolder();
                if(isRootFolder())
                {
                    btnPreviousFolder.setEnabled(false);
                }
                updatePath();
            }
        });
        btnPreviousFolder.setToolTip("Previous Folder", "Go back to the previous folder");
        btnPreviousFolder.setEnabled(false);
        main.addComponent(btnPreviousFolder);

        int btnIndex = 0;

        btnNewFolder = new Button(5, 25 + btnIndex * 20 , ASSETS, 0, 20, 10, 10);
        btnNewFolder.setClickListener((b, mouseButton) ->
        {
            Dialog.Input dialog = new Dialog.Input("Enter a name");
            dialog.setResponseHandler((success, v) ->
            {
                if(success)
                {
                    addFile(new Folder(v));
                }
                return true;
            });
            dialog.setTitle("Create a Folder");
            dialog.setPositiveText("Create");
            wrappable.openDialog(dialog);
        });
        btnNewFolder.setToolTip("New Folder", "Creates a new folder in this directory");
        main.addComponent(btnNewFolder);

        btnIndex++;

        btnRename = new Button(5, 25 + btnIndex * 20, ASSETS, 50, 20, 10, 10);
        btnRename.setClickListener((c, mouseButton) ->
        {
            renameSelectedFile();
        });
        btnRename.setToolTip("Rename", "Change the name of the selected file or folder");
        btnRename.setEnabled(false);
        main.addComponent(btnRename);

        if(mode == Mode.FULL)
        {
            btnIndex++;

            btnCopy = new Button(5, 25 + btnIndex * 20, ASSETS, 10, 20, 10, 10);
            btnCopy.setClickListener((b, mouseButton) ->
            {
                setClipboardFileToSelected();
                btnPaste.setEnabled(true);
            });
            btnCopy.setToolTip("Copy", "Copies the selected file or folder");
            btnCopy.setEnabled(false);
            main.addComponent(btnCopy);

            btnIndex++;

            btnCut = new Button(5, 25 + btnIndex * 20, ASSETS, 60, 20, 10, 10);
            btnCut.setClickListener((c, mouseButton) ->
            {
                cutSelectedFile();
                btnPaste.setEnabled(true);
            });
            btnCut.setToolTip("Cut", "Cuts the selected file or folder");
            btnCut.setEnabled(false);
            main.addComponent(btnCut);

            btnIndex++;

            btnPaste = new Button(5, 25 + btnIndex * 20, ASSETS, 20, 20, 10, 10);
            btnPaste.setClickListener((b, mouseButton) ->
            {
                pasteClipboardFile();
                if(!hasFileInClipboard())
                {
                    btnPaste.setEnabled(false);
                }
            });
            btnPaste.setToolTip("Paste", "Pastes the copied file into this directory");
            btnPaste.setEnabled(false);
            main.addComponent(btnPaste);
        }

        btnIndex++;

        btnDelete = new Button(5, 25 + btnIndex * 20, ASSETS, 30, 20, 10, 10);
        btnDelete.setClickListener((b, mouseButton) ->
        {
            File file = fileList.getSelectedItem();
            if(file != null)
            {
                Dialog.Confirmation dialog = new Dialog.Confirmation();
                StringBuilder builder = new StringBuilder();
                builder.append("Are you sure you want to delete this ");
                if(file.isFolder())
                {
                    builder.append("folder");
                }
                else
                {
                    builder.append("file");
                }
                builder.append(" '");
                builder.append(file.getName());
                builder.append("'?");
                dialog.setMessageText(builder.toString());
                dialog.setTitle("Delete");
                dialog.setPositiveText("Yes");
                dialog.setPositiveListener((c, mouseButton1) ->
                {
                    removeFile(fileList.getSelectedIndex());
                    btnRename.setEnabled(false);
                    btnDelete.setEnabled(false);
                    if(mode == Mode.FULL)
                    {
                        btnCopy.setEnabled(false);
                        btnCut.setEnabled(false);
                    }
                });
                wrappable.openDialog(dialog);
            }
        });
        btnDelete.setToolTip("Delete", "Deletes the selected file or folder");
        btnDelete.setEnabled(false);
        main.addComponent(btnDelete);

        fileList = new ItemList(mode.getOffset(), 25, 180, mode.getVisibleItems());
        fileList.setListItemRenderer(ITEM_RENDERER);
        fileList.sortBy(File.SORT_BY_NAME);
        fileList.setItemClickListener((file, index, mouseButton) ->
        {
            if(mouseButton == 0)
            {
                btnRename.setEnabled(true);
                btnDelete.setEnabled(true);
                if(mode == Mode.FULL)
                {
                    btnCopy.setEnabled(true);
                    btnCut.setEnabled(true);
                }
                if(System.currentTimeMillis() - this.lastClick <= 200)
                {
                    if(file.isFolder())
                    {
                        fileList.setSelectedIndex(-1);
                        openFolder((Folder) file, true);
                        if(mode == Mode.FULL)
                        {
                            btnRename.setEnabled(false);
                            btnCopy.setEnabled(false);
                            btnCut.setEnabled(false);
                            btnDelete.setEnabled(false);
                        }
                    }
                }
                else
                {
                    this.lastClick = System.currentTimeMillis();
                }
            }
            if(itemClickListener != null)
            {
                itemClickListener.onClick(file, index, mouseButton);
            }
        });
        main.addComponent(fileList);

        label = new Label("/", 26, 6);
        main.addComponent(label);
        layout.addComponent(main);

        openFolder(root, false);
    }

    @Override
    public void handleTick()
    {
        if(refreshList)
        {
            fileList.removeAll();
            fileList.setItems(current.getFiles());
        }
    }

    public void openFolder(Folder folder, boolean push)
    {
        if(push) {
            predecessor.push(current);
            btnPreviousFolder.setEnabled(true);
        }
        current = folder;
        fileList.removeAll();
        fileList.setItems(folder.getFiles());
        updatePath();
    }

    private void goToPreviousFolder()
    {
        if(predecessor.size() > 0)
        {
            Folder folder = predecessor.pop();
            openFolder(folder, false);
        }
    }

    public File getSelectedFile()
    {
        return fileList.getSelectedItem();
    }

    public boolean addFile(File file)
    {
        if(!current.add(file))
        {
            return false;
        }
        fileList.addItem(file);
        FileBrowser.refreshList = true;
        return true;
    }

    private void removeFile(int index)
    {
        File file = fileList.removeItem(index);
        if(file != null)
        {
            current.delete(file.getName());
            FileBrowser.refreshList = true;
        }
    }

    private void removeFile(File file)
    {
        if(fileList.getItems().remove(file))
        {
            current.delete(file.getName());
            FileBrowser.refreshList = true;
        }
    }

    public void removeFile(String name)
    {
        if(fileList.getItems().remove(current.getFile(name)))
        {
            current.delete(name);
            FileBrowser.refreshList = true;
        }
    }

    private void setClipboardFileToSelected()
    {
        if(fileList.getSelectedIndex() != -1)
        {
            clipboardDir = null;
            clipboardFile = fileList.getSelectedItem();
        }
    }

    private void cutSelectedFile()
    {
        if(fileList.getSelectedIndex() != -1)
        {
            clipboardDir = current;
            clipboardFile = fileList.getSelectedItem();
        }
    }

    private void pasteClipboardFile()
    {
        if(clipboardFile != null)
        {
            if(canPasteHere())
            {
                if(addFile(clipboardFile.copy()))
                {
                    if(clipboardDir != null)
                    {
                        clipboardDir.delete(clipboardFile.getName());
                        clipboardDir = null;
                        clipboardFile = null;
                    }
                }
                else
                {
                    Dialog.Input dialog = new Dialog.Input("A file with the same name already exists in this directory. Please choose a new name");
                    dialog.setPositiveText("Rename");
                    dialog.setInputText(clipboardFile.getName());
                    dialog.setResponseHandler((success, s) ->
                    {
                        if(success)
                        {
                            File file = renameFile(clipboardFile.copy(), s);
                            if(addFile(file))
                            {
                                if(clipboardDir != null)
                                {
                                    clipboardDir.delete(clipboardFile.getName());
                                    clipboardDir = null;
                                    clipboardFile = null;
                                }
                                return true;
                            }
                            else
                            {
                                TextField textField = dialog.getTextFieldInput();
                                textField.setText(s);
                                textField.setTextColour(Color.RED);
                            }
                        }
                        return false;
                    });
                    wrappable.openDialog(dialog);
                }
            }
            else
            {
                Dialog.Message dialog = new Dialog.Message("You cannot paste a copied folder inside itself");
                wrappable.openDialog(dialog);
            }
        }
    }

    private boolean canPasteHere()
    {
        if(clipboardFile != null)
        {
            if(clipboardFile instanceof Folder)
            {
                if(predecessor.contains(clipboardFile) || current == clipboardFile)
                {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean hasFileInClipboard()
    {
        return clipboardFile != null;
    }

    private boolean isRootFolder()
    {
        return predecessor.size() == 0;
    }

    private String getPath()
    {
        StringBuilder builder = new StringBuilder(TextFormatting.GOLD + "/" + TextFormatting.RESET);
        for(int i = 1; i < predecessor.size(); i++)
        {
            builder.append(predecessor.get(i).getName());
            builder.append(TextFormatting.GOLD + "/" + TextFormatting.RESET);
        }
        if(current != root)
        {
            builder.append(current.getName());
            builder.append(TextFormatting.GOLD + "/" + TextFormatting.RESET);
        }
        return builder.toString();
    }

    public void updatePath()
    {
        String path = getPath();
        int width = Minecraft.getMinecraft().fontRendererObj.getStringWidth(path);
        if(width > 190)
        {
            path = "..." + Minecraft.getMinecraft().fontRendererObj.trimStringToWidth(path, 190, true);
        }
        label.setText(path);
    }

    private void renameSelectedFile()
    {
        File file = fileList.getSelectedItem();
        if(file != null)
        {
            Dialog.Input dialog = new Dialog.Input("Enter a name");
            dialog.setResponseHandler((success, s) ->
            {
                if(success)
                {
                    removeFile(file);
                    addFile(renameFile(file, s));
                }
                return true;
            });
            dialog.setTitle("Rename " + (file instanceof Folder ? "Folder" : "File"));
            dialog.setInputText(file.getName());
            wrappable.openDialog(dialog);
        }
    }

    private File renameFile(File source, String newName)
    {
        if(source.isFolder())
        {
            Folder folder = new Folder(newName);
            folder.setFiles(((Folder) source).getFiles());
            return folder;
        }
        return new File(newName, source.getOpeningApp(), source.getData());
    }

    public void setItemClickListener(ItemClickListener<File> itemClickListener)
    {
        this.itemClickListener = itemClickListener;
    }

    public enum Mode
    {
        FULL(145, 26, 6), BASIC(100, 26, 4);

        private final int height;
        private final int offset;
        private final int visibleItems;

        Mode(int height, int offset, int visibleItems)
        {
            this.height = height;
            this.offset = offset;
            this.visibleItems = visibleItems;
        }

        public int getHeight()
        {
            return height;
        }

        public int getOffset()
        {
            return offset;
        }

        public int getVisibleItems()
        {
            return visibleItems;
        }
    }
}
