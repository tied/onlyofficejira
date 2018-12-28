package ru.hlynov.oit.tools;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.AttachmentManager;
import com.atlassian.jira.issue.attachment.Attachment;
import com.atlassian.jira.config.util.AttachmentPathManager;

import com.atlassian.jira.issue.attachment.FileAttachments;
import com.atlassian.jira.ofbiz.OfBizDelegator;
import com.atlassian.jira.user.ApplicationUser;
import org.apache.commons.io.IOUtils;
import org.ofbiz.core.entity.GenericEntityException;
import org.ofbiz.core.entity.GenericValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AttachmentUtil {

    private static final Logger log = LoggerFactory.getLogger(AttachmentUtil.class);

    // придется править размер вложения непосредственно модификацией значения в бд через сущности

    // <entity-group group="default" entity="FileAttachment"/>

//    <entity entity-name="FileAttachment" table-name="fileattachment" package-name="">
//        <field name="id" type="numeric"/>
//
//        <field name="issue" col-name="issueid" type="numeric"/>
//        <field name="mimetype" type="long-varchar"/>
//        <field name="filename" type="long-varchar"/>
//        <field name="created" type="date-time"/>
//        <field name="filesize" type="numeric"/>
//        <field name="author" type="long-varchar"/>
//        <field name="zip" col-name="zip" type="integer"/>
//        <field name="thumbnailable" col-name="thumbnailable" type="integer"/>
//
//        <prim-key field="id"/>
//
//        <relation type="one" title="Parent" rel-entity-name="Issue">
//            <key-map field-name="issue" rel-field-name="id"/>
//        </relation>
//
//        <!-- index 'fileattachment_issueid' -->
//        <index name="attach_issue">
//            <index-field name="issue"/>
//        </index>
//    </entity>

    public static void setAttachmentSize(String issueId, String attachmentId, int size) {
        OfBizDelegator delegator = ComponentAccessor.getOfBizDelegator();

        Map<String, Long> Conditions = new HashMap<String, Long>();
        Conditions.put("id", Long.valueOf(attachmentId));
//        Conditions.put("issueid", Integer.valueOf(issueId));
        Conditions.put("issue", Long.valueOf(issueId));

        List<GenericValue> attachs = delegator.findByAnd("FileAttachment", Conditions);

        if (attachs.size() == 1) {
            attachs.get(0).set("filesize", Long.valueOf(size));

            // меняем название файла, если был doc или xls
            String fileNameExt = attachs.get(0).getString("filename");
            String fileExt = fileNameExt.substring(fileNameExt.lastIndexOf('.') + 1).toLowerCase();
            String fileName = fileNameExt.substring(0, fileNameExt.lastIndexOf('.') + 1).toLowerCase();

//            log.warn(" name ==== " + fileName);
//            log.warn(" ext  ==== " + fileExt);

            if (fileExt.equals("doc")) {
                attachs.get(0).set("filename", fileName + "docx");
            }

            if (fileExt.equals("xls")) {
                attachs.get(0).set("filename", fileName + "xlsx");
            }

            try {
                attachs.get(0).store();

            } catch (GenericEntityException e) {
                e.printStackTrace();
            }
            ;
        }

        log.warn("found records = " + String.valueOf(attachs.size()));
        log.warn("name " + attachs.get(0).getString("filename") + " size " + attachs.get(0).getLong("filesize").toString());
        log.warn("size from oo " + String.valueOf(size));

    }


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
