package sdm.servlets;

import models.user.Owner;
import models.user.User;
import models.user.UserManager;
import models.zone.ZonesManager;
import resources.schema.jaxbgenerated.*;
import sdm.utils.ServletUtils;
import sdm.utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@WebServlet(name = "ValidateAndUploadServlet", urlPatterns = { "/uploadfile" })
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
public class ValidateFileServlet extends HttpServlet {

    private String TAG = "ValidateFileServlet";
    protected String loadingErrorMessage;
    protected String zoneName;
    protected SuperDuperMarketDescriptor sdm;
    protected String username;
    protected Owner user;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println(TAG + " - processRequest");
        StringBuilder sbout = new StringBuilder();

        //returning JSON objects, not HTML
        response.setContentType("text/plain");
        try (PrintWriter writer = response.getWriter()) {
            username = SessionUtils.getUsername(request);
            UserManager userManager = ServletUtils.getUserManager(getServletContext());
            System.out.println(TAG + " - LINE 50");
            user = (Owner) userManager.getUserByName(username);

            System.out.println(TAG + " - LINE 53");
            OutputStream out = null;
            InputStream filecontent = null;


            Collection<Part> parts = request.getParts();
            System.out.println("parts: " + parts);

            final Part filePart = request.getPart("file2upload");


            String fileName = parts.stream().findFirst().get().getSubmittedFileName();

            System.out.println(TAG + " - LINE 66");
            int pos = fileName.lastIndexOf(".");
            fileName = fileName.substring(0,pos);


//            sbout.append("<h2> fileName = getFileName(filePart) : " + fileName+ "</h2>");
//            sbout.append("<h2> Total parts : " + parts.size() + "</h2>");
            StringBuilder fileContent = new StringBuilder();

            // The directory is given as a File object
            File dir = (File) getServletContext()
                    .getAttribute("javax.servlet.context.tempdir");


            // Construct a temp file in the temp dir (JDK 1.2 method)
            File tmpFile = File.createTempFile(fileName, ".xml", dir);
            FileWriter fWriter = new FileWriter(tmpFile);

            for (Part part : parts) {
                //printPart(part, sbout);

                //to write the content of the file to a string
                fileContent.append(readFromInputStream(part.getInputStream()));
            }

            fWriter.write(fileContent.toString());
            fWriter.close();
            tryToUploadFile(tmpFile, sbout);
            System.out.println(TAG + " - LINE 94");
            //printFileContentFromFile(tmpFile, sbout);

            writer.println(sbout.toString());
            writer.flush();
        }

    }


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }


    private void printPart(Part part, StringBuilder out) {
        StringBuilder sb = new StringBuilder();
        sb.append("<p>")
                .append("Parameter Name (From html form): ").append(part.getName())
                .append("<br>")
                .append("Content Type (of the file): ").append(part.getContentType())
                .append("<br>")
                .append("Size (of the file): ").append(part.getSize())
                .append("<br>");
        for (String header : part.getHeaderNames()) {
            sb.append(header).append(" : ").append(part.getHeader(header)).append("<br>");
        }
        sb.append("</p>");
        out.append(sb.toString());

    }

    private String readFromInputStream(InputStream inputStream) {
        return new Scanner(inputStream).useDelimiter("\\Z").next();
    }

    private void printFileContent(String content, PrintWriter out) {
        out.println("<h2>File content:</h2>");
        out.println("<textarea style=\"width:100%;height:400px\">");
        out.println(content);
        out.println("</textarea>");
    }

    private void tryToUploadFile(File tmpFile, StringBuilder out){
        String content = getStringFromFile(tmpFile);
        Boolean ans = tryToVerifyFile(tmpFile);
        if (ans == false){
            out.append(loadingErrorMessage);
        } else{
            ZonesManager zonesManager = ServletUtils.getZoneManager(getServletContext());
            synchronized (this){
                if (zonesManager.isZoneExists(zoneName)){
                    out.append("Zone with name " + zoneName + " already created by " + zonesManager.getFounderOfZone(zoneName) + "!");
                } else{
                    zonesManager.createZoneFromSuperDuperMarketDescriptor(sdm, user);
                    out.append("Zone " + zoneName + " created successfully!");
                }
            }
        }
    }

    private void printFileContentFromFile(File tmpFile, StringBuilder out) {
        out.append("<h2>File content:</h2>");
        out.append("<textarea style=\"width:100%;height:400px\">");
        String content = getStringFromFile(tmpFile);
        out.append(content);
        out.append("</textarea>");

        out.append("<h3>FileName:" + tmpFile.getName()+ "</h3><br>");
        out.append("<h3>Is file valid?</h3>");
        out.append("<p>");
        Boolean ans = tryToVerifyFile(tmpFile);
        out.append(ans);
        out.append("<br>");
        if (ans == false){
            out.append(loadingErrorMessage);
        } else{
            ZonesManager zonesManager = ServletUtils.getZoneManager(getServletContext());
            synchronized (this){
                if (zonesManager.isZoneExists(zoneName)){
                    out.append("Zone with name " + zoneName + " already created by " + zonesManager.getFounderOfZone(zoneName) + "!");
                } else{
                    out.append("Zone with name " + zoneName + " does not yet exist!");
                    zonesManager.createZoneFromSuperDuperMarketDescriptor(sdm, user);
                }
            }
        }
        out.append("</p>");
    }

    private String getStringFromFile(File tmpFile) {
        StringBuilder contentBuilder = new StringBuilder();

        try (Stream<String> stream = Files.lines( Paths.get(tmpFile.toPath().toString())))
        {
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return contentBuilder.toString();

    }

    private String getFileName(final Part part) {
        final String partHeader = part.getHeader("content-disposition");

        for (String content : part.getHeader("content-disposition").split(";")) {
            if (content.trim().startsWith("filename")) {

                return content.substring(
                        content.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }


    public Boolean tryToVerifyFile(File file){
        JAXBContext jaxbContext = null;
        loadingErrorMessage = new String("");

        try {
            jaxbContext = JAXBContext.newInstance(SuperDuperMarketDescriptor.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            SuperDuperMarketDescriptor sdm = (SuperDuperMarketDescriptor) jaxbUnmarshaller.unmarshal(file);

            if (isSDMValidAppWise(sdm)) {
                this.sdm = sdm;
                this.zoneName = sdm.getSDMZone().getName();
                return true;
            } else{
                return false;
                //System.out.println(loadingErrorMessage);
            }

        } catch (JAXBException e) {
            loadingErrorMessage = "Problem with JAXB";
            e.printStackTrace();
            return false;
        }
    }

    public boolean isSDMValidAppWise(SuperDuperMarketDescriptor sdm) {
        boolean areItemIdsUnique,areStoreIdsUnique,isStoreUsingUniqueItemIds,
                isStoreUsingExistingItemIds, isEachExistingItemSoldSomewhere,
                areLocationsLegal, areItemsOnSaleSoldAtStore;

        StringBuilder sb = new StringBuilder();

        List<SDMStore> sdmStores = sdm.getSDMStores().getSDMStore();
        List<Integer> listOfStoreIds = getListOfStoreIds(sdm.getSDMStores().getSDMStore());
        List<List<Integer>> listOfStoreLocations = getListOfStoreLocations(sdm.getSDMStores().getSDMStore());
        List<Integer> listOfItemIds = getListOfItemIds(sdm.getSDMItems().getSDMItem());


        //error 3.2
        areItemIdsUnique = checkListOfIntsUnique(listOfItemIds, "course.java.sdm.engine.SDM-Items");
        //error 3.3
        areStoreIdsUnique = checkListOfIntsUnique(listOfStoreIds, "course.java.sdm.engine.SDM-Stores");
        //error 3.4
        isStoreUsingExistingItemIds = checkItemsSoldExist(sdmStores, listOfItemIds);
        //error 3.5
        isEachExistingItemSoldSomewhere = checkEachExistingItemSoldSomewhere(sdmStores, listOfItemIds);
        //error 3.6
        isStoreUsingUniqueItemIds = checkStoreUsesUniqueItemIds(sdmStores);
        //error 3.7
        //areLocationsLegal = checkLocationsAreAllowed(sdmStores);
        areLocationsLegal = checkLocationsAreAllowed(listOfStoreLocations);


        areItemsOnSaleSoldAtStore = checkIfItemsOnSaleAreSoldAtStores(sdmStores);

        return (areItemIdsUnique && areStoreIdsUnique  &&
                isStoreUsingExistingItemIds && isStoreUsingUniqueItemIds &&
                isEachExistingItemSoldSomewhere && areLocationsLegal && areItemsOnSaleSoldAtStore);
    }

    private boolean checkIfItemsOnSaleAreSoldAtStores(List<SDMStore> sdmStores) {
        boolean res = true;
        for (SDMStore store: sdmStores){

            if (checkIfStoreHasDiscounts(store)){
                if (!checkIfDiscountItemsAtStoreAreLegal(store)){
                    res = false;
                }
            }
        }
        return res;
    }

    public static boolean checkIfStoreHasDiscounts(SDMStore store) {
        try{
            return store.getSDMDiscounts().getSDMDiscount().size()>0;
        } catch (NullPointerException npe){
            return false;
        }
    }

    private boolean checkIfDiscountItemsAtStoreAreLegal(SDMStore store) {
        boolean res = true;
        Set<Integer> setOfItemIds = getSetOfItemIdsAtStore(store);
        StringBuilder sb1 = new StringBuilder("Store " + store.getName() + " has following item-ids: {");
        for (Integer i: setOfItemIds){
            sb1.append(String.valueOf(i)).append(",");
        }
        sb1.append("}\n");
        // System.out.println(sb1.toString());


        List<Integer> listOfIfYouBuyIds = getListOfIfYouBuyIdsAtStore(store);

        StringBuilder sb2 = new StringBuilder("Store " + store.getName() + " has following if-you-buy-ids: {");
        for (Integer i: listOfIfYouBuyIds){
            sb2.append(String.valueOf(i)).append(",");
        }
        sb2.append("}\n");
        //  System.out.println(sb2.toString());

        List<Integer> listThenYouGetIDs = getListOfThenYouGetIdsAtStore(store);
        StringBuilder sb3 = new StringBuilder("Store " + store.getName() + " has following then-you-get-ids: {");
        for (Integer i: listThenYouGetIDs){
            sb3.append(String.valueOf(i)).append(",");
        }
        sb3.append("}\n");
        // System.out.println(sb3.toString());

        for (Integer i: listOfIfYouBuyIds){
            if (setOfItemIds.add(i)){
                loadingErrorMessage = loadingErrorMessage.concat(
                        String.format("Store %d has if-you-buy for item id=%d, but no such item id found in store inventory!\n",store.getId(),i)
                );
                res =false;
            }
        }

        for (Integer i: listThenYouGetIDs){
            if (setOfItemIds.add(i)){
                loadingErrorMessage = loadingErrorMessage.concat(
                        String.format("Store %d has then-you-get for item id=%d, but no such item id found in store inventory!\n",store.getId(),i)
                );
                res =false;
            }
        }

        return res;
    }


    private List<Integer> getListOfCustomerIds(SDMCustomers sdmCustomers) {
        return sdmCustomers.getSDMCustomer().stream().map(customer->customer.getId()).collect(Collectors.toList());
    }

    public List<List<Integer>> getListOfCustomerLocations(List<SDMCustomer> sdmCustomers) {
        List<List<Integer>> res = new ArrayList<>();
        for (SDMCustomer customer: sdmCustomers){
            res.add(getCustomerLocation(customer));
        }
        return res;
    }

    public List<Integer> getListOfItemIdsAtStore(SDMStore store){
        List<Integer> res = new ArrayList<>();
        for (SDMSell sdmSell: store.getSDMPrices().getSDMSell()){
            res.add(sdmSell.getItemId());
        }
        return res;
    }
    public Set<Integer> getSetOfItemIdsAtStore(SDMStore store){
        Set<Integer> res = new HashSet<>();
        for (SDMSell sdmSell: store.getSDMPrices().getSDMSell()){
            res.add(sdmSell.getItemId());
        }
        return res;
    }
    public List<Integer> getListOfIfYouBuyIdsAtStore(SDMStore store){
        List<Integer> res = new ArrayList<>();
        for (SDMDiscount discount: store.getSDMDiscounts().getSDMDiscount()){
            res.add(discount.getIfYouBuy().getItemId());
        }
        return res;
    }

    public List<Integer> getListOfThenYouGetIdsAtStore(SDMStore store){
        List<Integer> res = new ArrayList<>();
        for (SDMDiscount discount: store.getSDMDiscounts().getSDMDiscount()){
            for (SDMOffer offer: discount.getThenYouGet().getSDMOffer()){
                res.add(offer.getItemId());
            }
        }
        return res;
    }

    public List<List<Integer>> getListOfStoreLocations(List<SDMStore> sdmStores) {
        List<List<Integer>> res = new ArrayList<>();
        for (SDMStore store: sdmStores){
            res.add(getStoreLocation(store));
        }
        return res;
    }

    public List<Integer> getStoreLocation(SDMStore store){
        List<Integer> res = new ArrayList<>();
        res.add(store.getLocation().getX());
        res.add(store.getLocation().getY());
        return res;
    }

    public List<Integer> getCustomerLocation(SDMCustomer customer){
        List<Integer> res = new ArrayList<>();
        res.add(customer.getLocation().getX());
        res.add(customer.getLocation().getY());
        return res;
    }

    public List<Integer> getListOfStoreIds(List<SDMStore> sdmStores) {
        List<Integer> res = new ArrayList<>();

        for (SDMStore store: sdmStores){
            res.add(store.getId());
        }
        return  res;
    }

    public List<Integer> getListOfItemIds(List<SDMItem> sdmItems) {
        List<Integer> res = new ArrayList<>();

        for (SDMItem item: sdmItems){
            res.add(item.getId());
        }
        return  res;
    }


    private boolean checkLocationsAreAllowed(List<List<Integer>> listOfStoreLocations) {
        Boolean res = true;
        Set<List<Integer>> set = new HashSet<>();

        for (List<Integer> loc: listOfStoreLocations){
            if (set.add(loc) == false){
                loadingErrorMessage = loadingErrorMessage.concat(String.format("Error: Store location (%d,%d) appears more than once\n",loc.get(0), loc.get(1)));
                res = false;
            }
        }
        return res;
    }


    public boolean checkEachExistingItemSoldSomewhere(List<SDMStore> sdmStores, List<Integer> listOfExistingItemIds) {
        boolean res = true;
        Set<Integer> itemsSold = new HashSet<>();

        for (SDMStore store: sdmStores){
            for (SDMSell sold: store.getSDMPrices().getSDMSell()){
                itemsSold.add(sold.getItemId());
            }
        }

        for (Integer existingItem: listOfExistingItemIds){
            if (!itemsSold.contains(existingItem)){
                loadingErrorMessage = loadingErrorMessage.concat("Error: Item with id= " + existingItem + " is not sold in any store.");
                res = false;
            }
        }
        return res;
    }

    public boolean checkItemsSoldExist(List<SDMStore> sdmStores, List<Integer> listOfAllowedIds) {
        boolean res = true;

        for (SDMStore store: sdmStores){
            List<SDMSell> itemsSold = store.getSDMPrices().getSDMSell();

            for (SDMSell sold: itemsSold){
                if (!listOfAllowedIds.contains(sold.getItemId())){
                    loadingErrorMessage = loadingErrorMessage.concat("Error: Store-Id = "+ store.getId() + " has item with item-Id= " + sold.getItemId() + ", but no such id exists in SDMItems!");
                    res = false;
                }
            }
        }
        return res;
    }

    public boolean checkStoreUsesUniqueItemIds(List<SDMStore> sdmStores) {
        boolean res = true;

        for (SDMStore store: sdmStores){
            List<SDMSell> itemsSold = store.getSDMPrices().getSDMSell();
            Set<Integer> tmpSet = new HashSet<>();

            for (SDMSell sold: itemsSold){
                if (!tmpSet.add(sold.getItemId())){
                    loadingErrorMessage = loadingErrorMessage.concat("Error: Store-Id = " + store.getId() + " is selling multiple items with id =" + sold.getItemId());
                    res = false;
                }
            }
        }

        return res;
    }

    public boolean checkListOfIntsUnique(List<Integer> inputList, String problematicType){
        boolean res = true;
        Set<Integer> tmpSet = new HashSet<>();

        for (Integer num: inputList){
            if (!tmpSet.add(num)){
                loadingErrorMessage = loadingErrorMessage.concat("Error: id=" + num + " is not unique for type " + problematicType + "\n");

                res = false;
            }
        }
        return res;
    }

}