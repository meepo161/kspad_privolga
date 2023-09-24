package ru.avem.stand

import ru.avem.kserialpooler.PortDiscover
import ru.avem.stand.io.DevicePoller
import kotlin.system.exitProcess

fun exit() {
    DevicePoller.stop()
    PortDiscover.isPortDiscover = false
    Thread.sleep(1)
    exitProcess(0)
}
