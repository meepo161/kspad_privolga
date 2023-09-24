package ru.avem.stand.io

import ru.avem.kserialpooler.Connection
import ru.avem.kserialpooler.adapters.modbusrtu.ModbusRTUAdapter
import ru.avem.kserialpooler.utils.SerialParameters
import ru.avem.library.polling.IDeviceController
import ru.avem.library.polling.SimplePollingModel
import ru.avem.stand.addTo
import ru.avem.stand.io.avem.avem3.AVEM3
import ru.avem.stand.io.avem.avem4.AVEM4
import ru.avem.stand.io.avem.avem7.AVEM7
import ru.avem.stand.io.avem.avem9.AVEM9
import ru.avem.stand.io.optimusdrive.ad800.AD800
import ru.avem.stand.io.owen.pr.PR
import ru.avem.stand.io.owen.th01.TH01
import ru.avem.stand.io.owen.trm202.TRM202
import ru.avem.stand.io.satec.pm130.PM130

object DevicePoller : SimplePollingModel() {
    private val connectionMain = Connection(
        "CP2103 USB to RS-485",
        SerialParameters(8, 0, 1, 38400)
    ).apply { connect() }

    private val connectionFI = Connection(
        "CP2103 USB to FI",
        SerialParameters(8, 0, 1, 38400)
    ).apply { connect() }

    private val main = ModbusRTUAdapter(connectionMain)
    private val fqir = ModbusRTUAdapter(connectionFI)

    private val devs = mutableListOf<IDeviceController>()

    val DD2 = PR("DD2", main, 2).addTo(devs) // ПР102-24.2416.03.1 (Прог. реле)

    val PV24 = AVEM3("PV24", main, 24).addTo(devs) // АВЭМ-3-04 (вольтметр) (напряжение ВВ)
    val PAV41 = PM130("PAV41", main, 41).addTo(devs) // PM-130P (МФУ)
    val PA62 = AVEM7("PA62", main, 62).addTo(devs) // АВЭМ-7-5000 (амперметр) (Измеритель А)
    val PV61 = AVEM4("PV61", main, 61).addTo(devs) // АВЭМ-4-01 (вольтметр) (Измеритель B)
    val PR65 = AVEM9("PR65", main, 65).addTo(devs) // АВЭМ-9 (меггер)
    val PC71 = TH01("PC71", main, 71).addTo(devs) // Тахометр
    val PS81 = TRM202("PS81", main, 81).addTo(devs) // Термометр

    val UZ91 = AD800("UZ91", fqir, 91.toByte()).addTo(devs) // регулятор (частотный преобразователь)

    override val deviceControllers: Map<String, IDeviceController> = devs.associateBy { it.name }

    init {
        start()
    }
}
