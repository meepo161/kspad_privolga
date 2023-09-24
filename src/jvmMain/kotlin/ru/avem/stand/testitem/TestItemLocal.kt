package ru.avem.stand.testitem

import ru.avem.stand.db.TestItemFieldScheme
import ru.avem.stand.db.TypeFormatTestItemField
import ru.avem.stand.formatPoint

data class TestItemLocal(
    val id: Int,
    val name: String,

    val nominalPower: Double,
    val nominalVoltage: Double,
    val nominalCurrent: Double,
    val nominalN: Double,
    val nominalCos: Double,

    val motor: String,
    val scheme: String,
    val frequency: Double,

    val hvVoltage: Double,
    val hvCurrent: Double,

    val meggerVoltage: Double,

    val idleTestTime: Double,
    val runoutTestTime: Double,
    val hvTestTime: Double,

    val meggerSVoltage: Double,
    val hvSVoltage: Double,
    val hvSCurrent: Double,
    val hvSTestTime: Double,
) {
    fun convertToDBType() = listOf(
        TestItemFieldScheme(
            key = "name",
            title = "Наименование",
            typeFormatRaw = TypeFormatTestItemField.STRING.toString(),
            value = name,
            blockName = "Номинальные параметры"
        ),
        TestItemFieldScheme(
            key = "nominalPower",
            title = "Мощность",
            typeFormatRaw = TypeFormatTestItemField.DOUBLE.toString(),
            minValue = "0.01",
            value = nominalPower.formatPoint(2),
            maxValue = "1000",
            unit = "kW",
            blockName = "Номинальные параметры"
        ),
        TestItemFieldScheme(
            key = "nominalVoltage",
            title = "Напряжение линейное",
            typeFormatRaw = TypeFormatTestItemField.DOUBLE.toString(),
            minValue = "380",
            value = nominalVoltage.formatPoint(),
            maxValue = "380",
            unit = "V",
            blockName = "Номинальные параметры"
        ),
        TestItemFieldScheme(
            key = "nominalCurrent",
            title = "Ток",
            typeFormatRaw = TypeFormatTestItemField.DOUBLE.toString(),
            minValue = "0",
            value = nominalCurrent.formatPoint(2),
            maxValue = "40",
            unit = "A",
            blockName = "Номинальные параметры"
        ),
        TestItemFieldScheme(
            key = "nominalN",
            title = "Скорость вращения",
            typeFormatRaw = TypeFormatTestItemField.DOUBLE.toString(),
            minValue = "0",
            value = nominalN.formatPoint(),
            maxValue = "3000",
            unit = "об/мин",
            blockName = "Номинальные параметры"
        ),
        TestItemFieldScheme(
            key = "nominalCos",
            title = "cos ф",
            typeFormatRaw = TypeFormatTestItemField.DOUBLE.toString(),
            minValue = "0",
            value = nominalCos.formatPoint(2),
            maxValue = "1",
            unit = "о.е.",
            blockName = "Номинальные параметры"
        ),
        TestItemFieldScheme(
            key = "motor",
            title = "Тип",
            typeFormatRaw = TypeFormatTestItemField.STRING.toString(),
            value = motor,
            permittedValuesString = "АД,АД с ФР,АД с термодатчиками,АД с ФР и термодатчиками",
            blockName = "Номинальные параметры"
        ),
        TestItemFieldScheme(
            key = "scheme",
            title = "Схема",
            typeFormatRaw = TypeFormatTestItemField.STRING.toString(),
            value = scheme,
            permittedValuesString = "△,Y",
            blockName = "Номинальные параметры"
        ),
        TestItemFieldScheme(
            key = "frequency",
            title = "Частота",
            typeFormatRaw = TypeFormatTestItemField.DOUBLE.toString(),
            minValue = "50",
            value = frequency.formatPoint(),
            maxValue = "50",
            unit = "Hz",
            blockName = "Номинальные параметры"
        ),
        TestItemFieldScheme(
            key = "hvVoltage",
            title = "Напряжение ВИУ",
            typeFormatRaw = TypeFormatTestItemField.DOUBLE.toString(),
            minValue = "1000",
            value = hvVoltage.formatPoint(),
            maxValue = "3000",
            unit = "V",
            blockName = "Параметры испытаний"
        ),
        TestItemFieldScheme(
            key = "hvCurrent",
            title = "Ток утечки ВИУ",
            typeFormatRaw = TypeFormatTestItemField.DOUBLE.toString(),
            minValue = "0",
            value = hvCurrent.formatPoint(),
            maxValue = "100",
            unit = "mA",
            blockName = "Параметры испытаний"
        ),
        TestItemFieldScheme(
            key = "hvTestTime",
            title = "Время выдержки ВИУ",
            typeFormatRaw = TypeFormatTestItemField.DOUBLE.toString(),
            minValue = "0",
            value = hvTestTime.formatPoint(),
            unit = "с",
            blockName = "Параметры испытаний"
        ),
        TestItemFieldScheme(
            key = "meggerVoltage",
            title = "Напряжение меггер",
            typeFormatRaw = TypeFormatTestItemField.DOUBLE.toString(),
            minValue = "500",
            value = meggerVoltage.formatPoint(),
            maxValue = "2500",
            unit = "В",
            blockName = "Параметры испытаний"
        ),
        TestItemFieldScheme(
            key = "idleTestTime",
            title = "Время выдержки ХХ",
            typeFormatRaw = TypeFormatTestItemField.DOUBLE.toString(),
            minValue = "0",
            value = idleTestTime.formatPoint(),
            unit = "с",
            blockName = "Параметры испытаний"
        ),
        TestItemFieldScheme(
            key = "runoutTestTime",
            title = "Время выдержки обкатки",
            typeFormatRaw = TypeFormatTestItemField.DOUBLE.toString(),
            minValue = "0",
            value = runoutTestTime.formatPoint(),
            unit = "с",
            blockName = "Параметры испытаний"
        ),
        TestItemFieldScheme(
            key = "meggerSVoltage",
            title = "Напряжение меггер",
            typeFormatRaw = TypeFormatTestItemField.DOUBLE.toString(),
            minValue = "500",
            value = meggerSVoltage.formatPoint(),
            maxValue = "2500",
            unit = "В",
            blockName = "Параметры испытаний"
        ),
        TestItemFieldScheme(
            key = "hvSVoltage",
            title = "Напряжение ВИУ",
            typeFormatRaw = TypeFormatTestItemField.DOUBLE.toString(),
            minValue = "1000",
            value = hvSVoltage.formatPoint(),
            maxValue = "3000",
            unit = "V",
            blockName = "Параметры испытаний"
        ),
        TestItemFieldScheme(
            key = "hvSCurrent",
            title = "Ток утечки ВИУ",
            typeFormatRaw = TypeFormatTestItemField.DOUBLE.toString(),
            minValue = "0",
            value = hvSCurrent.formatPoint(),
            maxValue = "100",
            unit = "mA",
            blockName = "Параметры испытаний"
        ),
        TestItemFieldScheme(
            key = "hvSTestTime",
            title = "Время выдержки ВИУ",
            typeFormatRaw = TypeFormatTestItemField.DOUBLE.toString(),
            minValue = "0",
            value = hvSTestTime.formatPoint(),
            unit = "с",
            blockName = "Параметры испытаний"
        ),
    )

    override fun toString() = name
}
