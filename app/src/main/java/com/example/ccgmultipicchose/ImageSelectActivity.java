package com.example.ccgmultipicchose;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ccgmultipicchose.widget.ListImageDirPopupWindow;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Administrator on 2016/7/26.
 */
public class ImageSelectActivity extends FragmentActivity implements View.OnClickListener, ListImageDirPopupWindow.OnImageDirSelected, CallBack {

    private ImageView image_select_back;
    private RelativeLayout id_bottom_ly;
    private TextView text_select_gallary, text_select_sure;
    private GridView grid_select_pic;
    private MyAdapter mAdapter;
    private int mScreenHeight;
    private ProgressDialog mProgressDialog;
    /**
     * 临时的辅助类，用于防止同一个文件夹的多次扫描
     */
    private HashSet<String> mDirPaths = new HashSet<String>();
    /*
    * 扫描拿到所有的图片文件夹
    */
    private List<ImageFloder> mImageFloders = new ArrayList<ImageFloder>();

    private int totalCount = 0;
    /**
     * 存储文件夹中的图片数量
     */
    private int mPicsSize;
    /**
     * 图片数量最多的文件夹
     */
    private File mImgDir;

    /**
     * 所有的图片
     */
    private List<String> mImgs;
    /**
     * 选择完图片后仍然保留的图片
     */


    private ListImageDirPopupWindow mListImageDirPopupWindow;
    private TextView bottom_text;


    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            mProgressDialog.dismiss();
            // 为View绑定数据
            data2View();

            // 初始化展示文件夹的popupWindw
            initListDirPopupWindw();///////////////////////////
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_select);
        // 得到屏幕的高度
        DisplayMetrics outMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        mScreenHeight = outMetrics.heightPixels;
        initView();
        getImages();

        text_select_sure.setText(MainActivity.tempSelectBitmap.size() - 1 + "/" + MainActivity.NUM + "确定");
    }

    public void initView() {
        image_select_back = (ImageView) findViewById(R.id.image_select_back);
        id_bottom_ly = (RelativeLayout) findViewById(R.id.id_bottom_ly);
        text_select_gallary = (TextView) findViewById(R.id.text_select_gallary);
        text_select_sure = (TextView) findViewById(R.id.text_select_sure);
        grid_select_pic = (GridView) findViewById(R.id.grid_select_pic);
        bottom_text = (TextView) findViewById(R.id.bottom_text);
        image_select_back.setOnClickListener(this);
        text_select_gallary.setOnClickListener(this);
        text_select_sure.setOnClickListener(this);
    }

    /**
     * 利用ContentProvider扫描手机中的图片，此方法在运行在子线程中 完成图片的扫描，最终获得jpg最多的那个文件夹
     */
    private void getImages() {
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, "暂无外部存储", Toast.LENGTH_SHORT).show();
            return;
        }
        // 显示进度条
        mProgressDialog = ProgressDialog.show(this, null, "正在加载...");
        // 开启一个线程读取图片
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 第一张图片的路径
                String firstImage = null;
                // 查询图片
                Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                // 获取一个ContentResolver实例ContentProvider
                ContentResolver mContentResolver = ImageSelectActivity.this
                        .getContentResolver();

                // 只查询jpeg和png的图片（缩略图）
                Cursor mCursor = mContentResolver.query(mImageUri, null,
                        MediaStore.Images.Media.MIME_TYPE + "=? or "
                                + MediaStore.Images.Media.MIME_TYPE + "=?",
                        new String[]{"image/jpeg", "image/png"},
                        MediaStore.Images.Media.DATE_MODIFIED);

                // Log.e("TAG", mCursor.getCount() + "");
                while (mCursor.moveToNext()) {
                    // 获取图片的路径
                    String path = mCursor.getString(mCursor
                            .getColumnIndex(MediaStore.Images.Media.DATA));

                    // Log.e("TAG", path);
                    // 拿到第一张图片的路径
                    if (firstImage == null)
                        firstImage = path;
                    // 获取该图片的父路径名
                    File parentFile = new File(path).getParentFile();
                    if (parentFile == null)
                        continue;
                    String dirPath = parentFile.getAbsolutePath();

                    ImageFloder imageFloder = null;
                    // 利用一个HashSet防止多次扫描同一个文件夹（不加这个判断，图片多起来还是相当恐怖的~~）
                    if (mDirPaths.contains(dirPath)) {
                        continue;
                    } else {
                        mDirPaths.add(dirPath);
                        // 初始化imageFloder
                        imageFloder = new ImageFloder();
                        imageFloder.setDir(dirPath);
                        imageFloder.setFirstImagePath(path);
                    }
                    // 得到图片的总数

                    String[] pic = parentFile.list(new FilenameFilter() {
                        @Override
                        public boolean accept(File dir, String filename) {
                            if (filename.endsWith(".jpg")
                                    || filename.endsWith(".png")
                                    || filename.endsWith(".jpeg"))
                                return true;
                            return false;
                        }
                    });
                    if (pic == null) {

                    } else {
                        int picSize = pic.length;
                        totalCount += picSize;

                        imageFloder.setCount(picSize);
                        mImageFloders.add(imageFloder);

                        if (picSize > mPicsSize) {
                            mPicsSize = picSize;
                            mImgDir = parentFile;
                        }
                    }

                }
                mCursor.close();

                // 扫描完成，辅助的HashSet也就可以释放内存了
                mDirPaths = null;
                // 通知Handler扫描图片完成
                mHandler.sendEmptyMessage(0x110);
            }
        }).start();
    }

    /**
     * 为View绑定数据
     */
    private void data2View() {
        if (mImgDir == null) {
//            Toast.makeText(getApplicationContext(), "抱歉，一张图片也没有找到",
//                    Toast.LENGTH_SHORT).show();
            bottom_text.setVisibility(View.VISIBLE);
            return;
        }

        mImgs = Arrays.asList(mImgDir.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                if (filename.endsWith(".jpg") || filename.endsWith(".png")
                        || filename.endsWith(".jpeg"))
                    return true;
                return false;
            }
        }));

        /**
         * 可以看到文件夹的路径和图片的路径分开保存，极大的减少了内存的消耗；
         */
        mAdapter = new MyAdapter(ImageSelectActivity.this, mImgs,
                R.layout.image_grid_item, mImgDir.getAbsolutePath(), this);///////////
