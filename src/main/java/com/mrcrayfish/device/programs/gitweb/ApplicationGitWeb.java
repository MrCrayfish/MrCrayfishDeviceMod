package com.mrcrayfish.device.programs.gitweb;

import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.app.Dialog.Confirmation;
import com.mrcrayfish.device.api.app.Icons;
import com.mrcrayfish.device.api.app.Layout;
import com.mrcrayfish.device.api.app.ScrollableLayout;
import com.mrcrayfish.device.api.app.component.*;
import com.mrcrayfish.device.api.app.component.Button;
import com.mrcrayfish.device.api.app.component.TextField;
import com.mrcrayfish.device.api.utils.OnlineRequest;
import com.mrcrayfish.device.core.Laptop;
import com.mrcrayfish.device.programs.gitweb.layout.TextLayout;
import com.mrcrayfish.device.programs.system.layout.StandardLayout;
import net.minecraft.client.gui.Gui;
import net.minecraft.nbt.NBTTagCompound;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.regex.Pattern;

/**
 * The Device Mod implementations of an internet layoutBrowser. Originally created by MinecraftDoodler.
 * Licensed under GPL 3.0
 */
public class ApplicationGitWeb extends Application
{
    public static final Pattern PATTERN_LINK = Pattern.compile("[a-zA-Z\\-]+\\.[a-zA-Z]+");

    private Layout layoutBrowser;
    private Layout layoutPref;

    private Button btnSearch;
    private Button btnHome;
    private Button btnSettings;

    private TextField textFieldAddress;
    private Spinner spinnerLoading;
    private TextLayout scrollable;

    @Override
    public void init()
    {
        layoutBrowser = new StandardLayout("GitWeb", 362, 240, this, null);
        layoutBrowser.setBackground((gui, mc, x, y, width, height, mouseX, mouseY, windowActive) ->
        {
            Color color = new Color(Laptop.getSystem().getSettings().getColorScheme().getItemBackgroundColor());
            Gui.drawRect(x, y + 21, x + width, y + 164, Color.GRAY.getRGB());
        });

        layoutPref = new Layout(200, 120);

        textFieldAddress = new TextField(2, 2, 304);
        textFieldAddress.setPlaceholder("Enter Address");
        layoutBrowser.addComponent(textFieldAddress);

        spinnerLoading = new Spinner(291, 4);
        spinnerLoading.setVisible(false);
        layoutBrowser.addComponent(spinnerLoading);

        btnSearch = new Button(308, 2, 16, 16, Icons.ARROW_RIGHT);
        btnSearch.setToolTip("Refresh", "Loads the entered address.");
        btnSearch.setClickListener((mouseX, mouseY, mouseButton) -> this.loadPasteBin(this.getAddress(), false));
        layoutBrowser.addComponent(btnSearch);

        btnHome = new Button(326, 2, 16, 16, Icons.HOME);
        btnHome.setToolTip("Home", "Loads page set in settings.");
        btnHome.setClickListener((mouseX, mouseY, mouseButton) -> this.loadLink("welcome.official", false));
        layoutBrowser.addComponent(btnHome);

        btnSettings = new Button(344, 2, 16, 16, Icons.WRENCH);
        btnSettings.setToolTip("Settings", "Change your preferences.");
        btnSettings.setClickListener((mouseX, mouseY, mouseButton) -> this.setCurrentLayout(layoutPref));
        layoutBrowser.addComponent(btnSettings);

        Text textAreaSiteView = new Text("", 0, 0, 352);
        textAreaSiteView.setWordListener((word, mouseButton) ->
        {
            if(mouseButton == 0 && PATTERN_LINK.matcher(word).matches())
            {
                this.loadLink(word, false);
            }
        });
        //textAreaSiteView.setPlaceholder("If you can see this page it either means you entered an address with no text or your not connected to the internet.\n\n  Gitweb can be accessed via an address like pickaxes.info, the word after the dot denotes the folder within the root directory and the first word identifies the filename of the site within said folder. \n \n  You can also access pastebin files by entering pastebin:PASTE_ID, this feature was added just to add an option for users to experiement with ideas and test the markup. \n \n  Remember in order to function correctly GitWeb and Minecraft itself need access to the internet.");

        scrollable = new TextLayout(5, 25, 135, textAreaSiteView);
        layoutBrowser.addComponent(scrollable);

        this.loadLink("welcome.official", false);
        this.setCurrentLayout(layoutBrowser);
    }

