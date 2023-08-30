package com.marian.puskas.bigfilefinder.data.directories

import android.content.SharedPreferences
import com.marian.puskas.bigfilefinder.domain.searchresults.directories.DirectoriesRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class DirectoriesRepositoryImpl @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : DirectoriesRepository {

    override fun getAllSelectedDirectories(): List<String> {
        return sharedPreferences.getStringSet(SAVED_DIRECTORIES_PREF_KEY, emptySet())?.toList() ?: emptyList()
    }

    override fun observeAllSelectedDirectories(): Flow<List<String>> {
        return observeValueChanges().map {
           getAllSelectedDirectories()
        }
    }

    override fun saveDirectory(path: String) {
        val directories = getAllSelectedDirectories().toMutableSet()
        directories.add(path)
        sharedPreferences.edit().putStringSet(SAVED_DIRECTORIES_PREF_KEY, directories).apply()
    }

    override fun removeDirectory(path: String) {
        val directories = getAllSelectedDirectories().toMutableSet()
        directories.remove(path)
        sharedPreferences.edit().putStringSet(SAVED_DIRECTORIES_PREF_KEY, directories).apply()
    }

    private fun observeValueChanges(): Flow<Unit> = callbackFlow {
        val onSharedPreferenceChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { _, changedKey ->
            if (changedKey == null || changedKey == SAVED_DIRECTORIES_PREF_KEY) {
                launch {
                    send(Unit)
                }
            }
        }
        sharedPreferences.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener)
        awaitClose {
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener)
        }
    }

    companion object {
        private const val SAVED_DIRECTORIES_PREF_KEY = "SAVED_DIRECTORIES_PREF_KEY"
    }

}