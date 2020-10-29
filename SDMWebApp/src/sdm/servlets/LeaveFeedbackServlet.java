package sdm.servlets;

import com.google.gson.*;
import models.order.eRate;
import models.store.Feedback;
import models.store.Store;
import models.user.Owner;
import models.user.UserManager;
import models.zone.Zone;
import models.zone.ZonesManager;
import sdm.utils.ServletUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

@WebServlet(name = "FeedbackServlet", urlPatterns = { "/leavefeedback" })
public class LeaveFeedbackServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    /*data arrives to servlet in format:
    {
    "customer": "Moshe",
    "zone": "HaSharon",
    "date": "???",
    "feedbacks": [{"storeName": "Maxstock",
                   "rate": "4",
                   "comments: "Great experience!"}, ...]
    }
    */

        System.out.println("LeaveFeedbackServlet called");
        //response.setContentType("application/json");

        //parsing json data in request
        String customerName = request.getParameter("customer");
        String zoneName = request.getParameter("zone");
        System.out.println("data parsed in servlet, customer name: " + customerName + " " + zoneName);


        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String feedbacks = request.getParameter("feedbacks");
        System.out.println("is feedbacks null?: ");
        System.out.println(feedbacks==null);
        System.out.println("Now what...");
        FeedbackJS[] items = gson.fromJson(feedbacks, FeedbackJS[].class);
        System.out.println("We made it");
        System.out.println("FeedbackJS[] size is: " + items.length);

        ZonesManager zonesManager = ServletUtils.getZoneManager(getServletContext());
        Zone zone = zonesManager.getZoneByName(zoneName);
        for (FeedbackJS feedbackJS: items){
            System.out.println(feedbackJS);
            String storeName = feedbackJS.getStorename();
            Store store = zone.getStoreByName(storeName);
            Feedback feedback = new Feedback(customerName,storeName,feedbackJS.getRate(),feedbackJS.getComments());
            store.addFeedbackAndNotifyOwner(feedback);
        }
    }

     private static class FeedbackJS {

        private String storename;
        private int rate;
        private String comments;

         public String getStorename() {
             return storename;
         }

         public int getRate() {
             return rate;
         }

         public String getComments() {
             return comments;
         }

         @Override
         public String toString() {
             return "FeedbackJS{" +
                     "storeName='" + storename + '\'' +
                     ", rate=" + rate +
                     ", comments='" + comments + '\'' +
                     '}';
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


