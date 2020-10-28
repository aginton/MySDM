package sdm.servlets;

import com.google.gson.Gson;
import models.inventoryitem.ePurchaseCategory;
import models.order.*;
import models.store.DiscountCondition;
import models.store.DiscountOffer;
import models.store.Store;
import models.user.Customer;
import models.user.User;
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
import java.util.*;


//TODO: Change name to customercart
@WebServlet(name = "CurrentCustomerServlet", urlPatterns = { "/customercart" })
public class CustomerCurrentCartServlet extends HttpServlet {

    private final String TAG = "\nCurrentCustomerServlet";
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {


        //System.out.println("On CurrentUserServlet, processRequest");

        //returning JSON objects, not HTML
        response.setContentType("application/json");
        // System.out.println(TAG);
        try (PrintWriter out = response.getWriter()) {
            Gson gson = new Gson();
            UserManager userManager = ServletUtils.getUserManager(getServletContext());
            String username = SessionUtils.getUsername(request);
            Customer customer = (Customer) userManager.getUserByName(username);
            //System.out.println(TAG + "Current customer = " + customer);
            CartJS cartJS = new CartJS(customer.getCurrentCart());
            String json = gson.toJson(cartJS);

            out.println(json);
            out.flush();
        }
    }


    private static class CartJS{
        private Set<CartItemJS> cartItems;
        private Set<UsedDiscountJS> usedDiscounts;
        private Set<DiscountOverview> discountOverviews;
        private HashMap<String,Integer> mapDiscountNamesToTimesUsed;
        private double subtotal;
        private int numberItems;
        private int numberItemsByType;
        private Set<StoreDetailsJS> participatingStores;

        public CartJS(Cart cart){
            //System.out.println(" CARTJS for cart with cartItems = " + cart.getCartItems() + " and usedDiscounts = " + cart.getUsedDiscounts());
            //System.out.println("CURRENTCUSTOMERSERVLET LINE 83");
            System.out.println("in cartJS constructor");
            numberItems = cart.getTotalNumberItems();
            numberItemsByType = cart.getTotalNumberItemsByType();
            subtotal = cart.getSubtotal();
            cartItems = new HashSet<>();
            usedDiscounts = new HashSet<>();
            discountOverviews = new HashSet<>();
            participatingStores = new HashSet<>();

            System.out.println("BBBBBBBBB");
            cart.getCartItems().forEach(cartItem -> {
                //  System.out.println("CurrentCustomerServlet LINE 90");
                cartItems.add(new CartItemJS(cartItem));
                //System.out.println("CurrentCustomerServlet LINE 92");
            });
            cart.getUsedDiscounts().forEach(usedDiscount -> {
                //System.out.println("CurrentCustomerServlet LINE 95");
                usedDiscounts.add(new UsedDiscountJS(usedDiscount));
                //System.out.println("CurrentCustomerServlet LINE 97");
            });
            mapDiscountNamesToTimesUsed = cart.getMapDiscountNamesToTimesUsed();
            for (String discountName: mapDiscountNamesToTimesUsed.keySet()){
                DiscountCondition condition = getConditionByDiscountName(discountName, cart.getUsedDiscounts());
                discountOverviews.add(new DiscountOverview(discountName,condition,mapDiscountNamesToTimesUsed.get(discountName)));

            }
            System.out.println("before updating names of participating stores");
            cart.getCartItems().forEach(cartItem -> {
                int storeIdForItem = cartItem.getStore().getStoreId();
                String storeNameForItem = cartItem.getStore().getStoreName();
                StoreDetailsJS storeToAdd = new StoreDetailsJS(storeIdForItem,storeNameForItem);
                participatingStores.add(storeToAdd);
            });
            System.out.println("the set: " + participatingStores);
        }

        private DiscountCondition getConditionByDiscountName(String discountName, Set<UsedDiscount> usedDiscounts) {
            DiscountCondition condition = usedDiscounts.stream().filter(d->d.getDiscountName().equals(discountName)).map(d->d.getCondition()).findFirst().orElse(null);
            return condition;
        }
    }

    private static class DiscountOverview{
        private String discountName;
        private ConditionJS condition;
        private int timesUsed;

        public DiscountOverview(String discountName, DiscountCondition condition, Integer timesUsed) {
            this.discountName=discountName;
            this.condition=new ConditionJS(condition);
            this.timesUsed = timesUsed;
        }
    }

    private static class ConditionJS{
        private int id;
        private String name;
        private String category;
        private double quantity;

        public ConditionJS(DiscountCondition condition){
            id = condition.getIfYouBuyItem().getItemID();
            name = condition.getIfYouBuyItem().getItemName();
            category = condition.getIfYouBuyItem().getPurchaseCategory()==ePurchaseCategory.QUANTITY?"quantity":"weight";
            quantity=condition.getQuantity();
        }
    }

    private static class CartItemJS{
        private int id;
        private String name;
        private int price;
        private double amount;
        private String category;
        private String store;

        public CartItemJS(CartItem cartItem){
            //System.out.println("CurrentCustomerServlet LINE 111");
            id = cartItem.getItemID();
            name = cartItem.getItemName();
            price = cartItem.getPrice();
            amount = cartItem.getAmount();
            category = cartItem.getPurchaseCategory()== ePurchaseCategory.QUANTITY? "quantity": "weight";
            this.store=cartItem.getStore().getStoreName();
        }
    }

    private static class UsedDiscountJS{
        private String discountName;
        private String storeName;
        private String discountOperator;
        private Set<DiscountOfferJS> offersChosen;
        private int timesUsed;
        private int discountUsedRef;


        public UsedDiscountJS(UsedDiscount usedDiscount) {
            //System.out.println("CurrentCustomerServlet LINE 128");
            discountName = usedDiscount.getDiscountName();
            discountOperator = usedDiscount.getDiscountOperator();
            storeName = usedDiscount.getStore().getStoreName();
            offersChosen = new HashSet<>();
            //System.out.println("CurrentCustomerServlet LINE 133");
            for (DiscountOffer offer: usedDiscount.getOffersChosen()){
                //  System.out.println("CurrentCustomerServlet LINE 135");
                offersChosen.add(new DiscountOfferJS(offer));
            }
            timesUsed = usedDiscount.getTimesUsed();
            discountUsedRef = usedDiscount.getDiscountUsedRef();
            //System.out.println("CurrentCustomerServlet LINE 139");
        }
    }

    private static class DiscountOfferJS{
        private int id;
        private String name;
        private int forAdditional;
        private double quantity;
        private String category;

        public DiscountOfferJS(DiscountOffer offer) {
            //System.out.println("CurrentCustomerServlet LINE 148");
            id = offer.getOfferItem().getItemID();
            name = offer.getOfferItem().getItemName();
            category = offer.getOfferItem().getPurchaseCategory()== ePurchaseCategory.QUANTITY? "quantity": "weight";
            quantity = offer.getQuantity();
            forAdditional = offer.getForAdditional();
            //System.out.println("CurrentCustomerServlet LINE 154");
        }
    }

    private static class StoreDetailsJS {

        private int id;
        private String name;

        public StoreDetailsJS(int id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof StoreDetailsJS)) return false;
            StoreDetailsJS that = (StoreDetailsJS) o;
            return id == that.id &&
                    name.equals(that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, name);
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