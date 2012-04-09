package com.skyworth.airplay.tvcc;

import org.cocos2d.layers.CCColorLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.types.ccColor4B;

public class MainLayerScene extends CCColorLayer {
	
	public MainLayerScene() {
		super(ccColor4B.ccc4(0, 0, 0, 0));
		// TODO Auto-generated constructor stub
		changeHeight(1080);
		changeWidth(1920);
		setIsTouchEnabled(true);
	}

	public CCScene getScene() {
		CCScene scene = CCScene.node();
		scene.addChild(this);
		return scene;
	}
}
