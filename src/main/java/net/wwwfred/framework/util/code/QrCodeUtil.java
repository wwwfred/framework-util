package net.wwwfred.framework.util.code;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import net.wwwfred.framework.util.io.IOUtil;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;

public class QrCodeUtil {

    /** 生成二维码 */
    public static byte[] encode(String content, String encoding)
    {
        Map<EncodeHintType, Object> map = new HashMap<EncodeHintType, Object>();
        if(!CodeUtil.isEmpty(encoding))
        {
            map.put(EncodeHintType.CHARACTER_SET, encoding);
        }
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, 100, 100,map);
        } catch (WriterException e) {
            throw new CodeException("new MultiFormatWriter illegal",e);
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            MatrixToImageWriter.writeToStream(bitMatrix, "png", out);
        } catch (IOException e) {
            throw new CodeException("writeToStream illegal",e);
        }
        IOUtil.closeOutputStream(out);
        return out.toByteArray();
    }
    
    /** 解析二维码 */
    public static String decode(byte[] imageData, String encoding)
    {
        InputStream in = new ByteArrayInputStream(imageData);
        Map<DecodeHintType, Object> map = new HashMap<DecodeHintType, Object>();
        if(CodeUtil.isEmpty(encoding))
        {
            map.put(DecodeHintType.CHARACTER_SET, encoding);
        }
        Result result;
        try {
            result = new MultiFormatReader().decode(new BinaryBitmap(new HybridBinarizer(
                    new BufferedImageLuminanceSource(ImageIO.read(in)))),map);
        } catch (Exception e) {
            throw new CodeException("decode illegal",e);
        }
        IOUtil.closeInputStream(in);
        return result.getText();
    }

    public static void main(String[] args) {
    
        byte[] imageData = IOUtil.readLoacalData("d:/fred", "r_test.png");
        System.out.println(decode(imageData, "UTF-8"));
        
        imageData = IOUtil.readLoacalData("d:/fred", "test.png");
        System.out.println(decode(imageData, "UTF-8"));
        
        imageData = IOUtil.readLoacalData("d:/fred", "wxpay.png");
        System.out.println(decode(imageData, "UTF-8"));
        
        imageData = IOUtil.readLoacalData("d:/fred", "alipay.png");
        String alipayContent = decode(imageData, "UTF-8");
        System.out.println(alipayContent);
        IOUtil.writeLocalData(encode(alipayContent, "UTF-8"),"d:/fred","alipay2.png");
        
        imageData = IOUtil.readLoacalData("d:/fred", "o2otest.png");
        System.out.println(decode(imageData, "UTF-8"));
        
        imageData = IOUtil.readLoacalData("d:/fred", "o2otest2.png");
        System.out.println(decode(imageData, "UTF-8"));
        
        imageData = IOUtil.readLoacalData("d:/fred", "fred.jpg");
        System.out.println(decode(imageData, "UTF-8"));
        
    }
    
}
