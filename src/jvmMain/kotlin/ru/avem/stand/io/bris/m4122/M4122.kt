package ru.avem.stand.io.bris.m4122

import ru.avem.kserialpooler.adapters.serial.SerialAdapter
import ru.avem.kserialpooler.utils.toHexString
import ru.avem.library.polling.DeviceController
import ru.avem.library.polling.DeviceRegister
import ru.avem.stand.io.bris.m4122.M4122Model.Companion.KABS
import ru.avem.stand.io.bris.m4122.M4122Model.Companion.MEASUREDVOLTAGE
import ru.avem.stand.io.bris.m4122.M4122Model.Companion.RESISTANCE
import ru.avem.stand.io.bris.m4122.M4122Model.Companion.RESULT
import ru.avem.stand.io.bris.m4122.M4122Model.Companion.SETVOLTAGE
import ru.avem.stand.io.bris.m4122.M4122Model.Companion.STATUS
import java.lang.Thread.sleep
import java.nio.ByteBuffer
import java.nio.ByteOrder

class M4122(
    override val name: String,
    override val protocolAdapter: SerialAdapter,
    override val id: Byte
) : DeviceController() {
    private val model = M4122Model()
    override var requestTotalCount = 0
    override var requestSuccessCount = 0
    override val pollingRegisters = mutableListOf<DeviceRegister>()

    override val writingRegisters = mutableListOf<Pair<DeviceRegister, Number>>()

    fun setVoltage(u: Int) {
        synchronized(protocolAdapter.connection) {
            protocolAdapter.write(model.getCommandById(SETVOLTAGE) + convertToBytes(u.toShort()))
            protocolAdapter.read(ByteArray(40))
        }
    }

    fun getVoltage(): Boolean {
        synchronized(protocolAdapter.connection) {
            protocolAdapter.write(model.getCommandById(MEASUREDVOLTAGE))
            val inputArray = ByteArray(40)
            val inputBuffer = ByteBuffer.allocate(40)
            var attempt = 0
            var frameSize: Int
            do {
                sleep(2)
                frameSize = maxOf(protocolAdapter.read(inputArray), 0)
                inputBuffer.put(inputArray, 0, frameSize)
            } while (inputBuffer.position() < 1 && ++attempt < 10)
            return frameSize > 0
        }
    }

    fun startMeasResistance() {
        synchronized(protocolAdapter.connection) {
            protocolAdapter.write(model.getCommandById(RESISTANCE))
            val inputArray = ByteArray(40)
            protocolAdapter.read(inputArray)
        }
    }

    fun startMeasAbs() {
        synchronized(protocolAdapter.connection) {
            protocolAdapter.write(model.getCommandById(KABS))
            val inputArray = ByteArray(40)
            protocolAdapter.read(inputArray)
        }
    }

    fun getMeasResistance(): List<Int> {
        synchronized(protocolAdapter.connection) {
            var voltage = 0
            var resistance = 0
            var result = 103
            protocolAdapter.write(model.getCommandById(RESULT))
            val inputArray = ByteArray(40)
            val inpBuf = ByteBuffer.allocate(40)
            sleep(2)
            val frameSize = maxOf(protocolAdapter.read(inputArray), 0)
            inpBuf.put(inputArray, 0, frameSize)
            try {
                result = inpBuf[0].toInt() // 91 - OK / 103 - NOT OK / 93 - Предыдущий результат из памяти
                voltage = ByteBuffer.allocate(4).put(inpBuf[5]).put(inpBuf[4]).apply { flip() }.short.toInt()
                resistance = ByteBuffer.allocate(8).put(inpBuf[10]).put(inpBuf[9])
                    .put(inpBuf[8]).put(inpBuf[7]).apply { flip() }.int
                println("====================================")
                println(inpBuf.array().toHexString())
                println("====================================")
            } catch (e: Exception) {
                println(e)
            }
            return listOf(result, voltage, resistance / 1000)
        }
    }

    fun getMeasAbs(): List<Double> {
        synchronized(protocolAdapter.connection) {
            var voltage = 0
            var r1 = 0
            var r2 = 0
            var abs = 0
            var result = 103

            protocolAdapter.write(model.getCommandById(RESULT))

            val inputArray = ByteArray(40)
            sleep(2)
            val frameSize = maxOf(protocolAdapter.read(inputArray), 0)

            val inpBuf = ByteBuffer.allocate(40)
            inpBuf.put(inputArray, 0, frameSize)

            try {
                println("===============================")
                println(inpBuf.array().toHexString())
                println("===============================")
                result = inpBuf[0].toInt() // 91 - OK / 103 - NOT OK / 93 - Предыдущий результат из памяти
                println("result = $result")
                val offset = if (result == 91) 1 else 0
                voltage = ByteBuffer.allocate(2).put(inpBuf[4 + offset]).put(inpBuf[3 + offset])
                    .apply { flip() }.short.toInt()
                r1 = ByteBuffer.allocate(4).put(inpBuf[9 + offset]).put(inpBuf[8 + offset]).put(inpBuf[7 + offset])
                    .put(inpBuf[6 + offset]).apply { flip() }.int
                r2 = ByteBuffer.allocate(4).put(inpBuf[14 + offset]).put(inpBuf[13 + offset]).put(inpBuf[12 + offset])
                    .put(inpBuf[11 + offset]).apply { flip() }.int
                abs = ByteBuffer.allocate(2).put(inpBuf[17 + offset]).put(inpBuf[16 + offset])
                    .apply { flip() }.short.toInt()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return listOf(result.toDouble(), voltage.toDouble(), r1 / 1000.0, r2 / 1000.0, abs / 1000.0)
        }
    }

    private fun convertToBytes(voltage: Short) =
        ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(voltage).array()


    override var isResponding: Boolean = false
        get() {
            checkResponsibility()
            return field
        }

    override fun getRegisterById(idRegister: String) = model.registers[M4122Model.RESPONDING_PARAM]!!

    override fun checkResponsibility() {
        synchronized(protocolAdapter.connection) {
            isResponding = try {
                protocolAdapter.write(model.getCommandById(STATUS))
                val inputArray = ByteArray(40)
                val inputBuffer = ByteBuffer.allocate(40)
                sleep(10)
                val frameSize = maxOf(protocolAdapter.read(inputArray), 0)
                inputBuffer.put(inputArray, 0, frameSize)
                inputBuffer[0].toInt() == 0x55 || inputBuffer[1].toInt() == 0x55
            } catch (e: Exception) {
                false
            }
        }
    }

    override fun readAllRegisters() {}

    override fun readRegister(register: DeviceRegister) {}

    override fun <T : Number> writeRegister(register: DeviceRegister, value: T) {}

    override fun writeRegisters(register: DeviceRegister, values: List<Short>) {}

    override fun writeRequest(request: String) {}
}
