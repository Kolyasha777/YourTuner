package com.example.tuner.screens

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AppBarDefaults
import androidx.compose.material.BottomAppBar
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.tuner.R
import com.example.tuner.components.StringControls
import com.example.tuner.components.TuningSelector
import com.example.tuner.music.Tuning

/**
 * UI screen used to tune individual strings and the tuning
 * itself up and down, as well as select from favourite tunings.
 *
 * @param tuning Guitar tuning used for comparison.
 * @param favTunings Set of tunings marked as favourite by the user.
 * @param customTunings Set of custom tunings added by the user.
 * @param onSelectTuning Called when a tuning is selected.
 * @param onTuneUpString Called when a string is tuned up.
 * @param onTuneDownString Called when a string is tuned down.
 * @param onTuneUpTuning Called when the tuning is tuned up.
 * @param onTuneDownTuning Called when the tuning is tuned down.
 * @param onOpenTuningSelector Called when the user opens the tuning selector screen.
 * @param onDismiss Called when the screen is dismissed.
 *
 */
@Composable
fun ConfigureTuningScreen(
    tuning: Tuning,
    favTunings: State<Set<Tuning>>,
    customTunings: State<Set<Tuning>>,
    onSelectTuning: (Tuning) -> Unit,
    onTuneUpString: (Int) -> Unit,
    onTuneDownString: (Int) -> Unit,
    onTuneUpTuning: () -> Unit,
    onTuneDownTuning: () -> Unit,
    onOpenTuningSelector: () -> Unit,
    onDismiss: () -> Unit,
) {
    val scrollState = rememberScrollState()

    val appBarElevation by animateDpAsState(
        remember { derivedStateOf {
            if (scrollState.value == 0) {
                0.dp
            } else AppBarDefaults.TopAppBarElevation
        }}.value,
        label = "App Bar Elevation"
    )

    Scaffold (
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.configure_tuning))
                },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, stringResource(R.string.dismiss))
                    }
                },
                backgroundColor = MaterialTheme.colors.background,
                elevation = appBarElevation
            )
        },
        bottomBar = {
            Column(Modifier.fillMaxWidth()) {
                Divider(thickness = Dp.Hairline)
                BottomAppBar(
                    modifier = Modifier.height(IntrinsicSize.Min),
                    backgroundColor = MaterialTheme.colors.background,
                    contentColor = MaterialTheme.colors.onBackground,
                    contentPadding = PaddingValues(vertical = 8.dp),
                ) {
                    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
                        TuningSelector(
                            tuning = tuning,
                            favTunings = favTunings,
                            customTunings = customTunings,
                            openDirect = true,
                            onSelect = onSelectTuning,
                            onTuneDown = onTuneDownTuning,
                            onTuneUp = onTuneUpTuning,
                            onOpenTuningSelector = onOpenTuningSelector,
                        )
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(Modifier.height(8.dp))
            StringControls(
                inline = true,
                tuning = tuning,
                selectedString = null,
                tuned = null,
           //     onSelect = {},
                onTuneDown = onTuneDownString,
                onTuneUp = onTuneUpString
            )
            Spacer(Modifier.height(8.dp))
        }
    }
}