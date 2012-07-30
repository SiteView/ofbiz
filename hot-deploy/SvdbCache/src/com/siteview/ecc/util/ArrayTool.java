package com.siteview.ecc.util;

import java.text.NumberFormat;


/**
 * <p>
 * int数组操作类，用于取出最大值、最小值、平均值
 * </p>
 * 
 * @author zhongping.wang
 * 
 */
public class ArrayTool {
	public ArrayTool() {
	}

	/**
	 * 获取int数组中最大值
	 */
	public static int getIntArrayMax(int[] arr) {
		int max = 0;
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] > arr[max]) {
				max = i;
			}
		}
		return arr[max];
	}

	/**
	 * 获取int数组中最小值
	 */
	public static int getIntArrayMin(int[] arr) {
		int min = 0;
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] < arr[min]) {
				min = i;
			}
		}
		return arr[min];
	}

	/**
	 * 获取int数组平均值
	 */
	public static int getIntArrayAvg(int[] arr) {
		int sum = 0;
		for (int i = 0; i < arr.length; i++) {
			sum += arr[i];
		}
		return sum / arr.length;
	}

	/**
	 * 获取double数组中最大值
	 */
	public static double getDoubleArrayMax(double[] arr) {
		Double maxResult = arr[0];
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] > maxResult) {
				maxResult = arr[i];
			}
		}
		return maxResult;
	}

	/**
	 * 获取double数组中最小值
	 */
	public static double getDoubleArrayMin(double[] arr) {
		Double minResult = arr[0];
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] < minResult) {
				minResult = arr[i];
			}
		}
		return minResult;
	}

	/**
	 * 获取double数组平均值
	 */
	public static double getDoubleArrayAvg(double[] arr) {
		Double sum = 0.0;
		for (int i = 0; i < arr.length; i++) {
			sum += arr[i];
		}
		return sum / arr.length;
	}
	/**
	 * 计算百分比
	 * @param y
	 * @param z
	 * @return
	 */
	public static String percent(int y,int z){
		   String baifenbi="";//百分比的值
		   double baiy=y*1.0;
		   double baiz=z*1.0;
		   double fen=baiy/baiz;
		   NumberFormat nf   =   NumberFormat.getPercentInstance();
		   nf.setMinimumFractionDigits( 2 );//保留到小数点后几位
		   baifenbi=nf.format(fen);
		   if (String.valueOf(fen).equals("NaN")) {
			   baifenbi = "无数据";
		}
		   return baifenbi.substring(0, baifenbi.length()-1);
		}
	public static void main(String[] args) {
		System.out.println(percent(0, 2));
	}
}
