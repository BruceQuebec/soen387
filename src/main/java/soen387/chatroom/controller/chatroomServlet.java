package soen387.chatroom.controller;

import javafx.util.Pair;
import soen387.chatroom.model.ChatManagerImpl;
import soen387.chatroom.utils.XmlBuilder;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@WebServlet(urlPatterns = "/chatroom")
public class chatroomServlet extends HttpServlet {
    ChatManagerImpl chatManagerImpl = ChatManagerImpl.getInstance();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String referrer = request.getHeader("referer");
        if(referrer==null) {
            styleCheck(request);
            request.getRequestDispatcher("/WEB-INF/views/forbidden.jsp").forward(request, response);
        }
        else {
            styleCheck(request);
            this.messageDownloadDeleteHandler(request, response);
            Map<Integer, Pair<String, String>> messageMap = chatManagerImpl.listMessages();
            request.setAttribute("messageMap", messageMap);
            request.getRequestDispatcher("/WEB-INF/views/chatroom.jsp").forward(request, response);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        styleCheck(request);
        if(Integer.valueOf(request.getParameter("send"))==1 && !request.getParameter("message").equals("")){
            String username = (!request.getParameterMap().containsKey("user") || request.getParameter("user").equals("")) ? "anonymous user" : request.getParameter("user");
            String message = request.getParameter("message");
            chatManagerImpl.postMessage(username, message);
        }
        Map<Integer, Pair<String, String>> messageMap = chatManagerImpl.listMessages();
        request.setAttribute("messageMap", messageMap);
        request.getRequestDispatcher("/WEB-INF/views/chatroom.jsp").forward(request, response);
    }

    private String messageStringGen(String format, Map<Integer, Pair<String, String>> messageMap){
        if(format=="application/xml"){
            XmlBuilder messageInXml = new XmlBuilder().addTag("ChatRoom");
            messageMap.forEach((index, pair)->{
                messageInXml.addChild(new XmlBuilder().addTag("ChatRecord")
                                .addChild(new XmlBuilder().addTag("Number").setText(String.valueOf(index)))
                                .addChild(new XmlBuilder().addTag("User").setText(pair.getKey()))
                                .addChild(new XmlBuilder().addTag("Message").setText(pair.getValue()))
                        );
            });
           return messageInXml.build(true, 0);
        }
        else {
            StringBuilder sb = new StringBuilder();
            messageMap.entrySet().forEach(entry->{
                sb.append("No. " + String.valueOf(entry.getKey()))
                .append(System.getProperty("line.separator"))
                .append("User: " + entry.getValue().getKey())
                .append(System.getProperty("line.separator"))
                .append("Message: " + entry.getValue().getValue())
                .append(System.getProperty("line.separator"));
            });
            return sb.toString();
        }
    }

    private void messageDownloadDeleteHandler(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if(request.getParameterMap().containsKey("download") && Integer.parseInt(request.getParameter("download"))==1){
            int from = (request.getParameterMap().containsKey("from") && !request.getParameter("from").equals(""))? Integer.parseInt(request.getParameter("from")) : 0;
            int to = (request.getParameterMap().containsKey("to") && !request.getParameter("to").equals("")) ? Integer.parseInt(request.getParameter("to")) : this.chatManagerImpl.getCount();

            messageListIndexValidator(request, response, from, to);
            String format = request.getParameterMap().containsKey("format") ? request.getParameter("format") : "plainText";
            format = format.equals("plainText") ? "text/plain" : "application/xml";
            String file_extension_name = format.equals("text/plain") ? "txt" : "xml";
            String file_name = "chat_history";
            final int ARBITARY_SIZE = 1048;

            Map<Integer, Pair<String, String>> messageMap =
                    (from!=0 || to!=this.chatManagerImpl.getCount()) ?
                            chatManagerImpl.listMessages(from, to) : chatManagerImpl.listMessages();
            response.setContentType(format);
            response.setHeader("Content-disposition", "attachment; filename=" + file_name + "." + file_extension_name);

            try(InputStream in = new ByteArrayInputStream(messageStringGen(format, messageMap).getBytes(StandardCharsets.UTF_8));
                OutputStream out = response.getOutputStream()) {
                byte[] buffer = new byte[ARBITARY_SIZE];
                int numBytesRead;
                while ((numBytesRead = in.read(buffer)) > 0) {
                    out.write(buffer, 0, numBytesRead);
                }
            }
        }
        if(request.getParameterMap().containsKey("delete") && Integer.parseInt(request.getParameter("delete"))==1){
            if((!request.getParameterMap().containsKey("from") && !request.getParameterMap().containsKey("to")) || (request.getParameter("from").equals("") && request.getParameter("to").equals(""))){
                messageListIndexValidator(request, response, 0, 0);
                chatManagerImpl.clearChat();
            }
            else {
                int from = (request.getParameterMap().containsKey("from") && !request.getParameter("from").equals(""))? Integer.parseInt(request.getParameter("from")) : 0;
                int to = (request.getParameterMap().containsKey("to") && !request.getParameter("to").equals("")) ? Integer.parseInt(request.getParameter("to")) : this.chatManagerImpl.getCount();
                messageListIndexValidator(request, response, from, to);
                chatManagerImpl.clearChat(from, to);
            }
        }
    }

    private void messageListIndexValidator(HttpServletRequest request, HttpServletResponse response, int from, int to) throws ServletException, IOException {
        styleCheck(request);
        if(from<0 || from>chatManagerImpl.getCount() || to<0 || to>chatManagerImpl.getCount()){
            String err = "The requested index is out of bound!";
            request.setAttribute("err", err);
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
        else if(chatManagerImpl.listMessages().size()==0){
            String err = "No message for now, no need to delete or download!";
            request.setAttribute("err", err);
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }

    private void styleCheck(HttpServletRequest request){
        if(request.getParameterMap().containsKey("style")){
            String style = request.getParameter("style");
            request.setAttribute("style", style);
        }
        else {
            request.setAttribute("style", "black");
        }
    }
}
