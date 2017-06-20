package com.mrcrayfish.device.programs.system.component;

import com.mrcrayfish.device.api.app.*;
import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.component.Button;
import com.mrcrayfish.device.api.app.component.Label;
import com.mrcrayfish.device.api.io.File;
import com.mrcrayfish.device.api.io.Folder;
import com.mrcrayfish.device.core.Laptop;
import com.mrcrayfish.device.programs.system.ApplicationFileBrowser;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.awt.Dialog;

/**
 * Created by Casey on 20-Jun-17.
 */
public class FileBrowser extends Component
{
    private static final ResourceLocation ASSETS = new ResourceLocation("cdm:textures/gui/file_browser.png");
    private static final int CLICK_INTERVAL = (Integer) Toolkit.getDefaultToolkit().getDesktopProperty("awt.multiClickInterval");

    private Application app;
    private boolean useFullLayout;

    private Layout main;
    private Button btnPreviousFolder;
    private Button btnNewFolder;
    private Button btnRename;
    private Button btnCopy;
    private Button btnCut;
    private Button btnPaste;
    private Button btnDelete;
    private FileList fileList;
    private Label label;

    private long lastClick = 0;

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
    public FileBrowser(int left, int top, Application app, boolean useFullLayout)
    {
        super(left, top);
        this.app = app;
        this.useFullLayout = useFullLayout;
    }

    @Override
    public void init(Layout layout)
    {
        main = new Layout(225, 145);
        main.setBackground((gui, mc, x, y, width, height, mouseX, mouseY, windowActive) -> {
            Gui.drawRect(x, y, x + width, y + 20, Color.GRAY.getRGB());
            Gui.drawRect(x, y + 20, x + width, y + 21, Color.DARK_GRAY.getRGB());
        });

        btnPreviousFolder = new Button(5, 2, ASSETS, 40, 20, 10, 10);
        btnPreviousFolder.setClickListener((c, mouseButton) -> {
            if(mouseButton == 0) {
                fileList.goToPreviousFolder();
                if(fileList.isRootFolder()) {
                    btnPreviousFolder.setEnabled(false);
                }
                updatePath();
            }
        });
        btnPreviousFolder.setToolTip("Previous Folder", "Go back to the previous folder");
        btnPreviousFolder.setEnabled(false);
        main.addComponent(btnPreviousFolder);

        btnNewFolder = new Button(5, 25, ASSETS, 0, 20, 10, 10);
        btnNewFolder.setClickListener((b, mouseButton) -> {
            com.mrcrayfish.device.api.app.Dialog.Input dialog = new com.mrcrayfish.device.api.app.Dialog.Input("Enter a name");
            dialog.setResponseHandler((success, v) -> {
                if(success) {
                    fileList.addFile(new Folder(v));
                }
                return true;
            });
            dialog.setTitle("Create a Folder");
            dialog.setPositiveText("Create");
            app.openDialog(dialog);
        });
        btnNewFolder.setToolTip("New Folder", "Creates a new folder in this directory");
        main.addComponent(btnNewFolder);

        btnRename = new Button(5, 45, ASSETS, 50, 20, 10, 10);
        btnRename.setClickListener((c, mouseButton) -> {
            fileList.renameSelectedFile();
        });
        btnRename.setToolTip("Rename", "Change the name of the selected file or folder");
        btnRename.setEnabled(false);
        main.addComponent(btnRename);

        if(useFullLayout)
        {
            btnCopy = new Button(5, 65, ASSETS, 10, 20, 10, 10);
            btnCopy.setClickListener((b, mouseButton) -> {
                fileList.setClipboardFileToSelected();
                btnPaste.setEnabled(true);
            });
            btnCopy.setToolTip("Copy", "Copies the selected file or folder");
            btnCopy.setEnabled(false);
            main.addComponent(btnCopy);

            btnCut = new Button(5, 85, ASSETS, 60, 20, 10, 10);
            btnCut.setClickListener((c, mouseButton) -> {
                fileList.cutSelectedFile();
                btnPaste.setEnabled(true);
            });
            btnCut.setToolTip("Cut", "Cuts the selected file or folder");
            btnCut.setEnabled(false);
            main.addComponent(btnCut);

            btnPaste = new Button(5, 105, ASSETS, 20, 20, 10, 10);
            btnPaste.setClickListener((b, mouseButton) -> {
                fileList.pasteClipboardFile();
                if(!fileList.hasFileInClipboard()) {
                    btnPaste.setEnabled(false);
                }
            });
            btnPaste.setToolTip("Paste", "Pastes the copied file into this directory");
            btnPaste.setEnabled(false);
            main.addComponent(btnPaste);
        }

        btnDelete = new Button(5, useFullLayout ? 124 : 65, ASSETS, 30, 20, 10, 10);
        btnDelete.setClickListener((b, mouseButton) -> {
            File file = fileList.getSelectedItem();
            if(file != null) {
                com.mrcrayfish.device.api.app.Dialog.Confirmation dialog = new com.mrcrayfish.device.api.app.Dialog.Confirmation();
                StringBuilder builder = new StringBuilder();
                builder.append("Are you sure you want to delete this ");
                if(file instanceof Folder) {
                    builder.append("folder");
                } else {
                    builder.append("file");
                }
                builder.append(" '");
                builder.append(file.getName());
                builder.append("'?");
                dialog.setMessageText(builder.toString());
                dialog.setTitle("Delete");
                dialog.setPositiveButton("Yes", (c, mouseButton1) -> {
                    fileList.removeFile(fileList.getSelectedIndex());
                    btnRename.setEnabled(false);
                    btnCopy.setEnabled(false);
                    btnCut.setEnabled(false);
                    btnDelete.setEnabled(false);
                });
                app.openDialog(dialog);
            }
        });
        btnDelete.setToolTip("Delete", "Deletes the selected file or folder");
        btnDelete.setEnabled(false);
        main.addComponent(btnDelete);

        Folder folder = app.getFileSystem().getBaseFolder();
        fileList = new FileList(app, 26, 25, 180, useFullLayout ? 6 : 5, folder);
        fileList.setItemClickListener((file, index, mouseButton) -> {
            if(mouseButton == 0) {
                btnRename.setEnabled(true);
                btnCopy.setEnabled(true);
                btnCut.setEnabled(true);
                btnDelete.setEnabled(true);
                if(System.currentTimeMillis() - this.lastClick <= CLICK_INTERVAL) {
                    if(file instanceof Folder) {
                        fileList.setSelectedIndex(-1);
                        fileList.openFolder((Folder) file, true);
                        btnPreviousFolder.setEnabled(true);
                        updatePath();
                    }
                } else {
                    this.lastClick = System.currentTimeMillis();
                }
            }
        });
        main.addComponent(fileList);

        label = new Label("/", 26, 6);
        main.addComponent(label);

        layout.addComponent(main);
    }

    @Override
    public void render(Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks)
    {

    }

    public void updatePath()
    {
        String path = fileList.getPath();
        int width = Minecraft.getMinecraft().fontRendererObj.getStringWidth(path);
        if(width > 190)
        {
            path = "..." + Minecraft.getMinecraft().fontRendererObj.trimStringToWidth(path, 190, true);
        }
        label.setText(path);
    }
}
