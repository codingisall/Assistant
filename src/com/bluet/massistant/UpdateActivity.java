package com.bluet.massistant;

import android.os.Bundle;
import android.view.View;

import com.lurencun.service.autoupdate.AppUpdate;
import com.lurencun.service.autoupdate.AppUpdateService;
import com.lurencun.service.autoupdate.internal.SimpleJSONParser;

public class UpdateActivity extends BaseActivity {

	AppUpdate appUpdate;
	
	final static String UPDATE_URL = "http://115.28.178.225:3000/version";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update);
		
		appUpdate = AppUpdateService.getAppUpdate(this);
		
		View check = findViewById(R.id.check);
		check.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				appUpdate.checkLatestVersion(UPDATE_URL, 
						new SimpleJSONParser());
			}
		});
		
		View download = findViewById(R.id.download);
		download.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {			
				appUpdate.checkAndUpdateDirectly(UPDATE_URL, 
						new SimpleJSONParser());
			}
		});
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
