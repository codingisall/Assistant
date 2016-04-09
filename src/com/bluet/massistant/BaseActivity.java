package com.bluet.massistant;

import com.bugtags.library.Bugtags;

import android.app.Activity;
import android.view.MotionEvent;

public class BaseActivity extends Activity {
	    @Override
	    protected void onResume() {
	        super.onResume();
	        
	        Bugtags.onResume(this);
	    }

	    @Override
	    protected void onPause() {
	        super.onPause();

	        Bugtags.onPause(this);
	    }

	    @Override
	    public boolean dispatchTouchEvent(MotionEvent event) {
	        
	        Bugtags.onDispatchTouchEvent(this, event);
	        return super.dispatchTouchEvent(event);
	    }
}
