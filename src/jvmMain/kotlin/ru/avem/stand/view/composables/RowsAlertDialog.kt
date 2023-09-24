package ru.avem.stand.view.composables

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.focusTarget
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class DialogRowData(
    val name: String,
    val type: DialogRowType = DialogRowType.TEXT,
    val isCanEmpty: Boolean = true,
    val minValue: Double = -Double.MAX_VALUE,
    val maxValue: Double = Double.MAX_VALUE,
    var variableField: List<String> = listOf(),
    var field: MutableState<String> = mutableStateOf(""),
    var isVisible: MutableState<Boolean> = mutableStateOf(true),
    var errorState: MutableState<Boolean> = mutableStateOf(false)
)

enum class DialogRowType {
    LABEL,
    TEXT,
    NUMBERIC,
    COMBO
}

@Composable
fun RowsAlertDialog(
    title: String,
    rows: List<DialogRowData>,
    isDialogVisible: MutableState<Boolean>,
    buttonText: String,
    checkCreateProjectErrors: () -> Unit,
    onClick: () -> Unit
) {
    val focusManager = LocalFocusManager.current

    AlertDialog(
        onDismissRequest = { },
        title = null,
        buttons = {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = MaterialTheme.colors.primary)) {
                            append(title.first())
                        }
                        withStyle(style = SpanStyle(color = Color.Black)) {
                            append(title.substring(1))
                        }
                    }, fontSize = 36.sp)
                    IconButton(onClick = {
                        isDialogVisible.value = false
                    }) {
                        Icon(Icons.Filled.Close, contentDescription = null)
                    }
                }
                ScrollableLazyColumn(
                    modifier = Modifier.padding(top = 8.dp).height(800.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        rows.forEach { dialogRowData ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth().padding(end = 16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    when (dialogRowData.type) {
                                        DialogRowType.LABEL -> {
                                            Text(
                                                text = dialogRowData.name,
                                                style = MaterialTheme.typography.h5
                                            )
                                        }

                                        DialogRowType.TEXT -> {
                                            OutlinedTextField(
                                                singleLine = true,
                                                value = dialogRowData.field.value,
                                                onValueChange = {
                                                    dialogRowData.field.value = it
                                                    checkCreateProjectErrors()
                                                },
                                                isError = dialogRowData.errorState.value,
                                                modifier = Modifier.fillMaxWidth().focusTarget().onPreviewKeyEvent {
                                                    keyEventNext(it, focusManager)
                                                },
                                                label = {
                                                    Text(text = dialogRowData.name)
                                                },
                                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                                                keyboardActions = keyboardActionNext(focusManager)
                                            )
                                            if (dialogRowData.errorState.value) {
                                                Text(
                                                    text = "Проверьте правильность поля",
                                                    color = MaterialTheme.colors.error
                                                )
                                            }
                                        }

                                        DialogRowType.NUMBERIC -> {
                                            OutlinedTextField(
                                                singleLine = true,
                                                value = dialogRowData.field.value,
                                                onValueChange = {
                                                    dialogRowData.field.value = it
                                                    checkCreateProjectErrors()
                                                },
                                                isError = dialogRowData.errorState.value,
                                                modifier = Modifier.fillMaxWidth().focusTarget().onPreviewKeyEvent {
                                                    keyEventNext(it, focusManager)
                                                },
                                                label = {
                                                    Text(text = dialogRowData.name)
                                                },
                                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                                                keyboardActions = keyboardActionNext(focusManager)
                                            )
                                            if (dialogRowData.errorState.value) {
                                                Text(
                                                    text = "Проверьте правильность поля",
                                                    color = MaterialTheme.colors.error
                                                )
                                            }
                                        }

                                        DialogRowType.COMBO -> {
                                            Column {
                                                Text(text = dialogRowData.name)
                                                ComboBox(
                                                    modifier = Modifier.fillMaxWidth().focusTarget().onPreviewKeyEvent {
                                                        keyEventNext(it, focusManager)
                                                    },
                                                    selectedItem = dialogRowData.field, // TODO check dialogRowData.variableField.first()
                                                    items = dialogRowData.variableField,
                                                    onSelect = {
                                                        checkCreateProjectErrors()
                                                    },
                                                    onDismissState = {}
                                                )
                                            }
                                            if (dialogRowData.errorState.value) {
                                                Text(
                                                    text = "Проверьте правильность поля",
                                                    color = MaterialTheme.colors.error
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            onClick()
                        }) {
                        Text(buttonText)
                    }
                }
            }
        },
        text = null
    )
}

fun keyboardActionNext(focusManager: FocusManager) = KeyboardActions(
    onNext = { focusManager.moveFocus(FocusDirection.Next) }
)

fun keyEventNext(
    it: KeyEvent,
    focusManager: FocusManager
) = if (it.key == Key.Tab) {
    focusManager.moveFocus(FocusDirection.Next); true
} else false
