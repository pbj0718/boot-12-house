package utils.waterMark;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.*;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class TestWaterPrint {
    public static void main(String[] args) throws DocumentException, IOException {
        // 要输出的pdf文件
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File("E:\\test1_temp.pdf")));
        // 将pdf文件先加水印然后输出
        setWatermark(bos, "E:\\test1.pdf", "内部参考  严禁外传");
    }

    /**
     * 页面上添加多行水印
     * @param bos 输出文件的位置
     * @param input
     *            原PDF位置
     * @param waterMarkName
     *            页脚添加水印
     * @throws DocumentException
     * @throws IOException
     */
    public static void setWatermark(BufferedOutputStream bos, String input, String waterMarkName)
            throws DocumentException, IOException {

        PdfReader reader = new PdfReader(input);
        PdfStamper stamper = new PdfStamper(reader, bos);

        // 获取总页数 +1, 下面从1开始遍历
        int total = reader.getNumberOfPages() + 1;
        // 使用classpath下面的字体库
        BaseFont base = null;
        try {
            base = BaseFont.createFont("static/fonts/STSONG.ttf", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
        } catch (Exception e) {
            // 日志处理
            e.printStackTrace();
        }

        // 间隔
        int interval = 50;
        // 获取水印文字的高度和宽度
        int textH = 0, textW = 0;
        JLabel label = new JLabel();
        label.setText(waterMarkName);
        FontMetrics metrics = label.getFontMetrics(label.getFont());
        textH = metrics.getHeight();
        textW = metrics.stringWidth(label.getText());
        System.out.println("textH: " + textH);
        System.out.println("textW: " + textW);

        // 设置水印透明度
        PdfGState gs = new PdfGState();
        gs.setFillOpacity(0.4f);
        gs.setStrokeOpacity(0.4f);

        Rectangle pageSizeWithRotation = null;
        PdfContentByte content = null;
        for (int i = 1; i < total; i++) {
            // 在内容上方加水印
            content = stamper.getOverContent(i);
            // 在内容下方加水印
            // content = stamper.getUnderContent(i);
            content.saveState();
            content.setGState(gs);

            // 设置字体和字体大小
            content.beginText();
            content.setFontAndSize(base, 20);

            // 获取每一页的高度、宽度
            pageSizeWithRotation = reader.getPageSizeWithRotation(i);
            float pageHeight = pageSizeWithRotation.getHeight();
            float pageWidth = pageSizeWithRotation.getWidth();

            // 根据纸张大小多次添加， 水印文字成30度角倾斜
            for (int height = interval + textH; height < pageHeight; height = height + textH * 6) {
                for (int width = interval + textW; width < pageWidth + textW; width = width + textW * 2) {
                    content.showTextAligned(Element.ALIGN_LEFT, waterMarkName, width - textW, height - textH, 55);
                }
            }

            content.endText();
        }

        // 关流
        stamper.close();
        reader.close();
    }


    /**
     * 在每页右下角生成倾斜水印
     * @param bos 输出文件的位置
     * @param input
     *            原PDF位置
     * @param waterMarkName
     *            页脚添加水印
     * @throws DocumentException
     * @throws IOException
     */
    public static void setWatermarkRightBottom(BufferedOutputStream bos, String input, String waterMarkName)
            throws DocumentException, IOException {

        PdfReader reader = new PdfReader(input);
        PdfStamper stamper = new PdfStamper(reader, bos);

        // 获取总页数 +1, 下面从1开始遍历
        int total = reader.getNumberOfPages() + 1;
        // 使用classpath下面的字体库
        BaseFont base = null;
        try {
            base = BaseFont.createFont("/calibri.ttf", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
        } catch (Exception e) {
            // 日志处理
            e.printStackTrace();
        }

        // 设置水印透明度
        PdfGState gs = new PdfGState();
        gs.setFillOpacity(0.4f);
        gs.setStrokeOpacity(0.4f);

        PdfContentByte content = null;
        for (int i = 1; i < total; i++) {
            // 在内容上方加水印
            content = stamper.getOverContent(i);
            // 在内容下方加水印
            // content = stamper.getUnderContent(i);
            content.saveState();
            content.setGState(gs);

            // 设置字体和字体大小
            content.beginText();
            content.setFontAndSize(base, 10);

            // 设置字体样式
            float ta = 1F, tb = 0F, tc = 0F, td = 1F, tx = 0F, ty = 0F;
            // 设置加粗(加粗)
            ta += 0.25F;
            td += 0.05F;
            ty -= 0.2F;
            // 设置倾斜(倾斜程序自己改)
            tc += 0.8F;
            content.setTextMatrix(ta, tb, tc, td, tx, ty);

            // 设置相对于左下角位置(向右为x，向上为y)
            content.moveText(300F, 5F);
            // 显示text
            content.showText(waterMarkName);

            content.endText();
            content.stroke();
            content.restoreState();
        }

        // 关流
        stamper.close();
        reader.close();
    }
}
