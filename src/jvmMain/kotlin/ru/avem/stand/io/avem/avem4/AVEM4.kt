package ru.avem.stand.io.avem.avem4

import ru.avem.kserialpooler.adapters.modbusrtu.ModbusRTUAdapter
import ru.avem.kserialpooler.adapters.utils.ModbusRegister
import ru.avem.kserialpooler.utils.TransportException
import ru.avem.library.polling.DeviceController
import ru.avem.library.polling.DeviceRegister
import java.nio.ByteBuffer

class AVEM4(
    override val name: String,
    override val protocolAdapter: ModbusRTUAdapter,
    override val id: Byte
) : DeviceController() {
    val model = AVEM4Model()
    override var requestTotalCount = 0
    override var requestSuccessCount = 0
    override val pollingRegisters = mutableListOf<DeviceRegister>()

    override val writingRegisters = mutableListOf<Pair<DeviceRegister, Number>>()

    override fun readRegister(register: DeviceRegister) {
        isResponding = try {
            transactionWithAttempts {
                register.value = when (register.valueType) {
                    DeviceRegister.RegisterValueType.SHORT -> {
                        protocolAdapter.readInputRegisters(id, register.address, 1).map(ModbusRegister::toShort).first()
                    }

                    DeviceRegister.RegisterValueType.FLOAT -> {
                        val modbusRegister =
                            protocolAdapter.readInputRegisters(id, register.address, 2).map(ModbusRegister::toShort)
                        ByteBuffer.allocate(4).putShort(modbusRegister.first()).putShort(modbusRegister.second())
                            .also { it.flip() }.float
                    }

                    DeviceRegister.RegisterValueType.INT32 -> {
                        val modbusRegister =
                            protocolAdapter.readInputRegisters(id, register.address, 2).map(ModbusRegister::toShort)
                        ByteBuffer.allocate(4).putShort(modbusRegister.first()).putShort(modbusRegister.second())
                            .also { it.flip() }.int
                    }
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

    private fun readRegister(register: DeviceRegister, offset: Short) {
        isResponding = try {
            transactionWithAttempts {
                register.value = when (register.valueType) {
                    DeviceRegister.RegisterValueType.SHORT -> {
                        protocolAdapter.readInputRegisters(id, (register.address + offset).toShort(), 1)
                            .map(ModbusRegister::toShort).first()
                    }

                    DeviceRegister.RegisterValueType.FLOAT -> {
                        val modbusRegister =
                            protocolAdapter.readInputRegisters(id, (register.address + offset).toShort(), 2)
                                .map(ModbusRegister::toShort)
                        ByteBuffer.allocate(4).putShort(modbusRegister.first()).putShort(modbusRegister.second())
                            .also { it.flip() }.float
                    }

                    DeviceRegister.RegisterValueType.INT32 -> {
                        val modbusRegister =
                            protocolAdapter.readInputRegisters(id, (register.address + offset).toShort(), 2)
                                .map(ModbusRegister::toShort)
                        ByteBuffer.allocate(4).putShort(modbusRegister.first()).putShort(modbusRegister.second())
                            .also { it.flip() }.int
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
                    val bb = ByteBuffer.allocate(4).putFloat(value).also { it.flip() }
                    val registers = listOf(ModbusRegister(bb.short), ModbusRegister(bb.short))
                    transactionWithAttempts {
                        protocolAdapter.presetMultipleRegisters(id, register.address, registers)
                    }
                }

                is Int -> {
                    val bb = ByteBuffer.allocate(4).putInt(value).also { it.flip() }
                    val registers = listOf(ModbusRegister(bb.short), ModbusRegister(bb.short))
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

    override fun checkResponsibility() {
        model.registers.values.firstOrNull()?.let {
            readRegister(it)
        }
    }

    override fun getRegisterById(idRegister: String) = model.getRegisterById(idRegister)

    override fun writeRequest(request: String) {}

    fun getTRMS(): Double {
        readRegister(model.getRegisterById(model.U_TRMS))

        return model.getRegisterById(model.U_TRMS).value.toDouble()
    }

    fun setTriggerValue(value: Float) {
        writeRegister(model.getRegisterById(model.TRIGGER_VALUE), value)
    }

    fun setDownTriggerMode() {
        writeRegister(model.getRegisterById(model.TRIGGER_MODE), 0.toShort())
    }

    fun setUpTriggerMode() {
        writeRegister(model.getRegisterById(model.TRIGGER_MODE), 1.toShort())
    }

    fun setChartTime(time: Int) {
        writeRegister(model.getRegisterById(model.SET_CHART_TIME), time)
    }

    fun startCharting() {
        writeRegister(model.getRegisterById(model.START_CHART), 1.toShort())
    }

    fun stopCharting() {
        writeRegister(model.getRegisterById(model.START_CHART), 0.toShort())
    }

    fun getStateChart(): Short {
        readRegister(model.getRegisterById(model.STATE_CHART))

        return model.getRegisterById(model.STATE_CHART).value.toShort()
    }

    fun getChartPeriod(): Int {
        readRegister(model.getRegisterById(model.GET_CHART_PERIOD))

        return model.getRegisterById(model.GET_CHART_PERIOD).value.toInt()
    }

    fun getChartPoint(offset: Short): Double {
        readRegister(model.getRegisterById(model.CHART_POINTS), offset)

        return model.getRegisterById(model.CHART_POINTS).value.toDouble()
    }
}
