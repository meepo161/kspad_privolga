package ru.avem.stand.io.avem.atr

import ru.avem.library.polling.DeviceRegister
import ru.avem.library.polling.IDeviceModel

class ATRModel : IDeviceModel {
    val U_RMS_REGISTER = "Измеренное значение"
    val ENDS_STATUS_REGISTER = "Нижний концевик"

    //  val MAX_END_STATUS_REGISTER = "Верхний концевик"
    val VALUE_REGISTER = "Заданное напряжение"
    val REGULATION_TIME_REGISTER = "Время выхода"
    val CORRIDOR_REGISTER = "CORRIDOR_REGISTER"
    val DELTA_REGISTER = "DELTA_REGISTER"
    val MIN_VOLTAGE_LIMIT_REGISTER = "Заданное - это значение для точной"
    val START_REGISTER = " START_REGISTER"
    val STOP_REGISTER = " STOP_REGISTER"
    val IR_TIME_PULSE_MAX_PERCENT = "IR_TIME_PULSE_MAX_PERCENT"
    val IR_TIME_PULSE_MIN_PERCENT = "IR_TIME_PULSE_MIN_PERCENT"
    val IR_DUTY_MAX_PERCENT = "IR_DUTY_MAX_PERCENT"
    val IR_DUTY_MIN_PERCENT = "IR_DUTY_MIN_PERCENT"
    val IR_TIME_PERIOD_MAX = "IR_TIME_PERIOD_MAX"
    val IR_TIME_PERIOD_MIN = "IR_TIME_PERIOD_MIN"
    val IR_TIMEOUT_RS = "IR_TIMEOUT_RS"
    val STATUS = "STATUS"

    override val registers: Map<String, DeviceRegister> = mapOf(
        STATUS to DeviceRegister(0x1024, DeviceRegister.RegisterValueType.INT32),
        U_RMS_REGISTER to DeviceRegister(0x1004, DeviceRegister.RegisterValueType.FLOAT),
        ENDS_STATUS_REGISTER to DeviceRegister(0x1119, DeviceRegister.RegisterValueType.SHORT),
//      MAX_END_STATUS_REGISTER to DeviceRegister(0x1118, DeviceRegister.RegisterValueType.SHORT),
        VALUE_REGISTER to DeviceRegister(0x111A, DeviceRegister.RegisterValueType.FLOAT),
        REGULATION_TIME_REGISTER to DeviceRegister(0x1120, DeviceRegister.RegisterValueType.INT32),
        IR_TIMEOUT_RS to DeviceRegister(0x1154, DeviceRegister.RegisterValueType.INT32),
        CORRIDOR_REGISTER to DeviceRegister(0x1122, DeviceRegister.RegisterValueType.FLOAT),
        DELTA_REGISTER to DeviceRegister(0x1124, DeviceRegister.RegisterValueType.FLOAT),
        MIN_VOLTAGE_LIMIT_REGISTER to DeviceRegister(0x112C, DeviceRegister.RegisterValueType.FLOAT),
        START_REGISTER to DeviceRegister(0x112E, DeviceRegister.RegisterValueType.SHORT),
        STOP_REGISTER to DeviceRegister(0x112F, DeviceRegister.RegisterValueType.SHORT),
        IR_TIME_PULSE_MAX_PERCENT to DeviceRegister(0x1156, DeviceRegister.RegisterValueType.FLOAT),
        IR_TIME_PULSE_MIN_PERCENT to DeviceRegister(0x1158, DeviceRegister.RegisterValueType.FLOAT),
        IR_DUTY_MAX_PERCENT to DeviceRegister(0x115A, DeviceRegister.RegisterValueType.FLOAT),
        IR_DUTY_MIN_PERCENT to DeviceRegister(0x115C, DeviceRegister.RegisterValueType.FLOAT),
        IR_TIME_PERIOD_MAX to DeviceRegister(0x115E, DeviceRegister.RegisterValueType.FLOAT),
        IR_TIME_PERIOD_MIN to DeviceRegister(0x1160, DeviceRegister.RegisterValueType.FLOAT)
    )

    override fun getRegisterById(idRegister: String) =
        registers[idRegister] ?: error("Такого регистра нет в описанной карте $idRegister")
}
