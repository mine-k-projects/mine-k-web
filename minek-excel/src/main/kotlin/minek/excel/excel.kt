package minek.excel

import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.*
import org.apache.poi.xssf.streaming.SXSSFWorkbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@Suppress("SpellCheckingInspection")
enum class WorkbookType(val extension: String) {
    HSSF("xls"), XSSF("xlsx"), SXSSF("xlsx")
}

data class StyleOption(
    val font: Font = Font(),
    val alignment: HorizontalAlignment? = null,
    val verticalAlignment: VerticalAlignment? = null,
    val border: Border = Border(),
    val format: String? = null,
    val wrapText: Boolean = false
) {
    companion object {
        const val CURRENCY = "#,##0"
        const val PERCENTAGE = "0.00"
    }

    data class Border(
        val top: BorderStyle = BorderStyle.NONE,
        val bottom: BorderStyle = BorderStyle.NONE,
        val left: BorderStyle = BorderStyle.NONE,
        val right: BorderStyle = BorderStyle.NONE
    ) {
        companion object {
            fun of(borderStyle: BorderStyle): Border {
                return Border(
                    borderStyle,
                    borderStyle,
                    borderStyle,
                    borderStyle
                )
            }
        }
    }

    data class Font(
        val name: String? = null,
        val color: IndexedColors? = null,
        val bold: Boolean = false,
        val height: Short? = null,
        val italic: Boolean = false,
        val strikeout: Boolean = false,
        val underline: Underline = Underline.NONE
    ) {
        enum class Underline(val byte: Byte) {
            NONE(org.apache.poi.ss.usermodel.Font.U_NONE),
            SINGLE(org.apache.poi.ss.usermodel.Font.U_SINGLE),
            DOUBLE(org.apache.poi.ss.usermodel.Font.U_DOUBLE)
        }
    }
}

class Workbook(
    private val workbook: org.apache.poi.ss.usermodel.Workbook
) : org.apache.poi.ss.usermodel.Workbook by workbook {

    val styles = mutableMapOf<String, CellStyle>()

    fun richTextString(value: String): RichTextString {
        return workbook.creationHelper.createRichTextString(value)
    }

    /**
     * read sheet by index number
     */
    fun sheet(index: Int, init: Sheet.() -> Unit): Sheet {
        return Sheet(this, getSheetAt(index)).apply(init)
    }

    /**
     * read sheet by name. if empty, create a sheet
     */
    fun sheet(sheetName: String, init: Sheet.() -> Unit): Sheet {
        return Sheet(this, getSheet(sheetName) ?: workbook.createSheet(sheetName)).apply(init)
    }

    /**
     * create sheet
     */
    fun sheet(init: Sheet.() -> Unit): Sheet {
        return Sheet(this, workbook.createSheet()).apply(init)
    }

    fun style(styleName: String, option: StyleOption) {
        styles[styleName] = workbook.createCellStyle().apply {
            this.setFont(workbook.createFont().apply {
                bold = option.font.bold
                if (option.font.height != null) {
                    fontHeight = option.font.height
                }
                if (option.font.name != null) {
                    fontName = option.font.name
                }
                if (option.font.color != null) {
                    color = option.font.color.index
                }
                italic = option.font.italic
                strikeout = option.font.strikeout
                underline = option.font.underline.byte
            })
            if (option.alignment != null) {
                this.alignment = option.alignment
            }
            if (option.verticalAlignment != null) {
                this.verticalAlignment = option.verticalAlignment
            }
            if (option.format != null) {
                this.dataFormat = workbook.createDataFormat().getFormat(option.format)
            }
            this.wrapText = option.wrapText

            this.borderTop = option.border.top
            this.borderBottom = option.border.bottom
            this.borderLeft = option.border.left
            this.borderRight = option.border.right
        }
    }

    fun generate(path: String) = generate(File(path))

    fun generate(path: File) = generate(path.outputStream())

    fun generate(os: OutputStream) = workbook.use { os.use { workbook.write(os) } }
}

/**
 * use to write
 */
fun workbook(type: WorkbookType = WorkbookType.XSSF, init: Workbook.() -> Unit): Workbook = Workbook(
    when (type) {
        WorkbookType.HSSF -> HSSFWorkbook()
        WorkbookType.XSSF -> XSSFWorkbook()
        WorkbookType.SXSSF -> SXSSFWorkbook()
    }
).apply(init)

/**
 * use to read
 */
fun workbook(file: File, type: WorkbookType = WorkbookType.XSSF, init: Workbook.() -> Unit): Workbook {
    return workbook(file.inputStream(), type, init)
}

/**
 * use to read
 */
fun workbook(`is`: InputStream, type: WorkbookType = WorkbookType.XSSF, init: Workbook.() -> Unit): Workbook = Workbook(
    when (type) {
        WorkbookType.HSSF -> HSSFWorkbook(`is`)
        WorkbookType.XSSF -> XSSFWorkbook(`is`)
        WorkbookType.SXSSF -> throw UnsupportedOperationException()
    }
).apply(init)

class Sheet(
    private val workbook: Workbook,
    private val sheet: org.apache.poi.ss.usermodel.Sheet
) : org.apache.poi.ss.usermodel.Sheet by sheet {

    fun row(
        @Suppress("SpellCheckingInspection") rownum: Int = sheet.physicalNumberOfRows,
        height: Short = sheet.defaultRowHeight,
        init: Row.() -> Unit
    ): Row {
        return Row(
            workbook,
            sheet.createRow(rownum).apply {
                this.height = height
            }).apply(init)
    }

    fun columnWidth(columnIndex: Int, width: Int) {
        sheet.setColumnWidth(columnIndex, width)
    }
}

class Row(
    private val workbook: Workbook,
    private val row: org.apache.poi.ss.usermodel.Row
) : org.apache.poi.ss.usermodel.Row by row {

    fun cell(
        value: String?,
        style: String? = null,
        column: Int = row.physicalNumberOfCells,
        init: (Cell.() -> Unit)? = null
    ): Cell {
        return cell(column, value, style, init)
    }

    fun cell(
        value: RichTextString,
        style: String? = null,
        column: Int = row.physicalNumberOfCells,
        init: (Cell.() -> Unit)? = null
    ): Cell {
        return cell(column, value, style, init)
    }

    fun cell(
        value: Number?,
        style: String? = null,
        defaultZero: Boolean = true,
        column: Int = row.physicalNumberOfCells,
        init: (Cell.() -> Unit)? = null
    ): Cell {
        return cell(column, value?.toDouble() ?: if (defaultZero) 0.0 else null, style, init)
    }

    private fun cell(column: Int, value: Any?, style: String? = null, init: (Cell.() -> Unit)? = null): Cell {
        val cell = row.createCell(column)
        if (value == null) {
            cell.setCellValue("")
        } else {
            when (value) {
                is Date -> cell.setCellValue(value)
                is String -> cell.setCellValue(value)
                is Number -> cell.setCellValue(value.toDouble())
                is Boolean -> cell.setCellValue(value)
                is LocalDate -> cell.setCellValue(value)
                is LocalDateTime -> cell.setCellValue(value)
                is RichTextString -> cell.setCellValue(value)
                else -> cell.setCellValue(value.toString())
            }
        }
        if (style != null) {
            cell.cellStyle = workbook.styles[style]
        }
        return Cell(cell).apply {
            if (init != null) {
                init()
            }
        }
    }
}

class Cell(private val cell: org.apache.poi.ss.usermodel.Cell) : org.apache.poi.ss.usermodel.Cell by cell