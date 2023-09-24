package ru.avem.stand.tests

import ru.avem.library.polling.IDeviceController
import ru.avem.stand.io.DevicePoller
import ru.avem.stand.io.DevicePoller.DD2
import ru.avem.stand.io.DevicePoller.UZ91
import ru.avem.stand.io.optimusdrive.ad800.AD800
import ru.avem.stand.ms
import ru.avem.stand.tests.model.Field
import ru.avem.stand.tests.model.TestRow
import ru.avem.stand.view.composables.MotorType
import ru.avem.stand.view.composables.table.TableScheme
import ru.avem.stand.view.composables.table.tables
import kotlin.concurrent.thread
import kotlin.experimental.and

abstract class KSPADTest(
    abbr: String,
    tag: String,
    name: String,
    tables: List<TableScheme<TestRow>> = tables(),
    linkedTests: List<Test> = listOf()
) : Test(abbr, tag, name, tables, linkedTests) {
    private var buttonPostStartPressed = false

    open val availableMotors: List<MotorType>
        get() = MotorType.all

    protected open val checkedDevices: List<IDeviceController> = listOf()
    protected open val alertMessages: List<String> = listOf()

    override fun init() {
        super.init()
        initVariables()
        initDevices()
        initButtonPost()
        assemblyCircuit()
    }

    protected open fun initVariables() {
        if (isRunning) state = "Инициализация переменных"
        if (isRunning) initVariablesBlock()
    }

    private var isFirstCheck = true

    protected open fun initDevices() {
        if (isRunning) state = "Инициализация устройств"
        if (isRunning) initDD2()
        if (isRunning) initDevicesBlock()
        if (isRunning) {
            isFirstCheck = true
            thread {
                while (isRunning) {
                    checkedDevices.forEach { it.checkResponsibilityAndNotify() }
                    isFirstCheck = false
                    wait(1)
                }
            }
        }
        if (isRunning) fields.forEach(Field::poll)
    }

    protected fun loadUZ91() {
        if (isRunning) state = "Ожидание загрузки частотного преобразователя UZ91..."
        while (isRunning) {
            wait(.1)

            val uz91Responding: Boolean

            with(UZ91) {
                checkResponsibility()
                uz91Responding = isResponding
            }

            if (!uz91Responding) break
        }
        if (isRunning) DD2.onUZ91()
        val t1 = ms()
        while (isRunning) {
            wait(.1)

            val uz91Responding: Boolean

            with(UZ91) {
                checkResponsibility()
                uz91Responding = isResponding
            }

            if (uz91Responding) break
        }
        val t2 = ms()
        wait((10 - (t2 - t1) / 1000.0).coerceAtLeast(.1))
        if (isRunning) state = "Частотный преобразователь UZ91 загружен..."
    }

    private fun initDD2() {
        buttonPostStartPressed = false
        with(DD2) {
            checkResponsibilityAndNotify()
            offAllKMs()
            init()
            DevicePoller.addWritingRegister(name, model.CMD, 1.toShort())
            DevicePoller.startPoll(name, model.STATE) {}

            DevicePoller.startPoll(name, model.DI_01_16_TRIG) { value ->
                buttonPostStartPressed = value.toShort() and 0b1 != 0.toShort() //1

                val isStopPressed = value.toShort() and 0b10 != 0.toShort() //2
                if (isStopPressed) cause = "Нажали кнопку СТОП на кнопочном посту"
            }

            DevicePoller.startPoll(name, model.DI_01_16_TRIG_INV) { value ->
                if (value.toShort() and 0b100 != 0.toShort()) cause = "Сработала защита: Токовая защита ВИУ" //3
                if (value.toShort() and 0b10000 != 0.toShort()) cause = "Сработала защита: Токовая защита ОИ" //5
//                if (value.toShort() and   0b100000 != 0.toShort()) cause = "Сработала защита: Тепловая защита дросселей" //6
                if (value.toShort() and 0b1000000 != 0.toShort()) cause = "Сработала защита: Концевик двери ШСО" //7
                if (value.toShort() and 0b10000000 != 0.toShort()) cause = "Сработала защита: Концевик двери Зоны" //8
            }
        }
    }

    private fun IDeviceController.checkResponsibilityAndNotify() {
        if (isRunning) {
            if (isFirstCheck) {
                state = "Инициализация $name..."
            }
            with(this) {
                checkResponsibility()
                if (!isResponding) cause = "Прибор $name не отвечает"
            }
        }
    }

    private fun initButtonPost() {
        if (isRunning) state = "Инициализация кнопочного поста"
        if (isRunning) alertMessages.forEach { showCommandMessage("Проделайте и нажмите ОК:|$it") }
        if (isRunning) showAlertNotification("Нажмите кнопку Пуск на КП для запуска испытания")
        if (isRunning) DD2.signalize()
    }

    private fun showCommandMessage(message: String) {
        model.commandMessage.value = message

        while (isRunning) {
            wait(.1)
            if (!model.isCommandMessageDialogVisible) break
        }
    }

    private fun showAlertNotification(message: String) {
        DD2.resetTriggers()
        isNeedCheckingErrors = false
        wait(3)
        if (isRunning) model.alertNotification.value = message

        while (isRunning) {
            wait(.1)

            if (buttonPostStartPressed) {
                model.alertNotification.value = ""
                wait(3)
                DD2.resetTriggers()
                break
            }
        }
        isNeedCheckingErrors = true
    }

    protected open fun assemblyCircuit() {
        if (isRunning) state = "Сбор электрической схемы"
        if (isRunning) assemblyCircuitBlock()
    }

    override fun process() {
        super.process()
        if (isRunning) processBlock()
    }

    protected fun regulation(
        influence: Field,
        needValue: Number,
        deltaMin: Number = 0,
        deltaMax: Number = deltaMin,
        influenceStep: Number,
        waitSec: Number,
        temporize: Int = 4,
        isFinite: Boolean = true,
        isReverse: Boolean = false,
        isNeedContinue: () -> Boolean = { isRunning },
        block: () -> Unit = { },
        respondent: () -> Number
    ): Boolean {
        val minValue = needValue.toDouble() - deltaMin.toDouble() / 100.0 * needValue.toDouble()
        val maxValue = needValue.toDouble() + deltaMax.toDouble() / 100.0 * needValue.toDouble()
        var attempts = temporize
        while (isNeedContinue()) {
            val response = respondent().toDouble()

            if (response in minValue..maxValue) {
                if (isFinite && attempts-- == 0) return true
            } else {
                attempts = temporize

                var step = influenceStep.toDouble()
                if (response > maxValue) step = -step
                if (isReverse) step = -step

                val oldInfluenceValue = influence.value
                influence.value = influence.d + step

                if (influence.value == oldInfluenceValue) return false
            }

            wait(waitSec, isNeedContinue = isNeedContinue)
            block()
        }
        return true
    }

    fun selectCurrentStage(current: () -> Double, ratio: (Double) -> Unit) {
        if (current() < 60.0) {
            DD2.on80Stage()
            DD2.offMaxStage()
            ratio(80.0 / 5.0)
            wait(3)
            if (current() < 16.0) {
                DD2.on20Stage()
                DD2.off80Stage()
                ratio(20.0 / 5.0)
                wait(3)
                if (current() < 4.0) {
                    DD2.onMinStage()
                    DD2.off20Stage()
                    ratio(5.0 / 5.0)
                    wait(3)
                }
            }
        }
    }

    fun toMaxCurrentStage(ratio: (Double) -> Unit) {
        DD2.onMaxStage()
        DD2.off80Stage()
        DD2.off20Stage()
        DD2.offMinStage()
        ratio(300.0 / 5.0)
    }

    override fun finish() {
        finishBlock()
        model.alertNotification.value = ""

        state = "Разбор электрической схемы"
        DD2.offAllKMs()

        DevicePoller.clearPollingRegisters()
        DevicePoller.clearWritingRegisters()

        super.finish()
    }

    protected fun AD800.offFreewheeling(out: Field) {
        stopObjectFreewheeling()
        regulation(out, out.min, influenceStep = 10, waitSec = 0, isNeedContinue = { true }) { out.d }
    }

    protected fun AD800.off(out: Field) {
        regulation(out, out.min, influenceStep = .5, waitSec = .05, isNeedContinue = { true }) { out.d }
        stopObjectFreewheeling()
    }
}
