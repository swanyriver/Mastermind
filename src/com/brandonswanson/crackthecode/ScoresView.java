package com.brandonswanson.crackthecode;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

import com.brandonswanson.crackthecode.R;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ScoresView extends Activity implements OnClickListener, OnTouchListener {

	private ArrayList<TextView> scoreTextViews;


	private int[] difficulty;
	private int mWidthOfFrame;
	private int mCurrentlySelected = 0;    
	private long mSlideOneFrameDuration = 300;
	

	//slide touch variables
	private float mXTouchDown;
	private float mStartingTranslation;
	private float mLastX;
	private boolean mTouchSliding=false;
	private boolean mSlidingLeft;


	private FrameLayout mScoreSlider;
	private FrameLayout mScoreHightlighter;


	public static final String HEIGHT_OF_FRAME= "HEIGHTOFFRAME";

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.scores_view_layout);

		scoreTextViews = new ArrayList<TextView>();

		scoreTextViews.add(GamePrefrences.EASY_DIFFICULTY, (TextView) findViewById(R.id.scoresEasyTextView));
		scoreTextViews.add(GamePrefrences.NORMAL_DIFFICULTY, (TextView) findViewById(R.id.scoresNormalTextView));
		scoreTextViews.add(GamePrefrences.HARD_DIFFICULTY, (TextView) findViewById(R.id.scoresHardTextView));
		scoreTextViews.add(GamePrefrences.INSANE_DIFFICULTY, (TextView) findViewById(R.id.scoresInsaneTextView));


		findViewById(R.id.easy_label).setOnClickListener(this);
		findViewById(R.id.easy_label).setTag(0);

		findViewById(R.id.normal_label).setOnClickListener(this);
		findViewById(R.id.normal_label).setTag(1);

		findViewById(R.id.hard_label).setOnClickListener(this);
		findViewById(R.id.hard_label).setTag(2);

		findViewById(R.id.insane_label).setOnClickListener(this);
		findViewById(R.id.insane_label).setTag(3);

		mScoreHightlighter= (FrameLayout) findViewById(R.id.label_highlighter);
		mScoreHightlighter.setPivotX(0);
		mScoreHightlighter.setScaleX(.25f);


		difficulty = new int[]{GamePrefrences.EASY_DIFFICULTY,GamePrefrences.NORMAL_DIFFICULTY,GamePrefrences.HARD_DIFFICULTY,GamePrefrences.INSANE_DIFFICULTY};



		//set Text Views TEXT
		DecimalFormat df = new DecimalFormat("##.##");
		df.setRoundingMode(RoundingMode.DOWN);


		SharedPreferences records = getSharedPreferences(GamePrefrences.RECORDS, MODE_PRIVATE);

		for (int i = 0; i < difficulty.length; i++) {

			String scoresString = new String();
			scoresString+="Total Number of Games Played: "+records.getInt(GamePrefrences.GAMES_FINISHED+difficulty[i], 0);
			scoresString+="\n\nNumber of Games Won: "+records.getInt(GamePrefrences.GAMES_WON+difficulty[i], 0);
			scoresString+="\n\nNumber of Games Lost:"+records.getInt(GamePrefrences.GAMES_LOST+difficulty[i], 0);
			scoresString+="\n\nAverage Number of Guesses to Win: "+df.format(records.getFloat(GamePrefrences.AVERAGE_GUESSES_TO_WIN+difficulty[i], 0));
			scoreTextViews.get(difficulty[i]).setText(scoresString);

		}



		findViewById(R.id.scoresView).post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				int width=findViewById(R.id.scoresView).getWidth();
				findViewById(R.id.scoresView).setLayoutParams(new FrameLayout.LayoutParams(width, LayoutParams.WRAP_CONTENT));

			}
		});

		mScoreSlider=(FrameLayout) findViewById(R.id.Scores_Slider);
		mScoreSlider.setOnTouchListener(this);
		mScoreSlider.setClickable(true);

		mScoreSlider.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				mWidthOfFrame=mScoreSlider.getWidth();
				mScoreSlider.setLayoutParams(new LinearLayout.LayoutParams(mWidthOfFrame*4,android.widget.LinearLayout.LayoutParams.WRAP_CONTENT));
				
				//set width of textviews
				
				for (int i = 0; i < difficulty.length; i++) {
					scoreTextViews.get(difficulty[i]).setLayoutParams(new FrameLayout.LayoutParams(scoreTextViews.get(difficulty[i]).getWidth(), LayoutParams.WRAP_CONTENT));
					
				}

				for (int i = 1; i < difficulty.length; i++) {
					scoreTextViews.get(difficulty[i]).setTranslationX(mWidthOfFrame*i);

				}
				
				///Initialize to selected difficulty
				SharedPreferences settings = getSharedPreferences(GamePrefrences.SETTINGS, MODE_PRIVATE);
				int savedDifficulty=settings.getInt(GamePrefrences.DIFFICULTY, GamePrefrences.NORMAL_DIFFICULTY);
				if(savedDifficulty==GamePrefrences.CUSTOM_DIFFICULTY)savedDifficulty=GamePrefrences.NORMAL_DIFFICULTY;
				for (int i = 0; i < difficulty.length; i++)if(difficulty[i]==savedDifficulty)mCurrentlySelected=i;

				mScoreSlider.setTranslationX(mCurrentlySelected * mWidthOfFrame * -1);
				mScoreHightlighter.setTranslationX(mCurrentlySelected * (mWidthOfFrame/4));
			}
		});


	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		int target = (Integer) v.getTag();

		animateScoreSlide(target);
	}

	private void animateScoreSlide(int target) {
		ObjectAnimator slideText = ObjectAnimator.ofFloat(mScoreSlider, "TranslationX", mScoreSlider.getTranslationX(), target * mWidthOfFrame * -1);
		ObjectAnimator slideHighlighter = ObjectAnimator.ofFloat(mScoreHightlighter, "TranslationX", mScoreHightlighter.getTranslationX(), target * (mWidthOfFrame/4));

		AnimatorSet slideThem = new AnimatorSet();

		slideThem.playTogether(slideText,slideHighlighter);
		
		
		float absoluteDistance = Math.abs(mScoreSlider.getTranslationX() - target*mWidthOfFrame*-1);
		float relativeDistance = absoluteDistance/mWidthOfFrame;
		slideThem.setDuration((long) (mSlideOneFrameDuration*relativeDistance));
		slideThem.start();

		//Log.d("debug","current:"+ mCurrentlySelected + " target:" + target + " absoluteDist:"+ absoluteDistance + " relativeDist:" + relativeDistance + " duration:" + slideThem.getDuration());
		
		mCurrentlySelected=target;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			
			mXTouchDown=event.getX();
			mStartingTranslation=mScoreSlider.getTranslationX();
			mLastX=mXTouchDown;
			
			break;
		case MotionEvent.ACTION_MOVE:
			
			if(!mTouchSliding){
				if(Math.abs(event.getX()-mXTouchDown)>5){
					mTouchSliding=true;
					if(event.getX()<mXTouchDown)mSlidingLeft=true;
					else mSlidingLeft=false;
				}
			}
			if(mTouchSliding){
				if((mSlidingLeft && event.getX()<mLastX)|| !mSlidingLeft && event.getX()>mLastX){
					mScoreSlider.setTranslationX(mStartingTranslation+(event.getX()-mXTouchDown));
					mLastX=event.getX();
				}else if(Math.abs(event.getX()-mLastX)>10){
					mSlidingLeft=!mSlidingLeft;
					mLastX=event.getX();
				}
			}
			
			
			break;
		case MotionEvent.ACTION_UP:
			
			if(Math.abs(event.getX()-mXTouchDown)>mWidthOfFrame/6){
				//move to target
				if(event.getX()<mXTouchDown&&mCurrentlySelected<difficulty.length-1){
					animateScoreSlide(mCurrentlySelected+1);
				}else if(event.getX()>mXTouchDown&&mCurrentlySelected>0){
					animateScoreSlide(mCurrentlySelected-1);
				}else{
					animateScoreSlide(mCurrentlySelected);
				}
				
			}else{
				//return to current
				animateScoreSlide(mCurrentlySelected);
			}
			
			mTouchSliding=false;
			break;
		case MotionEvent.ACTION_CANCEL:
			animateScoreSlide(mCurrentlySelected);
			mTouchSliding=false;
			break;
		
		}
		
		return false;
	}
}
