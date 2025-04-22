package com.csdy.jzyy.coremod.csdycore;



import com.csdy.jzyy.util.Helper;
import cpw.mods.modlauncher.LaunchPluginHandler;
import cpw.mods.modlauncher.Launcher;
import cpw.mods.modlauncher.api.IEnvironment;
import cpw.mods.modlauncher.api.ITransformationService;
import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.serviceapi.ILaunchPluginService;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CsdyTransformationService implements ITransformationService {
    static {
        LaunchPluginHandler handler = Helper.getFieldValue(Launcher.INSTANCE, "launchPlugins", LaunchPluginHandler.class);
        Map<String, ILaunchPluginService> plugins = (Map<String, ILaunchPluginService>) Helper.getFieldValue(handler, "plugins", Map.class);
        Map<String, ILaunchPluginService> newMap = new HashMap<>();
        newMap.put("!Csdy", new CsdyLaunchPluginService());
        if (plugins != null) for (String name : plugins.keySet())
            newMap.put(name, plugins.get(name));
        Helper.setFieldValue(handler, "plugins", newMap);
        Helper.coexistenceCoreAndMod();
    }

    @Override
    public @NotNull String name() {
        return "Csdy Jzyy TransformationService";
    }

    @Override
    public void initialize(IEnvironment environment) {
    }

    @Override
    public void onLoad(IEnvironment env, Set<String> otherServices) {
    }

    @Override
    public @NotNull List<ITransformer> transformers() {
        return List.of();
    }
}