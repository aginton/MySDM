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
var LEAVE_FEEDBACK_URL = "../givefeedback/givefeedback.html";

var userName;
var role;
var balance;
var messages;
var lastMessageRead = 0;
var numberOfUnreadMessages; //this member is unnecessary, but just for keeping track of things we'll keep it for now
var cart;
var transactions = [];
var onlineUsersVersion = 0;

//var discountCart = [];
//var orderHistory = [];
// var orderSummaryVersion = 0;
// var activityVersion = 0;
// var oldBalance;


var currentZone;

//var printEvery5Calls = 5;


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

// function DiscountUsedOverview(discountName, conditionID, conditionQuantity, totalTimesUsed){
//     this.discountName = discountName;
//     this.conditionID = conditionID;
//     this.conditionQuantity = conditionQuantity;
//     this.totalTimesUsed = totalTimesUsed;
// }

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

function goToLeaveFeedbackPage() {
    window.location.replace(LEAVE_FEEDBACK_URL);
}


function goToHomepage() {
    window.location.replace(HOMEPAGE_URL);
}



function ajaxUsersList(callback) {
    $.ajax({
        url: USER_LIST_URL,
        success: function(data) {
            console.log("ajaxUsersList returned following:")
            console.log(data);
            /*
            {   onlineVersion: 2,
                onlineUsers: [{name: "adam", role: "owner"}, ... {name: "johb", role: "customer"}]
            }
             */


            if (onlineUsersVersion !== data.onlineVersion){
                sessionStorage.setItem("onlineUsersVersion",data.onlineVersion);
                sessionStorage.setItem("onlineUsers",JSON.stringify(data.onlineUsers));
                //refreshUsersList(data.onlineUsers);
            }

            // var users = objToStrMap(data);
            // console.log("ajaxUsersList returned following data:")
            // console.log(users)
            // console.log(typeof users)
            //refreshUsersList(users);
        }
    })
        .done(function () {
            setTimeout(ajaxUsersList,refreshRate)
            if (typeof callback === 'function'){
                callback();
            }
        })
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
    users.forEach((user)=>{
        console.log("Adding user #" + index+ ": " + name + " " + role);
        $('<li>' + user.name + "-" + user.role + '</li>').appendTo($("#userslist"));
        index++;
    })
}

function createOnlineUsersList(){
    var users = JSON.parse(sessionStorage.getItem("onlineUsers"));
    console.log("createOnlineUsersList creating list for folling data:");
    console.log(users);
    var list = '';

    var index = 0;
    users.forEach((user)=>{
        list += '<li>' + user.name + "-" + user.role + '</li>'
    })
    return list;
}

$(function() { // onload...do
    console.log("\ncurrentsession.js onload")
    ajaxCurrentUser();
});