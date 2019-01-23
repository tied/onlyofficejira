package ru.hlynov.oit.impl;

import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
//import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import ru.hlynov.oit.api.PluginSettingsService;


import javax.inject.Inject;
import javax.inject.Named;

@ExportAsService ({PluginSettingsService.class})
@Named ("pluginSettingsCmp")
public class PluginSettingsServiceImpl implements PluginSettingsService
{

    private final PluginSettings pluginSettings;
    private static final String PLUGIN_STORAGE_KEY = "ru.hlynov.oit.";
    private static final String CONFIG_KEY = "onlyofficejira";

    @Inject
    public PluginSettingsServiceImpl(@ComponentImport PluginSettingsFactory pluginSettingsFactory)
    {
        this.pluginSettings = pluginSettingsFactory.createGlobalSettings();
    }

    private String getSettingValue(String settingKey) {
        if (settingKey == null) {
            return "";
        } else {
            return (String) pluginSettings.get(PLUGIN_STORAGE_KEY + settingKey);
        }
    }

    private void setSettingValue(String settingKey, String settingValue) {
        if (settingKey == null)
            return;

        if (settingValue == null) {
            this.pluginSettings.put(PLUGIN_STORAGE_KEY + settingKey,"");
        } else {
            this.pluginSettings.put(PLUGIN_STORAGE_KEY + settingKey, settingValue);
        }
    }

    @Override
    public String getConfigJson() {
        return getSettingValue(CONFIG_KEY);
    }

    @Override
    public void setConfigJson(String json) {
        setSettingValue(CONFIG_KEY, json);
    }
}