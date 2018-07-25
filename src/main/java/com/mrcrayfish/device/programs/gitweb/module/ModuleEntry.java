package com.mrcrayfish.device.programs.gitweb.module;

import java.util.Map;

/**
 * Author: MrCrayfish
 */
public class ModuleEntry
{
    private Module module;
    private Map<String, String> data;

    public ModuleEntry(Module module, Map<String, String> data)
    {
        this.module = module;
        this.data = data;
    }

    public Module getModule()
    {
        return module;
    }

    public Map<String, String> getData()
    {
        return data;
    }
}
