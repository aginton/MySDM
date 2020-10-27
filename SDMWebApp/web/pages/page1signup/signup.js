var LOGOUT_URL = buildUrlWithContextPath("logout");
var LOGIN_URL = buildUrlWithContextPath("login");
var PAGE2_URL = "../page2zones/zonesoverview.html"





function callLogoutServlet(){
    console.log("calling Logout servlet")
    $.ajax({
        url: LOGOUT_URL,
        success: function () {
            console.log("Logout returned successfully")
        },
        error: function () {
            console.log("Logout returned error")
        }
    })
}


$(function () {
    sessionStorage.clear();
    callLogoutServlet();
    //onLoginClicked();

    var chkYes = document.getElementById("chkYes");

    $("form").on('change',function () {
        var selected_value = $("input[name='role']:checked").val();
        if (selected_value==='owner'){
            $("#role-description").text("I don't need to buy things, I just want to open stores and make money!!!")
            $("#my_image").attr("src","../../common/images/catboss.jpg");
        }

        if (selected_value==='customer'){
            $("#role-description").text("I don't care about opening up stores, I just want to buy stuff!!!")
            $("#my_image").attr("src","../../common/images/dogcustomer.jpg");
        }
    })


    $("form").on("submit", function(event) {
        event.preventDefault();
        var name = $(this).find('[name=username]').val();

        var radioValue = $("input[name='role']:checked").val();

        if (name.length === 0){
            alert("You must enter a name!")
            return;
        }

        console.log(radioValue)
        console.log(typeof radioValue)
        if (radioValue !== "owner" && radioValue !== "customer"){
            alert("Must specify owner or customer role!")
            return;
        }

        //alert(`checking if username=${name} is taken or not, with role=${radioValue}`)
        $.ajax({
            url: LOGIN_URL,
            data: {"username": name, "role": radioValue},
            dataType: 'json',
            success: function(data){
                /* {isValid: false, errorMessage: "Username ADAM already exists. Please enter a different username."} */
                console.log(data)
                console.log(data.isValid)
                if (data.isValid){
                    sessionStorage.setItem("username",name)
                    sessionStorage.setItem("role",radioValue)
                    sessionStorage.setItem("balance",0)
                    sessionStorage.setItem("transactions",JSON.stringify([]))
                    sessionStorage.setItem("lastNotificationRead",0)
                    sessionStorage.setItem("lastNotificationAdded",0)
                    window.location.replace(PAGE2_URL);
                }else{
                    $("#servlet-response").text(data.errorMessage);
                }
            },
            error: function(err){
                console.log("error - " + err.message)
            }
        })
    });

})