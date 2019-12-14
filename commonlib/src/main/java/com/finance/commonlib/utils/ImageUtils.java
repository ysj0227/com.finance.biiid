package com.finance.commonlib.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.text.TextUtils;
import android.util.Base64;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.ObjectKey;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ImageUtils {
    /**
     * 按宽/高缩放图片到指定大小并进行裁剪得到中间部分图片
     *
     * @param bitmap 源bitmap
     * @param w      缩放后指定的宽度
     * @param h      缩放后指定的高度
     */
    public static Bitmap zoomBitmap(Bitmap bitmap, int w, int h) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float scaleWidth, scaleHeight, x, y;
        Bitmap newbmp;
        Matrix matrix = new Matrix();
        if (width > height) {
            scaleWidth = ((float) h / height);
            scaleHeight = ((float) h / height);
            x = (width - w * height / h) / 2;// 获取bitmap源文件中x做表需要偏移的像数大小
            y = 0;
        } else if (width < height) {
            scaleWidth = ((float) w / width);
            scaleHeight = ((float) w / width);
            x = 0;
            y = (height - h * width / w) / 2;// 获取bitmap源文件中y做表需要偏移的像数大小
        } else {
            scaleWidth = ((float) w / width);
            scaleHeight = ((float) w / width);
            x = 0;
            y = 0;
        }
        matrix.postScale(scaleWidth, scaleHeight);
        try {
            // createBitmap()方法中定义的参数x+width要小于或等于bitmap.getWidth()，
            // y+height要小于或等于bitmap.getHeight()
            newbmp = Bitmap.createBitmap(bitmap, (int) x, (int) y, (int) (width - x),
                    (int) (height - y), matrix, true);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return newbmp;
    }

    //图片转化字节（不压缩）
    public static byte[] imgToByte(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    //图片转化字节（压缩小于100k）
    public static byte[] imgToByteCompress(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            //第一个参数 ：图片格式 ，第二个参数： 图片质量，100为最高，0为最差  ，第三个参数：保存压缩后的数据的流
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
            //Log.i("TAG","***********："+baos.toByteArray().length/1024+"_________"+options);
        }
        return baos.toByteArray();
    }

    //压缩图片小于100k
    public static Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            //第一个参数 ：图片格式 ，第二个参数： 图片质量，100为最高，0为最差  ，第三个参数：保存压缩后的数据的流
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    //裁剪图片
    public static Bitmap cropBitmap(Bitmap bitmap) {
        int w = bitmap.getWidth(); // 得到图片的宽，高
        int h = bitmap.getHeight();
        int cropWidth = w >= h ? h : w;// 裁切后所取的正方形区域边长
        cropWidth /= 2;
        int cropHeight = (int) (cropWidth / 1.2);
        return Bitmap.createBitmap(bitmap, w / 3, 0, cropWidth, cropHeight, null, false);
    }

    /**
     * 将字符串转换成Bitmap类型
     */
    public static Bitmap base64ToBitmap(String string) {
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray;
            bitmapArray = decodeFromBase64(string);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * Save the bitmap.
     *
     * @param src     The source of bitmap.
     * @param file    The file.
     * @param recycle True to recycle the source of bitmap, false otherwise.
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean save(final Bitmap src, final String file,
                               Bitmap.CompressFormat format, int quality, boolean recycle) {
        if (src == null) {
            return false;
        }
        boolean ret = false;
        try (OutputStream os = new BufferedOutputStream(new FileOutputStream(file))) {
            ret = src.compress(format, quality, os);
            if (recycle && !src.isRecycled()) {
                src.recycle();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * 保存文件到指定路径
     *
     * @param context context
     * @param bmp     bmp
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean saveImageToGallery(Context context, Bitmap bmp, int quality) {
        if (bmp == null) {
            return false;
        }
        String storePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "screen_shot";
        File appDir = new File(storePath);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            //通过io流的方式来压缩保存图片
            boolean isSuccess = bmp.compress(Bitmap.CompressFormat.JPEG, quality, fos);
            fos.flush();
            fos.close();
            //把文件插入到系统图库
            MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), fileName, null);
            //保存图片后发送广播通知更新数据库
            Uri uri = Uri.fromFile(file);
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
            return isSuccess;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    public static byte[] decodeFromBase64(String string) {
        return Base64.decode(string.getBytes(), Base64.NO_WRAP);//解密
    }

    /**
     * 背景虚化模糊（高斯模糊）
     */
    public static Bitmap rsBlur(Context context, Bitmap source, int radius, float scale) {
//        Log.i(TAG, "origin size:" + source.getWidth() + "*" + source.getHeight());
        int width = Math.round(source.getWidth() * scale);
        int height = Math.round(source.getHeight() * scale);
        Bitmap inputBmp = Bitmap.createScaledBitmap(source, width, height, false);
        RenderScript renderScript = RenderScript.create(context);
//        Log.i(TAG, "scale size:" + inputBmp.getWidth() + "*" + inputBmp.getHeight());
        // Allocate memory for Renderscript to work with
        final Allocation input = Allocation.createFromBitmap(renderScript, inputBmp);
        final Allocation output = Allocation.createTyped(renderScript, input.getType());
        // Load up an instance of the specific script that we want to use.
        ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
        script.setInput(input); //
        // Set the blur radius
        script.setRadius(radius); // Start the ScriptIntrinisicBlur
        script.forEach(output); // Copy the output to the blurred bitmap
        output.copyTo(inputBmp);
        renderScript.destroy();
        return inputBmp;
    }

    public static boolean imageToWebp(String fromPath, String toPath) {
        if (!TextUtils.isEmpty(fromPath) && !TextUtils.isEmpty(toPath)) {
            try {
                File file = new File(fromPath);
                FileInputStream stream = new FileInputStream(file);
                Bitmap bitmap = BitmapFactory.decodeStream(stream);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.WEBP, 80, baos);
                return writeFileFromByteArray(toPath, baos.toByteArray());
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * 写入文件
     */
    private static boolean writeFileFromByteArray(String filePath, byte[] data) {
        File file = new File(filePath);
        BufferedOutputStream bos;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(file));
            bos.write(data);
            bos.flush();
            bos.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static byte[] streamToBytes(InputStream in) {
        ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
        byte[] buffer = new byte[1024];
        int len = -1;
        try {
            while ((len = in.read(buffer)) >= 0) {
                out.write(buffer, 0, len);
                out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return out.toByteArray();
    }

    /**
     * glide加载图片
     *
     * @param forceRefresh 是否强制刷新，针对url不变的图片
     * @param failureImgId 加载图片失败后显示的图片
     */
    public static void loadImage(Context context, String url, ImageView view,
                                 boolean forceRefresh, int failureImgId) {
        if (TextUtils.isEmpty(url)) return;
        if (forceRefresh) {
            Glide.with(context).load(url).placeholder(failureImgId)
                    .signature(new ObjectKey(String.valueOf(System.currentTimeMillis())))
                    .diskCacheStrategy(DiskCacheStrategy.ALL).into(view);
        } else {
            Glide.with(context).load(url).placeholder(failureImgId)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).into(view);
        }
    }


//    /**
//     * @param base64Data base64Data
//     * @return res
//     */
//    public static Bitmap base64ToBitmap(String base64Data) {
//        byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
//        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//    }

}
