package com.example.tuner

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.datastore.preferences.core.emptyPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.example.tuner.controller.midi.MidiController
import com.example.tuner.controller.tuner.Tuner
import com.example.tuner.model.preferences.TunerPreferences
import com.example.tuner.model.preferences.tunerPreferenceDataStore
import com.example.tuner.model.tuning.TuningList
import com.example.tuner.music.Tuning
import com.example.tuner.screens.MainLayout
import com.example.tuner.screens.TunerPermissionScreen
import com.example.tuner.ui.theme.TunerTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.billthefarmer.mididriver.GeneralMidiConstants
import java.io.IOException

class MainActivity : AppCompatActivity() {

    /** View model used to hold the current tuner state. */
    private val vm: TunerActivityViewModel by viewModels()

    /** Handler used to check and request microphone permission. */
    private lateinit var ph: PermissionHandler

    /** MIDI controller used to play guitar notes. */
    private lateinit var midi: MidiController

    /** User preferences for the tuner. */
//    private lateinit var prefs: Flow<TunerPreferences>

    /** Callback used to dismiss tuning selection screen when the back button is pressed. */
    private lateinit var dismissTuningSelectorOnBack: OnBackPressedCallback

    /** Callback used to dismiss configure tuning panel when the back button is pressed. */
    private lateinit var dismissConfigurePanelOnBack: OnBackPressedCallback

    /**
     * Called when activity is created.
     */
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setup preferences
//        prefs = tunerPreferenceDataStore.data
//            .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
//            .map(TunerPreferences::fromAndroidPreferences)

        // Setup permission handler.
        ph = PermissionHandler(this, Manifest.permission.RECORD_AUDIO)

        // Setup MIDI controller for note playback.
        midi = MidiController(vm.tuner.tuning.value.numStrings())

        // Load tunings
        lifecycleScope.launch {
            vm.tuningList.loadTunings(this@MainActivity)
        }

//        // Setup custom back navigation.
//        dismissConfigurePanelOnBack = onBackPressedDispatcher.addCallback(this,
//            enabled = vm.configurePanelOpen.value,
//        ) {
//            dismissConfigurePanel()
//        }

//        dismissTuningSelectorOnBack = onBackPressedDispatcher.addCallback(this,
//            enabled = vm.tuningSelectorOpen.value
//        ) {
//            dismissTuningSelector()
//        }

        // Set UI content.
        setContent {
        //    val prefs by prefs.collectAsStateWithLifecycle(initialValue = TunerPreferences())

            TunerTheme() {
                val granted by ph.granted.collectAsStateWithLifecycle()
                if (granted) {
                    // Collect state.
                    val tuning by vm.tuner.tuning.collectAsStateWithLifecycle()
                    val noteOffset = vm.tuner.noteOffset.collectAsStateWithLifecycle()
                    val selectedString by vm.tuner.selectedString.collectAsStateWithLifecycle()
                    val autoDetect by vm.tuner.autoDetect.collectAsStateWithLifecycle()
                    val tuned by vm.tuner.tuned.collectAsStateWithLifecycle()
                    val tuningSelectorOpen by vm.tuningSelectorOpen.collectAsStateWithLifecycle()
                    val configurePanelOpen by vm.configurePanelOpen.collectAsStateWithLifecycle()
                    val favTunings = vm.tuningList.favourites.collectAsStateWithLifecycle()
                    val customTunings = vm.tuningList.custom.collectAsStateWithLifecycle()

                    // Calculate window size/orientation
                    val windowSizeClass = calculateWindowSizeClass(this)

                    val compact = remember(windowSizeClass) {
                        windowSizeClass.heightSizeClass == WindowHeightSizeClass.Compact &&
                                windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact
                    }

                    val expanded = remember(windowSizeClass) {
                        windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded &&
                                windowSizeClass.heightSizeClass > WindowHeightSizeClass.Compact
                    }

                    // Dismiss configure panel if no longer compact.
                    LaunchedEffect(compact, configurePanelOpen) {
                        if (configurePanelOpen && !compact) dismissConfigurePanel()
                    }

                    // Dismiss tuning selector when switching to expanded view.
                    LaunchedEffect(expanded, tuningSelectorOpen) {
                        if (tuningSelectorOpen && expanded) dismissTuningSelector()
                    }

                    // Display UI content.
                    MainLayout(
                        windowSizeClass = windowSizeClass,
                        compact = compact,
                        expanded = expanded,
                        tuning = tuning,
                        noteOffset = noteOffset,
                        selectedString = selectedString,
                        tuned = tuned,
                        autoDetect = autoDetect,
                        favTunings = favTunings,
                        customTunings = customTunings,
                      //  prefs = prefs,
                        tuningList = vm.tuningList,
                        tuningSelectorOpen = tuningSelectorOpen,
                        configurePanelOpen = configurePanelOpen,
//                        onSelectString = remember(prefs.enableStringSelectSound) {
//                            {
//                                vm.tuner.selectString(it)
//                                // Play sound on string selection.
//                                if (prefs.enableStringSelectSound) playStringSelectSound(it)
//                            }
//                        } ,
                        onSelectTuning = ::setTuning,
                        onTuneUpString = vm.tuner::tuneStringUp,
                        onTuneDownString = vm.tuner::tuneStringDown,
                        onTuneUpTuning = vm.tuner::tuneUp,
                        onTuneDownTuning = vm.tuner::tuneDown,
                        onAutoChanged = vm.tuner::setAutoDetect,
//                        onTuned = remember(prefs.enableInTuneSound) {
//                            {
//                                vm.tuner.setTuned()
//                                // Play sound when string tuned.
//                                if (prefs.enableInTuneSound) playInTuneSound()
//                            }
//                        },
                        onOpenTuningSelector = ::openTuningSelector,
//                        onSettingsPressed = ::openSettings,
                        onConfigurePressed = ::openConfigurePanel,
                        onSelectTuningFromList = ::selectTuning,
                        onDismissTuningSelector = ::dismissTuningSelector,
                        onDismissConfigurePanel = ::dismissConfigurePanel
                    )
                } else {
                    // Audio permission not granted, show permission rationale.
                    val firstRequest by ph.firstRequest.collectAsStateWithLifecycle()
                    TunerPermissionScreen(
                        canRequest = firstRequest,
                    //    onSettingsPressed = ::openSettings,
                        onRequestPermission = ph::request,
                        onOpenPermissionSettings = ::openPermissionSettings,
                    )
                }
            }
        }

