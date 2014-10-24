
Basic Script that pushes request to app-demo endpoint.
 -http://localhost:8080/demo-orders/OrderService
 -uses SampleRequest.json. CUSTOMER_REPLACE, PRODUCT_REPLACE, ID_REPLACE been randomly set from a preset list. 
 - if the product “JAM” is requested then the InventoryService will artificially wait 7 seconds. The purpose is to show outliers in the kibana interface 
 - a ItemNotFoundException is throw if a product other than butter or jam is requested 


To execute, 
  - A single instance, sendMyRequestQueue.sh 
  - To send multiple requests sendMultipleParrell.sh
