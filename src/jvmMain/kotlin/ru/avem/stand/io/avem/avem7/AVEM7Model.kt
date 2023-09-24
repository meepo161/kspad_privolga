package ru.avem.stand.io.avem.avem7

import ru.avem.library.polling.DeviceRegister
import ru.avem.library.polling.IDeviceModel


class AVEM7Model : IDeviceModel {
    val AMPERAGE = "AMPERAGE"
    val SHUNT = "SHUNT"
    val PGA_MODE = "PGA_MODE"
    val RELAY_STATE = "RELAY_STATE"
    val SERIAL_NUMBER = "SERIAL_NUMBER"

    override val registers = mapOf(
        AMPERAGE to DeviceRegister(0x1004, DeviceRegister.RegisterValueType.FLOAT),
        SHUNT to DeviceRegister(0x11A0, DeviceRegister.RegisterValueType.FLOAT),
        PGA_MODE to DeviceRegister(0x10C4, DeviceRegister.RegisterValueType.SHORT),
        RELAY_STATE to DeviceRegister(0x1136, DeviceRegister.RegisterValueType.SHORT),
        SERIAL_NUMBER to DeviceRegister(0x1108, DeviceRegister.RegisterValueType.SHORT)
    )

    override fun getRegisterById(idRegister: String) =
        registers[idRegister] ?: error("Такого регистра нет в описанной карте $idRegister")
}
