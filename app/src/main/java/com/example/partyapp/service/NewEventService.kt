package com.example.partyapp.service

import android.content.Context
import android.content.SharedPreferences

class NewEventService(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        SharedPrefConstants.NAME,
        Context.MODE_PRIVATE
    )
    var eventName: String? = null
    var eventDescription: String? = null
    var eventImage: Int? = null
    private set

    init {
        loadData()
    }

    fun saveEventName(newName: String) {
        eventName = newName
        sharedPreferences
            .edit()
            .putString(SharedPrefConstants.NAME_OF_EVENT, newName)
            .apply()
    }

    fun saveEventDescr(newDescr: String) {
        eventDescription = newDescr
        sharedPreferences
            .edit()
            .putString(SharedPrefConstants.EVENT_DESCR, newDescr)
            .apply()
    }

    fun saveEventImage(newImage: Int) {
        eventImage = newImage
        sharedPreferences
            .edit()
            .putInt(SharedPrefConstants.EVENT_IMAGE, newImage)
            .apply()
    }

    private fun loadData() {
        eventName = if (sharedPreferences.contains(SharedPrefConstants.NAME_OF_EVENT))
            sharedPreferences.getString(SharedPrefConstants.NAME_OF_EVENT, null)
        else
            null
        eventDescription = if (sharedPreferences.contains(SharedPrefConstants.EVENT_DESCR))
            sharedPreferences.getString(SharedPrefConstants.EVENT_DESCR, null)
        else
            null
        eventImage = if (sharedPreferences.contains(SharedPrefConstants.EVENT_IMAGE))
            sharedPreferences.getInt(SharedPrefConstants.EVENT_IMAGE, 0)
        else
            null
    }
}