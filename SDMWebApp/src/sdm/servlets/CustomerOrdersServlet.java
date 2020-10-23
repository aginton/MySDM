package sdm.servlets;

import com.google.gson.Gson;
import models.order.*;
import models.store.Store;
import models.user.Customer;
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
@WebServlet(name = "CustomerOrdersServlet", urlPatterns = { "/customerorders" })
public class CustomerOrdersServlet extends HttpServlet {

    private final String TAG = "\nCurrentCustomerServlet";
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {


        //System.out.println("On CurrentUserServlet, processRequest");

        response.setContentType("application/json");
        System.out.println(TAG);
        try (PrintWriter out = response.getWriter()) {
            Gson gson = new Gson();
            UserManager userManager = ServletUtils.getUserManager(getServletContext());
            String username = SessionUtils.getUsername(request);
            Customer customer = (Customer) userManager.getUserByName(username);
            List<OrderJS> ordersJS = new ArrayList<>();
            for (Order order: customer.getOrders()){
                ordersJS.add(new OrderJS(order));
            }
            String json = gson.toJson(ordersJS);
            out.println(json);
            out.flush();
        }
    }

    private static class OrderJS{
        private JSOrderId orderId;
        private String date;
        private String storesInvolved;
        private String cartSubtotal;
        private String delivery;
        private String totalCost;
        private String orderType;

        public OrderJS(Order order){
            this.orderId = new JSOrderId(order.getOrderId());
            this.orderType=order.getOrderType()==eOrderType.STATIC_ORDER? "static": "dynamic";

            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm");
            this.date = dateFormat.format(order.getOrderDate());
            StringBuilder sb = new StringBuilder("");
            for (Store store: order.getMapStoresToCarts().keySet()){
                sb.append(store.getStoreName() + ", ");
            }
            this.storesInvolved = sb.toString();
            this.cartSubtotal = String.valueOf(order.getCartsSubtotal());
            this.delivery = String.valueOf(order.getDeliveryFee());
            this.totalCost = String.valueOf(order.getTotalOrderCost());
        }
    }

    private static class JSOrderId{
        private int id;
        private int subId;

        public JSOrderId(OrderId orderId){
            this.id=orderId.getId();
            this.subId = orderId.getSubId();
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
