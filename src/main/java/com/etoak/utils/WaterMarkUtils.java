package com.etoak.utils;


import lombok.extern.slf4j.Slf4j;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.*;

@Slf4j
public class WaterMarkUtils {

    public static void main(String[] args) {
        setWordWaterMark("E:\\test11.docx", "E:\\test11_temp.docx", "内部参考 严禁外传");
    }

    /**
     * word文字水印 多行添加 需要重写poi方法
     *
     * @param inputPath 文档路径
     * @param outPath 输出路径
     * @param markStr 水印文字
     */
    public static void setWordWaterMark(String inputPath, String outPath, String markStr) {
        File inputFile = new File(inputPath);
        //2003doc 用HWPFDocument  ； 2007doc 用 XWPFDocument
        XWPFDocument doc = null;
        try {
            // 延迟解析比率
            ZipSecureFile.setMinInflateRatio(-1.0d);
            doc = new XWPFDocument(new FileInputStream(inputFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        XWPFHeaderFooterPolicy headerFooterPolicy = doc.getHeaderFooterPolicy();
        if (headerFooterPolicy == null) {
            headerFooterPolicy = doc.createHeaderFooterPolicy();
        }
        //添加水印
        headerFooterPolicy.createWatermark(markStr);
        File file = new File(outPath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        OutputStream os = null;
        try {
            os = new FileOutputStream(outPath);
            doc.write(os);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (doc != null) {
                try {
                    doc.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
