package tech.azurestar.kmp.ecommercecustomer.db

object DatabaseConstants {
    // Table Names
    const val TABLE_ADDRESS = "address"
    const val TABLE_CARTS = "carts"
    const val TABLE_CART_ITEMS = "cart_items"
    const val TABLE_CATEGORIES = "categories"
    const val TABLE_CUSTOMERS = "customers"
    const val TABLE_ITEMS = "items"
    const val TABLE_ORDERS = "orders"
    const val TABLE_ORDER_ITEMS = "order_items"
    const val TABLE_PAYMENT_NOTIFICATION = "payment-notification"
    const val TABLE_SELLERS = "sellers"

    // Address Columns
    const val COL_ADDRESS_ID = "id"
    const val COL_ADDRESS_LINE1 = "line1"
    const val COL_ADDRESS_LINE2 = "line2"
    const val COL_ADDRESS_CITY = "city"
    const val COL_ADDRESS_PINCODE = "pincode"
    const val COL_ADDRESS_USER_ID = "user_id"
    
    // Carts Columns
    const val COL_CARTS_CREATED_AT = "created_at"
    const val COL_CARTS_USER_ID = "user_id"
    const val COL_CARTS_ITEM_IDS = "cart_item_ids"

    // Carts and Items Columns
    const val COL_CART_ITEMS_ID = "id"
    const val CART_ITEMS_CREATED_AT = "created_at"
    const val COL_CART_ITEMS_QUANTITY = "quantity"
    const val COL_CART_ITEMS_OPTIONS = "options"


    // Categories Columns
    const val COL_CATEGORIES_ID = "id"
    const val COL_CATEGORIES_NAME = "name"

    // Customers Columns
    const val COL_CUSTOMERS_ID = "id"
    const val COL_CUSTOMERS_CREATED_AT = "created_at"
    const val COL_CUSTOMERS_NAME = "name"
    const val COL_CUSTOMERS_USER_ID = "user_id"
    const val COL_CUSTOMERS_PROFILE_IMAGE = "profile_image"

    // Items Columns
    const val COL_ITEMS_ID = "id"
    const val COL_ITEMS_CREATED_AT = "created_at"
    const val COL_ITEMS_NAME = "name"
    const val COL_ITEMS_DESCRIPTION = "description"
    const val COL_ITEMS_USER_ID = "user_id"
    const val COL_ITEMS_IMAGES = "images"
    const val COL_ITEMS_CATEGORY_ID = "category_id"
    const val COL_ITEMS_PRICE = "price"

    // Order Columns
    const val COL_ORDERS_ID = "id"
    const val COL_ORDERS_CREATED_AT = "created_at"
    const val COL_ORDERS_PRICE = "price"
    const val COL_ORDERS_PERCENTAGE = "percentage"
    const val COL_ORDERS_PAID_TO_SELLER = "paid_to_seller"
    const val COL_ORDERS_USER_ID = "user_id"
    const val COL_ORDER_ITEMS_IDS = "order_items_ids"
    const val COL_ORDERS_ADDRESS_ID = "address_id"

    // Order Items Columns
    const val COL_ORDER_ITEMS_ID = "id"
    const val COL_ORDER_ITEMS_USER_ID = "user_id"
    const val COL_ORDER_ITEMS_CREATED_AT = "created_at"
    const val COL_ORDER_ITEMS_ORDER_ID = "order_id"
    const val COL_ORDER_ITEMS_ITEM_ID = "item_id"

    // Sellers Columns
    const val COL_SELLERS_ID = "id"
    const val COL_SELLERS_CREATED_AT = "created_at"
    const val COL_SELLERS_NAME = "name"
    const val COL_SELLERS_USER_ID = "user_id"
    const val COL_SELLERS_PROFILE_IMAGE = "profile_image"
}