package ru.avem.stand.io.owen.pr

import ru.avem.library.polling.DeviceRegister
import ru.avem.library.polling.IDeviceModel

class PRModel : IDeviceModel {
    val DO_01_16 = "DO_01_16"
    val DO_17_32 = "DO_17_32"
    val BLINK_PERIOD = "BLINK_PERIOD"

    val WD_TIMEOUT = "WD_TIMEOUT"
    val CMD = "CMD"
    val STATE = "Состояние"

    val DI_01_16_RST = "DI_01_16_RST"
    val DI_17_32_RST = "DI_17_32_RST"
    val DI_33_48_RST = "DI_33_48_RST"

    val DI_01_16_TRIG = "DI_01_16_TRIG"
    val DI_17_32_TRIG = "DI_17_32_TRIG"
    val DI_33_48_TRIG = "DI_33_48_TRIG"

    val DI_01_16_TRIG_INV = "DI_01_16_TRIG_INV"
    val DI_17_32_TRIG_INV = "DI_17_32_TRIG_INV"
    val DI_33_48_TRIG_INV = "DI_33_48_TRIG_INV"

    val DI_01_16_ERROR_MASK_1 = "DI_01_16_ERROR_MASK_1"
    val DI_01_16_ERROR_MASK_0 = "DI_01_16_ERROR_MASK_0"
    val DI_17_32_ERROR_MASK_1 = "DI_17_32_ERROR_MASK_1"
    val DI_17_32_ERROR_MASK_0 = "DI_17_32_ERROR_MASK_0"
    val DI_33_48_ERROR_MASK_1 = "DI_33_48_ERROR_MASK_1"
    val DI_33_48_ERROR_MASK_0 = "DI_33_48_ERROR_MASK_0"

    val DO_01_16_ERROR_S1_MASK_0 = "DO_01_16_ERROR_S1_MASK_0"
    val DO_01_16_ERROR_S1_MASK_1 = "DO_01_16_ERROR_S1_MASK_1"
    val DO_17_32_ERROR_S1_MASK_0 = "DO_17_32_ERROR_S1_MASK_0"
    val DO_17_32_ERROR_S1_MASK_1 = "DO_17_32_ERROR_S1_MASK_1"

    val DO_01_16_ERROR_S2_MASK_0 = "DO_01_16_ERROR_S2_MASK_0"
    val DO_01_16_ERROR_S2_MASK_1 = "DO_01_16_ERROR_S2_MASK_1"
    val DO_17_32_ERROR_S2_MASK_0 = "DO_17_32_ERROR_S2_MASK_0"
    val DO_17_32_ERROR_S2_MASK_1 = "DO_17_32_ERROR_S2_MASK_1"

    val DO_01_16_ERROR_S3_MASK_0 = "DO_01_16_ERROR_S3_MASK_0"
    val DO_01_16_ERROR_S3_MASK_1 = "DO_01_16_ERROR_S3_MASK_1"
    val DO_17_32_ERROR_S3_MASK_0 = "DO_17_32_ERROR_S3_MASK_0"
    val DO_17_32_ERROR_S3_MASK_1 = "DO_17_32_ERROR_S3_MASK_1"

    val DO_01_16_ERROR_S4_MASK_0 = "DO_01_16_ERROR_S4_MASK_0"
    val DO_01_16_ERROR_S4_MASK_1 = "DO_01_16_ERROR_S4_MASK_1"
    val DO_17_32_ERROR_S4_MASK_0 = "DO_17_32_ERROR_S4_MASK_0"
    val DO_17_32_ERROR_S4_MASK_1 = "DO_17_32_ERROR_S4_MASK_1"

    val DO_ERROR_S1_TIME = "DO_ERROR_S1_TIME"
    val DO_ERROR_S2_TIME = "DO_ERROR_S2_TIME"
    val DO_ERROR_S3_TIME = "DO_ERROR_S3_TIME"
    val DO_ERROR_S4_TIME = "DO_ERROR_S4_TIME"

    val AI_01_F = "AI_01_F"
    val AI_02_F = "AI_02_F"
    val AO_01_F = "AO_01_F"
    val AO_02_F = "AO_02_F"

