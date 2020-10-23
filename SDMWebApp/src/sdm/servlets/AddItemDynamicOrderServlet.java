package sdm.servlets;

import models.inventoryitem.InventoryItem;
import models.order.CartItem;
import models.store.Store;
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


@WebServlet(name = "AddItemDynamicOrderServlet", urlPatterns = { "/additemdynamicorder" })
public class AddItemDynamicOrderServlet extends HttpServlet {
    private final String TAG = "DynamicOrderAddItem";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {


        System.out.println("\n----------------------------------------\n" + TAG + " - processRequest");


        String idStr = request.getParameter("id");

        System.out.println("idStr = " + idStr);
        int id = Integer.parseInt(idStr);

        String zoneName = request.getParameter("zone");

        UserManager userManager = ServletUtils.getUserManager(getServletContext());
        String username = SessionUtils.getUsername(request);
        Customer customer = (Customer) userManager.getUserByName(username);

        ZonesManager zonesManager;
        zonesManager = ServletUtils.getZoneManager(getServletContext());
        Zone zone = zonesManager.getZoneByName(zoneName);

        System.out.println(TAG + " - LINE 52");
        synchronized (zone){
            Store cheapestStore = zone.findCheapestStoreForItem(id);
            if (cheapestStore != null){
//                int price = cheapestStore.getMapItemsToPrices().get(inventoryItem);
//                customer.getCurrentCart().addCartItem(inventoryItem,1,price,cheapestStore);
                customer.getCurrentCart().addCartItem(id,1,cheapestStore);
            }
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
