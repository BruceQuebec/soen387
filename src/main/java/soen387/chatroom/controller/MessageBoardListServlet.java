package soen387.chatroom.controller;

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
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
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
                                                            ,new TagDAO(Utils.getPropertiesFromClasspath(PROPERTIES_FILE,this).getProperty("db_url"), Utils.getPropertiesFromClasspath(PROPERTIES_FILE,this).getProperty("db_driver"), Utils.getPropertiesFromClasspath(PROPERTIES_FILE,this).getProperty("db_root"), Utils.getPropertiesFromClasspath(PROPERTIES_FILE,this).getProperty("db_password"), Utils.getPropertiesFromClasspath(PROPERTIES_FILE,this).getProperty("db_tag_table"))
                                                            ,new UserDAO(Utils.getPropertiesFromClasspath(PROPERTIES_FILE,this).getProperty("db_url"), Utils.getPropertiesFromClasspath(PROPERTIES_FILE,this).getProperty("db_driver"), Utils.getPropertiesFromClasspath(PROPERTIES_FILE,this).getProperty("db_root"), Utils.getPropertiesFromClasspath(PROPERTIES_FILE,this).getProperty("db_password"), Utils.getPropertiesFromClasspath(PROPERTIES_FILE,this).getProperty("db_user_table")));

    public MessageBoardListServlet() throws IOException {}

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSession session = request.getSession(false);

        if(session==null || session.getAttribute("username")==null){
            request.getRequestDispatcher("/views/login.jsp").forward(request, response);
        }
        else if(request.getParameterMap().containsKey("logout") && Integer.valueOf(request.getParameter("logout"))==1){
            request.getSession().removeAttribute("username");
            request.getSession().removeAttribute("uid");
            doGet(request, response);
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
                if(request.getParameterMap().containsKey("query") && request.getParameter("query").equals("1")){
                    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
                    request.getRequestDispatcher("/views/bbsquery.jsp").forward(request, response);
                }
                else if(request.getParameterMap().containsKey("search_type") && request.getParameter("search_type").equals("tag") && request.getParameterMap().containsKey("keyword") && !request.getParameter("keyword").equals("")){
                    boardQuery = "search_type=tag&keyword="+request.getParameter("keyword");
                    int tid = this.messageBoardImpl.findTidByName(request.getParameter("keyword"));
                    if(tid>0){
                        int totalNumPostByTid = this.messageBoardImpl.getTotalPostNumberByTag(tid);
                        postPageInfo = Utils.getListPageInfo(currentPage, postPerPage, totalNumPostByTid);
                        postList = this.messageBoardImpl.searchPostByTag(tid, postPageInfo.get("offset"), postPageInfo.get("limit"));
                    }
                }
                else if(request.getParameterMap().containsKey("search_type") && request.getParameter("search_type").equals("userid") && request.getParameterMap().containsKey("keyword") && !request.getParameter("keyword").equals("") && request.getParameter("keyword").matches("[0-9]+")){
                    boardQuery = "search_type=userid&keyword="+request.getParameter("keyword");
                    int totalNumPostByUid = this.messageBoardImpl.getTotalPostNumberByUid(Integer.valueOf(request.getParameter("keyword")));
                    postPageInfo = Utils.getListPageInfo(currentPage, postPerPage, totalNumPostByUid);
                    postList = this.messageBoardImpl.searchPostByUser(Integer.valueOf(request.getParameter("keyword")), postPageInfo.get("offset"), postPageInfo.get("limit"));
                }
                else if(request.getParameterMap().containsKey("search_type") && request.getParameter("search_type").equals("username") && request.getParameterMap().containsKey("keyword") && !request.getParameter("keyword").equals("")){
                    boardQuery = "search_type=username&keyword="+request.getParameter("keyword");
                    int uid = this.messageBoardImpl.getUidByName(request.getParameter("keyword"));
                    int totalNumPostByUid = this.messageBoardImpl.getTotalPostNumberByUid(uid);
                    postPageInfo = Utils.getListPageInfo(currentPage, postPerPage, totalNumPostByUid);
                    postList = this.messageBoardImpl.searchPostByUser(uid, postPageInfo.get("offset"), postPageInfo.get("limit"));
                }
                else if(request.getParameterMap().containsKey("search_type") && request.getParameter("search_type").equals("title") && request.getParameterMap().containsKey("keyword") && !request.getParameter("keyword").equals("")){
                    request.setAttribute("info", "This Function is still under the development!!");
                    request.setAttribute("redirect_link", "http://localhost:8080/");
                    request.getRequestDispatcher("/views/bbsinfo.jsp").forward(request, response);
                }
                else if(request.getParameterMap().containsKey("search_type") && request.getParameter("search_type").equals("datetime") && request.getParameterMap().containsKey("keyword") && !request.getParameter("keyword").equals("")){
                    request.setAttribute("info", "This Function is still under the development!!");
                    request.setAttribute("redirect_link", "http://localhost:8080/");
                    request.getRequestDispatcher("/views/bbsinfo.jsp").forward(request, response);
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
            } catch (ClassNotFoundException | NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            request.getRequestDispatcher("/views/bbslist.jsp").forward(request, response);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
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
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        else {
            request.getRequestDispatcher("/views/login.jsp").forward(request, response);
        }
    }
}
