package utils.waterMark;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class TextWaterMarking {
    //定义图片水印字体类型
    public static final String FONT_NAME = "微软雅黑";


    //定义图片水印字体加粗、变细、倾斜等样式
    public static final int FONT_STYLE = Font.BOLD;

    //设置字体大小
    public static final int FONT_SIZE = 80;

    //设置文字透明程度
    public static float ALPHA = 0.3F;

    /**
     * 给图片添加多个文字水印、可设置水印文字旋转角度
     * source 需要添加水印的图片路径
     * outPut 添加水印后图片输出路径
     * imageName 图片名称
     * imageType 图片类型
     * color 水印文字的颜色
     * word 水印文字
     * degree 水印文字旋转角度，为null表示不旋转
     */
    public static Boolean markImageByMoreText(String sourcePath, String outputPath, String imageName, String imageType, Color color, String word, Integer degree) {
        try {
            //读取原图片信息
            File file = new File(sourcePath);

            if (!file.isFile()) {
                return false;
            }

            //获取源图像的宽度、高度
            Image image = ImageIO.read(file);
            int width = image.getWidth(null);
            int height = image.getHeight(null);

            BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

            //创建绘图工具对象
            Graphics2D graphics2D = bufferedImage.createGraphics();
            //其中的0代表和原图位置一样
            graphics2D.drawImage(image, 0, 0, width, height, null);

            //设置水印文字（设置水印字体样式、粗细、大小）
            graphics2D.setFont(new Font(FONT_NAME, FONT_STYLE, FONT_SIZE));

            //设置水印颜色
            graphics2D.setColor(color);

            //设置水印透明度
            graphics2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, ALPHA));

            //设置水印旋转
            if (null != degree) {
                graphics2D.rotate(Math.toRadians(degree), (double) bufferedImage.getWidth() / 2, (double) bufferedImage.getHeight() / 2);
            }
            int len = graphics2D.getFontMetrics(graphics2D.getFont()).charsWidth(word.toCharArray(), 0, word.length());
            int x = width / 3;
            int y = FONT_SIZE;
            int space = height / FONT_SIZE;
            int widthSpace = width / FONT_SIZE;
            for (int i = 0; i < space; i++) {
                x = width / 3;
                //如果最后一个坐标的y轴比height高，直接退出
                if ((y + FONT_SIZE) > height) {
                    break;
                }
                for(int j = 0; j < widthSpace; j++){
                    if(x > width){
                        break;
                    }
                    //进行绘制
                    graphics2D.drawString(word, x, y);
                    x += len + FONT_SIZE;
                }
                y += (2 * FONT_SIZE);
            }

            graphics2D.dispose();

            //输出图片
            File sf = new File(outputPath, imageName + "." + imageType);
            // 保存图片
            ImageIO.write(bufferedImage, imageType, sf);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public static void main(String[] args) {
        boolean flag = markImageByMoreText("E:\\test3.jpeg","E:\\test","test3", "jpeg", new Color(0, 0, 0), "内部参考 严禁外传", -45);
        System.out.println(flag);
    }
}