        // Keep the screen on while tuning.
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    /** Called when the activity resumes after being paused. */
    override fun onResume() {
        // Call superclass.
        super.onResume()

        // Start midi driver.
        midi.start()

        // Start the tuner if no panels are open.
        if (!vm.tuningSelectorOpen.value && !vm.configurePanelOpen.value) {
            try {
                vm.tuner.start(ph)
            } catch (_: IllegalStateException) {
            }
        }
    }

    /** Called when the activity is paused but still visible. */
    override fun onPause() {
        // Stop the tuner.
        vm.tuner.stop()

        // Stop midi driver.
        midi.stop()

        // Save tunings.
        vm.tuningList.saveTunings(this)

        // Call superclass.
        super.onPause()
    }

    /** Plays the string selection sound for the specified [string]. */
    private fun playStringSelectSound(string: Int) {
        midi.playNote(
            string,
            MidiController.noteIndexToMidi(vm.tuner.tuning.value.getString(string).rootNoteIndex),
            150,
            vm.tuner.tuning.value.instrument.midiInstrument
        )
    }

    /** Plays the in tune sound for the selected string. */
    private fun playInTuneSound() {
        val string = vm.tuner.selectedString.value
        midi.playNote(
            string,
            MidiController.noteIndexToMidi(vm.tuner.tuning.value.getString(string).rootNoteIndex) + 12,
            50,
            GeneralMidiConstants.MARIMBA
        )
    }

    /**
     * Opens the configure tuning panel, and stops the tuner.
     */
    private fun openConfigurePanel() {
        dismissConfigurePanelOnBack.isEnabled = true
        vm.openConfigurePanel()
        vm.tuner.stop()
    }

    /**
     * Opens the tuning selection screen, and stops the tuner.
     */
    private fun openTuningSelector() {
        dismissTuningSelectorOnBack.isEnabled = true
        vm.openTuningSelector()
        vm.tuner.stop()
    }

    /**
     * Dismisses the tuning selection screen and restarts the tuner if no other panel is open.
     */
    private fun dismissTuningSelector() {
        dismissTuningSelectorOnBack.isEnabled = false
        vm.dismissTuningSelector()
        if (!vm.configurePanelOpen.value) {
            try {
                vm.tuner.start(ph)
            } catch(_: IllegalStateException) {}
        }
    }

    /** Dismisses the configure panel and restarts the tuner if no other panel is open. */
    private fun dismissConfigurePanel() {
        dismissConfigurePanelOnBack.isEnabled = false
        vm.dismissConfigurePanel()
        if (!vm.tuningSelectorOpen.value) {
            try {
                vm.tuner.start(ph)
            } catch (_: IllegalStateException) {}
        }
    }

    /**
     * Sets the current tuning to the [tuning] selected on the tuning
     * selection screen, restarts the tuner if no other panel is open,
     * and recreates the MIDI driver if necessary.
     */
    private fun selectTuning(tuning: Tuning) {
        // Consume back stack entry.
        dismissTuningSelectorOnBack.isEnabled = false

        // Recreate MIDI driver if number of strings different.
        checkAndRecreateMidiDriver(tuning)

        // Select the tuning.
        vm.selectTuning(tuning)

        // Start tuner if no other panel is open.
        if (!vm.configurePanelOpen.value) {
            try {
                vm.tuner.start(ph)
            } catch(_: IllegalStateException) {}
        }
    }

    /**
     * Sets the current tuning to the [tuning] specified,
     * and recreates the MIDI driver if necessary.
     */
    private fun setTuning(tuning: Tuning) {
        // Recreate MIDI driver if number of strings different.
        checkAndRecreateMidiDriver(tuning)

        // Select the tuning.
        vm.tuner.setTuning(tuning)
    }

    /**
     * Recreates the MIDI driver when the number of strings
     * in the new tuning is different from the current tuning.
     *
     * @param newTuning The new selected tuning.
     */
    private fun checkAndRecreateMidiDriver(newTuning: Tuning) {
        if (newTuning.numStrings() != vm.tuner.tuning.value.numStrings()) {
            midi.stop()
            midi = MidiController(newTuning.numStrings())
            midi.start()
        }
    }

    /** Opens the tuner settings activity. */
//    private fun openSettings() {
//        startActivity(Intent(this, SettingsActivity::class.java))
//    }

    /** Opens the permission settings screen in the device settings. */
    private fun openPermissionSettings() {
        startActivity(
            Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", packageName, null)
            )
        )
    }
}

/** View model used to hold the current tuner and UI state. */
class TunerActivityViewModel : ViewModel() {
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