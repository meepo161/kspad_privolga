package ru.avem.stand.db

import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties

val LOGIN_1_KEY = "login1"
val LOGIN_2_KEY = "login2"

val ADMIN = "admin"
val VIP = "vip"
val DEFAULT = "default"

enum class TypeEnterField {
    TEXT,
    COMBO,
    RADIO,
    CHECK;
}

enum class TypeFormatTestItemField {
    BOOLEAN,
    STRING,
    INT,
    FLOAT,
    LONG,
    DOUBLE;
}

@Suppress("UNCHECKED_CAST")
fun <R> readInstanceProperty(instance: Any, propertyName: String) =
    when {
        instance::class.memberProperties.any { it.name == propertyName } -> (instance::class.memberProperties.first { it.name == propertyName } as KProperty1<Any, *>).get(
            instance
        ) as R

        else -> null
    }

inline fun <reified T> forValue(value: String = "", default: T? = null): T where T : Enum<T> {
    val allValues = mutableSetOf<String>()

    enumValues<T>().forEach { enumItem ->
        val availableValues = mutableListOf<String>()

        ((readInstanceProperty(enumItem, "name")) as String?)?.let { name ->
            availableValues.add(name)
            allValues.add(name)
        }

        ((readInstanceProperty(enumItem, "value")) as String?)?.let { value ->
            availableValues.add(value)
            allValues.add(value)
        }

        ((readInstanceProperty(enumItem, "aliases")) as List<String>?)?.let { aliases ->
            aliases.forEach { value ->
                availableValues.add(value)
                allValues.add(value)
            }
        }

        availableValues.firstOrNull {
            it.equals(value, true)
        }?.let {
            return enumItem
        }
    }

    if (default != null) return default

    throw Exception("Указанное значение [$value] не входит в поддерживаемый перечень:\n${allValues}")
}

data class TestItemFieldScheme(
    var key: String,
    var title: String = "",

    var typeEnterRaw: String = TypeEnterField.TEXT.toString(),
    var typeFormatRaw: String = TypeFormatTestItemField.STRING.toString(),

    var minValue: String = "",
    var value: String = "",
    var maxValue: String = "",
    var unit: String = "",

    var permittedValuesString: String = "",
    var permittedTitlesString: String = "",

    var blockName: String = "Дополнительные",

    var isNotVoid: Boolean = true
)
