package ru.avem.stand.view.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.avem.stand.db.DBManager
import ru.avem.stand.io.DevicePoller
import ru.avem.stand.limit
import ru.avem.stand.ms
import ru.avem.stand.tests.Lists
import ru.avem.stand.view.screens.main.MainMenuScreen

class InitializationScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        LaunchedEffect(Unit) {
            launch(Dispatchers.Default) {
                val initMS = ms()
                DevicePoller
                DBManager
                Lists
                delay(limit(1, 1000 - (ms() - initMS), 1000))
                navigator.replace(MainMenuScreen())
            }
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)

        ) {
            CircularProgressIndicator()
            Text("Подождите, идёт инициализация...")
        }
    }
}
