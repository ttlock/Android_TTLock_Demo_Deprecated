package com.example.ttlock.utils;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * dp、sp convert to px
 * 
 *
 */
public class DisplayUtil
{
	private static int sScreenWidth;
	private static int sScreenHeight;

	/**
	 *
	 * convert px value to dp
	 * @param pxValue
	 *
	 */
	public static int px2dip(Context context, float pxValue)
	{
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * convert dp value to px
	 * 
	 * @param dipValue
	 *
	 * @return
	 */
	public static int dip2px(Context context, float dipValue)
	{
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	/**
	 * convert px value to sp
	 * 
	 * @param pxValue
	 *
	 * @return
	 */
	public static int px2sp(Context context, float pxValue)
	{
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (pxValue / fontScale + 0.5f);
	}

	/**
	 * convert sp value to px
	 * 
	 * @param spValue
	 *
	 * @return
	 */
	public static int sp2px(Context context, float spValue)
	{
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * fontScale + 0.5f);
	}


	/**
	 * get the width of screen
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
	 * get the height of screen
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
	 * get the height of status bar
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

}
