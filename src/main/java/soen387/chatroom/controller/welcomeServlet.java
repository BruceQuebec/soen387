package soen387.chatroom.controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = "/index")
public class welcomeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if(request.getParameterMap().containsKey("style")){
            String style = request.getParameter("style");
            request.setAttribute("style", style);
            System.out.println("get style: " + style);
            request.getRequestDispatcher("/WEB-INF/views/index.jsp").forward(request, response);
        }
        else {
            System.out.println("get request not found");
            request.setAttribute("style", "black");
            request.getRequestDispatcher("/WEB-INF/views/index.jsp").forward(request, response);
        }
    }
}
