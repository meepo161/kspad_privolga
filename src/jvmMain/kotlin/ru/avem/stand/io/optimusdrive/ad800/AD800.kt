package ru.avem.stand.io.optimusdrive.ad800

import ru.avem.kserialpooler.adapters.modbusrtu.ModbusRTUAdapter
import ru.avem.kserialpooler.adapters.utils.ModbusRegister
import ru.avem.kserialpooler.utils.TransportException
import ru.avem.kserialpooler.utils.TypeByteOrder
import ru.avem.kserialpooler.utils.allocateOrderedByteBuffer
import ru.avem.library.polling.DeviceController
import ru.avem.library.polling.DeviceRegister
import java.nio.ByteBuffer
import java.nio.ByteOrder

class AD800(
    override val name: String,
    override val protocolAdapter: ModbusRTUAdapter,
    override val id: Byte
) : DeviceController() {
    val model = AD800Model()
    override var requestTotalCount = 0
    override var requestSuccessCount = 0
    override val pollingRegisters = mutableListOf<DeviceRegister>()

    override val writingRegisters = mutableListOf<Pair<DeviceRegister, Number>>()

    init {
        protocolAdapter.connection.connect()
    }

    override fun readRegister(register: DeviceRegister) {
        isResponding = try {
            transactionWithAttempts {
                when (register.valueType) {
                    DeviceRegister.RegisterValueType.SHORT -> {
                        val modbusRegister =
                            protocolAdapter.readHoldingRegisters(id, register.address, 1).map(ModbusRegister::toShort)
                        register.value = modbusRegister.first().toDouble()
                    }

                    DeviceRegister.RegisterValueType.FLOAT -> {
                        val modbusRegister =
                            protocolAdapter.readHoldingRegisters(id, register.address, 2).map(ModbusRegister::toShort)
                        register.value =
                            allocateOrderedByteBuffer(modbusRegister, TypeByteOrder.BIG_ENDIAN, 4).float.toDouble()
                    }

                    DeviceRegister.RegisterValueType.INT32 -> {
                        val modbusRegister =
                            protocolAdapter.readHoldingRegisters(id, register.address, 2).map(ModbusRegister::toShort)
                        register.value =
                            allocateOrderedByteBuffer(modbusRegister, TypeByteOrder.BIG_ENDIAN, 4).int.toDouble()
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
                    val registers = listOf(ModbusRegister(bb.getShort(2)), ModbusRegister(bb.getShort(0)))
                    transactionWithAttempts {
                        protocolAdapter.presetMultipleRegisters(id, register.address, registers)
                    }
                }

                is Int -> {
                    transactionWithAttempts {
                        protocolAdapter.presetSingleRegister(id, register.address, ModbusRegister(value.toShort()))
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
        transactionWithAttempts {
            protocolAdapter.presetMultipleRegisters(id, register.address, registers)
        }
    }

    override fun checkResponsibility() {
        try {
            model.registers.values.firstOrNull()?.let {
                readRegister(it)
            }
        } catch (ignored: TransportException) {
        }
    }

    override fun getRegisterById(idRegister: String) = model.getRegisterById(idRegister)

    fun startObject() {
        writeRegister(getRegisterById(model.CONTROL_REGISTER), 1.toShort())
    }

    fun startObjectReverse() {
        writeRegister(getRegisterById(model.CONTROL_REGISTER), 2.toShort())
    }

    fun stopObject() {
        writeRegister(getRegisterById(model.CONTROL_REGISTER), 5.toShort())
    }

    fun stopObjectFreewheeling() {
        writeRegister(getRegisterById(model.CONTROL_REGISTER), 6.toShort())
    }

    fun setObjectParamsRun() {
        writeRegister(getRegisterById(model.VOLTAGE_1_REGISTER), 0.v())
        writeRegister(getRegisterById(model.FREQUENCY_1_REGISTER), 0.hz())

        writeRegister(getRegisterById(model.VOLTAGE_2_REGISTER), 0.v())
        writeRegister(getRegisterById(model.FREQUENCY_2_REGISTER), 0.hz())

        writeRegister(getRegisterById(model.VOLTAGE_3_REGISTER), 0.v())
        writeRegister(getRegisterById(model.FREQUENCY_3_REGISTER), 0.hz())

        writeRegister(getRegisterById(model.VOLTAGE_4_REGISTER), 0.v())
        writeRegister(getRegisterById(model.FREQUENCY_4_REGISTER), 0.hz())

        writeRegister(getRegisterById(model.VOLTAGE_5_REGISTER), 380.v())
        writeRegister(getRegisterById(model.FREQUENCY_5_REGISTER), 50.hz())
    }

    fun setObjectUMax(voltageMax: Number) {
        writeRegister(getRegisterById(model.VOLTAGE_4_REGISTER), voltageMax.v())
    }

    fun setObjectFCur(f: Double) {
        if (f > 0) {
            writeRegister(getRegisterById(model.OUTPUT_FREQUENCY_REGISTER), f.hz() * 10)
        } else {
            writeRegister(getRegisterById(model.OUTPUT_FREQUENCY_REGISTER), 0)
        }
    }

    fun setVoltage(voltage: Number) {
        val setVoltage = maxOf(0.0, minOf(voltage.toDouble(), 400.0))
        writeRegister(getRegisterById(model.VOLTAGE_PERCENT), setVoltage.vP())
    }

    override fun writeRequest(request: String) {

    }

    private fun Number.hz(): Short = (this.toDouble() * 10).toInt().toShort()
    private fun Number.v(): Short = (this.toDouble() * 10).toInt().toShort()
    private fun Number.vP(): Short = (this.toDouble() * 10000 / 400).toInt().toShort() // TODO брать 400 из регистра
}
