package sdm.servlets;

import com.google.gson.Gson;
import models.inventoryitem.ePurchaseCategory;
import models.order.Cart;
import models.order.CartItem;
import models.order.UsedDiscount;
import models.store.DiscountCondition;
import models.store.DiscountOffer;
import models.store.Store;
import models.store.StoreDiscount;
import models.user.Customer;
import models.user.User;
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
import java.util.*;
import java.util.stream.Collectors;

@WebServlet(name = "ChooseDiscountsServlet", urlPatterns = { "/choosediscounts" })
public class ChooseDiscountsServlet extends HttpServlet {
    private final String TAG = "ChooseDiscountsServlet";

    private void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        //System.out.println("\n------------------------------------------------------------\n" + TAG + " - getting discounts available for user");

        Gson gson = new Gson();
        String json = null;
        String zoneName = request.getParameter("zone");
        String orderType = request.getParameter("orderTypeStarted");
        Zone zone = ServletUtils.getZoneManager(getServletContext()).getZoneByName(zoneName);
        UserManager userManager = ServletUtils.getUserManager(getServletContext());
        String username = SessionUtils.getUsername(request);
        Customer customer = (Customer) userManager.getUserByName(username);

        HashMap<StoreDiscount,Integer> entitledDiscountsBasedOnCart = getEntitledDiscountsForCart(customer.getCurrentCart());
        //System.out.println(TAG + " - LINE 50");
        List<EntitledDiscountJS> discountsUserEntitledTo = new ArrayList<>();
        entitledDiscountsBasedOnCart.forEach((discount,times)->{
            if (times > 0){
                discountsUserEntitledTo.add(new EntitledDiscountJS(discount,times));
            }
        });

        //EntitledDiscountsJS entitledDiscountsJS = new EntitledDiscountsJS(entitledDiscountsBasedOnCart);
        json = gson.toJson(discountsUserEntitledTo);
        //System.out.println(TAG + " - LINE 52");

        try (PrintWriter out = response.getWriter()) {
            out.println(json);
            out.flush();
        }
    }

    private HashMap<StoreDiscount, Integer> getEntitledDiscountsForCart(Cart cartInProgress) {
        HashMap<StoreDiscount, Integer> res = new HashMap<>();
        HashMap<StoreDiscount, Integer> mapDiscountsToTimesEntitled = new HashMap<>();
        HashMap<Store, Cart> mapStoresToCarts = new HashMap<>();
        Set<CartItem> regularItems = cartInProgress.getCartItems();
        //System.out.println("regularItems = " + regularItems);

        //System.out.println(TAG + " - LINE 67");
        for (CartItem item: regularItems){
            if (!mapStoresToCarts.containsKey(item.getStore())){
                mapStoresToCarts.put(item.getStore(), new Cart());
            }
            mapStoresToCarts.get(item.getStore()).addItemToCart(item);
        }
        //System.out.println(TAG + " - LINE 74");
        mapStoresToCarts.forEach((store,cart)->{
            //System.out.println(TAG + " - LINE 77");
            HashMap<StoreDiscount, Integer> mapDiscountsToEntitledItemsForStore = store.getEntitledDiscounts(cart.getCartItems());
            for (StoreDiscount storeDiscount: mapDiscountsToEntitledItemsForStore.keySet()){
                int num = mapDiscountsToEntitledItemsForStore.get(storeDiscount);
                mapDiscountsToTimesEntitled.put(storeDiscount,num);
            }
          //  System.out.println(TAG + " - LINE 81");
        });

        //System.out.println("\n\nBased on current cart, user should be entitled to: \n" + mapDiscountsToTimesEntitled);

        res = getRemainingTimesEntitledToDiscount(mapDiscountsToTimesEntitled, cartInProgress);
        //System.out.println("\n\nAfter considering discounts used, result is\n " + res);
        return res;
    }

    private HashMap<StoreDiscount, Integer> getRemainingTimesEntitledToDiscount(HashMap<StoreDiscount, Integer> mapDiscountsToTimesEntitled, Cart cartInProgress) {
        HashMap<StoreDiscount, Integer> res = new HashMap<>();
        //System.out.println("LINE 92");
        for (StoreDiscount storeDiscount: mapDiscountsToTimesEntitled.keySet()){
            int timesUsed = 0;
            for (UsedDiscount discountUsed: cartInProgress.getUsedDiscounts()){
                if (discountUsed.getDiscountName().equals(storeDiscount.getDiscountName())
                        && discountUsed.getStore().getStoreName().equals(storeDiscount.getStore().getStoreName())){
                    timesUsed += discountUsed.getTimesUsed();
                }
            }
            int remaining = mapDiscountsToTimesEntitled.get(storeDiscount) - timesUsed;
          //  System.out.println("LINE 12");
            res.put(storeDiscount,remaining);
        }
        return res;
    }


    private static class EntitledDiscountsJS{

        private Set<EntitledDiscountJS> entitledDiscountJS;

        public EntitledDiscountsJS(HashMap<StoreDiscount, Integer> entitledDiscountsBasedOnCart) {
            entitledDiscountJS = new HashSet<>();
            entitledDiscountsBasedOnCart.forEach((discount,times)->entitledDiscountJS.add(new EntitledDiscountJS(discount,times)));
        }
    }

    private static class EntitledDiscountJS{
        private String store;
        private String discountName;
        private IfYouBuyJS discountCondition;
        private OffersJS discountOffers;
        private int entitledTimes;

        public EntitledDiscountJS(StoreDiscount discount, Integer times) {
            store=discount.getStore().getStoreName();
            discountName=discount.getDiscountName();
            discountCondition= new IfYouBuyJS(discount.getDiscountCondition());
            discountOffers=new OffersJS(discount.getDiscountOffers(),discount.getDiscountOperator());
            entitledTimes = times;
        }
    }

    private static class IfYouBuyJS{
        private int id;
        private String name;
        private String category;
        private double quantity;

        public IfYouBuyJS(DiscountCondition discountCondition) {
            //System.out.println("LINE 141");
            this.id = discountCondition.getIfYouBuyItem().getItemID();
            this.name = discountCondition.getIfYouBuyItem().getItemName();
            this.quantity = discountCondition.getQuantity();
            this.category = discountCondition.getIfYouBuyItem().getPurchaseCategory() == ePurchaseCategory.QUANTITY? "quantity" : "weight";
        }
    }

    private static class OffersJS{
        private String operator;
        private List<OfferJS> offers;

        public OffersJS(Set<DiscountOffer> discountOffers,String operator) {
            this.operator=operator;
            offers = new ArrayList<>();
            discountOffers.forEach(offer->{
                offers.add(new OfferJS(offer));
            });
        }
    }

    private static class OfferJS{
        private double quantity;
        private int id;
        private String name;
        private int forAdditional;
        private String category;

        public OfferJS(DiscountOffer offer) {
            //System.out.println("LINE 170");
            this.id = offer.getOfferItem().getItemID();
            this.name = offer.getOfferItem().getItemName();
            this.category = offer.getOfferItem().getPurchaseCategory() == ePurchaseCategory.QUANTITY? "quantity" : "weight";
            this.quantity = offer.getQuantity();
            this.forAdditional = offer.getForAdditional();
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
