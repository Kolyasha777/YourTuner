package com.example.tuner.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.tuner.model.preferences.StringLayout
import com.example.tuner.model.preferences.TunerPreferences
import com.example.tuner.model.preferences.TuningDisplayType

/**
 * A UI screen that displays and allows selection of the user's tuner preferences.
 *
 * @param prefs The tuner preferences.
 * @param onSelectStringLayout Called when the user selects a string control layout.
 * @param onSelectDisplayType Called when the user selects a tuning display type.
 * @param onEnableStringSelectSound Called when the user toggles the string select sound.
 * @param onEnableInTuneSound Called when the user toggles the in-tune sound.
 * @param onSetUseBlackTheme Called when the user toggles the full black theme.
 * @param onBackPressed Called when the user presses the back navigation button.
 *
 */
//@OptIn(ExperimentalMaterialApi::class)
//@Composable
//fun SettingsScreen(
//    prefs: TunerPreferences,
//    onSelectStringLayout: (StringLayout) -> Unit,
//    onSelectDisplayType: (TuningDisplayType) -> Unit,
//    onEnableStringSelectSound: (Boolean) -> Unit,
//    onEnableInTuneSound: (Boolean) -> Unit,
//    onSetUseBlackTheme: (Boolean) -> Unit,
//    onAboutPressed: () -> Unit,
//    onBackPressed: () -> Unit,
//) {
//    Scaffold(
//        topBar = { TopAppBar(
//            // Back navigation button.
//            navigationIcon = {
//                IconButton(onClick = onBackPressed) {
//                    Icon(Icons.Default.ArrowBack, stringResource(R.string.nav_back))
//                }
//            },
//            backgroundColor = MaterialTheme.colors.primarySurfaceBackground(prefs.useBlackTheme),
//            title = { Text(stringResource(R.string.tuner_settings)) }
//        )}
//    ) { padding ->
//        Column(
//            modifier = Modifier
//                .verticalScroll(rememberScrollState())
//                .padding(top = padding.calculateTopPadding())
//        ) {
//            // String layout selection.
//            SectionLabel(title = stringResource(R.string.pref_string_layout))
//
//            // Inline
//            ListItem(
//                text = { Text(stringResource(R.string.pref_string_layout_inline)) },
//                secondaryText = { Text(stringResource(R.string.pref_string_layout_inline_desc)) },
//                trailing = {
//                    RadioButton(
//                        selected = prefs.stringLayout == StringLayout.INLINE,
//                        onClick = { onSelectStringLayout(StringLayout.INLINE) }
//                    )
//                },
//                modifier = Modifier.clickable { onSelectStringLayout(StringLayout.INLINE) }
//            )
//
//            // Side by Side
//            ListItem(
//                text = { Text(stringResource(R.string.pref_string_layout_side_by_side)) },
//                secondaryText = { Text(stringResource(R.string.pref_string_layout_side_by_side_desc)) },
//                trailing = {
//                    RadioButton(
//                        selected = prefs.stringLayout == StringLayout.SIDE_BY_SIDE,
//                        onClick = { onSelectStringLayout(StringLayout.SIDE_BY_SIDE) }
//                    )
//                },
//                modifier = Modifier.clickable { onSelectStringLayout(StringLayout.SIDE_BY_SIDE) }
//            )
//            Divider()
//
//            // Display type selection.
//            SectionLabel(title = stringResource(R.string.pref_display_type))
//
//            // Simple
//            ListItem(
//                text = { Text(stringResource(R.string.pref_display_type_simple)) },
//                trailing = {
//                    RadioButton(
//                        selected = prefs.displayType == TuningDisplayType.SIMPLE,
//                        onClick = { onSelectDisplayType(TuningDisplayType.SIMPLE) }
//                    )
//                },
//                modifier = Modifier.clickable { onSelectDisplayType(TuningDisplayType.SIMPLE) }
//            )
//
//            // Semitones
//            ListItem(
//                text = { Text(stringResource(R.string.pref_display_type_semitones)) },
//                secondaryText = { Text(stringResource(R.string.pref_display_type_semitones_desc)) },
//                trailing = {
//                    RadioButton(
//                        selected = prefs.displayType == TuningDisplayType.SEMITONES,
//                        onClick = { onSelectDisplayType(TuningDisplayType.SEMITONES) }
//                    )
//                },
//                modifier = Modifier.clickable { onSelectDisplayType(TuningDisplayType.SEMITONES) }
//            )
//
//            // Cents
//            ListItem(
//                text = { Text(stringResource(R.string.pref_display_type_cents)) },
//                secondaryText = { Text(stringResource(R.string.pref_display_type_cents_desc)) },
//                trailing = {
//                    RadioButton(
//                        selected = prefs.displayType == TuningDisplayType.CENTS,
//                        onClick = { onSelectDisplayType(TuningDisplayType.CENTS) }
//                    )
//                },
//                modifier = Modifier.clickable { onSelectDisplayType(TuningDisplayType.CENTS) }
//            )
//            Divider()
//
//            // Sound preferences
//            SectionLabel(title = stringResource(R.string.prefs_sound))
//
//            // String selection sound
//            ListItem(
//                text = { Text(stringResource(R.string.pref_enable_string_select_sound)) },
//                secondaryText = { Text(stringResource(R.string.pref_enable_string_select_sound_desc))},
//                trailing = {
//                    Switch(
//                        checked = prefs.enableStringSelectSound,
//                        onCheckedChange = onEnableStringSelectSound
//                    )
//                },
//                modifier = Modifier.clickable { onEnableStringSelectSound(!prefs.enableStringSelectSound) }
//            )
//
//            // In tune sound
//            ListItem(
//                text = { Text(stringResource(R.string.pref_enable_in_tune_sound)) },
//                secondaryText = { Text(stringResource(R.string.pref_enable_in_tune_sound_desc))},
//                trailing = {
//                    Switch(
//                        checked = prefs.enableInTuneSound,
//                        onCheckedChange = onEnableInTuneSound
//                    )
//                },
//                modifier = Modifier.clickable { onEnableInTuneSound(!prefs.enableInTuneSound) }
//            )
//
//            Divider()
//
//            // Theme preferences
//            SectionLabel(title = stringResource(R.string.prefs_theme))
//
//            // Full black theme
//            ListItem(
//                text = { Text(stringResource(R.string.pref_use_black_theme)) },
//                secondaryText = { Text(stringResource(R.string.pref_use_black_theme_desc))},
//                trailing = {
//                    Switch(
//                        checked = prefs.useBlackTheme,
//                        onCheckedChange = onSetUseBlackTheme
//                    )
//                },
//                modifier = Modifier.clickable { onSetUseBlackTheme(!prefs.useBlackTheme) }
//            )
//            Divider()
//
//            // About
//            SectionLabel(stringResource(R.string.about))
//            ListItem(
//                text = { Text("${stringResource(R.string.about)} ${stringResource(R.string.app_name)}") },
//                modifier = Modifier.clickable(onClick = onAboutPressed)
//            )
//        }
//    }
//}