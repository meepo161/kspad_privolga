package ru.avem.stand.io.avem.atr

import ru.avem.kserialpooler.adapters.modbusrtu.ModbusRTUAdapter
import ru.avem.kserialpooler.adapters.utils.ModbusRegister
import ru.avem.kserialpooler.utils.TransportException
import ru.avem.kserialpooler.utils.TypeByteOrder
import ru.avem.kserialpooler.utils.allocateOrderedByteBuffer

import ru.avem.library.polling.DeviceRegister
import ru.avem.library.polling.IDeviceController
import java.nio.ByteBuffer
import java.nio.ByteOrder

class ATR(
    override val name: String,
    override val protocolAdapter: ModbusRTUAdapter,
    override val id: Byte
) : IDeviceController {
    override var isResponding = false
    val model = ATRModel()
    override var requestTotalCount = 0
    override var requestSuccessCount = 0
    override val pollingRegisters = mutableListOf<DeviceRegister>()
    override val writingRegisters = mutableListOf<Pair<DeviceRegister, Number>>()

    override fun readRegister(register: DeviceRegister) {
        isResponding = try {
            transactionWithAttempts {
                when (register.valueType) {
                    DeviceRegister.RegisterValueType.SHORT -> {
                        val modbusRegister =
                            protocolAdapter.readInputRegisters(id, register.address, 1).map(ModbusRegister::toShort)
                        register.value = modbusRegister.first().toDouble()
                    }

                    DeviceRegister.RegisterValueType.FLOAT -> {
                        val modbusRegister =
                            protocolAdapter.readInputRegisters(id, register.address, 2).map(ModbusRegister::toShort)
                        register.value =
                            allocateOrderedByteBuffer(
                                modbusRegister,
                                TypeByteOrder.BIG_ENDIAN,
                                4
                            ).float.toDouble()
                    }

                    DeviceRegister.RegisterValueType.INT32 -> {
                        val modbusRegister =
                            protocolAdapter.readInputRegisters(id, register.address, 2).map(ModbusRegister::toShort)
                        register.value =
                            allocateOrderedByteBuffer(
                                modbusRegister,
                                TypeByteOrder.BIG_ENDIAN,
                                4
                            ).int.toDouble()
                    }
                }
            }
            true
        } catch (e: TransportException) {
            false
        }
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
                    val bb = ByteBuffer.allocate(4).putFloat(value).order(ByteOrder.BIG_ENDIAN)
                    val registers = listOf(ModbusRegister(bb.getShort(0)), ModbusRegister(bb.getShort(2)))
                    transactionWithAttempts {
                        protocolAdapter.presetMultipleRegisters(id, register.address, registers)
                    }
                }

                is Int -> {
                    val bb = ByteBuffer.allocate(4).putInt(value).order(ByteOrder.BIG_ENDIAN)
                    val registers = listOf(ModbusRegister(bb.getShort(0)), ModbusRegister(bb.getShort(2)))
                    transactionWithAttempts {
                        protocolAdapter.presetMultipleRegisters(id, register.address, registers)
                    }
                }

                is Short -> {
                    transactionWithAttempts {
                        protocolAdapter.presetSingleRegister(id, register.address, ModbusRegister(value))
                    }
                }

                else -> {
                    throw UnsupportedOperationException("Method can handle only with Float, Int and Short")
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

    override fun writeRequest(request: String) {}

    override fun checkResponsibility() {
        try {
            model.registers.values.firstOrNull()?.let {
                readRegister(it)
            }
        } catch (ignored: TransportException) {
        }
    }

    override fun getRegisterById(idRegister: String) = model.getRegisterById(idRegister)

    fun startUp(voltage: Float, speedPerc: Float) {
        val corridor = 0.01f

        val delta = 0.01f
        val timeMinPulsePercent = 100.0f
        val timeMaxPulsePercent = 100.0f
        val timeMinPeriod = 100f
        val timeMaxPeriod = 100f
        val minVoltage = 0.1f
        try {
            writeRegister(getRegisterById(model.VALUE_REGISTER), (voltage))
            writeRegister(getRegisterById(model.IR_TIME_PERIOD_MIN), (timeMinPulsePercent))
            writeRegister(getRegisterById(model.IR_TIME_PERIOD_MAX), (timeMaxPulsePercent))
            writeRegister(getRegisterById(model.IR_TIME_PULSE_MIN_PERCENT), (timeMinPeriod))
            writeRegister(getRegisterById(model.IR_TIME_PULSE_MAX_PERCENT), (timeMaxPeriod))
            writeRegister(getRegisterById(model.IR_DUTY_MIN_PERCENT), (speedPerc))
            writeRegister(getRegisterById(model.IR_DUTY_MAX_PERCENT), (speedPerc))
            writeRegister(getRegisterById(model.REGULATION_TIME_REGISTER), (300000))
            writeRegister(getRegisterById(model.CORRIDOR_REGISTER), (corridor))
            writeRegister(getRegisterById(model.DELTA_REGISTER), (delta))
            writeRegister(getRegisterById(model.MIN_VOLTAGE_LIMIT_REGISTER), (minVoltage))
            startATR()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun startATR() {
        try {
            writeRegister(getRegisterById(model.START_REGISTER), (1).toShort())
            writeRegister(getRegisterById(model.STOP_REGISTER), (0).toShort())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setSpeedPerc(speedPerc: Float) {
        writeRegister(getRegisterById(model.IR_DUTY_MIN_PERCENT), (speedPerc))
        writeRegister(getRegisterById(model.IR_DUTY_MAX_PERCENT), (speedPerc))
    }

    fun startATRUp() {
        try {
            writeRegister(getRegisterById(model.VALUE_REGISTER), (400f))
            writeRegister(getRegisterById(model.START_REGISTER), (1).toShort())
            writeRegister(getRegisterById(model.STOP_REGISTER), (0).toShort())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun startATRDown() {
        try {
            writeRegister(getRegisterById(model.VALUE_REGISTER), (1f))
            writeRegister(getRegisterById(model.START_REGISTER), (1).toShort())
            writeRegister(getRegisterById(model.STOP_REGISTER), (0).toShort())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun startUpPulse(voltage: Float, timePulsePercent: Float) {
        val corridor = 0.01f
        val delta = 0.01f
        val timeMinPeriod = 20.0f
        val timeMaxPeriod = 20.0f
        val minVoltage = 0.1f
        val minDuttyPercent = 80f
        val maxDuttyPercent = 80f
        try {
            writeRegister(getRegisterById(model.VALUE_REGISTER), (voltage))
            writeRegister(getRegisterById(model.IR_TIME_PERIOD_MIN), (timePulsePercent))
            writeRegister(getRegisterById(model.IR_TIME_PERIOD_MAX), (timePulsePercent))
            writeRegister(getRegisterById(model.IR_TIME_PULSE_MIN_PERCENT), (timeMinPeriod))
            writeRegister(getRegisterById(model.IR_TIME_PULSE_MAX_PERCENT), (timeMaxPeriod))
            writeRegister(getRegisterById(model.IR_DUTY_MIN_PERCENT), (minDuttyPercent))
            writeRegister(getRegisterById(model.IR_DUTY_MAX_PERCENT), (maxDuttyPercent))
            writeRegister(getRegisterById(model.REGULATION_TIME_REGISTER), (300000))
            writeRegister(getRegisterById(model.CORRIDOR_REGISTER), (corridor))
            writeRegister(getRegisterById(model.DELTA_REGISTER), (delta))
            writeRegister(getRegisterById(model.MIN_VOLTAGE_LIMIT_REGISTER), (minVoltage))
            startATR()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun stop() {
        try {
            writeRegister(getRegisterById(model.START_REGISTER), (0).toShort())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun reset() {
        try {
            writeRegister(getRegisterById(model.START_REGISTER), (0x5A5A).toShort())
            writeRegister(getRegisterById(model.STOP_REGISTER), (0x5A5A).toShort())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
