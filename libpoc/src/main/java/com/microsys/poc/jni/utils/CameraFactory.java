package com.microsys.poc.jni.utils;

import android.content.Context;
import android.hardware.Camera;
import android.widget.Toast;

public class CameraFactory {
	
	public static Camera openCamera(int cameraTowards, Context context) {
		
		Camera camera = null;
		try {
			camera = Camera.open(cameraTowards);
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(context, "无法开启摄像头，请确认是否赋予该权限", Toast.LENGTH_SHORT).show();
			LogUtil.getInstance().logWithMethod(new Exception(), " camera open erro message "+e.getMessage(), "Zhaolg");
			//转跳到应用详情界面
//			Intent intent = new Intent(); 
//		    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);  
//	        Uri uri = Uri.fromParts("package", "com.microsys.pocdroidgv", null);  
//	        intent.setData(uri); 
//	        context.startActivity(intent);
		}
		
		return camera;
		
	}

}
