package com.example.ccgmultipicchose;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

/**
 * Created by Administrator on 2016/7/26.
 */
public class GridAdapter extends BaseAdapter {
    private static final String TAG = "GridAdapter";
    private Context context;




    public GridAdapter(Context context) {
        this.context = context;

    }


    @Override
    public int getCount() {

        return MainActivity.tempSelectBitmap.size();
    }

    @Override
    public Object getItem(int i) {
        return MainActivity.tempSelectBitmap.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.item_grid_image);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String path;
        if (MainActivity.tempSelectBitmap != null && position < MainActivity.tempSelectBitmap.size()) {
            path = MainActivity.tempSelectBitmap.get(position);
        } else {
            path = "camera_default";
        }

        if (path.contains("camera_default")) {
//            viewHolder.imageView.setImageResource(R.mipmap.icon_addpic_unfocused);
            MyApplication.getImageLoader(context).displayImage("mipmap://" + R.mipmap.icon_addpic_unfocused, viewHolder.imageView,new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.mipmap.icon_addpic_unfocused)
                    .showImageForEmptyUri(R.mipmap.icon_addpic_unfocused)
                    .showImageOnFail(R.mipmap.icon_addpic_unfocused)
                    .resetViewBeforeLoading(true).cacheOnDisk(true)
                    .cacheInMemory(true).bitmapConfig(Bitmap.Config.RGB_565)
                    .considerExifParams(true)
                    .displayer(new SimpleBitmapDisplayer()).build());
        } else {

            MyApplication.getImageLoader(context).displayImage("file://" + path, viewHolder.imageView);
        }

        return convertView;
    }

    public class ViewHolder {
        ImageView imageView;
    }
}
