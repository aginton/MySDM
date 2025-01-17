LEAVE_FEEDBACK_URL = buildUrlWithContextPath("/leavefeedback");
var customer = sessionStorage.getItem("username");
var zone = sessionStorage.getItem("zone");
var rate = "rate-";
var star = "star-rate-";
var fdbk = "feedback-";

function buildFeedbackSection(storeId, storeName) {

    console.log("in buildFeedbackSection function");
    console.log("creating feedback section for store id:" + storeId + " store name: " + storeName);
    var radioBtnGroupName = rate+storeId;
    var starId = star+storeId;
    var sectionName = fdbk+storeId;
    var textAreaName = "text-area-"+storeId;
    var feedbackSection = '';

    feedbackSection += `
    <div id=${sectionName} data-store-name="${storeName}" class="feedback-section">
        <p>Leave feedback for store: <span>${storeName}</span></p>
        <div class="row">
            <div class="column side col-5">
                <div class="star-widget">
                    <input type="radio" name=${radioBtnGroupName} id="5-${starId}">
                    <label for="5-${starId}" class="fas fa-star"></label>
                    <input type="radio" name=${radioBtnGroupName} id="4-${starId}">
                    <label for="4-${starId}" class="fas fa-star"></label>
                    <input type="radio" name=${radioBtnGroupName} id="3-${starId}">
                    <label for="3-${starId}" class="fas fa-star"></label>
                    <input type="radio" name=${radioBtnGroupName} id="2-${starId}">
                    <label for="2-${starId}" class="fas fa-star"></label>
                    <input type="radio" name=${radioBtnGroupName} id="1-${starId}">
                    <label for="1-${starId}" class="fas fa-star"></label>
                    <header></header>
                </div>
            </div>
        <div class="column middle"></div>
        <div class="column side"></div>
    </div>
    <p>comments:</p>
    <textarea disabled id=${textAreaName} cols="70" placeholder="Describe your experience.."></textarea> 
    </div>
    <br>
    `

    $('#leavefeedbackform').append(feedbackSection);
}

function createFeedbackForm(storesArr) {

    console.log("in createFeedbackForm function");
    $('#middlediv').append("<form id=\"leavefeedbackform\" name='feedback-form'  method=\"POST\" action=\"leavefeedback\">")

    storesArr.forEach((element, index) => {
        buildFeedbackSection(element.id, element.name);
    });


    $('#leavefeedbackform').append('<button type="button" id="skip-btn">Skip this step</button>');
    $('#leavefeedbackform').append('<input type="submit" value="Send feedback" />');
    $('#middlediv').append("</form>");
}

function onSkipClick() {

    $("#skip-btn").on("click", function(event){
        window.location = "../page3zone/zone.html";
    })
}

function enableTextAreasWhenRated () {

    $('input[type="radio"]').on('click', function(){
            if($(this).is(":checked")) {
                var storeId = $(this).attr("name").slice(5);
                $("#"+"text-area-"+storeId).attr('disabled', false).focus();
            }
        }
    );
}

function Feedback(storename, rate, comments) {
    this.storename = storename;
    this.rate = rate;
    this.comments = comments;
}

const isCheckedRadioButton = element => element.type === 'radio' && element.checked;

const feedbackFormToJSON = elements => [].reduce.call(elements, (data,element) => {
    if (isCheckedRadioButton(element)) {
        var sectionDiv = element.parentNode.parentNode.parentNode.parentNode;
        var storename = sectionDiv.getAttribute('data-store-name');
        var rate = element.id.charAt(0);   //1-5 stars rate
        var comments = sectionDiv.getElementsByTagName('textarea')[0].value;
        console.log("in feedbackFormToJSON: creating feedback for store : " + storename + "rate: " + rate + " stars and comments: " + comments);
        var feedback = new Feedback(storename,parseInt(rate),comments);
        data["feedbacks"] = (data["feedbacks"] || []).concat(feedback);
    }

    // console.log("in feedbackFormToJSON ");
    // console.log(data);

    return data;
}, {});

//on load function
$(function() {

    // const arr = [{id: 1, name: 'store1' }, {id: 2, name: 'store2' }, {id: 3, name: 'store3' }];
    // sessionStorage.setItem('storesOrderedFrom', JSON.stringify(arr));
    // var storesArr = JSON.parse(sessionStorage.getItem('storesOrderedFrom'));

    var participatingStores = JSON.parse(sessionStorage.getItem("participatingStores"));
    createFeedbackForm(participatingStores);
    enableTextAreasWhenRated();
    onSkipClick();


        $("#leavefeedbackform").submit(function(e) {

            e.preventDefault();

            var form = document.getElementById('leavefeedbackform');
            //console.log("printing the feedbacks array before the ajax call:");
            var dataToPass = (feedbackFormToJSON(form.elements)).feedbacks;
            console.log("printing dataToPass:");
            console.log(dataToPass);
            console.log("--------------------------------")

            /*data sent to servlet in format:
            {
            "customer": "Moshe",
            "zone": "HaSharon",
            "feedbacks": [{"storename": "Maxstock",
                           "rate": "4",
                           "comments: "Great experience!"}, ...]
            }
            */
                $.ajax({
                    type: "POST",
                    url: LEAVE_FEEDBACK_URL,
                    data: {"customer": customer,
                           "zone": zone,
                           "feedbacks": JSON.stringify(dataToPass)},
                    success: function () {
                        alert("Thank you for the feedback! \n returning to zone page...");
                        window.location = "../page3zone/zone.html";
                    },
                    error: function (data) {
                        console.log("error occured on leave feedback ajax call - " + data.message);
                        alert("Unable to leave feedback. :(");
                    }
                })
        })
})
