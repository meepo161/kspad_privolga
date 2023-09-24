package ru.avem.stand

import java.util.*
import kotlin.math.abs

fun Number.af() = with(abs(String.format(Locale.US, "%.4f", toDouble()).toDouble())) {
    val format = when {
        this.isNaN() -> return ""
        this < 1.0 -> "%.2f"
        this < 10 -> "%.1f"
        else -> "%.0f"
    }
    String.format(Locale.US, format, this@af.toDouble())
}

fun Number.formatPoint(n: Int = 0) = with(toDouble()) {
    val format = when {
        this.isNaN() -> return ""
        else -> "%.${n}f"
    }
    String.format(Locale.US, format, this@formatPoint.toDouble())
}

fun ms() = System.currentTimeMillis()

fun <T : Number> limit(min: T, value: T, max: T): T = when {
    value.toDouble() < min.toDouble() -> min
    value.toDouble() > max.toDouble() -> max
    else -> value
}

fun String.num() = toDoubleOrNull() ?: 0.0

fun <C : S, S> C.addTo(list: MutableList<S>): C {
    list.add(this)
    return this
}

fun <T> List<T>.second(): T {
    if (isEmpty() && size < 2) {
        throw NoSuchElementException("List invalid size.")
    }
    return this[1]
}
