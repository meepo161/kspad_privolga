package ru.avem.stand.view.composables

import androidx.compose.foundation.layout.*
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CustomDialog(
    modifier: Modifier = Modifier.width(500.dp),
    title: String,
    text: String,
    yesButton: String = "Да",
    noButton: String = "Нет",
    yesCallback: () -> Unit,
    noCallback: () -> Unit,
    content: @Composable BoxScope.() -> Unit = {}
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = {},
        title = { Text(title, style = MaterialTheme.typography.h5) },
        text = { Text(text, style = MaterialTheme.typography.h5) },
        buttons = {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Box(modifier = Modifier.fillMaxWidth()) { content() }
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(onClick = {
                        yesCallback()
                    }, modifier = Modifier.fillMaxWidth().height(52.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = yesButton, style = MaterialTheme.typography.h5)
                        }
                    }
                    Button(onClick = {
                        noCallback()
                    }, modifier = Modifier.fillMaxWidth().height(52.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = noButton, style = MaterialTheme.typography.h5)
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun CustomDialog(
    modifier: Modifier = Modifier.width(600.dp),
    title: String = "",
    text: String = "",
    okButton: String = "ОК",
    okCallback: () -> Unit = {},
    content: @Composable BoxScope.() -> Unit = {}
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = {},
        title = { Text(title, style = MaterialTheme.typography.h5) },
        text = { Text(text, style = MaterialTheme.typography.h5) },
        buttons = {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Box(modifier = Modifier.fillMaxWidth()) { content() }
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(onClick = {
                        okCallback()
                    }, modifier = Modifier.fillMaxWidth().height(52.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = okButton, style = MaterialTheme.typography.h5)
                        }
                    }
                }
            }
        }
    )
}
