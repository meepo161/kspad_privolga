package ru.avem.stand.io.bris.m4122

import ru.avem.library.polling.DeviceRegister

class M4122Model {
    companion object {
        const val STATUS = "STATUS"                    // Проверка соединения
        const val SETVOLTAGE = "SETVOLTAGE"            // Установка напряжения
        const val MEASUREDVOLTAGE = "MEASUREDVOLTAGE"  // Измерительное напряжение
        const val VOLTAGE = "VOLTAGE"                  // Измерение напряжения
        const val RESISTANCE = "RESISTANCE"            // Измерение сопротивления
        const val KABS = "KABS"                        // Измерение коэффициента абсорбции(90 сек)
        const val POL = "POL"                          // Измерение поляризации
        const val COMPLETE = "COMPLETE"
        const val ERROR = "ERROR"
        const val RESULT = "RESULT"

        const val RESPONDING_PARAM = "RESPONDING_PARAM"
    }

    val commands: Map<String, ByteArray> = mapOf(
        STATUS to byteArrayOf(0x055),
        VOLTAGE to byteArrayOf(0x057),
        RESISTANCE to byteArrayOf(0x058),
        KABS to byteArrayOf(0x059),
        POL to byteArrayOf(0x05A),
        COMPLETE to byteArrayOf(0x05B),
        ERROR to byteArrayOf(0x067),
        RESULT to byteArrayOf(0x05C),
        SETVOLTAGE to byteArrayOf(0x06B),
        MEASUREDVOLTAGE to byteArrayOf(0x06A),
    )

    val registers: Map<String, DeviceRegister> = mapOf(
        RESPONDING_PARAM to DeviceRegister(0, DeviceRegister.RegisterValueType.SHORT)
    )

    fun getCommandById(idRegister: String) =
        commands[idRegister] ?: error("Такого регистра нет в описанной карте $idRegister")
}
