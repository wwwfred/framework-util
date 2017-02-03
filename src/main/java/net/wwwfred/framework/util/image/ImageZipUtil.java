package net.wwwfred.framework.util.image;
import java.awt.Image;  
import java.awt.Toolkit;
import java.awt.image.BufferedImage;  
import java.awt.image.ColorConvertOp;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.io.File;  
import java.io.FileNotFoundException;  
import java.io.FileOutputStream;  
import java.io.IOException;  

import javax.imageio.ImageIO;



import java.awt.Graphics;
import java.awt.color.ColorSpace;
  

import com.sun.image.codec.jpeg.JPEGCodec;  
import com.sun.image.codec.jpeg.JPEGEncodeParam;  
import com.sun.image.codec.jpeg.JPEGImageEncoder;  
  
/**图片压缩
 * @author liq
 * @created 2014-10-17
 */
@SuppressWarnings("restriction")
public class ImageZipUtil {  
  
    /** 
     * 等比例压缩图片文件
     *  先保存原文件，再压缩、上传 
     * @param oldFile  要进行压缩的文件 
     * @param newFile  新文件 
     * @param width  宽度 //设置宽度时（高度传入0，等比例缩放） 
     * @param height 高度 //设置高度时（宽度传入0，等比例缩放） 
     * @param quality 质量 
     * @return 返回压缩后的文件的全路径 
     */  
    public static String zipImageFile(File oldFile,File newFile, int width, int height,  
            float quality) {  
        if (oldFile == null) {
            return null;  
        }  
        try {  
            /** 对服务器上的临时文件进行处理 */
            Image srcFile = ImageIO.read(oldFile);  
            int w = srcFile.getWidth(null);  
        //  System.out.println(w);  
            int h = srcFile.getHeight(null);  
        //  System.out.println(h);  
            double bili;  
            if(width>0){  
                bili=width/(double)w;  
                height = (int) (h*bili);  
            }else{  
                if(height>0){  
                    bili=height/(double)h;  
                    width = (int) (w*bili);  
                }else{
                	return null;
                }
            }  
            /** 宽,高设定 */  
            BufferedImage tag = new BufferedImage(width, height,  
                    BufferedImage.TYPE_INT_RGB);  
            tag.getGraphics().drawImage(srcFile, 0, 0, width, height, null);  
            //String filePrex = oldFile.getName().substring(0, oldFile.getName().indexOf('.'));  
            /** 压缩后的文件名 */  
            //newImage = filePrex + smallIcon+  oldFile.getName().substring(filePrex.length());  
  
            /** 压缩之后临时存放位置 */  
            FileOutputStream out = new FileOutputStream(newFile);  
  
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);  
            JPEGEncodeParam jep = JPEGCodec.getDefaultJPEGEncodeParam(tag);  
            /** 压缩质量 */  
            jep.setQuality(quality, true);
            encoder.encode(tag, jep);  
            out.close();  
  
        } catch (FileNotFoundException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        return newFile.getAbsolutePath();  
    }
  
    /** 
     * 按宽度高度压缩图片文件        需先保存原文件，再压缩、上传 
     * @param oldFile  要进行压缩的文件全路径 
     * @param newFile  新文件 
     * @param width  宽度 
     * @param height 高度 
     * @param quality 质量 
     * @return 返回压缩后的文件的全路径 
     */  
    public static String zipWidthHeightImageFile(File oldFile,File newFile, int width, int height,  
            float quality) {  
        if (oldFile == null) {  
            return null;  
        }  
        String newImage = null;  
        try {  
            /** 对服务器上的临时文件进行处理 */  
            Image srcFile = ImageIO.read(oldFile);
//            int w = srcFile.getWidth(null);
        //  System.out.println(w);  
//            int h = srcFile.getHeight(null);
        //  System.out.println(h);  
  
            /** 宽,高设定 */  
            BufferedImage tag = new BufferedImage(width, height,BufferedImage.TYPE_INT_RGB);  
            tag.getGraphics().drawImage(srcFile, 0, 0, width, height, null);  
            //String filePrex = oldFile.substring(0, oldFile.indexOf('.'));  
            /** 压缩后的文件名 */  
            //newImage = filePrex + smallIcon+ oldFile.substring(filePrex.length());  
  
            /** 压缩之后临时存放位置 */  
            FileOutputStream out = new FileOutputStream(newFile);  
  
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);  
            JPEGEncodeParam jep = JPEGCodec.getDefaultJPEGEncodeParam(tag);  
            /** 压缩质量 */  
            jep.setQuality(quality, true);  
            encoder.encode(tag, jep);  
            out.close();  
        } catch (FileNotFoundException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        return newImage;  
    }
    

