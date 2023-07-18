package com.wangpeng.excel

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.FileReader
import java.io.FileWriter


@SpringBootTest
class ExcelApplicationTests {

    @Test
    fun contextLoads() {
        val reader = BufferedReader(FileReader("/Users/wangpeng/Documents/工作相关/卡bin服务/Archive/1.csv"))
        var line: String? = null
        var sql: String? = null
        val out = BufferedWriter(FileWriter("1.sql",true))
        while ((reader.readLine().also { line = it }) != null) {
            sql = ""
            for (text in line!!.split(",")) {
                println(text)
                sql += "$text,"
            }
            out.write(sql)
            break
        }
        reader.close()
        out.close()
    }

}
