package sdm.servlets;

import com.google.gson.Gson;
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
import java.util.List;

@WebServlet(name = "ZoneItemsServlet", urlPatterns = { "/zoneitems" })
public class ZoneItemsServlet extends HttpServlet {
    private final String TAG = "ZoneItemsServlet";

    private void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException{

        //System.out.println(TAG + " - processRequest");
        response.setContentType("application/json");
        String zoneName = request.getParameter("zone");
        //System.out.println("ZoneItemsServlet found param zone= " + zoneName);

        Zone zone;
        synchronized (getServletContext()){
            //System.out.println(TAG + " - LINE 30");
            zone = ServletUtils.getZoneManager(getServletContext()).getZoneByName(zoneName);
            //  System.out.println(TAG + " - LINE 32");
        }

//        //System.out.println(TAG + " - LINE 33");
        int zoneVer = Integer.parseInt(request.getParameter("zoneVersion"));
        int actualZoneVer = zone.getZoneVersion();
//        //System.out.println("Current version of " + zoneName + " = " + zone.getZoneVersion());
        String jsonResponse;
        Gson gson = new Gson();
//
        if (zoneVer == actualZoneVer){
            //System.out.println(TAG + " - LINE 40");
            ItemsAndVersion iav = new ItemsAndVersion(actualZoneVer,null);
            //System.out.println(TAG + " - LINE 42");
            jsonResponse = gson.toJson(iav);

        } else{
            //System.out.println(TAG + " - LINE 46");
            List<ZoneItem> zoneItems = new ArrayList<>();

            zone.getItemsSoldInZone().forEach(inventoryItem -> {
                //System.out.println(TAG + " - LINE 50");
                int id = inventoryItem.getItemID();
                String name = inventoryItem.getItemName();
                //  System.out.println(TAG + " - LINE 56");
                String category = inventoryItem.getPurchaseCategory().name();
                double ave = zone.getMapItemsToAveragePrice().get(inventoryItem);
                //System.out.println(TAG + " - LINE 59");
                double numberSold = zone.getMapItemsToAmountSold().get(inventoryItem).doubleValue();
                zoneItems.add(new ZoneItem(id, name, category, ave, numberSold));
                //System.out.println(TAG + " - LINE 63");
            });


            ItemsAndVersion iav = new ItemsAndVersion(actualZoneVer, zoneItems);
            System.out.println("iav: " + iav);
            jsonResponse = gson.toJson(iav);
        }

        try (PrintWriter out = response.getWriter()) {
            out.print(jsonResponse);
            out.flush();
            return;
        }
    }

    private static class ItemsAndVersion {

        final private List<ZoneItem> entries;
        final private int version;

        public ItemsAndVersion(int version, List<ZoneItem> entries) {
            this.entries = entries;
            this.version = version;
        }

        @Override
        public String toString() {
            return "ItemsAndVersion{" +
                    "entries=" + entries +
                    ", version=" + version +
                    '}';
        }
    }

    private static class ZoneItem {
        int id;
        String name;
        String category;
        double averagePrice;
        double numberTimesSold;

        public ZoneItem(int id, String name, String category, double ave, double numberSold) {
            this.id = id;
            this.name = name;
            this.category = category;
            this.averagePrice = ave;
            this.numberTimesSold = numberSold;
        }

        @Override
        public String toString() {
            return "ZoneItem{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", category='" + category + '\'' +
                    ", averagePrice=" + averagePrice +
                    ", numberTimesSold=" + numberTimesSold +
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
