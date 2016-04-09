package com.bluet.massistant;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageView;


public class CircleButton extends ImageView {

	private static final int PRESSED_COLOR_LIGHTUP = 255 / 25;
	private static final int PRESSED_RING_ALPHA = 75;
	private static final int DEFAULT_PRESSED_RING_WIDTH_DIP = 4;
	private static final int ANIMATION_TIME_ID = android.R.integer.config_shortAnimTime;

	private int centerY;
	private int centerX;
	private int outerRadius;
	private int pressedRingRadius;

	private Paint circlePaint;
	private Paint focusPaint,statusText = new Paint();;

	private float animationProgress;

	private int pressedRingWidth;
	private int defaultColor = Color.GRAY;
	private int pressedColor;
	private ObjectAnimator pressedAnimator;
	private String text = "ABCD";
	private byte[] data = null;

	public CircleButton(Context context) {
		super(context);
		init(context, null);
	}

	public CircleButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public CircleButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
	}
	
	public void set_datab(byte[] b){
		data = b;
	}
	
	
	@Override
	public void setPressed(boolean pressed) {
		super.setPressed(pressed);

		if (circlePaint != null) {
			circlePaint.setColor(pressed ? pressedColor : defaultColor);
		}

		if (pressed) {
			if(data != null)
				MainActivity.key_presssed(data);
			showPressedRing();
		} else {
			if(data != null)
				MainActivity.key_off();
			hidePressedRing();
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		
		canvas.drawCircle(centerX, centerY, pressedRingRadius + animationProgress, focusPaint);
		canvas.drawCircle(centerX, centerY, outerRadius - pressedRingWidth, circlePaint);
		
		canvas.drawText(text, centerX, centerY+statusText.getTextSize()/4, statusText);
		super.onDraw(canvas);
	}

	int mag = 40;
	int high,wild;
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		high = h;
		wild = w;
		centerX = w / 2;
		centerY = h / 2;
		outerRadius = Math.min(w, h)*5 / 10;
		pressedRingRadius = outerRadius - pressedRingWidth - pressedRingWidth / 2;
		statusText.setTextSize(Math.min(w, h)/7);
	}

	@Override  
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {  
	int w,h;
	w = widthMeasureSpec;
	h = heightMeasureSpec;
	setMeasuredDimension(Math.min(w, h), Math.min(w, h));  
	}  
	
	public float getAnimationProgress() {
		return animationProgress;
	}

	public void setAnimationProgress(float animationProgress) {
		this.animationProgress = animationProgress;
		this.invalidate();
	}

	public void setText(String t){
		text = t;
		this.invalidate();
	}
	public void setColor(int color) {
		this.defaultColor = color;
		this.pressedColor = getHighlightColor(color, PRESSED_COLOR_LIGHTUP);

		circlePaint.setColor(defaultColor);
		focusPaint.setColor(defaultColor);
		focusPaint.setAlpha(PRESSED_RING_ALPHA);

		this.invalidate();
	}
	
	

	private void hidePressedRing() {
		pressedAnimator.setFloatValues(pressedRingWidth, 0f);
		pressedAnimator.start();
	}

	private void showPressedRing() {
		pressedAnimator.setFloatValues(animationProgress, pressedRingWidth);
		pressedAnimator.start();
	}

	private void init(Context context, AttributeSet attrs) {
		this.setFocusable(true);
		this.setScaleType(ScaleType.CENTER_INSIDE);
		setClickable(true);

		statusText.setColor(Color.rgb(240, 240, 240));
		statusText.setTextSize(20.0f);
		statusText.setAntiAlias(true);
		statusText.setTextAlign(Align.CENTER);
		
		circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		circlePaint.setStyle(Paint.Style.FILL);
		circlePaint.setAntiAlias(true);

		focusPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		focusPaint.setStyle(Paint.Style.STROKE);
		focusPaint.setAntiAlias(true);

		pressedRingWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_PRESSED_RING_WIDTH_DIP, getResources()
				.getDisplayMetrics())+8;

		int color = defaultColor;
		if (attrs != null) {
			final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircleButton);
			color = a.getColor(R.styleable.CircleButton_cb_color, color);
			pressedRingWidth = (int) a.getDimension(R.styleable.CircleButton_cb_pressedRingWidth, pressedRingWidth)+8;
			a.recycle();
		}

		setColor(color);

		focusPaint.setStrokeWidth(pressedRingWidth);
		final int pressedAnimationTime = getResources().getInteger(ANIMATION_TIME_ID);
		pressedAnimator = ObjectAnimator.ofFloat(this, "animationProgress", 0f, 0f);
		pressedAnimator.setDuration(pressedAnimationTime);
	}

	private int getHighlightColor(int color, int amount) {
		return Color.argb(Math.min(255, Color.alpha(color)), Math.min(255, Color.red(color) + amount),
				Math.min(255, Color.green(color) + amount), Math.min(255, Color.blue(color) + amount));
	}

}
