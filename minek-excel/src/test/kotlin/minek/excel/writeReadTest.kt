package minek.excel

import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import java.io.File

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class WriteTest {

    private val testFile = File("d:\\123123.xlsx")

    @Test
    @Order(1)
    fun writeTest() {
        workbook {
            style("bold", StyleOption(font = StyleOption.Font(bold = true)))
            sheet("sheetName") {
                row {
                    cell("hahaha1", "bold")
                    cell(richTextString("asdfsadf"))
                    cell(0.0, "bold")
                }
                row {
                    cell("hahaha2", "bold")
                    cell("testest2", "bold")
                }
                autoSizeColumn(0)
            }
            generate(testFile.outputStream())
        }
    }

    @Test
    @Order(2)
    fun readTest() {
        workbook(testFile) {
            sheet(0) {
                iterator().forEach { row ->
                    row.iterator().forEach { cell ->
                        println(cell)
                    }
                }
            }
        }
    }

}
