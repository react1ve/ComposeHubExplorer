package com.reactive.composerepos.ui.screens.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.kittinunf.result.onFailure
import com.github.kittinunf.result.onSuccess
import com.reactive.composerepos.ui.extensions.getScreenStatusDependingOnError
import com.reactive.composerepos.ui.screens.ScreenStatus
import com.reactive.domain.usecases.GetRepoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    savedStateHandle : SavedStateHandle,
    private val getRepoUseCase : GetRepoUseCase,
): ViewModel() {
    private val owner : String = savedStateHandle.get("owner")!!
    private val name : String = savedStateHandle.get("name")!!

    private val _state = MutableStateFlow(DetailsScreenState.Empty)
    val state = _state as StateFlow<DetailsScreenState>

    init {
        viewModelScope.launch {
            _state.value = DetailsScreenState(screenStatus = ScreenStatus.SUCCESS)
            getRepoUseCase(owner, name)
                .onSuccess { repo ->
                    _state.value = DetailsScreenState(
                        screenStatus = ScreenStatus.SUCCESS,
                        repo = repo
                    )
                }
                .onFailure { failure ->
                    _state.value =
                        DetailsScreenState(
                            repo = null,
                            screenStatus = failure.getScreenStatusDependingOnError()
                        )
                }
        }
    }
}
