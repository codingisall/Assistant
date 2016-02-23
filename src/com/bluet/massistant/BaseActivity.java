package com.bluet.massistant;

import com.bugtags.library.Bugtags;

import android.app.Activity;
import android.view.MotionEvent;

public class BaseActivity extends Activity {
	    @Override
	    protected void onResume() {
	        super.onResume();
	        //注：回调 1
	        Bugtags.onResume(this);
	    }

	    @Override
	    protected void onPause() {
	        super.onPause();
	        //注：回调 2
	        Bugtags.onPause(this);
	    }

	    @Override
	    public boolean dispatchTouchEvent(MotionEvent event) {
	        //注：回调 3
	        Bugtags.onDispatchTouchEvent(this, event);
	        return super.dispatchTouchEvent(event);
	    }
}
