package com.example.partyapp.utils

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

fun setPreferenceObject(c: Context, modal: Any?, key:String?, pref: SharedPreferences) {
    val prefsEditor: SharedPreferences.Editor = pref.edit()
    val gson = Gson()
    val jsonObject = gson.toJson(modal)
    prefsEditor.putString(key, jsonObject)
    prefsEditor.apply()
}

fun getPreferenceObjectJson(c: Context, key: String?, pref: SharedPreferences) : Bitmap {
    var json = pref.getString(key, "")
    if (json == null)
        json = ""
    val gson = Gson()
    val turnsType = object : TypeToken<Bitmap>() {}.type
    return gson.fromJson(json, turnsType)
}