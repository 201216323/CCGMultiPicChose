package com.example.ccgmultipicchose;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import java.io.File;


public class MyApplication extends Application {

    private static ImageLoader imageloader;
    private static DisplayImageOptions options;



    @Override
    public void onCreate() {
        super.onCreate();

    }

    public static ImageLoader getImageLoader(Context context) {
        if (imageloader == null) {
            File cacheDir = new File(CacheNameUtil.getCacheDir(context)); // 缓存文件夹路径
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                    context)
                    .memoryCacheExtraOptions(480, 800)
                    // default = device screen dimensions 内存缓存文件的最大长宽
                    .diskCacheExtraOptions(480, 800, null)
                    // 本地缓存的详细信息(缓存的最大长宽)，最好不要设置这个
                    // .taskExecutor(...)
                    // .taskExecutorForCachedImages(...)
                    .threadPoolSize(3)
                    // default 线程池内加载的数量
                    .threadPriority(Thread.NORM_PRIORITY - 2)
                    // default 设置当前线程的优先级
                    .tasksProcessingOrder(QueueProcessingType.FIFO)
                    // default
                    .denyCacheImageMultipleSizesInMemory()
                    .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                    // 可以通过自己的内存缓存实现
                    .memoryCacheSize(2 * 1024 * 1024)
                    // 内存缓存的最大值
                    .memoryCacheSizePercentage(13)
                    // default
                    .diskCache(new UnlimitedDiskCache(cacheDir))
                    // default 可以自定义缓存路径
                    .diskCacheSize(50 * 1024 * 1024)
                    // 50 Mb sd卡(本地)缓存的最大值
                    .diskCacheFileCount(100)
                    // 可以缓存的文件数量
                    // default为使用HASHCODE对UIL进行加密命名， 还可以用MD5(new
                    // Md5FileNameGenerator())加密
                    .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                    .imageDownloader(new BaseImageDownloader(context)) // default
                    // .imageDecoder(new BaseImageDecoder(false)) // default
                    .defaultDisplayImageOptions(
                            DisplayImageOptions.createSimple()) // default
                    .writeDebugLogs() // 打印debug log
                    .build(); // 开始构建
            imageloader = ImageLoader.getInstance();
            imageloader.init(config);
        }
        return imageloader;
    }





    /***
     * 简单options
     * 加载图片的时候加一个这个Option不会出现图片杂乱问题
     * 默认图片是方形的啡哈健身
     *
     * @param context
     * @return
     */
    public static DisplayImageOptions getSimpleImageOptions(Context context) {
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.ic_launcher)
                .showImageForEmptyUri(R.mipmap.ic_launcher)
                .showImageOnFail(R.mipmap.ic_launcher)
                .resetViewBeforeLoading(true).cacheOnDisk(true)
                .cacheInMemory(true).bitmapConfig(Bitmap.Config.RGB_565)
                .considerExifParams(true)
                .displayer(new SimpleBitmapDisplayer()).build();

        return options;
    }
}
