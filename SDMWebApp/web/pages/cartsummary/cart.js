var CONFIRM_ORDER_URL = buildUrlWithContextPath("confirmorder");
var CONTINUE_SHOPPING_URL= "../chooseitems/storeitems.html";
var SALES_URL = "../choosediscounts/sales.html";


var tolerance = 0.0000000001;
var cartSubtotal;
var xpos = 1;
var ypos = 1;

function createCart() {
    console.log("createCart() called")
    cartSubtotal = 0;
    /*
    entries is array of form:
    [{  id:_ , name:_ , category: _, averagePrice: _, numberTimesSold: _}, {...},...]
     */
    var trHTML ='';
    var entries = JSON.parse(sessionStorage.getItem("regularCart"));
    entries.sort(compareItemID)

    console.log("regularEntries has following form:")
    console.log(entries);

    $.each(entries,function (i,item) {

        if (item.category=="weight"){
            trHTML += `
            <tr><td class="cart-item-id">${item.id}</td><td class="cart-item-name">${item.name}</td><td class="cart-item-category">${item.category}</td>
            <td class="cart-item-amount"><input type="number" step="0.1" value="${item.amount}"></td>
        <td class="cart-item-price">${item.price}</td><td class="cart-item-store">${item.store}</td></tr>
        `
        } else{
            trHTML += `
            <tr><td class="cart-item-id">${item.id}</td><td class="cart-item-name">${item.name}</td><td class="cart-item-category">${item.category}</td>
            <td class="cart-item-amount"><input type="number" value="${item.amount}"></td>
        <td class="cart-item-price">${item.price}</td><td class="cart-item-store">${item.store}</td></tr>
        `
        }

        cartSubtotal += item.price * item.amount;
    })

    $("#stotal").text(cartSubtotal)
    $('#table-cart-summary tbody').empty().html(trHTML)
    //updateCartSubtotal(entries);
    createDiscountCart();
}

function createDiscountCart(){
    console.log("createCart() called")
    /*
    entries is array of form:
    [{  id:_ , name:_ , category: _, averagePrice: _, numberTimesSold: _}, {...},...]
     */
    var trHTML ='';


    var entries = JSON.parse(sessionStorage.getItem("discountsUsed"));
    /*
    [{  discountName: "YallA BaLaGaN"
        offersJS: [{name: "Toilet Paper", quantity: 1, id: 1, forAdditional: 0}]
        0: {name: "Toilet Paper", quantity: 1, id: 1, forAdditional: 0}
        storeName: "Rami"
        timesUsed: 6},
        ...{}]
     */
    console.log("regularEntries has following form:")
    console.log(entries);

    $.each(entries,function (i,item) {
        var discountName = item.discountName;
        var storeName = item.storeName;
        var timesUsed = item.timesUsed;
        var offersUsed = ``;


        trHTML += `
            <tr><td class="cart-item-store">${storeName}</td><td class="cart-item-discount-name">${discountName}</td>
            <td><table><thead>
            <tr><th scope="col">ID</th><th scope="col">Item</th><th scope="col">Qty</th><th scope="col" colspan="2">For Additonal</th></tr>
            </thead><tbody>
            `
        for (var i = 0; i<item.offersChosen.length; i++){
            // var offer = {"name": item.offersJS[i].name, "quantity": item.offersJS[i].quantity, "id": item.offersJS[i].id, "forAdditional": item.offersJS[i].forAdditional}
            var offer = item.offersChosen[i];
            offersUsed += `<tr><td class="cart-item-id">${offer.id}</td><td class="cart-item-name">${offer.name}</td>
            <td class="cart-item-amount">${offer.quantity}</td>
            <td class="cart-item-price">${offer.forAdditional}</td></tr>`
            cartSubtotal += offer.forAdditional * timesUsed;
        }

        trHTML += offersUsed;

        trHTML += `
            </tbody></table></td><td class="times-used">${timesUsed}</td><td><button class="btn-primary decrement-discount-btn" type="button">Decrement</button></td></tr>`
    })

    $("#stotal").text(cartSubtotal.toFixed(2))
    $('#table-discount-summary tbody').empty().html(trHTML)
    onDecrementDiscountBtnClicked()
}



function onDecrementDiscountBtnClicked(){

    var discounts = JSON.parse(sessionStorage.getItem("discountsUsed"));

    $(".decrement-discount-btn").on('click', function () {
        console.log("onDecrementBtnClicked!")
        console.log(this)
        var grandparent = this.parentElement.parentElement;
        console.log(grandparent)
        var index = grandparent.rowIndex;
        var discountRefToDecrement = discounts[index-1].discountUsedRef;
        alert(`decrementing discount with ref ${discountRefToDecrement}`)
        decrementDiscountUsage(discountRefToDecrement, createCart)
    })
}

