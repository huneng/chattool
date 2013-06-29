package com.huneng.chattool.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

public class FileHelper {
	public static final String rootPath = Environment
			.getExternalStorageDirectory().getPath() + "/chattool";

	public static Bitmap getBitmap(String fileName) {
		String path = rootPath + "/img/" + fileName;
		File file = new File(path);
		if (file.isFile()) {
			return BitmapFactory.decodeFile(path);
		}
		
		return null;
	}
	public static Bitmap getBitmap(String fileName, int width, int height) {
		String path = rootPath + "/img/" + fileName;
		File file = new File(path);
		if (file.isFile()) {
			Bitmap bitmap =BitmapFactory.decodeFile(path);
			bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
			return bitmap;
		}
		return null;
	}
	public static byte[] readFromFile(String fileName) {
		byte[] data = new byte[10000];
		File file = new File(fileName);
		try {
			FileInputStream fin = new FileInputStream(file);
			fin.read(data);
			fin.close();
			return data;
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}

		return null;
	}

	public static void writeToFile(String fileName, byte[] data) {
		try {
			FileOutputStream fout = new FileOutputStream(new File(fileName));
			fout.write(data);
			fout.flush();
			fout.close();
		} catch (FileNotFoundException e) {
			Log.v("FileHelper", "FileNotFound:" + e.getMessage());
		} catch (IOException e) {
			Log.v("FileHelper", "IOException:" + e.getMessage());
		}

	}
	
	

}
