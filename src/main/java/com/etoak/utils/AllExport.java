package com.etoak.utils;

import com.alibaba.excel.util.FileUtils;
import com.lowagie.text.Element;
import com.lowagie.text.pdf.*;
import org.apache.poi.ooxml.POIXMLDocumentPart;
import org.apache.poi.openxml4j.opc.PackagePartName;
import org.apache.poi.openxml4j.opc.PackageRelationship;
import org.apache.poi.openxml4j.opc.TargetMode;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRelation;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFHeader;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.xmlbeans.XmlObject;

import javax.imageio.ImageIO;
import javax.xml.namespace.QName;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.regex.Pattern;

public class AllExport {

    public static void main(String[] args) throws Exception {
        addWaterMarkWhenDownload("E:\\test11.docx", "E:\\test11_temp.docx", "内部资料 严禁外传");
        //addWaterMarkWhenDownload("E:\\test1.pdf", "E:\\test1_temp.pdf", "内部资料 严禁外传");
    }

    public static void addWaterMarkWhenDownload(String inputSrc, String outputSrc, String waterMark) throws Exception {
        File file = new File(inputSrc);
        String fileName = file.getName();
        byte[] bytes = FileUtils.readFileToByteArray(file);

        if (fileName.toLowerCase().endsWith("pdf")) {
            addPDFWaterMark(bytes, outputSrc, waterMark);
        } else if (fileName.toLowerCase().endsWith("xlsx")) {
            addExcelWaterMark(bytes, outputSrc, waterMark);
        } else if (fileName.toLowerCase().endsWith("docx")) {
            addWordWaterMark(bytes, outputSrc, waterMark);
        }
    }

