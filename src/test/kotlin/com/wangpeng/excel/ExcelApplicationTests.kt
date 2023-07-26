package com.wangpeng.excel

import org.junit.jupiter.api.Test
import org.junit.platform.commons.util.StringUtils
import org.springframework.boot.test.context.SpringBootTest
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.FileReader
import java.io.FileWriter


@SpringBootTest
class ExcelApplicationTests {

    @Test
    fun contextLoads() {
        val reader = BufferedReader(FileReader("/Users/wangpeng/Documents/工作相关/卡bin服务/new-csv/4.csv"))
        var line: String? = null
        var sql: String? = null
        val out = BufferedWriter(FileWriter("/Users/wangpeng/Documents/工作相关/卡bin服务/Archive/4.sql", true))
        try {
            while ((reader.readLine().also { line = it }) != null) {
                sql = "INSERT INTO fintech_cashier.cy_cardbin_raw_data (start_card_bin_field_1, end_card_bin_field_2, maybe_country_field_3, card_brand_field_4, issuer_field_5, country_field_6, country_code_field_7, country_field_8, field_9, currency_field_10, ddc_field_11, field_12, field_13, field_14, field_15, field_16) VALUES("
                val list = line!!.split(",")
                if (list.size < 16) {
                    println("error ++++++++++ list size below 16")
                    continue
                }
                for (i in 1 until 17) {
                    sql += if (StringUtils.isBlank(list[i])) {
                       null + ","
                    } else if (i < 16){
                        "'"+list[i].replace("'","") + "',"
                    } else {
                        "'"+list[i].replace("'","") + "'"
                    }
                }
                sql +="); \n"
                sql = sql.replace(",)",")")
                sql = sql.replace(",,",",")
                println(sql)
                out.write(sql)
                out.flush()
            }

        } catch (e: Exception) {
            println(e)
        } finally {
            out.close()
            reader.close()
        }
    }

}
