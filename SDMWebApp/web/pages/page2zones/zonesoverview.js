
var refreshRate = 5000; //milli seconds
var USER_LIST_URL = buildUrlWithContextPath("userslist");

var UPLOAD_FILE_URL = buildUrlWithContextPath("uploadfile");
var ZONES_LIST_URL = buildUrlWithContextPath("zoneslist");
var DEPOSIT_URL = buildUrlWithContextPath("deposit");
var ZONE_URL = "../page3zone/zone.html";


var onlineUsersVersion = 0;
var numberTransactionsInTable = 0;



function ajaxZonesTable(){

    $.ajax({
        url: ZONES_LIST_URL,
        dataType: 'json',
        success: function(response){
            /*
            [{ averageOrderCost: 0, founder: "adan", name: "Hasharon", numberOfItems: 10, numberOfStores: 4, zoneVersion: 1}, ...{}]
             */
            // console.log("ajaxZonesTable returned following data:")
            // console.log(response)

            var trHTML = '';
            $.each(response, function(i,item){
                trHTML += `<tr><td>${item.founder}</td><td>${item.name}</td><td>${item.numberOfItems}</td><td>${item.numberOfStores}</td><td>${item.averageOrderCost}</td></tr>`
            });
            $('#zones-table tbody').empty().html(trHTML)

            //onZoneTableClick();

            $("#fillname").val("onZoneTableClick called!");
            $("#zones-table tbody tr").on("click", function(event){
                $("#fillname").val($(this).find("td").eq(1).html());
                var zone = ($(this).find("td").eq(1).html());
                console.log("Should go to zone " + zone);
                currentZone = zone;
                sessionStorage.setItem("zone", zone);
                window.location.replace(ZONE_URL);
            });
        }
    })
        .done(function () {
            setTimeout(ajaxZonesTable, refreshRate);
        })
}


function onUploadFile(){
    console.log("onSubmitFileClick called!")
    $("#file-upload-form").on("submit",function(e){
        // cancel the default behavior
        e.preventDefault();

        $.ajax({
            url: UPLOAD_FILE_URL,
            type: "POST",
            data: new FormData(this),
            enctype: "multipart/form-data",
            processData: false,
            contentType: false,
            success: function(data){
                $("#filesubmitmessage").text(data);
            },
            error: function(err){
                console.log(err)
            }
        })
    })

};


function onDepositClicked(){
    console.log("onDepositClicked called!")

    $("#deposit-form").on("submit",function(e){
        // cancel the default behavior
        e.preventDefault();

        $.ajax({
            url: DEPOSIT_URL,
            type: "POST",
            data: $(this).serialize(),
            success: function(){
                alert("Deposit Successful!")
            },
            error: function(err){
                alert("Unable to perform deposit. Please try again.")
            }
        })
            .done($("#deposit-section").html(""))
    })
};





function updateTransactionTable() {
    /*
    activitySummaries: [amount: "138.28", balanceAfter: "-138.28", balanceBefore: "0.00", date: "17-10-2020 02:10", type: "withdrawal"},...{}],
     */
    var transactions = JSON.parse(sessionStorage.getItem("transactions"));
    if (numberTransactionsInTable !== transactions.length){
        // var entries = JSON.parse(sessionStorage.getItem("transactions"));
        console.log("updateTransactionsTable has data:")
        console.log(transactions)
        var rows = ``;
        $.each(transactions,function (i,item) {
            rows += `
        <tr><td>${item.type}</td><td>${item.date}</td><td>${item.amount}</td><td>${item.balanceBefore}</td>
        <td>${item.balanceAfter}</td></tr>
        `
        })

        $('#activity-history-table tbody').empty().html(rows)
        numberTransactionsInTable = transactions.length;
    }
    setTimeout(updateTransactionTable,refreshRate);
}

function createDepositSection(){
    console.log("creating upload file button")

//    <h2 class="h2inline">To start a dynamic order, </h2><button type="button" id="dynamic-order-btn">Click Here</button><h2 class="h2inline">!</h2>


    var depositBtn = `<p class="p-inline">Want to deposit money? Click here: <button type="button" id="deposit-btn" onclick="createDepositForm()">Deposit</button></p><br><br><div id="deposit-section"></div>`

    $(".container-column1of2").prepend(depositBtn)

}

function isEmpty() {
    return ($("div.container-deposit").length === 0);
}

function createDepositForm() {
    if (!isEmpty()){
        console.log("Form already on screen!")
        return;
    }

    var content = `<div class="container-deposit">
                <form id="deposit-form">
                    <p>Enter deposit amount:</p>
                    <input type="number" step="0.01" name="deposit-amount" id="deposit-amount">
                    <br/>
                    <br/>
                    <input id="submitbutton" type="submit" value="Deposit">
                </form>
            </div>
            <br><br>`

    $("#deposit-section").prepend(content)

    onDepositClicked()
}




function createUploadFileSection(){
    console.log("creating upload file button")

    var content = `            <div class="container-uploadform">
                <form id="file-upload-form">
                    <p>What file do you want to upload?</p>
                    <input type="file" name="fileToUpload" id="file2upload">
                    <br/>
                    <br/>
                    <input id="submitbutton" type="submit" value="Upload File">
                </form>
            </div>
            <br><br>
            <p id="filesubmitmessage"></p>`
    $(".container-column1of2").prepend(content)
}

// function zonesUpdateUsersList(){
//     if (onlineUsersVersion !== sessionStorage.getItem("onlineUsersVersion")){
//         var usersList = createOnlineUsersList();
//         $("#userslist").empty();
//         $("#userslist").html(usersList);
//         onlineUsersVersion = sessionStorage.getItem("onlineUsersVersion");
//     }
//     setTimeout(zonesUpdateUsersList,refreshRate);
// }

function createUsersList(){
    if (onlineUsersVersion !== sessionStorage.getItem("onlineUsersVersion")){
        var usersList = createOnlineUsersList();
        $("#userslist").empty();
        $("#userslist").html(usersList);
        onlineUsersVersion = sessionStorage.getItem("onlineUsersVersion");
    }
}

$(function() {
    ajaxZonesTable();
    ajaxUsersList(createUsersList);
    // zonesUpdateUsersList();
    updateTransactionTable();

    role = sessionStorage.getItem("role");
    if (role === "owner"){
        ajaxOwner();
        createUploadFileSection();
        onUploadFile();
    }
    if (role === "customer"){
        createDepositSection();
    }
});