    //Paste bin then moves on to GitWeb
    void loadPasteBin(String address, Boolean masked)
    {
        textFieldAddress.setFocused(false);
        if(!masked)
        {
            textFieldAddress.setText(address);
        }
        if(address.startsWith("pastebin:") || address.startsWith("rawpastebin:") || address.startsWith("rawpaste:"))
        {
            Confirmation pasteBinConfirm = new Confirmation("Are you sure... Pastebins are not moderated by the §aGitWeb§r team!");
            pasteBinConfirm.setTitle("Load Pastebin!");
            pasteBinConfirm.setPositiveListener((mouseX1, mouseY1, mouseButton1) ->
            {
                this.makeOnlineRequest("https://pastebin.com/raw/" + address.replace("paste", "").replace("raw", "").replace("bin", "").replace(":", "") + "/");
            });
            pasteBinConfirm.setNegativeListener((mouseX1, mouseY1, mouseButton1) ->
            {
                this.setContent("This file did not get permission to load!");
            });
            this.openDialog(pasteBinConfirm);
        }
        else loadLink(address, masked);
    }

    //Attempt to load 'GitWeb' link.
    void loadLink(String address, Boolean masked)
    {
        if(address.contains("."))
        {
            if(!masked)
            {
                textFieldAddress.setText(address);
            }
            textFieldAddress.setFocused(false);
            String[] urlA = address.split("\\.", -1);
            if(!address.contains("/"))
            {
                this.makeOnlineRequest("https://raw.githubusercontent.com/MrCrayfish/GitWeb-Sites/master/" + urlA[1] + "/" + urlA[0]);
            }
            else if(address.contains("/"))
            {
                String[] urlB = urlA[1].split("/", -1);
                this.makeOnlineRequest("https://raw.githubusercontent.com/MrCrayfish/GitWeb-Sites/master/" + urlB[0] + "/" + urlA[0] + "-sub/" + urlB[1]);
            }
        }
        else
        {
            this.setContent("That address doesn't look right");
        }
    }

    //Makes online Request with redirects and other stuff!
    void makeOnlineRequest(String URL)
    {
        spinnerLoading.setVisible(true);
        textFieldAddress.setFocused(false);
        textFieldAddress.setEditable(false);
        btnSearch.setEnabled(false);
        OnlineRequest.getInstance().make(URL, (success, response) ->
        {
            //Redirects to another GitWeb site!
            //pastebin's can not be redirected to.
            if(response.startsWith("redirect>"))
            {
                String reD = response;
                reD = reD.substring(reD.indexOf(">") + 1);
                reD = reD.substring(0, reD.indexOf("<"));
                this.loadLink(reD, false);
                return;
            }
            //Masked redirect to another site! (Keeps redirecting sites address in textFieldAddress)
            if(response.startsWith("masked_redirect>"))
            {
                String reD = response;
                reD = reD.substring(reD.indexOf(">") + 1);
                reD = reD.substring(0, reD.indexOf("<"));
                this.loadLink(reD, true);
                return;
            }
            this.setContent(response);
            spinnerLoading.setVisible(false);
            textFieldAddress.setEditable(true);
            btnSearch.setEnabled(true);
        });
    }

    //check for enter key
    @Override
    public void handleKeyTyped(char character, int code)
    {
        super.handleKeyTyped(character, code);
        if(code == Keyboard.KEY_RETURN)
        {
            loadPasteBin(this.getAddress(), false);
        }
    }

    private String getAddress()
    {
        return textFieldAddress.getText().replace("\\s+", "");
    }

    private void setContent(String text)
    {
        Text textContent = new Text(text, 0, 0, 355);
        textContent.setWordListener((word, mouseButton) ->
        {
            if(mouseButton == 0 && PATTERN_LINK.matcher(word).matches())
            {
                this.loadLink(word, false);
            }
        });
        scrollable.setText(textContent);
    }

    @Override
    public void load(NBTTagCompound tag) {}

    @Override
    public void save(NBTTagCompound tag) {}
}
