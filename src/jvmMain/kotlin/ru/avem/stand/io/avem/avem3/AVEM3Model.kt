package ru.avem.stand.io.avem.avem3

import ru.avem.library.polling.DeviceRegister
import ru.avem.library.polling.IDeviceModel

class AVEM3Model : IDeviceModel {
    val U_TRMS = "U_TRMS"
    val U_AMP = "U_AMP"
    val TRIGGER_VALUE = "TRIGGER_VALUE"
    val SET_CHART_TIME = "SET_CHART_TIME"
    val TRIGGER_MODE = "TRIGGER_MODE"
    val START_CHART = "START_CHART"
    val STATE_CHART = "STATE_CHART"
    val GET_CHART_PERIOD = "GET_CHART_PERIOD"
    val CHART_POINTS = "CHART_POINTS"

    override val registers: Map<String, DeviceRegister> = mapOf(
        U_TRMS to DeviceRegister(
            0x1004,
            DeviceRegister.RegisterValueType.FLOAT
        ),
        U_AMP to DeviceRegister(
            0x1000,
            DeviceRegister.RegisterValueType.FLOAT
        ),
        TRIGGER_VALUE to DeviceRegister(
            0xE000.toShort(),
            DeviceRegister.RegisterValueType.FLOAT
        ),
        SET_CHART_TIME to DeviceRegister(
            0xE002.toShort(),
            DeviceRegister.RegisterValueType.INT32
        ),
        TRIGGER_MODE to DeviceRegister(
            0xE004.toShort(),
            DeviceRegister.RegisterValueType.SHORT
        ),
        START_CHART to DeviceRegister(
            0xE005.toShort(),
            DeviceRegister.RegisterValueType.SHORT
        ),
        STATE_CHART to DeviceRegister(
            0xE006.toShort(),
            DeviceRegister.RegisterValueType.SHORT
        ),
        GET_CHART_PERIOD to DeviceRegister(
            0xE008.toShort(),
            DeviceRegister.RegisterValueType.INT32
        ),
        CHART_POINTS to DeviceRegister(
            0xE00A.toShort(),
            DeviceRegister.RegisterValueType.FLOAT
        )
    )

    override fun getRegisterById(idRegister: String) =
        registers[idRegister] ?: error("Такого регистра нет в описанной карте $idRegister")
}
