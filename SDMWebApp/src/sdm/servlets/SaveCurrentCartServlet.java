package sdm.servlets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import models.order.UsedDiscount;
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
import java.util.List;
import java.util.Set;

@WebServlet(name = "SaveCurrentCartServlet", urlPatterns = { "/savecurrentcart" })

public class SaveCurrentCartServlet extends HttpServlet {
    private final String TAG = "SaveCartInProgressServlet";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");

        System.out.println("\n------------------------------------------------\n" + TAG);

        String cart = request.getParameter("cart");
        String zoneName = request.getParameter("zone");
        System.out.println(TAG + " LINE 34");

        UserManager userManager = ServletUtils.getUserManager(getServletContext());
        String username = SessionUtils.getUsername(request);
        Customer user = (Customer) userManager.getUserByName(username);

        System.out.println(TAG + " LINE 40");

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        //UserManager userManager;
        ZonesManager zonesManager;
        zonesManager = ServletUtils.getZoneManager(getServletContext());

        Zone zone = zonesManager.getZoneByName(zoneName);

        System.out.println(TAG + " LINE 50");

        System.out.println(TAG + " LINE 55");
        Item[] items = gson.fromJson(cart,Item[].class);
        System.out.println("items is:...." + items);

        synchronized (user){
            user.getCurrentCart().getCartItems().clear();
            user.getCurrentCart().setTotalNumberItems(0);
            user.getCurrentCart().setTotalNumberItemsByType(0);
            for (Item item: items){
                System.out.println(TAG + " - received following item\n" + item);
                Store store = zone.getStoreByName(item.store);
                user.getCurrentCart().addCartItem(item.id, item.amount,store);
            }
        }
        System.out.println(TAG + " updated user cart!");
        //TODO: Check if need to return something here
        RequestDispatcher rd = request.getRequestDispatcher("customercart");
        rd.include(request,response);
    }

    private static class Item{
        private int id;
        private double amount;
        private String store;
        private int price;
        private String category;

        @Override
        public String toString() {
            return "Item{" +
                    "id=" + id +
                    ", amount=" + amount +
                    ", store='" + store + '\'' +
                    ", price=" + price +
                    ", category='" + category + '\'' +
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
