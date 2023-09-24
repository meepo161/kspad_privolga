package ru.avem.stand.io.avem.avem9

import ru.avem.kserialpooler.adapters.modbusrtu.ModbusRTUAdapter
import ru.avem.kserialpooler.adapters.utils.ModbusRegister
import ru.avem.kserialpooler.utils.TransportException
import ru.avem.library.polling.DeviceController
import ru.avem.library.polling.DeviceRegister
import java.nio.ByteBuffer

class AVEM9(
    override val name: String,
    override val protocolAdapter: ModbusRTUAdapter,
    override val id: Byte
) : DeviceController() {
    val model = AVEM9Model()
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

    override fun writeRequest(request: String) {}

    override fun checkResponsibility() {
        readRegister(model.registers.values.first())
    }

    override fun getRegisterById(idRegister: String) = model.getRegisterById(idRegister)

    fun startMeasurement(mode: AVEM9Model.MeasurementMode, voltage: AVEM9Model.SpecifiedVoltage) {
        if (mode == AVEM9Model.MeasurementMode.Empty || voltage == AVEM9Model.SpecifiedVoltage.Empty) error("")
        writeRegister(getRegisterById(model.CFG_SCHEME), mode.scheme)
        writeRegister(getRegisterById(model.VOLTAGE_SCHEME), voltage.scheme)
        writeRegister(getRegisterById(model.START_STOP), 1.toShort())
    }

    fun stopTest() {
        writeRegister(getRegisterById(model.START_STOP), 0.toShort())
    }

    fun dischargeVoltage() {
        writeRegister(getRegisterById(model.CFG_SCHEME), 1.toShort())
        writeRegister(getRegisterById(model.START_STOP), 1.toShort())
    }

    private fun <T> List<T>.second(): T {
        if (size < 2) throw NoSuchElementException("List invalid size.")
        return this[1]
    }
}
