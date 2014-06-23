package com.brandonswanson.crackthecode;

import java.util.ArrayList;
import java.util.Arrays;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.brandonswanson.crackthecode.R;



public class GameSplashScreen extends Activity implements OnClickListener {
	
	private FrameLayout mMasterLayout;
	private long ANIMATIONTIMING = 600;
	
	private ImageView[] mBalls;
	private float[] ballsTranslateEndX;
	private float[] ballsTranslateEndY;
	private float[] ballsTranslateStartX;
	private float[] ballsTranslateStartY;
	private FrameLayout ballFrame;
	private ImageView mResumeGameButton;
	private ImageView mNewGameButton;
	private ImageView mSettingsButton;
	private ImageView mHelpButton;
	private ImageView mScoresButton;
	
	
	private final float finalAlpha=.6f;
	private PropertyValuesHolder fadeInPV = PropertyValuesHolder.ofFloat("Alpha", 0,finalAlpha);
	
	public static final String DIFFICULTY_KEY = "DifficultyKey";
	private static final int CHANGE_DIFFICULTY_REQUEST = 0;

	
	public static final String HELP_TEXT_KEY = "helpTextKey";
	private static final int HELP_REQUEST = 2;
	
	
	
	
	private static final int PLAY_GAME_REQUEST = 1;
	public static final String GO_TO_SETTINGS_KEY = "go to settings";
	public static final String GO_TO_HELP_KEY = "go to help";
	

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_screen_layout);
		
		
		mMasterLayout = (FrameLayout) findViewById(R.id.SplashMasterLayout);
		mNewGameButton = (ImageView) findViewById(R.id.playgamebutton);
		mResumeGameButton = (ImageView) findViewById(R.id.resumegame);
		
		
		mSettingsButton = (ImageView) findViewById(R.id.settingsButton);
		mScoresButton = (ImageView) findViewById(R.id.scoresButton);
		mHelpButton = (ImageView) findViewById(R.id.helpButton);
		
		mSettingsButton.setOnClickListener(this);
		mScoresButton.setOnClickListener(this);
		mHelpButton.setOnClickListener(this);
		
		
		
		mMasterLayout.post(new Runnable() {
			
			

			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				int heightOfMasterFrame=mMasterLayout.getHeight();
				int radiusOfMasterFrame=heightOfMasterFrame/2;
				final int sizeOfBalls = mMasterLayout.getWidth()/6;
				
				mResumeGameButton.setLayoutParams(new FrameLayout.LayoutParams(sizeOfBalls*2, sizeOfBalls*2, Gravity.CENTER));
				mNewGameButton.setLayoutParams(new FrameLayout.LayoutParams(sizeOfBalls*2, sizeOfBalls*2, Gravity.CENTER));
				
				mResumeGameButton.setTranslationY(radiusOfMasterFrame+sizeOfBalls);
				mNewGameButton.setTranslationY(radiusOfMasterFrame+sizeOfBalls);
				
				
				///special logging
				
				
				
				ObjectAnimator buttonUp = ObjectAnimator.ofFloat(mNewGameButton, "TranslationY", radiusOfMasterFrame+sizeOfBalls,0);
				buttonUp.setStartDelay(ANIMATIONTIMING*2+ANIMATIONTIMING/6);
				buttonUp.setDuration(ANIMATIONTIMING*2);
				buttonUp.setInterpolator(new OvershootInterpolator());
				buttonUp.addListener(new AnimatorListenerAdapter() {
					@Override
					public void onAnimationEnd(Animator animation) {
						// TODO Auto-generated method stub
						mResumeGameButton.animate().translationY(sizeOfBalls+sizeOfBalls/2).setDuration(ANIMATIONTIMING);
					}
					
				});
				buttonUp.addUpdateListener(new AnimatorUpdateListener() {
					
					@Override
					public void onAnimationUpdate(ValueAnimator animation) {
						// TODO Auto-generated method stub
						mResumeGameButton.setTranslationY((Float)animation.getAnimatedValue());
					}
				});
				
				buttonUp.start();
				
				
				
				ballFrame = new FrameLayout(getApplicationContext());
				ballFrame.setLayoutParams(new FrameLayout.LayoutParams(heightOfMasterFrame,heightOfMasterFrame,Gravity.CENTER));
				
				
				int mColors[] = new int[8];
				mColors[0] = R.drawable.red_ball;
				mColors[1] = R.drawable.orange_ball;
				mColors[2] = R.drawable.yellow_ball;
				mColors[3] = R.drawable.green_ball;
				mColors[4] = R.drawable.blue_ball;
				mColors[5] = R.drawable.light_blue_ball;
				mColors[6] = R.drawable.purple_ball;
				mColors[7] = R.drawable.pink_ball;
				
				mBalls = new ImageView[8];
				
				for (int i = 0; i < mColors.length; i++) {
					ImageView ball = new ImageView(getApplicationContext());
					
					ball.setLayoutParams(new FrameLayout.LayoutParams(sizeOfBalls, sizeOfBalls, Gravity.CENTER));
					ball.setScaleType(ScaleType.CENTER_INSIDE);
					ball.setImageResource(mColors[i]);
					mBalls[i]=ball;
					ballFrame.addView(ball);
				}
				
				Float startX = mBalls[0].getX();
				Float startY = mBalls[0].getY();
				
				ballsTranslateEndX = new float[8];
				Arrays.fill(ballsTranslateEndX, 0f);
				ballsTranslateEndY = new float[8];
				Arrays.fill(ballsTranslateEndY, 0f);

				ballsTranslateStartX = new float[8];
				Arrays.fill(ballsTranslateStartX, 0f);
				ballsTranslateStartY = new float[8];
				Arrays.fill(ballsTranslateStartY, 0f);
				
				ballsTranslateEndY[0]=startY-sizeOfBalls;
				ballsTranslateEndY[1]=startY-sizeOfBalls*.707f;ballsTranslateEndX[1]=startX+sizeOfBalls*.707f;
				ballsTranslateEndX[2]=startX+sizeOfBalls;
				ballsTranslateEndY[3]=startY+sizeOfBalls*.707f;ballsTranslateEndX[3]=startX+sizeOfBalls*.707f;
				ballsTranslateEndY[4]=startX+sizeOfBalls;
				ballsTranslateEndY[5]=startY+sizeOfBalls*.707f;ballsTranslateEndX[5]=startX-sizeOfBalls*.707f;
				ballsTranslateEndX[6]=startX-sizeOfBalls;
				ballsTranslateEndY[7]=startY-sizeOfBalls*.707f;ballsTranslateEndX[7]=startX-sizeOfBalls*.707f;

				
				float downAndRight = (radiusOfMasterFrame+sizeOfBalls/2)*1.25f;
				float upAndLeft = ((radiusOfMasterFrame*-1)-sizeOfBalls/2)*1.25f;
				
				mBalls[1].setTranslationY(upAndLeft);
				mBalls[2].setTranslationY(upAndLeft*.707f);
				mBalls[2].setTranslationX(downAndRight*.707f);
				mBalls[3].setTranslationX(downAndRight);
				mBalls[4].setTranslationX(downAndRight*.707f);
				mBalls[4].setTranslationY(downAndRight*.707f);
				mBalls[5].setTranslationY(downAndRight);
				mBalls[6].setTranslationY(downAndRight*.707f);
				mBalls[6].setTranslationX(upAndLeft*.707f);
				mBalls[7].setTranslationX(upAndLeft);
				mBalls[0].setTranslationX(upAndLeft*.707f);
				mBalls[0].setTranslationY(upAndLeft*.707f);
				
				
				
				int index = mMasterLayout.indexOfChild(findViewById(R.id.splash_screen_settings_and_title_layout));
				mMasterLayout.addView(ballFrame, index+1);
				
				ArrayList<Animator> ballsInAL = new ArrayList<Animator>();
				
				for (int i = 0; i < mBalls.length; i++) {
					
					ballsTranslateStartX[i]=mBalls[i].getTranslationX();
					ballsTranslateStartY[i]=mBalls[i].getTranslationY();
					
					PropertyValuesHolder ballTranslateInX = PropertyValuesHolder.ofFloat("TranslationX", ballsTranslateStartX[i],ballsTranslateEndX[i]);
					PropertyValuesHolder ballTranslateInY = PropertyValuesHolder.ofFloat("TranslationY", ballsTranslateStartY[i],ballsTranslateEndY[i]);
					
					ObjectAnimator ballIn = ObjectAnimator.ofPropertyValuesHolder(mBalls[i], ballTranslateInX, ballTranslateInY);
					ballIn.setDuration(ANIMATIONTIMING*3);
					ballIn.setStartDelay((ANIMATIONTIMING/6)*(7-i));
					ballsInAL.add(0,ballIn);
				}
				
				AnimatorSet ballsInAS = new AnimatorSet();
				ballsInAS.playTogether(ballsInAL);
				ballsInAS.addListener(new AnimatorListenerAdapter() {
						
						@Override
						public void onAnimationStart(Animator animation) {
							// TODO Auto-generated method stub
							//findViewById(R.id.playgamebutton).setEnabled(false);
							mNewGameButton.setClickable(false);
							mSettingsButton.setClickable(false);
							mScoresButton.setClickable(false);
							mHelpButton.setClickable(false);
						}
						
						@Override
						public void onAnimationEnd(Animator animation) {
							// TODO Auto-generated method stub
							//findViewById(R.id.playgamebutton).setEnabled(true);
							mNewGameButton.setClickable(true);
							mSettingsButton.setClickable(true);
							mScoresButton.setClickable(true);
							mHelpButton.setClickable(true);
							
							
							
							///animate fade in of bottom icons
							ImageView bottomIcons[]= new ImageView[]{mSettingsButton,mScoresButton,mHelpButton};

							for (int i = 0; i < bottomIcons.length; i++) {
								ObjectAnimator fadeIn = ObjectAnimator.ofPropertyValuesHolder(bottomIcons[i], fadeInPV);
								fadeIn.start();
							}
							
						}
						
					});
				ballsInAS.start();
				
				ballFrame.animate().rotationBy(360*30).setDuration(600*160).setInterpolator(new LinearInterpolator()).setListener(new AnimatorListenerAdapter() {
					
					public void onAnimationEnd(Animator animation) {
						// TODO Auto-generated method stub
						ballFrame.animate().rotationBy(360*30).setDuration(600*160).setInterpolator(new LinearInterpolator());
					}
					
				});
			}
		});
		
		
		
		
	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		
		
		SharedPreferences savedGame = getSharedPreferences(GamePrefrences.SavedGame, Activity.MODE_PRIVATE);
		boolean gameInProgress = savedGame.getBoolean(GamePrefrences.GAME_IN_PROGRESS, false);
		if(gameInProgress){
			mResumeGameButton.setEnabled(true);
			mResumeGameButton.setVisibility(View.VISIBLE);
		}else{
			mResumeGameButton.setEnabled(false);
			mResumeGameButton.setVisibility(View.INVISIBLE);
		}
		
	}
	
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		overridePendingTransition(0, 0);
		
		super.onStart();
		
		restoreLayoutItemVisibiility();
		
	}

	private void restoreLayoutItemVisibiility() {
		findViewById(R.id.playgamebutton).setVisibility(View.VISIBLE);
		for (int i = 0; i <8; i++) {
			mBalls[i].setVisibility(View.VISIBLE);
		}
		
		mSettingsButton.setVisibility(View.VISIBLE);
		mScoresButton.setVisibility(View.VISIBLE);
		mHelpButton.setVisibility(View.VISIBLE);
		
		mSettingsButton.setAlpha(finalAlpha);
		mScoresButton.setAlpha(finalAlpha);
		mHelpButton.setAlpha(finalAlpha);
	}
	
	public void play (View v){
		
		SharedPreferences savedGame = getSharedPreferences(GamePrefrences.SavedGame, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = savedGame.edit();
		editor.clear();
		editor.putBoolean(GamePrefrences.GAME_IN_PROGRESS, false);
		editor.commit();
		
		animateAndStart();
		
	}
	public void resumeGame(View v){
		animateAndStart();
	}

	private void animateAndStart() {
		ArrayList<Animator> ballsOutAL = new ArrayList<Animator>();
		
		for (int i = 0; i < 8; i++) {
			
			PropertyValuesHolder ballTranslateInX = PropertyValuesHolder.ofFloat("TranslationX", ballsTranslateEndX[i], ballsTranslateStartX[(i+2)%7]*1.10f);
			PropertyValuesHolder ballTranslateInY = PropertyValuesHolder.ofFloat("TranslationY", ballsTranslateEndY[i], ballsTranslateStartY[(i+2)%7]*1.10f);

			
			
			ObjectAnimator ballOut = ObjectAnimator.ofPropertyValuesHolder(mBalls[i], ballTranslateInX, ballTranslateInY);
			ballOut.setDuration(ANIMATIONTIMING*2);
			ballOut.setStartDelay((ANIMATIONTIMING/6)*(7-i));
			ballsOutAL.add(0,ballOut);
		}
		
		AnimatorSet ballsOutAS = new AnimatorSet();
		ballsOutAS.playTogether(ballsOutAL);
		ballsOutAS.addListener(new AnimatorListenerAdapter() {
				
				@Override
				public void onAnimationStart(Animator animation) {
					// TODO Auto-generated method stub
					mNewGameButton.setVisibility(View.INVISIBLE);
					mResumeGameButton.setVisibility(View.INVISIBLE);
					mSettingsButton.setVisibility(View.INVISIBLE);
					mScoresButton.setVisibility(View.INVISIBLE);
					mHelpButton.setVisibility(View.INVISIBLE);
				}
				
				@Override
				public void onAnimationEnd(Animator animation) {
					// TODO Auto-generated method stub
					
					for (int i = 0; i < 8; i++) {
						mBalls[i].setVisibility(View.INVISIBLE);
						mBalls[i].setTranslationX(ballsTranslateEndX[i]);
						mBalls[i].setTranslationY(ballsTranslateEndY[i]);
					}
					
					startActivityForResult(new Intent(getApplicationContext(), PlayGameActivity.class), PLAY_GAME_REQUEST);
					
				}
				
			});
		ballsOutAS.start();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.settingsButton:
			
			startActivityForResult(new Intent(getApplicationContext(), ChangeDifficulty.class), CHANGE_DIFFICULTY_REQUEST);
			
			break;
		case R.id.scoresButton:
			
			//launch scoresView activity
			startActivity(new Intent(this, ScoresView.class));
			
			break;
		case R.id.helpButton:
			
			startActivityForResult(new Intent(this, Help.class), HELP_REQUEST);
			
			
			break;

		default:
			break;
		}
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		switch (requestCode) {
		case CHANGE_DIFFICULTY_REQUEST:
			if(resultCode==RESULT_OK){
				int newDifficulty = data.getIntExtra(DIFFICULTY_KEY, GamePrefrences.NORMAL_DIFFICULTY);
				
				SharedPreferences settings = getSharedPreferences(GamePrefrences.SETTINGS, MODE_PRIVATE);
				
				int oldDifficuly = settings.getInt(GamePrefrences.DIFFICULTY, GamePrefrences.NORMAL_DIFFICULTY);
				
				if(newDifficulty!=oldDifficuly || newDifficulty==GamePrefrences.CUSTOM_DIFFICULTY){
					SharedPreferences savedGame = getSharedPreferences(GamePrefrences.SavedGame, MODE_PRIVATE);
					SharedPreferences.Editor savedGameClear = savedGame.edit();
					savedGameClear.clear();
					savedGameClear.putBoolean(GamePrefrences.GAME_IN_PROGRESS, false);
					savedGameClear.commit();

					SharedPreferences.Editor editor = settings.edit();
					editor.putInt(GamePrefrences.DIFFICULTY, newDifficulty);
					
					if(newDifficulty==GamePrefrences.CUSTOM_DIFFICULTY){
						editor.putInt(GamePrefrences.CUSTOM_NUMCOLORS, data.getIntExtra(GamePrefrences.CUSTOM_NUMCOLORS, GamePrefrences.NumColors[GamePrefrences.NORMAL_DIFFICULTY]));
						editor.putInt(GamePrefrences.CUSTOM_LENGTHOFCODE, data.getIntExtra(GamePrefrences.CUSTOM_LENGTHOFCODE, GamePrefrences.LenghtOfCode[GamePrefrences.NORMAL_DIFFICULTY]));
						editor.putInt(GamePrefrences.CUSTOM_NUMGUESSES, data.getIntExtra(GamePrefrences.CUSTOM_NUMGUESSES, GamePrefrences.NumGuesses[GamePrefrences.NORMAL_DIFFICULTY]));
					}
					
					
					editor.commit();
				}
				
			} 
			break;
		case PLAY_GAME_REQUEST:
			if(resultCode==RESULT_OK){
				if(data.hasExtra(GO_TO_SETTINGS_KEY)){
					if(data.getBooleanExtra(GO_TO_SETTINGS_KEY, false)){
						startActivityForResult(new Intent(getApplicationContext(), ChangeDifficulty.class), CHANGE_DIFFICULTY_REQUEST);
					}
				}
				if(data.hasExtra(GO_TO_HELP_KEY)){
					if(data.getBooleanExtra(GO_TO_HELP_KEY, false)){
						startActivityForResult(new Intent(getApplicationContext(), Help.class), HELP_REQUEST);
					}
				}
			}

		case HELP_REQUEST:
			if(resultCode==RESULT_OK){
				if(data.hasExtra(HELP_TEXT_KEY)){
					boolean newHelpTextState = data.getBooleanExtra(HELP_TEXT_KEY, false);
					getSharedPreferences(GamePrefrences.SETTINGS, MODE_PRIVATE).edit().putBoolean(GamePrefrences.HELP_TEXT, newHelpTextState).commit();
				}
			}
			break;
		default:
			break;
		}
		
	}
	
	
	
}
