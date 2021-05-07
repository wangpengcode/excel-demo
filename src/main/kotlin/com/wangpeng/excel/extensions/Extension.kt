package com.wangpeng.excel.extensions

import com.wangpeng.excel.annotation.Column
import com.wangpeng.excel.annotation.ExcelColumn
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.math.BigDecimal
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime
import kotlin.jvm.Throws
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaField

fun excelResponseEntity(fileName: String, byteArray: ByteArray): ResponseEntity<ByteArray> {
	val name = String(fileName.toByteArray(Charsets.ISO_8859_1), StandardCharsets.UTF_8)
	return ResponseEntity.ok()
		.contentType(MediaType.APPLICATION_OCTET_STREAM)
		.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=$name.xlsx")
		.body(byteArray)
}

@Throws(IOException::class)
inline fun <reified T : Any> writeToExcel(data: List<T>?): ByteArray = try {
	val excelWorkBook = XSSFWorkbook()
	
	val sheet = excelWorkBook.createSheet(T::class.simpleName)
	sheet.defaultColumnWidth = 3766
	
	val headerFont = excelWorkBook.createFont()
	headerFont.color = IndexedColors.BLACK.getIndex()
	val headerCellStyle = excelWorkBook.createCellStyle()
	headerCellStyle.fillForegroundColor = IndexedColors.GREY_25_PERCENT.index
	headerCellStyle.setFont(headerFont)
	
	val headerRow = sheet.createRow(0) //initialize 1st row
	
	val map: HashMap<String, Int> = HashMap()
	var memberIndex = 0
	for (s in T::class.java.declaredFields) {
		val annotation = s.getAnnotation(ExcelColumn::class.java)
		annotation?.let {
			map[s.name] = memberIndex
			val cell = headerRow.createCell(memberIndex)
			cell.setCellValue(annotation.value)
			cell.cellStyle = headerCellStyle
			memberIndex += 1
		}
	}
	
	var rowIndex = 1
	data?.let {
		for (t in data) {
			val row = sheet.createRow(rowIndex++)
			
			val kClass = t::class
			kClass.memberProperties.forEach { property->
				val index = map[property.name]
				index?.let { idx->
					val value = property.getter.call(t).toString()
					row.createCell(idx).setCellValue(value)
				}
			}
		}
	}
	
	val stream = ByteArrayOutputStream()
	
	stream.use {
		excelWorkBook.write(it)
	}
	stream.toByteArray()
} catch (e: Exception) {
	println(e.stackTrace)
	throw e
}

fun <T : Any> excelToList(input: InputStream, clazz: KClass<T>): List<T> {
	return try {
		val workbook = XSSFWorkbook(input)
		val sheet = workbook.getSheetAt(0)
		val rows = sheet.iterator()
		val result = mutableListOf<T>()
		var rowNumber = 0
		while (rows.hasNext()) {
			val instance = clazz.createInstance()
			val pro = clazz.memberProperties
			val currentRow: Row = rows.next()
			if (rowNumber == 0) {
				rowNumber++
				continue
			}
			val cellsInRow = currentRow.iterator()
			var cellIdx = 0
			while (cellsInRow.hasNext()) {
				val currentCell = cellsInRow.next()
				pro.forEach { it1 ->
					val columnValue = it1.javaField?.getAnnotation(Column::class.java)?.value
					if (columnValue == cellIdx) {
						val kmp = it1 as KMutableProperty1<Any, Any?>
						
						when (it1.javaField?.type?.simpleName?.toLowerCase()) {
							"double" -> {
								kmp.set(instance, currentCell.numericCellValue)
							}
							"float" -> {
								kmp.set(instance, currentCell.numericCellValue.toFloat())
							}
							"long" -> {
								kmp.set(instance, currentCell.numericCellValue.toLong())
							}
							"bigdecimal" -> {
								kmp.set(instance, BigDecimal(currentCell.numericCellValue.toString()))
							}
							"int" -> {
								kmp.set(instance, currentCell.numericCellValue.toInt())
							}
							"string" -> {
								kmp.set(instance, currentCell.stringCellValue)
							}
							"boolean" -> {
								kmp.set(instance, currentCell.booleanCellValue)
							}
							"date" -> {
								kmp.set(instance, currentCell.dateCellValue)
							}
							"localdatetime" -> {
								kmp.set(instance, LocalDateTime.parse(currentCell.stringCellValue))
							}
							else -> {
								throw Exception("Not support type")
							}
						}
					}
				}
				cellIdx++
			}
			result.add(instance)
		}
		result
	} catch (e: Exception) {
		e.printStackTrace()
		throw e
	}
}
