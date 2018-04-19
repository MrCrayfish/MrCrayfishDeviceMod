package com.mrcrayfish.device.programs.gitweb.component;

import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.Layout;
import com.mrcrayfish.device.api.app.ScrollableLayout;
import com.mrcrayfish.device.api.app.component.Text;
import com.mrcrayfish.device.api.task.Callback;
import com.mrcrayfish.device.api.utils.OnlineRequest;
import com.mrcrayfish.device.programs.gitweb.module.*;

import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Author: MrCrayfish
 */
public class GitWebFrame extends Component
{
    public static final Pattern PATTERN_LINK = Pattern.compile("(?<domain>[a-zA-Z\\-]+)\\.(?<extension>[a-zA-Z]+)(?<directory>(/[a-zA-Z\\-]+)*)(/)?");
    private static final Map<String, Module> MODULES = new HashMap<>();

    static
    {
        MODULES.put("header", new HeaderModule());
        MODULES.put("text", new TextModule());
        MODULES.put("divider", new DividerModule());
        MODULES.put("banner", new BannerModule());
        MODULES.put("crafting", new CraftingModule());
        MODULES.put("download", new DownloadModule());
    }

    private Application app;
    private ScrollableLayout layout;
    private int width;
    private int height;

    private boolean initialized = false;
    private String currentWebsite;
    private String pendingWebsite;
    private String pendingUrl;

    private Callback<String> loadingCallback;
    private Callback<String> loadedCallback;

    public GitWebFrame(Application app, int left, int top, int width, int height)
    {
        super(left, top);
        this.app = app;
        this.width = width;
        this.height = height;
        this.layout = new ScrollableLayout(left, top, width, height, height);
        this.layout.setScrollSpeed(8);
    }

    @Override
    protected void init(Layout layout)
    {
       layout.addComponent(this.layout);
    }

    @Override
    protected void handleLoad()
    {
        this.initialized = true;
        if(pendingUrl != null)
        {
            this.loadUrl(pendingUrl);
            pendingUrl = null;
        }
        else if(pendingWebsite != null)
        {
            this.setWebsite(pendingWebsite);
            pendingWebsite = null;
        }
    }

    @Override
    protected void handleUnload()
    {
        this.initialized = false;
    }

    @Override
    protected void handleTick()
    {
        if(pendingWebsite != null)
        {
            this.setWebsite(pendingWebsite);
            pendingWebsite = null;
        }
    }

    public void loadRaw(String data)
    {
        layout.clear();
        generateLayout(data, false);
    }

    public void loadWebsite(String website)
    {
        pendingWebsite = website;
    }

    public void loadUrl(String url)
    {
        pendingUrl = url;
    }

    private void setWebsite(String website)
    {
        layout.clear();

        Matcher matcher = GitWebFrame.PATTERN_LINK.matcher(website);
        if(!matcher.matches())
        {
            if(loadedCallback != null)
            {
                loadedCallback.execute(null, false);
            }
            return;
        }

        currentWebsite = website;

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

    private void setUrl(String url)
    {
        layout.clear();

        try
        {
            new URL(url).toURI();
        }
        catch(Exception e)
        {
            if(loadedCallback != null)
            {
                loadedCallback.execute(null, false);
            }
            return;
        }

        currentWebsite = url;

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
                generateLayout(response, true);
            }
            if(loadedCallback != null)
            {
                loadedCallback.execute(response, success);
            }
        });
    }

    public String getCurrentWebsite()
    {
        return currentWebsite;
    }

    private void generateLayout(String websiteData, boolean dynamic)
    {
        List<ModuleEntry> modules = parseData(websiteData);
        if(modules == null)
        {
            //DISPLAY DIALOG?
            return;
        }

        layout.clear();
        layout.height = calculateHeight(modules, width);

        int offset = 0;
        for(int i = 0; i < modules.size() - 1; i++)
        {
            ModuleEntry entry = modules.get(i);
            Module module = entry.getModule();
            int height = module.calculateHeight(entry.getData(), width);
            Layout moduleLayout = new Layout(0, offset, width, height);
            module.generate(this, moduleLayout, width, entry.getData());
            layout.addComponent(moduleLayout);
            offset += height;
        }

        if(modules.size() > 0)
        {
            ModuleEntry entry = modules.get(modules.size() - 1);
            Module module = entry.getModule();
            int height = module.calculateHeight(entry.getData(), width);
            Layout moduleLayout = new Layout(0, offset, width, height);
            module.generate(this, moduleLayout, width, entry.getData());
            layout.addComponent(moduleLayout);
        }

        if(dynamic || initialized)
        {
            layout.handleLoad();
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
        addWordListener(layout, listener);
    }

    private void addWordListener(Layout layout, Text.WordListener listener)
    {
        for(Component c : layout.components)
        {
            if(c instanceof Layout)
            {
                addWordListener((Layout) c, listener);
            }
            else if(c instanceof Text)
            {
                ((Text) c).setWordListener(listener);
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
                String[] data = line.split("=", 2);
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
        int height = 0;
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

    public Application getApp()
    {
        return app;
    }
}
