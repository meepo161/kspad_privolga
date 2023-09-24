package ru.avem.stand

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import ru.avem.stand.view.composables.CustomDialog
import ru.avem.stand.view.screens.InitializationScreen
import ru.avem.stand.view.theme.colors
import ru.avem.stand.view.theme.typography

@ExperimentalAnimationApi
fun main() = application {
    var isCloseRequested by remember { mutableStateOf(false) }
    Window(
        onCloseRequest = { isCloseRequested = true },
        state = rememberWindowState(size = DpSize(width.dp, height.dp), placement = WindowPlacement.Maximized),
        title = title,
        undecorated = true,
        resizable = false,
    ) {
        MaterialTheme(colors, typography) {
            if (isCloseRequested) {
                CustomDialog(
                    title = "Внимание",
                    text = "Вы действительно хотите выйти из программы?",
                    yesButton = "Да",
                    noButton = "Нет",
                    yesCallback = { exit() },
                    noCallback = { isCloseRequested = false }
                )
            }
            Scaffold(
                modifier = Modifier.padding(8.dp),
                topBar = {
                    TopAppBar(
                        title = { Text(title) },
                        actions = {
                            IconButton(onClick = { isCloseRequested = true }) {
                                Icon(
                                    imageVector = Icons.Filled.Cancel,
                                    contentDescription = "Выход"
                                )
                            }
                        }
                    )
                }
            ) {
                Navigator(InitializationScreen()) { SlideTransition(it) }
            }
        }
    }
}
