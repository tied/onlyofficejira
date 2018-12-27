package ru.hlynov.oit.webserv;

import com.atlassian.annotations.PublicApi;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.util.AttachmentPathManager;
import com.atlassian.jira.issue.attachment.FileAttachments;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;

/**
 * A resource of message.
 */

@PublicApi
@Path("/file")
public class AttachmentRest {

    private static final Logger log = LoggerFactory.getLogger(AttachmentRest.class);

    private AttachmentPathManager attachmentPathManager = ComponentAccessor.getAttachmentPathManager();
//    private File rootDir = new File(attachmentPathManager.getAttachmentPath());
    private File rootDir = new File(ComponentAccessor.getAttachmentPathManager().getAttachmentPath());



    @GET
    @AnonymousAllowed
    @Produces({MediaType.APPLICATION_OCTET_STREAM})
//    @Path("/get/{projectId}/{issueId}/{attachmentId}/{filename}")
    @Path("/get/{projectId}/{issueId}/{attachmentId}/{filename}")
//    public Response getAttachment(@QueryParam("projectId") String projectId,
//                                  @QueryParam("issueId") String issueId,
//                                  @QueryParam("attachmentId") String attachmentId,
//                                  @QueryParam("filename") String filename)
    public Response getAttachment(@PathParam("projectId") String projectId,
                                  @PathParam("issueId") String issueId,
                                  @PathParam("attachmentId") String attachmentId,
                                  @PathParam("filename") String filename)
    {

//        File rootDir = new File(ComponentAccessor.getAttachmentPathManager().getAttachmentPath());

        String issueKey = ComponentAccessor.getIssueManager().getIssueObject(Long.valueOf(issueId)).getKey();
        String projectKey = ComponentAccessor.getIssueManager().getIssueObject(Long.valueOf(issueId)).getProjectObject().getKey();


        File filePath = FileAttachments.getAttachmentDirectoryForIssue(rootDir, projectKey, issueKey);

        String filePathStr = filePath.toString();
        filePathStr = filePathStr + "/" + attachmentId;

        log.warn(" ======== get attach ======= " + filePathStr);

        File atFile = new File(filePathStr);
        Response.ResponseBuilder responseBuilder = Response.ok((Object) atFile);
        responseBuilder.header("Content-Disposition", "attachment; filename=" + filename);
        return responseBuilder.build();

    }
}