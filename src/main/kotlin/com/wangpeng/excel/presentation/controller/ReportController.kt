package com.wangpeng.excel.presentation.controller

import com.wangpeng.excel.annotation.Column
import com.wangpeng.excel.annotation.ExcelColumn
import com.wangpeng.excel.domain.interfaces.IReportServce
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartHttpServletRequest
import java.io.InputStream
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaField


@RestController
class ReportController(val reportService: IReportServce) {
	
	@GetMapping("/excel2")
	fun getSheet(): ResponseEntity<ByteArray> {
		
		val list = listOf(
			A("123", BigDecimal.TEN),
			A("778", BigDecimal.TEN)
		)
		val a = writeToExcel(list)
		
		
		return excelResponseEntity(
			LocalDateTime.now().toString(), a
		)
	}
	
	@PostMapping("/excel3")
	fun uploadFile(request: MultipartHttpServletRequest): ResponseEntity<String> {
		val file = request.getFiles("file")[0]
		val list = excelToList(file.inputStream,
			Template::class)
		list?.forEach(::println)
		return ResponseEntity.status(HttpStatus.OK).body("$list")
	}
	
	class Template {
		@Column(0)
		var aa: String = ""
		
		@Column(1)
		var bb: String = ""
		
		@Column(2)
		var cc: Int = 0
		
		@Column(3)
		var dd: Boolean = true
		
		@Column(4)
		lateinit var ee: BigDecimal
		
		@Column(5)
		lateinit var ff: LocalDateTime
		
		@Column(6)
		var gg: Double = 0.0
		
		@Column(7)
		var hh: Long = 0
		
		@Column(8)
		var kk: Float = 0.0f
		
		@Column(9)
		lateinit var mm: Date
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
					rowNumber ++
					continue
				}
				val cellsInRow = currentRow.iterator()
				var cellIdx = 0
				while (cellsInRow.hasNext()) {
					var currentCell = cellsInRow.next()
					pro.forEach { it1 ->
						val columnValue = it1.javaField?.getAnnotation(Column::class.java)?.value
						if (columnValue == cellIdx) {
							val kmp = it1 as KMutableProperty1<Any, Any?>
							println("excelToList javaField ${it1.name} ${it1.javaField?.type?.simpleName}")
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
					cellIdx ++
				}
				result.add(instance)
			}
			result
		} catch (e: Exception) {
			e.printStackTrace()
			throw e
		}
	}
}

data class A(
	@ExcelColumn("序号")
	val id: String,
	@ExcelColumn("金额")
	val amount: BigDecimal
)