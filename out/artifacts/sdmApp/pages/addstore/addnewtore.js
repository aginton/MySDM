var ZONE_ITEMS_URL = buildUrlWithContextPath("zoneitems");
var ADD_STORE_URL = buildUrlWithContextPath("addnewstore");
var zone = sessionStorage.getItem("zone");
var username = sessionStorage.getItem("username");
var zoneVersion = 0;

function validateFields(form) {

    console.log("validating storename: " + form["storeName"].value)
    if (form["storeName"].value == ""){
        alert("Please fill store name");
        return false;
    }

    console.log("validating store location chosen");
    console.log(form["x-coordinate"].value + form["y-coordinate"].value);
    if (form["x-coordinate"].value === '0' || form["y-coordinate"].value === '0') {

        alert("Please select location");
        return false;
    }

    console.log("validating : " + form["ppk"].value)
    if (form["ppk"].value == "" || !Number.isInteger(parseInt(form["ppk"].value))) {
        alert ("Please fill store ppk field with a non-negative integer");
        return false;
    }

    //validate user chooses at least 1 item for store
    if (!isAtLeastOneItemChosen()) {
        alert("You must choose at least one item to sell in your store");
        return false;
    }

    //validate each chosen item has a price
    if (!eachChosenItemHasPrice(form)) {
        return false;
    }

    return true;
}

function eachChosenItemHasPrice(form) {
    console.log("inside eachChosenItemHasPrice() function");

    var checkedBoxes = $("input:checkbox(:checked)");
    checkedBoxes.each(function() {
        var itemId = $(this).attr('name');
        console.log("now checking the checkbox named: " + itemId);
        var priceTextFieldName = "givenprice-"+itemId;
        console.log("checking the price field named: " + priceTextFieldName);
        if (form[priceTextFieldName].value == "" || !Number.isInteger(parseInt(form[priceTextFieldName].value))) {
            alert("Please set a valid price for item id: " + itemId);
            return false;
        }
    })
    return true;
}


function isAtLeastOneItemChosen()  {

    var checkboxes = document.querySelectorAll('input[type="checkbox"]');
    console.log("there are:" + checkboxes.length + " checkboxes total");
   return Array.prototype.slice.call(checkboxes).some(x => x.checked);

}

function setCoordinatesSelection() {
    console.log("setCoordinateSelection function called");

    var x_locationOptions = "<option value = '0'> select </option>";
    for( var i = 1; i <= 50; i++) {
        x_locationOptions += "<option value='"+i+"'>"+i+"</option>";
    }
    console.log(x_locationOptions);
    document.getElementById("x-coordinate").innerHTML = x_locationOptions;

    var y_locationOptions = "<option value = '0'> select </option>";
    for( var i = 1; i <= 50; i++) {
        y_locationOptions += "<option value='"+i+"'>"+i+"</option>";
    }
    document.getElementById("y-coordinate").innerHTML = y_locationOptions;
}

//NOTE: Changed name from "createZoneItemsTable" to "createChooseStoreItemsTable" just to distinguish between
// "createZoneItemsTable" in zone.js
function createChooseStoreItemsTable(entries) {
    /*
    entries is array of form:
    [{  id:_ , name:_ , category: _, averagePrice: _, numberTimesSold: _}, {...},...]
     */
    var trHTML ='';


    //the name of each checkbox is the item id it is referring to
    //the name of each set-price text field is in format: <givenprice-itemid>
    $.each(entries,function (i,item) {

        console.log("Creating checkbox names: " + item.id);
        var currentSetPriceTextFieldName = "givenprice-" + item.id;
        console.log("Creating set-price ids: " + currentSetPriceTextFieldName);

        trHTML += `
        <tr>
        <td><input type="checkbox" name=${item.id}></td>
        <td>${item.id}</td>
        <td>${item.name}</td>
        <td>${item.category}</td>
        <td>${Number.parseFloat(item.averagePrice).toFixed(2)}</td>
        <td>${item.numberTimesSold}</td><
        <td><input type="text" class="priceGiven" name=${currentSetPriceTextFieldName} value="0"></td>
        </tr>
        `

    })
    $('#zone-items-table tbody').empty().html(trHTML)
}


