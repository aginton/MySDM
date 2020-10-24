function buildFeedbackSection(storeName) {

    var feedbackSection = '';

    feedbackSection += `
    <div class="feedback_sec">
    
    <p>Leave feedback for store: <span>${storeName}</span></p>
    <p>rate 1-5 stars</p>
    <br>
    <p>comments:</p>
    <input type="text">
    </div>
    `

    $('feedback_container').empty().html(feedbackSection);
}


function createFeedbackForm(storeNames) {

    //get all stores that the user ordered from
    storeNames.forEach((storeName) => {
        buildFeedbackSection(storeName);
    });
}

//on load function
$(function() {


    const obj = { storeNames: ['store1', 'store2', 'store3'] };
    sessionStorage.setItem('storesOrderedFrom', JSON.stringify(obj));
    var storeNamesArray = JSON.parse(sessionStorage.getItem('storesOrderedFrom')).storeNames;


    createFeedbackForm(storeNamesArray);



}

var userData = {
    storeUserDataInSession: function(userData) {
        var userObjectString = JSON.stringify(userData);
        window.sessionStorage.setItem('userObject',userObjectString)
    },
    getUserDataFromSession: function() {
        var userData = window.sessionStorage.getItem('userObject')
        return JSON.parse(userData);
    }
}