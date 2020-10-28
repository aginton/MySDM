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
                    $("#servlet-response").text(data);
                }
            },
            error: function(err){
                console.log("error - " + err.message)
            }
        })
    });

})