package ru.avem.stand.io.owen.trm202

import ru.avem.library.polling.DeviceRegister
import ru.avem.library.polling.IDeviceModel

class TRM202Model : IDeviceModel {
    val T_1 = "T_1"
    val T_2 = "T_2"

    override val registers: Map<String, DeviceRegister> = mapOf(
        T_1 to DeviceRegister(0x1009, DeviceRegister.RegisterValueType.FLOAT),
        T_2 to DeviceRegister(0x100B, DeviceRegister.RegisterValueType.FLOAT)
    )

    override fun getRegisterById(idRegister: String) =
        registers[idRegister] ?: error("Такого регистра нет в описанной карте $idRegister")
}
