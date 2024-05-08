package com.example.tuner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tuner.model.tuning.TuningList
import com.example.tuner.controller.tuner.Tuner
import com.example.tuner.music.Tuning
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    /** Tuner used for audio processing and note comparison. */
    val tuner = Tuner()

    /** State holder containing the lists of favourite and custom tunings. */
    val tuningList = TuningList(tuner.tuning.value, viewModelScope)

    /** Mutable backing property for [tuningSelectorOpen]. */
    private val _tuningSelectorOpen = MutableStateFlow(false)

    /** Whether the tuning selection screen is currently open. */
    val tuningSelectorOpen = _tuningSelectorOpen.asStateFlow()

    /** Mutable backing property for [configurePanelOpen]. */
    private val _configurePanelOpen = MutableStateFlow(false)

    /**
     * Whether the configure tuning panel is currently open.
     */
    val configurePanelOpen = _configurePanelOpen.asStateFlow()

    /** Runs when the view model is instantiated. */
    init {
        // Update tuner when the current selection in the tuning list is updated.
        viewModelScope.launch {
            tuner.tuning.collect {
                tuningList.setCurrent(it)
            }
        }
        // Update the tuning list when the tuner's tuning is updated.
        viewModelScope.launch {
            tuningList.current.collect {
                it?.let { tuner.setTuning(it) }
            }
        }
    }

    /** Opens the tuning selection screen. */
    fun openTuningSelector() {
        _tuningSelectorOpen.update { true }
    }

    /**
     * Opens the configure tuning panel.
     */
    fun openConfigurePanel() {
        _configurePanelOpen.update { true }
    }

    /** Dismisses the tuning selection screen. */
    fun dismissTuningSelector() {
        _tuningSelectorOpen.update { false }
    }

    /**
     * Dismisses the configure tuning panel.
     */
    fun dismissConfigurePanel() {
        _configurePanelOpen.update { false }
    }

    /** Sets the current tuning to that selected in the tuning selection screen and dismisses it. */
    fun selectTuning(tuning: Tuning) {
        _tuningSelectorOpen.update { false }
        tuner.setTuning(tuning)
    }
}