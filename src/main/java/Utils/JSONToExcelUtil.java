package Utils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 *  uni
 *  2022/07/05~2022/07/06
 *  将 JSON 转化为 Excel的工具类
 */
public class JSONToExcelUtil {

    /**
     * 读取绝对路径下的json文件
     * @param resourcePath json文件的绝对路径
     * @return json文件格式化后的字符串
     */
    public static String readJSONFile(String resourcePath) {
        try{
            // 1. 创建文件流
            File file = new File(resourcePath);
            // 2. 使用 common-lang3工具包, 以 UTF-8 格式读取文件, 转为字符串
            String str = FileUtils.readFileToString(file, "UTF-8");
            JSONObject jsonObject = JSONObject.parseObject(str);
            // 3. 将字符串转为标准的JSON格式的字符串
            return JSONObject.toJSONString(jsonObject, JSONWriter.Feature.WriteMapNullValue);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 创建 Sheet
     * @param layer 当前Sheet所在JSON中的层级
     * @param workbook 工作台 ( excel表格的主体 )
     * @param sheetName 当前页的名称
     * @param jsonArray JSON数组
     */
    public static void createSubSheet(int layer, XSSFWorkbook workbook, String sheetName, JSONArray jsonArray){
        // 创建新的 sheet
        XSSFSheet sheet = workbook.createSheet(sheetName);
        // 存储每个字段
        Map<String, Integer> map = new HashMap<>();
        // 统计当前的列
        int cellCount = 0;
        // 创建第一行
        XSSFRow firstRow = sheet.createRow(0);
        // 获取每一项
        for (int row = 1; row <= jsonArray.size(); row++) {
            JSONObject jsonObject = jsonArray.getJSONObject(row - 1);
            // 创建行
            XSSFRow currentRow = sheet.createRow(row);
            if(jsonObject != null){
                // 遍历每个KV
                for (String cellName : jsonObject.keySet()) {
                    // 列不存在时, 则创建列
                    if (!map.containsKey(cellName)) {
                        // 第一行创建列
                        XSSFCell firstRowCell = firstRow.createCell(cellCount);
                        firstRowCell.setCellValue(cellName);
                        map.put(cellName, cellCount++);
                    }
                    // 设置单元格
                    XSSFCell cell = currentRow.createCell(map.get(cellName));
                    // 获取 Value
                    String cellValue = JSON.toJSONString(jsonObject.get(cellName));

                    // 如果V为数组则递归创建sheet
                    if(JSON.isValidArray(cellValue)){
                        String subCellName = sheetName + "-" + cellName;
                        cell.setCellValue(subCellName);
                        createSubSheet(layer + 1, workbook,subCellName, jsonObject.getJSONArray(cellName));
                    }
                    else{
                        cell.setCellValue(jsonObject.getString(cellName));
                    }
                }
            } else{ // Value为一个数组
                JSONArray array = jsonArray.getJSONArray(row - 1);
                // 遍历数组
                if(array != null && array.size() > 0){
                    for (int i = 1; i <= array.size(); i++) {
                        JSONObject obj = array.getJSONObject(i - 1);
                        // 遍历 obj
                        for (String cellName : obj.keySet()) {
                            // 若列不存在则添加
                            if(!map.containsKey(cellName)){
                                XSSFCell cell = firstRow.createCell(cellCount);
                                map.put(cellName, cellCount++);
                                cell.setCellValue(cellName);
                            }
                            // 分情况讨论
                            String cellValue = obj.getString(cellName);
                            XSSFCell cell = currentRow.createCell(map.get(cellName));
                            // 如果值是JSON对象, 则递归创建
                            if(JSON.isValidObject(cellValue)){
                                String subSheetName = sheetName + "-" + cellName;
                                cell.setCellValue(subSheetName);
                                createSubSheet(layer+1, workbook, subSheetName , JSONObject.parseObject(cellValue));
                            } else if(JSON.isValidArray(cellValue)){
                                String subSheetName = sheetName + "-" + cellName;
                                cell.setCellValue(subSheetName);
                                createSubSheet(layer+1, workbook, subSheetName , JSONArray.parseArray(cellValue));
                            } else {
                                cell.setCellValue(cellValue);
                            }
                        }
                    }
                } else {
                    firstRow.createCell(0).setCellValue(sheetName);
                    XSSFCell cell = currentRow.createCell(cellCount);
                    cell.setCellValue(jsonArray.getString(row-1));
                }
            }
        }
    }

    /**
     * 创建 Sheet
     * @param layer 当前Sheet所在JSON中的层级
     * @param workbook 工作台 ( excel表格的主体 )
     * @param sheetName 当前页的名称
     * @param jsonObject JSON对象
     */
    public static void createSubSheet(int layer, XSSFWorkbook workbook, String sheetName, JSONObject jsonObject){
        // 创建新的 sheet
        XSSFSheet sheet = workbook.createSheet(sheetName);
        // 存储每个字段
        Map<String, Integer> map = new HashMap<>();
        // 统计当前的列
        int cellCount = 0;
        // 创建第一行
        XSSFRow fistRow = sheet.createRow(0);
        // 记录行数
        int row = 1;
        // 获取每一项
        // 创建行
        XSSFRow currentRow = sheet.createRow(row);
        // 遍历每个KV
        for (String cellName : jsonObject.keySet()) {
            // 列不存在时, 则创建列
            if (!map.containsKey(cellName)) {
                // 第一行创建列
                XSSFCell firstRowCell = fistRow.createCell(cellCount);
                firstRowCell.setCellValue(cellName);
                map.put(cellName, cellCount++);
            }
            // 设置单元格
            XSSFCell cell = currentRow.createCell(map.get(cellName));
            // 获取 Value
            String cellValue = JSON.toJSONString(jsonObject.get(cellName));
            // 如果V为对象则递归创建sheet
            if(JSON.isValidObject(cellValue)){
                String subCellName = "Sheet" + layer + "-" + sheetName + "-" + cellName;
                cell.setCellValue(subCellName);
                createSubSheet(layer + 1, workbook,subCellName, JSON.parseObject(cellValue));

            } else if(JSON.isValidArray(cellValue)){
                String subCellName = "Sheet" + layer + "-" + sheetName + "-" + cellName;
                cell.setCellValue(subCellName);
                createSubSheet(layer + 1, workbook,subCellName, JSON.parseArray(cellValue));
            }
            else{
                cell.setCellValue(jsonObject.getString(cellName));
            }
        }
    }

    /**
     * 将格式化的JSON字符串导出为Excel
     * @param jsonStr 格式化后的JSON字符串
     * @param savePath Excel保存路径
     * @param excelName Excel名称
     */
    public static void toExcelByString(String jsonStr, String savePath, String excelName){
        assert JSON.isValid(jsonStr) : "字符串: " + jsonStr + " 不是标准的JSON字符串";
        toExcelByJSONObject(JSONObject.parseObject(jsonStr),savePath, excelName);
    }

    /**
     * 将普通的Java对象导出为JSON文件
     * @param obj   Java对象
     * @param savePath  Excel保存路径
     * @param excelName Excel名称
     */
    public static void toExcelByObject(Object obj, String savePath, String excelName){
        String jsonStr = JSON.toJSONString(obj, JSONWriter.Feature.WriteMapNullValue);
        JSONObject jsonObject = JSONObject.parseObject(jsonStr);
        toExcelByJSONObject(jsonObject, savePath, excelName);
    }

    /**
     * 将本地的JSON文件导出为 Excel
     * @param resourcePath JSON文件的绝对路径
     * @param savePath  保存的路径
     * @param excelName 保存的Excel名称
     */
    public static void toExcelByLocalJSONFile(String resourcePath, String savePath, String excelName){
        // 1. 获取标准的 JSON 字符串
        String jsonStr = readJSONFile(resourcePath);
        // 验证字符串是否合法
        assert JSON.isValid(jsonStr) : "路径:[" + resourcePath + "] 的json文件不符合标准的JSON格式";
        toExcelByString(jsonStr, savePath, excelName);
    }

    /**
     * 将JSONObject转化导出到 Excel
     * 这里遵循递归导出，当遇到数组时会调用 createSheet创建新的页面。
     * @param jsonObject    JSON对象
     * @param savePath      Excel保存路径
     * @param excelName     Excel名称
     */
    public static void toExcelByJSONObject(JSONObject jsonObject,  String savePath, String excelName){
        try(XSSFWorkbook workbook = new XSSFWorkbook()){
            // 获取当前的Sheet
            XSSFSheet sheet = workbook.createSheet("sheet");
            // 获取第一行
            XSSFRow firstRow = sheet.createRow(0);
            // 记录Key所在的列
            Map<String, Integer> map = new HashMap<>();
            // 记录列数
            int cellCount = 0;
            // 遍历 JSON的key
            XSSFRow currentRow = sheet.createRow(1);
            for (String key : jsonObject.keySet()) {
                // 先处理列
                if(!map.containsKey(key)){  // 当列不存在则添加
                    map.put(key, cellCount);
                    XSSFCell cell = firstRow.createCell(cellCount++);
                    cell.setCellValue(key);
                }
                XSSFCell currentCell = currentRow.createCell(map.get(key));
                String jsonStr = jsonObject.getString(key);
                // 如果 Value为数组 则创建新的 Sheet
                if(JSON.isValidArray(jsonStr)){
                    String subSheetName = "Sheet-" + key;
                    createSubSheet(1, workbook, subSheetName, jsonObject.getJSONArray(key));
                    currentCell.setCellValue(subSheetName);
                } else if(JSON.isValidObject(jsonStr)){ // 如果当前 value 仍然是一个JSON对象
                    String subSheetName = "Sheet-" + key;
                    createSubSheet(1, workbook, subSheetName, jsonObject.getJSONObject(key));
                    currentCell.setCellValue(subSheetName);
                }
                else {
                    // 特殊处理空值
                    if(StringUtils.isEmpty(jsonStr))
                        currentCell.setCellValue("null");
                    else
                        currentCell.setCellValue(jsonStr);
                }
            }
            save(workbook, savePath, excelName);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    /**
     * 将 Excel对象保存到本地
     * @param workbook Excel对象
     * @param path Excel文件路径
     * @param excelName excel名称
     */
    public static void save(Workbook workbook, String path, String excelName){
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(path +"/" + excelName +".xlsx");
            workbook.write(fileOutputStream);
            fileOutputStream.close();
            System.out.println("保存完毕. 保存位置为[ " + path + "/" + excelName + " ]");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void main(String[] args) {
        String jsonPath = "D:\\Download\\jsondata.txt";
        String savePath = "D:\\";
        String excelName = "demo";
        // 测试1
        toExcelByLocalJSONFile(jsonPath, savePath, excelName + "1");
        String jsonStr = readJSONFile(jsonPath);
//        JSONObject jsonObject = JSONObject.parseObject(jsonStr);
//        Object object = JSON.parse(jsonStr);
        // 测试2
        toExcelByString(jsonStr, savePath, excelName + "2");
        // 测试3
//        toExcelByObject(object, savePath, excelName + "3");
//        // 测试4
//        toExcelByJSONObject(jsonObject, savePath, excelName + "4");
    }
}
