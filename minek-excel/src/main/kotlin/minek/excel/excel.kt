package minek.excel

import java.io.File
import java.io.OutputStream
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.*
import org.apache.poi.xssf.streaming.SXSSFWorkbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook

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

class Workbook(type: WorkbookType) {
    private val workbook: org.apache.poi.ss.usermodel.Workbook = when (type) {
        WorkbookType.HSSF -> HSSFWorkbook()
        WorkbookType.XSSF -> XSSFWorkbook()
        WorkbookType.SXSSF -> SXSSFWorkbook()
    }
    val styles = mutableMapOf<String, CellStyle>()

    fun richTextString(value: String): RichTextString {
        return workbook.creationHelper.createRichTextString(value)
    }

    fun sheet(sheetName: String? = null, init: Sheet.() -> Unit): Sheet =
        Sheet(
            this,
            if (sheetName != null) workbook.createSheet(sheetName) else workbook.createSheet()
        ).apply(init)

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

fun workbook(type: WorkbookType = WorkbookType.XSSF, init: Workbook.() -> Unit): Workbook = Workbook(
    type
).apply(init)

class Sheet(
    private val workbook: Workbook,
    private val sheet: org.apache.poi.ss.usermodel.Sheet
) {
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

    fun autoSizeColumn(columnIndex: Int) {
        sheet.autoSizeColumn(columnIndex)
    }
}

class Row(
    private val workbook: Workbook,
    private val row: org.apache.poi.ss.usermodel.Row
) {
    var height: Short
        set(value) {
            row.height = value
        }
        get() = row.height

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

class Cell(private val cell: org.apache.poi.ss.usermodel.Cell) {
    @Suppress("DEPRECATION")
    var cellType: CellType
        set(value) {
            cell.cellType = value
        }
        get() {
            return cell.cellType
        }
}
