package com.harishtk.goldrate.app.ui.screens.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.harishtk.goldrate.app.UiState
import com.harishtk.goldrate.app.data.Resource
import com.harishtk.goldrate.app.data.Resource.Status.*
import com.harishtk.goldrate.app.data.entities.GoldrateEntry
import com.harishtk.goldrate.app.data.repository.GoldrateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: GoldrateRepository
) : ViewModel() {

    val pagedGoldRateEntries: Flow<PagingData<GoldrateEntry>>

    init {
        pagedGoldRateEntries = getEntries()
            .cachedIn(viewModelScope)
    }

    private fun getEntries() = repository.getGoldrateEntries()
        /*.map { pagingData ->
            pagingData.map { it }
                .insertSeparators { before: GoldrateEntry?, after: GoldrateEntry? ->
                    if (after == null) {
                        // We're at the end of the list
                        return@insertSeparators null
                    }

                    if (before == null) {
                        // We're at the start of the list
                        return@insertSeparators
                    }
                }
        }*/


    /*private val _goldrateEntries = repository.getGoldrateEntries()
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
    }*/

}