
var GET_DISCOUNTS_URL = buildUrlWithContextPath("choosediscounts");
var USE_DISCOUNT_URL = buildUrlWithContextPath("usediscount")


var store = sessionStorage.getItem("store");
var zone = sessionStorage.getItem("zone");
var cart = sessionStorage.getItem("cart")

var numberTimesSubmitClicked = 0;
var entitledDiscounts;

function createDiscountForms(entitledDiscounts) {
    /* [{discountCondition: {id: 1, name: "Ketshop", category: "quantity", quantity: 1}
            discountName: "Balabait ishtagea !",
            discountOffers: {operator: "ONE-OF", offers: [{quantity: 1, id: 2, name: "Banana", forAdditional: 0, category: "weight"},…]},
            entitledTimes: 3
            store: "super baba"
            }]
   */
    console.log("createDiscountForms received data:")
    console.log(entitledDiscounts)
    $("#discounts-list").empty()

    var choiceSection = ``;
    var discountsList = '';

    $.each(entitledDiscounts, function (i,discount) {
        var formid = "discount-form-" + i;
        var submitid = "btn" + i;
        var conditionDescription = `If you buy ${discount.discountCondition.quantity}`
        var unit = discount.discountCondition.category == "weight" ? "kgs" : "pcks";
        var operator = discount.discountOffers.operator;
        var operatorString;
        if (operator === "ALL-OR-NOTHING"){
            operatorString = "(all-or-nothing):"
        }

        if (operator === "ONE-OF")
            operatorString = "(choose one):"
        else
            operatorString =":";

        conditionDescription += unit + ` of item ${discount.discountCondition.id} (${discount.discountCondition.name}), then you get the following ${operatorString}`

        var lihtml = `<li><h1>${discount.discountName}</h1><p>${conditionDescription}</p>`
        var offersList = `<ul>`

        for (var j = 0; j<discount.discountOffers.offers.length; j++){
            var offer = discount.discountOffers.offers[j];
            var offerCategory = offer.category == "weight" ? "kgs" : "pcks";
            var offerDescription = `${offer.quantity} ${offerCategory} of item ${offer.id} (${offer.name}) for additional ${offer.forAdditional}`;
            offersList += `<li>${offerDescription}</li>`
            if (operator == "ONE-OF"){
                choiceSection += `<option value="${offer.name}">${offerDescription}</option>`
            }
        }

        offersList += `</ul>`
        lihtml += offersList;
        lihtml += `<p>You can use this discount up to ${discount.entitledTimes} times</p>`


        lihtml += `<br><br><form id="${formid}" name="${formid}" onsubmit="return false;">
                        <input type="hidden" name="conditionItemId" value="${discount.discountCondition.id}">
                    <input type="hidden" name="conditionItemQuantity" value="${discount.discountCondition.quantity}">
                   <input type="hidden" name="discountName" value="${discount.discountName}">
                   <input type="hidden" name="discountOperator" value="${operator}"> 
                   <input type="hidden" name="zoneName" value="${zone}">
                   <input type="hidden" name="storeName" value="${discount.store}">
                    `
        if (operator == "ONE-OF"){
            lihtml += `<label for="offer">Choose an offer:</label><select name="offer" id="offer">` + choiceSection  +`</select>`;
        }
        if (operator == "IRRELEVANT"){
            lihtml += `<input type="hidden" name="offer" value="${discount.discountOffers.offers[0].name}">`
        }
        lihtml += `<br><br><input type="submit" name="${submitid}" id="${submitid}"  value="Submit"></form>`  //onclick="onSubmitButtonClick(this.form)"
        discountsList += lihtml;
    })

    $("#discounts-list").append(discountsList)
    onSubmitButtonClick(entitledDiscounts)
}


function onSubmitButtonClick(entitledDiscounts) {
    console.log("submit button clicked " + numberTimesSubmitClicked + " times!")

    $.each(entitledDiscounts,function (i,item) {
        var formid = "discount-form-" + i;
        var submitid = "btn" + i;
        $(`#${submitid}`).click(function () {
            var formData = $(`#${formid}`).serialize();
            console.log("formData:")
            console.log(formData)
            $.ajax({
                        url: USE_DISCOUNT_URL,
                        data: 'POST',
                        data: $(this.form).serialize(),
                        dataType: 'json',
                        success: function (data) {
                            console.log("onSubmitButtonClick success, returned following data!")
                            console.log(data)
                        },
                        error: function (error) {
                        console.log("sales.js - onSubmitButtonClick error! " + error.message)
                        }
                    })
                        .done(function () {
                            ajaxCustomerCart(getEntitledDiscounts)
                        })

        })
    })
}



function getEntitledDiscounts(){
    $.ajax({
        url: GET_DISCOUNTS_URL,
        data: {store: store, zone: zone},
        dataType: 'json',
        success: function (data) {
            /*
            entitledDiscountJS: [
                {discountCondition: {id: 1, name: "Toilet Paper", category: "quantity", quantity: 1}
                discountName: "YallA BaLaGaN"
                discountOffers: {operator: "ONE-OF", offers: [{quantity: 1, id: 1, name: "Toilet Paper", forAdditional: 0, category: "quantity"},...]}
                entitledTimes: 1
                store: "Rami"}, ... {}]
             */
            console.log("\n\ngetSales() returned following data!")
            console.log(data);
            entitledDiscounts = data.sort(compareDiscountName);
            sessionStorage.setItem("entitledDiscounts",JSON.stringify(data))
            createDiscountForms(entitledDiscounts)
        },
        error: function (err) {
            console.log(err);
        }
    })
}

$(function() { // onload...do

    getEntitledDiscounts();

});
