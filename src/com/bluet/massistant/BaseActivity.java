package com.bluet.massistant;

import com.bugtags.library.Bugtags;

import android.app.Activity;
import android.view.MotionEvent;

public class BaseActivity extends Activity {
	    @Override
	    protected void onResume() {
	        super.onResume();
	        //ע���ص� 1
	        Bugtags.onResume(this);
	    }

	    @Override
	    protected void onPause() {
	        super.onPause();
	        //ע���ص� 2
	        Bugtags.onPause(this);
	    }

	    @Override
	    public boolean dispatchTouchEvent(MotionEvent event) {
	        //ע���ص� 3
	        Bugtags.onDispatchTouchEvent(this, event);
	        return super.dispatchTouchEvent(event);
	    }
}
