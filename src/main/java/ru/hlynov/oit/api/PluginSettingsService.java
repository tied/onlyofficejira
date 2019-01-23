package ru.hlynov.oit.api;

public interface PluginSettingsService
{
    String getConfigJson();
    void setConfigJson(String json);
}