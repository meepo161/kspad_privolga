package ru.avem.stand.view.composables.table

import kotlin.reflect.KProperty1

class TableScheme<T>(
    val name: String,
    val columns: List<KProperty1<T, Any>>,
    val columnNames: List<String> = emptyList()
) {
    companion object {
        fun <T> create(name: String = "", columns: List<Pair<KProperty1<T, Any>, String>>) = TableScheme(
            name,
            columns.unzip().first,
            columns.unzip().second
        )

        infix fun <A : KProperty1<*, *>> A.named(that: String) = this to that
    }
}
