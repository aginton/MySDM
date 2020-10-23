package sdm.servlets;

import com.google.gson.Gson;
import models.store.Store;
import models.user.Owner;
import models.user.UserManager;
import models.zone.Zone;
import models.zone.ZonesManager;
import sdm.utils.ServletUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "AddStoreServlet", urlPatterns = { "/addnewstore" })
public class AddStoreServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        /*data sent to servlet in format:
        {"storename":"story",
        "zone":"Hasharon",
        "owner":"tom",
        "xcoordinate":"4",
        "ycoordinate":"11",
        "ppk":"4500",
        "items":["10","800","1","100"]}
         */

        System.out.println("AddStore servlet called");
        response.setContentType("application/json");

        //parsing json data in request
        String storeName = request.getParameter("storename");
        String zoneName = request.getParameter("zone");
        String storeOwnerName = request.getParameter("owner");
        System.out.printf("storeOwnerName = " + storeOwnerName);
        int xLoc = Integer.parseInt(request.getParameter("xcoordinate"));
        int yLoc =  Integer.parseInt(request.getParameter("ycoordinate"));
        int[] loc = new int[2];
        loc[0]=xLoc;
        loc[1] = yLoc;
        int ppk =  Integer.parseInt(request.getParameter("ppk"));;
        String[] itemsToPricesArr = request.getParameterValues("items[]");

        System.out.println("data parsed in servlet: " + storeName + " " + zoneName + " " + storeOwnerName + " " + xLoc + " " + yLoc + " " + ppk);
        //List<String> parameterNames = new ArrayList<>(request.getParameterMap().keySet());
        //System.out.println(parameterNames);
//        List<String> itemsToPricesList = Arrays.asList(itemsToPricesArr);
//        for(String part: itemsToPricesList) {
//            System.out.println(part);
//        }

        //load the zone manager
        ZonesManager zonesManager = ServletUtils.getZoneManager(getServletContext());
        Zone storeZone = zonesManager.getZoneByName(zoneName);
        System.out.println("zone manager loaded");

        //load the user manager
        UserManager userManager = ServletUtils.getUserManager(getServletContext());
        Owner storeOwnerUser = (Owner) userManager.getUserByName(storeOwnerName);
        System.out.println("user manager loaded");

        //check if store location is available
        boolean isLocationAvailable = zonesManager.isValidLocation(loc);

        boolean wasStoreCreated;
        int numOfItemsSoldInStore = 0;
        int totalNumOfItemsInZone = 0;

        if (isLocationAvailable){
            Store storeToAdd = new Store(storeName, storeZone, storeOwnerUser, loc, ppk, itemsToPricesArr);
            System.out.println("about to call addStoreAndNotifyFounder function");
            storeZone.addStoreAndNotifyFounder(storeToAdd);
            System.out.println("Store created!");
            wasStoreCreated = true;
            numOfItemsSoldInStore = storeToAdd.getStoreItems().size();
            totalNumOfItemsInZone = storeZone.getItemsSoldInZone().size();
        } else{
            wasStoreCreated = false;
        }

        Gson gson = new Gson();
        jsonResponse jsonResponse = new jsonResponse(wasStoreCreated,isLocationAvailable, loc, storeName, numOfItemsSoldInStore, totalNumOfItemsInZone);
        String jsonObject = gson.toJson(jsonResponse);

        try (PrintWriter out = response.getWriter()) {
            out.print(jsonObject);
            out.flush();
        }
    }



    public static class jsonResponse {

        boolean success;
        boolean isLocationAvailable;
        int[] loc;
        String storeName;
        int numOfItemsSold;
        int totalNumOfItemsInZone;

        public jsonResponse(boolean isStoreCreated,boolean isLocationAvailable,
                           int[] storeLoc,
                            String storeName,
                            int numOfItemsSold,
                            int totalNumOfItemsInZone) {

            this.success = isStoreCreated;
            this.isLocationAvailable = isLocationAvailable;
            this.loc = storeLoc;
            this.storeName = storeName;
            this.numOfItemsSold = numOfItemsSold;
            this.totalNumOfItemsInZone = totalNumOfItemsInZone;
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


