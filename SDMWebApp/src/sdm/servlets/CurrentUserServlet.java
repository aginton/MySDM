package sdm.servlets;

import com.google.gson.Gson;
import models.order.Order;
import models.order.OrderId;
import models.order.eOrderType;
import models.store.Store;
import models.user.Transaction;
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
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@WebServlet(name = "CurrentUserServlet", urlPatterns = { "/currentuser" })
public class CurrentUserServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {


        //System.out.println("On CurrentUserServlet, processRequest");

        //returning JSON objects, not HTML
        response.setContentType("application/json");
        try (PrintWriter out = response.getWriter()) {
            Gson gson = new Gson();
            UserManager userManager = ServletUtils.getUserManager(getServletContext());
            String username = SessionUtils.getUsername(request);
            User user = userManager.getUserByName(username);
            UserJS userInfo = new UserJS(user);
            String json = gson.toJson(userInfo);
            // System.out.println(json);
            out.println(json);
            out.flush();
        }
    }

    private static class UserJS{
        private String name;
        private String role;
        private double balance;
        private List<TransactionJS> transactions;

        public UserJS(User user){
            name = user.getName();
            role = user.getRole();
            BigDecimal bd = new BigDecimal(user.getBalance()).setScale(2, RoundingMode.HALF_UP);
            balance = bd.doubleValue();
            transactions = new ArrayList<>();
            for (Transaction transaction: user.getTransactions()){
                transactions.add(new TransactionJS(transaction));
            }
        }
    }

    private static class TransactionJS{
        private String type;
        private String date;
        private double amount;
        private double balanceBefore;
        private double balanceAfter;

        public TransactionJS(Transaction transaction){
            this.type = transaction.getType();
            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm");
            this.date = dateFormat.format(transaction.getDate());
            BigDecimal bd = new BigDecimal(transaction.getBalanceBefore()).setScale(2, RoundingMode.HALF_UP);
            this.balanceBefore = bd.doubleValue();
            bd = new BigDecimal(transaction.getBalanceAfter()).setScale(2, RoundingMode.HALF_UP);
            this.balanceAfter = bd.doubleValue();
            bd = new BigDecimal(transaction.getAmount()).setScale(2, RoundingMode.HALF_UP);
            this.amount = bd.doubleValue();
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
