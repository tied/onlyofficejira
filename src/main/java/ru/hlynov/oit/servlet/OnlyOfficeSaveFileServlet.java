package ru.hlynov.oit.servlet;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.attachment.Attachment;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.util.UserManager;

import org.ofbiz.core.entity.GenericValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Scanner;
import java.net.HttpURLConnection;
import org.json.JSONArray;
import org.json.JSONObject;
import ru.hlynov.oit.tools.AttachmentUtil;

import java.net.URL;

public class OnlyOfficeSaveFileServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final Logger log = LoggerFactory.getLogger(OnlyOfficeSaveFileServlet.class);

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //super.doPost(req, resp);
        resp.setContentType("text/plain; charset=utf-8");

        //String vkey = req.getParameter("vkey");
        //log.info("vkey = " + vkey);



        // здесь id
        String projectId = req.getParameter("projectId");
        String issueId = req.getParameter("issueId");
        String attachmentId = req.getParameter("attachmentId");

//        log.warn(" ============================================ ");
//        log.warn("req = " + req.getRequestURL().toString());
//        log.warn("arg = " + req.getQueryString());
//
//        log.warn(" ============================================ ");
//
//        log.warn(" ============================================ ");
//        log.warn("vkey = " + attachmentId);
//        log.warn(" ============================================ ");

//        boolean result = processData(attachmentIdString, req.getInputStream());

//        // найти параметры, задачи, проекта и вложения
//        Issue issue = ComponentAccessor.getIssueManager().getIssueObject(Long.valueOf(issueId));
//        String issueKey = issue.getKey();
//        String projectKey = issue.getProjectObject().getKey();


        // для отладки - потом удалить
        //String projectKey = req.getParameter("projectId");
        Issue issue = ComponentAccessor.getIssueManager().getIssueObject(Long.valueOf(issueId));
        Collection<Attachment> attachments = issue.getAttachments();
        for (Attachment oneAtt : attachments) {
            log.warn("from db name = " + oneAtt.getFilename() + " size " + String.valueOf(oneAtt.getFilesize()));
        }


        boolean result = processData(projectId, issueId, attachmentId, req.getInputStream());
//        boolean result = processData(projectKey, issueKey, attachmentId, req.getInputStream());


        String error = "1";
        if (result) {
            error = "0";
        }


//        PrintWriter writer = resp.getWriter();
//        writer.write("{\"error\":0}");

        PrintWriter writer = resp.getWriter();
        writer.write("{\"error\":" + error + "}");
        log.info("error = " + error);
    }


    //    private boolean processData(String attachmentIdString, InputStream requestStream) throws IOException {
    private boolean processData(String projectId, String issueId, String attachmentId, InputStream requestStream) throws IOException {


        // здесь key
        // найти параметры, задачи, проекта и вложения
        Issue issue = ComponentAccessor.getIssueManager().getIssueObject(Long.valueOf(issueId));
        String issueKey = issue.getKey();
        String projectKey = issue.getProjectObject().getKey();

        String attachmentIdString = projectKey + issueKey + attachmentId;

        if (attachmentIdString.isEmpty()) {
            return false;
        }

        HttpURLConnection connection = null;

        try {
            //Long attachmentIdLong = Long.valueOf(Long.parseLong(attachmentIdString));

            String body = getBody(requestStream);

            log.warn("body = " + body);

            if (body.isEmpty()) {
                return false;
            }

            JSONObject jsonObj = new JSONObject(body);

            long status = jsonObj.getLong("status");
            log.warn("status = " + status);

//            ConfluenceUser user;
            ApplicationUser user = null;

            if ((status == 2L) || (status == 3L)) {
//            if ((status == 2L) || (status == 3L) || (status == 1L)) {
                //user = null;
                JSONArray users = jsonObj.getJSONArray("users");
                if (users.length() > 0) {
                    String userName = users.getString(0);

//                    JSONObject userObj = users.getJSONObject(0);
//                    String userName = userObj.getString("name");
//                    int userId = userObj.getInt("id");

                    log.warn("users = " + users.toString());
                    log.warn("username = " + userName);


//                    UserAccessor userAccessor = (UserAccessor)ContainerManager.getComponent("userAccessor");
                    UserManager userManager = ComponentAccessor.getUserManager();
                    //user = userManager.getUserByName(userName);
                    user = userManager.getUserByKey(userName.toLowerCase());
//                    user = userManager.getUserById(Long.valueOf(userName));

                    // log.warn("user = " + user.toString());
                }
//
//                if ((user == null) || (!AttachmentUtil.checkAccess(attachmentId, user, true))) {
//                    throw new SecurityException("Try save without access: " + user);
//                }


//                log.warn(" ====== user editor =====");
//                if (user == null) {
//                    log.warn(" user is null");
//                } else {
//                    log.warn(user.getUsername());
//                }

                // если редактирование задачи и аттачментов запрещено то не сохраняем изменения
                if (!AttachmentUtil.canDeleteAttachment(issueKey, attachmentId, user)) {
                    log.warn("attacment with id " + attachmentId + " not allow to edit");
                    return true;
                }

                String downloadUrl = jsonObj.getString("url");
                log.warn("downloadUri = " + downloadUrl);

                URL url = new URL(downloadUrl);

                connection = (HttpURLConnection)url.openConnection();
                int size = connection.getContentLength();
                log.warn("size = " + size);

                InputStream stream = connection.getInputStream();


                AttachmentUtil.saveAttachment(projectKey, issueKey, attachmentId, stream, size, user);
                AttachmentUtil.setAttachmentSize(issueId,attachmentId, size);

            }

            return true;
        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            String error = ex.toString() + "\n" + sw.toString();
            log.error(error);

            return false;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private String getBody(InputStream stream) {
        Scanner scanner = null;
        Scanner scannerUseDelimiter = null;
        try {
            scanner = new Scanner(stream);
            scannerUseDelimiter = scanner.useDelimiter("\\A");
            return scanner.hasNext() ? scanner.next() : "";
        } finally {
            scannerUseDelimiter.close();
            scanner.close();
        }
    }

}
