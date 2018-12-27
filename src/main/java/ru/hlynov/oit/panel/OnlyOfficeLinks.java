package ru.hlynov.oit.panel;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.properties.APKeys;
import com.atlassian.jira.issue.AttachmentManager;
import com.atlassian.jira.config.util.AttachmentPathManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.plugin.webfragment.contextproviders.AbstractJiraContextProvider;
import com.atlassian.jira.plugin.webfragment.model.JiraHelper;
import com.atlassian.jira.user.ApplicationUser;
//import com.atlassian.plugin.web.model.WebPanel;
import com.atlassian.jira.issue.attachment.Attachment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.*;

import static java.time.temporal.ChronoUnit.DAYS;

public class OnlyOfficeLinks extends AbstractJiraContextProvider {

    private static final Logger log = LoggerFactory.getLogger(OnlyOfficeLinks.class);

    @Override
    public Map getContextMap(ApplicationUser applicationUser, JiraHelper jiraHelper) {
        Map<String, Object> contextMap = new HashMap<>();
        List<AttachmentParams> attachmentParamsList = new ArrayList<AttachmentParams>();

        Issue currentIssue = (Issue) jiraHelper.getContextParams().get("issue");

//        AttachmentManager attachmentManager = ComponentAccessor.getAttachmentManager();
//        AttachmentPathManager attachmentPathManager = ComponentAccessor.getAttachmentPathManager(); //getDefaultAttachmentPath()

        Collection<Attachment> attachments = currentIssue.getAttachments();

        for (Attachment oneAttach : attachments) {
            AttachmentParams attachmentParams = new AttachmentParams(
                    oneAttach.getFilename(),
                    oneAttach.getFilesize(),
                    oneAttach.getAuthorObject().getDisplayName(),
                    oneAttach.getCreated(),
                    oneAttach.getId()

            );
            attachmentParamsList.add(attachmentParams);
        }


        contextMap.put("attachments", attachmentParamsList);
        contextMap.put("issueid", currentIssue.getId());
        contextMap.put("jirabaseurl", ComponentAccessor.getApplicationProperties().getString(APKeys.JIRA_BASEURL));

        contextMap.put("projectId", currentIssue.getProjectId());
        contextMap.put("issueId", currentIssue.getId());


        return contextMap;
    }
}
