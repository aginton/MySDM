package sdm.servlets;

import models.inventoryitem.ePurchaseCategory;
import models.store.DiscountCondition;
import models.store.DiscountOffer;
import models.store.StoreDiscount;
import models.user.Customer;
import models.user.User;
import models.user.UserManager;
import models.zone.Zone;
import models.zone.ZonesManager;
import sdm.utils.ServletUtils;
import sdm.utils.SessionUtils;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "UseDiscountServlet", urlPatterns = { "/usediscount" })
public class UseDiscountServlet extends HttpServlet {

    private final String TAG = "UseDiscountServlet";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");

        String discountName = request.getParameter("discountName");
        String discountOperator = request.getParameter("discountOperator");
        String storeName = request.getParameter("storeName");
        String zoneName = request.getParameter("zoneName");
        String conditionId = request.getParameter("conditionItemId");
        String conditionQuantity = request.getParameter("conditionItemQuantity");

        System.out.println(TAG + " called");
        System.out.println(TAG + " - User wants to use " + discountName + " (" + discountOperator + ") at store " + storeName + " in zone " + zoneName);
        String offerChosen = "";
        if (!discountOperator.equals("ALL-OR-NOTHING")){
            offerChosen = request.getParameter("offer");
            System.out.println("The ONE-OF item chosen is: " + offerChosen);
        }

        UserManager userManager = ServletUtils.getUserManager(getServletContext());
        String username = SessionUtils.getUsername(request);
        Customer user = (Customer) userManager.getUserByName(username);
        ZonesManager zonesManager;
        zonesManager = ServletUtils.getZoneManager(getServletContext());
        Zone zone = zonesManager.getZoneByName(zoneName);
        synchronized (user){
            user.addDiscountBasedOnParams(zone,discountName,storeName,discountOperator,offerChosen );

            //user.askStoresForEntitledDiscounts();
        }


        System.out.println(TAG + " - finished, calling requestDispatcher for customercart");
        RequestDispatcher rd = request.getRequestDispatcher("customercart");
        rd.include(request,response);
        //System.out.println(discountChosen);
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
