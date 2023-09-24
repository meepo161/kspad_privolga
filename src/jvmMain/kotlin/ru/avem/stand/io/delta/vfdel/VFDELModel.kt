package ru.avem.stand.io.delta.vfdel

import ru.avem.library.polling.DeviceRegister
import ru.avem.library.polling.IDeviceModel

class VFDELModel : IDeviceModel {
    companion object {
        const val ERRORS_REGISTER = "ERRORS_REGISTER"
        const val STATUS_REGISTER = "STATUS_REGISTER"
        const val CURRENT_FREQUENCY_INPUT_REGISTER = "CURRENT_FREQUENCY_INPUT_REGISTER"
        const val CONTROL_REGISTER = "CONTROL_REGISTER"
        const val CURRENT_FREQUENCY_OUTPUT_REGISTER = "CURRENT_FREQUENCY_OUTPUT_REGISTER"
        const val MAX_FREQUENCY_OUT_REGISTER = "MAX_FREQUENCY_OUT_REGISTER"
        const val MAX_FREQUENCY_TI_REGISTER = "MAX_FREQUENCY_TI_REGISTER"
        const val MAX_VOLTAGE_REGISTER = "MAX_VOLTAGE_REGISTER"
        const val POINT_1_FREQUENCY_REGISTER = "POINT_1_FREQUENCY_REGISTER"
        const val POINT_1_VOLTAGE_REGISTER = "POINT_1_VOLTAGE_REGISTER"
        const val POINT_2_FREQUENCY_REGISTER = "POINT_2_FREQUENCY_REGISTER"
        const val POINT_2_VOLTAGE_REGISTER = "POINT_2_VOLTAGE_REGISTER"
        const val MIN_FREQUENCY_REGISTER = "MIN_FREQUENCY_REGISTER"
        const val MIN_VOLTAGE_REGISTER = "MIN_VOLTAGE_REGISTER"

        const val ACC_TIME = "ACC_TIME"
        const val DEC_TIME = "DEC_TIME"

        const val NORMALLY_MASK = "NORMALLY_MASK" // 0 - NO, 1 - NC
        const val CLOSURE_MASK = "CLOSURE_MASK"
    }

    override val registers: Map<String, DeviceRegister> = mapOf(
        ERRORS_REGISTER to DeviceRegister(0x2100, DeviceRegister.RegisterValueType.SHORT),
        STATUS_REGISTER to DeviceRegister(0x2101, DeviceRegister.RegisterValueType.SHORT),
        CURRENT_FREQUENCY_INPUT_REGISTER to DeviceRegister(0x2103, DeviceRegister.RegisterValueType.SHORT),
        CONTROL_REGISTER to DeviceRegister(0x2000, DeviceRegister.RegisterValueType.SHORT),
        CURRENT_FREQUENCY_OUTPUT_REGISTER to DeviceRegister(0x2001, DeviceRegister.RegisterValueType.SHORT),
        MAX_FREQUENCY_OUT_REGISTER to DeviceRegister(0x0100, DeviceRegister.RegisterValueType.SHORT),
        MAX_FREQUENCY_TI_REGISTER to DeviceRegister(0x0101, DeviceRegister.RegisterValueType.SHORT),
        MAX_VOLTAGE_REGISTER to DeviceRegister(0x0102, DeviceRegister.RegisterValueType.SHORT),
        POINT_1_FREQUENCY_REGISTER to DeviceRegister(0x0103, DeviceRegister.RegisterValueType.SHORT),
        POINT_1_VOLTAGE_REGISTER to DeviceRegister(0x0104, DeviceRegister.RegisterValueType.SHORT),
        POINT_2_FREQUENCY_REGISTER to DeviceRegister(0x0105, DeviceRegister.RegisterValueType.SHORT),
        POINT_2_VOLTAGE_REGISTER to DeviceRegister(0x0106, DeviceRegister.RegisterValueType.SHORT),
        MIN_FREQUENCY_REGISTER to DeviceRegister(0x0107, DeviceRegister.RegisterValueType.SHORT),
        MIN_VOLTAGE_REGISTER to DeviceRegister(0x0108, DeviceRegister.RegisterValueType.SHORT),

        ACC_TIME to DeviceRegister(0x0109, DeviceRegister.RegisterValueType.SHORT),
        DEC_TIME to DeviceRegister(0x010A, DeviceRegister.RegisterValueType.SHORT),

        NORMALLY_MASK to DeviceRegister(0x0409, DeviceRegister.RegisterValueType.SHORT),
        CLOSURE_MASK to DeviceRegister(0x041A, DeviceRegister.RegisterValueType.SHORT),
    )

    override fun getRegisterById(idRegister: String) =
        registers[idRegister] ?: error("Такого регистра нет в описанной карте $idRegister")
}
