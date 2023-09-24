package ru.avem.stand.io.owen.th01

import ru.avem.library.polling.DeviceRegister
import ru.avem.library.polling.IDeviceModel

class TH01Model : IDeviceModel {
    val RPM = "RPM"

    override val registers: Map<String, DeviceRegister> = mapOf(
        RPM to DeviceRegister(0x0029, DeviceRegister.RegisterValueType.INT32)
    )

    override fun getRegisterById(idRegister: String) =
        registers[idRegister] ?: error("Такого регистра нет в описанной карте $idRegister")
}
