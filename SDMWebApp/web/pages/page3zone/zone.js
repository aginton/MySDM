var refreshRate = 5000; //milli seconds

var ZONE_ITEMS_URL = buildUrlWithContextPath("zoneitems");
var ZONE_STORES_URL = buildUrlWithContextPath("zonestores");
var CUSTOMER_ORDER_DETAILS_URL = buildUrlWithContextPath("orderdetails");
var STORE_ORDER_DETAILS_URL = buildUrlWithContextPath("storeorderdetails");

var STOREITEMS_URL = "../chooseitems/storeitems.html";

var zone = sessionStorage.getItem("zone");
var role = sessionStorage.getItem("role");

currentZone = zone;
var zoneVersion = 0;
var zoneStores = [];

var orderHistoryForZone;

var showZoneItemsOnce = 0;

function ajaxZoneItems() {
    //console.log("\najaxZoneContent called")
    $.ajax({
        url: ZONE_ITEMS_URL,
        data: {zone: zone, zoneVersion: zoneVersion},
        dataType: 'json',
        success: function(data) {
            /*
             data will arrive in the next form:
            [{  id:_ , name:_ , category: _, averagePrice: _, numberTimesSold: _}, {...},...]
             */

            if (data.version === zoneVersion){
                //console.log("ajaxZoneItems - No need to update zone items!")
                return;
            }

            zoneVersion = data.version;
            createZoneItemsTable(data.entries);
            if (showZoneItemsOnce === 0){
                console.log("ajaxZoneItems - returned following data:")
                console.log(data);
                showZoneItemsOnce = 1;
            }
        },
        error: function(error) {
            $('#zone-items-table').append("<p>Some error occurred!!!</p>")
        }
    });
}

function createZoneItemsTable(entries) {
    /*
    entries is array of form:
    [{  id:_ , name:_ , category: _, averagePrice: _, numberTimesSold: _}, {...},...]
     */
    entries.sort(compareItemID)
    var trHTML ='';

    $.each(entries,function (i,item) {
        trHTML += `
        <tr><td>${item.id}</td><td>${item.name}</td><td>${item.category}</td><td>${Number.parseFloat(item.averagePrice).toFixed(2)}</td>
        <td>${item.numberTimesSold}</td></tr>
        `
    })
    $('#zone-items-table tbody').empty().html(trHTML)
}


var showOnce = 0;
function ajaxZoneStores(){
    //console.log("\ngetZoneStores called")
    currentZone = zone;
    $.ajax({
        url: ZONE_STORES_URL,
        data: {"zone": currentZone},
        dataType: 'json',
        success: function (data) {
            /*
           [{
                deliveryIncome: _,
                numberOfOrders: _,
                owner: "",
                ppk: _,
                salesIncome: _,
                storeID: _,
                storeItems: [{category: "", id: _, name: "", totalSoldAtStore: _, unitPrice: }, {…},..]
                storeName: ""
           },...]

             */


            zoneStores = data;
            zoneStores.sort(compareStoreID)
            sessionStorage.setItem("zoneStores",JSON.stringify(zoneStores))
            if (showOnce === 0){
                console.log("ajaxZoneStores returned following data:")
                console.log(data);
                console.log("zone stores is:")
                console.log(zoneStores)
            }

            createZoneStoresTable(data);
        },
        error: function (err) {
            console.log("getZoneStores error " + err.message);
        }
    })

    if (role === "customer")
        onStoreClick();
}



function createStoreItemsTable(items){
    items.sort(compareItemID)
    var table = "<table > <thead> <tr> <th>ID</th> <th>Name</th>" +
        "<th>Categoryr</th> <th>Unit Price</th> <th>Total Sold</th> </tr> </thead> <tbody>";

    var content = '';
    $.each(items, function(i,item){
        content += '<tr><td>' + item.id + '</td><td>' + item.name + '</td><td>' + item.category + '</td><td>' + item.unitPrice
            + '</td><td>' + item.totalSoldAtStore + '</td></tr>';
    })
        content += "</tbody> </table>";

    table += content;
    return table;
}



function onStoreClick() {
    //console.log("Inside onStoreClick()")
    $("#zone-stores-table tbody tr").on("click", function(event){

        var store = ($(this).find("td").eq(1).html());
        if (sessionStorage.getItem("numberCartItemsByType") > 0
            && sessionStorage.getItem("orderTypeStarted") == "static" && sessionStorage.getItem("store") != store){
            if (confirm('If you switch to this store, your current order will be erased. Do you wish to continue?')) {
                // Save it!
                // var obj = goToStore("static",store);
                // emptyCartInProgress(obj)

                emptyCartInProgress()

                var obj = goToStore("static",store);
            } else {
                // Do nothing!
                console.log('Thing was not saved to the database.');
                return;
            }
        }
        goToStore("static", store)
    });
}


