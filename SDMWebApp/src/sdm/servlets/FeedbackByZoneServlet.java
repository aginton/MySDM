package sdm.servlets;

import com.google.gson.Gson;
import models.store.Feedback;
import models.user.Customer;
import models.user.Owner;
import models.user.UserManager;
import sdm.utils.ServletUtils;
import sdm.utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



@WebServlet(name = "FeedbackByZoneServlet", urlPatterns = { "/feedbackbyzone" })
public class FeedbackByZoneServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {


        //System.out.println("\ncalled UsersListServlet.processRequest()");

        //returning JSON objects, not HTML
        response.setContentType("application/json");
        try (PrintWriter out = response.getWriter()) {
            Gson gson = new Gson();
            UserManager userManager = ServletUtils.getUserManager(getServletContext());
            String username = SessionUtils.getUsername(request);
            Owner owner = (Owner) userManager.getUserByName(username);
            String zoneName = request.getParameter("zone");
            List<Feedback> feedbacksByZone = owner.getFeedbacksForZone(zoneName);
            List<FeedbackJS> feedbacksJS = new ArrayList<>();
            for (Feedback feedback: feedbacksByZone){
                feedbacksJS.add(new FeedbackJS(feedback));
            }
            String json = gson.toJson(feedbacksJS);
            out.println(json);
            out.flush();
        }
    }

    private static class FeedbackJS{
        private String customer;
        private String store;
        private String comments;
        private String date;
        private int rating;

        public FeedbackJS(Feedback feedback){
            this.comments=feedback.getComment();
            this.customer=feedback.getCustomer();
            this.rating=feedback.getRating();
            this.store=feedback.getStore();
            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm");
            this.date = dateFormat.format(feedback.getDate());
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
