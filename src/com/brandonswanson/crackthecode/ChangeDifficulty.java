package com.brandonswanson.crackthecode;

import java.util.ArrayList;
import java.util.Arrays;

import com.brandonswanson.crackthecode.R;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.TimeInterpolator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ChangeDifficulty extends Activity implements OnClickListener, OnTouchListener {


	private static final int NUM_COLORS=0;
	private static final int LENGTH_OF_CODE=1;
	private static final int NUM_GUESSES=2;

	private int mDifficultySelected;
	private int mDifficulty[];
	private ArrayList<FrameLayout> mDifficulyFrames;
	private ArrayList mSettings[];
	private ArrayList<Integer> mNumColorsSettings;
	private ArrayList<Integer> mLengthofCodeSettings;
	private ArrayList<Integer> mNumGuessesSettings;

	/*private FrameLayout mNumColorsFrame;
	private FrameLayout mLengthofCodeFrame;
	private FrameLayout mNumGuessesFrame;*/
	private FrameLayout mSliderFrames[];

	private int mNumberImageIDs[];

	private int mFrameWidth;
	private final long mSlideOneFrameDuration = 400;
	private Interpolator mSlideInterpolater = new OvershootInterpolator(1.25f);
	private TimeInterpolator mGrowNumberInterpolater = new OvershootInterpolator();

	private float mBallScale = .75f;
	private float mNumberScaleSmall = .6f;
	private float mNumberScaleLarge = .9f;
	private ImageView mNumberViews[][];
	private int mNumberHiglighted[];

	private PropertyValuesHolder mPVballShrinkX = PropertyValuesHolder.ofFloat("ScaleX", 1,.6f);
	private PropertyValuesHolder mPVballShrinkY = PropertyValuesHolder.ofFloat("ScaleY", 1,.6f);
	private PropertyValuesHolder mPVballGrowX = PropertyValuesHolder.ofFloat("ScaleX", .6f,1);
	private PropertyValuesHolder mPVballGrowY = PropertyValuesHolder.ofFloat("ScaleY", .6f,1);

	private float mXTouchDown;
	private float mStartingTranslation;
	private float mLastX;
	private boolean mTouchSliding = false;
	private boolean mSlidingLeft;
	private int mSettingSelected[];

	private boolean mAnimating[] = new boolean[]{false,false,false};
	private AnimatorListener mAnimListener[] = new AnimatorListener[3];
	private int mTouchPointerID=-1;
	private AnimatorSet mCustomDifficultAnimation = new AnimatorSet();
	private int mSavedDifficulty;
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.change_difficulty_layout);

		mDifficulyFrames = new ArrayList<FrameLayout>();

		mDifficulyFrames.add(GamePrefrences.EASY_DIFFICULTY, (FrameLayout) findViewById(R.id.change_difficulty_easy_button_frame));
		mDifficulyFrames.add(GamePrefrences.NORMAL_DIFFICULTY, (FrameLayout) findViewById(R.id.change_difficulty_normal_button_frame));
		mDifficulyFrames.add(GamePrefrences.HARD_DIFFICULTY, (FrameLayout) findViewById(R.id.change_difficulty_hard_button_frame));
		mDifficulyFrames.add(GamePrefrences.INSANE_DIFFICULTY, (FrameLayout) findViewById(R.id.change_difficulty_insane_button_frame));
		mDifficulyFrames.add(GamePrefrences.CUSTOM_DIFFICULTY, (FrameLayout) findViewById(R.id.change_difficulty_custom_button_frame));


		mSliderFrames = new FrameLayout[3];

		mSliderFrames[NUM_COLORS] = (FrameLayout) findViewById(R.id.change_difficulty_num_colors_frame);
		mSliderFrames[LENGTH_OF_CODE] = (FrameLayout) findViewById(R.id.change_difficulty_length_of_code_frame);
		mSliderFrames[NUM_GUESSES] = (FrameLayout) findViewById(R.id.change_difficulty_num_guesses_frame);

		mSliderFrames[NUM_COLORS].setTag(NUM_COLORS);
		mSliderFrames[LENGTH_OF_CODE].setTag(LENGTH_OF_CODE);
		mSliderFrames[NUM_GUESSES].setTag(NUM_GUESSES);

		for (int i = 0; i < mSliderFrames.length; i++) {
			mSliderFrames[i].setOnTouchListener(this);
			mSliderFrames[i].setClickable(true);
		}


		mDifficulty = new int[]{GamePrefrences.EASY_DIFFICULTY,GamePrefrences.NORMAL_DIFFICULTY,GamePrefrences.HARD_DIFFICULTY,GamePrefrences.INSANE_DIFFICULTY,GamePrefrences.CUSTOM_DIFFICULTY};



		mNumColorsSettings = new ArrayList<Integer>(); 
		//mNumColorsSettings.addAll(Arrays.asList(4,5,6,7,8));
		mNumColorsSettings.add(0, 4);
		mNumColorsSettings.add(1, 5);
		mNumColorsSettings.add(2, 6);
		mNumColorsSettings.add(3, 7);
		mNumColorsSettings.add(4, 8);

		mLengthofCodeSettings = new ArrayList<Integer>(); 
		//mLengthofCodeSettings.addAll(Arrays.asList(3,4,5,6,7));
		mLengthofCodeSettings.add(0, 3);
		mLengthofCodeSettings.add(1, 4);
		mLengthofCodeSettings.add(2, 5);
		mLengthofCodeSettings.add(3, 6);
		mLengthofCodeSettings.add(4, 7);

		mNumGuessesSettings = new ArrayList<Integer>(); 
		//mNumGuessesSettings.addAll(Arrays.asList(4,5,6,7,8,9,10));
		mNumGuessesSettings.add(0, 4);
		mNumGuessesSettings.add(1, 5);
		mNumGuessesSettings.add(2, 6);
		mNumGuessesSettings.add(3, 7);
		mNumGuessesSettings.add(4, 8);
		mNumGuessesSettings.add(5, 9);
		mNumGuessesSettings.add(6, 10);

		mSettings= new ArrayList[3];
		mSettings[NUM_COLORS]=mNumColorsSettings;
		mSettings[LENGTH_OF_CODE]=mLengthofCodeSettings;
		mSettings[NUM_GUESSES]=mNumGuessesSettings;




		SharedPreferences settings = getSharedPreferences(GamePrefrences.SETTINGS, MODE_PRIVATE);
		mSavedDifficulty = settings.getInt(GamePrefrences.DIFFICULTY, GamePrefrences.NORMAL_DIFFICULTY);
		mDifficultySelected = mSavedDifficulty;

		mSettingSelected= new int[3];

		if(mDifficultySelected==GamePrefrences.CUSTOM_DIFFICULTY){

			int lengthOfCode=settings.getInt(GamePrefrences.CUSTOM_LENGTHOFCODE, GamePrefrences.LenghtOfCode[GamePrefrences.NORMAL_DIFFICULTY]);
			int numColors=settings.getInt(GamePrefrences.CUSTOM_NUMCOLORS, GamePrefrences.NumColors[GamePrefrences.NORMAL_DIFFICULTY]);
			int numGuesses=settings.getInt(GamePrefrences.CUSTOM_NUMGUESSES,GamePrefrences.NumGuesses[GamePrefrences.NORMAL_DIFFICULTY]);

			mSettingSelected[NUM_COLORS] = mNumColorsSettings.indexOf(numColors);
			mSettingSelected[LENGTH_OF_CODE] = mLengthofCodeSettings.indexOf(lengthOfCode);
			mSettingSelected[NUM_GUESSES] = mNumGuessesSettings.indexOf(numGuesses);
		}else getIndexofSettings();


		mDifficulyFrames.get(mDifficultySelected).getChildAt(0).setScaleX(1);
		mDifficulyFrames.get(mDifficultySelected).getChildAt(0).setScaleY(1);

		for (int i = 0; i < mDifficulty.length; i++) {
			mDifficulyFrames.get(mDifficulty[i]).setTag(mDifficulty[i]);
			mDifficulyFrames.get(mDifficulty[i]).setOnClickListener(this);

		}

		createListeners();




		mNumberImageIDs = new int[]{0,0,0,R.drawable.three,R.drawable.four,R.drawable.five,R.drawable.six,R.drawable.seven,R.drawable.eight,R.drawable.nine,R.drawable.ten};

		findViewById(R.id.change_difficulty_wheel_layout).post(new Runnable() {



			@Override
			public void run() {

				//measure frame
				final int frameHeight = mSliderFrames[0].getHeight();
				mFrameWidth = findViewById(R.id.change_difficulty_wheel_layout).getWidth();

				int colors[] = new int[8];
				colors[0] = R.drawable.red_ball;
				colors[1] = R.drawable.orange_ball;
				colors[2] = R.drawable.yellow_ball;
				colors[3] = R.drawable.green_ball;
				colors[4] = R.drawable.blue_ball;
				colors[5] = R.drawable.light_blue_ball;
				colors[6] = R.drawable.purple_ball;
				colors[7] = R.drawable.pink_ball;

				FrameLayout.LayoutParams ballLayoutPrams = new FrameLayout.LayoutParams(mFrameWidth, frameHeight, Gravity.LEFT);



				ArrayList<ArrayList<Integer>> settings = new ArrayList<ArrayList<Integer>>();
				settings.add(mNumColorsSettings);
				settings.add(mLengthofCodeSettings);
				settings.add(mNumGuessesSettings);

				mNumberViews = new ImageView[3][7];
				mNumberHiglighted = new int[3];

				for (int x = 0; x < mSliderFrames.length; x++) {
					//ImageView balls[] = new ImageView[8];

					for (int i = 0; i < settings.get(x).size(); i++) {
						ImageView ball = new ImageView(getApplicationContext());
						ball.setLayoutParams(ballLayoutPrams);
						ball.setScaleType(ScaleType.FIT_CENTER);
						ball.setImageResource(colors[i]);
						ball.setTranslationX(mFrameWidth*i); // CHECK HERE

						ball.setScaleX(mBallScale);
						ball.setScaleY(mBallScale);

						mSliderFrames[x].addView(ball);

						ImageView number = new ImageView(getApplicationContext());
						number.setLayoutParams(ballLayoutPrams);
						number.setScaleType(ScaleType.FIT_CENTER);
						number.setImageResource(mNumberImageIDs[settings.get(x).get(i)]);
						number.setTranslationX(mFrameWidth*i);  ///CHECK HERE

						number.setScaleX(mNumberScaleSmall);
						number.setScaleY(mNumberScaleSmall);

						////add to an array///
						mNumberViews[x][i]=number;

						mSliderFrames[x].addView(number);
					}
					LinearLayout.LayoutParams frameLP = new LinearLayout.LayoutParams(mFrameWidth*settings.get(x).size(), frameHeight);
					mSliderFrames[x].setLayoutParams(frameLP);
				}

				//intialize sliders
				mSliderFrames[NUM_COLORS].setTranslationX(mFrameWidth*mSettingSelected[NUM_COLORS]*-1);
				mNumberViews[NUM_COLORS][mSettingSelected[NUM_COLORS]].setScaleX(mNumberScaleLarge);
				mNumberViews[NUM_COLORS][mSettingSelected[NUM_COLORS]].setScaleY(mNumberScaleLarge);
				mNumberHiglighted[NUM_COLORS]=mSettingSelected[NUM_COLORS];

				mSliderFrames[LENGTH_OF_CODE].setTranslationX(mFrameWidth*mSettingSelected[LENGTH_OF_CODE]*-1);
				mNumberViews[LENGTH_OF_CODE][mSettingSelected[LENGTH_OF_CODE]].setScaleX(mNumberScaleLarge);
				mNumberViews[LENGTH_OF_CODE][mSettingSelected[LENGTH_OF_CODE]].setScaleY(mNumberScaleLarge);
				mNumberHiglighted[LENGTH_OF_CODE]=mSettingSelected[LENGTH_OF_CODE];

				mSliderFrames[NUM_GUESSES].setTranslationX(mFrameWidth*mSettingSelected[NUM_GUESSES]*-1);
				mNumberViews[NUM_GUESSES][mSettingSelected[NUM_GUESSES]].setScaleX(mNumberScaleLarge);
				mNumberViews[NUM_GUESSES][mSettingSelected[NUM_GUESSES]].setScaleY(mNumberScaleLarge);
				mNumberHiglighted[NUM_GUESSES]=mSettingSelected[NUM_GUESSES];


				//set textview backgrounds
				TextView label[] = new TextView[]{
						(TextView) findViewById(R.id.change_difficulty_num_colors_textview),
						(TextView) findViewById(R.id.change_difficulty_lenght_of_code_textview),
						(TextView) findViewById(R.id.change_difficulty_num_guesses_textview)
				};

				for (int i = 0; i < label.length; i++) {
					label[i].setLayoutParams(new LinearLayout.LayoutParams(label[i].getWidth(), label[i].getHeight()));
					label[i].setTranslationX((mFrameWidth-label[i].getWidth())/2);
					label[i].setBackgroundResource(R.drawable.dark_wood_background);
				}

			}
		});


	}

	private void createCustomAnimation() {
		mCustomDifficultAnimation=new AnimatorSet();

		ArrayList<Animator> animators = new ArrayList<Animator>();
		for (int i = 0; i < mSliderFrames.length; i++) {
			int modifier = 1;
			if(i%2==1)modifier=-1;
			ObjectAnimator slideFrame = ObjectAnimator.ofFloat(mSliderFrames[i], "TranslationX", 
					mSliderFrames[i].getTranslationX(),
					mSliderFrames[i].getTranslationX()-((mFrameWidth/6)*modifier),
					mSliderFrames[i].getTranslationX(),
					mSliderFrames[i].getTranslationX()+((mFrameWidth/6)*modifier),
					mSliderFrames[i].getTranslationX());
			slideFrame.setRepeatCount(2);
			animators.add(slideFrame);		
		}

		PropertyValuesHolder PVballGrowShrinkX = PropertyValuesHolder.ofFloat("ScaleX", .6f,1,.6f);
		PropertyValuesHolder PVballGrowShrinkY = PropertyValuesHolder.ofFloat("ScaleY", .6f,1,.6f);

		ObjectAnimator ballGrowShrink = ObjectAnimator.ofPropertyValuesHolder(mDifficulyFrames.get(GamePrefrences.CUSTOM_DIFFICULTY).getChildAt(0), PVballGrowShrinkX,PVballGrowShrinkY);
		ballGrowShrink.setRepeatCount(2);
		animators.add(ballGrowShrink);

		mCustomDifficultAnimation.playTogether(animators);

		mCustomDifficultAnimation.setInterpolator(new LinearInterpolator());
		mCustomDifficultAnimation.setDuration(900);

		for (int i = 0; i < mSliderFrames.length; i++)mCustomDifficultAnimation.addListener(mAnimListener[i]);

	}

	private void createListeners() {
		mAnimListener[NUM_COLORS] = new AnimatorListenerAdapter() {
			@Override
			public void onAnimationStart(Animator animation) {mAnimating[NUM_COLORS]=true; mSliderFrames[NUM_COLORS].setEnabled(false);}

			@Override
			public void onAnimationEnd(Animator animation) {mAnimating[NUM_COLORS]=false; mSliderFrames[NUM_COLORS].setEnabled(true);}
		};
		mAnimListener[LENGTH_OF_CODE] = new AnimatorListenerAdapter() {
			@Override
			public void onAnimationStart(Animator animation) {mAnimating[LENGTH_OF_CODE]=true; mSliderFrames[LENGTH_OF_CODE].setEnabled(false);}

			@Override
			public void onAnimationEnd(Animator animation) {mAnimating[LENGTH_OF_CODE]=false;  mSliderFrames[LENGTH_OF_CODE].setEnabled(true);}
		};
		mAnimListener[NUM_GUESSES] = new AnimatorListenerAdapter() {
			@Override
			public void onAnimationStart(Animator animation) {mAnimating[NUM_GUESSES]=true; mSliderFrames[NUM_GUESSES].setEnabled(false);}

			@Override
			public void onAnimationEnd(Animator animation) {mAnimating[NUM_GUESSES]=false;  mSliderFrames[NUM_GUESSES].setEnabled(true);}
		};
	}

	private void getIndexofSettings() {
		mSettingSelected[NUM_COLORS] = mNumColorsSettings.indexOf(GamePrefrences.NumColors[mDifficultySelected]);
		mSettingSelected[LENGTH_OF_CODE] = mLengthofCodeSettings.indexOf(GamePrefrences.LenghtOfCode[mDifficultySelected]);
		mSettingSelected[NUM_GUESSES] = mNumGuessesSettings.indexOf(GamePrefrences.NumGuesses[mDifficultySelected]);

	}

	@Override
	public void onClick(View v) {
		if(!mTouchSliding&&!mAnimating[NUM_COLORS]&&!mAnimating[LENGTH_OF_CODE]&&!mAnimating[NUM_GUESSES]){

			int difficultyClicked = (Integer) v.getTag();

			if (difficultyClicked==GamePrefrences.CUSTOM_DIFFICULTY){

				if(mDifficultySelected==GamePrefrences.CUSTOM_DIFFICULTY) return;
				else{
					createCustomAnimation();
					mCustomDifficultAnimation.start();
					Toast.makeText(this, "Slide the Settings to Select a Custom Difficulty", Toast.LENGTH_LONG).show();
				}


			}else{

				if(mDifficultySelected!=difficultyClicked){
					changeHighlightedDifficulty(difficultyClicked);

					mDifficultySelected=difficultyClicked;
					getIndexofSettings();
					moveFrames();
				}
			}
		}

	}

	private void changeHighlightedDifficulty(int difficultyClicked) {

		ObjectAnimator.ofPropertyValuesHolder(mDifficulyFrames.get(mDifficultySelected).getChildAt(0), mPVballShrinkX,mPVballShrinkY)
		.setDuration(mSlideOneFrameDuration/2*3)
		.start();
		ObjectAnimator.ofPropertyValuesHolder(mDifficulyFrames.get(difficultyClicked).getChildAt(0), mPVballGrowX,mPVballGrowY)
		.setDuration(mSlideOneFrameDuration/2*3)
		.start();

	}

	private void moveFrames() {


		int categories[] = new int[]{NUM_COLORS,LENGTH_OF_CODE,NUM_GUESSES};
		for (int i = 0; i < categories.length; i++) {
			animateSettingSlide(mSettingSelected[categories[i]], mSliderFrames[categories[i]]);
		}

	}

	private void animateSettingSlide(int target, View mScoreSlider) {

		int indexOfFrame = (Integer) mScoreSlider.getTag();

		AnimatorSet SlideThem = new AnimatorSet();

		//sliding
		ObjectAnimator slideFrame = ObjectAnimator.ofFloat(mScoreSlider, "TranslationX", mScoreSlider.getTranslationX(), target * mFrameWidth * -1);

		if(mScoreSlider.getTranslationX()== target * mFrameWidth * -1)return;

		float absoluteDistance = Math.abs(mScoreSlider.getTranslationX() - target*mFrameWidth*-1);
		float relativeDistance = absoluteDistance/mFrameWidth;
		slideFrame.setDuration((long) (mSlideOneFrameDuration*relativeDistance));
		slideFrame.setInterpolator(mSlideInterpolater);


		//number Shrinking
		ImageView numbertoShrink = mNumberViews[indexOfFrame][mNumberHiglighted[indexOfFrame]];
		mNumberHiglighted[indexOfFrame]=mSettingSelected[indexOfFrame];
		if(numbertoShrink.getScaleX()==mNumberScaleLarge){
			PropertyValuesHolder shrinkNumberX = PropertyValuesHolder.ofFloat("ScaleX", numbertoShrink.getScaleX(),mNumberScaleSmall);
			PropertyValuesHolder shrinkNumberY = PropertyValuesHolder.ofFloat("ScaleY", numbertoShrink.getScaleY(),mNumberScaleSmall);
			ObjectAnimator shrinkNumber = ObjectAnimator.ofPropertyValuesHolder(numbertoShrink, shrinkNumberX,shrinkNumberY);
			shrinkNumber.setDuration(mSlideOneFrameDuration/6);
			shrinkNumber.setInterpolator(null);

			SlideThem.playTogether(shrinkNumber,slideFrame);
		}else SlideThem.play(slideFrame);


		//grow number
		ImageView numbertoGrow = mNumberViews[indexOfFrame][mSettingSelected[indexOfFrame]];
		PropertyValuesHolder growNumberX = PropertyValuesHolder.ofFloat("ScaleX", numbertoGrow.getScaleX(),mNumberScaleLarge);
		PropertyValuesHolder growNumberY = PropertyValuesHolder.ofFloat("ScaleY", numbertoGrow.getScaleY(),mNumberScaleLarge);
		ObjectAnimator growNumber = ObjectAnimator.ofPropertyValuesHolder(numbertoGrow, growNumberX,growNumberY);
		growNumber.setDuration(mSlideOneFrameDuration);
		growNumber.setInterpolator(mGrowNumberInterpolater);

		//make and start animator set
		SlideThem.play(growNumber).after(slideFrame);

		SlideThem.addListener(mAnimListener[indexOfFrame]);

		SlideThem.start();

		//Log.d("debug","current:"+ mCurrentlySelected + " target:" + target + " absoluteDist:"+ absoluteDistance + " relativeDist:" + relativeDistance + " duration:" + slideThem.getDuration());

	}


	public void chooseDifficultyClick(View v){
		makeDifficultySelectionWithWarning();
	}

	private void makeDifficultySelectionWithWarning() {

		boolean savedGameinProgress = getSharedPreferences(GamePrefrences.SavedGame, MODE_PRIVATE).getBoolean(GamePrefrences.GAME_IN_PROGRESS, false);



		//Decision Tree
		if(mDifficultySelected==mSavedDifficulty){
			if(mDifficultySelected==GamePrefrences.CUSTOM_DIFFICULTY){
				
				if(customDifficultyUnchanged())commitChangesAndExit(false);
				else{
					if(!savedGameinProgress)commitChangesAndExit(true);
					else{
						createDestroySaveGamedialog().show();
					}
				}
				
			}else commitChangesAndExit(false); 
		}
		else{
			if(!savedGameinProgress)commitChangesAndExit(true);
			else{
				createDestroySaveGamedialog().show();
			}

		}

	}

	private boolean customDifficultyUnchanged() {
		SharedPreferences settings = getSharedPreferences(GamePrefrences.SETTINGS, MODE_PRIVATE);
		
		int savedSettings[] = new int[3];
		
		savedSettings[0]=settings.getInt(GamePrefrences.CUSTOM_NUMCOLORS, 0);
		savedSettings[1]=settings.getInt(GamePrefrences.CUSTOM_LENGTHOFCODE, 0);
		savedSettings[2]=settings.getInt(GamePrefrences.CUSTOM_NUMGUESSES, 0);
		
		int newSettings[] = new int[]{
				(Integer)mSettings[0].get(mSettingSelected[0]),
				(Integer)mSettings[1].get(mSettingSelected[1]),
				(Integer)mSettings[2].get(mSettingSelected[2]),};
				
		return Arrays.equals(savedSettings, newSettings);
	}

	private void commitChangesAndExit(boolean changeDifficulty) {
		if(changeDifficulty){
			Intent result = new Intent();
			result.putExtra(GameSplashScreen.DIFFICULTY_KEY, mDifficultySelected);

			if(mDifficultySelected==GamePrefrences.CUSTOM_DIFFICULTY){
				result.putExtra(GamePrefrences.CUSTOM_NUMCOLORS, mNumColorsSettings.get(mSettingSelected[NUM_COLORS]));
				result.putExtra(GamePrefrences.CUSTOM_LENGTHOFCODE, mLengthofCodeSettings.get(mSettingSelected[LENGTH_OF_CODE]));
				result.putExtra(GamePrefrences.CUSTOM_NUMGUESSES, mNumGuessesSettings.get(mSettingSelected[NUM_GUESSES]));
			}

			setResult(RESULT_OK, result);
		}else{
			setResult(RESULT_CANCELED);
		}

		finish();
	}

	@Override
	public void onBackPressed() {



		//Toast.makeText(this, "go Back", Toast.LENGTH_SHORT).show();
		if(mDifficultySelected==mSavedDifficulty && mDifficultySelected!=GamePrefrences.CUSTOM_DIFFICULTY) {
			commitChangesAndExit(false);
			
		}else if(mDifficultySelected==GamePrefrences.CUSTOM_DIFFICULTY && mSavedDifficulty==GamePrefrences.CUSTOM_DIFFICULTY){
			if(customDifficultyUnchanged())commitChangesAndExit(false);
			else{
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				// Add the buttons
				builder.setPositiveButton("Yes, leave it at my "+GamePrefrences.DifficultyNames[mSavedDifficulty] + " setting", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						commitChangesAndExit(false);
					}
				});
				builder.setNegativeButton("No, Change it to this "+GamePrefrences.DifficultyNames[mDifficultySelected]+ " setting", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						makeDifficultySelectionWithWarning();
					}
				});
				// Set other dialog properties, LABEL STRING

				builder.setMessage("Did you want to leave without saving these custom difficulty changes?");
				builder.setCancelable(false);

				// Create the AlertDialog
				AlertDialog dialog = builder.create();
				dialog.show();
			}
		}
		else{
			//build Dialog A "leave without changing", leave it at "saved" or change it to "selecte", finish if leave at saved
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			// Add the buttons
			builder.setPositiveButton("Yes, leave it at "+GamePrefrences.DifficultyNames[mSavedDifficulty], new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					commitChangesAndExit(false);
				}
			});
			builder.setNegativeButton("No, Change it to "+GamePrefrences.DifficultyNames[mDifficultySelected], new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					makeDifficultySelectionWithWarning();
				}
			});
			// Set other dialog properties, LABEL STRING

			builder.setMessage("Did you want to leave without changing the difficulty?");
			builder.setCancelable(false);

			// Create the AlertDialog
			AlertDialog dialog = builder.create();
			dialog.show();

		}
	}

	public AlertDialog createDestroySaveGamedialog(){
		
		
		//build Dialog B "Destroy Game", change difficulty (yes), or finish game (no) 
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		// Add the buttons
		builder.setPositiveButton("Change the Difficulty", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				//changeDifficulty=true
				commitChangesAndExit(true);
			}
		});
		builder.setNegativeButton("Finish my Game in Progress", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				//changeDifficulty=false
				commitChangesAndExit(false);
			}
		});
		// Set other dialog properties, LABEL STRING

		builder.setMessage("You have a "+GamePrefrences.DifficultyNames[mSavedDifficulty]+ " game in progress," +
				" Changing the difficulty will end this game and start a new " +
				GamePrefrences.DifficultyNames[mDifficultySelected]+ " game");
		
		builder.setCancelable(false);
		

		// Create the AlertDialog
		return  builder.create();

	}

	@Override
	public boolean onTouch(View v, MotionEvent touchEvent) {

		int frameSelected= (Integer) v.getTag();
		int pointerIndex;
		float eventX;



		int action = touchEvent.getAction();

		switch (action & MotionEvent.ACTION_MASK) {

		case MotionEvent.ACTION_DOWN:

			if(mCustomDifficultAnimation.isRunning())mCustomDifficultAnimation.end();

			if(mTouchPointerID==-1){

				mTouchPointerID=touchEvent.getPointerId(0);


				mXTouchDown=touchEvent.getX();
				mStartingTranslation=mSliderFrames[frameSelected].getTranslationX();
				//mStartingTranslation=mSettingSelected[frameSelected]*mFrameWidth*-1;
				mLastX=mXTouchDown;
			}

			break;
		case MotionEvent.ACTION_MOVE:

			pointerIndex=touchEvent.findPointerIndex(mTouchPointerID);

			boolean containsOurPointer=false;

			for (int i = 0; i < touchEvent.getPointerCount(); i++) {
				if(touchEvent.getPointerId(i)==mTouchPointerID)containsOurPointer=true;
			}

			if(containsOurPointer){

				eventX = touchEvent.getX(pointerIndex);

				///set auto advance threshold

				if(!mTouchSliding ){
					if(Math.abs(eventX-mXTouchDown)>5){
						mTouchSliding=true;
						if(eventX<mXTouchDown)mSlidingLeft=true;
						else mSlidingLeft=false;
					}
				}
				if(mTouchSliding){

					//check here for sliding on first or last item, indicate bounds somehow

					if((mSlidingLeft && eventX<mLastX)|| !mSlidingLeft && eventX>mLastX){

						mSliderFrames[frameSelected].setTranslationX(mStartingTranslation+(eventX-mXTouchDown));
						mLastX=eventX;

						float distance = Math.abs(mLastX-mXTouchDown);
						float percentTraveled = distance/(mFrameWidth/6);
						if (percentTraveled<1){
							float newScale = mNumberScaleLarge-((mNumberScaleLarge-mNumberScaleSmall)*percentTraveled);
							mNumberViews[frameSelected][mNumberHiglighted[frameSelected]].setScaleX(newScale);
							mNumberViews[frameSelected][mNumberHiglighted[frameSelected]].setScaleY(newScale);
						}
						//else end slide

					}else if(Math.abs(eventX-mLastX)>10){
						mSlidingLeft=!mSlidingLeft;
						mLastX=eventX;
					}
				}
			}

			break;
		case MotionEvent.ACTION_POINTER_UP:



			//final int upPointerIndex = (action & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
			final int upPointerIndex = touchEvent.getActionIndex();
			final int pointerId = touchEvent.getPointerId(upPointerIndex);

			if(pointerId == mTouchPointerID){


				pointerIndex=touchEvent.findPointerIndex(mTouchPointerID);
				eventX = touchEvent.getX(pointerIndex);


				finishTouchEvent(frameSelected, eventX);


			}

			break;
		case MotionEvent.ACTION_UP:


			//final int finalUpPointerIndex = (action & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
			final int finalUpPointerIndex = touchEvent.getActionIndex();
			final int finalpointerId = touchEvent.getPointerId(finalUpPointerIndex);

			if(finalpointerId == mTouchPointerID){


				pointerIndex=touchEvent.findPointerIndex(mTouchPointerID);
				eventX = touchEvent.getX(pointerIndex);


				finishTouchEvent(frameSelected, eventX);


			}

			break;
		case MotionEvent.ACTION_CANCEL:
			animateSettingSlide(mSettingSelected[frameSelected],mSliderFrames[frameSelected]);

			mTouchPointerID=-1;
			mTouchSliding=false;
			break;

		}


		return false;
	}

	private void finishTouchEvent(int frameSelected, float eventX) {
		if(Math.abs(eventX-mXTouchDown)>mFrameWidth/6){
			//move to target
			boolean moved=false;
			if(eventX<mXTouchDown&&mSettingSelected[frameSelected]<mSettings[frameSelected].size()-1){
				mSettingSelected[frameSelected]+=1;
				animateSettingSlide(mSettingSelected[frameSelected],mSliderFrames[frameSelected]);
				moved=true;

			}else if(eventX>mXTouchDown&&mSettingSelected[frameSelected]>0){
				mSettingSelected[frameSelected]-=1;
				animateSettingSlide(mSettingSelected[frameSelected],mSliderFrames[frameSelected]);
				moved=true;
			}else{
				animateSettingSlide(mSettingSelected[frameSelected],mSliderFrames[frameSelected]);
			}

			if(moved){
				if(mDifficultySelected!=GamePrefrences.CUSTOM_DIFFICULTY){
					changeHighlightedDifficulty(GamePrefrences.CUSTOM_DIFFICULTY);
					mDifficultySelected=GamePrefrences.CUSTOM_DIFFICULTY;
				}else{
					checkForSettingsMatch();
					checkForInvalidConfig(frameSelected);
				}
			}
		}else{
			//return to current
			animateSettingSlide(mSettingSelected[frameSelected],mSliderFrames[frameSelected]);
		}

		mTouchSliding=false;
		mTouchPointerID=-1;
	}

	private void checkForInvalidConfig(int frameSelected) {
		if(frameSelected==NUM_COLORS){
			if(mNumColorsSettings.get(mSettingSelected[NUM_COLORS])<mLengthofCodeSettings.get(mSettingSelected[LENGTH_OF_CODE])){
				mSettingSelected[LENGTH_OF_CODE]=mLengthofCodeSettings.indexOf(mNumColorsSettings.get(mSettingSelected[NUM_COLORS]));
				animateSettingSlide(mSettingSelected[LENGTH_OF_CODE],mSliderFrames[LENGTH_OF_CODE]);
			}
		}
		if(frameSelected==LENGTH_OF_CODE){
			if(mNumColorsSettings.get(mSettingSelected[NUM_COLORS])<mLengthofCodeSettings.get(mSettingSelected[LENGTH_OF_CODE])){
				mSettingSelected[NUM_COLORS]=mNumColorsSettings.indexOf(mLengthofCodeSettings.get(mSettingSelected[LENGTH_OF_CODE]));
				animateSettingSlide(mSettingSelected[NUM_COLORS],mSliderFrames[NUM_COLORS]);
			}
		}
	}

	private void checkForSettingsMatch() {
		int theseSetting[] = new int[]{	mNumColorsSettings.get(mSettingSelected[NUM_COLORS]),
				mLengthofCodeSettings.get(mSettingSelected[LENGTH_OF_CODE]),
				mNumGuessesSettings.get(mSettingSelected[NUM_GUESSES]),};

		for (int i = 0; i < mDifficulty.length-1; i++) {
			int preSettings[] = new int[]{GamePrefrences.NumColors[i],GamePrefrences.LenghtOfCode[i],GamePrefrences.NumGuesses[i]};
			if(Arrays.equals(theseSetting, preSettings)){
				changeHighlightedDifficulty(i);
				mDifficultySelected=i;
			}
		}

	}




}


