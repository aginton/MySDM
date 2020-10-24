
var refreshRate = 5000; //milli seconds
var USER_LIST_URL = buildUrlWithContextPath("userslist");
var CHAT_LIST_URL = buildUrlWithContextPath("chat");
var CURRENT_USER_URL = buildUrlWithContextPath("currentuser");

var UPLOAD_FILE_URL = buildUrlWithContextPath("uploadfile");
var ZONES_LIST_URL = buildUrlWithContextPath("zoneslist");
var ZONE_URL = "../page3zone/zone.html";


var chatVersion = 0;
var lastActivityVersionUpdated = 0;
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


//
// function onZoneTableClick(){
//
//     $("#fillname").val("onZoneTableClick called!");
//     $("#zones-table tbody tr").on("click", function(event){
//         $("#fillname").val($(this).find("td").eq(1).html());
//         var zone = ($(this).find("td").eq(1).html());
//         console.log("Should go to zone " + zone);
//         currentZone = zone;
//         sessionStorage.setItem("zone", zone);
//         window.location.replace(ZONE_URL);
//     });
// }

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





$(function() {
    onUploadFile();
    ajaxZonesTable();
    ajaxUsersList();
    updateTransactionTable();

    role = sessionStorage.getItem("role");
    if (role === "owner"){
        ajaxOwner();
    }

    //The chat content is refreshed only once (using a timeout) but
    //on each call it triggers another execution of itself later (1 second later)

    //triggerAjaxChatContent();
});