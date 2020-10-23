package sdm.servlets;

import com.google.gson.Gson;
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
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "ZonesListServlet", urlPatterns = { "/zoneslist" })
public class ZonesListServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {


        //System.out.println("\ncalled ZonesListServlet.processRequest()");

        //returning JSON objects, not HTML
        response.setContentType("application/json");
        try (PrintWriter out = response.getWriter()) {

            ZonesManager zonesManager = ServletUtils.getZoneManager(getServletContext());
            List<SingleZone> zones = new ArrayList<>();
            for (Zone zone: zonesManager.getZoneSet()){
                zones.add(new SingleZone(zone));
            }

            //System.out.println("zones list: " + zones);
            String json = new Gson().toJson(zones);
            out.println(json);
            out.flush();
        }
    }

    private static class SingleZone{
        private String founder;
        private String name;
        private int numberOfItems;
        private int numberOfStores;
        private double averageOrderCost;
        private int zoneVersion;

        public SingleZone(Zone zone){
            this.founder = zone.getFounder().getName();
            this.name = zone.getZoneName();
            this.numberOfItems=zone.getItemsSoldInZone().size();
            this.numberOfStores = zone.getStores().size();
            double preciseVal = zone.getAverageOrderCost();
            BigDecimal bd = new BigDecimal(preciseVal).setScale(2, RoundingMode.HALF_UP);
            this.averageOrderCost=bd.doubleValue();
            this.zoneVersion = zone.getZoneVersion();
        }

        @Override
        public String toString() {
            return "SingleZone{" +
                    "founder='" + founder + '\'' +
                    ", name='" + name + '\'' +
                    ", numberOfItems=" + numberOfItems +
                    ", numberOfStores=" + numberOfStores +
                    ", averageOrderCost=" + averageOrderCost +
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
