package ru.avem.stand.tests

import androidx.compose.runtime.MutableState
import ru.avem.stand.ms
import ru.avem.stand.protocol.ProtocolManager
import ru.avem.stand.tests.model.Field
import ru.avem.stand.tests.model.TestModel
import ru.avem.stand.tests.model.TestRow
import ru.avem.stand.view.composables.table.TableScheme
import ru.avem.stand.view.composables.table.tables
import java.lang.Thread.sleep
import java.text.SimpleDateFormat
import kotlin.concurrent.thread

abstract class Test(
    val abbr: String,
    val tag: String,
    val name: String,
    val tables: List<TableScheme<TestRow>> = tables(),
    val linkedTests: List<Test> = listOf()
) {
    val model = TestModel()

    val tablesData: List<MutableList<TestRow>> = tables.map {
        mutableListOf(TestRow())
    }

    val table1 = try {
        tablesData[0]
    } catch (_: Exception) {
        mutableListOf(TestRow())
    }
    val table2 = try {
        tablesData[1]
    } catch (_: Exception) {
        mutableListOf(TestRow())
    }
    val table3 = try {
        tablesData[2]
    } catch (_: Exception) {
        mutableListOf(TestRow())
    }
    val table4 = try {
        tablesData[3]
    } catch (_: Exception) {
        mutableListOf(TestRow())
    }
    val table5 = try {
        tablesData[4]
    } catch (_: Exception) {
        mutableListOf(TestRow())
    }
    val table6 = try {
        tablesData[5]
    } catch (_: Exception) {
        mutableListOf(TestRow())
    }
    val table7 = try {
        tablesData[6]
    } catch (_: Exception) {
        mutableListOf(TestRow())
    }
    val table8 = try {
        tablesData[7]
    } catch (_: Exception) {
        mutableListOf(TestRow())
    }
    val table9 = try {
        tablesData[8]
    } catch (_: Exception) {
        mutableListOf(TestRow())
    }
    val table10 = try {
        tablesData[9]
    } catch (_: Exception) {
        mutableListOf(TestRow())
    }

    val table1r1 = try {
        tablesData[0][0]
    } catch (_: Exception) {
        TestRow()
    }
    val table2r1 = try {
        tablesData[1][0]
    } catch (_: Exception) {
        TestRow()
    }
    val table3r1 = try {
        tablesData[2][0]
    } catch (_: Exception) {
        TestRow()
    }
    val table4r1 = try {
        tablesData[3][0]
    } catch (_: Exception) {
        TestRow()
    }
    val table5r1 = try {
        tablesData[4][0]
    } catch (_: Exception) {
        TestRow()
    }
    val table6r1 = try {
        tablesData[5][0]
    } catch (_: Exception) {
        TestRow()
    }
    val table7r1 = try {
        tablesData[6][0]
    } catch (_: Exception) {
        TestRow()
    }
    val table8r1 = try {
        tablesData[7][0]
    } catch (_: Exception) {
        TestRow()
    }
    val table9r1 = try {
        tablesData[8][0]
    } catch (_: Exception) {
        TestRow()
    }
    val table10r1 = try {
        tablesData[9][0]
    } catch (_: Exception) {
        TestRow()
    }

    val isRunning: Boolean
        get() = model.isRunning.value

    protected open val fields = listOf<Field>()

    var state: String = ""
        set(value) {
            field = value
            model.addMessageToLog(value)
            stateCell.value = value
        }

    protected abstract val stateCell: MutableState<String>

    protected var isNeedCheckingErrors = true

    protected var cause: String = ""
        set(value) {
            if ((isNeedCheckingErrors || !value.contains("не отвечает")) && value.isNotEmpty()) {
                if (!model.isStopping.value) {
                    state = "Прерывание испытания"
                    model.setStoppingState()
                }

                if (!field.contains(value)) field += "${if (field != "") " / " else ""}$value"
            } else {
                field = ""
            }
        }

    var result: String = ""

    open fun onShow() {
        clear()
        result = "Не запускался"
    }

    fun onPass() {
        saveDataForProtocol()
    }

    private fun clear() {
        cause = ""
        state = ""
        for (i in fields.indices) fields[i].reinit()
    }

    fun switchState() {
        if (isRunning) cancel() else if (!model.isStopping.value) start()
    }

    fun cancel() {
        cause = "Отменено оператором"
    }

    private fun start() {
        thread {
            clear()
            init()
            process()
            storeData()
            finish()
            restoreData()
        }
    }

    protected open fun init() {
        model.setRunningState()
        state = "Подготовка к запуску испытания"
    }

    protected open fun process() {
        if (isRunning) state = "Запуск испытания"
    }

    protected open fun finish() {
        state = if (cause.isEmpty()) "Завершено успешно" else cause
        if (cause.isNotEmpty()) model.alertMessage.value = "Испытание завершено:|$cause"
        result = state

        val resultForProtocol = when {
            result.contains("Завершено успешно") -> "Успешно"
            result.contains("Отменено оператором") -> "Отменено"
            else -> "Прервано"
        }

        ProtocolManager.saveField("${tag}Status" to resultForProtocol)
        model.setFinishedState()
    }

    protected open fun storeData() {
        fields.forEach(Field::store)
    }

    protected open fun restoreData() {
        fields.forEach(Field::restore)
    }

    private fun saveDataForProtocol() {
        ProtocolManager.saveField("TEST_NAME" to name)
        ProtocolManager.saveField("${tag}Operator" to "Н / У")

        ProtocolManager.saveField("${tag}Date" to SimpleDateFormat("dd.MM.yyyy").format(System.currentTimeMillis()))
        ProtocolManager.saveField("${tag}Time" to SimpleDateFormat("HH:mm").format(System.currentTimeMillis()))
        fields.filter { it.id.isNotEmpty() && it.s.isNotEmpty() }.forEach {
            ProtocolManager.saveField("${tag}${it.id}" to it.s)
        }
    }

    protected fun wait(
        seconds: Number, step: Long = 10,
        isNeedContinue: () -> Boolean = { isRunning },
        onTick: (Double) -> Unit = {}
    ) {
        val startStamp = ms()
        while (isNeedContinue()) {
            val progress = (ms() - startStamp) / (seconds.toDouble() * 1000)
            if (progress < 1.0) {
                onTick(progress * seconds.toDouble())
                sleep(step)
            } else {
                onTick(seconds.toDouble())
                break
            }
        }
    }

    protected fun waitWithDescription(seconds: Int, description: (Double) -> String) {
        wait(seconds) { state = description(it) }
    }

    override fun toString() = name

    // DSL
    protected var initDevicesBlock: () -> Unit = {}
    protected var initVariablesBlock: () -> Unit = {}
    protected var assemblyCircuitBlock: () -> Unit = {}
    protected var processBlock: () -> Unit = {}
    protected var finishBlock: () -> Unit = {}

    protected fun define(
        initVariables: () -> Unit = {},
        initDevices: () -> Unit = {},
        assemblyCircuit: () -> Unit = {},
        process: () -> Unit = {},
        finish: () -> Unit = {},
    ) {
        initVariablesBlock = initVariables
        initDevicesBlock = initDevices
        assemblyCircuitBlock = assemblyCircuit
        processBlock = process
        finishBlock = finish
    }
}
