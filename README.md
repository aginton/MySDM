# MySDM

<h2>Hierarchy</h2>

    SDMEngine
    - src
    -- models
    -- resources
    
    SDMWebApp
    - src
    -- sdm
    --- constants
    --- servlets
    --- utils
    - web
    -- common
    -- pages
    -- WEB-INF


<h2>Tools Used</h2>
This is a basic Java web application that is meant to simulate a server hosting an online shopping and comparison website. 
This project uses Java for the backend, and regular HTML, CSS, and JavaScript for the frontend. 


<h2>Description</h2>
Initially, the user is directed to a signup page. A user must select a unique username and can register as either a customer or owner. Upon entering valid information (verified by the server), the user is taken to page 2, where they can see an overview of all zones in the system as well as a history of their transactions.

Owners have the option of uploading new zones to to the server, where each zone is represented within an XML file. When an owner tries uploading a new file, the server tries to validate it based on a given XML Schema (under `SDMEngine --> resources --> schema`). If the file is valid, and the zone name is not taken, then the server uses JAXB to unmarshall the file, creates the appropriate Java Object instances, and adds them to the system. If the file is invalid or the zone name is taken, a message describing the problem is displayed in the browser.

Each zone contains its own inventory, as well as a set of stores that sell items from the given inventory. When an owner uploads a new zone to the server, he is registered
as the owner of all the stores initially in the given zone. Once a zone is created, other owners can create new stores in the zone. 


A customer can choose a store and place either a static or dynamic order. In a static order, the customer selects the store they wish to order from and orders as usual. In a dynamic order, the customer selects the zone they wish to order from and can select any item from the zone inventory. The server looks up the cheapest prices for the chosen item and adds it to the customer's cart. Based on the customer cart and stores ordered from, the customer may be entitled to certain discounts (e.g., "buy 1 kg of Ketchup, get 2 pcks of Toilet Paper for additional 0"), which they can choose to add. 


Upon confirming an order, the customer can leave feedback for the stores involved in the purchase. An owner is sent a notification whenever a new order or feedback is placed at one of their stores.


The project currently includes crude chat and deposit features; the chat lets users send messages, and all messages ever written are displayed in the browser. The deposit lets customers add to their balance (even though the customer balance currently does not affect whether they can make a purchase or not). 

<h2>Screenshots</h2>

![Page2](/Page2.PNG?raw=true "Zones Overview")

![ChooseItems](/ChooseItems.PNG?raw=true "Choose Items")

![Cart](/CartSummary.PNG?raw=true "Current Cart")
