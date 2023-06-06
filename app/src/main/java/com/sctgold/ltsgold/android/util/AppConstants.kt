package com.sctgold.ltsgold.android.util

interface AppConstants {
    companion object {
        val PLAY_SERVICES_RESOLUTION_REQUEST = 9000

        val CONNECTION_maxRetry = 3
        val CONNECTION_TEST_URL = "http://www.google.com"
        val CONNECTION_TEST_AGENT = "Test"
        val CONNECTION_TEST_TYPE = "close"
        val CONNECTION_TEST_TIMEOUT = 1500 //timemillis


        val KEY_HOST_PORT = "%API_HOST_PORT%"

        const val DELIMITOR_JSON_TEXT_WS = "|"
        val DELIMITOR_PIPE = "|"
        val DELIMITOR_DOT = "."
        val CONFIRM_SEC = 5

        val FORMAT_ORDER_DATE = "dd-MM-yyyy HH:mm:ss"
        val FORMAT_HISTORYLIST_DATE = "dd/MM/yyyy | HH:mm"
        val FORMAT_PRICE_DOUBLE = "%1$,.2f"
        val FORMAT_PRICE_SIGN_DOUBLE = "+#,##0.00;-#"

        val FORMAT_PASSWORD_POLICY = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{6,}$"
        val FORMAT_PIN_POLICY = "^[0-9]{4}$"
        val FORMAT_EMAIL =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
        val FORMAT_MOBILE = "^[0-9]{1,}"
        val FORMAT_VERSION_NAME = "%1\$d.%2\$d.%3\$d"

        val FRAGMENT_KEY = "fragmentKey"

        val PRODUCT_STRING_PREFIX = "lbl_product_"
        val PRODUCT_UNIT_PREFIX = "lbl_product_unit_"
        val PRODUCT_LBL_PREFIX = "lbl_"
        val PRODUCT_TRADE_TYPE_PREFIX = "lbl_trade_type_"
        val PAYMANT_PREFIX = "lbl_payment_"
        val MATCH_STATUS_PREFIX = "lbl_match_status_"

        val CONNECTION_TIMEOUT = 30
        val EXTRA_FEED_URI = "FeedURI"

        val EXTRA_GRADE = "Grade"
        val EXTRA_ACCOUNT_ID = "AccountId"
        val EXTRA_SYSTEM_URI = "SystemURI"

        val EXTRA_EVENT_URI = "EventURI"
        val EXTRA_SELECTED_NOTIFICATION = "Notification"

        val UPLOAD_IMG_WIDTH = 800
        val UPLOAD_IMG_HEIGHT = 600
        val UPLOAD_FILE_TYPE = "image/jpeg"

        val PRICE_FLASH_DELAY = 500
        val ALPHA_DISABLE = 0.5f
        val ALPHA_ENABLE = 1f
        val AUTO_INCREMENT_WAIT = 100

        val SCT_API_LANG = "th"


        val SOCKET_RETRY_DELAY = 400
        val SOCKET_RETRY_DELAY_AFTER_LOGIN = 3000
    }
}