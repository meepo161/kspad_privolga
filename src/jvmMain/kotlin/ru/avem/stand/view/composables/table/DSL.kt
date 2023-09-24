package ru.avem.stand.view.composables.table

import kotlin.reflect.KProperty1

fun <T> tables(vararg tables: T) = listOf(*tables)
fun <T> columns(vararg columns: T) = listOf(*columns)
fun <T> table1(columns: List<Pair<KProperty1<T, Any>, String>>, name: String = "") = TableScheme.create(name, columns)
fun <T> table2(columns: List<Pair<KProperty1<T, Any>, String>>, name: String = "") = TableScheme.create(name, columns)
fun <T> table3(columns: List<Pair<KProperty1<T, Any>, String>>, name: String = "") = TableScheme.create(name, columns)
fun <T> table4(columns: List<Pair<KProperty1<T, Any>, String>>, name: String = "") = TableScheme.create(name, columns)
fun <T> table5(columns: List<Pair<KProperty1<T, Any>, String>>, name: String = "") = TableScheme.create(name, columns)
fun <T> table6(columns: List<Pair<KProperty1<T, Any>, String>>, name: String = "") = TableScheme.create(name, columns)
fun <T> table7(columns: List<Pair<KProperty1<T, Any>, String>>, name: String = "") = TableScheme.create(name, columns)
fun <T> table8(columns: List<Pair<KProperty1<T, Any>, String>>, name: String = "") = TableScheme.create(name, columns)
fun <T> table9(columns: List<Pair<KProperty1<T, Any>, String>>, name: String = "") = TableScheme.create(name, columns)
fun <T> table10(columns: List<Pair<KProperty1<T, Any>, String>>, name: String = "") = TableScheme.create(name, columns)
