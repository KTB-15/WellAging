package com.example.wellaging

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

class MyApplication : Application() {
    val dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
}