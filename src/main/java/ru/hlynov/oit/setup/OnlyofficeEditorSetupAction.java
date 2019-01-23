package ru.hlynov.oit.setup;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import ru.hlynov.oit.api.PluginSettingsService;

import javax.inject.Inject;


public class OnlyofficeEditorSetupAction extends JiraWebActionSupport
{
    private static final Logger log = LoggerFactory.getLogger(OnlyofficeEditorSetupAction.class);

    private final PluginSettingsService pluginSettingsService;
    private String serverName;

    @Inject
    public OnlyofficeEditorSetupAction(PluginSettingsService pluginSettingsService) {
        this.pluginSettingsService = pluginSettingsService;
    }

    @Override
    public String execute() throws Exception {

        super.execute(); //returns SUCCESS
        return SUCCESS;
    }

    public String doDefault() throws Exception {
        String cfg = pluginSettingsService.getConfigJson();

        this.serverName = "";

        if (cfg == null) {
            return SUCCESS;
        }

        if (cfg.isEmpty()) {
            return SUCCESS;
        }

        JsonParser parser = new JsonParser();
        JsonObject cfgObj = parser.parse(cfg).getAsJsonObject();

        String currVar = cfgObj.get("servername").getAsString();
        if (currVar != null) {
            this.serverName = currVar;
        }

        return SUCCESS;
    }


    public String doSave() throws Exception {

        String paramServerName;

        JsonObject params = new JsonObject();

        paramServerName = request.getParameter("servername");
        if (paramServerName != null) {
            params.addProperty("servername", paramServerName);
        } else {
            params.addProperty("servername", "");
        }

        params.addProperty("servername", paramServerName);

        pluginSettingsService.setConfigJson(params.toString());

        this.serverName = paramServerName;

        return SUCCESS;

    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }
}
