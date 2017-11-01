package com.mrcrayfish.device.programs.system.component;

import com.mrcrayfish.device.api.ApplicationManager;
import com.mrcrayfish.device.api.app.*;
import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.Dialog;
import com.mrcrayfish.device.api.app.component.Button;
import com.mrcrayfish.device.api.app.component.*;
import com.mrcrayfish.device.api.app.component.Label;
import com.mrcrayfish.device.api.app.component.TextField;
import com.mrcrayfish.device.api.app.listener.ClickListener;
import com.mrcrayfish.device.api.app.listener.ItemClickListener;
import com.mrcrayfish.device.api.app.renderer.ListItemRenderer;
import com.mrcrayfish.device.api.io.Drive;
import com.mrcrayfish.device.api.io.File;
import com.mrcrayfish.device.api.io.Folder;
import com.mrcrayfish.device.api.task.Callback;
import com.mrcrayfish.device.api.task.Task;
import com.mrcrayfish.device.api.task.TaskManager;
import com.mrcrayfish.device.api.utils.RenderUtil;
import com.mrcrayfish.device.core.Laptop;
import com.mrcrayfish.device.core.Window;
import com.mrcrayfish.device.core.Wrappable;
import com.mrcrayfish.device.core.io.FileSystem;
import com.mrcrayfish.device.core.io.action.FileAction;
import com.mrcrayfish.device.core.io.task.TaskGetFiles;
import com.mrcrayfish.device.core.io.task.TaskGetStructure;
import com.mrcrayfish.device.core.io.task.TaskSetupFileBrowser;
import com.mrcrayfish.device.object.AppInfo;
import com.mrcrayfish.device.programs.system.SystemApplication;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.awt.*;
import java.lang.System;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by Casey on 20-Jun-17.
 */
public class FileBrowser extends Component
{
    private static final ResourceLocation ASSETS = new ResourceLocation("cdm:textures/gui/file_browser.png");

    private static final Color HEADER_BACKGROUND = Color.decode("0x616161");
    private static final Color ITEM_BACKGROUND = Color.decode("0x9E9E9E");
    private static final Color ITEM_SELECTED = Color.decode("0x757575");
    private static final Color PROTECTED_FILE = new Color(155, 237, 242);

    private static final ListItemRenderer<File> ITEM_RENDERER = new ListItemRenderer<File>(18)
    {
        @Override
        public void render(File file, Gui gui, Minecraft mc, int x, int y, int width, int height, boolean selected)
        {
            Gui.drawRect(x, y, x + width, y + height, selected ? ITEM_SELECTED.getRGB() : ITEM_BACKGROUND.getRGB());

            GlStateManager.color(1.0F, 1.0F, 1.0F);
            Minecraft.getMinecraft().getTextureManager().bindTexture(ASSETS);
            if(file.isFolder())
            {
                RenderUtil.drawRectWithTexture(x + 3, y + 2, 0, 0, 14, 14, 14, 14);
            }
            else
            {
                AppInfo info = ApplicationManager.getApplication(file.getOpeningApp());
                RenderUtil.drawApplicationIcon(info, x + 3, y + 2);
            }
            Color color = file.isProtected() ? PROTECTED_FILE : Color.WHITE;
            gui.drawString(Minecraft.getMinecraft().fontRendererObj, file.getName(), x + 22, y + 5, color.getRGB());
        }
    };

    public static boolean refreshList = false;

    private final Wrappable wrappable;
    private final Mode mode;

    private Layout layoutMain;
    private ItemList<File> fileList;
    private Button btnPreviousFolder;
    private Button btnNewFolder;
    private Button btnRename;
    private Button btnCopy;
    private Button btnCut;
    private Button btnPaste;
    private Button btnDelete;

    private ComboBox.List<Drive> comboBoxDrive;
    private Label labelPath;

    private Layout layoutLoading;
    private Spinner spinnerLoading;

    private Stack<Folder> predecessor = new Stack<>();
    private Drive currentDrive;
    private Folder currentFolder;

    private Drive clipboardDrive;
    private Folder clipboardDir;
    private File clipboardFile;

    private String initialFolder = FileSystem.DIR_ROOT;
    private boolean loadedStructure = false;

    private long lastClick = 0;

    private ItemClickListener<File> itemClickListener;

    private Predicate<File> filter;

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
    public FileBrowser(int left, int top, Wrappable wrappable, Mode mode)
    {
        super(left, top);
        this.wrappable = wrappable;
        this.mode = mode;
    }

