package com.example.ccgmultipicchose;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Administrator on 2016/7/26.
 */
public class MyAdapter extends CommonAdapter<String> {
    private CallBack mCallBack;
    private Context context;

    /**
     * 文件夹路径
     */
    private String mDirPath;

    public MyAdapter(Context context, List<String> mDatas, int itemLayoutId,
                     String dirPath, CallBack mCallBack) {
        super(context, mDatas, itemLayoutId);
        this.context = context;
        this.mDirPath = dirPath;

        this.mCallBack = mCallBack;
    }

    @Override
    public void convert(final ViewHolder helper, final String item, int position) {
        //设置no_pic
        helper.setImageResource(R.id.id_item_image, R.drawable.icon_dault_img);
        //设置no_selected
        helper.setImageResource(R.id.id_item_select,
                R.drawable.picture_unselected);
        //设置图片
        helper.setImageByUrl(R.id.id_item_image, mDirPath + "/" + item);

        final ImageView mImageView = helper.getView(R.id.id_item_image);
        final ImageView mSelect = helper.getView(R.id.id_item_select);

        mImageView.setColorFilter(null);
        //设置ImageView的点击事件
        mImageView.setOnClickListener(new View.OnClickListener() {
            //选择，则将图片变暗，反之则反之
            @Override
            public void onClick(View v) {
                int a = 0;//0为未选中或者撤销选择、、1为选中
                // 已经选择过该图片
                if (MainActivity.tempSelectBitmap.contains(mDirPath + "/" + item)) {
                    MainActivity.tempSelectBitmap.remove(mDirPath + "/" + item);
                    mSelect.setImageResource(R.drawable.picture_unselected);
                    mImageView.setColorFilter(null);
                    a = 0;
                } else
                // 未选择该图片
                {
                    if (MainActivity.tempSelectBitmap.size() < MainActivity.NUM+1) {


                        for (int i = 0; i < MainActivity.tempSelectBitmap.size(); i++) {
                            String path = MainActivity.tempSelectBitmap.get(i);
                            if (path.contains("camera_default")) {
                                MainActivity.tempSelectBitmap.remove(MainActivity.tempSelectBitmap.size() - 1);
                            }

                        }
                        MainActivity.tempSelectBitmap.add(mDirPath + "/" + item);
                        if (MainActivity.tempSelectBitmap.size() < MainActivity.NUM+1) {
                            MainActivity.tempSelectBitmap.add("camera_default");
                        }

                        mSelect.setImageResource(R.drawable.pictures_selected);
                        mImageView.setColorFilter(Color.parseColor("#77000000"));
                        a = 1;
                    } else {
                        Toast.makeText(context, "最多只能选择" + MainActivity.NUM + "张图片", Toast.LENGTH_SHORT).show();
                    }
                }
                mCallBack.clickItem(v, a);

            }
        });

        /**
         * 已经选择过的图片，显示出选择过的效果
         */
        if (MainActivity.tempSelectBitmap.contains(mDirPath + "/" + item)) {
            mSelect.setImageResource(R.drawable.pictures_selected);
            mImageView.setColorFilter(Color.parseColor("#77000000"));
        }

    }


}