    /**
     * pdf文件添加文字水印
     *
     * @param fileData      原始文件字节数组
     * @param outSrc        文件输出路径
     * @param waterMarkName 水印内容
     */
    private static void addPDFWaterMark(byte[] fileData, String outSrc, String waterMarkName) throws Exception {

        PdfReader reader = null;
        PdfStamper stamper = null;
        try {
            reader = new PdfReader(fileData);
            stamper = new PdfStamper(reader, new FileOutputStream(outSrc));
            File file = new File("static/fonts/STSong.ttf");
            System.out.println(file.getPath());
            BaseFont base = BaseFont.createFont("static/fonts/STSONG.ttf", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
            PdfGState gs = new PdfGState();
            gs.setFillOpacity(0.3f);
            gs.setStrokeOpacity(0.4f);
            PdfContentByte under = null;
            com.lowagie.text.Rectangle pageRect = null;
            for (int i = 1, n = reader.getNumberOfPages(); i <= n; i++) {
                pageRect = reader.getPageSizeWithRotation(i);
                // 计算水印X,Y坐标
                float x = pageRect.getWidth() / 2;
                float y = pageRect.getHeight() / 2;
                under = stamper.getOverContent(i);
                under.saveState();
                under.setGState(gs);
                under.beginText();
                under.setFontAndSize(base, 20);
                under.showTextAligned(Element.ALIGN_CENTER, waterMarkName, x, y, 30);
                under.endText();
            }
        } finally { // 关闭流
            if (stamper != null) {
                stamper.close();
            }
            if (reader != null) {
                reader.close();
            }
        }
    }
    /**
     * word文件添加文字水印
     *
     * @param fileData      原始文件字节数组
     * @param outputSrc     文件输出路径
     * @param waterMarkName 水印内容
     */
    private static void addWordWaterMark(byte[] fileData, String outputSrc, String waterMarkName) throws Exception {
        InputStream input = new ByteArrayInputStream(fileData);
        XWPFDocument doc = new XWPFDocument(input);
        XWPFHeaderFooterPolicy headerFooterPolicy = doc.createHeaderFooterPolicy();
        //添加文字水印
        headerFooterPolicy.createWatermark(waterMarkName);
        XWPFHeader header = headerFooterPolicy.getHeader(XWPFHeaderFooterPolicy.DEFAULT);
        XWPFParagraph paragraph = header.getParagraphArray(0);
        paragraph.getCTP().newCursor();
        XmlObject[] xmlobjects = paragraph.getCTP().getRArray(0).getPictArray(0).selectChildren(new QName("urn:schemas-microsoft-com:vml", "shape"));
        if (xmlobjects.length > 0) {
            com.microsoft.schemas.vml.CTShape ctshape = (com.microsoft.schemas.vml.CTShape) xmlobjects[0];
            //设置水印颜色
            ctshape.setFillcolor("#828282");
            //修改水印样式
            ctshape.setStyle(getWaterMarkStyle(ctshape.getStyle(), 33) + ";rotation:315");
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            doc.write(out);
        } finally {
            out.close();
        }
        out.writeTo(new BufferedOutputStream(new FileOutputStream(outputSrc)));
    }

    /**
     * 设置水印格式
     * word
     *
     * @param styleStr
     * @param height
     * @return
     */
    public static String getWaterMarkStyle(String styleStr, double height) {
        Pattern p = Pattern.compile(";");
        String[] strs = p.split(styleStr);
        for (String str : strs) {
            if (str.startsWith("height:")) {
                String heightStr = "height:" + height + "pt";
                styleStr = styleStr.replace(str, heightStr);
                break;
            }
        }
        return styleStr;
    }

    /**
     * excel添加水印
     *
     * @param fileData      原始文件字节数组
     * @param outputSrc     生成文件路径
     * @param waterMarkName 水印内容
     */
    private static void addExcelWaterMark(byte[] fileData, String outputSrc, String waterMarkName) throws Exception {

        BufferedImage image = createWaterMarkImage(waterMarkName);
        // 导出到字节流B
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(image, "png", os);
        InputStream sbs = new ByteArrayInputStream(fileData);
        XSSFWorkbook workbook = new XSSFWorkbook(sbs);
        int pictureIdx = workbook.addPicture(os.toByteArray(), Workbook.PICTURE_TYPE_PNG);
        POIXMLDocumentPart poixmlDocumentPart = workbook.getAllPictures().get(pictureIdx);

        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {//获取每个Sheet表
            XSSFSheet sheet = workbook.getSheetAt(i);
            PackagePartName ppn = poixmlDocumentPart.getPackagePart().getPartName();
            String relType = XSSFRelation.IMAGES.getRelation();
            PackageRelationship pr = sheet.getPackagePart().addRelationship(ppn, TargetMode.INTERNAL, relType, null);
            sheet.getCTWorksheet().addNewPicture().setId(pr.getId());
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            workbook.write(bos);
        } finally {
            bos.close();
        }
        bos.writeTo(new BufferedOutputStream(new FileOutputStream(outputSrc)));
    }
    /**
     * 创建水印图片
     * excel
     *
     * @param waterMark 水印内容
     * @return
     */
    public static BufferedImage createWaterMarkImage(String waterMark) {
        String[] textArray = waterMark.split("\n");
        Font font = new Font("microsoft-yahei", Font.PLAIN, 20);
        Integer width = 500;
        Integer height = 200;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        // 背景透明 开始
        Graphics2D g = image.createGraphics();
        image = g.getDeviceConfiguration().createCompatibleImage(width, height, Transparency.TRANSLUCENT);
        g.dispose();
        // 背景透明 结束
        g = image.createGraphics();
        g.setColor(new Color(Integer.parseInt("#C5CBCF".substring(1), 16)));// 设定画笔颜色
        g.setFont(font);// 设置画笔字体
        g.shear(0.1, -0.26);// 设定倾斜度
        // 设置字体平滑
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int y = 150;
        for (int i = 0; i < textArray.length; i++) {
            g.drawString(textArray[i], 0, y);// 画出字符串
            y = y + font.getSize();
        }
        g.dispose();// 释放画笔
        return image;
    }
}
