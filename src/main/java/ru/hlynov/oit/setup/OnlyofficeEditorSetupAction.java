package ru.hlynov.oit.setup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.atlassian.jira.web.action.JiraWebActionSupport;

public class OnlyofficeEditorSetupAction extends JiraWebActionSupport
{
    private static final Logger log = LoggerFactory.getLogger(OnlyofficeEditorSetupAction.class);

    @Override
    public String execute() throws Exception {

        super.execute(); //returns SUCCESS
        return SUCCESS;
    }
}