    @Override
    public void init(Layout layout)
    {
        layoutMain = new Layout(mode.getWidth(), mode.getHeight());
        layoutMain.setBackground((gui, mc, x, y, width, height, mouseX, mouseY, windowActive) ->
        {
            Gui.drawRect(x, y, x + width, y + 20, Laptop.getSystem().getSettings().getColourScheme().getBackgroundColour());
            Gui.drawRect(x, y + 20, x + width, y + 21, Color.DARK_GRAY.getRGB());
        });

        btnPreviousFolder = new Button(5, 2, Icons.ARROW_LEFT);
        btnPreviousFolder.setClickListener((c, mouseButton) ->
        {
            if(mouseButton == 0)
            {
                goToPreviousFolder();
            }
        });
        btnPreviousFolder.setToolTip("Previous Folder", "Go back to the previous folder");
        btnPreviousFolder.setEnabled(false);
        layoutMain.addComponent(btnPreviousFolder);

        int btnIndex = 0;

        btnNewFolder = new Button(5, 25 + btnIndex * 20, Icons.NEW_FOLDER);
        btnNewFolder.setClickListener((b, mouseButton) ->
        {
            if(mouseButton == 0)
            {
                createFolder();
            }
        });
        btnNewFolder.setToolTip("New Folder", "Creates a new folder in this directory");
        layoutMain.addComponent(btnNewFolder);

        btnIndex++;

        btnRename = new Button(5, 25 + btnIndex * 20, Icons.RENAME);
        btnRename.setClickListener((c, mouseButton) ->
        {
            if(mouseButton == 0)
            {
                renameSelectedFile();
            }
        });
        btnRename.setToolTip("Rename", "Change the name of the selected file or folder");
        btnRename.setEnabled(false);
        layoutMain.addComponent(btnRename);

        if(mode == Mode.FULL)
        {
            btnIndex++;

            btnCopy = new Button(5, 25 + btnIndex * 20, Icons.COPY);
            btnCopy.setClickListener((b, mouseButton) ->
            {
                if(mouseButton == 0)
                {
                    setClipboardFileToSelected();
                }
            });
            btnCopy.setToolTip("Copy", "Copies the selected file or folder");
            btnCopy.setEnabled(false);
            layoutMain.addComponent(btnCopy);

            btnIndex++;

            btnCut = new Button(5, 25 + btnIndex * 20, Icons.CUT);
            btnCut.setClickListener((c, mouseButton) ->
            {
                if(mouseButton == 0)
                {
                    cutSelectedFile();
                }
            });
            btnCut.setToolTip("Cut", "Cuts the selected file or folder");
            btnCut.setEnabled(false);
            layoutMain.addComponent(btnCut);

            btnIndex++;

            btnPaste = new Button(5, 25 + btnIndex * 20, Icons.CLIPBOARD);
            btnPaste.setClickListener((b, mouseButton) ->
            {
                if(mouseButton == 0)
                {
                    pasteClipboardFile();
                }
            });
            btnPaste.setToolTip("Paste", "Pastes the copied file into this directory");
            btnPaste.setEnabled(false);
            layoutMain.addComponent(btnPaste);
        }

        btnIndex++;

        btnDelete = new Button(5, 25 + btnIndex * 20, Icons.TRASH);
        btnDelete.setClickListener((b, mouseButton) ->
        {
            if(mouseButton == 0)
            {
                deleteSelectedFile();
            }
        });
        btnDelete.setToolTip("Delete", "Deletes the selected file or folder");
        btnDelete.setEnabled(false);
        layoutMain.addComponent(btnDelete);

        fileList = new ItemList<>(mode.getOffset(), 25, 180, mode.getVisibleItems());
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
                        openFolder((Folder) file, true, (folder, success) ->
                        {
                            if(mode == Mode.FULL)
                            {
                                btnRename.setEnabled(false);
                                btnCopy.setEnabled(false);
                                btnCut.setEnabled(false);
                                btnDelete.setEnabled(false);
                            }
                        });
                    }
                    else if(mode == Mode.FULL && wrappable instanceof SystemApplication)
                    {
                        SystemApplication systemApp = (SystemApplication) wrappable;
                        Laptop laptop = systemApp.getLaptop();
                        if(laptop != null)
                        {
                            //TODO change to check if application is installed
                            Application targetApp = laptop.getApplication(file.getOpeningApp());
                            if(targetApp != null)
                            {
                                laptop.open(targetApp);
                                if(!targetApp.handleFile(file))
                                {
                                    laptop.close(targetApp);
                                    laptop.open(systemApp);
                                    createErrorDialog(targetApp.getInfo().getName() + " was unable to open the file.");
                                }
                            }
                            else
                            {
                                createErrorDialog("The application designed for this file does not exist or is not installed.");
                            }
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
        layoutMain.addComponent(fileList);

        comboBoxDrive = new ComboBox.List<>(26, 3, 44, 100, new Drive[]{});
        comboBoxDrive.setChangeListener((oldValue, newValue) ->
        {
            openDrive(newValue);
        });
        comboBoxDrive.setListItemRenderer(new ListItemRenderer<Drive>(12)
        {
            @Override
            public void render(Drive drive, Gui gui, Minecraft mc, int x, int y, int width, int height, boolean selected)
            {
                drawRect(x, y, x + width, y + height, Color.GRAY.getRGB());
                mc.getTextureManager().bindTexture(ASSETS);
                GlStateManager.color(1.0F, 1.0F, 1.0F);
                RenderUtil.drawRectWithTexture(x + 2, y + 2, drive.getType().ordinal() * 8, 30, 8, 8, 8, 8);

                String text = drive.getName();
                if(mc.fontRendererObj.getStringWidth(text) > 87)
                {
                    text = mc.fontRendererObj.trimStringToWidth(drive.getName(), 78) + "...";
                }
                mc.fontRendererObj.drawString(text, x + 13, y + 2, Color.WHITE.getRGB(), true);
            }
        });
        layoutMain.addComponent(comboBoxDrive);

        labelPath = new Label("/", 72, 6);
        layoutMain.addComponent(labelPath);
        layout.addComponent(layoutMain);

        layoutLoading = new Layout(mode.getOffset(), 25, fileList.getWidth(), fileList.getHeight());
        layoutLoading.setBackground((gui, mc, x, y, width, height, mouseX, mouseY, windowActive) ->
        {
            Gui.drawRect(x, y, x + width, y + height, Window.COLOUR_WINDOW_DARK);
        });
        layoutLoading.setVisible(false);

        spinnerLoading = new Spinner((layoutLoading.width - 12) / 2, (layoutLoading.height - 12) / 2);
        layoutLoading.addComponent(spinnerLoading);
        layout.addComponent(layoutLoading);
    }

    @Override
    public void handleOnLoad()
    {
        if(!loadedStructure)
        {
            setLoading(true);
            Task task = new TaskSetupFileBrowser(Laptop.getPos(), Laptop.getMainDrive() == null);
            task.setCallback((nbt, success) ->
            {
                if(success)
                {
                    if(Laptop.getMainDrive() == null)
                    {
                        NBTTagCompound structureTag = nbt.getCompoundTag("structure");
                        Drive drive = new Drive(nbt.getCompoundTag("main_drive"));
                        drive.syncRoot(Folder.fromTag(FileSystem.LAPTOP_DRIVE_NAME, structureTag));
                        drive.getRoot().validate();
                        Laptop.setMainDrive(drive);
                    }

                    NBTTagList driveList = nbt.getTagList("available_drives", Constants.NBT.TAG_COMPOUND);
                    Drive[] drives = new Drive[driveList.tagCount() + 1];
                    drives[0] = currentDrive = Laptop.getMainDrive();
                    for(int i = 0; i < driveList.tagCount(); i++)
                    {
                        NBTTagCompound driveTag = driveList.getCompoundTagAt(i);
                        drives[i + 1] = new Drive(driveTag);
                    }
                    comboBoxDrive.setItems(drives);

                    Folder folder = currentDrive.getFolder(initialFolder);
                    if(folder != null)
                    {
                        pushPredecessors(folder);
                        openFolder(folder, false, (folder1, success1) ->
                        {
                            if(!success1)
                            {
                                createErrorDialog("A critical error occurred while initializing.");
                            }
                        });
                        return;
                    }
                    else
                    {
                        openFolder(currentDrive.getRoot(), false, (folder12, success12) ->
                        {
                            if(success)
                            {
                                createErrorDialog("Unable to open directory '" + initialFolder + "'");
                            }
                            else
                            {
                                createErrorDialog("A critical error occurred while initializing.");
                            }
                        });
                    }
                }
                else
                {
                    createErrorDialog("A critical error occurred while initializing.");
                }
                setLoading(false);
            });
            TaskManager.sendTask(task);
            loadedStructure = true;
        }
    }

    @Override
    public void handleTick()
    {
        if(refreshList)
        {
            fileList.removeAll();
            fileList.setItems(currentFolder.getFiles());
        }
    }

    public void openFolder(String directory)
    {
        this.initialFolder = directory;
    }

    private void openDrive(Drive drive)
    {
        predecessor.clear();
        if(drive.isSynced())
        {
            openFolder(drive.getRoot(), false, (folder, success) ->
            {
                if(!success)
                {
                    createErrorDialog("Unable to open drive '" + drive.getName() + "'");
                }
            });
        }
        else
        {
            setLoading(true);
            TaskGetStructure task = new TaskGetStructure(drive, Laptop.getPos());
            task.setCallback((nbt, success) ->
            {
                setLoading(false);
                if(success)
                {
                    Folder folder = Folder.fromTag(nbt.getString("file_name"), nbt.getCompoundTag("structure"));
                    drive.syncRoot(folder);
                    openFolder(drive.getRoot(), false, (folder1, success1) ->
                    {
                        if(!success1)
                        {
                            createErrorDialog("Unable to open drive '" + drive.getName() + "'");
                        }
                    });
                }
                else
                {
                    createErrorDialog("Unable to retrieve drive structure for '" + drive.getName() + "'");
                }
            });
            TaskManager.sendTask(task);
        }
    }

    private void openFolder(Folder folder, boolean push, Callback<Folder> callback)
    {
        if(!folder.isSynced())
        {
            setLoading(true);
            Task task = new TaskGetFiles(folder, Laptop.getPos()); //TODO convert to file system
            task.setCallback((nbt, success) ->
            {
                if(success && nbt.hasKey("files", Constants.NBT.TAG_LIST))
                {
                    NBTTagList files = nbt.getTagList("files", Constants.NBT.TAG_COMPOUND);
                    folder.syncFiles(files);
                    setCurrentFolder(folder, push);
                }
                if(callback != null)
                {
                    callback.execute(folder, success);
                }
                setLoading(false);
            });
            TaskManager.sendTask(task);
        }
        else
        {
            setCurrentFolder(folder, push);
            if(callback != null)
            {
                callback.execute(folder, true);
            }
            setLoading(false);
        }
    }

    private void setCurrentFolder(Folder folder, boolean push)
    {
        if(push)
        {
            predecessor.push(currentFolder);
            btnPreviousFolder.setEnabled(true);
        }
        currentDrive = folder.getDrive();
        currentFolder = folder;
        fileList.removeAll();

        List<File> files = folder.getFiles();
        if(filter != null)
        {
            files = files.stream().filter(filter).collect(Collectors.toList());
        }
        fileList.setItems(files);

        updatePath();
    }

    private void pushPredecessors(Folder folder)
    {
        List<Folder> predecessors = new ArrayList<>();
        Folder temp = folder.getParent();
        while(temp != null)
        {
            predecessors.add(temp);
            temp = temp.getParent();
        }
        Collections.reverse(predecessors);
        predecessors.forEach(f -> predecessor.push(f));
        if(predecessor.size() > 0)
        {
            btnPreviousFolder.setEnabled(true);
        }
    }

    private void createFolder()
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
    }

