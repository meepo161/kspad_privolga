package ru.avem.stand.io.meradat.m12thg1

import ru.avem.library.polling.DeviceRegister
import ru.avem.library.polling.IDeviceModel

class M12THG1Model : IDeviceModel {
    val RPM = "RPM"

    override val registers: Map<String, DeviceRegister> = mapOf(
        RPM to DeviceRegister(0x00, DeviceRegister.RegisterValueType.SHORT)
    )

    override fun getRegisterById(idRegister: String) =
        registers[idRegister] ?: error("Такого регистра нет в описанной карте $idRegister")
}
