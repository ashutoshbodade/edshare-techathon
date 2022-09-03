package com.ashutosh.techathon.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.ashutosh.techathon.model.UserDataModel
import com.google.gson.Gson


class SessionManager(private val context:Context) {

    private val PREFS_FILENAME = "com.ashutosh.techathon"

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME, 0)

    private val editor = sharedPreferences.edit()

    private fun storeString(key: String, value: String) {
        editor.run {
            putString(key, value)
            apply()
        }
    }

    private fun storeLong(key: String, value: Long) {
        editor.run {
            putLong(key, value)
            apply()
        }
    }

    private fun storeBoolean(key: String, value: Boolean) =
        editor.run {
            putBoolean(key, value)
            apply()
        }

    private fun getString(key: String) =
        sharedPreferences.getString(key, "")

    fun clearAll() =
        editor?.run {
            clear()
            apply()
        }


    private val KEY_USER="user"


    fun saveUser(userData: UserDataModel){
        storeString(KEY_USER, Gson().toJson(userData))
    }

    fun getUser(): UserDataModel?  {
        val str=getString(KEY_USER)
        if (str.isNullOrBlank())
            return null
        return Gson().fromJson(str, UserDataModel::class.java)
    }



}