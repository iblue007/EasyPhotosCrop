package com.huantansheng.easyphotos.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.huantansheng.easyphotos.EasyPhotos;
import com.huantansheng.easyphotos.R;
import com.huantansheng.easyphotos.models.album.entity.Photo;
import com.huantansheng.easyphotos.ui.widget.ClipImageView;
import com.huantansheng.easyphotos.utils.String.StringUtils;
import com.huantansheng.easyphotos.utils.file.ImageUtil;
import com.huantansheng.easyphotos.utils.system.VersionUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import androidx.fragment.app.Fragment;

public class ClipImageActivity extends Activity {

    private FrameLayout btnConfirm;
    private FrameLayout btnBack;
    private ClipImageView imageView;
    private int mRequestCode;
    private boolean isCameraImage;
    private float cropRatio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        setContentView(R.layout.activity_clip_image);

        //  ImageSelectorActivity.openActivity(this, mRequestCode, config);
        Intent intent = getIntent();
        if (intent != null) {
            mRequestCode = intent.getIntExtra("requestCode", 0);
            EasyPhotosActivity.start(this, mRequestCode);
            setStatusBarColor();
            initView();
        } else {
            finish();
        }
    }

    /**
     * 修改状态栏颜色
     */
    private void setStatusBarColor() {
        if (VersionUtils.isAndroidL()) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#373c3d"));
        }
    }

    private void initView() {
        imageView = findViewById(R.id.process_img);
        btnConfirm = findViewById(R.id.btn_confirm);
        btnBack = findViewById(R.id.btn_back);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageView.getDrawable() != null) {
                    btnConfirm.setEnabled(false);
                    confirm(imageView.clipImage());
                }
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

      //  imageView.setRatio(cropRatio);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null && requestCode == mRequestCode) {
            ArrayList<Photo> resultPhotos = data.getParcelableArrayListExtra(EasyPhotos.RESULT_PHOTOS);
//            ArrayList<String> images = data.getStringArrayListExtra(ImageSelector.SELECT_RESULT);
//            isCameraImage = data.getBooleanExtra(ImageSelector.IS_CAMERA_IMAGE, false);
            Bitmap bitmap = ImageUtil.decodeSampledBitmapFromFile(this, resultPhotos.get(0).uri, 720, 1080);
            if (bitmap != null) {
                imageView.setBitmapData(bitmap);
            } else {
                finish();
            }
        } else {
            finish();
        }
    }

    private void confirm(Bitmap bitmap) {
        String imagePath = null;
        if (bitmap != null) {
            String name = DateFormat.format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.getDefault())).toString();
            String path = ImageUtil.getImageCacheDir(this);
            imagePath = ImageUtil.saveImage(bitmap, path, name);
            bitmap.recycle();
            bitmap = null;
        }

        if (!TextUtils.isEmpty(imagePath)) {
            ArrayList<String> selectImages = new ArrayList<>();
            selectImages.add(imagePath);
            Intent intent = new Intent();
            intent.putStringArrayListExtra(EasyPhotos.SELECT_RESULT, selectImages);
           // intent.putExtra(ImageSelector.IS_CAMERA_IMAGE, isCameraImage);
            setResult(RESULT_OK, intent);
        }
        finish();
    }

    /**
     * 启动图片选择器
     *
     * @param activity
     * @param requestCode
     */
    public static void openActivity(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, ClipImageActivity.class);
        intent.putExtra("requestCode", requestCode);
        activity.startActivityForResult(intent, requestCode);
    }

//    /**
//     * 启动图片选择器
//     *
//     * @param fragment
//     * @param requestCode
//     * @param config
//     */
//    public static void openActivity(Fragment fragment, int requestCode, RequestConfig config) {
//        Intent intent = new Intent(fragment.getActivity(), ClipImageActivity.class);
//        intent.putExtra(ImageSelector.KEY_CONFIG, config);
//        fragment.startActivityForResult(intent, requestCode);
//    }
//
//    /**
//     * 启动图片选择器
//     *
//     * @param fragment
//     * @param requestCode
//     * @param config
//     */
//    public static void openActivity(android.app.Fragment fragment, int requestCode, RequestConfig config) {
//        Intent intent = new Intent(fragment.getActivity(), ClipImageActivity.class);
//        intent.putExtra(ImageSelector.KEY_CONFIG, config);
//        fragment.startActivityForResult(intent, requestCode);
//    }
}
