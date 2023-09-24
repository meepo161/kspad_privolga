package ru.avem.stand.io.optimusdrive.ad800

import ru.avem.library.polling.DeviceRegister
import ru.avem.library.polling.IDeviceModel

class AD800Model : IDeviceModel {
    val CONTROL_REGISTER = "CONTROL_REGISTER"
    val OUTPUT_FREQUENCY_REGISTER = "OUTPUT_FREQUENCY_REGISTER"
    val VOLTAGE_1_REGISTER = "VOLTAGE_1_REGISTER"
    val FREQUENCY_1_REGISTER = "FREQUENCY_1_REGISTER"
    val VOLTAGE_2_REGISTER = "VOLTAGE_2_REGISTER"
    val FREQUENCY_2_REGISTER = "FREQUENCY_2_REGISTER"
    val VOLTAGE_3_REGISTER = "VOLTAGE_3_REGISTER"
    val FREQUENCY_3_REGISTER = "FREQUENCY_3_REGISTER"
    val VOLTAGE_4_REGISTER = "VOLTAGE_4_REGISTER"
    val FREQUENCY_4_REGISTER = "FREQUENCY_4_REGISTER"
    val VOLTAGE_5_REGISTER = "VOLTAGE_5_REGISTER"
    val FREQUENCY_5_REGISTER = "FREQUENCY_5_REGISTER"

    val CURRENT_VOLTAGE_REGISTER = "CURRENT_VOLTAGE_REGISTER"
    val CURRENT_FREQUENCY_REGISTER = "CURRENT_FREQUENCY_REGISTER"
    val CURRENT_CURRENT_REGISTER = "CURRENT_CURRENT_REGISTER"

    val VOLTAGE = "VOLTAGE"
    val VOLTAGE_PERCENT = "VOLTAGE_PERCENT"

    override val registers: Map<String, DeviceRegister> = mapOf(
        CONTROL_REGISTER to DeviceRegister(0x270F, DeviceRegister.RegisterValueType.SHORT),
        OUTPUT_FREQUENCY_REGISTER to DeviceRegister(0x2710, DeviceRegister.RegisterValueType.SHORT),

        VOLTAGE_1_REGISTER to DeviceRegister(0x0098, DeviceRegister.RegisterValueType.SHORT),
        FREQUENCY_1_REGISTER to DeviceRegister(0x0099, DeviceRegister.RegisterValueType.SHORT),
        VOLTAGE_2_REGISTER to DeviceRegister(0x009A, DeviceRegister.RegisterValueType.SHORT),
        FREQUENCY_2_REGISTER to DeviceRegister(0x009B, DeviceRegister.RegisterValueType.SHORT),
        VOLTAGE_3_REGISTER to DeviceRegister(0x009C, DeviceRegister.RegisterValueType.SHORT),
        FREQUENCY_3_REGISTER to DeviceRegister(0x009D, DeviceRegister.RegisterValueType.SHORT),
        VOLTAGE_4_REGISTER to DeviceRegister(0x009E, DeviceRegister.RegisterValueType.SHORT),
        FREQUENCY_4_REGISTER to DeviceRegister(0x009F, DeviceRegister.RegisterValueType.SHORT),
        VOLTAGE_5_REGISTER to DeviceRegister(0x00A0, DeviceRegister.RegisterValueType.SHORT),
        FREQUENCY_5_REGISTER to DeviceRegister(0x00A1, DeviceRegister.RegisterValueType.SHORT),

        CURRENT_VOLTAGE_REGISTER to DeviceRegister(0x0389, DeviceRegister.RegisterValueType.SHORT),
        CURRENT_FREQUENCY_REGISTER to DeviceRegister(0x038A, DeviceRegister.RegisterValueType.SHORT),
        CURRENT_CURRENT_REGISTER to DeviceRegister(0x038B, DeviceRegister.RegisterValueType.SHORT),

        VOLTAGE to DeviceRegister(103, DeviceRegister.RegisterValueType.SHORT),
        VOLTAGE_PERCENT to DeviceRegister(20, DeviceRegister.RegisterValueType.SHORT),
    )

    override fun getRegisterById(idRegister: String) =
        registers[idRegister] ?: error("Такого регистра нет в описанной карте $idRegister")
}