function onUpdateClicked(){
    var problems = '';

    var tmpArray = [];

    var cartRows = $("#table-cart-summary tbody").children('tr');
    console.log(cartRows);
    var areValidAmounts = true;

    for (var i =0; i<cartRows.length; i++){
        console.log("\n----------------------------------------------------------------------------\n")
        console.log(cartRows[i])
        var id;
        var name;
        var category;
        var amount;
        var store;
        var price;

        var children = cartRows[i].childNodes;
        for (var j = 0; j<children.length; j++){
            if (children[j].className === "cart-item-id")
                id = children[j].innerText;
            if (children[j].className === "cart-item-name")
                name = children[j].innerText;
            if (children[j].className === "cart-item-category")
                category = children[j].innerText;
            if (children[j].className === "cart-item-price")
                price = children[j].innerText;
            if (children[j].className === "cart-item-amount")
                amount = children[j].childNodes[0].value;
            if (children[j].className === "cart-item-store")
                store = children[j].innerText;
        }

        if (isNaN(amount)){
            problems += `amount for item ${id} (${name}) is not a number!\n`
            areValidAmounts = false;
        }
        if (category === "quantity" && Math.abs(parseFloat(amount) - parseInt(amount))> tolerance){
            problems += `item ${id} (${name}) is of type quantity, but ${amount} is not an integer!\n`
            areValidAmounts = false;
        }

        if (areValidAmounts){
            console.log(`Creating item with id=${id},name=${name},amount=${amount},category=${category},price=${price},store=${store}`)
            tmpArray.push(new Item(parseInt(id),name,amount,category, price,store));
        }
    }
    if (areValidAmounts){
        console.log("All new amounts are valid! The array to be updated for is:")
        console.log(tmpArray);
        var isDiscountsUsedAffected = checkIfDiscountsUsedAffected(tmpArray);

        if (isDiscountsUsedAffected){
            var cback = function(){
                ajaxCustomerCart(createCart)
            }
            saveCartAtServer(tmpArray, cback)
        } else{
            createCart();
            return;
        }
    } else{
        alert(problems)
        createCart()
    }
}

function checkIfDiscountsUsedAffected(tmpArray) {
    var ans = true;
    console.log("checkIfDiscountsUsedAffected passed in tmpArray:")
    console.log(tmpArray);
    var discountsUsedOverview = JSON.parse(sessionStorage.getItem("discountsUsedOverview"));
    console.log("checkIfDiscountsUsedAffected comparing with discountsUsedOverview:")
    console.log(discountsUsedOverview)


    for (var i=0; i< discountsUsedOverview.length; i++){
        var discountName = discountsUsedOverview[i].discountName;
        var id = discountsUsedOverview[i].condition.id;
        var qnty = discountsUsedOverview[i].condition.quantity;
        var timesDiscountUsed = discountsUsedOverview[i].timesUsed;

        for (var j = 0; j<tmpArray.length; j++){
            if (tmpArray[j].id === id){
                var newEntitledAmount = tmpArray[j].amount / qnty;
                if (newEntitledAmount < timesDiscountUsed){
                    alert(`For new amounts, discount ${discountName} can only be used ${newEntitledAmount} times. Please update discounts used list to continue.`)
                    ans = false;
                }
            }
        }
    }
    return ans;

}

function checkIfThisAffectsDiscounts(tmpArray) {
    var itemsWithLowerAmounts = [];
    var oldItemsArray = JSON.parse(sessionStorage.getItem("regularCart"));
    var isLoweredAmount = false;
    for (var i = 0; i<oldItemsArray.length; i++){
        if (tmpArray[i].amount < oldItemsArray[i].amount){
            itemsWithLowerAmounts.push(i)
            isLoweredAmount = true;
        }
    }
    return isLoweredAmount;
}

function createXYChoices(){
    for (var i = 2; i<=50; i++){
        $("#x-choice").append(`<option value=${i}>${i}</option>`)
        $("#y-choice").append(`<option value=${i}>${i}</option>`)
    }

    $("#x-choice").change(function(){
        xpos = $(this).children("option:selected").val();
    });

    $("#y-choice").change(function(){
        ypos = $(this).children("option:selected").val();
    });
}

function onConfirmOrderClick(){
    var numberItemsInCart = sessionStorage.getItem("numberCartItemsByType");
    if (parseInt(numberItemsInCart) === 0){
        alert("Cannot create an order for an empty cart!")
        return;
    }

    var orderType = sessionStorage.getItem("orderTypeStarted");
    var zoneName = sessionStorage.getItem("zone");

    $.ajax({
        url: CONFIRM_ORDER_URL,
        data: {"x-position": xpos, "y-position": ypos, "orderType":orderType, "zoneName": zoneName},
        dataType: 'json',
        success: function (data) {

            console.log("confirm success!, returned:")
            console.log(data);
            if (data === true){
                alert("Order successfully created!")
                emptyCartInProgress(goToLeaveFeedbackPage())
            } else{
                alert("This location is currently taken. Please enter a different location and try again.")
            }
        },
        error: function (error) {
            console.log("confirm error! - " + error.message)
        }
    })
}

$(function () {
    cartSubtotal = 0;
    createXYChoices();
    $("#update-cart").on('click', onUpdateClicked)
    $("#confirm-order").on('click', onConfirmOrderClick)
    $("#continue-shopping-btn").on('click',function () {
        window.location.replace(CONTINUE_SHOPPING_URL);
    })

    $("#goto-sales-btn").on('click',function () {
        window.location.replace(SALES_URL);
    })

    var onEmptyCall = function(){
        alert("Cart Emptied!")
        emptyCartInProgress(createCart)
    }

    $("#empty-cart").on('click',onEmptyCall)

    ajaxCustomerCart(createCart)
})
