package com.skyworth.airplay.tvcc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.cocos2d.actions.base.CCAction;
import org.cocos2d.actions.instant.CCCallFuncN;
import org.cocos2d.actions.interval.CCFadeIn;
import org.cocos2d.actions.interval.CCFadeOut;
import org.cocos2d.actions.interval.CCMoveTo;
import org.cocos2d.actions.interval.CCRotateTo;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.actions.interval.CCSpawn;
import org.cocos2d.layers.CCColorLayer;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.ccColor3B;
import org.cocos2d.types.ccColor4B;
import org.cocos2d.utils.javolution.MathLib;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.util.Log;
import android.widget.Toast;

public class ImageShare extends CCColorLayer {
	public static String TAG = "ImageShare";
	
	private static class IBAnimation {

		public static CCAction getAction(Object obj, String func, int to) {
			CCMoveTo mv = CCMoveTo.action(0.3f, CGPoint.ccp(30, (float)(-(to-4)*57-(to-5)*200)));
			CCRotateTo ro = CCRotateTo.action(0.3f, MathLib.random(-10.0f, 10.0f));
			CCCallFuncN moveEnd = CCCallFuncN.action(obj, func);
			CCSpawn act = CCSpawn.actions(mv, ro, moveEnd);
			return act;
		}
		
		public static CGPoint getPoint(int to) {
			return CGPoint.ccp(30, (float)(-(to-4)*57-(to-5)*200));
		}
	}
	
	private static class ImageItem extends CCColorLayer {
		//private static final Bitmap nullbitmap = Bitmap.createBitmap(152, 125, Config.ARGB_8888);
		private static Bitmap loading = null;
		private CCSprite img = null;
		public int index = -1;
		public String filepath = null;
		
