package com.brandonswanson.crackthecode;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Help extends Activity implements OnClickListener {

	private boolean mHelpTextOn;
	private LinearLayout mMasterLayout;
	private TextView mHelpTextHeader;
	private TextView mHowtoPlayHeader;
	private TextView mHowtoWinHeader;
	private ImageView mHelpTextOnButton;
	private ImageView mHelpTextOffButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
		setContentView(R.layout.activity_help_layout);
	
	
		mHelpTextOn = getSharedPreferences(GamePrefrences.SETTINGS, MODE_PRIVATE).getBoolean(GamePrefrences.HELP_TEXT, true);
		mHelpTextOnButton = (ImageView) findViewById(R.id.help_text_on_button);
		mHelpTextOffButton = (ImageView) findViewById(R.id.help_text_off_button);
		
		if(mHelpTextOn){
			mHelpTextOffButton.setAlpha(.5f);
			mHelpTextOnButton.setEnabled(false);
		}
		else{
			mHelpTextOnButton.setAlpha(.5f);
			mHelpTextOffButton.setEnabled(false);
		}
		
		mHelpTextOffButton.setOnClickListener(this);
		mHelpTextOnButton.setOnClickListener(this);
		
		
	
		mMasterLayout = (LinearLayout) findViewById(R.id.help_master_layout);
		mHelpTextHeader = (TextView) findViewById(R.id.help_header_help_text);
		mHowtoPlayHeader = (TextView) findViewById(R.id.help_header_how_to_play);
		mHowtoWinHeader = (TextView) findViewById(R.id.help_header_how_to_win);
		
		addFragmentViews();
		
		
		mMasterLayout.post(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				TextView textHeaders[] = new TextView[]{mHelpTextHeader,mHowtoPlayHeader,mHowtoWinHeader};
				for (int i = 0; i < textHeaders.length; i++) {
					textHeaders[i].setLayoutParams(new LinearLayout.LayoutParams(textHeaders[i].getWidth(), textHeaders[i].getHeight()));
					textHeaders[i].setBackgroundResource(R.drawable.dark_wood_background);
				}
			}
		});
		
		
	
	
	}

	private void addFragmentViews() {
		// TODO Auto-generated method stub
		
		
		Resources myResources = getResources();
		
		String rHowtoPlayHeading[] = myResources.getStringArray(R.array.how_to_play_headings); 
		String rHowtoPlayDetails[] = myResources.getStringArray(R.array.how_to_play_details);
		int rHowtoPlayImages[]= new int[]{R.drawable.guess_button,R.drawable.shuffle_button,R.drawable.go_back_button,R.drawable.drag_drop};
		
		for (int i = rHowtoPlayImages.length-1; i > -1; i--) {
			View fragment = LayoutInflater.from(getBaseContext()).inflate(R.layout.help_fragment,null);
	        ((ImageView)fragment.findViewById(R.id.help_fragment_image)).setImageResource(rHowtoPlayImages[i]);
	        ((TextView)fragment.findViewById(R.id.help_fragment_heading)).setText(rHowtoPlayHeading[i]);
	        ((TextView)fragment.findViewById(R.id.help_fragment_detail_text)).setText(rHowtoPlayDetails[i]);
	        
	        int index=mMasterLayout.indexOfChild(mHowtoPlayHeader);
	        mMasterLayout.addView(fragment, index+1);
	        
		}
        
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.help_text_on_button:
			
			mHelpTextOnButton.setAlpha(1f);
			mHelpTextOffButton.setAlpha(.5f);
			mHelpTextOn=true;
			mHelpTextOffButton.setEnabled(true);
			mHelpTextOnButton.setEnabled(false);
			
			
			break;
		case R.id.help_text_off_button:
			
			mHelpTextOnButton.setAlpha(.5f);
			mHelpTextOffButton.setAlpha(1f);
			mHelpTextOn=false;
			
			mHelpTextOffButton.setEnabled(false);
			mHelpTextOnButton.setEnabled(true);
			
			break;

		default:
			break;
		}
	}

	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if( mHelpTextOn!=getSharedPreferences(GamePrefrences.SETTINGS, MODE_PRIVATE).getBoolean(GamePrefrences.HELP_TEXT, false)){
			Intent result = new Intent();
			result.putExtra(GameSplashScreen.HELP_TEXT_KEY, mHelpTextOn);
			setResult(RESULT_OK, result);
		}else setResult(RESULT_CANCELED);
		
		super.onBackPressed();
	}
}

