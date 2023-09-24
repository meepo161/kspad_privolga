package ru.avem.stand.tests.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

data class TestRow(
    val c1: MutableState<String> = mutableStateOf(""),
    val c2: MutableState<String> = mutableStateOf(""),
    val c3: MutableState<String> = mutableStateOf(""),
    val c4: MutableState<String> = mutableStateOf(""),
    val c5: MutableState<String> = mutableStateOf(""),
    val c6: MutableState<String> = mutableStateOf(""),
    val c7: MutableState<String> = mutableStateOf(""),
    val c8: MutableState<String> = mutableStateOf(""),
)
