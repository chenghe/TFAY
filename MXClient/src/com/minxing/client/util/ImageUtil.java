package com.minxing.client.util;

import java.io.File;

import android.content.Context;

import com.minxing.client.AppConstants;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class ImageUtil {
	public static void initImageEngine(Context context) {
		Context c = context.getApplicationContext();
		File cacheDir = new File(AppConstants.IMAGE_ENGINE_CACHE); // 图片持久化的位置(本地sdcard和机身ROM)
		ImageLoader imageLoader = ImageLoader.getInstance(); // 实例化图片加载引擎
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(c)

		.threadPoolSize(AppConstants.IMAGE_ENGINE_CORETHREAD) // 可以同步加载图片的个数
				.threadPriority(Thread.NORM_PRIORITY - 1) // 可设置当前线程的等级,在Thread.MAX_PRIORITY和Thread.MIN_PROIORITY之间
				.denyCacheImageMultipleSizesInMemory().memoryCache(new UsingFreqLimitedMemoryCache(AppConstants.IMAGE_ENGINE_FREQ_LIMITED_MEMECACHE)) // 设置缓存内存的大小,在此设置2M的缓存
				.diskCache(new UnlimitedDiscCache(cacheDir)) // 设置需要持久化的磁盘位置
				.diskCacheFileNameGenerator(new HashCodeFileNameGenerator()) // 文件名的生成策略、
				.defaultDisplayImageOptions(AppConstants.DEFAULT_IMAGE_OPTIONS) // 可以自定义默认设置
				.imageDownloader(new HttpsImageDownloader(context)).build();
		imageLoader.init(config);
	}
}