    /**
     * 缩放图像
     * @param srcImageFile 源图像文件地址
     * @param result 缩放后的图像地址
     * @param scale 缩放比例
     * @param flag 缩放选择:true 放大; false 缩小;
     */
    public static void scale(String srcImageFile, String result, int scale,
            boolean flag) {
        try {
            BufferedImage src = ImageIO.read(new File(srcImageFile)); // 读入文件
            int width = src.getWidth(); // 得到源图宽
            int height = src.getHeight(); // 得到源图长
            if (flag) {
                // 放大
                width = width * scale;
                height = height * scale;
            } else {
                // 缩小
                width = width / scale;
                height = height / scale;
//      width=128;
//      height=160;
            }
            Image image = src.getScaledInstance(width, height,
                    Image.SCALE_DEFAULT);
            BufferedImage tag = new BufferedImage(width, height,
                    BufferedImage.TYPE_INT_RGB);
            Graphics g = tag.getGraphics();
            g.drawImage(image, 0, 0, null); // 绘制缩小后的图
            g.dispose();
            ImageIO.write(tag, "JPEG", new File(result));// 输出到文件流
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 图像切割
     * 
     * @param srcImageFile 源图像地址
     * @param descDir 切片目标文件夹
     * @param destWidth 目标切片宽度
     * @param destHeight 目标切片高度
     */
    public static void cut(String srcImageFile, String descDir, int destWidth,
            int destHeight) {
        try {
            Image img;
            ImageFilter cropFilter;
            // 读取源图像
            BufferedImage bi = ImageIO.read(new File(srcImageFile));
            int srcWidth = bi.getHeight(); // 源图宽度
            int srcHeight = bi.getWidth(); // 源图高度
            if (srcWidth > destWidth && srcHeight > destHeight) {
                Image image = bi.getScaledInstance(srcWidth, srcHeight,
                        Image.SCALE_DEFAULT);
                destWidth = 200; // 切片宽度
                destHeight = 150; // 切片高度
                int cols = 0; // 切片横向数量
                int rows = 0; // 切片纵向数量
                // 计算切片的横向和纵向数量
                if (srcWidth % destWidth == 0) {
                    cols = srcWidth / destWidth;
                } else {
                    cols = (int) Math.floor(srcWidth / destWidth) + 1;
                }
                if (srcHeight % destHeight == 0) {
                    rows = srcHeight / destHeight;
                } else {
                    rows = (int) Math.floor(srcHeight / destHeight) + 1;
                }
                // 循环建立切片
                // 改进的想法:是否可用多线程加快切割速度
                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < cols; j++) {
                        // 四个参数分别为图像起点坐标和宽高
                        // 即: CropImageFilter(int x,int y,int width,int height)
                        cropFilter = new CropImageFilter(j * 200, i * 150,
                                destWidth, destHeight);
                        img = Toolkit.getDefaultToolkit().createImage(
                                new FilteredImageSource(image.getSource(),
                                cropFilter));
                        BufferedImage tag = new BufferedImage(destWidth,
                                destHeight, BufferedImage.TYPE_INT_RGB);
                        Graphics g = tag.getGraphics();
                        g.drawImage(img, 0, 0, null); // 绘制缩小后的图
                        g.dispose();
                        // 输出为文件
                        ImageIO.write(tag, "JPEG", new File(descDir + "pre_map_" + i + "_" + j + ".jpg"));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 图像类型转换
     * GIF->JPG GIF->PNG PNG->JPG PNG->GIF(X)
     */
    public static void convert(String source, String result) {
        try {
            File f = new File(source);
            f.canRead();
            f.canWrite();
            BufferedImage src = ImageIO.read(f);
            ImageIO.write(src, "JPG", new File(result));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 彩色转为黑白
     * 
     * @param source
     * @param result
     */
    public static void gray(String source, String result) {
        try {
            BufferedImage src = ImageIO.read(new File(source));
            ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
            ColorConvertOp op = new ColorConvertOp(cs, null);
            src = op.filter(src, null);
            ImageIO.write(src, "JPEG", new File(result));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {

    }
}
