package com.cc5.btt.util;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.util.SAXHelper;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import java.io.InputStream;
import java.util.*;

/**
 * 读取 .xlsx 文件的帮助类
 */
public class ExcelReader extends DefaultHandler {

    private int startRow = 1;

    private int currentRow = 1;

    private StylesTable stylesTable;

    private SharedStringsTable sst;

    private boolean nextIsString;
    // 上一个单元格的内容
    private String lastContents;

    private boolean isBlankSheet = true;
    // 一行数据的列表
    private Map<Integer, String> rowList = new LinkedHashMap<>();
    private Map<Integer, String> headerIndex = new LinkedHashMap<>();

    // 定义一个 List 用来保存所有内容
    private List<Map<String, String>> rows = new ArrayList<>();

    private List<Map<Integer, String>> remainRows = new LinkedList<>();

    // 定义前一个单元格和当前单元格的位置，用来计算其中空的单元格数量
    private String preRef = null, ref = null;

    private CellDataType nextDataType = CellDataType.SSTINDEX;
    private final DataFormatter formatter = new DataFormatter();
    private short formatIndex;
    private String formatString;

    // 用一个枚举表示单元格可能的数据类型
    protected enum CellDataType {
        BOOL, ERROR, FORMULA, INLINESTR, SSTINDEX, NUMBER, DATE, NULL
    }

    public ExcelReader() {}

    public ExcelReader(int startRow) {
        this.startRow = startRow;
    }

    public boolean isBlankSheet() {
        return isBlankSheet;
    }

    public List<Map<String, String>> getRows() {
        return this.rows;
    }

    public List<Map<Integer, String>> getRemainRows() {
        return this.remainRows;
    }

    public Collection<String> getHeaders() {
        return headerIndex.values();
    }

    public Map<Integer, String> getHeaderMap(){
        return headerIndex;
    }

    /**
     * 处理第一个sheet
     *
     */
    public void processAllSheets(InputStream filename) throws Exception {
        OPCPackage pkg = OPCPackage.open(filename);
        XSSFReader r = new XSSFReader(pkg);
        stylesTable = r.getStylesTable();
        SharedStringsTable sst = r.getSharedStringsTable();
        XMLReader parser = fetchSheetParser(sst);
        parser.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        parser.setFeature("http://xml.org/sax/features/external-general-entities", false);
        parser.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        InputStream sheet = r.getSheetsData().next();
        InputSource sheetSource = new InputSource(sheet);
        parser.parse(sheetSource);
        sheet.close();
    }

    /**
     * 获取解析器
     *
     */
    private XMLReader fetchSheetParser(SharedStringsTable sst) throws SAXException, ParserConfigurationException {
        XMLReader parser = SAXHelper.newXMLReader();
        this.sst = sst;
        parser.setContentHandler(this);
        return parser;
    }

    /**
     * 解析一个单元格开始时触发事件
     */
    public void startElement(String uri, String localName, String name, Attributes attributes) {
        // c => cell
        if (name.equals("c")) {
            // 前一个单元格的位置
            if (preRef == null) {
                preRef = attributes.getValue("r");
            }
            // 当前单元格的位置
            ref = attributes.getValue("r");
            this.setNextDataType(attributes);
            // 找出这个值是否是SST中的一个索引
            String cellType = attributes.getValue("t");
            nextIsString = cellType != null && cellType.equals("s");
        }
        // 清除上一个单元格的内容
        lastContents = "";
    }

    /**
     * 获取单元格的文本数据
     */
    public void characters(char[] ch, int start, int length) {
        lastContents = lastContents.concat(new String(ch, start, length));
    }

