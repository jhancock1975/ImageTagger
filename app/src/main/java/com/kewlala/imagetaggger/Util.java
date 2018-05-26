package com.kewlala.imagetaggger;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;

public class Util {
    public static Bitmap getbitpam(String path){
        Bitmap imgthumBitmap=null;
        try
        {

            final int THUMBNAIL_SIZE = 64;

            FileInputStream fis = new FileInputStream(path);
            imgthumBitmap = BitmapFactory.decodeStream(fis);

            imgthumBitmap = Bitmap.createScaledBitmap(imgthumBitmap,
                    THUMBNAIL_SIZE, THUMBNAIL_SIZE, false);

            ByteArrayOutputStream bytearroutstream = new ByteArrayOutputStream();
            imgthumBitmap.compress(Bitmap.CompressFormat.JPEG, 100,bytearroutstream);


        }
        catch(Exception ex) {

        }
        return imgthumBitmap;
    }
}
