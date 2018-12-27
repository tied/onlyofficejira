package ru.hlynov.oit.tools;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.AttachmentManager;
import com.atlassian.jira.issue.attachment.Attachment;
import com.atlassian.jira.config.util.AttachmentPathManager;

import com.atlassian.jira.issue.attachment.FileAttachments;
import com.atlassian.jira.user.ApplicationUser;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class AttachmentUtil {

    private static final Logger log = LoggerFactory.getLogger(AttachmentUtil.class);

    public static void saveAttachment(String projectId, String issueId, String attachmentId, InputStream attachmentData, int size, ApplicationUser user) throws IOException, IllegalArgumentException {

        AttachmentPathManager attachmentPathManager = ComponentAccessor.getAttachmentPathManager();
        File rootDir = new File(attachmentPathManager.getAttachmentPath());
        File filePath = FileAttachments.getAttachmentDirectoryForIssue(rootDir, projectId, issueId);
        String filePathStr = filePath.toString();
        filePathStr = filePathStr + "/" + attachmentId;
        File atFile = new File(filePathStr);

        if (atFile.exists()) {
            OutputStream outStream = new FileOutputStream(atFile);
            byte[] buffer = new byte[8 * 1024];
            int bytesRead;
            while ((bytesRead = attachmentData.read(buffer)) != -1) {
                outStream.write(buffer, 0, bytesRead);
            }
            IOUtils.closeQuietly(attachmentData);
            IOUtils.closeQuietly(outStream);
        }



    }


}
