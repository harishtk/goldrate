package com.harishtk.goldrate.app.ui.screens.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.harishtk.goldrate.app.data.Resource
import com.harishtk.goldrate.app.data.Resource.Status.*
import com.harishtk.goldrate.app.data.entities.GoldrateEntry
import com.harishtk.goldrate.app.data.repository.GoldrateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    repository: GoldrateRepository
) : ViewModel() {

    private val _goldrateEntries = repository.getGoldrateEntries()
    val goldrateEntries: LiveData<Resource<Map<String, List<GoldrateEntry>>>> = _goldrateEntries.map { result ->
        return@map when (result.status) {
            SUCCESS     -> {
                if (result.data?.isNotEmpty() == true) {
                    Resource.success(result.data.groupBy { it.type })
                } else {
                    Resource.error("No data!")
                }
            }
            ERROR       -> Resource.error(result.message ?: "UNKNOWN ERR")
            LOADING     -> Resource.loading()
        }
    }

}