package com.mrcrayfish.device.programs.gitweb;

import akka.japi.pf.Match;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The Device Mod implementations of an internet layoutBrowser. Originally created by MinecraftDoodler.
 * Licensed under GPL 3.0
 */
public class ApplicationGitWeb extends Application
{
    public static final Pattern PATTERN_LINK = Pattern.compile("(?<domain>[a-zA-Z\\-]+)\\.(?<extension>[a-zA-Z]+)(?<directory>(/[a-zA-Z\\-]+)*)(/)?");

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
        scrollable = new TextLayout(5, 25, 135, textAreaSiteView);
        layoutBrowser.addComponent(scrollable);

        this.loadLink("welcome.official", false);
        this.setCurrentLayout(layoutBrowser);
    }

    /**
     *
     * @param address
     * @param masked
     */
    private void loadPasteBin(String address, Boolean masked)
    {
        textFieldAddress.setFocused(false);
        if(!masked)
        {
            textFieldAddress.setText(address);
        }
        if(address.startsWith("pastebin:") || address.startsWith("rawpastebin:") || address.startsWith("rawpaste:"))
        {
            Confirmation pasteBinConfirm = new Confirmation("Pastebins are not moderated by the §aGitWeb§r team. Are you sure you want to continue loading?");
            pasteBinConfirm.setTitle("Load Pastebin!");
            pasteBinConfirm.setPositiveListener((mouseX1, mouseY1, mouseButton1) -> this.makeOnlineRequest("https://pastebin.com/raw/" + address.replace("paste", "").replace("raw", "").replace("bin", "").replace(":", "") + "/"));
            pasteBinConfirm.setNegativeListener((mouseX1, mouseY1, mouseButton1) -> this.setContent("This file did not get permission to load!"));
            this.openDialog(pasteBinConfirm);
        }
        else
        {
            this.loadLink(address, masked);
        }
    }

    /**
     *
     * @param address
     * @param masked
     */
    private void loadLink(String address, Boolean masked)
    {
        Matcher matcher = PATTERN_LINK.matcher(address);
        if(!matcher.matches())
        {
            this.setContent("That address doesn't look right");
            return;
        }

        String domain = matcher.group("domain");
        String extension = matcher.group("extension");
        String directory = matcher.group("directory");

        if(!masked)
        {
            textFieldAddress.setText(address);
        }
        textFieldAddress.setFocused(false);

        if(directory == null)
        {
            this.makeOnlineRequest("https://raw.githubusercontent.com/MrCrayfish/GitWeb-Sites/master/" + extension + "/" + domain + "/index");
        }
        else
        {
            if(directory.endsWith("/"))
            {
                directory = directory.substring(0, directory.length() - 1);
            }
            this.makeOnlineRequest("https://raw.githubusercontent.com/MrCrayfish/GitWeb-Sites/master/" + extension + "/" + domain + directory + "/index");
        }
    }

    /**
     *
     * @param URL
     */
    private void makeOnlineRequest(String URL)
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

    @Override
    public void handleKeyTyped(char character, int code)
    {
        super.handleKeyTyped(character, code);
        if(code == Keyboard.KEY_RETURN)
        {
            loadPasteBin(this.getAddress(), false);
        }
    }

    /**
     *
     * @return
     */
    private String getAddress()
    {
        return textFieldAddress.getText().replace("\\s+", "");
    }

    /**
     *
     * @param text
     */
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
