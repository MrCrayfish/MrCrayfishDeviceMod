package com.mrcrayfish.device.programs.debug;

import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.Icons;
import com.mrcrayfish.device.api.app.Layout;
import com.mrcrayfish.device.api.app.component.ButtonToggle;
import com.mrcrayfish.device.api.app.component.TextArea;
import com.mrcrayfish.device.api.app.interfaces.IHighlight;
import com.mrcrayfish.device.api.app.listener.ClickListener;
import com.mrcrayfish.device.core.Laptop;
import com.mrcrayfish.device.core.client.LaptopFontRenderer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;

import java.util.Arrays;

/**
 * Author: MrCrayfish
 */
public class ApplicationTextArea extends Application
{
    public static final IHighlight JAVA_HIGHLIGHT = text -> 
    {
        if(text.startsWith("@"))
            return asArray(TextFormatting.YELLOW);

        if(text.startsWith("\"") && text.endsWith("\""))
            return asArray(TextFormatting.AQUA);

        switch(text)
        {
            case "abstract":	
            case "continue":	
            case "for":	
            case "new":	
            case "switch":
            case "assert":
            case "default":
            case "goto":
            case "package":	
            case "synchronized":
            case "boolean":	
            case "do":	
            case "if":	
            case "private":	
            case "this":
            case "break":	
            case "double":	
            case "implements":	
            case "protected":	
            case "throw":
            case "byte":	
            case "else":	
            case "import":	
            case "public":	
            case "throws":
            case "case":	
            case "enum":	
            case "instanceof":	
            case "return":	
            case "transient":
            case "catch":	
            case "extends":	
            case "int":	
            case "short":	
            case "try":
            case "char":	
            case "final":	
            case "interface":	
            case "static":	
            case "void":
            case "class":	
            case "finally":	
            case "long":	
            case "strictfp":	
            case "volatile":
            case "const":	
            case "float":	
            case "native":	
            case "super":	
            case "while":
            case "null":
                return asArray(TextFormatting.BLUE);
            default:
                return asArray(TextFormatting.WHITE);
        }
    };
    @Override
    public void init()
    {
        Layout layout = new Layout(250, 150);

        TextArea textArea = new TextArea(5, 25, 240, 120);
        textArea.setScrollBarSize(5);
        layout.addComponent(textArea);

        ButtonToggle buttonWordWrap = new ButtonToggle(5, 5, Icons.ALIGN_JUSTIFY);
        buttonWordWrap.setToolTip("Word Wrap", "Break the lines to fit in the view");
        buttonWordWrap.setClickListener((c, mouseButton) ->
        {
            if(mouseButton == 0)
            {
                textArea.setWrapText(!buttonWordWrap.isSelected());
            }
        });
        layout.addComponent(buttonWordWrap);

        ButtonToggle buttonDebug = new ButtonToggle(24, 5, Icons.HELP);
        buttonDebug.setToolTip("Debug Mode", "Show invisible characters");
        buttonDebug.setClickListener((c, mouseButton) ->
        {
            if(mouseButton == 0)
            {
                ((LaptopFontRenderer)Laptop.fontRenderer).setDebug(!buttonDebug.isSelected());
            }
        });
        layout.addComponent(buttonDebug);

        ButtonToggle buttonHighlight = new ButtonToggle(43, 5, Icons.FONT);
        buttonHighlight.setToolTip("Highlight", "Set text highlighting to Java");
        buttonHighlight.setClickListener((c, mouseButton) ->
        {
            if(mouseButton == 0)
            {
                if(!buttonHighlight.isSelected())
                {
                    textArea.setHighlight(JAVA_HIGHLIGHT);
                }
                else
                {
                    textArea.setHighlight(null);
                }
            }
        });
        layout.addComponent(buttonHighlight);

        setCurrentLayout(layout);
    }

    @Override
    public void load(NBTTagCompound tagCompound)
    {

    }

    @Override
    public void save(NBTTagCompound tagCompound)
    {

    }

    private static <T extends Object> T[] asArray(T ... t)
    {
        return t;
    }
}
