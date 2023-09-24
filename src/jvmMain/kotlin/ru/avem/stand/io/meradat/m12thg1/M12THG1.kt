package ru.avem.stand.io.meradat.m12thg1

import ru.avem.kserialpooler.adapters.modbusascii.ModbusASCIIAdapter
import ru.avem.kserialpooler.adapters.utils.ModbusRegister
import ru.avem.kserialpooler.utils.TransportException
import ru.avem.library.polling.DeviceController
import ru.avem.library.polling.DeviceRegister

class M12THG1(
    override val name: String,
    override val protocolAdapter: ModbusASCIIAdapter,
    override val id: Byte
) : DeviceController() {
    val model = M12THG1Model()
    override var requestTotalCount = 0
    override var requestSuccessCount = 0
    override val pollingRegisters = mutableListOf<DeviceRegister>()

    override val writingRegisters = mutableListOf<Pair<DeviceRegister, Number>>()

    override fun readRegister(register: DeviceRegister) {
        isResponding = try {
            transactionWithAttempts {
                val modbusRegister =
                    protocolAdapter.readHoldingRegisters(id, register.address, 1).map(ModbusRegister::toShort)
                register.value = modbusRegister.first()
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

    override fun <T : Number> writeRegister(register: DeviceRegister, value: T) {

    }

    override fun writeRegisters(register: DeviceRegister, values: List<Short>) {

    }

    override fun checkResponsibility() {
        model.registers.values.firstOrNull()?.let {
            readRegister(it)
        }
    }

    override fun getRegisterById(idRegister: String) = model.getRegisterById(idRegister)

    override fun writeRequest(request: String) {}

    fun getRPM(): Int {
        readRegister(model.getRegisterById(model.RPM))

        return model.getRegisterById(model.RPM).value.toInt()
    }
}
