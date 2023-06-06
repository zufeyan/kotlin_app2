package com.sctgold.ltsgold.android.activities

import android.content.Context
import android.content.Intent


class IntentActivityUtils {

    fun launchActivity(activity: Context, goldSpotIntent: GoldSpotIntent) {
        launchActivity(activity, goldSpotIntent, false)
    }

    fun launchActivity(activity: Context, goldSpotIntent: GoldSpotIntent, clearBackStack: Boolean) {
        val intent = Intent()
       // LogUtil.debug("LAUNCH-INTENT", activity.packageName)
        intent.action = activity.packageName + goldSpotIntent.actName
        intent.addCategory(goldSpotIntent.categoryName)
        //if(clearBackStack) {
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        //}
        activity.startActivity(intent)
    }



    enum class GoldSpotIntent(var actName: String, var categoryName: String) {
        LOGIN(".LOGIN", Intent.CATEGORY_DEFAULT),
        HOME(
            ".HOME",
            Intent.CATEGORY_DEFAULT
        ),
        CONFIRM_ORDER(".CONFIRM_ORDER", Intent.CATEGORY_DEFAULT), SPLASH(
            Intent.ACTION_MAIN,
            Intent.CATEGORY_LAUNCHER
        );

    }

    enum class GoldSpotFragment {
        LOGIN_FRAGMENT, CHANGE_PASSWORD_AND_PIN, CHANGE_PASSWORD, CHECK_PIN, NEW_ACCOUNT, PREFERENCE, ORDER_FRAGMENT, CHANGE_PIN, HOME_PRODUCT_LIST_FRAGMENT, HOME_PRODUCT_SETTING_FRAGMENT, PORTFOLIO, PORTFOLIO_DETAIL, HISTORY_LIST_FRAGMENT, HISTORY_DETAIL_FRAGMENT, SYSTEM_CLOSE_FRAGMENT, SYSTEM_UPDATE_FRAGMENT, NEWS_LIST, NEWS_DETAIL, PASSWORD_EXPIRE, MATCHING_ORDER
    }



}