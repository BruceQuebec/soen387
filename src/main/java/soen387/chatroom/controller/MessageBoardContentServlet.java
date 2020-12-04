package soen387.chatroom.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import javafx.util.Pair;
import soen387.chatroom.model.*;
import soen387.chatroom.persistence.AttachmentDAO;
import soen387.chatroom.persistence.PostDAO;
import soen387.chatroom.persistence.TagDAO;
import soen387.chatroom.persistence.UserDAO;
import soen387.chatroom.service.userservice.UserManagerImpl;
import soen387.chatroom.utils.Utils;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.fasterxml.jackson.dataformat.xml.*;

@WebServlet(urlPatterns = "/messageboardcontent")
@MultipartConfig(
        fileSizeThreshold = 1024 * 10,  // 10 KB
        maxFileSize = 1024 * 300,       // 300 KB
        maxRequestSize = 1024 * 1024    // 1 MB
)
public class MessageBoardContentServlet extends HttpServlet {
    private final String PROPERTIES_FILE = "properties.properties";

    public MessageBoardImpl getMessageBoardImpl() { return messageBoardImpl; }

    private MessageBoardImpl messageBoardImpl = new MessageBoardImpl(new PostDAO(Utils.getPropertiesFromClasspath(PROPERTIES_FILE,this).getProperty("db_url"), Utils.getPropertiesFromClasspath(PROPERTIES_FILE,this).getProperty("db_driver"), Utils.getPropertiesFromClasspath(PROPERTIES_FILE,this).getProperty("db_root"), Utils.getPropertiesFromClasspath(PROPERTIES_FILE,this).getProperty("db_password"), Utils.getPropertiesFromClasspath(PROPERTIES_FILE,this).getProperty("db_post_table") , Utils.getPropertiesFromClasspath(PROPERTIES_FILE,this).getProperty("db_ptr_table"))
            ,new AttachmentDAO(Utils.getPropertiesFromClasspath(PROPERTIES_FILE,this).getProperty("db_url"), Utils.getPropertiesFromClasspath(PROPERTIES_FILE,this).getProperty("db_driver"), Utils.getPropertiesFromClasspath(PROPERTIES_FILE,this).getProperty("db_root"), Utils.getPropertiesFromClasspath(PROPERTIES_FILE,this).getProperty("db_password"), Utils.getPropertiesFromClasspath(PROPERTIES_FILE,this).getProperty("db_attachment_table"))
            ,new TagDAO(Utils.getPropertiesFromClasspath(PROPERTIES_FILE,this).getProperty("db_url"), Utils.getPropertiesFromClasspath(PROPERTIES_FILE,this).getProperty("db_driver"), Utils.getPropertiesFromClasspath(PROPERTIES_FILE,this).getProperty("db_root"), Utils.getPropertiesFromClasspath(PROPERTIES_FILE,this).getProperty("db_password"), Utils.getPropertiesFromClasspath(PROPERTIES_FILE,this).getProperty("db_tag_table"))
            ,new UserDAO(Utils.getPropertiesFromClasspath(PROPERTIES_FILE,this).getProperty("db_url"), Utils.getPropertiesFromClasspath(PROPERTIES_FILE,this).getProperty("db_driver"), Utils.getPropertiesFromClasspath(PROPERTIES_FILE,this).getProperty("db_root"), Utils.getPropertiesFromClasspath(PROPERTIES_FILE,this).getProperty("db_password"), Utils.getPropertiesFromClasspath(PROPERTIES_FILE,this).getProperty("db_user_table")));

    public MessageBoardContentServlet() throws IOException {}

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSession session = request.getSession(false);
        if(session.getAttribute("username")==null){
            request.getRequestDispatcher("/views/login.jsp").forward(request, response);
        }
        Integer user_id = (Integer) session.getAttribute("uid");
        String userGroupName = (String) session.getAttribute("membership");
        List<Group> groups = Utils.getGroups(PROPERTIES_FILE, this);
        List<String> curGroupNames = Utils.getGroupNameList(PROPERTIES_FILE, this, userGroupName);

