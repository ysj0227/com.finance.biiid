package com.finance.biiid.test;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.finance.biiid.R;
import com.finance.commonlib.base.BaseActivity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

/**
 * @author yangShiJie
 * @date 2019-11-08
 */
@EActivity(R.layout.activity_picture)
public class SelectPictureActivity extends BaseActivity {

    private final int TAKE_PHOTO = 100;
    private final int CHOOSE_PHOTO = 101;
    private final String PACKAGENAME_FILEPROVIDER = "${applicationId}.fileprovider";
    @ViewById(R.id.btn_take)
    Button buttonTake;
    @ViewById(R.id.btn_select)
    Button button;
    @ViewById(R.id.img)
    ImageView picture;
    Uri imageUri;


    @AfterViews
    void init() {

    }

    @Click(R.id.btn_take)
    void takepictureClick() {
        openCamera();
    }

    @Click(R.id.btn_select)
    void pictureClick() {
        getPhotoFromAlbum();
    }

    private void takePhoto() {
        //创建File对象，用于储存拍照后的图片
        File outputImage = new File(getExternalCacheDir(), "biiid_image.jpg");
        try {
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= 24) {
            imageUri = FileProvider.getUriForFile(SelectPictureActivity.this,
                    PACKAGENAME_FILEPROVIDER, outputImage);
        } else {
            imageUri = Uri.fromFile(outputImage);
        }
        //启动相机程序
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, TAKE_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        //将拍摄的照片显示出来
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        picture.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    handleImageOnKitKat(data);
                }
                break;
            default:
                break;
        }
    }
    private void openCamera() {
        if (ContextCompat.checkSelfPermission(SelectPictureActivity.this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(SelectPictureActivity.this, new String[]{Manifest.permission.CAMERA}, 1);
        } else {
            takePhoto();
        }
    }

    private void getPhotoFromAlbum() {
        if (ContextCompat.checkSelfPermission(SelectPictureActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(SelectPictureActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
        } else {
            openAlbum();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePhoto();
                } else {
                    Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
                case 2:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                }
        }
    }

    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO);
    }

    private void handleImageOnKitKat(Intent data) {
        String imagesPath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            //如果是document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagesPath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            imagesPath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            imagesPath = uri.getPath();
        }
        displayImage(imagesPath);
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        //通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void displayImage(String imagesPath) {
        if (imagesPath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagesPath);
            picture.setImageBitmap(bitmap);
        } else {
            Toast.makeText(this, "failed to get image", Toast.LENGTH_LONG).show();
        }
    }


}
