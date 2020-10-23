/*
This file should be responsible for setting up the header on each page
 */

var CART_SUMMARY_URL = "../cartsummary/cart.html"

var $bell;

function updateUserName(){
    $("#userprofile").text(sessionStorage.getItem("username"));
}

function createHeader(){
    var user = sessionStorage.getItem("userName");
    var notifications = JSON.parse(sessionStorage.getItem("notifications"));
    $("#dropdown-list").empty()
    var messages = ``;
    $.each(notifications,function (i,item) {
        messages += `<li>${item}</li>`
    })

//prepend() - Inserts content at the beginning of the selected elements
    var content = `
    <div class="header">
            <ul class="navigation">

                <li>
                    <a href="#">Chat</a>
                </li>
                <li>
                    <button type="button" class="notification dropdown-toggle" >
                        <span>Notifications</span>
                        <span class="notification-messages" id="notification-messages">0</span>

                        <ul id="dropdown-list" class="dropdown">
                            ${messages}
                            <li><a href="#" style="color: rgba(115, 187, 22, 1)"><i class="fa fa-list"></i> View All Notifications</a></li>
                        </ul>

                    </button>

                </li>
                
                <li>
                    <button type="button" id="cart-btn" class="notification items-in-cart" >
                        <span>Cart</span>
                        <span class="badge" id="number-cart-items">3</span>
                    </button>
                </li>
                
                <li>
                    <a href="#" id="userprofile">${user}</a>
                </li>
            </ul>
        </div>
    `

    $("body").prepend(content);

    $("#cart-btn").on('click',function () {
        window.location.replace(CART_SUMMARY_URL);
    })
}


function createHeaderForOwner(){
    var user = sessionStorage.getItem("userName");
    var notifications = JSON.parse(sessionStorage.getItem("notifications"));
    $("#dropdown-list").empty()
    var messages = ``;
    $.each(notifications,function (i,item) {
        messages += `<li>${item}</li>`
    })

//prepend() - Inserts content at the beginning of the selected elements
    var content = `
    <div class="header">
            <ul class="navigation">

                <li>
                    <a href="#">Chat</a>
                </li>
                <li>
                    <button type="button" class="notification dropdown-toggle" >
                        <span>Notifications</span>
                        <span class="notification-messages" id="notification-messages">0</span>

                        <ul id="dropdown-list" class="dropdown">
                            ${messages}
                            <li><a href="#" style="color: rgba(115, 187, 22, 1)"><i class="fa fa-list"></i> View All Notifications</a></li>
                        </ul>

                    </button>

                </li>                                             
                <li>
                    <a href="#" id="userprofile">${user}</a>
                </li>
            </ul>
        </div>
    `

    $("body").prepend(content);

}


function updateNumberCartItemsBadge(numberItemsInCart){
    $('#number-cart-items').text(numberItemsInCart);
}

function openNotificationsList() {
    $("#dropdown-list").toggle(400)
    var numMessages = sessionStorage.getItem("lastNotificationAdded");
    sessionStorage.setItem("lastNotificationRead",numMessages)

    $bell.setAttribute('data-count', 0);
    $bell.classList.remove('show-count');
    $bell.classList.remove('notify');
}

function dropdownToggle(){
    // Dropdown toggle

    // $('.dropdown-toggle').click(function() {
    //     //$(this).next('.dropdown').toggle( 400 );
    //     $("#dropdown-list").toggle(400)
    // });

    $('.dropdown-toggle').click(openNotificationsList);

    //https://stackoverflow.com/questions/32346507/notification-message-using-php-and-jquery
    //https://www.geeksforgeeks.org/how-to-count-number-of-notification-on-an-icon/
    //https://stackoverflow.com/questions/10903526/how-to-toggle-a-bootstrap-alert-on-and-off-with-button-click
    //https://csshint.com/css-notification-bell-icon/

    $(document).click(function(e) {
        var target = e.target;
        if (!$(target).is('.dropdown-toggle') && !$(target).parents().is('.dropdown-toggle')) {
            $('.dropdown').hide() ;
            // $("#unread-messages-badge").innerText = 0;
            // $("#unread-messages-badge").hide();
        }
    });
}

//activate the timer calls after the page is loaded
$(function() {
    // Get HTML head element
    var head = document.getElementsByTagName('HEAD')[0];

    // Create new link Element
    var link = document.createElement('link');
    var link2 = document.createElement('link');

    // set the attributes for link element
    link.rel = 'stylesheet';
    link.type = 'text/css';
    link.href = '../header/header.css';
    link2.rel = 'stylesheet';
    link2.type = 'text/css';
    link2.href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css"

        // Append link element to HTML head
    head.prepend(link);
    head.prepend(link2)

    var role = sessionStorage.getItem("role");
    if (role === "owner"){
        createHeader();
        dropdownToggle()
        $bell = document.getElementById('notification-messages');

        $bell.addEventListener("animationend", function(event){
            $bell.classList.remove('notify');
        });
    }
    if (role === "customer"){
        createHeader();
    }


    updateUserName();


});