        if(request.getParameterMap().containsKey("newpost") && Integer.valueOf(request.getParameter("newpost"))==1){
            request.setAttribute("available_groups", curGroupNames);
            request.getRequestDispatcher("/views/bbsadd.jsp").forward(request, response);
        }
        else if(!request.getParameterMap().containsKey("pid") || request.getParameter("pid").equals("") || !request.getParameter("pid").matches("[0-9]+")) {
            request.setAttribute("info", "No post id found!");
            request.setAttribute("redirect_link", "http://localhost:8080/");
            request.getRequestDispatcher("/views/bbsinfo.jsp").forward(request, response);
        }
        else {
            try {
                List<Pair<Post, List<Pair<Integer, String>>>> post = this.getMessageBoardImpl().searchPostByPid(Integer.valueOf(request.getParameter("pid")));
                if(post.size()>0){
                    if(request.getParameterMap().containsKey("postdel") &&
                       request.getParameter("postdel").equals("delete") &&
                       (userGroupName.equals(Utils.getAdminGroupNameKey(PROPERTIES_FILE, this)) || (post.get(0).getKey().getUserId()==user_id))
                    ){
                        try {
                            this.messageBoardImpl.deletePost(Integer.valueOf(request.getParameter("pid")));
                            request.setAttribute("info", "Post has been deleted!");
                            request.setAttribute("redirect_link", "http://localhost:8080/messageboard");
                            request.getRequestDispatcher("/views/bbsinfo.jsp").forward(request, response);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                    request.setAttribute("post", post.get(0));
                    if(request.getParameterMap().containsKey("modify") &&
                       request.getParameter("modify").equals("modify")){
                        if(userGroupName.equals(Utils.getAdminGroupNameKey(PROPERTIES_FILE, this)) ||
                           (post.get(0).getKey().getUserId()==user_id)){
                            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
                            request.setAttribute("available_groups", curGroupNames);
                            request.getRequestDispatcher("/views/bbsedit.jsp").forward(request, response);
                        }
                        else {
                            request.setAttribute("info", "You don't have the right to modify this post!");
                            request.setAttribute("redirect_link", "http://localhost:8080/messageboardcontent?pid=" + Integer.valueOf(request.getParameter("pid")) + "");
                            request.getRequestDispatcher("/views/bbsinfo.jsp").forward(request, response);
                        }
                    }
                    else if(request.getParameterMap().containsKey("reply")){
                        request.setAttribute("info", "This Function is still under the development!!");
                        request.setAttribute("redirect_link", "http://localhost:8080/messageboardcontent?pid=" + Integer.valueOf(request.getParameter("pid")) + "");
                        request.getRequestDispatcher("/views/bbsinfo.jsp").forward(request, response);
                    }
                    else {
                        Group userGroupObj = groups.stream().filter(group -> group.getName().equals(userGroupName)).collect(Collectors.toList()).get(0);
                        // transform view to convert instance of Post to Xml
                        if(request.getParameterMap().containsKey("toXml") && request.getParameter("toXml").equals("XML")){
                            ObjectMapper objectMapper = new XmlMapper();
                            JacksonXmlModule xmlModule = new JacksonXmlModule();
                            xmlModule.setDefaultUseWrapper(false);
                            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
                            String xml = objectMapper.writeValueAsString(post.get(0).getKey());

                            String filename = "post" + String.valueOf(post.get(0).getKey().getPid()) + ".xml";
                            String filetype = "xml";
                            response.setHeader("Content-Type", filetype);
                            response.setHeader("Content-Length", String.valueOf(xml.length()));
                            response.setHeader("Content-Disposition", "attchment" + "; filename=" + filename + "");
                            InputStream is = new ByteArrayInputStream(xml.getBytes());
                            byte[] bytes = new byte[1024];
                            int bytesRead;
                            while ((bytesRead = is.read(bytes)) != -1) {
                                // Write data to Response.
                                response.getOutputStream().write(bytes, 0, bytesRead);
                            }
                            is.close();
                        }
                        // template view to pass the instance of Post to jsp page on which data are populated into the markup tag
                        else if( userGroupName.equals(Utils.getAdminGroupNameKey(PROPERTIES_FILE, this)) ||
                            post.get(0).getKey().getGroupToSee().equals("Public") ||
                            userGroupName.equals(post.get(0).getKey().getGroupToSee()) ||
                            userGroupObj.getSubGroups().stream().filter(subGroup->subGroup.equals(post.get(0).getKey().getGroupToSee())).findAny().isPresent()){
                            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
                            request.getRequestDispatcher("/views/bbsdetail.jsp").forward(request, response);
                        }
                        else {
                            request.setAttribute("info", "The current membership is not a valid group to see this post!!");
                            request.setAttribute("redirect_link", "http://localhost:8080/messageboard");
                            request.getRequestDispatcher("/views/bbsinfo.jsp").forward(request, response);
                        }
                    }
                }
                else {
                    request.setAttribute("info", "Post not found!");
                    request.setAttribute("redirect_link", "http://localhost:8080/messageboard");
                    request.getRequestDispatcher("/views/bbsinfo.jsp").forward(request, response);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
        if(request.getParameterMap().containsKey("username") && !request.getParameter("username").equals("")  && request.getParameterMap().containsKey("password") && !request.getParameter("password").equals("")){
            try {
                UserManagerFactory userManagerFactory = new LocalUserManagerFactory(Utils.getUserManagerClassNameKey(PROPERTIES_FILE, this));
                UserManagerImpl userManager = (UserManagerImpl) userManagerFactory.getUserManager();

                String jsonUsrLocalFilePath = Utils.getUsrLocalJsonPath(PROPERTIES_FILE,this);
                String jsonUsrLocalFileFullPath = this.getServletContext().getRealPath(jsonUsrLocalFilePath);
                Map<String, String> userManagerContext = new HashMap<>();
                userManagerContext.put("username", request.getParameter("username"));
                userManagerContext.put("password", request.getParameter("password"));
                userManagerContext.put("userFilePath", jsonUsrLocalFileFullPath);
                userManager.setContext(userManagerContext);
                userManager.userAuthenticate();
                if(userManager.getUserMatched().size()>0){
                    HttpSession session = request.getSession(true);
                    session.setAttribute("username", userManager.getUserMatched().get(0).getUsername());
                    session.setAttribute("uid", userManager.getUserMatched().get(0).getUserId());
                    session.setAttribute("membership", userManager.getUserMatched().get(0).getMembership());
                    doGet(request, response);
                }
                else {
                    request.getRequestDispatcher("/views/login.jsp").forward(request, response);
                }
            } catch (NoSuchAlgorithmException | ClassNotFoundException | IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        else {
            if(request.getSession().getAttribute("username")==null){
                request.getRequestDispatcher("/views/login.jsp").forward(request, response);
            }
            else {
                if(request.getParameterMap().containsKey("editpost") &&
                        !request.getParameter("title").equals("")&&
                        !request.getParameter("content").equals("") &&
                        request.getParameterMap().containsKey("pid") &&
                        !request.getParameter("pid").equals("") &&
                        request.getParameterMap().containsKey("uid") &&
                        !request.getParameter("uid").equals("") &&
                        request.getParameterMap().containsKey("group_to_see") &&
                        !request.getParameter("group_to_see").equals("")){
                    String userGroupName = (String) request.getSession().getAttribute("membership");
                    int uid_session = (Integer) request.getSession().getAttribute("uid");
                    int uid_post = Integer.valueOf(request.getParameter("uid"));
                    int pid = Integer.valueOf(request.getParameter("pid"));

                    if(uid_session!=uid_post && !userGroupName.equals(Utils.getAdminGroupNameKey(PROPERTIES_FILE, this))){
                        request.setAttribute("info", "You don't have the right to modify this post!");
                        request.setAttribute("redirect_link", "http://localhost:8080/messageboardcontent?pid=" + Integer.valueOf(request.getParameter("pid")) + "");
                        request.getRequestDispatcher("/views/bbsinfo.jsp").forward(request, response);
                    }
                    else{
                        Post post = new Post(request.getParameter("title"),
                                request.getParameter("content"),
                                uid_post,
                                (String) request.getSession().getAttribute("username"),
                                request.getParameter("group_to_see"));
                        List<Attachment> attachments = Utils.extractAttachment(request);
                        if(attachments.size()>0){
                            post.setAttachments(attachments);
                        }
                        List<String> hashTags = Utils.parseForHashTag(request.getParameter("content"));
                        if(hashTags.size()>0){
                            post.setHashTags(hashTags);
                        }
                        try {
                            post.setPid(pid);
                            messageBoardImpl.editPost(pid, post);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                        request.setAttribute("info", "The post has been successfully modified!");
                        request.setAttribute("redirect_link", "http://localhost:8080/messageboardcontent?pid=" + Integer.valueOf(request.getParameter("pid")) + "");
                        request.getRequestDispatcher("/views/bbsinfo.jsp").forward(request, response);
                    }
                }
                else if(request.getParameterMap().containsKey("addNewPost") &&
                        !request.getParameter("title").equals("")&&
                        !request.getParameter("content").equals("") &&
                        !request.getParameter("group_to_see").equals("")){
                    Post post = new Post(request.getParameter("title"),
                            request.getParameter("content"),
                            (Integer) request.getSession().getAttribute("uid"),
                            (String) request.getSession().getAttribute("username"),
                            request.getParameter("group_to_see"));
                    List<Attachment> attachments = Utils.extractAttachment(request);
                    if(attachments.size()>0){
                        post.setAttachments(attachments);
                    }
                    List<String> hashTags = Utils.parseForHashTag(request.getParameter("content"));
                    if(hashTags.size()>0){
                        post.setHashTags(hashTags);
                    }
                    int pid = 0;
                    try {
                        pid = messageBoardImpl.createPost(post);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    request.setAttribute("info", "New post has been successfully created!");
                    request.setAttribute("redirect_link", "http://localhost:8080/messageboardcontent?pid=" + pid + "");
                    request.getRequestDispatcher("/views/bbsinfo.jsp").forward(request, response);
                }
                else {
                    request.setAttribute("info", "ilegal operation!");
                    request.setAttribute("redirect_link", "http://localhost:8080/messageboard");
                    request.getRequestDispatcher("/views/bbsinfo.jsp").forward(request, response);
                }
            }
        }
    }
}



