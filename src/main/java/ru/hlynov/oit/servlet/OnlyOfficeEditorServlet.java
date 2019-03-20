package ru.hlynov.oit.servlet;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.properties.APKeys;
import com.atlassian.jira.user.ApplicationUser;

import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.security.login.LoginManager;

import javax.inject.Inject;
import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import com.atlassian.templaterenderer.TemplateRenderer;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.auth.LoginUriProvider;

import java.net.URI;
import java.util.UUID;

import ru.hlynov.oit.api.PluginSettingsService;

public class OnlyOfficeEditorServlet extends HttpServlet{
    private static final Logger log = LoggerFactory.getLogger(OnlyOfficeEditorServlet.class);

    @ComponentImport
    private final UserManager userManager;
    @ComponentImport
    private final TemplateRenderer renderer;
    @ComponentImport
    private final LoginUriProvider loginUriProvider;

    private final PluginSettingsService pluginSettingsService;


    @Inject
    public OnlyOfficeEditorServlet(UserManager userManager, TemplateRenderer renderer, LoginUriProvider loginUriProvider, PluginSettingsService pluginSettingsService) {
        this.userManager = userManager;
        this.renderer = renderer;
        this.loginUriProvider = loginUriProvider;
        this.pluginSettingsService = pluginSettingsService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
//        resp.setContentType("text/html");
//        resp.getWriter().write("<html><body>Hello World</body></html>");

        String username = userManager.getRemoteUsername(req);
//        if (username == null || !userManager.isSystemAdmin(username))
        if (username == null)
        {
            redirectToLogin(req, resp);
            return;
        }

        Map<String, Object> context = Maps.newHashMap();

        ///////////////////////////////////////////
        // имя сервера с редактором
        ///////////////////////////////////////////
        String cfg = pluginSettingsService.getConfigJson();
        JsonParser parser = new JsonParser();
        JsonObject cfgObj = parser.parse(cfg).getAsJsonObject();

        String serverName = cfgObj.get("servername").getAsString();


        context.put("docserviceApiUrl", serverName + "/web-apps/apps/api/documents/api.js");
//        context.put("docserviceApiUrl", "http://" + serverName + "/web-apps/apps/api/documents/api.js");
//        context.put("key", "12345678");
        ///////////////////////////////////////////

        String reqParam = req.getParameter("fileurl");
        if (reqParam == null) {
            context.put("fileUrl", "");
        } else {
            context.put("fileUrl", reqParam);
        }

        reqParam = req.getParameter("filename");
        if (reqParam == null) {
            context.put("fileName", "");
        } else {
            context.put("fileName", reqParam);
        }

        ApplicationUser loggedInUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser();

//        context.put("userId", String.valueOf(loggedInUser.getId()));
        context.put("userId", (loggedInUser.getName()));
        context.put("userName", loggedInUser.getDisplayName());

        reqParam = req.getParameter("projectId");
        if (reqParam == null) {
            context.put("projectId", "");
        } else {
            context.put("projectId", reqParam);
        }

        reqParam = req.getParameter("issueId");
        if (reqParam == null) {
            context.put("issueId", "");
        } else {
            context.put("issueId", reqParam);
        }

        reqParam = req.getParameter("attachmentId");
        if (reqParam == null) {
            context.put("attachmentId", "");
        } else {
            context.put("attachmentId", reqParam);
        }

//        context.put("jirabaseurl", ComponentAccessor.getApplicationProperties().getString(APKeys.JIRA_BASEURL));

//        context.put("key", req.getParameter("projectId").toString() + req.getParameter("issueId").toString() + req.getParameter("attachmentId").toString());
        context.put("key", UUID.randomUUID().toString());


        String callbackURL = ComponentAccessor.getApplicationProperties().getString(APKeys.JIRA_BASEURL)
                + "/plugins/servlet/onlyoffice/save?projectId=" + req.getParameter("projectId")
                + "&issueId=" + req.getParameter("issueId")
                + "&attachmentId=" + req.getParameter("attachmentId");

//        callbackURL = URLEncoder.encode(callbackURL, StandardCharsets.UTF_8.toString());

        context.put("callbackUrl", callbackURL);

        context.put("errorMessage", "");

        resp.setContentType("text/html;charset=utf-8");
//        renderer.render("templates/only-editor-test.vm", context, resp.getWriter());
        renderer.render("templates/onlyoffice-editor-panel/only-editor.vm", context, resp.getWriter());
    }


    private void redirectToLogin(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        response.sendRedirect(loginUriProvider.getLoginUri(getUri(request)).toASCIIString());
    }

    private URI getUri(HttpServletRequest request)
    {
        StringBuffer builder = request.getRequestURL();
        if (request.getQueryString() != null)
        {
            builder.append("?");
            builder.append(request.getQueryString());
        }
        return URI.create(builder.toString());
    }


}
