package ru.avem.stand.io.delta.vfdel

import ru.avem.kserialpooler.adapters.modbusrtu.ModbusRTUAdapter
import ru.avem.kserialpooler.adapters.utils.ModbusRegister
import ru.avem.kserialpooler.utils.TransportException
import ru.avem.kserialpooler.utils.TypeByteOrder
import ru.avem.kserialpooler.utils.allocateOrderedByteBuffer
import ru.avem.library.polling.DeviceController
import ru.avem.library.polling.DeviceRegister
import ru.avem.stand.io.delta.vfdel.VFDELModel.Companion.CONTROL_REGISTER
import ru.avem.stand.io.delta.vfdel.VFDELModel.Companion.CURRENT_FREQUENCY_OUTPUT_REGISTER
import ru.avem.stand.io.delta.vfdel.VFDELModel.Companion.MAX_FREQUENCY_OUT_REGISTER
import ru.avem.stand.io.delta.vfdel.VFDELModel.Companion.MAX_FREQUENCY_TI_REGISTER
import ru.avem.stand.io.delta.vfdel.VFDELModel.Companion.MAX_VOLTAGE_REGISTER
import ru.avem.stand.io.delta.vfdel.VFDELModel.Companion.POINT_1_FREQUENCY_REGISTER
import ru.avem.stand.io.delta.vfdel.VFDELModel.Companion.POINT_1_VOLTAGE_REGISTER
import ru.avem.stand.io.delta.vfdel.VFDELModel.Companion.POINT_2_FREQUENCY_REGISTER
import ru.avem.stand.io.delta.vfdel.VFDELModel.Companion.POINT_2_VOLTAGE_REGISTER
import java.nio.ByteBuffer
import java.nio.ByteOrder

class VFDEL(
    override val name: String,
    override val protocolAdapter: ModbusRTUAdapter,
    override val id: Byte
) : DeviceController() {
    private val model = VFDELModel()
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
                    val bb = ByteBuffer.allocate(4).putInt(value).order(ByteOrder.BIG_ENDIAN)
                    val registers = listOf(ModbusRegister(bb.getShort(2)), ModbusRegister(bb.getShort(0)))
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

    enum class Direction {
        FORWARD,
        REVERSE
    }

    fun start(direction: Direction = Direction.FORWARD) {
        if (direction == Direction.FORWARD) {
            writeRegister(getRegisterById(CONTROL_REGISTER), (0b010010).toShort())
        }
        if (direction == Direction.REVERSE) {
            writeRegister(getRegisterById(CONTROL_REGISTER), (0b100010).toShort())
        }
    }

    fun stop() {
        writeRegister(getRegisterById(CONTROL_REGISTER), (0b1).toShort())
    }

    fun setParams(
        fOut: Number = 50,
        voltageP1: Number = 380,
        fP1: Number = 50,
        voltageP2: Number = 380 / 50,
        fP2: Number = 50 / 50
    ) {
        try {
            writeRegister(getRegisterById(MAX_VOLTAGE_REGISTER), (420.v() + 1).toShort())
            writeRegister(getRegisterById(MAX_FREQUENCY_OUT_REGISTER), (165.hz() + 1).toShort())
            writeRegister(getRegisterById(MAX_FREQUENCY_TI_REGISTER), (165.hz() + 1).toShort())

            writeRegister(getRegisterById(POINT_1_VOLTAGE_REGISTER), voltageP1.v())
            writeRegister(getRegisterById(POINT_1_FREQUENCY_REGISTER), fP1.hz())

            writeRegister(getRegisterById(POINT_2_VOLTAGE_REGISTER), voltageP2.v())
            writeRegister(getRegisterById(POINT_2_FREQUENCY_REGISTER), fP2.hz())

            writeRegister(getRegisterById(CURRENT_FREQUENCY_OUTPUT_REGISTER), fOut.hz())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun Number.hz(): Short = (this.toDouble() * 100).toInt().toShort()
    private fun Number.v(): Short = (this.toDouble() * 10).toInt().toShort()

    fun setUMax(voltageMax: Number) {
        writeRegister(getRegisterById(POINT_1_VOLTAGE_REGISTER), voltageMax.v())
    }

    fun setFCur(fOut: Double) {
        writeRegister(getRegisterById(CURRENT_FREQUENCY_OUTPUT_REGISTER), fOut.hz())
    }

    override fun writeRequest(request: String) {}
}
