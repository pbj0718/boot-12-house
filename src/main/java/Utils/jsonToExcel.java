package Utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Set;

public class jsonToExcel {

    public static void main(String[] args) throws Exception {
        // TODO:从接口获取数据
//        String ret = HttpUtils.sendPost("url","param");
        String ret = "{\n" +
                "    \"DATA1\":[\n" +
                "        {\n" +
                "            \"BY_DATE\":\"263.38\",\n" +
                "            \"BY_MONTH\":\"4391.58\",\n" +
                "            \"BY_MONTH_INC\":\"-27.79%\",\n" +
                "            \"BY_YEAR\":\"20356.44\",\n" +
                "            \"BY_YEAR_INC\":\"-14.87%\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"BY_DATE\":\"91.81\",\n" +
                "            \"BY_MONTH\":\"1087.39\",\n" +
                "            \"BY_MONTH_INC\":\"-27.00%\",\n" +
                "            \"BY_YEAR\":\"5879.43\",\n" +
                "            \"BY_YEAR_INC\":\"-12.10%\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"BY_DATE\":\"40.29\",\n" +
                "            \"BY_MONTH\":\"533.51\",\n" +
                "            \"BY_MONTH_INC\":\"-30.89%\",\n" +
                "            \"BY_YEAR\":\"2937.50\",\n" +
                "            \"BY_YEAR_INC\":\"-27.67%\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"DATA2\":[\n" +
                "        {\n" +
                "            \"BY_DATE\":\"123.38\",\n" +
                "            \"BY_MONTH\":\"123.58\",\n" +
                "            \"BY_MONTH_INC\":\"-12.79%\",\n" +
                "            \"BY_YEAR\":\"12345.44\",\n" +
                "            \"BY_YEAR_INC\":\"-12.87%\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"BY_DATE\":\"12.81\",\n" +
                "            \"BY_MONTH\":\"1234.39\",\n" +
                "            \"BY_MONTH_INC\":\"-12.00%\",\n" +
                "            \"BY_YEAR\":\"1234.43\",\n" +
                "            \"BY_YEAR_INC\":\"-12.10%\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"BY_DATE\":\"98.29\",\n" +
                "            \"BY_MONTH\":\"987.51\",\n" +
                "            \"BY_MONTH_INC\":\"-98.89%\",\n" +
                "            \"BY_YEAR\":\"9876.50\",\n" +
                "            \"BY_YEAR_INC\":\"-89.67%\"\n" +
                "        }\n" +
                "    ]\n" +
                "}\n";
        System.out.println("接口返回参数：" + ret);

        // 替换表头
//        String replace = ret.replaceAll("BY_MONTH_INC", "当月同比").replaceAll("BY_YEAR_INC", "当年同比")
//                .replaceAll("BY_DATE", "当日(万元)").replaceAll("BY_MONTH", "当月(万元)")
//                .replaceAll("BY_YEAR", "当年(万元)")
//                // 也可以把JSON数组的节点名称替换掉，这样sheet页的名称也替换成了想要的文字
//                .replaceAll("DATA1", "数据一").replaceAll("DATA2", "数据二");
        JSONObject jsonObject = JSONObject.parseObject(ret, Feature.OrderedField);

        // 设置数组的节点名称
//        String[]ArrayKeys = new String[]{"DATA1","DATA2"};

        // 设置生成的文件名及路径
        String fileName = "D:/test.xlsx";

        // 调用转换方法
        jsonToExcel(fileName, jsonObject);

        System.out.println("生成文件成功："+fileName);
    }

    public static void jsonToExcel(String fileName, JSONObject jsonObject) throws Exception {
        // 创建HSSFWorkbook对象
        HSSFWorkbook wb = new HSSFWorkbook();

        // 多个数组的建成多个sheet
        for (String arrayKey : jsonObject.keySet()) {
            // 创建HSSFSheet对象
            HSSFSheet sheet = wb.createSheet(jsonObject.keySet().size() > 1 ? arrayKey : "sheet");
            Set keys = null;
            int rowNo = 0;
            HSSFRow row = sheet.createRow(0);

            JSONArray jsonArray = jsonObject.getJSONArray(arrayKey);
            //获取标题
            for (int i = 0; i < jsonArray.size(); i++) {
                if (keys == null) {
                    JSONObject item = jsonArray.getJSONObject(i);
                    keys = item.keySet();
                    for (Object s : keys) {
                        HSSFCell cell = row.createCell(rowNo++);
                        cell.setCellValue(s.toString());
                    }
                } else {
                    break;
                }
            }
            // 获取数据一次循环一行
            for (int i = 0; i < jsonArray.size(); i++) {
                rowNo = 0;
                JSONObject item = jsonArray.getJSONObject(i);
                row = sheet.createRow(i + 1);
                keys = item.keySet();
                for (Object s : keys) {
                    HSSFCell cell = row.createCell(rowNo++);
                    cell.setCellValue(item.getString(s.toString()));
                }
            }
        }
        // 创建Excel文件
        File file = new File(fileName);
        file.createNewFile();
        // 输出到Excel文件
        FileOutputStream output = new FileOutputStream(fileName);
        wb.write(output);

        wb.close();
        output.flush();
        output.close();
    }
}
