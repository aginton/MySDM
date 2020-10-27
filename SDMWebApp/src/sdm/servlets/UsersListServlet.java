package sdm.servlets;

import com.google.gson.Gson;
import models.user.User;
import models.user.UserManager;
import sdm.utils.ServletUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

@WebServlet(name = "UsersListServlet", urlPatterns = { "/userslist" })
public class UsersListServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {


        //System.out.println("\ncalled UsersListServlet.processRequest()");

        //returning JSON objects, not HTML
        response.setContentType("application/json");
        try (PrintWriter out = response.getWriter()) {
            Gson gson = new Gson();
            UserManager userManager = ServletUtils.getUserManager(getServletContext());
            OnlineUsersJS onlineUsersJS = new OnlineUsersJS(userManager);
//            Set<String> usersList = userManager.getUserNames();
            //HashMap<String,String> usersList = userManager.getOnlineUsers();
            String json = gson.toJson(onlineUsersJS);
            out.println(json);
            out.flush();
        }
    }

    private static class OnlineUsersJS{
        private int onlineVersion;
        private List<OnlineUser> onlineUsers;

        public OnlineUsersJS(UserManager userManager){
            onlineVersion = userManager.getOnlineVersion();
            onlineUsers = new ArrayList<>();
            HashMap<String,String> onlineUsersAndRoles = userManager.getOnlineUsers();
            for (String username: onlineUsersAndRoles.keySet()){
                onlineUsers.add(new OnlineUser(username,onlineUsersAndRoles.get(username)));
            }
        }
    }

    private static class OnlineUser{
        private String name;
        private String role;

        public OnlineUser(String name, String role){
            this.name=name;
            this.role=role;
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
