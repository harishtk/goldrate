package com.harishtk.goldrate.app.ui.screens.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.harishtk.goldrate.app.data.Resource
import com.harishtk.goldrate.app.data.entities.GoldrateEntry
import com.harishtk.goldrate.app.data.respository.GoldrateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: GoldrateRepository
) : ViewModel() {

    private val _goldrateEntries = repository.getGoldrateEntries()
    val goldrateEntries: LiveData<Resource<List<GoldrateEntry>>> = _goldrateEntries
}