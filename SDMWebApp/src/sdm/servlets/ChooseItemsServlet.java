package sdm.servlets;

import com.google.gson.Gson;
import models.inventoryitem.InventoryItem;
import models.inventoryitem.ePurchaseCategory;
import models.store.Store;
import models.zone.Zone;
import sdm.utils.ServletUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

@WebServlet(name = "ChooseItemsServlet", urlPatterns = { "/chooseitems" })
public class ChooseItemsServlet extends HttpServlet {

    private void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException,IOException{

        response.setContentType("application/json");
        Gson gson = new Gson();
        String json = null;
        System.out.println("blahblah");
        String zoneName = request.getParameter("zone");
        String orderType = request.getParameter("orderTypeStarted");
        Zone zone = ServletUtils.getZoneManager(getServletContext()).getZoneByName(zoneName);

        Set<ItemJS> storeItemsForJSON = new HashSet<>();
        String storeName = request.getParameter("store");
        System.out.println("Inside StoreItemsServlet, processRequest for static order, store " + storeName);

        if (orderType.equals("static")){
            Store store = zone.getStoreByName(storeName);
            store.getStoreItems().forEach(storeItem -> {
                int price = store.getMapItemsToPrices().get(storeItem);
                storeItemsForJSON.add(new ItemJS(storeItem, price));
            });
            json = gson.toJson(storeItemsForJSON);

        } else if (orderType.equals("dynamic")){
            System.out.println("Inside StoreItemsServlet, processRequest for dynamic order");
            zone.getItemsSoldInZone().forEach(item -> {
                storeItemsForJSON.add(new ItemJS(item, -1));
            });
            json = gson.toJson(storeItemsForJSON);
        } else{
            json = "Error at StoreItemsServlet: orderTypeStarted must be static or dynamic";
        }

        try (PrintWriter out = response.getWriter()) {
            out.println(json);
            out.flush();
        }
    }

    private static class ItemJS {
        private final int id;
        private final String name;
        private final String category;
        private final int price;

        public ItemJS(InventoryItem item, int price) {
            id = item.getItemID();
            this.price=price;
            name = item.getItemName();
            category = item.getPurchaseCategory()== ePurchaseCategory.WEIGHT? "Weight": "Quantity";
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
