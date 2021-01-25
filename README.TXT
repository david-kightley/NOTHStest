There is a main method in the Checkout class that can be used to run the application.

The inventory is loaded up from a comma-separated file.  By default, it will load up
inventory.txt from the resources file.  The promotions are listed in a json file.

Scanning each item adds a product to a list - at the moment there is no mechanism to un-scan,
but it could be added and will remove an item from the list.
When a total is calculated, each of the items is converted to a lightweight object so that
prices can be changed without modifying the inventory item.
Each promotion is processed in the order that they appear in the config file.  If two bulk order
promotions are specified for the same item, as long as the higher amount with lower prices is specified second it
will be applied over the top of the first. eg
* Buy 2 or more travel card holders then the price drops to £8.50.
* Buy 5 or more travel card holders then the price drops to £7.75.
the first one is applied first and adjust the prices to £8.50, then if 5 or more are in the basket
the second will overwrite all of the prices with the lower one.

The discounts can also be serially applied on the total in the basket. eg.
* Spend over £60, then you get 10% off your purchase
* spend over £100, then you get an extra 5% off your purchase
This will take the 10% off and then add and extra 5%.  The discounts are kept separate from the
prices and so can be added.  All discounts are applied.
NOTE:  If you spend £60 and the bulk discount brings your total spend below the basket total threshold,
the latter will not get applied.

Also, there is an extra discount that can be used that takes a fixed amounts off the total price (eg. £5 off
when you spend over £50).  The type is specified in the json file.

Other promotions of different types can be entered in, but would probably require a bit more programming.
Rules extend a common interface and can be executed in the current framework.  However, the current solution
is a bit rigid around loading the blocks of promotions and the order each of the types is processed.

