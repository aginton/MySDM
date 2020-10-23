package sdm.servlets;

import com.google.gson.Gson;
import models.user.Customer;
import models.user.User;
import models.user.UserManager;
import models.zone.Zone;
import models.zone.ZonesManager;
import sdm.utils.ServletUtils;
import sdm.utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "ConfirmOrderServlet", urlPatterns = { "/confirmorder" })
public class ConfirmOrderServlet extends HttpServlet {

    private final String TAG = "ConfirmOrderServlet";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        System.out.println(TAG + " - started!");
        String xString = request.getParameter("x-position");
        int x = Integer.parseInt(xString);
        String yString = request.getParameter("y-position");
        int y = Integer.parseInt(yString);
        int[] loc = new int[2];
        loc[0] = x;
        loc[1] = y;
        String zoneName = request.getParameter("zoneName");
        String orderType = request.getParameter("orderType");
        String username = SessionUtils.getUsername(request);


        UserManager userManager = ServletUtils.getUserManager(getServletContext());
        Customer user = (Customer) userManager.getUserByName(username);
        ZonesManager zonesManager = ServletUtils.getZoneManager(getServletContext());
        Zone zone = zonesManager.getZoneByName(zoneName);

        boolean isSuccess;
        boolean isValidLocation = zonesManager.isValidLocation(loc);
        if (isValidLocation){
            if (user.getCurrentCart().getCartItems().size() > 0){
                zone.confirmOrderForUser(user, orderType, loc);
            }
            isSuccess = true;
        } else{
            isSuccess = false;
        }
        Gson gson = new Gson();
        try (PrintWriter out = response.getWriter()){
            out.println(gson.toJson(isSuccess));
            out.flush();
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
