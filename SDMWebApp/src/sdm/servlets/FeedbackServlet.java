package sdm.servlets;

import com.google.gson.*;
import models.order.Feedback;
import models.order.eRate;
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
import java.util.List;
import java.util.Map;

@WebServlet(name = "FeedbackServlet", urlPatterns = { "/leavefeedback" })
public class FeedbackServlet extends HttpServlet {

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

        System.out.println("FeedbackServlet called");
        response.setContentType("application/json");

        //parsing json data in request
        String customerName = request.getParameter("customer");
        String zoneName = request.getParameter("zone");
        System.out.println("data parsed in servlet, customer name: " + customerName + " " + zoneName);


        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String feedbacks = request.getParameter("feedbacks");
        System.out.println("is feedbacks null?");
        System.out.println(feedbacks == null);


//        for (FeedbackJS fb : feedbacksjs) {
//            System.out.println(fb);
//        }




//        List<String> feedbacksArr = Arrays.asList(feedbacks);
//        for(String part: feedbacksArr) { System.out.println(part); }

        //List<String> parameterNames = new ArrayList<>(request.getParameterMap().keySet());
        //System.out.println(parameterNames);
//        List<String> itemsToPricesList = Arrays.asList(itemsToPricesArr);
//        for(String part: itemsToPricesList) {
//            System.out.println(part);
//        }

        //load the zone manager
        ZonesManager zonesManager = ServletUtils.getZoneManager(getServletContext());
        Zone zone = zonesManager.getZoneByName(zoneName);
        System.out.println("zone manager loaded");

        //load the user manager
        UserManager userManager = ServletUtils.getUserManager(getServletContext());
        System.out.println("user manager loaded");

        //2. for each feedback:
        //2.1 store name->search store by storename and get its owner
        //2.2


        //response
//        Gson gson = new Gson();
//        jsonResponse jsonResponse = new jsonResponse(wasStoreCreated,isLocationAvailable, loc, storeName, numOfItemsSoldInStore, totalNumOfItemsInZone);
//        String jsonObject = gson.toJson(jsonResponse);
//
//        try (PrintWriter out = response.getWriter()) {
//            out.print(jsonObject);
//            out.flush();
//        }
    }

     private static class FeedbackWrapperJS {

        FeedbackJS[] feedbacks;

     }

     private static class FeedbackJS {

        String storename;
        String rate;
        String comments;

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


