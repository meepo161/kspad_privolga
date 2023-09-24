package ru.avem.stand.io.avem.avem9

import ru.avem.library.polling.DeviceRegister
import ru.avem.library.polling.IDeviceModel

class AVEM9Model : IDeviceModel {
    val STATUS = "STATUS"
    val VOLTAGE_SCHEME = "VOLTAGE_SCHEME"
    val VOLTAGE_AKB = "VOLTAGE_AKB"
    val CFG_SCHEME = "CFG_SCHEME"
    val TIMER = "TIMER"
    val START_STOP = "START_STOP"
    val VOLTAGE = "VOLTAGE"
    val R15_MEAS = "R15_MEAS"
    val R60_MEAS = "R60_MEAS"
    val R600_MEAS = "R600_MEAS"
    val POLARIZATION = "POLARIZATION"
    val ABSORPTION = "ABSORPTION"

    enum class MeasurementMode(val modeName: String, val scheme: Short) {
        Empty("", 0),
        Resistance("Измерение сопротивления", 15),
        AbsRatio("Измерение Кабс", 60),
        PolRatio("Измерение Кпол", 600);

        override fun toString() = modeName
    }

    enum class SpecifiedVoltage(val scheme: Short, val value: String) {
        Empty(0, ""),
        V500(1.toShort(), "500 В"),
        V1000(2.toShort(), "1000 В"),
        V2500(3.toShort(), "2500 В");

        override fun toString() = value
    }

    override val registers: Map<String, DeviceRegister> = mapOf(
        STATUS to DeviceRegister(5, DeviceRegister.RegisterValueType.SHORT),
        VOLTAGE_SCHEME to DeviceRegister(2, DeviceRegister.RegisterValueType.SHORT),
        CFG_SCHEME to DeviceRegister(3, DeviceRegister.RegisterValueType.SHORT),
        START_STOP to DeviceRegister(4, DeviceRegister.RegisterValueType.SHORT),
        VOLTAGE_AKB to DeviceRegister(6, DeviceRegister.RegisterValueType.FLOAT),
        VOLTAGE to DeviceRegister(8, DeviceRegister.RegisterValueType.FLOAT),
        R15_MEAS to DeviceRegister(10, DeviceRegister.RegisterValueType.FLOAT),
        R60_MEAS to DeviceRegister(12, DeviceRegister.RegisterValueType.FLOAT),
        R600_MEAS to DeviceRegister(14, DeviceRegister.RegisterValueType.FLOAT),
        ABSORPTION to DeviceRegister(16, DeviceRegister.RegisterValueType.FLOAT),
        POLARIZATION to DeviceRegister(18, DeviceRegister.RegisterValueType.FLOAT),
        TIMER to DeviceRegister(20, DeviceRegister.RegisterValueType.SHORT),
    )

    override fun getRegisterById(idRegister: String) =
        registers[idRegister] ?: error("Такого регистра нет в описанной карте $idRegister")
}