    private void goToPreviousFolder()
    {
        if(predecessor.size() > 0)
        {
            setLoading(true);
            Folder folder = predecessor.pop();
            openFolder(folder, false, (folder2, success) ->
            {
                if(success)
                {
                    if(isRootFolder())
                    {
                        btnPreviousFolder.setEnabled(false);
                    }
                    updatePath();
                }
                else
                {
                    createErrorDialog("Unable to open previous folder");
                }
                setLoading(false);
            });
        }
    }

    public File getSelectedFile()
    {
        return fileList.getSelectedItem();
    }

    public void addFile(File file)
    {
        addFile(file, null);
    }

    public void addFile(File file, Callback<FileSystem.Response> callback)
    {
        setLoading(true);
        currentFolder.add(file, (response, success) ->
        {
            if(response.getStatus() == FileSystem.Status.SUCCESSFUL)
            {
                fileList.addItem(file);
                FileBrowser.refreshList = true;
            }
            if(callback != null)
            {
                callback.execute(response, success);
            }
            setLoading(false);
        });
    }

    private void deleteSelectedFile()
    {
        File file = fileList.getSelectedItem();
        if(file != null)
        {
            if(file.isProtected())
            {
                String message = "This " + (file.isFolder() ? "folder" : "file") + " is protected and can not be deleted.";
                Dialog.Message dialog = new Dialog.Message(message);
                wrappable.openDialog(dialog);
                return;
            }

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
            builder.append(" '").append(file.getName()).append("'?");
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
    }

    private void removeFile(int index)
    {
        File file = fileList.getItem(index);
        if(file != null)
        {
            if(file.isProtected())
            {
                String message = "This " + (file.isFolder() ? "folder" : "file") + " is protected and can not be deleted.";
                Dialog.Message dialog = new Dialog.Message(message);
                wrappable.openDialog(dialog);
                return;
            }
            setLoading(true);
            currentFolder.delete(file, (response, success) ->
            {
                if(success)
                {
                    fileList.removeItem(index);
                    FileBrowser.refreshList = true;
                }
                setLoading(false);
            });
        }
    }

    public void removeFile(String name)
    {
        File file = currentFolder.getFile(name);
        if(file != null)
        {
            if(file.isProtected())
            {
                String message = "This " + (file.isFolder() ? "folder" : "file") + " is protected and can not be deleted.";
                Dialog.Message dialog = new Dialog.Message(message);
                wrappable.openDialog(dialog);
                return;
            }
            setLoading(true);
            currentFolder.delete(file, (o, success) ->
            {
                if(success)
                {
                    int index = fileList.getItems().indexOf(file);
                    fileList.removeItem(index);
                    FileBrowser.refreshList = true;
                }
                setLoading(false);
            });
        }
    }

    private void setClipboardFileToSelected()
    {
        if(fileList.getSelectedIndex() != -1)
        {
            File file = fileList.getSelectedItem();
            if(file.isProtected())
            {
                String message = "This " + (file.isFolder() ? "folder" : "file") + " is protected and can not be copied.";
                Dialog.Message dialog = new Dialog.Message(message);
                wrappable.openDialog(dialog);
                return;
            }
            clipboardDir = null;
            clipboardFile = file;
            btnPaste.setEnabled(true);
        }
    }

    private void cutSelectedFile()
    {
        if(fileList.getSelectedIndex() != -1)
        {
            File file = fileList.getSelectedItem();
            if(file.isProtected())
            {
                String message = "This " + (file.isFolder() ? "folder" : "file") + " is protected and can not be cut.";
                Dialog.Message dialog = new Dialog.Message(message);
                wrappable.openDialog(dialog);
                return;
            }
            clipboardDrive = comboBoxDrive.getValue();
            clipboardDir = currentFolder;
            clipboardFile = file;
            btnPaste.setEnabled(true);
        }
    }

    private void pasteClipboardFile()
    {
        if(clipboardFile != null)
        {
            if(canPasteHere())
            {
                if(currentFolder.hasFile(clipboardFile.getName()))
                {
                    Dialog.Confirmation dialog = new Dialog.Confirmation("A file with the same name already exists in this directory. Do you want to override it?");
                    dialog.setPositiveText("Override");
                    dialog.setPositiveListener((c, mouseButton) ->
                    {
                        if(mouseButton == 0)
                        {
                            handleCopyCut(true);
                        }
                    });
                    wrappable.openDialog(dialog);
                }
                else
                {
                    handleCopyCut(false);
                }
            }
            else
            {
                Dialog.Message dialog = new Dialog.Message("Destination folder can't be a subfolder");
                wrappable.openDialog(dialog);
            }
        }
    }

    private void handleCopyCut(boolean override)
    {
        if(clipboardDir != null)
        {
            setLoading(true);
            clipboardFile.moveTo(currentFolder, override, (response, success) ->
            {
                if(response.getStatus() == FileSystem.Status.SUCCESSFUL)
                {
                    resetClipboard();
                }
                else
                {
                    createErrorDialog(response.getMessage());
                }
                setLoading(false);
            });
        }
        else
        {
            setLoading(true);
            clipboardFile.copyTo(currentFolder, override, (response, success) ->
            {
                if(response.getStatus() == FileSystem.Status.SUCCESSFUL)
                {
                    resetClipboard();
                }
                else
                {
                    createErrorDialog(response.getMessage());
                }
                setLoading(false);
            });
        }
    }

    private void resetClipboard()
    {
        if(clipboardDir != null)
        {
            clipboardDir.refresh();
            clipboardDir = null;
            clipboardFile = null;
            btnPaste.setEnabled(false);
        }
        currentFolder.refresh();
        openFolder(currentFolder, false, (folder, success) ->
        {
            if(mode == Mode.FULL)
            {
                btnRename.setEnabled(false);
                btnCopy.setEnabled(false);
                btnCut.setEnabled(false);
                btnDelete.setEnabled(false);
            }
        });
    }

    private boolean canPasteHere()
    {
        if(clipboardFile != null)
        {
            if(clipboardFile instanceof Folder)
            {
                if(predecessor.contains(clipboardFile) || currentFolder == clipboardFile)
                {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isRootFolder()
    {
        return predecessor.size() == 0;
    }

    private void updatePath()
    {
        String path = currentFolder.getPath();
        path = path.replace("/", TextFormatting.GOLD + "/" + TextFormatting.RESET);
        int width = Minecraft.getMinecraft().fontRendererObj.getStringWidth(path);
        if(width > 144)
        {
            path = "..." + Minecraft.getMinecraft().fontRendererObj.trimStringToWidth(path, 144, true);
        }
        labelPath.setText(path);
    }

    public void setLoading(boolean loading)
    {
        layoutLoading.setVisible(loading);
        if(loading)
        {
            disableAllButtons();
        }
        else
        {
            updateButtons();
        }
    }

    private void updateButtons()
    {
        boolean hasSelectedFile = fileList.getSelectedIndex() != -1;
        btnNewFolder.setEnabled(true);
        btnRename.setEnabled(hasSelectedFile);
        btnDelete.setEnabled(hasSelectedFile);
        if(mode == Mode.FULL)
        {
            btnCopy.setEnabled(hasSelectedFile);
            btnCut.setEnabled(hasSelectedFile);
            btnPaste.setEnabled(clipboardFile != null);
        }
        btnPreviousFolder.setEnabled(!isRootFolder());
    }

    private void disableAllButtons()
    {
        btnPreviousFolder.setEnabled(false);
        btnNewFolder.setEnabled(false);
        btnRename.setEnabled(false);
        btnDelete.setEnabled(false);
        if(mode == Mode.FULL)
        {
            btnCopy.setEnabled(false);
            btnCut.setEnabled(false);
            btnPaste.setEnabled(false);
        }
    }

    private void renameSelectedFile()
    {
        File file = fileList.getSelectedItem();
        if(file != null)
        {
            if(file.isProtected())
            {
                String message = "This " + (file.isFolder() ? "folder" : "file") + " is protected and can not be renamed.";
                Dialog.Message dialog = new Dialog.Message(message);
                wrappable.openDialog(dialog);
                return;
            }

            Dialog.Input dialog = new Dialog.Input("Enter a name");
            dialog.setResponseHandler((success, s) ->
            {
                if(success)
                {
                    setLoading(true);
                    file.rename(s, (response, success1) ->
                    {
                        if(response.getStatus() == FileSystem.Status.SUCCESSFUL)
                        {
                            dialog.close();
                        }
                        else
                        {
                            createErrorDialog(response.getMessage());
                        }
                        setLoading(false);
                    });
                }
                return false;
            });
            dialog.setTitle("Rename " + (file instanceof Folder ? "Folder" : "File"));
            dialog.setInputText(file.getName());
            wrappable.openDialog(dialog);
        }
    }

    private void createErrorDialog(String message)
    {
        Dialog.Message dialog = new Dialog.Message(message);
        dialog.setTitle("Error");
        wrappable.openDialog(dialog);
    }

    public void setFilter(Predicate<File> filter)
    {
        this.filter = filter;
    }

    public void setItemClickListener(ItemClickListener<File> itemClickListener)
    {
        this.itemClickListener = itemClickListener;
    }

    public enum Mode
    {
        FULL(225, 145, 26, 6), BASIC(210, 100, 26, 4);

        private final int width;
        private final int height;
        private final int offset;
        private final int visibleItems;

        Mode(int width, int height, int offset, int visibleItems)
        {
            this.width = width;
            this.height = height;
            this.offset = offset;
            this.visibleItems = visibleItems;
        }

        public int getWidth()
        {
            return width;
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
