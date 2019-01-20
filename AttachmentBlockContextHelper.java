package com.atlassian.jira.plugin.viewissue;

import com.atlassian.core.AtlassianCoreException;
import com.atlassian.jira.config.properties.APKeys;
import com.atlassian.jira.config.properties.ApplicationProperties;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.attachment.Attachment;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.security.PermissionManager;
import com.atlassian.jira.security.Permissions;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.preferences.ExtendedPreferences;
import com.atlassian.jira.user.preferences.UserPreferencesManager;
import com.atlassian.jira.util.velocity.VelocityRequestContext;
import com.atlassian.jira.util.velocity.VelocityRequestContextFactory;
import com.atlassian.jira.util.velocity.VelocityRequestSession;
import com.atlassian.jira.web.SessionKeys;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AttachmentBlockContextHelper {
    private static final Logger log = LoggerFactory.getLogger(AttachmentBlockContextHelper.class);

    static final String ORDER_DESC = "desc";
    static final String DEFAULT_ISSUE_ATTACHMENTS_ORDER = "asc";
    static final String SORTBY_DATE_TIME = "dateTime";
    static final String DEFAULT_ISSUE_ATTACHMENTS_SORTBY = "fileName";
    static final String VIEWMODE_LIST = "list";
    static final String DEFAULT_ISSUE_ATTACHMENTS_VIEWMODE = "gallery";

    private final VelocityRequestContextFactory velocityRequestContextFactory;
    private final ApplicationProperties applicationProperties;
    private final IssueManager issueManager;
    private final PermissionManager permissionManager;
    private final UserPreferencesManager userPreferencesManager;
    private final JiraAuthenticationContext jiraAuthenticationContext;

    public AttachmentBlockContextHelper(VelocityRequestContextFactory velocityRequestContextFactory, ApplicationProperties applicationProperties,
                                        IssueManager issueManager, PermissionManager permissionManager, final UserPreferencesManager userPreferencesManager, final JiraAuthenticationContext jiraAuthenticationContext) {
        this.velocityRequestContextFactory = velocityRequestContextFactory;
        this.applicationProperties = applicationProperties;
        this.issueManager = issueManager;
        this.permissionManager = permissionManager;
        this.userPreferencesManager = userPreferencesManager;
        this.jiraAuthenticationContext = jiraAuthenticationContext;
    }

    public String getAttachmentOrder() {
        return getSessionBackedRequestParam("attachmentOrder", DEFAULT_ISSUE_ATTACHMENTS_ORDER, SessionKeys.VIEWISSUE_ATTACHMENT_ORDER);
    }

    public String getAttachmentSortBy() {
        return getSessionBackedRequestParam("attachmentSortBy", DEFAULT_ISSUE_ATTACHMENTS_SORTBY, SessionKeys.VIEWISSUE_ATTACHMENT_SORTBY);
    }

    public String getAttachmentViewMode() {
        if (applicationProperties.getOption(APKeys.JIRA_OPTION_ALLOWTHUMBNAILS)) {
            return getPreferencesBackedRequestParam("attachmentViewMode", DEFAULT_ISSUE_ATTACHMENTS_VIEWMODE, SessionKeys.VIEWISSUE_ATTACHMENT_VIEWMODE);
        } else {
            return VIEWMODE_LIST;
        }
    }

    private String getSessionBackedRequestParam(String requestParamName, String defaultValue, String sessionKey) {
        final VelocityRequestContext requestContext = velocityRequestContextFactory.getJiraVelocityRequestContext();
        final VelocityRequestSession session = requestContext.getSession();

        return getBackedRequestParam(requestParamName, defaultValue, sessionKey, new SessionParamProvider(session));
    }

    private String getPreferencesBackedRequestParam(String requestParamName, String defaultValue, String sessionKey) {
        if (jiraAuthenticationContext.isLoggedInUser()) {
            final ExtendedPreferences extendedPreferences = userPreferencesManager.getExtendedPreferences(jiraAuthenticationContext.getUser());
            return getBackedRequestParam(requestParamName, defaultValue, sessionKey, new PreferencesParamProvider(extendedPreferences));
        } else {
            return getSessionBackedRequestParam(requestParamName, defaultValue, sessionKey);
        }
    }

    private String getBackedRequestParam(String requestParamName, String defaultValue, String sessionKey, ParamProvider provider) {
        final VelocityRequestContext requestContext = velocityRequestContextFactory.getJiraVelocityRequestContext();

        final String requestParameter = requestContext.getRequestParameter(requestParamName);
        if (StringUtils.isNotBlank(requestParameter)) {
            if (requestParameter.equals(defaultValue)) {
                provider.removeAttribute(sessionKey);
                return defaultValue;
            } else {
                provider.setAttribute(sessionKey, requestParameter);
                return requestParameter;
            }
        }

        final String providerValue = provider.getAttribute(sessionKey);
        return StringUtils.isNotBlank(providerValue) ? providerValue : defaultValue;
    }

    boolean getZipSupport() {
        return applicationProperties.getOption(APKeys.JIRA_OPTION_ALLOW_ZIP_SUPPORT);
    }

    int getMaximumNumberOfZipEntriesToShow() {
        String maximumNumberOfZipEntriesToShowAsString = applicationProperties.getDefaultBackedString(APKeys.JIRA_ATTACHMENT_NUMBER_OF_ZIP_ENTRIES_TO_SHOW);
        int maximumNumberOfZipEntriesToShow = 30;
        try {
            maximumNumberOfZipEntriesToShow = Integer.parseInt(maximumNumberOfZipEntriesToShowAsString);
        } catch (NumberFormatException e) {
            //Ignoring error, we'll use the default of 30
        }
        return maximumNumberOfZipEntriesToShow;
    }

    boolean canDeleteAttachment(Issue issue, Attachment attachment, ApplicationUser user) {
        return issueManager.isEditable(issue)
                && (permissionManager.hasPermission(Permissions.ATTACHMENT_DELETE_ALL, issue, user)
                || (permissionManager.hasPermission(Permissions.ATTACHMENT_DELETE_OWN, issue, user) && isUserAttachmentAuthor(attachment, user)));

    }

    private boolean isUserAttachmentAuthor(Attachment attachment, ApplicationUser user) {
        ApplicationUser attachmentAuthor = attachment.getAuthorObject();

        if (attachmentAuthor == null && user == null) {
            return true;
        } else if (attachmentAuthor == null || user == null) {
            return false;
        }

        return attachmentAuthor.equals(user);
    }

    private interface ParamProvider {
        void removeAttribute(String key);

        void setAttribute(String key, String value);

        String getAttribute(String key);
    }

    private static class SessionParamProvider implements AttachmentBlockContextHelper.ParamProvider {
        private final VelocityRequestSession session;

        public SessionParamProvider(final VelocityRequestSession session) {
            this.session = session;
        }

        @Override
        public void removeAttribute(final String key) {
            session.removeAttribute(key);
        }

        @Override
        public void setAttribute(final String key, final String value) {
            session.setAttribute(key, value);
        }

        @Override
        public String getAttribute(final String key) {
            return (String) session.getAttribute(key);
        }
    }

    private static class PreferencesParamProvider implements AttachmentBlockContextHelper.ParamProvider {
        private final ExtendedPreferences extendedPreferences;

        public PreferencesParamProvider(final ExtendedPreferences extendedPreferences) {
            this.extendedPreferences = extendedPreferences;
        }

        @Override
        public void removeAttribute(final String key) {
            try {
                if (extendedPreferences.containsValue(key)) {
                    extendedPreferences.remove(key);
                }
            } catch (AtlassianCoreException e) {
                log.error("Exception occurred while trying to remove property:", e);
            }
        }

        @Override
        public void setAttribute(final String key, final String value) {
            try {
                extendedPreferences.setText(key, value);
            } catch (AtlassianCoreException e) {
                log.error("Exception occurred while trying to set property:", e);
            }
        }

        @Override
        public String getAttribute(final String key) {
            return extendedPreferences.getText(key);
        }
    }
}
