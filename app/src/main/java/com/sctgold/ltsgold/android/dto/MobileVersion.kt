package com.sctgold.ltsgold.android.dto

class MobileVersion {
    private var major = 0
    private var minor = 0
    private var buildNumber = 0
    private var forceUpdate = false

    fun getMajor(): Int {
        return major
    }

    fun setMajor(major: Int) {
        this.major = major
    }

    fun getMinor(): Int {
        return minor
    }

    fun setMinor(minor: Int) {
        this.minor = minor
    }

    fun getBuildNumber(): Int {
        return buildNumber
    }

    fun setBuildNumber(buildNumber: Int) {
        this.buildNumber = buildNumber
    }

    fun isForceUpdate(): Boolean {
        return forceUpdate
    }

    fun setForceUpdate(forceUpdate: Boolean) {
        this.forceUpdate = forceUpdate
    }

    override fun toString(): String {
        return "MobileVersion{" +
                "major=" + major +
                ", minor=" + minor +
                ", buildNumber=" + buildNumber +
                ", forceUpdate=" + forceUpdate +
                '}'
    }
}