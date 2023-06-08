package com.sctgold.goldinvest.android.dto

import com.sctgold.goldinvest.android.activities.IntentActivityUtils.GoldSpotIntent
import com.sctgold.goldinvest.android.activities.IntentActivityUtils.GoldSpotFragment
import java.io.Serializable

class ActionResult : Serializable {
    private val serialVersionUID = 100L

    private var newIntent = java.lang.Boolean.FALSE
    private var reloadRequired = java.lang.Boolean.FALSE
    private var nextIntent: GoldSpotIntent? = null
    private var nextFragment: GoldSpotFragment? = null
    private var messageId = -1
    private var messageString: String? = null

    fun isNewIntent(): Boolean {
        return newIntent
    }

    fun setNewIntent(newIntent: Boolean) {
        this.newIntent = newIntent
    }

    fun isReloadRequired(): Boolean {
        return reloadRequired
    }

    fun setReloadRequired(reloadRequired: Boolean) {
        this.reloadRequired = reloadRequired
    }

    fun getNextIntent(): GoldSpotIntent? {
        return nextIntent
    }

    fun setNextIntent(nextIntent: GoldSpotIntent?) {
        this.nextIntent = nextIntent
    }

    fun getNextFragment(): GoldSpotFragment? {
        return nextFragment
    }

    fun setNextFragment(nextFragment: GoldSpotFragment?) {
        this.nextFragment = nextFragment
    }

    fun getMessageId(): Int {
        return messageId
    }

    fun setMessageId(messageId: Int) {
        this.messageId = messageId
    }

    fun getMessageString(): String? {
        return messageString
    }

    fun setMessageString(messageString: String?) {
        this.messageString = messageString
    }

}
