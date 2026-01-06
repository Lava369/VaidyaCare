package com.example.vaidyacare.utils

import android.content.Context
import android.content.SharedPreferences

object DoctorSession {

    private const val PREF_NAME = "doctor_session"

    private const val KEY_ID = "doctor_id"
    private const val KEY_NAME = "doctor_name"
    private const val KEY_EMAIL = "doctor_email"
    private const val KEY_MOBILE = "doctor_mobile"
    private const val KEY_LICENSE = "doctor_license"
    private const val KEY_LOGGED_IN = "doctor_logged_in"

    private fun prefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    /* ---------------- SAVE SESSION (CALL AFTER LOGIN) ---------------- */

    fun saveDoctor(
        context: Context,
        doctorId: Int,
        name: String,
        email: String,
        mobile: String,
        license: String
    ) {
        prefs(context).edit().apply {
            putInt(KEY_ID, doctorId)
            putString(KEY_NAME, name)
            putString(KEY_EMAIL, email)
            putString(KEY_MOBILE, mobile)
            putString(KEY_LICENSE, license)
            putBoolean(KEY_LOGGED_IN, true)
            apply()
        }
    }

    /* ---------------- LOGIN STATE ---------------- */

    fun isLoggedIn(context: Context): Boolean {
        return prefs(context).getBoolean(KEY_LOGGED_IN, false)
    }

    /* ---------------- GETTERS ---------------- */

    fun getDoctorId(context: Context): Int {
        return prefs(context).getInt(KEY_ID, 0)
    }

    fun getName(context: Context): String {
        return prefs(context).getString(KEY_NAME, "") ?: ""
    }

    fun getEmail(context: Context): String {
        return prefs(context).getString(KEY_EMAIL, "") ?: ""
    }

    fun getMobile(context: Context): String {
        return prefs(context).getString(KEY_MOBILE, "") ?: ""
    }

    fun getLicense(context: Context): String {
        return prefs(context).getString(KEY_LICENSE, "") ?: ""
    }

    /* ---------------- CLEAR SESSION (LOGOUT) ---------------- */

    fun clear(context: Context) {
        prefs(context).edit().clear().apply()
    }
}
