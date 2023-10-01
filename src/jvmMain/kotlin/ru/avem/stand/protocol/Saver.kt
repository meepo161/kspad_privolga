package ru.avem.stand.protocol

import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import ru.avem.stand.db.entities.Protocol
import ru.avem.stand.db.entities.ProtocolField
import ru.avem.stand.formatPoint
import ru.avem.stand.testitem.TIManager
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.text.SimpleDateFormat

const val metavariableParts = "\${}"

fun saveProtocolsAsWorkbook(protocolFields: List<ProtocolField>, protocolPathString: String, targetPath: File? = null): Path {
    val mills = System.currentTimeMillis()
    val protocolDir = Files.createDirectories(Paths.get("protocols/${SimpleDateFormat("yyyyMMdd").format(mills)}"))
    val resultFile = targetPath ?: Paths.get(protocolDir.toString(), "$mills.xlsx").toFile()
    val templateStream = Protocol::class.java.getResourceAsStream("/templates/$protocolPathString")
    copyFileFromStream(templateStream, resultFile)

    data class LocalProtocolFields(val key: String, var value: String)

    val fields = protocolFields.map { LocalProtocolFields(it.key, it.value) }
        .toMutableList().also {
            it.addAll(
                listOf(
                    LocalProtocolFields("TEST_EQUIPMENT", "КСПАД 441462.294"),
                    LocalProtocolFields("TE_SERIAL", "231030294"),
                    LocalProtocolFields("P", TIManager.testItem.nominalPower.formatPoint(2)),
                    LocalProtocolFields("U", TIManager.testItem.nominalVoltage.formatPoint()),
                    LocalProtocolFields("I", TIManager.testItem.nominalCurrent.formatPoint(2)),
                    LocalProtocolFields("N", TIManager.testItem.nominalN.formatPoint()),
                    LocalProtocolFields("Cos", TIManager.testItem.nominalCos.formatPoint(2)),
                    LocalProtocolFields("SCHEME", TIManager.testItem.scheme),
                )
            )
        }

    XSSFWorkbook(resultFile).use { workBook ->
        val sheet = workBook.getSheetAt(0)
        sheet.rowIterator().forEach { row ->
            row.cellIterator().forEach { cell ->
                if (cell != null && (cell.cellType == CellType.STRING)) {
                    val values = fields.filter { cell.stringCellValue.contains("\${${it.key}}") }
                    values.forEach {
                        val fieldValue =
                            if (it.value.toDoubleOrNull()?.toInt().toString() == it.value.toIntOrNull().toString()) {
                                it.value
                            } else {
                                it.value.toDoubleOrNull()?.let { it.toString().replace('.', ',') } ?: it.value
                            }

                        cell.setCellValue(cell.stringCellValue.replace("\${${it.key}}", fieldValue))
                    }

                    if (cell.stringCellValue.containsAll(metavariableParts)) {
                        cell.setCellValue(cell.stringCellValue.clearMetavariable())
                    }
                }
            }
        }
        val outStream = ByteArrayOutputStream()
        workBook.write(outStream)
        outStream.close()
    }

    return Paths.get(resultFile.absolutePath)
}

fun String.containsAll(other: String) =
    with(other.toHashSet()) { this@containsAll.forEach { this -= it }; this }.isEmpty()

private fun copyFileFromStream(inputStream: InputStream, dest: File) {
    inputStream.use { inputStream ->
        val fileOutputStream = FileOutputStream(dest)
        val buffer = ByteArray(1024)
        var length = inputStream.read(buffer)
        while (length > 0) {
            fileOutputStream.write(buffer, 0, length)
            length = inputStream.read(buffer)
        }
    }
}

fun String.clearMetavariable() = buildString {
    var isInsideMetavariable = false
    this@clearMetavariable.forEach {
        if (it == '$') {
            isInsideMetavariable = true
        }

        if (!isInsideMetavariable) append(it)

        if (it == '}') {
            isInsideMetavariable = false
        }
    }
}