    override val registers: Map<String, DeviceRegister> = mapOf(
        DO_01_16 to DeviceRegister(512, DeviceRegister.RegisterValueType.SHORT),
        DO_17_32 to DeviceRegister(513, DeviceRegister.RegisterValueType.SHORT),
        BLINK_PERIOD to DeviceRegister(514, DeviceRegister.RegisterValueType.SHORT),

        WD_TIMEOUT to DeviceRegister(573, DeviceRegister.RegisterValueType.SHORT),
        CMD to DeviceRegister(574, DeviceRegister.RegisterValueType.SHORT),
        STATE to DeviceRegister(575, DeviceRegister.RegisterValueType.SHORT),

        DI_01_16_RST to DeviceRegister(517, DeviceRegister.RegisterValueType.SHORT),
        DI_17_32_RST to DeviceRegister(521, DeviceRegister.RegisterValueType.SHORT),
        DI_33_48_RST to DeviceRegister(525, DeviceRegister.RegisterValueType.SHORT),

        DI_01_16_TRIG to DeviceRegister(518, DeviceRegister.RegisterValueType.SHORT),
        DI_17_32_TRIG to DeviceRegister(522, DeviceRegister.RegisterValueType.SHORT),
        DI_33_48_TRIG to DeviceRegister(526, DeviceRegister.RegisterValueType.SHORT),

        DI_01_16_TRIG_INV to DeviceRegister(519, DeviceRegister.RegisterValueType.SHORT),
        DI_17_32_TRIG_INV to DeviceRegister(523, DeviceRegister.RegisterValueType.SHORT),
        DI_33_48_TRIG_INV to DeviceRegister(527, DeviceRegister.RegisterValueType.SHORT),

        DI_01_16_ERROR_MASK_1 to DeviceRegister(547, DeviceRegister.RegisterValueType.SHORT),
        DI_01_16_ERROR_MASK_0 to DeviceRegister(548, DeviceRegister.RegisterValueType.SHORT),
        DI_17_32_ERROR_MASK_1 to DeviceRegister(549, DeviceRegister.RegisterValueType.SHORT),
        DI_17_32_ERROR_MASK_0 to DeviceRegister(550, DeviceRegister.RegisterValueType.SHORT),
        DI_33_48_ERROR_MASK_1 to DeviceRegister(551, DeviceRegister.RegisterValueType.SHORT),
        DI_33_48_ERROR_MASK_0 to DeviceRegister(552, DeviceRegister.RegisterValueType.SHORT),

        DO_01_16_ERROR_S1_MASK_0 to DeviceRegister(554, DeviceRegister.RegisterValueType.SHORT),
        DO_01_16_ERROR_S1_MASK_1 to DeviceRegister(553, DeviceRegister.RegisterValueType.SHORT),
        DO_17_32_ERROR_S1_MASK_0 to DeviceRegister(556, DeviceRegister.RegisterValueType.SHORT),
        DO_17_32_ERROR_S1_MASK_1 to DeviceRegister(555, DeviceRegister.RegisterValueType.SHORT),

        DO_01_16_ERROR_S2_MASK_0 to DeviceRegister(559, DeviceRegister.RegisterValueType.SHORT),
        DO_01_16_ERROR_S2_MASK_1 to DeviceRegister(558, DeviceRegister.RegisterValueType.SHORT),
        DO_17_32_ERROR_S2_MASK_0 to DeviceRegister(561, DeviceRegister.RegisterValueType.SHORT),
        DO_17_32_ERROR_S2_MASK_1 to DeviceRegister(560, DeviceRegister.RegisterValueType.SHORT),

        DO_01_16_ERROR_S3_MASK_0 to DeviceRegister(564, DeviceRegister.RegisterValueType.SHORT),
        DO_01_16_ERROR_S3_MASK_1 to DeviceRegister(563, DeviceRegister.RegisterValueType.SHORT),
        DO_17_32_ERROR_S3_MASK_0 to DeviceRegister(566, DeviceRegister.RegisterValueType.SHORT),
        DO_17_32_ERROR_S3_MASK_1 to DeviceRegister(565, DeviceRegister.RegisterValueType.SHORT),

        DO_01_16_ERROR_S4_MASK_0 to DeviceRegister(569, DeviceRegister.RegisterValueType.SHORT),
        DO_01_16_ERROR_S4_MASK_1 to DeviceRegister(568, DeviceRegister.RegisterValueType.SHORT),
        DO_17_32_ERROR_S4_MASK_0 to DeviceRegister(571, DeviceRegister.RegisterValueType.SHORT),
        DO_17_32_ERROR_S4_MASK_1 to DeviceRegister(570, DeviceRegister.RegisterValueType.SHORT),

        DO_ERROR_S1_TIME to DeviceRegister(557, DeviceRegister.RegisterValueType.SHORT),
        DO_ERROR_S2_TIME to DeviceRegister(562, DeviceRegister.RegisterValueType.SHORT),
        DO_ERROR_S3_TIME to DeviceRegister(567, DeviceRegister.RegisterValueType.SHORT),
        DO_ERROR_S4_TIME to DeviceRegister(572, DeviceRegister.RegisterValueType.SHORT),

        AI_01_F to DeviceRegister(531, DeviceRegister.RegisterValueType.FLOAT), // CDAB
        AI_02_F to DeviceRegister(533, DeviceRegister.RegisterValueType.FLOAT), // CDAB
        AO_01_F to DeviceRegister(539, DeviceRegister.RegisterValueType.FLOAT), // CDAB
        AO_02_F to DeviceRegister(541, DeviceRegister.RegisterValueType.FLOAT), // CDAB
    )

    override fun getRegisterById(idRegister: String) =
        registers[idRegister] ?: error("Такого регистра нет в описанной карте $idRegister")
}
