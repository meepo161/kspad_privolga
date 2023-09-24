package ru.avem.stand.view.composables.keyboards

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow

class AlphabeticalKeyboard(
    private val inputValue: MutableState<String>,
    private val isCanBeEmpty: Boolean = false,
    private val initialLanguage: KeyboardLanguage,
    private val languagesList: List<KeyboardLanguage> = listOf(KeyboardLanguage.RU, KeyboardLanguage.EN)
) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val tempValue = mutableStateOf(inputValue.value)
        var isCapsLock by remember { mutableStateOf(false) }
        var isDigitAndSymbols by remember { mutableStateOf(false) }
        var lang by remember { mutableStateOf(initialLanguage) }

        val onCapsPressed = {
            isCapsLock = !isCapsLock
        }
        val onDigitsPressed = {
            isDigitAndSymbols = true
        }
        val onAbcPressed = {
            isDigitAndSymbols = false
        }
        val onLangPressed = {
            val idx = languagesList.indexOf(lang)
            lang = if (idx < languagesList.size - 1) {
                languagesList[idx + 1]
            } else {
                languagesList[0]
            }
        }
        val onApplyPressed: () -> Unit = {
            if (isCanBeEmpty) {
                inputValue.value = tempValue.value
                navigator.pop()
            } else {
                if (tempValue.value.isNotBlank()) {
                    inputValue.value = tempValue.value
                    navigator.pop()
                }
            }
        }

        Scaffold(topBar = {
            TopAppBar(title = { Text("Введите значение") }, navigationIcon = {
                IconButton(
                    onClick = {
                        if (isCanBeEmpty) {
                            navigator.pop()
                        } else {
                            if (tempValue.value.isNotBlank()) {
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
                modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = tempValue.value,
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    onValueChange = {},
                    isError = if (!isCanBeEmpty) {
                        tempValue.value.isBlank()
                    } else {
                        false
                    },
                    singleLine = true,
                    textStyle = TextStyle(fontSize = 56.sp)
                )
                when (lang) {
                    KeyboardLanguage.EN -> {
                        if (isDigitAndSymbols) {
                            DigitsAndSymbols(tempValue, onAbcPressed)
                        } else if (isCapsLock) {
                            EnglishCapitalized(tempValue, onCapsPressed, onDigitsPressed, onLangPressed, onApplyPressed)
                        } else {
                            EnglishDecapitalized(
                                tempValue,
                                onCapsPressed,
                                onDigitsPressed,
                                onLangPressed,
                                onApplyPressed
                            )
                        }
                    }

                    KeyboardLanguage.RU -> {
                        if (isDigitAndSymbols) {
                            DigitsAndSymbols(tempValue, onAbcPressed)
                        } else if (isCapsLock) {
                            RussianCapitalized(tempValue, onCapsPressed, onDigitsPressed, onLangPressed, onApplyPressed)
                        } else {
                            RussianDecapitalized(
                                tempValue,
                                onCapsPressed,
                                onDigitsPressed,
                                onLangPressed,
                                onApplyPressed
                            )
                        }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    private fun EnglishDecapitalized(
        tempValue: MutableState<String>,
        onCapsPressed: () -> Unit,
        onDigitsPressed: () -> Unit,
        onLangPressed: () -> Unit,
        onApplyPressed: () -> Unit
    ) {
        Column(
            modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.weight(0.25f)
            ) {
                Button(onClick = {
                    tempValue.value += "q"
                }, modifier = Modifier.weight(0.1f).fillMaxHeight()) {
                    Text("q", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "w"
                }, modifier = Modifier.weight(0.1f).fillMaxHeight()) {
                    Text("w", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "e"
                }, modifier = Modifier.weight(0.1f).fillMaxHeight()) {
                    Text("e", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "r"
                }, modifier = Modifier.weight(0.1f).fillMaxHeight()) {
                    Text("r", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "t"
                }, modifier = Modifier.weight(0.1f).fillMaxHeight()) {
                    Text("t", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "y"
                }, modifier = Modifier.weight(0.1f).fillMaxHeight()) {
                    Text("y", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "u"
                }, modifier = Modifier.weight(0.1f).fillMaxHeight()) {
                    Text("u", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "i"
                }, modifier = Modifier.weight(0.1f).fillMaxHeight()) {
                    Text("i", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "o"
                }, modifier = Modifier.weight(0.1f).fillMaxHeight()) {
                    Text("o", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "p"
                }, modifier = Modifier.weight(0.1f).fillMaxHeight()) {
                    Text("p", style = MaterialTheme.typography.h5)
                }
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.weight(0.25f)
            ) {
                Button(onClick = {
                    tempValue.value += "a"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("a", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "s"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("s", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "d"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("d", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "f"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("f", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "g"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("g", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "h"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("h", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "j"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("j", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "k"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("k", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "l"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("l", style = MaterialTheme.typography.h5)
                }
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.weight(0.25f)
            ) {
                Button(onClick = {
                    onCapsPressed()
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Filled.KeyboardCapslock,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp)
                        )
                        Text("Caps", style = MaterialTheme.typography.h5)
                    }
                }
                Button(onClick = {
                    tempValue.value += "z"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("z", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "x"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("x", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "c"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("c", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "v"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("v", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "b"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("b", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "n"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("n", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "m"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("m", style = MaterialTheme.typography.h5)
                }
                Surface(
                    color = MaterialTheme.colors.primary,
                    contentColor = MaterialTheme.colors.contentColorFor(MaterialTheme.colors.primary),
                    elevation = 2.dp,
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.weight(0.10f).fillMaxHeight().combinedClickable(
                        onLongClick = {
                            tempValue.value = ""
                        },
                        onClick = {
                            tempValue.value = tempValue.value.dropLast(1)
                        }
                    )) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Filled.KeyboardBackspace,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp)
                        )
                        Text("BckSpc", style = MaterialTheme.typography.h5)
                    }
                }
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.weight(0.25f)
            ) {
                Button(onClick = {
                    onDigitsPressed()
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.Dialpad, contentDescription = null, modifier = Modifier.size(48.dp))
                        Text("123?!", style = MaterialTheme.typography.h5)
                    }
                }
                Button(onClick = {
                    onLangPressed()
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.Language, contentDescription = null, modifier = Modifier.size(48.dp))
                        Text("Lang", style = MaterialTheme.typography.h5)
                    }
                }
                Button(onClick = {
                    tempValue.value += " "
                }, modifier = Modifier.weight(0.60f).fillMaxHeight()) {
                    Text("Space", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "."
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text(".", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    onApplyPressed()
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(48.dp))
                        Text("Enter", style = MaterialTheme.typography.h5)
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    private fun RussianDecapitalized(
        tempValue: MutableState<String>,
        onCapsPressed: () -> Unit,
        onDigitsPressed: () -> Unit,
        onLangPressed: () -> Unit,
        onApplyPressed: () -> Unit
    ) {
        Column(
            modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.weight(0.25f)
            ) {
                Button(onClick = {
                    tempValue.value += "й"
                }, modifier = Modifier.weight(0.1f).fillMaxHeight()) {
                    Text("й", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "ц"
                }, modifier = Modifier.weight(0.1f).fillMaxHeight()) {
                    Text("ц", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "у"
                }, modifier = Modifier.weight(0.1f).fillMaxHeight()) {
                    Text("у", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "к"
                }, modifier = Modifier.weight(0.1f).fillMaxHeight()) {
                    Text("к", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "е"
                }, modifier = Modifier.weight(0.1f).fillMaxHeight()) {
                    Text("е", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "н"
                }, modifier = Modifier.weight(0.1f).fillMaxHeight()) {
                    Text("н", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "г"
                }, modifier = Modifier.weight(0.1f).fillMaxHeight()) {
                    Text("г", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "ш"
                }, modifier = Modifier.weight(0.1f).fillMaxHeight()) {
                    Text("ш", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "щ"
                }, modifier = Modifier.weight(0.1f).fillMaxHeight()) {
                    Text("щ", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "з"
                }, modifier = Modifier.weight(0.1f).fillMaxHeight()) {
                    Text("з", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "х"
                }, modifier = Modifier.weight(0.1f).fillMaxHeight()) {
                    Text("х", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "ъ"
                }, modifier = Modifier.weight(0.1f).fillMaxHeight()) {
                    Text("ъ", style = MaterialTheme.typography.h5)
                }
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.weight(0.25f)
            ) {
                Button(onClick = {
                    tempValue.value += "ф"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("ф", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "ы"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("ы", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "в"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("в", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "а"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("а", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "п"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("п", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "р"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("р", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "о"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("о", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "л"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("л", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "д"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("д", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "ж"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("ж", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "э"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("э", style = MaterialTheme.typography.h5)
                }
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.weight(0.25f)
            ) {
                Button(onClick = {
                    onCapsPressed()
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Filled.KeyboardCapslock,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp)
                        )
                        Text("Рег", style = MaterialTheme.typography.h5)
                    }
                }
                Button(onClick = {
                    tempValue.value += "я"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("я", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "ч"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("ч", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "с"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("с", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "м"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("м", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "и"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("и", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "т"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("т", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "ь"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("ь", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "б"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("б", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "ю"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("ю", style = MaterialTheme.typography.h5)
                }
                Surface(
                    color = MaterialTheme.colors.primary,
                    contentColor = MaterialTheme.colors.onPrimary,
                    elevation = 2.dp,
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.weight(0.10f).fillMaxHeight().combinedClickable(
                        onLongClick = {
                            tempValue.value = ""
                        },
                        onClick = {
                            tempValue.value = tempValue.value.dropLast(1)
                        }
                    )) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Filled.KeyboardBackspace,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp)
                        )
                        Text("Удалить", style = MaterialTheme.typography.h5)
                    }
                }
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.weight(0.25f)
            ) {
                Button(onClick = {
                    onDigitsPressed()
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.Dialpad, contentDescription = null, modifier = Modifier.size(48.dp))
                        Text("123?!", style = MaterialTheme.typography.h5)
                    }
                }
                Button(onClick = {
                    onLangPressed()
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.Language, contentDescription = null, modifier = Modifier.size(48.dp))
                        Text("Язык", style = MaterialTheme.typography.h5)
                    }
                }
                Button(onClick = {
                    tempValue.value += " "
                }, modifier = Modifier.weight(0.60f).fillMaxHeight()) {
                    Text("Пробел", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += ","
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text(",", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    onApplyPressed()
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(48.dp))
                        Text("Ввод", style = MaterialTheme.typography.h5)
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    private fun RussianCapitalized(
        tempValue: MutableState<String>,
        onCapsPressed: () -> Unit,
        onDigitsPressed: () -> Unit,
        onLangPressed: () -> Unit,
        onApplyPressed: () -> Unit
    ) {
        Column(
            modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.weight(0.25f)
            ) {
                Button(onClick = {
                    tempValue.value += "Й"
                }, modifier = Modifier.weight(0.1f).fillMaxHeight()) {
                    Text("Й", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "Ц"
                }, modifier = Modifier.weight(0.1f).fillMaxHeight()) {
                    Text("Ц", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "У"
                }, modifier = Modifier.weight(0.1f).fillMaxHeight()) {
                    Text("У", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "К"
                }, modifier = Modifier.weight(0.1f).fillMaxHeight()) {
                    Text("К", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "Е"
                }, modifier = Modifier.weight(0.1f).fillMaxHeight()) {
                    Text("Е", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "Н"
                }, modifier = Modifier.weight(0.1f).fillMaxHeight()) {
                    Text("Н", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "Г"
                }, modifier = Modifier.weight(0.1f).fillMaxHeight()) {
                    Text("Г", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "Ш"
                }, modifier = Modifier.weight(0.1f).fillMaxHeight()) {
                    Text("Ш", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "Щ"
                }, modifier = Modifier.weight(0.1f).fillMaxHeight()) {
                    Text("Щ", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "З"
                }, modifier = Modifier.weight(0.1f).fillMaxHeight()) {
                    Text("З", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "Х"
                }, modifier = Modifier.weight(0.1f).fillMaxHeight()) {
                    Text("Х", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "Ъ"
                }, modifier = Modifier.weight(0.1f).fillMaxHeight()) {
                    Text("Ъ", style = MaterialTheme.typography.h5)
                }
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.weight(0.25f)
            ) {
                Button(onClick = {
                    tempValue.value += "Ф"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("Ф", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "Ы"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("Ы", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "В"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("В", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "А"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("А", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "П"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("П", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "Р"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("Р", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "О"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("О", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "Л"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("Л", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "Д"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("Д", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "Ж"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("Ж", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "Э"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("Э", style = MaterialTheme.typography.h5)
                }
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.weight(0.25f)
            ) {
                Button(onClick = {
                    onCapsPressed()
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Filled.KeyboardCapslock,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp)
                        )
                        Text("Caps", style = MaterialTheme.typography.h5)
                    }
                }
                Button(onClick = {
                    tempValue.value += "Я"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("Я", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "Ч"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("Ч", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "С"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("С", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "М"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("М", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "И"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("И", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "Т"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("Т", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "Ь"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("Ь", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "Б"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("Б", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "Ю"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("Ю", style = MaterialTheme.typography.h5)
                }
                Surface(
                    color = MaterialTheme.colors.primary,
                    contentColor = MaterialTheme.colors.onPrimary,
                    elevation = 2.dp,
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.weight(0.10f).fillMaxHeight().combinedClickable(
                        onLongClick = {
                            tempValue.value = ""
                        },
                        onClick = {
                            tempValue.value = tempValue.value.dropLast(1)
                        }
                    )) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Filled.KeyboardBackspace,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp)
                        )
                        Text("BckSpc", style = MaterialTheme.typography.h5)
                    }
                }
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.weight(0.25f)
            ) {
                Button(onClick = {
                    onDigitsPressed()
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.Dialpad, contentDescription = null, modifier = Modifier.size(48.dp))
                        Text("123?!", style = MaterialTheme.typography.h5)
                    }
                }
                Button(onClick = {
                    onLangPressed()
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.Language, contentDescription = null, modifier = Modifier.size(48.dp))
                        Text("Язык", style = MaterialTheme.typography.h5)
                    }
                }
                Button(onClick = {
                    tempValue.value += " "
                }, modifier = Modifier.weight(0.60f).fillMaxHeight()) {
                    Text("Пробел", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += ","
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text(",", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    onApplyPressed()
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(48.dp))
                        Text("Ввод", style = MaterialTheme.typography.h5)
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    private fun EnglishCapitalized(
        tempValue: MutableState<String>,
        onCapsPressed: () -> Unit,
        onDigitsPressed: () -> Unit,
        onLangPressed: () -> Unit,
        onApplyPressed: () -> Unit
    ) {
        Column(
            modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.weight(0.25f)
            ) {
                Button(onClick = {
                    tempValue.value += "Q"
                }, modifier = Modifier.weight(0.1f).fillMaxHeight()) {
                    Text("Q", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "W"
                }, modifier = Modifier.weight(0.1f).fillMaxHeight()) {
                    Text("W", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "E"
                }, modifier = Modifier.weight(0.1f).fillMaxHeight()) {
                    Text("E", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "R"
                }, modifier = Modifier.weight(0.1f).fillMaxHeight()) {
                    Text("R", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "T"
                }, modifier = Modifier.weight(0.1f).fillMaxHeight()) {
                    Text("T", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "Y"
                }, modifier = Modifier.weight(0.1f).fillMaxHeight()) {
                    Text("Y", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "U"
                }, modifier = Modifier.weight(0.1f).fillMaxHeight()) {
                    Text("U", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "I"
                }, modifier = Modifier.weight(0.1f).fillMaxHeight()) {
                    Text("I", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "O"
                }, modifier = Modifier.weight(0.1f).fillMaxHeight()) {
                    Text("O", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "P"
                }, modifier = Modifier.weight(0.1f).fillMaxHeight()) {
                    Text("P", style = MaterialTheme.typography.h5)
                }
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.weight(0.25f)
            ) {
                Button(onClick = {
                    tempValue.value += "A"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("A", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "S"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("S", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "D"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("D", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "F"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("F", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "G"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("G", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "H"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("H", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "J"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("J", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "K"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("K", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "L"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("L", style = MaterialTheme.typography.h5)
                }
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.weight(0.25f)
            ) {
                Button(onClick = {
                    onCapsPressed()
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Filled.KeyboardCapslock,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp)
                        )
                        Text("Caps", style = MaterialTheme.typography.h5)
                    }
                }
                Button(onClick = {
                    tempValue.value += "Z"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("Z", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "X"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("X", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "C"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("C", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "V"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("V", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "B"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("B", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "N"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("N", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "M"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("M", style = MaterialTheme.typography.h5)
                }
                Surface(
                    color = MaterialTheme.colors.primary,
                    contentColor = MaterialTheme.colors.onPrimary,
                    elevation = 2.dp,
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.weight(0.10f).fillMaxHeight().combinedClickable(
                        onLongClick = {
                            tempValue.value = ""
                        },
                        onClick = {
                            tempValue.value = tempValue.value.dropLast(1)
                        }
                    )) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Filled.KeyboardBackspace,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp)
                        )
                        Text("BckSpc", style = MaterialTheme.typography.h5)
                    }
                }
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.weight(0.25f)
            ) {
                Button(onClick = {
                    onDigitsPressed()
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.Dialpad, contentDescription = null, modifier = Modifier.size(48.dp))
                        Text("123?!", style = MaterialTheme.typography.h5)
                    }
                }
                Button(onClick = {
                    onLangPressed()
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.Language, contentDescription = null, modifier = Modifier.size(48.dp))
                        Text("Lang", style = MaterialTheme.typography.h5)
                    }
                }
                Button(onClick = {
                    tempValue.value += " "
                }, modifier = Modifier.weight(0.60f).fillMaxHeight()) {
                    Text("Space", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += ","
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text(",", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    onApplyPressed()
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(48.dp))
                        Text("Enter", style = MaterialTheme.typography.h5)
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun DigitsAndSymbols(tempValue: MutableState<String>, onAbcPressed: () -> Unit) {
        Column(
            modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.weight(0.25f)
            ) {
                Button(onClick = {
                    tempValue.value += "1"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("1", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "2"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("2", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "3"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("3", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "4"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("4", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "5"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("5", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "6"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("6", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "7"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("7", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "8"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("8", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "9"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("9", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "0"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("0", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "-"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("-", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "="
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("=", style = MaterialTheme.typography.h5)
                }
                Surface(
                    color = MaterialTheme.colors.primary,
                    contentColor = MaterialTheme.colors.contentColorFor(MaterialTheme.colors.primary),
                    elevation = 2.dp,
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.weight(0.10f).fillMaxHeight().combinedClickable(
                        onLongClick = {
                            tempValue.value = ""
                        },
                        onClick = {
                            tempValue.value = tempValue.value.dropLast(1)
                        }
                    )) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Filled.KeyboardBackspace,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp)
                        )
                        Text("BckSpc", style = MaterialTheme.typography.h5)
                    }
                }

            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.weight(0.25f)
            ) {
                Button(onClick = {
                    tempValue.value += "`"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("`", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "~"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("~", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "!"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("!", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "@"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("@", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "#"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("#", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "$"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("$", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "%"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("%", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "^"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("^", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "&"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("&", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "*"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("*", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "("
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("(", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += ")"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text(")", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "_"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("_", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "+"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("+", style = MaterialTheme.typography.h5)
                }
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.weight(0.25f)
            ) {
                Button(onClick = {
                    onAbcPressed()
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.Dialpad, contentDescription = null, modifier = Modifier.size(48.dp))
                        Text("Abc", style = MaterialTheme.typography.h5)
                    }
                }
                Button(onClick = {
                    tempValue.value += "\""
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("\"", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "№"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("№", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += ";"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text(";", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += ":"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text(":", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "?"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("?", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "'"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("'", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "\\"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("\\", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "|"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("|", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "/"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("/", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += "<"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text("<", style = MaterialTheme.typography.h5)
                }
                Button(onClick = {
                    tempValue.value += ">"
                }, modifier = Modifier.weight(0.10f).fillMaxHeight()) {
                    Text(">", style = MaterialTheme.typography.h5)
                }
            }
        }
    }
}