    /**
     * 解析一个单元格结束时触发事件
     */
    public void endElement(String uri, String localName, String name) {

        // if a cell type is s, then should get value from shared string table.
        if (nextIsString) {
            int idx = Integer.parseInt(lastContents);
            lastContents = new XSSFRichTextString(sst.getEntryAt(idx)).toString();
            nextIsString = false;
        }

        if (Objects.equals(name, "t")) {
            rowList.put(getColumn(ref), lastContents);
            preRef = ref; //prevent missing blank cell
        } else if (name.equals("v")) { // v => value of a cell
            String value = this.getDataValue(lastContents.trim());
            rowList.put(getColumn(ref), value);
            preRef = ref; //prevent missing blank cell
        } else {
            // <row> means end of a row.
            if (name.equals("row")) {
                isBlankSheet = false;
                if (currentRow == startRow) {
                    boolean isBlank = true;
                    for (int i = 1; i <= rowList.size(); i ++) {
                        if (StringUtils.isNotEmpty(rowList.get(i)))
                            isBlank = false;
                        headerIndex.put(i, rowList.get(i));
                    }
                    if (rowList.size() == 0 || isBlank)
                        throw new RuntimeException("Blank Excel uploaded.");
                } else if (currentRow > startRow) {
                    boolean isBlankRow = true;
                    for (String value: rowList.values()) {
                        if (StringUtils.isNotEmpty(value))
                            isBlankRow = false;
                    }
                    if (isBlankRow) return;
                    Map<String, String> row = new LinkedHashMap<>();
                    for (int i = 1; i <= headerIndex.size(); i ++) {
                        String value = rowList.get(i);
                        row.put(headerIndex.get(i), value == null ? "" : value.trim());
                    }
                    rows.add(row);
                } else {
                    remainRows.add(new LinkedHashMap<>(rowList));
                }
                rowList.clear();
                preRef = null;
                ref = null;
                currentRow ++;
            }
        }
    }

    /**
     * 根据单元格属性设置数据类型
     *
     */
    private void setNextDataType(Attributes attributes) {
        nextDataType = CellDataType.NUMBER;
        formatIndex = -1;
        formatString = null;
        String cellType = attributes.getValue("t");
        String cellStyleStr = attributes.getValue("s");
        if (null != cellType) {
            switch (cellType) {
                case "b":
                    nextDataType = CellDataType.BOOL;
                    break;
                case "e":
                    nextDataType = CellDataType.ERROR;
                    break;
                case "s":
                    nextDataType = CellDataType.SSTINDEX;
                    break;
                case "inlineStr":
                    nextDataType = CellDataType.INLINESTR;
                    break;
                case "str":
                    nextDataType = CellDataType.FORMULA;
                    break;
            }
        }

        if (cellStyleStr != null) {
            int styleIndex = Integer.parseInt(cellStyleStr);
            XSSFCellStyle style = stylesTable.getStyleAt(styleIndex);
            formatIndex = style.getDataFormat();
            formatString = style.getDataFormatString();
            if (Objects.equals("m/d/yy", formatString)) {
//                nextDataType = CellDataType.DATE;
//                //"yyyy-MM-dd hh：mm：ss.SSS"
                formatString = "yyyy-MM-dd";
            }
            if (formatString == null) {
                nextDataType = CellDataType.NULL;
                formatString = BuiltinFormats.getBuiltinFormat(formatIndex);
            }
        }
    }

    /**
     * 根据数据类型获取数据
     */
    private String getDataValue(String value) {
        String thisStr;
        switch (nextDataType) {
            // follow the steps below. 
            case BOOL:
                char first = value.charAt(0);
                thisStr = first == '0' ? "FALSE" : "TRUE";
                break;
            case ERROR:
                thisStr = "\"ERROR:" + value + '"';
                break;
            case FORMULA:
                thisStr = value;
                break;
            case INLINESTR:
                XSSFRichTextString rtsi = new XSSFRichTextString(value);
                thisStr = rtsi.toString();
                break;
            case SSTINDEX:
                thisStr = value;
                break;
            case NUMBER:
                if (formatString != null) {
                    thisStr = formatter.formatRawCellContents(Double.parseDouble(value), formatIndex, formatString).trim();
                } else {
                    thisStr = value.trim();
                }
                break;
            default:
                thisStr = "";
                break;
        }
        return thisStr;
    }

    private int getColumn(String content) {
        content = content.replaceAll("\\d+", "");
        int num;
        int result = 0;
        int length = content.length();
        for(int i = 0; i < length; i++) {
            char ch = content.charAt(length - i - 1);
            num = ch - 'A' + 1;
            num *= Math.pow(26, i);
            result += num;
        }
        return result;
    }


}
