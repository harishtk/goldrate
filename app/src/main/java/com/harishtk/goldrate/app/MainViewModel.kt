package com.harishtk.goldrate.app

import android.content.Context
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.map
import com.harishtk.goldrate.app.data.Resource
import com.harishtk.goldrate.app.data.entities.GoldrateEntry
import com.harishtk.goldrate.app.data.repository.GoldrateRepository
import dagger.assisted.AssistedFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.lang.IllegalStateException
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    @ApplicationContext private val applicationContext: Context,
    private val repository: GoldrateRepository
) : ViewModel() {

    val resources = applicationContext.resources

    private val _uiState = MutableStateFlow(UiState(loading = true))
    val uiState: StateFlow<UiState> = _uiState

    val msgFlow: MutableStateFlow<String>

    init {
        msgFlow = MutableStateFlow(DEFAULT_MESSAGE)

        viewModelScope.launch {
            repository.getLastGoldrateEntry()
                .collectLatest { entry ->
                    if (entry == null) {
                        _uiState.value = UiState(error = IllegalStateException("No entries"))
                    } else {
                        _uiState.value = UiState(lastGoldrateEntry = entry)
                    }
                }
        }
    }

    fun setMessage(msg: String) { msgFlow.value = msg }

    /*@AssistedFactory
    interface Factory {
        fun create(applicationContext: Context): MainViewModel
    }

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun provideFactory(
            assistedFactory: Factory,
            context: Context
        ) : ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(applicationContext = context) as T
            }
        }
    }*/
}

data class UiState(
    val error: Exception? = null,
    val loading: Boolean = false,
    val lastGoldrateEntry: GoldrateEntry? = null,
    val message: String = DEFAULT_MESSAGE
)

const val DEFAULT_MESSAGE = "Loading.."