		public ImageItem() {
			super(ccColor4B.ccc4(0, 0, 0, 0));
			// TODO Auto-generated constructor stub
			
			try {
				changeHeight(156);
				changeWidth(250);
				setRotation(MathLib.random(-10.0f, 10.0f));
				setAnchorPoint(0,0);
				setContentSize(250, 156);
				
				if(loading == null)
					loading = new BitmapDrawable(AirPlayTvcc.assetManager.open("loading.png")).getBitmap();
				img = CCSprite.sprite(loading, null);
				img.setAnchorPoint(0,0);
				img.setPosition(0,0);
				addChild(img);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public void setColor(ccColor3B c) {
			img.setColor(c);
		}
		
		public ccColor3B getColor(){
			return img.getColor();		
		}
		
		public void drawImage(String path, int i) {
			removeChild(img, true);
			//Bitmap s = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(path), 240, 146, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
			//img = CCSprite.sprite(path, CGRect.make(0,0,250,160));
			img = CCSprite.sprite(getThumbnail(path, 250, 160), null);
			img.setAnchorPoint(0,0);
			img.setPosition(0,0);
			addChild(img);
			index = i;
			filepath = path;
		}
		
		public void clearup() {
			if(filepath != null) {
				File file = new File(filepath);
				if(file.exists())
					file.delete();
			}
			super.cleanup();
		}
	}
	
	private class MainImageLayer extends CCColorLayer {
		private CCSprite playCCSprite = null;
		
		protected MainImageLayer(int o) {
			super(ccColor4B.ccc4(0, 0, 0, 0));
			// TODO Auto-generated constructor stub
			changeHeight(1080);
			changeWidth(660);
			setAnchorPoint(0,0);
			
			if(o == ORIENTATIONLEFT)
				setPosition(CGPoint.ccp(300, 0));
			else 
				setPosition(CGPoint.ccp(0, 0));
		}
		
		public void draw(String path) {
			Log.d(TAG, "draw image:" + path);
			CCFadeIn fi = CCFadeIn.action(0.3f);
			CCFadeOut fo = CCFadeOut.action(0.3f);
			CCCallFuncN moveEnd = CCCallFuncN.action(this, "fodone");
			
			CCSprite tmpCCSprite = CCSprite.sprite(getThumbnail(path, 640, 480), null);
			tmpCCSprite.setAnchorPoint(0, 0);
			tmpCCSprite.setPosition(10, 300);
			addChild(tmpCCSprite);

			CCSequence actt = CCSequence.actions(fi, moveEnd);
			tmpCCSprite.runAction(actt);
			if(playCCSprite != null) {
				CCSequence act = CCSequence.actions(fo, moveEnd);
				playCCSprite.runAction(act);
			}
		}
		
		public void fodone(Object sender) {
			CCSprite s = (CCSprite)sender;
			if(s == playCCSprite) {
				s.cleanup();
				removeChild(s, true);
			}
			else 
				playCCSprite = s;
		}
	}
	
	private class ImageListLayer extends CCColorLayer {
		private ArrayList<ImageItem> ImageBitmaplist = new ArrayList<ImageItem>();

		private boolean isInited = false;
		private int initcount = 0;
		private int imgAmount=0;
		
		protected ImageListLayer(int o) {
			super(ccColor4B.ccc4(0, 0, 0, 0));
			// TODO Auto-generated constructor stub
			changeHeight(1080);
			changeWidth(300);
			setAnchorPoint(0,0);
			if(o == ORIENTATIONLEFT)
				setPosition(CGPoint.ccp(0, 0));
			else 
				setPosition(CGPoint.ccp(660, 0));
		}
		
		public void doUp() {
			Log.i(TAG, "doUp");
			if(ImageBitmaplist.get(3).index < imgAmount-1) {
				for(int i = 0; i < 7; i++) {
					if(i != 0)
						ImageBitmaplist.get(i).runAction(IBAnimation.getAction(this, "actionUpDone", i-1));
				}
				removeChild(ImageBitmaplist.get(0), true);
				ImageBitmaplist.get(0).clearup();
				ImageBitmaplist.remove(0);
				
				ImageItem bb = new ImageItem();
				ImageBitmaplist.add(6, bb);
				bb.setPosition(IBAnimation.getPoint(6));
				addChild(bb);
				showImage(ImageBitmaplist.get(3).filepath);
			}
		}
		
		public void doDown() {
			Log.i(TAG, "doDown");
			if(ImageBitmaplist.get(3).index > 0) {
				for(int i = 0; i < 7; i++) {
					if(i != 6)
						ImageBitmaplist.get(i).runAction(IBAnimation.getAction(this, "actionDownDone", i+1));
				}
				removeChild(ImageBitmaplist.get(6), true);
				ImageBitmaplist.get(6).clearup();
				ImageBitmaplist.remove(6);
				
				ImageItem bb = new ImageItem();
				ImageBitmaplist.add(0, bb);
				bb.setPosition(IBAnimation.getPoint(0));
				addChild(bb);
				showImage(ImageBitmaplist.get(3).filepath);
			}
		}
		
		public void actionUpDone(Object sender) {
	    }
		
		public void actionDownDone(Object sender) {
	    }
		
		public void setCount(int c) {
			imgAmount = c;
			int max = 0;
			if(imgAmount > 6)
				max = 7;
			else 
				max = imgAmount;
			for(int i = 0; i < max; i++) {
				ImageItem bb = new ImageItem();
				addChild(bb);
				ImageBitmaplist.add(bb);
				
				bb.runAction(IBAnimation.getAction(this, "actionUpDone", i));
			}
		}
		
		public void newImage(String path, int i) {
			if(!isInited) { 
				Log.d(TAG, "init image:" + i + "  path:" + path + "  initcount:" + initcount);
				ImageBitmaplist.get(i).drawImage(path, i);
				initcount++;
				if(imgAmount > 7) {
					if(initcount == 7)
						isInited = true;
				}
				else {
					if(initcount == imgAmount)
						isInited = true;
				}
			}
			else {
				if(ImageBitmaplist.get(0).index == -1)
					ImageBitmaplist.get(0).drawImage(path, i);
				else
					ImageBitmaplist.get(6).drawImage(path, i);
			}
		}
	}
	
	public final static int ORIENTATIONLEFT = 0;
	public final static int ORIENTATIONRIGHT = 1;
	
	private Context mContext = null;
	private ImageListLayer mImageListLayer = null;
	private MainImageLayer mMainImageLayer = null;
	
	protected ImageShare(Context c, int o) {
		super(ccColor4B.ccc4(0, 0, 0, 0));
		// TODO Auto-generated constructor stub
		changeHeight(1080);
		changeWidth(960);
		setAnchorPoint(0,0);
		mContext = c;
		
		mImageListLayer = new ImageListLayer(o);
		mMainImageLayer = new MainImageLayer(o);
		
		addChild(mImageListLayer);
		addChild(mMainImageLayer);
		if(o == ORIENTATIONLEFT)
			setPosition(CGPoint.ccp(0, 0));
		else
			setPosition(CGPoint.ccp(960, 0));
	}
	
	public void pressDown(){
		System.out.println("Press KEY-DOWN");
		mImageListLayer.doDown();
	}
	
	public void pressUp(){
		System.out.println("Press KEY-UP");
		mImageListLayer.doUp();
	}
	
	public void shareImage(){
		Toast.makeText(mContext, "分享图片", Toast.LENGTH_SHORT).show();
	}
	
	public void newImage(String path, int index) {
		// TODO Auto-generated method stub
		Log.d(TAG, "newImage:" + path + "  index:" + index);
		mImageListLayer.newImage(path, index);
	}
	
	public void setCount(int c) {
		mImageListLayer.setCount(c);
	}
	
	public static Bitmap getThumbnail(String path, int w, int h) {
		Bitmap s = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(path), w, h, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		try {
			if(s == null)
				s = ThumbnailUtils.extractThumbnail(new BitmapDrawable(AirPlayTvcc.assetManager.open("loading.png")).getBitmap(), w, h, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return s;
	}
	
	private void showImage(String path) {
		mMainImageLayer.draw(path);
	}
	
	public void clear() {
		
	}
}
