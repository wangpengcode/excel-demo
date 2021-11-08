package com.wangpeng.excel.controller

import com.wangpeng.excel.annotation.Column
import com.wangpeng.excel.annotation.ExcelColumn
import com.wangpeng.excel.extensions.excelResponseEntity
import com.wangpeng.excel.extensions.excelToList
import com.wangpeng.excel.extensions.writeToExcel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartHttpServletRequest
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*


@RestController
class ReportController {
	
	@GetMapping("/download")
	fun download(): ResponseEntity<ByteArray> {
		
		val list = listOf(
			A("123", BigDecimal.TEN),
			A("778", BigDecimal.TEN)
		)
		val a = writeToExcel(list)
		
		
		return excelResponseEntity(
			LocalDateTime.now().toString(), a
		)
	}
	
	@PostMapping("/upload")
	fun uploadFile(request: MultipartHttpServletRequest): ResponseEntity<String> {
		val file = request.getFiles("file")[0]
		val list = excelToList(
			file.inputStream,
			Template::class
		)
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
		
		override fun toString(): String {
			return "Template(aa='$aa', bb='$bb', cc=$cc, dd=$dd, ee=$ee, ff=$ff, gg=$gg, hh=$hh, kk=$kk, mm=$mm)"
		}
	}
}

data class A(
	@ExcelColumn("序号")
	val id: String,
	@ExcelColumn("金额")
	val amount: BigDecimal
)