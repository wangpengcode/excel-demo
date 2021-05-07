package com.wangpeng.excel.application.services


import org.apache.poi.ss.usermodel.HorizontalAlignment
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.stereotype.Service
import com.wangpeng.excel.domain.interfaces.IReportServce
import java.io.ByteArrayOutputStream
import java.time.LocalDate


@Service
class ReportService : IReportServce {
    override fun generate(): ByteArray {
        val workbook: Workbook = XSSFWorkbook()
        val sheet: Sheet = workbook.createSheet("poc")

        val dateStyle = workbook.createCellStyle()
        dateStyle.alignment = HorizontalAlignment.RIGHT
        dateStyle.dataFormat = 14

        val header = sheet.createRow(0)
        header.createCell(0).setCellValue("String")
        header.createCell(1).setCellValue("Integer")
        header.createCell(2).setCellValue("Decimal")
        header.createCell(3).setCellValue("Date")

        val line = sheet.createRow(1)
        line.createCell(0).setCellValue("Text")
        line.createCell(1).setCellValue((9).toDouble())
        line.createCell(2).setCellValue((99.99).toDouble())

        val date = line.createCell(3)
        date.cellStyle = dateStyle
        date.setCellValue((LocalDate.now()))

        val anotherSheet: Sheet = workbook.createSheet("another")
        anotherSheet.createRow(0).createCell(0).setCellValue("Lorem ipsum")

        val stream = ByteArrayOutputStream()

        stream.use {
            workbook.write(it)
        }

        return stream.toByteArray()
    }


}