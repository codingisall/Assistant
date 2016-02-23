package com.bluet.massistant;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.lurencun.service.autoupdate.AppUpdate;
import com.lurencun.service.autoupdate.AppUpdateService;
import com.lurencun.service.autoupdate.internal.SimpleJSONParser;

public class UpdateActivity extends BaseActivity {

	AppUpdate appUpdate;
	
	final static String UPDATE_URL = "http://192.168.0.103:3000/version";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update);
		
		appUpdate = AppUpdateService.getAppUpdate(this);
		
		View check = findViewById(R.id.check);
		check.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// 妫�煡鏈�柊鐗堟湰锛屽苟寮瑰嚭绐楀彛
				appUpdate.checkLatestVersion(UPDATE_URL, 
						new SimpleJSONParser());
			}
		});
		
		View download = findViewById(R.id.download);
		download.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// 鏃犻』鎻愮ず锛岀洿鎺ュ崌绾�				
				appUpdate.checkAndUpdateDirectly(UPDATE_URL, 
						new SimpleJSONParser());
			}
		});
		throw new RuntimeException("this is a demo crash current time：" + System.currentTimeMillis());
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		
		// ******** 
		appUpdate.callOnResume();
	}
	
	@Override
	protected void onPause(){
		super.onPause();
		
		// ******** 
		appUpdate.callOnPause();
	}

}
