var STORE_ITEMS_URL = buildUrlWithContextPath("chooseitems");
var DYNAMIC_ORDER_ADD_ITEM = buildUrlWithContextPath("additemdynamicorder");
var SALES_URL = "../choosediscounts/sales.html";
var CART_SUMMARY_URL = "../cartsummary/cart.html"

var orderType = sessionStorage.getItem("orderTypeStarted")
var zone = sessionStorage.getItem("zone");

var store;
var items;


function checkIfHasImage(itemName) {
    if (itemName.includes("cheeze")) {
        return "../../common/images/yellowcheeze.png";
    }
    if (itemName.includes("Toilet")) {
        return "../../common/images/toiletpapers.jpg";
    }
    if (itemName.includes("Tomato")) {
        return "../../common/images/tomato.png";
    } else {
        return "../../common/images/placeholder1.png";
    }
}


function ajaxGetChooseItems() {
    //var orderType = sessionStorage.getItem("orderTypeStarted");

    $.ajax({
        url: STORE_ITEMS_URL,
        data: {store: store, zone: zone, "orderTypeStarted": orderType},
        dataType: 'json',
        success: function (data) {
            /*
            For static order, data returned as:
            [{
                category: "",
                id: _,
                name: "",
                price: _,
            }...]

            For dynamic order, data returned as:
            [{
                category: "",
                id: _,
                name: "",
            }...]

             */
            console.log("getStoreItems returned following data:")
            console.log(data);
            items = data;
            createStoreItems(data);
        },
        error: function (err) {
            console.log(err);
        }
    })
}


function createItemsTableHead() {
    var res = `<table class="store-items-table" id="store-items-table">
                        <thead>
                        <tr>
                            <th scope="col"></th>
                            <th scope="col">ID</th>
                            <th scope="col">Item</th>
                            <th scope="col">CATEGORY</th>
                            `
    if (orderType === "static"){
        res += `<th scope="col">Price</th>`
    }
    res += `<th scope="col">ADD</th></tr></thead><tbody></tbody></table>`
    return res;
}

function createStoreItems(data) {
    var itemsTable = createItemsTableHead();
    $("#products").html(itemsTable);


    var trhtml = "<ul>"
    items.sort(compareItemID)

    $.each(items, function (i, item) {
        var imgResource = checkIfHasImage(item.name);

        trhtml += `<tr class="shop-item"><td><img src="${imgResource}" class="shop-item-image"></td>
                <td><span class="shop-item-id">${item.id}.</span></td><td><span class="shop-item-title">${item.name}</span></td>
                <td><span class="shop-item-category">${item.category}</span></td>
        `
        if (orderType === "static"){
            trhtml += `<td><span class="shop-item-price">${item.price}</span></td>`
        }
        trhtml += `<td><button class="btn-primary shop-item-button" type="button">Add to Cart</button></td>`

    })


    $("#store-items-table tbody").html(trhtml);

    ready();
}


function ready() {
    var addToCartButtons = document.getElementsByClassName('shop-item-button');
    for (var i = 0; i < addToCartButtons.length; i++) {
        var button = addToCartButtons[i];
        button.addEventListener('click', onAddToCartClicked);
    }

    document.getElementsByClassName('btn-sales')[0].addEventListener('click', function () {
        console.log("Should go to sales ");
        sessionStorage.setItem("zone", zone);
        window.location.replace(SALES_URL);
    })

    document.getElementsByClassName('btn-cart-summary')[0].addEventListener('click', function () {
        console.log("Should go to cart summary ");
        window.location.replace(CART_SUMMARY_URL);
    })


}


$(".btn-sales").on('click', function () {
    console.log("Should go to sales ");
    sessionStorage.setItem("zone", zone);
    window.location.replace(SALES_URL);
})

$(".btn-cart-summary").on('click', function () {
    window.location.replace(CART_SUMMARY_URL);
})

$(".btn-update-cart").on('click', function () {
    checkIfAmountInputsValid();
})


//TODO: Add a quantity input
function onAddToCartClicked(event) {
    var button = event.target;
    var shopItem = button.parentElement.parentElement;
    var id = shopItem.getElementsByClassName('shop-item-id')[0].innerText;
    var category = shopItem.getElementsByClassName('shop-item-category')[0].innerText;
    var title = shopItem.getElementsByClassName('shop-item-title')[0].innerText;

    var orderType = sessionStorage.getItem("orderTypeStarted");

    if (orderType === "static"){
        var price = shopItem.getElementsByClassName('shop-item-price')[0].innerText;
        // addItemToCartForStaticOrder(id, title, category, price, store);
        //addItemToCartForStaticOrder(id, store);
        addItemForStaticOrder(parseInt(id),store,zone)

    } else if (orderType === "dynamic"){
        addItemForDynamicOrder(parseInt(id), zone);
    }
}

function addItemForDynamicOrder(id, zone) {
    console.log("addItemForDynamicOrder called!")
    $.ajax({
        url: DYNAMIC_ORDER_ADD_ITEM,
        data: {"zone": zone, "id": id},
        success: function () {
            console.log("addItemForDynamicOrder success!")
        },
        error: function (error) {
            console.log("addItemForDynamicOrder error! - " + error.message)
        }
    })
        .done(ajaxCustomerCart())
}




function getIndexOfItemInCart(id, title, store) {
    var regularCart = JSON.parse(sessionStorage.getItem("regularCart"));
    console.log(`getRegularItemFromCart() called with params: id=${id}, title=${title}, store=${store}`)

    for (var i = 0; i < regularCart.length; i++) {
        var itemFromCart = regularCart[i];

        if (itemFromCart.id === id && itemFromCart.store === store && itemFromCart.name === title) {
            return i;
        }
    }
    return -1;
}


$(function () { // onload...do
    console.log("\nstoreitems.js")
    if (orderType == "static") {
        store = sessionStorage.getItem("store");
        $("#header-txt").text(`Choose Items From ${store}`)
    } else if (orderType == "dynamic") {
        $("#header-txt").text(`Choose Items For Dynamic Order`)
    }

    ajaxGetChooseItems();
});

