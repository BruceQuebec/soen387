package soen387.chatroom.controller;

import soen387.chatroom.model.Attachment;
import soen387.chatroom.model.MessageBoardImpl;
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
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;

@WebServlet(urlPatterns = "/attachment")
@MultipartConfig(
        fileSizeThreshold = 1024 * 10,  // 10 KB
        maxFileSize = 1024 * 300,       // 300 KB
        maxRequestSize = 1024 * 1024    // 1 MB
)
public class AttachmentServlet extends HttpServlet {
    private final String PROPERTIES_FILE = "properties.properties";

    MessageBoardImpl messageBoardImpl = new MessageBoardImpl(new PostDAO(Utils.getPropertiesFromClasspath(PROPERTIES_FILE,this).getProperty("db_url"), Utils.getPropertiesFromClasspath(PROPERTIES_FILE,this).getProperty("db_driver"), Utils.getPropertiesFromClasspath(PROPERTIES_FILE,this).getProperty("db_root"), Utils.getPropertiesFromClasspath(PROPERTIES_FILE,this).getProperty("db_password"), Utils.getPropertiesFromClasspath(PROPERTIES_FILE,this).getProperty("db_post_table") , Utils.getPropertiesFromClasspath(PROPERTIES_FILE,this).getProperty("db_ptr_table"))
            ,new AttachmentDAO(Utils.getPropertiesFromClasspath(PROPERTIES_FILE,this).getProperty("db_url"), Utils.getPropertiesFromClasspath(PROPERTIES_FILE,this).getProperty("db_driver"), Utils.getPropertiesFromClasspath(PROPERTIES_FILE,this).getProperty("db_root"), Utils.getPropertiesFromClasspath(PROPERTIES_FILE,this).getProperty("db_password"), Utils.getPropertiesFromClasspath(PROPERTIES_FILE,this).getProperty("db_attachment_table"))
            ,new TagDAO(Utils.getPropertiesFromClasspath(PROPERTIES_FILE,this).getProperty("db_url"), Utils.getPropertiesFromClasspath(PROPERTIES_FILE,this).getProperty("db_driver"), Utils.getPropertiesFromClasspath(PROPERTIES_FILE,this).getProperty("db_root"), Utils.getPropertiesFromClasspath(PROPERTIES_FILE,this).getProperty("db_password"), Utils.getPropertiesFromClasspath(PROPERTIES_FILE,this).getProperty("db_tag_table"))
            ,new UserDAO(Utils.getPropertiesFromClasspath(PROPERTIES_FILE,this).getProperty("db_url"), Utils.getPropertiesFromClasspath(PROPERTIES_FILE,this).getProperty("db_driver"), Utils.getPropertiesFromClasspath(PROPERTIES_FILE,this).getProperty("db_root"), Utils.getPropertiesFromClasspath(PROPERTIES_FILE,this).getProperty("db_password"), Utils.getPropertiesFromClasspath(PROPERTIES_FILE,this).getProperty("db_user_table")));

    public AttachmentServlet() throws IOException {}

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
        if(request.getSession().getAttribute("username")==null){
            request.setAttribute("info", "User is not logged in");
            request.setAttribute("redirect_link", "http://localhost:8080/");
            request.getRequestDispatcher("/views/bbsinfo.jsp").forward(request, response);
        }
        else {
            int uid_session = (Integer) request.getSession().getAttribute("uid");
            if(request.getParameterMap().containsKey("attdel") &&
                    Integer.valueOf(request.getParameter("attdel"))==1 &&
                    request.getParameterMap().containsKey("aid") &&
                    !request.getParameter("aid").equals("")){

                int aid = Integer.valueOf(request.getParameter("aid"));
                try {
                    List<Attachment> attachments = this.messageBoardImpl.getAttachmentById(aid);
                    int uid_att = this.messageBoardImpl.searchPostByPid(attachments.get(0).getPid()).get(0).getKey().getUserId();
                    if(uid_att!=uid_session){
                        request.setAttribute("info", "This user does have the authentication to delete the attachment!");
                        request.setAttribute("redirect_link", "http://localhost:8080/");
                        request.getRequestDispatcher("/views/bbsinfo.jsp").forward(request, response);
                    }
                    else {
                        int pid_att = attachments.get(0).getPid();
                        this.messageBoardImpl.deleteAttachment(aid);
                        request.setAttribute("info", "attachment has been deleted!");
                        request.setAttribute("redirect_link", "http://localhost:8080/messageboardcontent?pid=" + pid_att + "");
                        request.getRequestDispatcher("/views/bbsinfo.jsp").forward(request, response);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            else if(request.getParameterMap().containsKey("attopen") &&
                    Integer.valueOf(request.getParameter("attopen"))==1 &&
                    request.getParameterMap().containsKey("aid") &&
                    !request.getParameter("aid").equals("")){
                int aid = Integer.valueOf(request.getParameter("aid"));
                try {
                    List<Attachment> attachments = this.messageBoardImpl.getAttachmentById(aid);
                    if(attachments.size()>0){
                        String filename = attachments.get(0).getName();
                        String filetype = this.getServletContext().getMimeType(filename);
                        response.setHeader("Content-Type", filetype);
                        response.setHeader("Content-Length", String.valueOf(attachments.get(0).getSize()));
                        String openMethod = "inline";
                        if(filetype.toLowerCase().equals("zip") || filetype.toLowerCase().equals("rar")  ||
                                filetype.toLowerCase().equals("exe") || filetype.toLowerCase().equals("avi")  ||
                                filetype.toLowerCase().equals("midi") || filetype.toLowerCase().equals("mp3")  ||
                                filetype.toLowerCase().equals("mpg") || filetype.toLowerCase().equals("mov")  ||
                                filetype.toLowerCase().equals("wav")) {openMethod = "attchment";}
                        response.setHeader("Content-Disposition", openMethod + "; filename=" + filename + "");
                        InputStream is = new ByteArrayInputStream(attachments.get(0).getContent());

                        byte[] bytes = new byte[1024];
                        int bytesRead;

                        while ((bytesRead = is.read(bytes)) != -1) {
                            // Write data to Response.
                            response.getOutputStream().write(bytes, 0, bytesRead);
                        }
                        is.close();
                    }
                    else {
                        request.setAttribute("info", "The attachment required does not exist!");
                        request.setAttribute("redirect_link", "http://localhost:8080/");
                        request.getRequestDispatcher("/views/bbsinfo.jsp").forward(request, response);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            else {
                request.setAttribute("info", "Invalid operation!");
                request.setAttribute("redirect_link", "http://localhost:8080/");
                request.getRequestDispatcher("/views/bbsinfo.jsp").forward(request, response);
            }
        }
    }
}
