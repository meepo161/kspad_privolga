package ru.avem.stand.view.composables.keyboards

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow

class NumericalKeyboard(private val inputValue: MutableState<String>, private val isCanBeEmpty: Boolean = false) :
    Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        Scaffold(topBar = {
            TopAppBar(title = { Text("Введите значение") }, navigationIcon = {
                IconButton(
                    onClick = {
                        if (isCanBeEmpty) {
                            if (inputValue.value.isEmpty() || inputValue.value.toFloatOrNull() != null) {
                                navigator.pop()
                            }
                        } else {
                            if (inputValue.value.toFloatOrNull() != null) {
                                navigator.pop()
                            }
                        }
                    },
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = null)
                        Text("Назад")
                    }
                }
            })
        }) {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = inputValue.value,
                    modifier = Modifier.fillMaxWidth().weight(.25f),
                    readOnly = true,
                    onValueChange = {},
                    isError = if (isCanBeEmpty) {
                        inputValue.value.toFloatOrNull() == null && inputValue.value.isNotEmpty()
                    } else {
                        inputValue.value.toFloatOrNull() == null
                    },
                    singleLine = true,
                    textStyle = TextStyle(fontSize = 56.sp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth().weight(.25f), horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(modifier = Modifier.weight(.3f).fillMaxHeight(), onClick = {
                        inputValue.value += "7"
                    }) {
                        Text("7", style = TextStyle(fontSize = 56.sp))
                    }
                    Button(modifier = Modifier.weight(.3f).fillMaxHeight(), onClick = {
                        inputValue.value += "8"
                    }) {
                        Text("8", style = TextStyle(fontSize = 56.sp))
                    }
                    Button(modifier = Modifier.weight(.3f).fillMaxHeight(), onClick = {
                        inputValue.value += "9"
                    }) {
                        Text("9", style = TextStyle(fontSize = 56.sp))
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth().weight(.25f), horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(modifier = Modifier.weight(.3f).fillMaxHeight(), onClick = {
                        inputValue.value += "4"
                    }) {
                        Text("4", style = TextStyle(fontSize = 56.sp))
                    }
                    Button(modifier = Modifier.weight(.3f).fillMaxHeight(), onClick = {
                        inputValue.value += "5"
                    }) {
                        Text("5", style = TextStyle(fontSize = 56.sp))
                    }
                    Button(modifier = Modifier.weight(.3f).fillMaxHeight(), onClick = {
                        inputValue.value += "6"
                    }) {
                        Text("6", style = TextStyle(fontSize = 56.sp))
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth().weight(.25f), horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(modifier = Modifier.weight(.3f).fillMaxHeight(), onClick = {
                        inputValue.value += "1"
                    }) {
                        Text("1", style = TextStyle(fontSize = 56.sp))
                    }
                    Button(modifier = Modifier.weight(.3f).fillMaxHeight(), onClick = {
                        inputValue.value += "2"
                    }) {
                        Text("2", style = TextStyle(fontSize = 56.sp))
                    }
                    Button(modifier = Modifier.weight(.3f).fillMaxHeight(), onClick = {
                        inputValue.value += "3"
                    }) {
                        Text("3", style = TextStyle(fontSize = 56.sp))
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth().weight(.25f), horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(modifier = Modifier.weight(.3f).fillMaxHeight(), onClick = {
                        inputValue.value += "0"
                    }) {
                        Text("0", style = TextStyle(fontSize = 56.sp))
                    }
                    Row(
                        modifier = Modifier.weight(.3f).fillMaxHeight(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(modifier = Modifier.weight(.5f).fillMaxHeight(), onClick = {
                            inputValue.value += "."
                        }) {
                            Text(".", style = TextStyle(fontSize = 56.sp))
                        }
                        Button(modifier = Modifier.weight(.5f).fillMaxHeight(), onClick = {
                            inputValue.value = inputValue.value.dropLast(1)
                        }) {
                            Icon(Icons.Filled.ArrowBack, modifier = Modifier.size(56.dp), contentDescription = null)
                        }
                    }
                    Button(modifier = Modifier.weight(.3f).fillMaxHeight(), onClick = {
                        inputValue.value = ""
                    }) {
                        Text("C", style = TextStyle(fontSize = 56.sp))
                    }
                }
            }
        }
    }
}