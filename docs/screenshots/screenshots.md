# Example system screenshots

## Homepage

Products are listed here.
Products with attribute `hidden=true` are not listed due
to business rule post-condition.

![Homepage](homepage.png)

## Add product to shopping cart

![Homepage](add-product-to-cart.png)

## Adding product to shopping cart failed

Due to business rule precondition requiring the shopping cart has at most 10 items.

![Homepage](add-product-to-cart-fail.png)

## Shopping cart overview

![Homepage](shopping-cart.png)

## Shopping cart checkout

![Homepage](checkout.png)

## Order created

Order has been successfuly created after checking out.

![Homepage](order-created.png)

## List all orders

This action requires to be signed in as an Employee or an Administrator.

![Homepage](order-management.png)


## Listing all orders failed

Due to business rule precondition requiring the user to be an Employee or an Administrator.

![Homepage](order-management-fail.png)
