package com.harishtk.goldrate.app

import androidx.lifecycle.*
import com.harishtk.goldrate.app.data.Resource
import com.harishtk.goldrate.app.data.entities.GoldrateEntry
import com.harishtk.goldrate.app.data.respository.GoldrateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: GoldrateRepository
) : ViewModel() {

    private val _goldrateEntries = repository.getGoldrateEntries()
    val goldrateEntries: LiveData<Resource<List<GoldrateEntry>>> = _goldrateEntries

    private val _lastEntry = _goldrateEntries.map { lastEntry(it) }
    val lastEntry: LiveData<Resource<GoldrateEntry>> = _lastEntry

    private fun lastEntry(res: Resource<List<GoldrateEntry>>): Resource<GoldrateEntry> = when (res.status) {
        Resource.Status.SUCCESS ->
            if (res.data?.isNotEmpty() == true) Resource.success(res.data.first())
            else Resource.error("No entries")
        Resource.Status.LOADING -> Resource.loading()
        else -> Resource.error("Failed to load entry")
    }
}