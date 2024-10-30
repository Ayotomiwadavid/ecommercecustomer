package tech.azurestar.kmp.ecommercecustomer.language

enum class TextProvider {
    NAME,
    DESCRIPTION,
    PRICE,
    SELECT_IMAGES,
    CREATE_ACCOUNT,
    UPDATE_ACCOUNT,
    LOGIN,
    SIGN_UP,
    FORGOT_PASSWORD,
    EMAIL,
    PASSWORD,
    ADD_ITEM,
    EDIT_ITEM,
    DELETE_ITEM,
    MY_ITEMS,
    PLEASE_CHECK_YOUR_EMAIL,
    AN_ERROR_OCCURRED,
    PLEASE_FILL_OUT_ALL_FIELDS,
    PLEASE_ADD_ATLEAST_ONE_IMAGE,
    LOGIN_INSTEAD,
    SIGN_UP_INSTEAD,
    SEARCH,
    ADD_TO_CART,
    REMOVE_FROM_CART,
    ACCOUNT_UPDATED,
    CART_SUMMARY,
    SELECTED_ITEMS,
    TOTAL_PRICE,
    PLACE_ORDER,
    HOME,
    PROFILE,
    ORDERS,
    CART,
    ;


    fun getText(english: Boolean = true): String =
        if (english) getEnglishText() else getMongolianText()

    fun getEnglishText(): String =
        when (this) {
            NAME -> "Name"
            DESCRIPTION -> "Description"
            PRICE -> "Price"
            SELECT_IMAGES -> "Select Images"
            CREATE_ACCOUNT -> "Create Account"
            LOGIN -> "Login"
            SIGN_UP -> "Sign Up"
            FORGOT_PASSWORD -> "Forgot Password"
            EMAIL -> "Email"
            PASSWORD -> "Password"
            ADD_ITEM -> "Add Item"
            EDIT_ITEM -> "Edit Item"
            DELETE_ITEM -> "Delete Item"
            MY_ITEMS -> "My Items"
            PLEASE_CHECK_YOUR_EMAIL -> "Please check your email for confirmation"
            AN_ERROR_OCCURRED -> "An error occurred"
            PLEASE_FILL_OUT_ALL_FIELDS -> "Please fill out all fields"
            PLEASE_ADD_ATLEAST_ONE_IMAGE -> "Please add at least one image"
            LOGIN_INSTEAD -> "Login instead"
            SIGN_UP_INSTEAD -> "Sign up instead"
            SEARCH -> "Search"
            ADD_TO_CART -> "Add to Cart"
            REMOVE_FROM_CART -> "Remove from Cart"
            UPDATE_ACCOUNT -> "Update Account"
            ACCOUNT_UPDATED -> "Account updated"
            CART_SUMMARY -> "Cart Summary"
            SELECTED_ITEMS -> "Selected Items"
            TOTAL_PRICE -> "Total Price"
            PLACE_ORDER -> "Place Order"
            HOME -> "Home"
            PROFILE -> "Profile"
            ORDERS -> "Orders"
            CART -> "Cart"
        }

    fun getMongolianText() = when(this) {
        NAME -> "НЭР"
        DESCRIPTION -> "ТАЙЛБАР"
        PRICE -> "ҮНЭ"
        SELECT_IMAGES -> "ЗУРАГ СОНГОХ"
        CREATE_ACCOUNT -> "ХАЯГ ҮҮСГЭХ"
        LOGIN -> "НЭВТРЭХ"
        SIGN_UP -> "БҮРТГҮҮЛЭХ"
        FORGOT_PASSWORD -> "НУУЦ ҮГ МАРТСАН"
        EMAIL -> "EMAIL"
        PASSWORD -> "НУУЦ ҮГ"
        ADD_ITEM -> "БАРАА НЭМЭХ"
        EDIT_ITEM -> "БАРАА ЗАСАХ"
        DELETE_ITEM -> "БАРАА УСТГАХ"
        MY_ITEMS -> "МИНИЙ БАРАА"
        PLEASE_CHECK_YOUR_EMAIL -> "Баталгаажуулалтаа шалгахын тулд email-ээ шалгана уу"
        AN_ERROR_OCCURRED -> "Алдаа гарлаа"
        PLEASE_FILL_OUT_ALL_FIELDS -> "Бүх талбаруудыг бөглөнө үү"
        PLEASE_ADD_ATLEAST_ONE_IMAGE -> "Дор хаяж нэг зураг kруулнауу"
        else -> ""
    }
}