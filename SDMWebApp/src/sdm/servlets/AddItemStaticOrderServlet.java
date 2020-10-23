package sdm.servlets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "AddItemStaticOrderServlet", urlPatterns = { "/additemstaticorder" })
public class AddItemStaticOrderServlet extends HttpServlet {
    private final String TAG = "AddItemsToCartInProgressServlet";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("\n----------------------------------------\n" + TAG + " - processRequest");

        String idStr = request.getParameter("id");
        int id = Integer.parseInt(idStr);
        String storeName = request.getParameter("store");
        String zoneName = request.getParameter("zone");

        UserManager userManager = ServletUtils.getUserManager(getServletContext());
        String username = SessionUtils.getUsername(request);
        Customer customer = (Customer) userManager.getUserByName(username);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        //UserManager userManager;
        ZonesManager zonesManager;
        zonesManager = ServletUtils.getZoneManager(getServletContext());
        Zone zone = zonesManager.getZoneByName(zoneName);
        Store store = zone.getStoreByName(storeName);
        InventoryItem inventoryItem = store.getInventoryItemById(id);

        if (inventoryItem != null){
            //int price = store.getMapItemsToPrices().get(inventoryItem);
            synchronized (customer){
                customer.getCurrentCart().addCartItem(id,1,store);
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