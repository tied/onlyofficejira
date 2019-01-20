package com.atlassian.jira.plugin.viewissue;

import com.atlassian.jira.issue.AttachmentManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.attachment.Attachment;
import com.atlassian.jira.issue.attachment.AttachmentCreationDateComparator;
import com.atlassian.jira.issue.attachment.AttachmentFileNameCreationDateComparator;
import com.atlassian.jira.issue.fields.rest.json.dto.AttachmentViewDtoConverter;
import com.atlassian.jira.plugin.webfragment.CacheableContextProvider;
import com.atlassian.jira.plugin.webfragment.JiraWebInterfaceManager;
import com.atlassian.jira.plugin.webfragment.model.JiraHelper;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.util.JiraUrlCodec;
import com.atlassian.jira.util.collect.MapBuilder;
import com.atlassian.jira.util.http.JiraUrl;
import com.atlassian.plugin.PluginParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Provides context for the Attachments block on the View Issue page.
 *
 * @since v5.0
 */
public class AttachmentBlockContextProvider implements CacheableContextProvider {
    private static final Logger log = LoggerFactory.getLogger(AttachmentBlockContextProvider.class);

    private final AttachmentManager attachmentManager;
    private final JiraAuthenticationContext authenticationContext;
    private final AttachmentBlockContextHelper helper;
    private final AttachmentViewDtoConverter viewDtoConverter;

    public AttachmentBlockContextProvider(final AttachmentManager attachmentManager, final JiraAuthenticationContext authenticationContext, final AttachmentBlockContextHelper helper, final AttachmentViewDtoConverter viewDtoConverter) {
        this.attachmentManager = attachmentManager;
        this.authenticationContext = authenticationContext;
        this.viewDtoConverter = viewDtoConverter;
        this.helper = helper;
    }

    @Override
    public void init(Map<String, String> params) throws PluginParseException {
    }

    @Override
    public Map<String, Object> getContextMap(Map<String, Object> context) {
        final MapBuilder<String, Object> paramsBuilder = MapBuilder.newBuilder(context);

        final Issue issue = (Issue) context.get("issue");

        List<Attachment> attachments = attachmentManager.getAttachments(issue, attachmentComparator());

        paramsBuilder.add("hasAttachments", !attachments.isEmpty());
        paramsBuilder.add("openSquareBracket", JiraUrlCodec.encode("["));
        paramsBuilder.add("closeSquareBracket", JiraUrlCodec.encode("]"));

        paramsBuilder.add("viewMode", helper.getAttachmentViewMode());
        paramsBuilder.add("sortKey", helper.getAttachmentSortBy());
        paramsBuilder.add("sortOrder", helper.getAttachmentOrder());
        paramsBuilder.add("attachments", viewDtoConverter.convert(attachments));

        paramsBuilder.add("maximumNumberOfZipEntriesToShow", helper.getMaximumNumberOfZipEntriesToShow());
        paramsBuilder.add("fullBaseUrl", JiraUrl.constructBaseUrl(getRequest(context)));

        ///////////////////////////////////////////////////
        // alx - for only office
        ///////////////////////////////////////////////////
        paramsBuilder.add("projectId", issue.getProjectId().intValue());
        paramsBuilder.add("issueIdId", issue.getId().intValue());

//        for (Attachment attOne : attachments) {
//            log.warn(" ===== att " + attOne.getFilename());
//        }
        ///////////////////////////////////////////////////


        return paramsBuilder.toMap();
    }

    /**
     * @return a Comparator&lt;Attachment&gt; according to the user's selection.
     */
    protected Comparator<Attachment> attachmentComparator() {
        final String attachmentSortBy = helper.getAttachmentSortBy();
        final String attachmentOrder = helper.getAttachmentOrder();

        Comparator<Attachment> attachmentComparator;
        if (AttachmentBlockContextHelper.SORTBY_DATE_TIME.equals(attachmentSortBy)) {
            attachmentComparator = new AttachmentCreationDateComparator();
        } else {
            attachmentComparator = new AttachmentFileNameCreationDateComparator(authenticationContext.getLocale());
        }

        if (AttachmentBlockContextHelper.ORDER_DESC.equals(attachmentOrder)) {
            attachmentComparator = Collections.reverseOrder(attachmentComparator);
        }

        return attachmentComparator;
    }

    @Override
    public String getUniqueContextKey(Map<String, Object> context) {
        final Issue issue = (Issue) context.get("issue");
        final ApplicationUser user = authenticationContext.getLoggedInUser();

        return issue.getId() + "/" + (user == null ? "" : user.getName());
    }

    private static HttpServletRequest getRequest(Map<String, Object> context) {
        return ((JiraHelper) context.get(JiraWebInterfaceManager.CONTEXT_KEY_HELPER)).getRequest();
    }

}
