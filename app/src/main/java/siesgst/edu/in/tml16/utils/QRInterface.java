package siesgst.edu.in.tml16.utils;

/**
 * Created by vishal on 29/12/15.
 */
import android.graphics.Bitmap;
import android.graphics.Color;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

/**
 * Created by Srinath on 19-Dec-15.
 */
public class QRInterface {

    public Bitmap encodeQRcode(String data, int width, int height){
        QRCodeWriter writerInstance = new QRCodeWriter();
        try{
            BitMatrix matrix = writerInstance.encode(data,BarcodeFormat.QR_CODE,width,height);
            return toBitmap(matrix);
        }
        catch(WriterException exception){
            exception.printStackTrace();
        }
        return null;
    }

    public static Bitmap toBitmap(BitMatrix matrix){
        int height = matrix.getHeight();
        int width = matrix.getWidth();
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        int[] pixels = new int[width * height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                pixels[(y * width) + x] = matrix.get(x, y) ? Color.BLACK : Color.WHITE;
            }
        }
        bmp.setPixels(pixels,0,width,0,0,width,height);
        return bmp;
    }
}