function onDynamicButtonClick() {
    $("#dynamic-order-btn").on('click', function (event) {

        if (sessionStorage.getItem("numberCartItemsByType") > 0 && sessionStorage.getItem("orderTypeStarted") == "static"){
            if (confirm('You already started a static order. Starting a new dynamic order will erase your current cart. Do you wish to continue?')) {
                var obj = goToStore("dynamic","na");
                emptyCartInProgress(obj)
            } else {
                // Do nothing!
                return;
            }
        }
        goToStore("dynamic","na")
    })
}



function goToStore(type, store) {
    sessionStorage.setItem("orderTypeStarted",type);
    sessionStorage.setItem("store", store);
    window.location.replace(STOREITEMS_URL);
}



function createZoneStoresTable(entries) {
    /*
        entries is array of form:
        [{
                deliveryIncome: _,
                numberOfOrders: _,
                owner: "",
                ppk: _,
                salesIncome: _,
                storeID: _,
                storeItems: [{category: "", id: _, name: "", totalSoldAtStore: _, unitPrice: }, {…},..]
                storeName: ""
           },...]
         */
    var trHTML ='';


    $.each(zoneStores, function(i,item){
        var storeItemsTable = createStoreItemsTable(item.storeItems);

        trHTML += `<tr><td>${item.storeID}</td><td>${item.storeName}</td><td>${item.owner}</td><td>${storeItemsTable}</td>
                    <td>${item.numberOfOrders}</td><td>${item.salesIncome}</td><td>${item.ppk}</td>
                    <td>${Number.parseFloat(item.deliveryIncome ).toFixed(2)}</td></tr>`
    });

    $('#zone-stores-table tbody').empty().html(trHTML)
    if (role === "customer")
        onStoreClick();
}











function createCustomerOrdersTable() {
    var rows = '';
    var entries = JSON.parse(sessionStorage.getItem("orders"))

    //var orderHistory = sessionStorage.getItem("orderHistory")
    /*
    orderSummaries: [ {cartSubtotal: "93.0",
                        date: "22-10-2020 10:54",
                        delivery: "36.060001373291016",
                        orderId: {id: 1, subId: -1},
                        orderType: "static",
                        storesInvolved: "Rami, ",
                        totalCost: "129.06000137329102" },...{}]
     */
    $.each(entries,function (i, item) {

        var id = "" + item.orderId.id;

        if (item.orderType === "dynamic"){
            id += `-${item.orderId.subId}`
        }

        //TODO: Make server return correct precision
        rows += `
        <tr><td>${id}</td><td>${item.date}</td><td>${item.storesInvolved}</td><td>${item.cartSubtotal}</td>
        <td>${Number.parseInt(item.delivery).toFixed(2)}</td><td>${Number.parseInt(item.totalCost).toFixed(2)}</td></tr>
        `
    })
    $('#user-orders-table tbody').empty().html(rows)
}

function createOwnerOrdersTable() {
    var rows = '';
    var entries = JSON.parse(sessionStorage.getItem("orders"))

    //var orderHistory = sessionStorage.getItem("orderHistory")
    /*
    orderSummaries: [ {cartSubtotal: "93.0",
                        date: "22-10-2020 10:54",
                        delivery: "36.060001373291016",
                        orderId: {id: 1, subId: -1},
                        orderType: "static",
                        storeName: "Rami, ",
                        totalCost: "129.06000137329102" },...{}]
     */
    $.each(entries,function (i, item) {

        var id = "" + item.orderId.id;

        if (item.orderType === "dynamic"){
            id += `-${item.orderId.subId}`;
        }

        //TODO: Make server return correct precision
        rows += `
        <tr><td>${id}</td><td>${item.date}</td><td>${item.storeName}</td><td>${item.cartSubtotal}</td>
        <td>${Number.parseInt(item.delivery).toFixed(2)}</td><td>${Number.parseInt(item.totalCost).toFixed(2)}</td></tr>
        `
    })
    $('#user-orders-table tbody').empty().html(rows)
}


function ajaxCustomerOrderDetails(rowIndex) {
    console.log("\najaxOrderDetails called")
    var orders = JSON.parse(sessionStorage.getItem("orders"));
    var id = orders[rowIndex].orderId.id;
    var subId = orders[rowIndex].orderId.subId;
    var orderType = orders[rowIndex].orderType;
    //alert(`Getting details for ${orderType} order ${id}-${subId}`)

    $.ajax({
        url: CUSTOMER_ORDER_DETAILS_URL,
        data: {"id": id, "subId":subId, "orderType": orderType, "zone": zone},
        dataType: 'json',
        success: function(data) {
            /*
             data will arrive in the next form:
                [{amountOrdered: "2.00 pcks"
                    category: "quantity"
                    cost: "20.00"
                    id: "7"
                    isPartOfDiscount: "yes (YallA BaLaGaN)"
                    name: "Eggs",
                    unitPrice: "73.00"
                    storeOrderedFrom: "(1) Rami"},
                    ...,{}]
             */

            console.log("ajaxOrderDetails success! - returned following data:")
            console.log(data);
            createOrderDetailsTable(data);
            //triggerAjaxChatContent();
        },
        error: function(error) {

        }
    });
}

