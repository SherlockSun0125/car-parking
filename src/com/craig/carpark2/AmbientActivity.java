package com.craig.carpark2;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class AmbientActivity extends Activity {

	TextView t;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView(R.layout.activity_ambient);
        final ImageView image = (ImageView) findViewById(R.id.imageView1);
        TextView t = (TextView) findViewById(R.id.textView1);
        t.setText("S1437151");
		Intent i = getIntent();
		double rate = i.getDoubleExtra("rate", 0.0);
		
		long speed = (100 - (long)rate) * 100;
		
		setBackgroundColor(rate);
		Animation an = AnimationUtils.loadAnimation(this, R.anim.pulse);
		an.setDuration(speed);
		Animation an2 = AnimationUtils.loadAnimation(this, R.anim.pulse2);
		an2.setStartOffset(speed);
		an2.setDuration(speed);
		
		final AnimationSet as = new AnimationSet(false);
		as.addAnimation(an);
		as.addAnimation(an2);
		
		image.startAnimation(as);
		as.setAnimationListener(new Animation.AnimationListener() {
		    @Override
		    public void onAnimationStart(Animation animation) {
		    	image.setVisibility(View.VISIBLE);
		    }

		    @Override
		    public void onAnimationEnd(Animation animation) {
		    	image.setVisibility(View.INVISIBLE);
		        image.startAnimation(as);
		    }

		    @Override
		    public void onAnimationRepeat(Animation animation) {}
		});
		
		
	}
	
	public void setBackgroundColor(double rate){
		//few spaces = red
		if(rate >= 75){
			getWindow().getDecorView().setBackgroundColor(Color.parseColor("#F44336"));
		} else if(rate <= 25) {
			//Lots of spaces = green
			getWindow().getDecorView().setBackgroundColor(Color.parseColor("#4CAF50"));
			
			//In between = amber
		} else {
			getWindow().getDecorView().setBackgroundColor(Color.parseColor("#FFC107"));
		}
	}
}
