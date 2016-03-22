package com.mycj.junsda.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcelable;
import android.widget.Toast;

public class ShareUtil {
	
	public  static  void shareText(String text,Activity ac){
		Intent sendIntent = new Intent();
		sendIntent.setAction(Intent.ACTION_SEND);
		sendIntent.putExtra(Intent.EXTRA_TEXT, text);
		sendIntent.setType("text/plain");
		ac.startActivity(Intent.createChooser(sendIntent, "分享运动")); 
	}
	
	public  static  void shareImage(String path,Activity ac,String msg){
//		String filename = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+"xyx.jpg";
		File file = new File(path);
		Uri imageUri = Uri.fromFile(file);
	    Intent shareIntent = new Intent();  
	    shareIntent.setAction(Intent.ACTION_SEND);  
	    shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);  
	    shareIntent.setType("image/jpeg");  
	    ac.startActivity(Intent.createChooser(shareIntent, msg));
	}
	

	/** 
     * 分享功能 
     *  
     * @param context 
     *            上下文 
     * @param activityTitle 
     *            Activity的名字 
     * @param msgTitle 
     *            消息标题 
     * @param msgText 
     *            消息内容 
     * @param imgPath 
     *            图片路径，不分享图片则传null 
     */  
    public void shareMsg(Context context,String activityTitle, String msgTitle, String msgText,  
            String imgPath) {  
        Intent intent = new Intent(Intent.ACTION_SEND);  
        if (imgPath == null || imgPath.equals("")) {  
            intent.setType("text/plain"); // 纯文本  
        } else {  
            File f = new File(imgPath);  
            if (f != null && f.exists() && f.isFile()) {  
                intent.setType("image/jpg");  
                Uri u = Uri.fromFile(f);  
                intent.putExtra(Intent.EXTRA_STREAM, u);  
            }  
        }  
        intent.putExtra(Intent.EXTRA_SUBJECT, msgTitle);  
        intent.putExtra(Intent.EXTRA_TEXT, msgText);  
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
        context.startActivity(Intent.createChooser(intent, activityTitle));  
    } 
    
    
    public void t(Context context){
    	String contentDetails = "";
        String contentBrief = "";
        String shareUrl = "";
        Intent it = new Intent(Intent.ACTION_SEND);
        it.setType("text/plain");
        List<ResolveInfo> resInfo = context.getPackageManager().queryIntentActivities(it, 0);
        if (!resInfo.isEmpty()) {
            List<Intent> targetedShareIntents = new ArrayList<Intent>();
            for (ResolveInfo info : resInfo) {
                Intent targeted = new Intent(Intent.ACTION_SEND);
                targeted.setType("text/plain");
                ActivityInfo activityInfo = info.activityInfo;
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       
                // judgments : activityInfo.packageName, activityInfo.name, etc.
                if (activityInfo.packageName.contains("bluetooth") || activityInfo.name.contains("bluetooth")) {
                    continue;
                }
                if (activityInfo.packageName.contains("gm") || activityInfo.name.contains("mail")) {
                    targeted.putExtra(Intent.EXTRA_TEXT, contentDetails);
                } else if (activityInfo.packageName.contains("zxing")) {
                    targeted.putExtra(Intent.EXTRA_TEXT, shareUrl);
                } else {
                    targeted.putExtra(Intent.EXTRA_TEXT, contentBrief);
                }
                targeted.setPackage(activityInfo.packageName);
                targetedShareIntents.add(targeted);
            }
            Intent chooserIntent = Intent.createChooser(targetedShareIntents.remove(0), "Select app to share");
            if (chooserIntent == null) {
                return;
            }
            // A Parcelable[] of Intent or LabeledIntent objects as set with
            // putExtra(String, Parcelable[]) of additional activities to place
            // a the front of the list of choices, when shown to the user with a
            // ACTION_CHOOSER.
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedShareIntents.toArray(new Parcelable[] {}));
            try {
            	context.startActivity(chooserIntent);
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(context, "Can't find share component to share", Toast.LENGTH_SHORT).show();
            }
        }
    }
	
}
