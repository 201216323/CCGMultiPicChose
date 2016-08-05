package com.example.ccgmultipicchose;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by Administrator on 2016/7/26.
 */
public abstract class CommonAdapter<T> extends BaseAdapter {
    protected LayoutInflater mInflater;
    protected Context mContext;
    protected List<T> mDatas;
    protected final int mItemLayoutId;

    /**
     * 构造器，传入需要的参数和进行初始化操作
     *
     * @param context
     *            上下文
     * @param mDatas
     *            数据
     * @param itemLayoutId
     *            布局的ID
     */
    public CommonAdapter(Context context, List<T> mDatas, int itemLayoutId) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(mContext);
        this.mDatas = mDatas;
        this.mItemLayoutId = itemLayoutId;
    }

    @Override
    public int getCount() {
        return mDatas!=null?mDatas.size():0;// 得到数据的大小
//		mList!=null?mList.size():0;
    }

    @Override
    public T getItem(int position) {
        //图片的位置是根据拍摄日期从小到大进行排序的
        //要得到正序就采用mDatas.size()-position-1
        //得到反序就采用position
        return mDatas.get(mDatas.size()-position-1); // 得到数据的位置
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder = getViewHolder(position, convertView,
                parent);
        convert(viewHolder, getItem(position),position);
        return viewHolder.getConvertView();

    }

    /**
     * 抽象方法实现holder持有的View对象
     *
     * @param helper
     *            ViewHolder
     * @param item
     *            数据
     */
    public abstract void convert(ViewHolder helper, T item,int position);

    private ViewHolder getViewHolder(int position, View convertView,
                                     ViewGroup parent) {
        return ViewHolder.get(mContext, convertView, parent, mItemLayoutId,
                position);
    }

}
