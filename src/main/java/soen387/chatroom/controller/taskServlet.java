package soen387.chatroom.controller;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.stream.Collectors;

public class taskServlet extends HttpServlet  {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSession session = request.getSession(false);

        // v. Not to mention, if the user is not logged in, the user will be redirected to the login page.
        if(session==null || session.getAttribute("userid")==null){
            request.getRequestDispatcher("/views/login.jsp").forward(request, response);
        }
        else {
            int uid = int session.getAttribute("userid");
            int wid = Integer.valueOf(request.getParameter("taskid"));
            Work work = WorkDAO.find(wid);
            // i. the task is closed therefore only a read-only access is provided (view only)
            if(work.status==closed){
                request.setAttribute("work", work);
                request.getRequestDispatcher("/views/view_only.jsp").forward(request, response);
            }
            // iv. the current user does not have access to the current task (never been assigned to this task). No access is provided (error).
            else if(
                    work.getUid()!= uid && work.getSnapshots().stream().filter(snapshot->snapshot.getUid()==uid).collect(Collectors.toList().size()==0)
            ){
                request.setAttribute("work", work);
                request.getRequestDispatcher("/views/error_no_access.jsp").forward(request, response);
            }
            //iii. the task is open but assigned to a different user (at some point current user had been assigned to this task). A read-only access is provided.
            else if(work.getUid()!= uid  && work.getSnapshots().stream().filter(snapshot->snapshot.getUid()==uid).collect(Collectors.toList().size()>0){
                request.setAttribute("work", work);
                request.getRequestDispatcher("/views/view_only.jsp").forward(request, response);
            }
            // ii. the task is open and assigned to the current user. Full access is provided (edit).
            else {
                request.setAttribute("work", work);
                request.getRequestDispatcher("/views/view_full_access.jsp").forward(request, response);
            }
        }
    }
}
