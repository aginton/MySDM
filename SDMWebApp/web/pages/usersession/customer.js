var CUSTOMER_CART_URL=buildUrlWithContextPath("customercart")
var CUSTOMER_ORDERS_URL=buildUrlWithContextPath("customerorders")


function ajaxCustomerCart(callback) {
    //console.log("Calling ajaxCurrentCustomer()");
    $.ajax({
        url: CUSTOMER_CART_URL,
        dataType: 'json',
        success: function(data) {
            /*
             data will arrive in the form:
            {
            cartItems: [{id: 7, name: "Eggs", price: 73, amount: 1, category: "quantity"},...,{}]
            mapDiscountNamesToTimesUsed: {YallA BaLaGaN: 1, ... }
            numberItems: 0
            numberItemsByType: 0
            subtotal: 0,
            discountOverviews: [condition: {id: 1, name: "Toilet Paper", category: "quantity", quantity: 1}, discountName: "YallA BaLaGaN", timesUsed: 1}, ...{}]
            usedDiscounts: [{discountName: "YallA BaLaGaN",
                            storeName: "Rami",
                            discountOperator: "ONE-OF",
                            offersChosen: [ {id: 1, name: "Toilet Paper", forAdditional: 0, quantity: 1, category: "quantity"}, ...{}],
                            timesUsed: 1}, ...{…}]
            }
             */

            console.log("ajaxCustomerCart success, returned data:")
            console.log(data)


            sessionStorage.setItem("regularCart", JSON.stringify(data.cartItems))
            sessionStorage.setItem("discountsUsed", JSON.stringify(data.usedDiscounts) )

            //var discountsUsedOverview = createDiscountsUsedOverview(data.discountUsedJSList);
            sessionStorage.setItem("mapDiscountNamesToTimesUsed", JSON.stringify(data.mapDiscountNamesToTimesUsed))
            sessionStorage.setItem("discountsUsedOverview", JSON.stringify(data.discountOverviews));
            sessionStorage.setItem("numberCartItemsByType",  data.numberItemsByType);
            sessionStorage.setItem("currentCart", JSON.stringify(data));
            updateNumberCartItemsBadge(data.numberItemsByType)
        },
        error: function(error) {
            console.log("OH NO! ajaxCurrentUser() returned error! " + error.message)
        }
    })
        .done(function () {
            if (typeof callback === "function"){
                console.log("ajaxCustomerCart calling callback " + callback.name)
                callback()
            }

        })
}


function saveCartAtServer(items, callback){

    var zone = sessionStorage.getItem("zone");

    $.ajax({
        url: SAVE_CURRENT_CART_URL,
        data: {"zone":zone,
            "cart": JSON.stringify(items)},
        dataType: 'json',

        success: function(data){
            console.log("saveCartAtServer success, returned following data:")
            console.log(data)

        },
        error: function (err) {
            console.log("Oh No! saveCartAtServer returned error: " + err.message)
        }
    }).done(function () {
        if (typeof callback == "function"){
            console.log("saveCartAtServer running callback " + callback.name)
            callback();
        }
    })
}


function addItemForStaticOrder(id, store,zone){

    $.ajax({
        url: ADD_ITEM_STATIC_ORDER_URL,
        data: {"store": store,
            "zone":zone,
            "id": id},
        success: function(){
            console.log("addItemForStaticOrder success")
            // saveCartToSessionAndUpdateHeader(data)

        },
        error: function (err) {
            console.log("Oh No! addItemForStaticOrder returned error: " + err.message)
        }
    }).done(function () {
        ajaxCustomerCart();
    })
}

function saveCartToSessionAndUpdateHeader(data) {
    sessionStorage.setItem("cartVersion", data.cartVersion);
    sessionStorage.setItem("numberCartItemsByType",  data.numberCartItemsByType);
    sessionStorage.setItem("currentCart", JSON.stringify(data));
    updateNumberCartItemsBadge(data.numberCartItemsByType)
}

function decrementDiscountUsage(discountToDecrement, callback){
    $.ajax({
        url: REMOVE_DISCOUNT_URL,
        data: {"discountToDecrement": discountToDecrement},
        success: function () {
            console.log("decrementDiscountUsage success!")
            ajaxCustomerCart(callback)

        },
        error: function (error) {
            console.log("decrementDiscountUsage error - " + error.message)
        }
    })
}


function emptyCartInProgress(callback) {


    $.ajax({
        url: EMPTY_USER_CART_IN_PROGRESS,
        success: function(){
            console.log("emptyCartInProgress success!")
            sessionStorage.setItem("currentCart", "");
            sessionStorage.setItem("regularCart", "");
            sessionStorage.setItem("discountsUsed", "");
            sessionStorage.setItem("discountsUsedOverview", "");
            sessionStorage.setItem("entitledDiscounts", "");
        },
        error: function () {
            console.log("emptyCartInProgress error")
        }
    }).done(function () {
        if (typeof callback == "function"){
            callback();
        }
    })
}

function ajaxCustomerOrders(callback){
    $.ajax({
        url: CUSTOMER_ORDERS_URL,
        dataType: 'json',
        success: function (data) {
            /*
    orderSummaries: [ {cartSubtotal: "93.0",
                        date: "22-10-2020 10:54",
                        delivery: "36.060001373291016",
                        orderId: {id: 1, subId: -1},
                        orderType: "static",
                        storesInvolved: "Rami, ",
                        totalCost: "129.06000137329102" },...{}]
     */
            console.log("ajaxCustomerOrders returned:")
            console.log(data);
            sessionStorage.setItem("orders",JSON.stringify(data));
        },
        error: function (error) {
            console.log("ajaxCustomerOrders returned error: " + error.message)
        }
    })
        .done(function () {
            if (typeof callback === "function"){
                callback();
            }
        })
}


$(function() { // onload...do

    if (sessionStorage.getItem("role") === "customer"){
        console.log("\ncustomer.js onload")

        ajaxCustomerCart();
        //getCurrentUserCart();

    }
});