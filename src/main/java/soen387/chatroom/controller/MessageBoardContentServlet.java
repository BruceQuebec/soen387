package soen387.chatroom.controller;

import javafx.util.Pair;
import soen387.chatroom.model.Attachment;
import soen387.chatroom.model.MessageBoardImpl;
import soen387.chatroom.model.Post;
import soen387.chatroom.model.User;
import soen387.chatroom.persistence.AttachmentDAO;
import soen387.chatroom.persistence.PostDAO;
import soen387.chatroom.persistence.TagDAO;
import soen387.chatroom.persistence.UserDAO;
import soen387.chatroom.utils.Utils;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.List;

@WebServlet(urlPatterns = "/messageboardcontent")
@MultipartConfig(
        fileSizeThreshold = 1024 * 10,  // 10 KB
        maxFileSize = 1024 * 300,       // 300 KB
        maxRequestSize = 1024 * 1024    // 1 MB
)
public class MessageBoardContentServlet extends HttpServlet {
    private final String PROPERTIES_FILE = "properties.properties";
    MessageBoardImpl messageBoardImpl = new MessageBoardImpl(new PostDAO(Utils.getPropertiesFromClasspath(PROPERTIES_FILE,this).getProperty("db_url"), Utils.getPropertiesFromClasspath(PROPERTIES_FILE,this).getProperty("db_driver"), Utils.getPropertiesFromClasspath(PROPERTIES_FILE,this).getProperty("db_root"), Utils.getPropertiesFromClasspath(PROPERTIES_FILE,this).getProperty("db_password"), Utils.getPropertiesFromClasspath(PROPERTIES_FILE,this).getProperty("db_post_table") , Utils.getPropertiesFromClasspath(PROPERTIES_FILE,this).getProperty("db_ptr_table"))
            ,new AttachmentDAO(Utils.getPropertiesFromClasspath(PROPERTIES_FILE,this).getProperty("db_url"), Utils.getPropertiesFromClasspath(PROPERTIES_FILE,this).getProperty("db_driver"), Utils.getPropertiesFromClasspath(PROPERTIES_FILE,this).getProperty("db_root"), Utils.getPropertiesFromClasspath(PROPERTIES_FILE,this).getProperty("db_password"), Utils.getPropertiesFromClasspath(PROPERTIES_FILE,this).getProperty("db_attachment_table"))
            ,new TagDAO(Utils.getPropertiesFromClasspath(PROPERTIES_FILE,this).getProperty("db_url"), Utils.getPropertiesFromClasspath(PROPERTIES_FILE,this).getProperty("db_driver"), Utils.getPropertiesFromClasspath(PROPERTIES_FILE,this).getProperty("db_root"), Utils.getPropertiesFromClasspath(PROPERTIES_FILE,this).getProperty("db_password"), Utils.getPropertiesFromClasspath(PROPERTIES_FILE,this).getProperty("db_tag_table"))
            ,new UserDAO(Utils.getPropertiesFromClasspath(PROPERTIES_FILE,this).getProperty("db_url"), Utils.getPropertiesFromClasspath(PROPERTIES_FILE,this).getProperty("db_driver"), Utils.getPropertiesFromClasspath(PROPERTIES_FILE,this).getProperty("db_root"), Utils.getPropertiesFromClasspath(PROPERTIES_FILE,this).getProperty("db_password"), Utils.getPropertiesFromClasspath(PROPERTIES_FILE,this).getProperty("db_user_table")));

    public MessageBoardContentServlet() throws IOException {}

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSession session = request.getSession(false);
        if(session.getAttribute("username")==null){
            request.getRequestDispatcher("/views/login.jsp").forward(request, response);
        }
        else if(request.getParameterMap().containsKey("newpost") && Integer.valueOf(request.getParameter("newpost"))==1){
            request.getRequestDispatcher("/views/bbsadd.jsp").forward(request, response);
        }
        else if(!request.getParameterMap().containsKey("pid") || request.getParameter("pid").equals("") || !request.getParameter("pid").matches("[0-9]+")) {
            request.setAttribute("info", "No post id found!");
            request.setAttribute("redirect_link", "http://localhost:8080/");
            request.getRequestDispatcher("/views/bbsinfo.jsp").forward(request, response);
        }
        else {
            if(request.getParameterMap().containsKey("postdel") && request.getParameter("postdel").equals("delete")){
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
            try {
                List<Pair<Post, List<Pair<Integer, String>>>> post = this.messageBoardImpl.searchPostByPid(Integer.valueOf(request.getParameter("pid")));
                if(post.size()>0){
                    request.setAttribute("post", post.get(0));
                    if(request.getParameterMap().containsKey("modify") && request.getParameter("modify").equals("modify")){
                        if(post.get(0).getKey().getUserId()==(Integer) request.getSession(false).getAttribute("uid")){
                            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
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
                        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
                        request.getRequestDispatcher("/views/bbsdetail.jsp").forward(request, response);
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

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
        if(request.getParameterMap().containsKey("username") && !request.getParameter("username").equals("")  && request.getParameterMap().containsKey("password") && !request.getParameter("password").equals("")){
            try {
                String jsonUsrLocalFilePath = Utils.getUsrLocalJsonPath(PROPERTIES_FILE,this);
                String jsonUsrLocalFileFullPath = this.getServletContext().getRealPath(jsonUsrLocalFilePath);
                List<User> userMatch = messageBoardImpl.userAuthenticate(request.getParameter("username"), request.getParameter("password"), jsonUsrLocalFileFullPath);
                if(userMatch.size()>0){
                    HttpSession session = request.getSession(true);
                    session.setAttribute("username", userMatch.get(0).getUsername());
                    session.setAttribute("uid", userMatch.get(0).getUserId());
                    doGet(request, response);
                }
                else {
                    request.getRequestDispatcher("/views/login.jsp").forward(request, response);
                }
            } catch (NoSuchAlgorithmException e) {
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
                        !request.getParameter("uid").equals("")){
                    int uid_session = (Integer) request.getSession().getAttribute("uid");
                    int uid_post = Integer.valueOf(request.getParameter("uid"));
                    int pid = Integer.valueOf(request.getParameter("pid"));
                    if(uid_session!=uid_post){
                        request.setAttribute("info", "You don't have the right to modify this post!");
                        request.setAttribute("redirect_link", "http://localhost:8080/messageboardcontent?pid=" + Integer.valueOf(request.getParameter("pid")) + "");
                        request.getRequestDispatcher("/views/bbsinfo.jsp").forward(request, response);
                    }
                    else{
                        Post post = new Post(request.getParameter("title"),
                                request.getParameter("content"),
                                uid_post,
                                (String) request.getSession().getAttribute("username"));
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
                        !request.getParameter("content").equals("")){
                    Post post = new Post(request.getParameter("title"),
                            request.getParameter("content"),
                            (Integer) request.getSession().getAttribute("uid"),
                            (String) request.getSession().getAttribute("username"));
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



