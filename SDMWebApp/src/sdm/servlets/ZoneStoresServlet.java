package sdm.servlets;

import com.google.gson.Gson;
import models.inventoryitem.InventoryItem;
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@WebServlet(name = "ZoneStoresServlet", urlPatterns = { "/zonestores" })
public class ZoneStoresServlet extends HttpServlet {
    private final String TAG = "ZoneStoresServlet";

    private void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException{
        response.setContentType("application/json");
        String zoneName = request.getParameter("zone");
        System.out.println(TAG + " -   processRequest for zone " + zoneName);
        Zone zone;

        synchronized (getServletContext()){
            zone = ServletUtils.getZoneManager(getServletContext()).getZoneByName(zoneName);
        }
        try (PrintWriter out = response.getWriter()) {
            Gson gson = new Gson();
            Set<StoreJS> storeSummaries = new HashSet<>();
            zone.getStores().forEach(store->{
                storeSummaries.add(new StoreJS(store));
            });
            String json = gson.toJson(storeSummaries);
            out.println(json);
            out.flush();
        }



    }

    private static class StoreJS {

        final private int storeID;
        final private String owner;
        final private String storeName;
        final private List<StoreItemJS> storeItems;
        final private int numberOfOrders;
        final private double salesIncome;
        final private int ppk;
        final private double deliveryIncome;

        public StoreJS(Store store) {
            //System.out.println("LINE62");
            this.storeID = store.getStoreId();
            this.owner = store.getOwner().getName();
            //System.out.println("Store owner name: " + this.owner);
            this.storeName = store.getStoreName();
            this.numberOfOrders = store.getOrders().size();
            this.salesIncome = store.getTotalSalesIncome();
            this.deliveryIncome = store.getTotalDeliveryIncome();
            this.ppk = store.getPpk();
            storeItems = new ArrayList<>();
            //System.out.println("LINE72");
            store.getStoreItems().forEach(storeItem -> {
                int unitPrice = store.getMapItemsToPrices().get(storeItem);
                double totalSoldAtStore = store.getMapItemsToTotalSold().get(storeItem);
              //  System.out.println("LINE76");
                storeItems.add(new StoreItemJS(storeItem,unitPrice,totalSoldAtStore));
            });
            //System.out.println("LINE78");
        }

    }

    private static class StoreItemJS {
        final private int id;
        final private String name;
        final private String category;
        final private int unitPrice;
        final private double totalSoldAtStore;

        public StoreItemJS(InventoryItem storeItem, int unitPrice, double totalSoldAtStore) {
            //System.out.println("LINE88");
            this.id=storeItem.getItemID();
            this.name = storeItem.getItemName();
            this.category=storeItem.getPurchaseCategory().name();
            this.unitPrice=unitPrice;
            this.totalSoldAtStore = totalSoldAtStore;
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
