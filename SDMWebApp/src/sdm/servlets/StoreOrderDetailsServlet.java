package sdm.servlets;

import com.google.gson.Gson;
import models.inventoryitem.InventoryItem;
import models.inventoryitem.ePurchaseCategory;
import models.order.*;
import models.store.DiscountOffer;
import models.store.Store;
import models.user.Customer;
import models.user.UserManager;
import models.zone.Zone;
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


@WebServlet(name = "StoreOrderDetailsServlet", urlPatterns = { "/storeorderdetails" })

public class StoreOrderDetailsServlet extends HttpServlet {

    private final String TAG = "StoreOrderDetailsServlet";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {


        //System.out.println("On CurrentUserServlet, processRequest");

        //returning JSON objects, not HTML
        response.setContentType("application/json");
        try (PrintWriter out = response.getWriter()) {
            Gson gson = new Gson();

            UserManager userManager = ServletUtils.getUserManager(getServletContext());
            String username = SessionUtils.getUsername(request);
            String role = SessionUtils.getRole(request);
            if (role.equals("owner")){
                String storeName = request.getParameter("storeName");
                String idStr = request.getParameter("id");
                String subIdStr = request.getParameter("subId");
                String orderTypeStr = request.getParameter("orderType");
                System.out.println(TAG + " - need to fetch details for " + orderTypeStr + " order " + idStr + subIdStr);
                int id = Integer.parseInt(idStr);
                int subId = Integer.parseInt(subIdStr);
                eOrderType orderType = orderTypeStr.equals("static")? eOrderType.STATIC_ORDER: eOrderType.DYNAMIC_ORDER;

                String zoneName = request.getParameter("zone");
                Zone zone = ServletUtils.getZoneManager(getServletContext()).getZoneByName(zoneName);
                Store store = zone.getStoreByName(storeName);
                if (store != null){
                    Order order = store.getOrderByParams(id,subId,orderType);
                    List<ItemDetails> orderDetails = new ArrayList<>();
                    for (Cart cart: order.getMapStoresToCarts().values()){
                        //public ItemDetails(int id, String name, ePurchaseCategory purchaseCategory, Store store, double amount, double unitPrice, double cost, String isPartOfDiscount){
                        for (CartItem cartItem: cart.getCartItems()){
                            int itemId = cartItem.getItemID();
                            int unitPrice = cartItem.getStore().getPriceByItemId(itemId);
                            double cost = unitPrice*cartItem.getAmount();
                            orderDetails.add(new ItemDetails(cartItem.getItemID(),cartItem.getItemName(),cartItem.getPurchaseCategory(),cartItem.getStore(),
                                    cartItem.getAmount(),unitPrice,cost,"no"));
                        }

                        for (UsedDiscount usedDiscount: cart.getUsedDiscounts()){
                            for (DiscountOffer offer: usedDiscount.getOffersChosen()){
                                InventoryItem inventoryItem = offer.getOfferItem();
                                double unitPrice = offer.getForAdditional() / offer.getQuantity();
                                double amount = offer.getQuantity() * usedDiscount.getTimesUsed();
                                double cost = offer.getForAdditional()*usedDiscount.getTimesUsed();
                                String isPartOfDiscount = "yes (" + usedDiscount.getDiscountName() + ")";
                                orderDetails.add(new ItemDetails(inventoryItem.getItemID(),inventoryItem.getItemName(),inventoryItem.getPurchaseCategory(),usedDiscount.getStore(),
                                        amount,unitPrice,cost,isPartOfDiscount));
                            }
                        }
                    }

                    String json = gson.toJson(orderDetails);
                    out.println(json);
                    out.flush();
                    return;
                }
            }
        }
    }


    private static class ItemDetails{
        private String id;
        private String name;
        private String category;
        private String storeOrderedFrom;
        private String amountOrdered;
        private String unitPrice;
        private String cost;
        private String isPartOfDiscount;

        public ItemDetails(int id, String name, ePurchaseCategory purchaseCategory, Store store, double amount, double unitPrice, double cost, String isPartOfDiscount){
            this.id = String.valueOf(id);
            this.name = name;
            this.category = purchaseCategory== ePurchaseCategory.QUANTITY? "quantity" : "weight";
            StringBuilder sbStore = new StringBuilder();
            sbStore.append("(").append(String.valueOf(store.getStoreId())).append(") ").append(store.getStoreName());
            this.storeOrderedFrom = sbStore.toString();
            this.unitPrice =String.format("%.2f",unitPrice);

            StringBuilder sbAmount = new StringBuilder(String.format("%.2f ",amount));
            StringBuilder sbUnitCost = new StringBuilder(String.format("%.2f",unitPrice));

            if (this.category.equals("quantity")){
                sbAmount.append("pcks");
                sbUnitCost.append("/pck");
            }
            this.amountOrdered = sbAmount.toString();

            double val = amount*unitPrice;
            this.cost=String.format("%.2f",val);
            this.isPartOfDiscount = isPartOfDiscount;
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

