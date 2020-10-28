var OWNER_URL=buildUrlWithContextPath("currentowner")
var OWNER_ORDERS_URL=buildUrlWithContextPath("ownerorders")
var OWNER_FEEDBACK_BY_ZONE_URL = buildUrlWithContextPath("feedbackbyzone");

var lastNotificationAdded = 0;

function ajaxOwner(){
    console.log("ajaxOwner called")
    $.ajax({
        url: OWNER_URL,
        dataType: 'json',
        success: function (data) {
            console.log("ajaxOwner returned:")
            console.log(data);
            sessionStorage.setItem("notifications",JSON.stringify(data.notifications))

            var numberNotifications = data.notifications.length;
            var newNotifications = numberNotifications - sessionStorage.getItem("lastNotificationAdded");

            if (newNotifications !== 0){
                for (var i=0; i<newNotifications; i++){
                    $("#dropdown-list").prepend(`<li>${data.notifications[numberNotifications-i-1]}</li>`)
                }
                lastNotificationAdded = data.notifications.length;
                sessionStorage.setItem("lastNotificationAdded",lastNotificationAdded)
                var numberUnreadMessages = lastNotificationAdded - sessionStorage.getItem("lastNotificationRead");

                const $bell = document.getElementById('notification-messages');
                $bell.setAttribute('data-count', numberUnreadMessages);
                $bell.classList.add('show-count');
                $bell.classList.add('notify');
            }
        },
        error: function (error) {
            console.log("ajaxCustomerOrders returned error: " + error.message)
        }
    })
    setTimeout(ajaxOwner,refreshRate)
}

function createFeedbackTable() {
    var htmlContent = `<div class="modal" id="myModal">
                <div class="modal-dialog">
                    <div class="modal-content">

                        <!-- Modal Header -->
                        <div class="modal-header">
                            <h4 class="modal-title">Feedbacks for My Stores</h4>
                            <button type="button" class="close" data-dismiss="modal">&times;</button>
                        </div>

                        <!-- Modal body -->
                        <div class="modal-body">
                            <table id="feedbacks-table" class="table table-striped">
                                <thead>
                                <tr>
                                    <th>Date</th>
                                    <th>Store</th>
                                    <th>Customer</th>
                                    <th>Rating (1-5)</th>
                                    <th>Comments</th>
                                </tr>
                                </thead>
                                <tbody>

                                </tbody>
                            </table>
                        </div>

                        <div class="modal-footer">
                            <button type="button" class="btn btn-danger" data-dismiss="modal">Close</button>
                        </div>

                    </div>
                </div>
            </div>`

    return htmlContent;
}

function createModal(data) {
    var trHTML = '';
    data.forEach(function(feedback,index){
      trHTML += `<tr><td>${feedback.date}</td><td>${feedback.store}</td><td>${feedback.customer}</td><td>${feedback.rating}</td><td>${feedback.comments}</td></tr>`
    })
    $('#feedbacks-table tbody').empty().html(trHTML)

    $("#myModal").modal('show');
    //data-toggle="modal" data-target="#myModal"
}

function ajaxFeedbackByZone(zone) {
    $.ajax({
        url: OWNER_FEEDBACK_BY_ZONE_URL,
        data: {"zone": zone},
        dataType: 'json',
        success: function (data) {
            console.log("FeedbackByZone success, returned following:")
            console.log(data);
            createModal(data);
        },
        error: function (error) {
            console.log("FeedbackByZone error - " + error.message);
        }
    })
}

function ajaxOwnerOrders(callback){
    $.ajax({
        url: OWNER_ORDERS_URL,
        data: {"zone": sessionStorage.getItem("zone")},
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
            console.log("ajaxOwnerOrders returned:")
            console.log(data);
            sessionStorage.setItem("orders",JSON.stringify(data));
        },
        error: function (error) {
            console.log("ajaxOwnerOrders returned error: " + error.message)
        }
    })
        .done(function () {
            if (typeof callback === "function"){
                callback();
            }
        })

}

$(function() { // onload...do

    if (sessionStorage.getItem("role") === "owner"){
        console.log("\nowner.js onload")
    }
});