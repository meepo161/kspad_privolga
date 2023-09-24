package ru.avem.stand.view.composables

import androidx.compose.runtime.*
import ru.avem.stand.formatPoint
import ru.avem.stand.testitem.TestItemLocal
import ru.avem.stand.view.screens.main.MainViewModel

enum class MotorType(val text: String) {
    A("АД"),
    AWR("АД с ФР"),
    ATS("АД с термодатчиками"),
    AWRTS("АД с ФР и термодатчиками");

    override fun toString() = text

    companion object {
        val all = listOf(A, AWR, ATS, AWRTS)
        val withSensors = listOf(ATS, AWRTS)
        val wrs = listOf(AWR, AWRTS)

        fun valueOfText(text: String) = all.first { it.text == text }
    }
}

enum class SchemeType(private val glyph: String) {
    TRIANGLE("△"),
    STAR("Y");

    override fun toString() = glyph

    companion object {
        val all = listOf(TRIANGLE.glyph, STAR.glyph)
    }
}

@Composable
fun AddTestItemDialog(
    ti: TestItemLocal?,
    isVisible: MutableState<Boolean>,
    vm: MainViewModel,
    onAdd: (TestItemLocal) -> Unit
) {
    val nominalParameters =
        DialogRowData(
            name = "Номинальные параметры объекта испытания",
            type = DialogRowType.LABEL,
        )
    val motor by remember {
        mutableStateOf(
            DialogRowData(
                name = "Тип",
                type = DialogRowType.COMBO,
                variableField = MotorType.all.map { it.text },
                field = mutableStateOf(ti?.motor ?: "")
            )
        )
    }
    val name by remember {
        mutableStateOf(
            DialogRowData(
                name = "Название",
                field = mutableStateOf(ti?.name ?: "")
            )
        )
    }
    val nominalPower by remember {
        mutableStateOf(
            DialogRowData(
                name = "Мощность ОИ, кВт (0 - 100)",
                field = mutableStateOf(ti?.nominalPower?.formatPoint(2) ?: ""),
                type = DialogRowType.NUMBERIC,
                minValue = 0.0,
                maxValue = 100.0
            )
        )
    }
    val nominalVoltage by remember {
        mutableStateOf(
            DialogRowData(
                name = "Напряжение ОИ, В",
                type = DialogRowType.COMBO,
                variableField = listOf("380"),
                field = mutableStateOf(ti?.nominalVoltage?.formatPoint() ?: "380")
            )
        )
    }
    val frequency by remember {
        mutableStateOf(
            DialogRowData(
                name = "Частота, Гц",
                type = DialogRowType.COMBO,
                variableField = listOf("50"),
                field = mutableStateOf(ti?.frequency?.formatPoint() ?: "50")
            )
        )
    }
    val nominalCurrent by remember {
        mutableStateOf(
            DialogRowData(
                name = "Ток ОИ, А (0.1 - 250)",
                field = mutableStateOf(ti?.nominalCurrent?.formatPoint(2) ?: ""),
                type = DialogRowType.NUMBERIC,
                minValue = 0.1,
                maxValue = 250.0
            )
        )
    }
    val nominalN by remember {
        mutableStateOf(
            DialogRowData(
                name = "Скорость вращения, об/мин (1 - 3000)",
                field = mutableStateOf(ti?.nominalN?.formatPoint() ?: ""),
                type = DialogRowType.NUMBERIC,
                minValue = 1.0,
                maxValue = 3000.0
            )
        )
    }
    val nominalCos by remember {
        mutableStateOf(
            DialogRowData(
                name = "Коэффициент мощности, о.е. (0.1 - 1)",
                field = mutableStateOf(ti?.nominalCos?.formatPoint(2) ?: ""),
                type = DialogRowType.NUMBERIC,
                minValue = 0.1,
                maxValue = 1.0
            )
        )
    }

    val scheme by remember {
        mutableStateOf(
            DialogRowData(
                name = "Схема статора",
                type = DialogRowType.COMBO,
                variableField = SchemeType.all,
                field = mutableStateOf(ti?.scheme ?: "")
            )
        )
    }

    val meggerParameters =
        DialogRowData(
            name = "Параметры опыта \"Измерение сопротивления изоляции обмотки статора\"",
            type = DialogRowType.LABEL,
        )
    val meggerVoltage by remember {
        mutableStateOf(
            DialogRowData(
                name = "Напряжение мегаомметра, В",
                type = DialogRowType.COMBO,
                variableField = listOf("500", "1000", "2500"),
                field = mutableStateOf(ti?.meggerVoltage?.formatPoint() ?: "500"),
            )
        )
    }

    val hvParameters =
        DialogRowData(
            name = "Параметры опыта \"Испытание изоляции обмотки статора\" ",
            type = DialogRowType.LABEL,
        )
    val hvVoltage by remember {
        mutableStateOf(
            DialogRowData(
                name = "Напряжение высоковольтное, В (1000 - 3000)",
                field = mutableStateOf(ti?.hvVoltage?.formatPoint() ?: ""),
                type = DialogRowType.NUMBERIC,
                minValue = 1000.0,
                maxValue = 3000.0
            )
        )
    }
    val hvCurrent by remember {
        mutableStateOf(
            DialogRowData(
                name = "Ток утечки, мА (20 - 100)",
                field = mutableStateOf(ti?.hvCurrent?.formatPoint() ?: ""),
                type = DialogRowType.NUMBERIC,
                minValue = 20.0,
                maxValue = 100.0
            )
        )
    }
    val hvTestTime by remember {
        mutableStateOf(
            DialogRowData(
                name = "Время выдержки напряжения, сек (0 - 90)",
                field = mutableStateOf(ti?.hvTestTime?.formatPoint() ?: ""),
                type = DialogRowType.NUMBERIC,
                minValue = 0.0,
                maxValue = 90.0,
            )
        )
    }

    val idleParameters =
        DialogRowData(
            name = "Параметры опыта \"Определение тока и потерь холостого хода\" ",
            type = DialogRowType.LABEL,
        )
    val idleTestTime by remember {
        mutableStateOf(
            DialogRowData(
                name = "Время выдержки напряжения, сек (0 - 600)",
                field = mutableStateOf(ti?.idleTestTime?.formatPoint() ?: ""),
                type = DialogRowType.NUMBERIC,
                minValue = 0.0,
                maxValue = 600.0,
            )
        )
    }
    val runoutParameters =
        DialogRowData(
            name = "Параметры опыта \"Обкатка на холостом ходу\" ",
            type = DialogRowType.LABEL,
        )
    val runoutTestTime by remember {
        mutableStateOf(
            DialogRowData(
                name = "Время выдержки напряжения, сек (0 - 3600)",
                field = mutableStateOf(ti?.runoutTestTime?.formatPoint() ?: ""),
                type = DialogRowType.NUMBERIC,
                minValue = 0.0,
                maxValue = 3600.0,
            )
        )
    }

    val meggerSParameters =
        DialogRowData(
            name = "Параметры опыта \"Измерение сопротивления изоляции термодатчика\"",
            type = DialogRowType.LABEL,
        )
    val meggerSVoltage by remember {
        mutableStateOf(
            DialogRowData(
                name = "Напряжение мегаомметра, В",
                type = DialogRowType.COMBO,
                variableField = listOf("500", "1000", "2500"),
                field = mutableStateOf(ti?.meggerSVoltage?.formatPoint() ?: "500"),
            )
        )
    }

    val hvSParameters =
        DialogRowData(
            name = "Параметры опыта \"Испытание изоляции термодатчика\" ",
            type = DialogRowType.LABEL,
        )
    val hvSVoltage by remember {
        mutableStateOf(
            DialogRowData(
                name = "Напряжение высоковольтное, В (1000 - 3000)",
                field = mutableStateOf(ti?.hvSVoltage?.formatPoint() ?: ""),
                type = DialogRowType.NUMBERIC,
                minValue = 1000.0,
                maxValue = 3000.0
            )
        )
    }
    val hvSCurrent by remember {
        mutableStateOf(
            DialogRowData(
                name = "Ток утечки, мА (20 - 100)",
                field = mutableStateOf(ti?.hvSCurrent?.formatPoint() ?: ""),
                type = DialogRowType.NUMBERIC,
                minValue = 20.0,
                maxValue = 100.0
            )
        )
    }
    val hvSTestTime by remember {
        mutableStateOf(
            DialogRowData(
                name = "Время выдержки напряжения, сек (0 - 90)",
                field = mutableStateOf(ti?.hvSTestTime?.formatPoint() ?: ""),
                type = DialogRowType.NUMBERIC,
                minValue = 0.0,
                maxValue = 90.0,
            )
        )
    }

    val fields = listOf(
        nominalParameters,
        motor,
        name,
        nominalPower,
        nominalVoltage,
        nominalCurrent,
        nominalN,
        nominalCos,
        scheme,
        frequency,

        hvParameters,
        hvVoltage,
        hvCurrent,
        hvTestTime,

        meggerParameters,
        meggerVoltage,

        idleParameters,
        idleTestTime,

        runoutParameters,
        runoutTestTime,

        meggerSParameters,
        meggerSVoltage,

        hvSParameters,
        hvSVoltage,
        hvSCurrent,
        hvSTestTime,
    )

    fun checkFields(): Boolean {
        var isCorrect = true
        fields.forEach {
            if (it.type == DialogRowType.NUMBERIC) {
                it.errorState.value = it.field.value.isEmpty()
                        || it.field.value.toDoubleOrNull() == null
                        || it.field.value.toDouble() < it.minValue
                        || it.field.value.toDouble() > it.maxValue
            } else if (it.type == DialogRowType.COMBO || it.type == DialogRowType.TEXT) {
                it.errorState.value = it.field.value.isEmpty()
            }
            isCorrect = isCorrect && !it.errorState.value
        }
        return isCorrect
    }

    RowsAlertDialog(
        title = "Заполните поля:",
        rows = fields,
        isDialogVisible = isVisible,
        buttonText = if (ti == null) "Создать" else "Изменить",
        checkCreateProjectErrors = ::checkFields
    ) {
        if (checkFields()) {
            onAdd(
                TestItemLocal(
                    0,
                    name.field.value,
                    nominalPower.field.value.toDouble(),
                    nominalVoltage.field.value.toDouble(),
                    nominalCurrent.field.value.toDouble(),
                    nominalN.field.value.toDouble(),
                    nominalCos.field.value.toDouble(),
                    motor.field.value,
                    scheme.field.value,
                    frequency.field.value.toDouble(),
                    hvVoltage.field.value.toDouble(),
                    hvCurrent.field.value.toDouble(),
                    meggerVoltage.field.value.toDouble(),
                    idleTestTime.field.value.toDouble(),
                    runoutTestTime.field.value.toDouble(),
                    hvTestTime.field.value.toDouble(),
                    meggerSVoltage.field.value.toDouble(),
                    hvSVoltage.field.value.toDouble(),
                    hvSCurrent.field.value.toDouble(),
                    hvSTestTime.field.value.toDouble(),
                )
            )
        } else {
            vm.isCheckFieldsDialogVisible.value = true

        }
    }
    checkFields()
}
