package com.example.vaidyacare.utils

import android.content.Context
import android.content.SharedPreferences

object UserSession {

    private const val PREF_NAME = "user_session"

    private const val KEY_ID = "user_id"
    private const val KEY_NAME = "user_name"
    private const val KEY_EMAIL = "user_email"
    private const val KEY_MOBILE = "user_mobile"
    private const val KEY_PATIENT_ID = "patient_id"
    private const val KEY_LOGGED_IN = "user_logged_in"

    private fun prefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    /* ---------------- SAVE SESSION (CALL AFTER LOGIN/SIGNUP) ---------------- */

    fun saveUser(
        context: Context,
        userId: Int,
        name: String,
        email: String,
        mobile: String,
        patientId: String
    ) {
        prefs(context).edit().apply {
            putInt(KEY_ID, userId)
            putString(KEY_NAME, name)
            putString(KEY_EMAIL, email)
            putString(KEY_MOBILE, mobile)
            putString(KEY_PATIENT_ID, patientId)
            putBoolean(KEY_LOGGED_IN, true)
            apply()
        }
    }

    /* ---------------- LOGIN STATE ---------------- */

    fun isLoggedIn(context: Context): Boolean {
        return prefs(context).getBoolean(KEY_LOGGED_IN, false)
    }

    /* ---------------- GETTERS ---------------- */

    fun getUserId(context: Context): Int {
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

    fun getPatientId(context: Context): String {
        return prefs(context).getString(KEY_PATIENT_ID, "") ?: ""
    }

    /* ---------------- CLEAR SESSION (LOGOUT) ---------------- */

    fun clear(context: Context) {
        prefs(context).edit().clear().apply()
    }
}

