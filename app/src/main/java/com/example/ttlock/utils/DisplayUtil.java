package com.example.ttlock.utils;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * dp、sp 转换为 px 的工具类
 * 
 *
 */
public class DisplayUtil
{
	private static int sScreenWidth;
	private static int sScreenHeight;

	/**
	 * 将px值转换为dip或dp值，保证尺寸大小不变
	 * 
	 * @param pxValue
	 *            （DisplayMetrics类中属性density）
	 * @return
	 */
	public static int px2dip(Context context, float pxValue)
	{
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 将dip或dp值转换为px值，保证尺寸大小不变
	 * 
	 * @param dipValue
	 *            （DisplayMetrics类中属性density）
	 * @return
	 */
	public static int dip2px(Context context, float dipValue)
	{
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	/**
	 * 将px值转换为sp值，保证文字大小不变
	 * 
	 * @param pxValue
	 *            （DisplayMetrics类中属性scaledDensity）
	 * @return
	 */
	public static int px2sp(Context context, float pxValue)
	{
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (pxValue / fontScale + 0.5f);
	}

	/**
	 * 将sp值转换为px值，保证文字大小不变
	 * 
	 * @param spValue
	 *            （DisplayMetrics类中属性scaledDensity）
	 * @return
	 */
	public static int sp2px(Context context, float spValue)
	{
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * fontScale + 0.5f);
	}


	/**
	 * 获得屏幕高度
	 *
	 * @param context
	 * @return
	 */
	public static int getScreenWidth(Context context)
	{
		DisplayMetrics outMetrics = context.getResources().getDisplayMetrics();
		sScreenWidth = outMetrics.widthPixels;
		return sScreenWidth;
	}

	/**
	 * 获得屏幕宽度
	 *
	 * @param context
	 * @return
	 */
	public static int getScreenHeight(Context context)
	{
		DisplayMetrics outMetrics = context.getResources().getDisplayMetrics();
		sScreenHeight = outMetrics.heightPixels;
		return sScreenHeight;
	}

	/**
	 * 获得状态栏的高度
	 *
	 * @param context
	 * @return
	 */
	public static int getStatusHeight(Context context)
	{

		int statusHeight = -1;
		try
		{
			Class<?> clazz = Class.forName("com.android.internal.R$dimen");
			Object object = clazz.newInstance();
			int height = Integer.parseInt(clazz.getField("status_bar_height")
					.get(object).toString());
			statusHeight = context.getResources().getDimensionPixelSize(height);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return statusHeight;
	}

	private static long lastClickTime;

	/**
	 * 防止重复点击机制 3s 内仅允许一次点击
	 * @return
     */
	public synchronized static boolean isFastClick() {
		long time = System.currentTimeMillis();
		if (Math.abs(time - lastClickTime) < 1000) {
			return true;
		}
		lastClickTime = time;
		return false;
	}

}