function ajaxStoreOrderDetails(rowIndex) {
    console.log("\najaxOrderDetails called")
    var orders = JSON.parse(sessionStorage.getItem("orders"));
    var id = orders[rowIndex].orderId.id;
    var subId = orders[rowIndex].orderId.subId;
    var orderType = orders[rowIndex].orderType;
    var storeName = orders[rowIndex].storeName;
    //alert(`Getting details for ${orderType} order ${id}-${subId} in store ${storeName}`)

    $.ajax({
        url: STORE_ORDER_DETAILS_URL,
        data: {"id": id, "subId":subId, "orderType": orderType, "zone": zone, "storeName": storeName},
        dataType: 'json',
        success: function(data) {
            /*
             data will arrive in the next form:
                [{amountOrdered: "2.00 pcks"
                    category: "quantity"
                    cost: "20.00"
                    id: "7"
                    isPartOfDiscount: "yes (YallA BaLaGaN)"
                    name: "Eggs",
                    unitPrice: "73.00"
                    storeName: "(1) Rami"},
                    ...,{}]
             */

            console.log("ajaxOrderDetails success! - returned following data:")
            console.log(data);
            createOrderDetailsTable(data);
            //triggerAjaxChatContent();
        },
        error: function(error) {

        }
    });
}

function createOrderDetailsTable(data) {
    data.sort(compareStoreBroughtFromAndIds);
    console.log(data);
    /*
             data will arrive in the next form:
                [{amountOrdered: "2.00 pcks"
                    category: "quantity"
                    cost: "20.00"
                    id: "7"
                    isPartOfDiscount: "yes (YallA BaLaGaN)"
                    name: "Eggs"
                    storeOrderedFrom: "(1) Rami"},
                    unitPrice: "73.00",
                    ...,{}]
             */

    var rows = ``;
    $.each(data,function (i,item) {
        rows += `
        <tr><td>${item.id}</td><td>${item.name}</td><td>${item.category}</td><td>${item.storeOrderedFrom}</td>
        <td>${item.amountOrdered}</td><td>${item.unitPrice}</td><td>${item.cost}</td><td>${item.isPartOfDiscount}</td></tr>
        `
    })
    $('#order-details-table tbody').empty().html(rows)

     $("#OrderDetailsModal").modal('show');
}



function getZoneItemsAndStores(){
    ajaxZoneItems();
    ajaxZoneStores();
    // if (lastOrderVersionUpdatedFor !== orderSummaryVersion){
    //     updateUserOrderHistoryTable();
    // }

    setTimeout(getZoneItemsAndStores, refreshRate);
}


$(function() { // onload...do
    console.log("zone.js onload ")

    if (zone === "" || zone === null){
        var content = `<h1>NO ZONE SELECTED!</h1>`
        $('#main-container').hide()
        $('body').append(content)

    } else{


        $("#welcome-msg").text(`${zone} - Overview`)

        getZoneItemsAndStores();

        if (role === "customer"){
            $("#zone-table-message").html(`<h2>To start a static order, click on the row for the store you wish to order from.</h2>
            <h2 class="h2inline">To start a dynamic order, </h2><button type="button" id="dynamic-order-btn">Click Here</button><h2 class="h2inline">!</h2>`)

            onDynamicButtonClick()

            ajaxCustomerOrders(createCustomerOrdersTable);

            document.querySelector('#user-orders-table tbody').onclick = function(ev) {
                var index = ev.target.parentElement.rowIndex;
                //        alert(`Clicked row ${index}`)
                ajaxCustomerOrderDetails(index-1);
            }
        }

        if (role === "owner"){
            var feedbackTable = createFeedbackTable();
            $("#feedback-table-section").html(feedbackTable);
            $("#view-feedbacks-section").html(`<button type="button" class="btn btn-primary" id="view-feedbacks-btn">View Feedbacks</button>`)

            $("#view-feedbacks-btn").on('click',function () {
                ajaxFeedbackByZone(zone)
            });

            $("#zone-table-message").html(`<h2>To add a new store, <a href="../addstore/addstore.html">click here</a></h2>`)
            ajaxOwner();
            var ordersObj = function(){
                ajaxOwnerOrders(createOwnerOrdersTable)
            }
            ajaxOwnerOrders(createOwnerOrdersTable);
            setInterval(ordersObj,refreshRate);

            document.querySelector('#user-orders-table tbody').onclick = function(ev) {
                var index = ev.target.parentElement.rowIndex;
                //alert(`Clicked row ${index}`)
                ajaxStoreOrderDetails(index-1);
            }
        }

    }
});