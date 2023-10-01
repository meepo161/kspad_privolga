package ru.avem.stand.io.owen.pr

import ru.avem.kserialpooler.adapters.modbusrtu.ModbusRTUAdapter
import ru.avem.kserialpooler.adapters.utils.ModbusRegister
import ru.avem.kserialpooler.utils.TransportException
import ru.avem.library.polling.DeviceController
import ru.avem.library.polling.DeviceRegister
import java.lang.Thread.sleep
import java.nio.ByteBuffer
import kotlin.experimental.and
import kotlin.experimental.or
import kotlin.math.pow

class PR(
    override val name: String,
    override val protocolAdapter: ModbusRTUAdapter,
    override val id: Byte
) : DeviceController() {
    val model = PRModel()
    override var requestTotalCount = 0
    override var requestSuccessCount = 0
    override val pollingRegisters = mutableListOf<DeviceRegister>()

    override val writingRegisters = mutableListOf<Pair<DeviceRegister, Number>>()

    private var outMask01To16: Short = 0
    private var outMask17To32: Short = 0

    fun init() {
        writeRegister(getRegisterById(model.WD_TIMEOUT), 8000.toShort())

        resetTriggers()

        writeRegister(getRegisterById(model.DO_01_16_ERROR_S1_MASK_0), 0xFFFF.toShort())
        writeRegister(getRegisterById(model.DO_01_16_ERROR_S1_MASK_1), 0x0000.toShort())
        writeRegister(getRegisterById(model.DO_17_32_ERROR_S1_MASK_0), 0xFFFF.toShort())
        writeRegister(getRegisterById(model.DO_17_32_ERROR_S1_MASK_1), 0x0000.toShort())
        writeRegister(getRegisterById(model.DO_ERROR_S1_TIME), 0.toShort())
        writeRegister(getRegisterById(model.DO_01_16_ERROR_S2_MASK_0), 0x0000.toShort())
        writeRegister(getRegisterById(model.DO_01_16_ERROR_S2_MASK_1), 0x0000.toShort())
        writeRegister(getRegisterById(model.DO_17_32_ERROR_S2_MASK_0), 0x0000.toShort())
        writeRegister(getRegisterById(model.DO_17_32_ERROR_S2_MASK_1), 0x0000.toShort())
        writeRegister(getRegisterById(model.DO_ERROR_S2_TIME), 500.toShort())
        writeRegister(getRegisterById(model.DO_01_16_ERROR_S3_MASK_0), 0x0000.toShort())
        writeRegister(getRegisterById(model.DO_01_16_ERROR_S3_MASK_1), 0x0000.toShort())
        writeRegister(getRegisterById(model.DO_17_32_ERROR_S3_MASK_0), 0x0000.toShort())
        writeRegister(getRegisterById(model.DO_17_32_ERROR_S3_MASK_1), 0x0000.toShort())
        writeRegister(getRegisterById(model.DO_ERROR_S3_TIME), 1000.toShort())
        writeRegister(getRegisterById(model.DO_01_16_ERROR_S4_MASK_0), 0x0000.toShort())
        writeRegister(getRegisterById(model.DO_01_16_ERROR_S4_MASK_1), 0x0000.toShort())
        writeRegister(getRegisterById(model.DO_17_32_ERROR_S4_MASK_0), 0x0000.toShort())
        writeRegister(getRegisterById(model.DO_17_32_ERROR_S4_MASK_1), 0x0000.toShort())
        writeRegister(getRegisterById(model.DO_ERROR_S4_TIME), 1500.toShort())

        writeRegister(getRegisterById(model.DI_01_16_ERROR_MASK_1), 0x0002.toShort())
        writeRegister(getRegisterById(model.DI_01_16_ERROR_MASK_0), 0x00D4.toShort())
        writeRegister(getRegisterById(model.DI_17_32_ERROR_MASK_1), 0x0000.toShort())
        writeRegister(getRegisterById(model.DI_17_32_ERROR_MASK_0), 0x0000.toShort())
        writeRegister(getRegisterById(model.DI_33_48_ERROR_MASK_1), 0x0000.toShort())
        writeRegister(getRegisterById(model.DI_33_48_ERROR_MASK_0), 0x0000.toShort())

        writeRegister(getRegisterById(model.CMD), 3.toShort()) // RESET ERROR + WD_CYCLE
    }

    fun resetTriggers() {
        writeRegister(getRegisterById(model.DI_01_16_RST), 0xFFFF.toShort())
        writeRegister(getRegisterById(model.DI_01_16_RST), 0x0000.toShort())
        writeRegister(getRegisterById(model.DI_17_32_RST), 0xFFFF.toShort())
        writeRegister(getRegisterById(model.DI_17_32_RST), 0x0000.toShort())
        writeRegister(getRegisterById(model.DI_33_48_RST), 0xFFFF.toShort())
        writeRegister(getRegisterById(model.DI_33_48_RST), 0x0000.toShort())
    }

    fun initWithoutProtections() {
        writeRegister(getRegisterById(model.WD_TIMEOUT), 8000.toShort())

        resetTriggers()

        writeRegister(getRegisterById(model.DO_01_16_ERROR_S1_MASK_0), 0xFFFF.toShort())
        writeRegister(getRegisterById(model.DO_01_16_ERROR_S1_MASK_1), 0x0000.toShort())
        writeRegister(getRegisterById(model.DO_17_32_ERROR_S1_MASK_0), 0xFFFF.toShort())
        writeRegister(getRegisterById(model.DO_17_32_ERROR_S1_MASK_1), 0x0000.toShort())

        writeRegister(getRegisterById(model.DI_01_16_ERROR_MASK_1), 0x0000.toShort())
        writeRegister(getRegisterById(model.DI_01_16_ERROR_MASK_0), 0x0000.toShort())
        writeRegister(getRegisterById(model.DI_17_32_ERROR_MASK_1), 0x0000.toShort())
        writeRegister(getRegisterById(model.DI_17_32_ERROR_MASK_0), 0x0000.toShort())
        writeRegister(getRegisterById(model.DI_33_48_ERROR_MASK_1), 0x0000.toShort())
        writeRegister(getRegisterById(model.DI_33_48_ERROR_MASK_0), 0x0000.toShort())

        writeRegister(getRegisterById(model.CMD), 3.toShort()) // RESET ERROR + WD_CYCLE
    }

    override fun readRegister(register: DeviceRegister) {
        isResponding = try {
            transactionWithAttempts {
                when (register.valueType) {
                    DeviceRegister.RegisterValueType.SHORT -> {
                        val modbusRegister =
                            protocolAdapter.readHoldingRegisters(id, register.address, 1).map(ModbusRegister::toShort)
                        register.value = modbusRegister.first()
                    }

                    DeviceRegister.RegisterValueType.FLOAT -> {
                        val modbusRegister =
                            protocolAdapter.readHoldingRegisters(id, register.address, 2).map(ModbusRegister::toShort)
                        register.value =
                            ByteBuffer.allocate(4).putShort(modbusRegister.second()).putShort(modbusRegister.first())
                                .also { it.flip() }.float
                    }

                    else -> throw UnsupportedOperationException("Method can handle only with Float and Short")
                }
            }
            true
        } catch (e: TransportException) {
            false
        }
    }

    private fun <T> List<T>.second(): T {
        if (isEmpty() && size < 2) {
            throw NoSuchElementException("List invalid size.")
        }
        return this[1]
    }

    override fun readAllRegisters() {
        model.registers.values.forEach {
            readRegister(it)
        }
    }

    @Synchronized
    override fun <T : Number> writeRegister(register: DeviceRegister, value: T) {
        isResponding = try {
            when (value) {
                is Float -> {
                    val bb = ByteBuffer.allocate(4).putFloat(value)
                    val registers = listOf(ModbusRegister(bb.getShort(2)), ModbusRegister(bb.getShort(0)))
                    transactionWithAttempts {
                        protocolAdapter.presetMultipleRegisters(id, register.address, registers)
                    }
                }

                is Short -> {
                    transactionWithAttempts {
                        protocolAdapter.presetMultipleRegisters(id, register.address, listOf(ModbusRegister(value)))
                    }
                }

                else -> {
                    throw UnsupportedOperationException("Method can handle only with Float and Short")
                }
            }
            true
        } catch (e: TransportException) {
            false
        }
    }

    override fun writeRegisters(register: DeviceRegister, values: List<Short>) {
        val registers = values.map { ModbusRegister(it) }
        isResponding = try {
            transactionWithAttempts {
                protocolAdapter.presetMultipleRegisters(id, register.address, registers)
            }
            true
        } catch (e: TransportException) {
            false
        }
    }

    override fun checkResponsibility() {
        model.registers.values.firstOrNull()?.let { //TODO заменить на специальный регистр, а не первый попавшийся
            readRegister(it)
        }
    }

    override fun getRegisterById(idRegister: String) = model.getRegisterById(idRegister)

    override fun writeRequest(request: String) {}

    fun onUZ91() {
        onOutput01To16(1)
        sleep(100)
    }

    fun onMGR() {
        onOutput01To16(11) // шунтировать GB30 (резистора в блоке питания)
        sleep(100)
        onOutput01To16(11) // шунтировать GB30 (резистора в блоке питания)
        sleep(100)
        onOutput01To16(15) // разомкнуть заземлитель QV3
        sleep(100)
        onOutput01To16(16) // замкнуть QV2
        sleep(500)
        offOutput01To16(11) // снять сигнал с GB30 (работа с резистором т.к. удержание ВВ переключателя необходимо напряжение ниже 5 В)
        sleep(100)
    }

    fun onHV() {
        onOutput01To16(11) // шунтировать GB30 (резистора в блоке питания)
        sleep(100)
        onOutput01To16(15) // разомкнуть заземлитель QV3
        sleep(100)
        onOutput01To16(9) // замкнуть QV1
        sleep(500)
        offOutput01To16(11) // снять сигнал с GB30 (работа с резистором т.к. удержание ВВ переключателя необходимо напряжение ниже 5 В)
        sleep(100)
    }

    fun onIdling() {
        onMaxStage()
        onOutput01To16(12) // Напрям с ЧП
        sleep(100)
        onOutput01To16(6) // Измерение напряжения
        sleep(100)
    }

    fun switchKTR() {
        offOutput01To16(6) // Измерение напряжения
        sleep(100)
        onOutput17To32(5) // Ктр
        sleep(100)
    }

    fun onShortCircuit() {
        onMaxStage()
        onOutput01To16(5) // Первичка ВД
        sleep(100)
        onOutput01To16(2) // ВД в КЗ
        sleep(100)
        onOutput01To16(6) // Измерение напряжения
        sleep(100)
    }

    fun onTTT() {
        onMaxStage()
        onOutput01To16(5) // Первичка ВД
        sleep(100)
        onOutput01To16(13) // ВД к МВ
        sleep(100)
        onOutput01To16(6) // Измерение напряжения
        sleep(100)
    }

    fun onMaxStage() {
        onOutput01To16(3)
        sleep(100)
    }

    fun offMaxStage() {
        offOutput01To16(3)
        sleep(100)
    }

    fun on80Stage() {
        onOutput01To16(10)
        sleep(100)
    }

    fun off80Stage() {
        offOutput01To16(10)
        sleep(100)
    }

    fun on20Stage() {
        onOutput01To16(14)
        sleep(100)
    }

    fun off20Stage() {
        offOutput01To16(14)
        sleep(100)
    }

    fun onMinStage() {
        onOutput01To16(4)
        sleep(100)
    }

    fun offMinStage() {
        offOutput01To16(4)
        sleep(100)
    }

    fun signalize() {
        onOutput01To16(7)
        onOutput01To16(8)
        sleep(100)
    }

    fun light() {
        onOutput01To16(7)
    }

    fun onIKASa() {
        onOutput17To32(1)
        sleep(100)
    }

    fun offIKASa() {
        offOutput17To32(1)
        sleep(100)
    }

    fun onIKASbc() {
        onOutput17To32(2)
        sleep(100)
    }

    fun offIKASbc() {
        offOutput17To32(2)
        sleep(100)
    }

    fun onIKASba() {
        onOutput17To32(3)
        sleep(100)
    }

    fun offIKASba() {
        offOutput17To32(3)
        sleep(100)
    }

    fun onIKASc() {
        onOutput17To32(4)
        sleep(100)
    }

    fun offIKASc() {
        offOutput17To32(4)
        sleep(100)
    }

    fun onMGRDevice() {
        onOutput17To32(7)
        sleep(100)
    }

    fun offMGRDevice() {
        offOutput17To32(7)
        sleep(100)
    }

    fun offAllKMs() {
        outMask01To16 = 0
        outMask17To32 = 0
        writeRegister(getRegisterById(model.DO_01_16), outMask01To16)
        writeRegister(getRegisterById(model.DO_17_32), outMask17To32)
    }

    private fun onOutput01To16(position: Short) {
        val bitPosition = position - 1
        outMask01To16 = outMask01To16 or 2.0.pow(bitPosition).toInt().toShort()
        writeRegister(getRegisterById(model.DO_01_16), outMask01To16)
    }

    private fun offOutput01To16(position: Short) {
        val bitPosition = position - 1
        outMask01To16 = outMask01To16 and 2.0.pow(bitPosition).toInt().inv().toShort()
        writeRegister(getRegisterById(model.DO_01_16), outMask01To16)
    }

    private fun onOutput17To32(position: Short) {
        val bitPosition = position - 1
        outMask17To32 = outMask17To32 or 2.0.pow(bitPosition).toInt().toShort()
        writeRegister(getRegisterById(model.DO_17_32), outMask17To32)
    }

    private fun offOutput17To32(position: Short) {
        val bitPosition = position - 1
        outMask17To32 = outMask17To32 and 2.0.pow(bitPosition).toInt().inv().toShort()
        writeRegister(getRegisterById(model.DO_17_32), outMask17To32)
    }

    fun setBlinkPeriod(blinkPeriod: Short) {
        writeRegister(getRegisterById(model.BLINK_PERIOD), blinkPeriod)
    }
}