//        MyAdapter.setmSelectedImage(leaveList);//设置保留的图片显示
        grid_select_pic.setAdapter(mAdapter);
    }

    /**
     * 初始化展示文件夹的popupWindw
     */
    private void initListDirPopupWindw() {
        mListImageDirPopupWindow = new ListImageDirPopupWindow(
                ViewGroup.LayoutParams.MATCH_PARENT, (int) (mScreenHeight * 0.7),
                mImageFloders, LayoutInflater.from(getApplicationContext())
                .inflate(R.layout.image_select_lis, null));

        mListImageDirPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                // 设置背景颜色变暗
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1.0f;
                getWindow().setAttributes(lp);
            }
        });
        // 设置选择文件夹的回调
        mListImageDirPopupWindow.setOnImageDirSelected(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_select_back:
                finish();
                break;

            case R.id.text_select_gallary:
                mListImageDirPopupWindow.showAsDropDown(id_bottom_ly);

                break;
            case R.id.text_select_sure:

                Intent intent = getIntent();
                setResult(RESULT_OK, intent); // 设置结果

                ImageSelectActivity.this.finish(); // 结束该Activity


                break;
            default:
                break;
        }

    }

    @Override
    public void selected(ImageFloder floder) {

        mImgDir = new File(floder.getDir());// 得到文件路径
        // 得到所有图片，只得到jpg和png以及jpeg格式的图片
        mImgs = Arrays.asList(mImgDir.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                if (filename.endsWith(".jpg") || filename.endsWith(".png")
                        || filename.endsWith(".jpeg"))
                    return true;
                return false;
            }
        }));

        /**
         * 可以看到文件夹的路径和图片的路径分开保存，极大的减少了内存的消耗；
         */
        mAdapter = new MyAdapter(ImageSelectActivity.this, mImgs,
                R.layout.image_grid_item, mImgDir.getAbsolutePath(), this);
        grid_select_pic.setAdapter(mAdapter);
        // mAdapter.notifyDataSetChanged();
        text_select_gallary.setText(floder.getName());
        mListImageDirPopupWindow.dismiss();

    }

    /**
     * 实时监听已经选择的照片
     */
    private int select = 0;


    @Override
    public void clickItem(View v, int position) {

        text_select_sure.setText(MainActivity.tempSelectBitmap.size() - 1 + "/" + MainActivity.NUM + "+确定");
    }
}
