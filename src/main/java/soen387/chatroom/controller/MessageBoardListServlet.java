package soen387.chatroom.controller;

import javafx.util.Pair;
import soen387.chatroom.persistence.AttachmentDAO;
import soen387.chatroom.persistence.PostDAO;
import soen387.chatroom.persistence.TagDAO;
import soen387.chatroom.model.MessageBoardImpl;
import soen387.chatroom.model.Post;
import soen387.chatroom.model.User;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = "/messageboard")
@MultipartConfig(
        fileSizeThreshold = 1024 * 10,  // 10 KB
        maxFileSize = 1024 * 300,       // 300 KB
        maxRequestSize = 1024 * 1024    // 1 MB
)
public class MessageBoardListServlet extends HttpServlet {
    private final String PROPERTIES_FILE = "properties.properties";
    MessageBoardImpl messageBoardImpl = new MessageBoardImpl(new PostDAO(Utils.getPropertiesFromClasspath(PROPERTIES_FILE,this).getProperty("db_url"), Utils.getPropertiesFromClasspath(PROPERTIES_FILE,this).getProperty("db_driver"), Utils.getPropertiesFromClasspath(PROPERTIES_FILE,this).getProperty("db_root"), Utils.getPropertiesFromClasspath(PROPERTIES_FILE,this).getProperty("db_password"), Utils.getPropertiesFromClasspath(PROPERTIES_FILE,this).getProperty("db_post_table") , Utils.getPropertiesFromClasspath(PROPERTIES_FILE,this).getProperty("db_ptr_table"))
                                                            ,new AttachmentDAO(Utils.getPropertiesFromClasspath(PROPERTIES_FILE,this).getProperty("db_url"), Utils.getPropertiesFromClasspath(PROPERTIES_FILE,this).getProperty("db_driver"), Utils.getPropertiesFromClasspath(PROPERTIES_FILE,this).getProperty("db_root"), Utils.getPropertiesFromClasspath(PROPERTIES_FILE,this).getProperty("db_password"), Utils.getPropertiesFromClasspath(PROPERTIES_FILE,this).getProperty("db_attachment_table"))
                                                            ,new TagDAO(Utils.getPropertiesFromClasspath(PROPERTIES_FILE,this).getProperty("db_url"), Utils.getPropertiesFromClasspath(PROPERTIES_FILE,this).getProperty("db_driver"), Utils.getPropertiesFromClasspath(PROPERTIES_FILE,this).getProperty("db_root"), Utils.getPropertiesFromClasspath(PROPERTIES_FILE,this).getProperty("db_password"), Utils.getPropertiesFromClasspath(PROPERTIES_FILE,this).getProperty("db_tag_table")));

    public MessageBoardListServlet() throws IOException {}

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSession session = request.getSession(false);

        if(session==null || session.getAttribute("username")==null){
            request.getRequestDispatcher("/views/login.jsp").forward(request, response);
        }
        else {
            int postPerPage = Integer.valueOf(Utils.getPropertiesFromClasspath(PROPERTIES_FILE,this).getProperty("view_threads_per_page"));
            int currentPage = 1;

            String boardQuery = "";
            String pageQuery = "";
            if(request.getParameterMap().containsKey("boardQuery") && !request.getParameter("boardQuery").equals("")){
                boardQuery = request.getParameter("boardQuery");
            }
            if(request.getParameterMap().containsKey("numPostPage") &&
                !request.getParameter("numPostPage").equals("") &&
                request.getParameter("numPostPage").matches("[0-9]+")){
                postPerPage = Integer.valueOf(request.getParameter("numPostPage"));
                pageQuery += "&numPostPage=" + postPerPage;
            }
            if(request.getParameterMap().containsKey("page") &&
                    !request.getParameter("page").equals("") &&
                    request.getParameter("page").matches("[0-9]+")){
                currentPage = Integer.valueOf(request.getParameter("page"));
                pageQuery += "&page=" + currentPage;
            }
            Map<String, Integer> postPageInfo = new HashMap<>();
            List<Pair<Post, List<Pair<Integer, String>>>> postList = new ArrayList<>();
            try {
                if(request.getParameterMap().containsKey("query")){
                    request.setAttribute("info", "This Function is still under the development!!");
                    request.setAttribute("redirect_link", "http://localhost:8080/");
                    request.getRequestDispatcher("/views/bbsinfo.jsp").forward(request, response);
                }
                else if(request.getParameterMap().containsKey("tagsearch") && Integer.valueOf(request.getParameter("tagsearch"))==1 && request.getParameterMap().containsKey("tag") && !request.getParameter("tag").equals("")){
                    boardQuery = "tagsearch=1&tag="+request.getParameter("tag");
                    int tid = this.messageBoardImpl.findTidByName(request.getParameter("tag"));
                    if(tid>0){
                        int totalNumPostByTid = this.messageBoardImpl.getTotalPostNumberByTag(tid);
                        postPageInfo = Utils.getListPageInfo(currentPage, postPerPage, totalNumPostByTid);
                        postList = this.messageBoardImpl.searchPostByTag(tid, postPageInfo.get("offset"), postPageInfo.get("limit"));
                    }
                }
                else if(request.getParameterMap().containsKey("usersearch") && Integer.valueOf(request.getParameter("usersearch"))==1 && request.getParameterMap().containsKey("userid") && !request.getParameter("userid").equals("") && request.getParameter("userid").matches("[0-9]+")){
                    boardQuery = "usersearch=1&userid="+request.getParameterMap().containsKey("userid");
                    int totalNumPostByUid = this.messageBoardImpl.getTotalPostNumberByUid(Integer.valueOf(request.getParameter("userid")));
                    postPageInfo = Utils.getListPageInfo(currentPage, postPerPage, totalNumPostByUid);
                    postList = this.messageBoardImpl.searchPostByUser(Integer.valueOf(request.getParameter("userid")), postPageInfo.get("offset"), postPageInfo.get("limit"));
                }
                else {
                    boardQuery = "";
                    int totalNumPost = this.messageBoardImpl.getTotalPostNumber();
                    postPageInfo = Utils.getListPageInfo(currentPage, postPerPage, totalNumPost);
                    postList = this.messageBoardImpl.listPosts(postPageInfo.get("offset"), postPageInfo.get("limit"));
                }

                boardQuery += pageQuery;
                request.setAttribute("iPageCurrent", postPageInfo.get("iPageCurrent"));
                request.setAttribute("iPageNext", postPageInfo.get("iPageNext"));
                request.setAttribute("iPageLast", postPageInfo.get("iPageLast"));
                request.setAttribute("iPageCount", postPageInfo.get("iPageCount"));
                request.setAttribute("postList", postList);
                request.setAttribute("boardQuery", boardQuery);
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            request.getRequestDispatcher("/views/bbslist.jsp").forward(request, response);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
        if(request.getParameterMap().containsKey("username") && !request.getParameter("username").equals("")  && request.getParameterMap().containsKey("password") && !request.getParameter("password").equals("")){
            try {
                List<User> userMatch = messageBoardImpl.userAuthenticate(request.getParameter("username"), request.getParameter("password"));
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
            request.getRequestDispatcher("/views/login.jsp").forward(request, response);
        }
    }
}
