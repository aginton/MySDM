/*
This file should be responsible for getting information about current session:
userName, cart information, last message read
 */

var chatVersion = 0;
var refreshRate = 100000; //milli seconds
var SAVE_CURRENT_CART_URL = buildUrlWithContextPath("savecurrentcart");
var CURRENT_USER_URL = buildUrlWithContextPath("currentuser");
var EMPTY_USER_CART_IN_PROGRESS = buildUrlWithContextPath("emptycart");

var ADD_ITEM_STATIC_ORDER_URL = buildUrlWithContextPath("additemstaticorder");
var REMOVE_DISCOUNT_URL = buildUrlWithContextPath("decrementdiscount");
var HOMEPAGE_URL = "../page2zones/zonesoverview.html";

var userName;
var role;
var balance;
var messages;
var lastMessageRead = 0;
var numberOfUnreadMessages; //this member is unnecessary, but just for keeping track of things we'll keep it for now
var cart;
var discountCart = [];
//var orderHistory = [];
var orderSummaryVersion = 0;
var activityVersion = 0;
var transactions = [];
var oldBalance;


var currentZone;

var printEvery5Calls = 5;


function Item(id,name, amount,category,price,store){
    this.id = id;
    this.name = name;
    if (category === "quantity")
        this.amount = Math.floor(amount);
    else
        this.amount = amount;
    this.category = category;
    this.price = price;
    this.store = store;
}

function DiscountUsedOverview(discountName, conditionID, conditionQuantity, totalTimesUsed){
    this.discountName = discountName;
    this.conditionID = conditionID;
    this.conditionQuantity = conditionQuantity;
    this.totalTimesUsed = totalTimesUsed;
}

var showOnce = 0;
function ajaxCurrentUser() {
    console.log("Calling ajaxCurrentUser()");
    $.ajax({
        url: CURRENT_USER_URL,
        dataType: 'json',
        success: function(data) {
            /*    {name: "frank", role: "customer", balance: 0, transactions:[{date: "22-10-2020 04:14", amount: 146.06, balanceBefore: 0, balanceAfter: -146.06},...,{}]}*/

            if (balance !== data.balance){
                sessionStorage.setItem("balance",data.balance);
                updateProfileBalance(data.balance);
            }

            if (showOnce === 0 ){
                // sessionStorage.setItem("name",data.name)
                console.log("ajaxCurrentUser success")
                console.log(data)
                showOnce = 1;
            }
            if (transactions.length !== data.transactions.length){
                transactions = data.transactions;
                sessionStorage.setItem("transactions",JSON.stringify(transactions))
            }

        },
        error: function(error) {
            console.log("OH NO! ajaxCurrentUser() returned error! " + error.message)
        }
    })
        .done(function () {
            setTimeout(ajaxCurrentUser,refreshRate);
        })
}



function goToHomepage() {
    window.location.replace(HOMEPAGE_URL);
}



function ajaxUsersList() {
    $.ajax({
        url: USER_LIST_URL,
        success: function(data) {
            var users = objToStrMap(data);
            console.log(users)
            console.log("ajaxUsersList returned data with type:")
            console.log(typeof users)
            refreshUsersList(users);
        }
    })
        .done(setTimeout(ajaxUsersList,refreshRate))
}

function objToStrMap(obj) {
    let strMap = new Map();
    for (let k of Object.keys(obj)) {
        strMap.set(k, obj[k]);
    }
    return strMap;
}


//https://2ality.com/2015/08/es6-map-json.html

//users = an array of user json object, each one includes name and role.
// [{"name":"bibi","role":"customer"},{"name":"lala","role":"customer"}]
function refreshUsersList(users) {
    //clear all current users
    $("#userslist").empty();

    console.log("user list was emptied");
    console.log(users);

    var index = 0;
    users.forEach((role,name)=>{
        console.log("Adding user #" + index+ ": " + name + " " + role);
        $('<li>' + name + "-" + role + '</li>').appendTo($("#userslist"));
        index++;
    })
}

$(function() { // onload...do
    console.log("\ncurrentsession.js onload")
    ajaxCurrentUser();
});