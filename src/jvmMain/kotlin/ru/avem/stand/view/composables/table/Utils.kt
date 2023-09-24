package ru.avem.stand.view.composables.table

import kotlin.reflect.full.memberProperties

@Throws(IllegalAccessException::class, ClassCastException::class)
inline fun <reified T> Any.getField(fieldName: String): T? {
    this::class.memberProperties.forEach { field ->
        if (fieldName == field.name) {
            return field.getter.call(this) as T?
        }
    }
    return null
}