//TODO: Maybe put ajaxZoneItems in currentsession.js and just call from there?
function getInventoryForZone() {

    console.log("\najaxZoneContent called")
    $.ajax({
        url: ZONE_ITEMS_URL,
        data: {zone: zone, zoneVersion: zoneVersion},
        dataType: 'json',
        success: function(data) {
            /*
             data will arrive in the next form:
            [{  id:_ , name:_ , category: _, averagePrice: _, numberTimesSold: _}, {...},...]
             */
            createChooseStoreItemsTable(data.entries);

        },
        error: function(error) {
            $('#errorMessagesForTable').append("<p>Some error occurred!!!</p>")
        }
    });

}


function updateZoneNameHeader() {
    //var zoneNameHeader = sessionStorage.getItem("zone");
    $('#zoneNameHeader').text("Current zone: " + zone);
}

const isValidElement = element => {
    return element.name && element.value;
}

const isValidValue = element => {
    return (!['checkbox'].includes(element.type) || element.checked);
}

const hasClassPriceGiven = element => {
    return (element.classList.contains("priceGiven"))
}

const isCheckbox = element => element.type === 'checkbox';

const formToJSON = elements => [].reduce.call(elements, (data,element) => {
    if (isValidElement(element) && isValidValue(element)) {
        if (isCheckbox(element)) {
            //item chosen to be added to the new store are saved as an array [key, value, key, value...].
            //key is the item id and value is the given price that the user entered for the item.
            var key = element.name;
            var valueName = "givenprice-"+key;
            var value = document.getElementsByName(valueName)[0].value;
            var itemPair = [key, value];
            console.log("adding key: " + key + " and value: " + value);
            data["items"] = (data["items"] || []).concat(itemPair);
        } else if (!hasClassPriceGiven(element)){
            data[element.name] = element.value;
        }
        data["zone"] = zone;
        data["owner"] = username;


        console.log(JSON.stringify(data));
    }
    return data;

}, {});
//
// function sendAlertToZoneFounder(data) {
//
//     //TODO: function isn't finished
//     alert("A new store was opened in your zone!\n" +
//           "Store owner name: " + username + "\n" +
//           "Store name: " + data.storeName + "\n" +
//           "Location: " + "(" + data.xLoc + "," + data.yLoc + ")\n" +
//           "Number of items sold: " + data.numOfItemsSold + "/" + data.totalNumOfItemsInZone);
// }


$(function(){

    updateZoneNameHeader();
    console.log(ADD_STORE_URL);
    setCoordinatesSelection();
    getInventoryForZone();

    $("#addstoreform").submit(function(e) {

        console.log(ADD_STORE_URL);

        e.preventDefault();

        var form = document.getElementById('addstoreform');
        console.log(formToJSON(form.elements));

        if (validateFields(form)){
            /*data sent to servlet in format:
    {
    "storename":"story",
    "zone":"Hasharon",
    "owner":"tom",
    "xcoordinate":"4",
    "ycoordinate":"11",
    "ppk":"4500",
    "items":["10","800","1","100"]
    }
 */

            /*data returned from servlet in format:
                {
                "isLocaionAvailable": true/false,
                 "xLoc": "4",
                 "yLoc": "5",
                 "storeName": "My Store",
                 "numOfItemsSold": "5",
                 "totalNumOfItemsInZone": "10"
                 }

             */

            $.ajax({
                type: "POST",
                url: ADD_STORE_URL,
                data: formToJSON(form.elements),    //form.elements is a HTMLFormControlsCollection
                dataType: "json",
                success: function (data) {
                    console.log("ajax call success on add store, returned following data:!");
                    console.log(data)

                    if (data.success == true) {
                        alert("Store was added successfully!");

                        //sendAlertToZoneFounder(data);
                        //window.location = "../page3zone/zone.html";
                    }
                    else {              //chosen location is not available
                        alert("Unavailable location:\n There is already a store in (" + data.loc[0] + "," + data.loc[1] + ")");
                    }
                },
                error: function (data) {
                    console.log("error occured on add store ajax call");
                    alert("Unable to add a new store. Please try again");
                }

            })
        }
    })
});