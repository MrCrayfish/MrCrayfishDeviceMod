package com.mrcrayfish.device.programs.gitweb.component;

import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.Layout;
import com.mrcrayfish.device.api.app.ScrollableLayout;
import com.mrcrayfish.device.api.app.component.Text;
import com.mrcrayfish.device.api.task.Callback;
import com.mrcrayfish.device.api.utils.OnlineRequest;
import com.mrcrayfish.device.programs.gitweb.module.Module;
import com.mrcrayfish.device.programs.gitweb.module.ModuleEntry;
import com.mrcrayfish.device.programs.gitweb.module.TextModule;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Author: MrCrayfish
 */
public class GitWebView extends Component
{
    public static final Pattern PATTERN_LINK = Pattern.compile("(?<domain>[a-zA-Z\\-]+)\\.(?<extension>[a-zA-Z]+)(?<directory>(/[a-zA-Z\\-]+)*)(/)?");
    private static final Map<String, Module> MODULES = new HashMap<>();

    static
    {
        MODULES.put("text", new TextModule());
    }
    private ScrollableLayout layout;
    private int width;
    private int height;

    private String pendingWebsite;

    private Callback<String> loadingCallback;
    private Callback<String> loadedCallback;

    public GitWebView(int left, int top, int width, int height)
    {
        super(left, top);
        this.width = width;
        this.height = height;
        this.layout = new ScrollableLayout(left, top, width, height, height);
    }

    @Override
    protected void init(Layout layout)
    {
       layout.addComponent(this.layout);
    }

    @Override
    protected void handleTick()
    {
        if(pendingWebsite != null)
        {
            this.loadWebsite(pendingWebsite);
            pendingWebsite = null;
        }
    }

    public void loadRaw(String data)
    {
        layout.clear();
        generateLayout(data);
    }

    public void loadWebsite(String website)
    {
        layout.clear();

        Matcher matcher = GitWebView.PATTERN_LINK.matcher(website);
        if(!matcher.matches())
        {
            this.loadRaw("That address doesn't look right");
            return;
        }

        String domain = matcher.group("domain");
        String extension = matcher.group("extension");
        String directory = matcher.group("directory");
        String url;

        if(directory == null)
        {
            url = "https://raw.githubusercontent.com/MrCrayfish/GitWeb-Sites/master/" + extension + "/" + domain + "/index";
        }
        else
        {
            if(directory.endsWith("/"))
            {
                directory = directory.substring(0, directory.length() - 1);
            }
            url = "https://raw.githubusercontent.com/MrCrayfish/GitWeb-Sites/master/" + extension + "/" + domain + directory + "/index";
        }

        if(loadingCallback != null)
        {
            loadingCallback.execute(website, true);
        }
        this.load(url);
    }

    public void loadUrl(String url)
    {
        if(loadingCallback != null)
        {
            loadingCallback.execute(url, true);
        }
        this.load(url);
    }

    private void load(String url)
    {
        OnlineRequest.getInstance().make(url, (success, response) ->
        {
            if(success)
            {
                generateLayout(response);
            }
            if(loadedCallback != null)
            {
                loadedCallback.execute(response, success);
            }
        });
    }

    private void generateLayout(String websiteData)
    {
        List<ModuleEntry> modules = parseData(websiteData);
        if(modules == null)
        {
            //DISPLAY DIALOG?
            return;
        }

        layout.height = calculateHeight(modules, width);
        int offset = 0;
        for(int i = 0; i < modules.size() - 1; i++)
        {
            ModuleEntry entry = modules.get(i);
            Module module = entry.getModule();
            int height = module.calculateHeight(entry.getData(), width);
            Layout moduleLayout = new Layout(0, offset, width, height);
            module.generate(moduleLayout, entry.getData(), width);
            layout.addComponent(moduleLayout);
            offset += height + 5;
        }

        if(modules.size() > 0)
        {
            ModuleEntry entry = modules.get(modules.size() - 1);
            Module module = entry.getModule();
            int height = module.calculateHeight(entry.getData(), width);
            Layout moduleLayout = new Layout(0, offset, width, height);
            module.generate(moduleLayout, entry.getData(), width);
            layout.addComponent(moduleLayout);
        }

        layout.resetScroll();
        updateListeners();
    }

    private void updateListeners()
    {
        Text.WordListener listener = (word, mouseButton) ->
        {
            if(mouseButton == 0 && PATTERN_LINK.matcher(word).matches())
            {
                this.pendingWebsite = word;
            }
        };
        for(Component c1 : layout.components)
        {
            if(c1 instanceof Layout)
            {
                Layout layout = (Layout) c1;
                for(Component c2 : layout.components)
                {
                    if(c2 instanceof Text)
                    {
                        ((Text) c2).setWordListener(listener);
                    }
                }
            }
        }
    }

    private static List<ModuleEntry> parseData(String websiteData)
    {
        websiteData = websiteData.replace("\r", "");

        List<ModuleEntry> modules = new LinkedList<>();
        String[] lines = websiteData.trim().split("\\n");

        Module module = null;
        Map<String, String> moduleData = null;
        for(String line : lines)
        {
            if(line.isEmpty())
                continue;

            if(line.startsWith("#"))
            {
                ModuleEntry entry = compileEntry(module, moduleData);
                if(entry != null)
                {
                    modules.add(entry);
                }
                module = MODULES.get(line.substring(1));
                moduleData = new HashMap<>();
            }
            else if(module != null)
            {
                String[] data = line.split("=");
                if(data.length != 2)
                    return null;
                moduleData.put(data[0], data[1]);
            }
            else
            {
                modules.add(createPlainWebsite(websiteData));
                return modules;
            }
        }

        ModuleEntry entry = compileEntry(module, moduleData);
        if(entry != null)
        {
            modules.add(entry);
        }

        return modules;
    }

    private static ModuleEntry compileEntry(Module module, Map<String, String> data)
    {
        if(module != null && verifyModuleEntry(module, data))
        {
            return new ModuleEntry(module, data);
        }
        return null;
    }

    private static ModuleEntry createPlainWebsite(String content)
    {
        Module module = MODULES.get("text");
        Map<String, String> data = new HashMap<>(1, 1.0F);
        data.put("text", content);
        return new ModuleEntry(module, data);
    }

    private static boolean verifyModuleEntry(Module module, Map<String, String> data)
    {
        String[] requiredData = module.getRequiredData();
        for(String s : requiredData)
        {
            if(!data.containsKey(s))
            {
                return false;
            }
        }
        return true;
    }

    private static int calculateHeight(List<ModuleEntry> modules, int width)
    {
        int height = (modules.size() - 1) * 5;
        for(int i = 0; i < modules.size(); i++)
        {
            ModuleEntry entry = modules.get(i);
            height += entry.getModule().calculateHeight(entry.getData(), width);
        }
        return height;
    }

    public void setLoadingCallback(Callback<String> loadingCallback)
    {
        this.loadingCallback = loadingCallback;
    }

    public void setLoadedCallback(Callback<String> loadedCallback)
    {
        this.loadedCallback = loadedCallback;
    }
}
