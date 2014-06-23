package com.brandonswanson.crackthecode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;
import java.util.StringTokenizer;

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
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnClickListener;
import android.view.View.OnDragListener;
import android.view.View.OnTouchListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.Toast;

public class PlayGameActivity extends Activity implements OnTouchListener{

	/*private static final int NUMCOLORS = 8;
	private static final int NUMGUESSES=8;
	private static final int LENGTHOFCODE=4;*/
	
	private static int NUMCOLORS;
	private static int NUMGUESSES;
	private static int LENGTHOFCODE;

	private int mColors[];

	private int mNumGuessesMade=0;

	private int mGuessSelected=0;

	///Color Wheel Variables
	private int mBallSelected[];
	private ArrayList<LinearLayout> mColorWheels;
	private ArrayList<LinkedList<ImageView>> mBalls;
	private Boolean mColorWheelsInitializing=true;
	private Boolean mColorWheelButtonHeld[];
	private Boolean mShuffleButtonHeld=false;
	private Boolean mShuffleButtonAnimationEnded;
	private long mShuffleButtonRotation=360;
	private static final int INITIALDURATION=350;
	private long shufflebegining=INITIALDURATION*2;
	private int mAcceleratedDuration[];
	private Random mGenerator = new Random();
	private boolean mColorWheelControlsEnabled=true;
	private boolean mShuffling = false;
	private boolean mDisableColorWheels=false;
	private long mRemoveGuessAnimationDelay = 0;




	private BallTouchListener mBallTouchListener;

	///layout variables
	LinearLayout.LayoutParams mWeightedWidth = new LinearLayout.LayoutParams(R.dimen.nodp,
			LinearLayout.LayoutParams.MATCH_PARENT,
			1f);
	LinearLayout.LayoutParams mWeightedHeight = new LinearLayout.LayoutParams(
			LinearLayout.LayoutParams.MATCH_PARENT,R.dimen.nodp,
			1f);
	LinearLayout.LayoutParams mFillParrentParams = new LinearLayout.LayoutParams(
			LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);

	private TimeInterpolator mDecceInterpolator = new DecelerateInterpolator();
	private TimeInterpolator mAccelInterpolator = new AccelerateInterpolator();
	private TimeInterpolator mAccelandDeceInterpolator = new AccelerateDecelerateInterpolator();
	private TimeInterpolator mBounceInterpolator = new BounceInterpolator();
	private TimeInterpolator mOvershootInterpolator = new OvershootInterpolator(2f);
	private TimeInterpolator mAnticipateInterpolator = new AnticipateInterpolator(2f);
	private TimeInterpolator mLinearInterpolator = new LinearInterpolator();


	private int mTargetIndex[]= new int[LENGTHOFCODE];


	
	private Handler mActivityHandler;
	private static final int DISMISSNEWGAMEWHEEL = 5;
	private static final int ANIMATIONCOMMENCEKEY= 3;
	private static final int SHUFFLEINITKEY= 4;
	private static final int SHUFFLECOLLUMNDELAY = 2;
	private static final int SHOWTUTORAILDIALOGKEY = 6;

	//Guess display Variables
	private LinearLayout mGuessDisplay;
	private LinearLayout mGuessHintDisplay;
	private LinearLayout mGuessprintouts[];
	private View mGuessPrintOutBalls[][];
	private LinearLayout mGuessHintsprintouts[];
	private ObjectAnimator mSlideOut;
	private float mWidthofGuessViewforSlideIn;
	private long mSlideOutLength = INITIALDURATION*2;

	private AnimatorSet mMakeGuessAnimation;
	private PropertyValuesHolder mPVScaleX = PropertyValuesHolder.ofFloat("ScaleX", 1f,.25f, .5f, .75f,1f);
	private PropertyValuesHolder mPVScaleY = PropertyValuesHolder.ofFloat("ScaleY", 1f,.25f, .5f, .75f,1f);


	///bottom pane values

	private LinearLayout mSecretComboView;
	private LinearLayout mSecretComboHiderView;
	private int mSecretComboViewHeight;
	private ImageView mSecretComboBalls[];
	private ImageView mComboHiders[];
	private AnimatorSet mHideComboAnimation;
	private AnimatorSet mShowComboAnimation;
	private ImageView mNewGameText;
	private ImageView mNewGameWheel;
	
	private ObjectAnimator mEndGameWheelUp;
	private ObjectAnimator mEndGameWheelDown;
	private AnimatorSet mEndGameWheelGrowShrink;
	private PropertyValuesHolder mPVEndGameWheelScaleX = PropertyValuesHolder.ofFloat("ScaleX", .5f, 1f, .5f);
	private PropertyValuesHolder mPVEndGameWheelScaleY = PropertyValuesHolder.ofFloat("ScaleY", .5f, 1f, .5f);
	private boolean mContinueEndWheelAnimation=true;


	//begining animation values
	int mSecretBallBeingAnimated =0;
	boolean mBeginingAnimationinProgress=false;
	private PropertyValuesHolder mPVShrinkX = PropertyValuesHolder.ofFloat("ScaleX", .8f,0f);
	private PropertyValuesHolder mPVShrinkY = PropertyValuesHolder.ofFloat("ScaleY", .8f,0f);
	private PropertyValuesHolder mPVGrowX = PropertyValuesHolder.ofFloat("ScaleX", 0f,.8f);
	private PropertyValuesHolder mPVGrowY = PropertyValuesHolder.ofFloat("ScaleY", 0f,.8f);
	
	private boolean mPleaseToastMe=false;

	private boolean mGameOver=true;


	private GameModel mGame;


	private BallDragListener mBallDragListener;
	private FrameLayout mNewGameFrame;
	private AnimatorListenerAdapter mBallShrinkAndChangeListener;
	private Random mBeginingAnimationGenerator;
	private ObjectAnimator mShrinkComboBalls;
	private ObjectAnimator mGrowComboBalls;
	private Toast mEndGameToast;
	private AnimatorSet mWinningAnimation = new AnimatorSet();
	private ImageView mEndGameWheel;
	
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
		setContentView(R.layout.play_game_layout);

		mColors = new int[8];
		mColors[0] = R.drawable.red_ball;
		mColors[1] = R.drawable.orange_ball;
		mColors[2] = R.drawable.yellow_ball;
		mColors[3] = R.drawable.green_ball;
		mColors[4] = R.drawable.blue_ball;
		mColors[5] = R.drawable.light_blue_ball;
		mColors[6] = R.drawable.purple_ball;
		mColors[7] = R.drawable.pink_ball;
		

		
		
		getSettings();

		createHandlersandListeners();


		initializeColorWheels();
		//Log.d("INIT", "initialized colorwheels");
		initializeGuessPane();
		//Log.d("INIT", "initialized guesspane");
		intializeBottomPane();
		//Log.d("INIT", "initialized bottom pane");


		
		enableControlButtons(false);

		this.findViewById(R.id.MasterLayout).post(new Runnable() {
			

			@Override
			public void run() {
				
				SharedPreferences savedGame = getSharedPreferences(GamePrefrences.SavedGame, Activity.MODE_PRIVATE);
				boolean gameInProgress = savedGame.getBoolean(GamePrefrences.GAME_IN_PROGRESS, false);
				
				if(gameInProgress){
					resumeGame();
				}else beginGame();
				
			}
		});
		//beginGame();
	}
	
	private void getSettings(){
		SharedPreferences settings = getSharedPreferences(GamePrefrences.SETTINGS, Activity.MODE_PRIVATE);
		int difficulty = settings.getInt(GamePrefrences.DIFFICULTY, GamePrefrences.NORMAL_DIFFICULTY);
		
		if(difficulty!=GamePrefrences.CUSTOM_DIFFICULTY){
			LENGTHOFCODE=GamePrefrences.LenghtOfCode[difficulty];
			NUMCOLORS=GamePrefrences.NumColors[difficulty];
			NUMGUESSES=GamePrefrences.NumGuesses[difficulty];
		}else{
			LENGTHOFCODE=settings.getInt(GamePrefrences.CUSTOM_LENGTHOFCODE, GamePrefrences.LenghtOfCode[GamePrefrences.NORMAL_DIFFICULTY]);
			NUMCOLORS=settings.getInt(GamePrefrences.CUSTOM_NUMCOLORS, GamePrefrences.NumColors[GamePrefrences.NORMAL_DIFFICULTY]);
			NUMGUESSES=settings.getInt(GamePrefrences.CUSTOM_NUMGUESSES,GamePrefrences.NumGuesses[GamePrefrences.NORMAL_DIFFICULTY]);
		}
		
		mPleaseToastMe = settings.getBoolean(GamePrefrences.HELP_TEXT, true);
		
	}


	private void createHandlersandListeners() {
		
		mActivityHandler = new Handler(){
			public void handleMessage(android.os.Message msg) {
				switch (msg.what) {

				case ANIMATIONCOMMENCEKEY:
					mShuffleButtonHeld=true;
					mShuffling=true;
					mActivityHandler.sendEmptyMessageDelayed(SHUFFLEINITKEY, shufflebegining);
					shuffleballs();
					break;
				case SHUFFLEINITKEY:
					mShuffling=false;
					break;
				case SHUFFLECOLLUMNDELAY:
					scrollCollumn(isCloserByIncrement(mBallSelected[msg.arg1],msg.arg2), msg.arg1);
					break;
				case DISMISSNEWGAMEWHEEL:
					dismissNewGameWheel();
					break;
				case SHOWTUTORAILDIALOGKEY:
					mEndGameToast.cancel();
					wouldYouLikeHelpDialog().show();
					break;

				default:
					break;
				}
			}
		};

	}



	private void initializeColorWheels() {



		mColorWheelButtonHeld=new Boolean[LENGTHOFCODE];
		Arrays.fill(mColorWheelButtonHeld, false);


		//restoring balls selected
		mBallSelected = new int[LENGTHOFCODE];
		
		SharedPreferences savedGame = getSharedPreferences(GamePrefrences.SavedGame, Activity.MODE_PRIVATE);
		boolean gameInProgress = savedGame.getBoolean(GamePrefrences.GAME_IN_PROGRESS, false);
		String savedBallsSelectedString = savedGame.getString(GamePrefrences.BALLS_SELECTED, null);
		if(gameInProgress&&savedBallsSelectedString!=null){
			StringTokenizer extractBallsSelected = new StringTokenizer(savedBallsSelectedString, ",");
			for (int i = 0; i < LENGTHOFCODE; i++) {
				mBallSelected[i] = Integer.parseInt(extractBallsSelected.nextToken());
				mBallSelected[i]= (mBallSelected[i] + NUMCOLORS - 3) % NUMCOLORS;
			}
		}else Arrays.fill(mBallSelected, NUMCOLORS-3);

		mAcceleratedDuration=new int[LENGTHOFCODE];
		Arrays.fill(mAcceleratedDuration, INITIALDURATION);

		mBalls = new ArrayList<LinkedList<ImageView>>();
		for(int i=0;i<LENGTHOFCODE;i++)mBalls.add(new LinkedList<ImageView>());

		mColorWheels = new ArrayList<LinearLayout>();

		mBallDragListener = new BallDragListener();
		mBallTouchListener = new BallTouchListener();

		final LinearLayout upArrowRow = (LinearLayout) findViewById(R.id.uparrowrow);
		final LinearLayout colorrow = (LinearLayout) findViewById(R.id.colorwheelrow);
		final LinearLayout downArrowRow = (LinearLayout) findViewById(R.id.downarrowrow);
		



		for(int i=0;i<LENGTHOFCODE;i++){

			//create up arrow
			ImageView upArrow = new ImageView(this);
			upArrow.setImageResource(R.drawable.up_arrow_selector);
			upArrow.setLayoutParams(mWeightedWidth);
			upArrow.setClickable(true);
			upArrow.setScaleType(ScaleType.CENTER_INSIDE);
			

			//add tags to up arrow
			upArrow.setTag(i);
			upArrow.setTag(R.id.INCREMENT, true);

			//add touchlistner to arrow
			upArrow.setOnTouchListener(this);

			//create down arrow
			ImageView downArrow = new ImageView(this);
			downArrow.setImageResource(R.drawable.down_arrow_selector);
			downArrow.setLayoutParams(mWeightedWidth);
			downArrow.setClickable(true);
			downArrow.setScaleType(ScaleType.CENTER_INSIDE);

			//add tags to up arrow
			downArrow.setTag(i);
			downArrow.setTag(R.id.INCREMENT, false);

			//add touchlistner to arrow
			downArrow.setOnTouchListener(this);


			//create colorwheel linear layout vertical
			LinearLayout thisColorWheel = new LinearLayout(this); 
			thisColorWheel.setLayoutParams(mWeightedWidth);
			thisColorWheel.setOrientation(LinearLayout.VERTICAL);


			//add colorwheel to mColorWheels


			mColorWheels.add(thisColorWheel);
			for (int x = 0; x < 3; x++) {
				addBall(true, i);
			}
			mBalls.get(i).getFirst().setScaleX(.5f);
			mBalls.get(i).getFirst().setScaleY(.5f);
			mBalls.get(i).get(1).setScaleX(1);
			mBalls.get(i).get(1).setScaleY(1);
			mBalls.get(i).getLast().setScaleX(.5f);
			mBalls.get(i).getLast().setScaleY(.5f);


			////FOR DEBUGING
			//if(i%2==0){upArrow.setBackgroundColor(Color.YELLOW);downArrow.setBackgroundColor(Color.BLACK); thisColorWheel.setBackgroundColor(Color.GREEN);}
			//else{upArrow.setBackgroundColor(Color.BLACK);downArrow.setBackgroundColor(Color.YELLOW); thisColorWheel.setBackgroundColor(Color.RED);}
			
			//add uparrow colorwheel and down arrow to linear layout
			upArrowRow.addView(upArrow);
			colorrow.addView(thisColorWheel);
			downArrowRow.addView(downArrow);



		}
		
		mColorWheels.get(LENGTHOFCODE-1).post(new Runnable() {
			@Override
			public void run() {
				for(int i=0;i<LENGTHOFCODE;i++)	mColorWheels.get(i).setLayoutParams(new LinearLayout.LayoutParams(mColorWheels.get(i).getWidth(), mColorWheels.get(i).getHeight()));
			}
		});

		//for drag events
		for(int i=0;i<LENGTHOFCODE;i++){
			mBalls.get(i).get(1).setOnTouchListener(mBallTouchListener);
			mColorWheels.get(i).setOnDragListener(mBallDragListener);
			mColorWheels.get(i).setTag(i);
		}

		mColorWheelsInitializing=false;

	}



	private void initializeGuessPane() {


		mGuessDisplay = (LinearLayout) findViewById(R.id.GuessDisplay);
		mGuessHintDisplay = (LinearLayout) findViewById(R.id.GuessHints);


		mGuessprintouts = new LinearLayout[NUMGUESSES];
		mGuessHintsprintouts = new LinearLayout[NUMGUESSES];
		mGuessPrintOutBalls = new View[NUMGUESSES][LENGTHOFCODE];


		mSlideOut =new ObjectAnimator();
		mSlideOut.setPropertyName("TranslationX");

		for(int i=0;i<NUMGUESSES;i++){
			mGuessprintouts[i]=new LinearLayout(this);
			mGuessprintouts[i].setLayoutParams(mWeightedHeight);

			mGuessDisplay.addView(mGuessprintouts[i]);

			mGuessHintsprintouts[i] =new LinearLayout(this);
			mGuessHintsprintouts[i].setLayoutParams(mWeightedHeight);


			ImageView GuessRemainingImage = new ImageView(this);
			GuessRemainingImage.setImageResource(R.drawable.key_guess_remaining);
			GuessRemainingImage.setLayoutParams(mFillParrentParams);
			GuessRemainingImage.setScaleType(ScaleType.CENTER_INSIDE);
			GuessRemainingImage.setScaleX(.75f);
			GuessRemainingImage.setScaleY(.75f);
			mGuessHintsprintouts[i].addView(GuessRemainingImage);

			mGuessHintDisplay.addView(mGuessHintsprintouts[i]);

		}

		mGuessprintouts[NUMGUESSES-1].post(new Runnable() {
			
			@Override
			public void run() {
				
				mSlideOut.setFloatValues(0,mGuessHintDisplay.getWidth()-mGuessHintsprintouts[0].getChildAt(0).getX());
				mWidthofGuessViewforSlideIn=mGuessDisplay.getWidth();
			
				for(int i=0;i<NUMGUESSES;i++){
					mGuessprintouts[i].setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, mGuessprintouts[i].getHeight()));
					mGuessHintsprintouts[i].setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, mGuessHintsprintouts[i].getHeight()));
					
					mGuessprintouts[i].setCameraDistance(mWidthofGuessViewforSlideIn*10);  //if there are problems with rotate animation fix here
				}
				
			}
		});
		

	}



	private void intializeBottomPane() {

		mSecretComboView = (LinearLayout) findViewById(R.id.secretcomboview);
		mSecretComboHiderView = (LinearLayout) findViewById(R.id.combohiderview);
		mNewGameFrame= (FrameLayout) findViewById(R.id.newGameFrame);
		mNewGameText= (ImageView) findViewById(R.id.NewGameText);
		mNewGameText.setScaleX(.75f);
		mNewGameText.setScaleY(.75f);
		mEndGameWheel = (ImageView) findViewById(R.id.endGameWheel);
		mEndGameWheel.setScaleX(.5f);
		mEndGameWheel.setScaleY(.5f);
		mEndGameWheel.setEnabled(false);

		mSecretComboBalls = new ImageView[LENGTHOFCODE];
		for(int i=0;i<LENGTHOFCODE;i++){
			ImageView ball = new ImageView(this);
			ball.setImageResource(mColors[0]);
			ball.setScaleType(ScaleType.CENTER_INSIDE);
			ball.setScaleX(.80f);
			ball.setScaleY(.80f);
			ball.setLayoutParams(mWeightedWidth);
			mSecretComboBalls[i]=ball;
			mSecretComboView.addView(ball);

		}


		mComboHiders=new ImageView[LENGTHOFCODE];
		for(int i=0;i<LENGTHOFCODE;i++){
			ImageView hider = new ImageView(this);
			hider.setImageResource(R.drawable.lock_code_hider);
			hider.setScaleType(ScaleType.CENTER_INSIDE);
			hider.setScaleX(.80f);
			hider.setScaleY(.80f);
			hider.setLayoutParams(mWeightedWidth);
			hider.setVisibility(View.INVISIBLE);
			mComboHiders[i]=hider;
			mSecretComboHiderView.addView(hider);

		}

		mNewGameWheel = new ImageView(this);
		
		mNewGameFrame.post(new Runnable() {	
			@Override
			public void run() {
				//newGameWheel
				int newGameFrameHeight = mNewGameFrame.getHeight();
				
				mEndGameWheel.setTranslationY(newGameFrameHeight);
				mEndGameWheel.setVisibility(View.VISIBLE);
				mEndGameWheel.setOnClickListener(new endGameWheelClickListener());
				
				mEndGameWheelUp = ObjectAnimator.ofFloat(mEndGameWheel, "TranslationY", newGameFrameHeight,0);
				mEndGameWheelDown = ObjectAnimator.ofFloat(mEndGameWheel, "TranslationY", 0, newGameFrameHeight);
				mEndGameWheelUp.setDuration(INITIALDURATION);
				mEndGameWheelDown.setDuration(INITIALDURATION);
				mEndGameWheelUp.setInterpolator(mOvershootInterpolator);
				mEndGameWheelDown.setInterpolator(mAnticipateInterpolator);
				
				mEndGameWheelUp.setStartDelay(INITIALDURATION);  //so it starts after other wheel goes down
				
				mEndGameWheelUp.addListener(new AnimatorListenerAdapter() {
					
					@Override
					public void onAnimationEnd(Animator animation) {
						mEndGameWheel.setEnabled(true);
						mNewGameText.setVisibility(View.VISIBLE);
						mEndGameWheelGrowShrink.start();
					}
					
				});
				
				
				mEndGameWheelGrowShrink = new AnimatorSet();
				
				ObjectAnimator grow1= ObjectAnimator.ofPropertyValuesHolder(mEndGameWheel, mPVEndGameWheelScaleX,mPVEndGameWheelScaleY);
				ObjectAnimator grow2= ObjectAnimator.ofPropertyValuesHolder(mEndGameWheel, mPVEndGameWheelScaleX,mPVEndGameWheelScaleY);
			
				mEndGameWheelGrowShrink.playSequentially(grow1,grow2);
				
				mEndGameWheelGrowShrink.setDuration(INITIALDURATION*2);
				mEndGameWheelGrowShrink.addListener(new AnimatorListenerAdapter() {
					
					@Override
					public void onAnimationStart(Animator animation) {
						mContinueEndWheelAnimation=true;
					}
					
					@Override
					public void onAnimationEnd(Animator animation) {
						if(mContinueEndWheelAnimation)mEndGameWheelGrowShrink.start();
					}
					
				});
				

				mNewGameWheel.setLayoutParams(new LinearLayout.LayoutParams(mNewGameFrame.getWidth(), mNewGameFrame.getWidth()));
				mNewGameWheel.setScaleType(ScaleType.CENTER_INSIDE);
				mNewGameWheel.setImageResource(R.drawable.shuffle_button);
				mNewGameWheel.setScaleType(ScaleType.CENTER_CROP);
				mNewGameWheel.setTranslationY(newGameFrameHeight/2);
				mNewGameFrame.addView(mNewGameWheel,0);
				
				mNewGameFrame.setOnClickListener(new NewGameFrameClickListener());
				mNewGameFrame.setClickable(true);
				
			}
		});
		
		

		mSecretComboHiderView.post(new Runnable() {

			@Override
			public void run() {
				//combo hiders
				
				mSecretComboViewHeight=mSecretComboHiderView.getHeight();
				
				for(int i=0;i<LENGTHOFCODE;i++){
					mComboHiders[i].setTranslationY(mSecretComboViewHeight);
					mComboHiders[i].setVisibility(View.VISIBLE);
				}

				mHideComboAnimation = new AnimatorSet();
				ArrayList<Animator> popUps = new ArrayList<Animator>();

				for(int i=0;i<LENGTHOFCODE;i++){
					ObjectAnimator popUp = ObjectAnimator.ofFloat(mComboHiders[i], "TranslationY", mSecretComboViewHeight,0);
					popUps.add(popUp);
				}
				mHideComboAnimation.playSequentially(popUps);
				mHideComboAnimation.setDuration(INITIALDURATION);
				mHideComboAnimation.setInterpolator(mOvershootInterpolator);

				mHideComboAnimation.addListener(new AnimatorListenerAdapter() {

					@Override
					public void onAnimationEnd(Animator animation) {
						mBeginingAnimationinProgress=false;
					}
				});

				mShowComboAnimation = new AnimatorSet();
				ArrayList<Animator> popDowns = new ArrayList<Animator>();

				for(int i=0;i<LENGTHOFCODE;i++){
					ObjectAnimator popDown = ObjectAnimator.ofFloat(mComboHiders[i], "TranslationY", 0, mSecretComboViewHeight);
					popDowns.add(0, popDown);
				}
				mShowComboAnimation.playSequentially(popDowns);
				mShowComboAnimation.setDuration(INITIALDURATION);
				mShowComboAnimation.setInterpolator(mAnticipateInterpolator);


			}
		});

		//customize toasts here

	}



	private void enableControlButtons(boolean enable) {

		mColorWheelControlsEnabled=enable;
		findViewById(R.id.guessbutton).setEnabled(enable);	
		findViewById(R.id.backbutton).setEnabled(enable);	
		findViewById(R.id.shufflebutton).setEnabled(enable);
		mNewGameFrame.setEnabled(enable);
		
	}
	
	private void resumeGame(){
		
		SharedPreferences savedGame = getSharedPreferences(GamePrefrences.SavedGame, Activity.MODE_PRIVATE);
		String savedGuessesString=savedGame.getString(GamePrefrences.GUESSES_AS_STRING, null);
		String savedCodeString=savedGame.getString(GamePrefrences.SECRET_CODE_AS_STRING, null);
		int numGuessesMade=savedGame.getInt(GamePrefrences.GUESSES_MADE, 0);
		
		if(savedGuessesString!=null&&savedCodeString!=null&&numGuessesMade!=0){
			
			//Log.d("Resume", "guesses:"+savedGuessesString + " code:"+savedCodeString);
			
			int guessesMade[][] = new int[numGuessesMade][LENGTHOFCODE];
			int secretCodeRestored[] = new int[LENGTHOFCODE];
			
			StringTokenizer extractGuesses = new StringTokenizer(savedGuessesString, ",");
			for (int i = 0; i < numGuessesMade; i++) {
				for (int x = 0; x < LENGTHOFCODE; x++) {
					guessesMade[i][x] = Integer.parseInt(extractGuesses.nextToken());
				}
			}
			StringTokenizer extractCode = new StringTokenizer(savedCodeString, ",");
			for (int i = 0; i < LENGTHOFCODE; i++) {
				secretCodeRestored[i] = Integer.parseInt(extractCode.nextToken());
			}
			
			for(int i=0;i<LENGTHOFCODE;i++){
				mSecretComboBalls[i].setImageResource(mColors[secretCodeRestored[i]]);
				mComboHiders[i].setTranslationY(0);
			}
			
			mGame=new GameModel(NUMGUESSES, LENGTHOFCODE, NUMCOLORS, secretCodeRestored);
			
			
			
			for (int i = 0; i < numGuessesMade; i++) {
				mGame.makeGuess(guessesMade[i], i);
				updateGuessPaneViews(i);
				mMakeGuessAnimation.end(); 
			}
			
			mNumGuessesMade=numGuessesMade;
			mGuessSelected=numGuessesMade-1;

			enableControlButtons(true);
			
			mGameOver=false;
			
		}else {
			beginGame();
			return;
		}
		
	}


	private void beginGame() {

		mGameOver=true;
		
		mWinningAnimation.end();  //WIN REMOVE
		
		enableControlButtons(false);


		if(mNumGuessesMade>0){
			if(mComboHiders[0].getTranslationY()==0)mRemoveGuessAnimationDelay=INITIALDURATION*8;
			removeGuessPrintOutViews();

		}

		mGame=new GameModel(NUMGUESSES, LENGTHOFCODE, NUMCOLORS);

		//Log.d("ani", "begining new game animation");
		mBeginingAnimationinProgress=true;
		mBeginingAnimationGenerator = new Random();

		//PropertyValuesHolder rotate = PropertyValuesHolder.ofFloat("Rotation", 0,360);
		PropertyValuesHolder translateup = PropertyValuesHolder.ofFloat("TranslationY", mNewGameWheel.getTranslationY(), 0);
		PropertyValuesHolder translatedown = PropertyValuesHolder.ofFloat("TranslationY", 0,mSecretComboViewHeight/2);

		ObjectAnimator wheelup = ObjectAnimator.ofPropertyValuesHolder(mNewGameWheel,translateup);
		wheelup.setDuration(INITIALDURATION*2);


		ObjectAnimator wheelspin = ObjectAnimator.ofFloat(mNewGameWheel, "Rotation", 0, 360*7);
		wheelspin.setDuration(INITIALDURATION*20);
		wheelspin.setInterpolator(mAccelandDeceInterpolator);

		ObjectAnimator wheeldown = ObjectAnimator.ofPropertyValuesHolder(mNewGameWheel, translatedown);
		wheeldown.setDuration(INITIALDURATION);
		wheeldown.setStartDelay(INITIALDURATION*18);

		AnimatorSet wheelAnimationSet = new AnimatorSet();
		wheelAnimationSet.playTogether(wheelup,wheelspin,wheeldown);


		mShrinkComboBalls = ObjectAnimator.ofPropertyValuesHolder(mSecretComboBalls[0],mPVShrinkX,mPVShrinkY);
		mGrowComboBalls = ObjectAnimator.ofPropertyValuesHolder(mSecretComboBalls[0],mPVGrowX,mPVGrowY);
		mGrowComboBalls.setDuration(INITIALDURATION/4);

		mBallShrinkAndChangeListener = new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				mSecretComboBalls[mSecretBallBeingAnimated].setImageResource(mColors[mBeginingAnimationGenerator.nextInt(NUMCOLORS)]);
				mGrowComboBalls.setTarget(mSecretComboBalls[mSecretBallBeingAnimated]);
				mGrowComboBalls.start();

				if(mBeginingAnimationinProgress){
					mSecretBallBeingAnimated = (mSecretBallBeingAnimated + 1) % LENGTHOFCODE;
					ObjectAnimator shrink = ObjectAnimator.ofPropertyValuesHolder(mSecretComboBalls[mSecretBallBeingAnimated], mPVShrinkX,mPVShrinkY);
					shrink.addListener(mBallShrinkAndChangeListener);
					shrink.setDuration(INITIALDURATION/4);
					shrink.start();
				}

			}


		};




		mShrinkComboBalls.setDuration(INITIALDURATION/4);
		mShrinkComboBalls.setStartDelay(INITIALDURATION);
		mShrinkComboBalls.addListener(mBallShrinkAndChangeListener);

		wheelAnimationSet.addListener(new AnimatorListenerAdapter() {

			@Override
			public void onAnimationEnd(Animator animation) {
				//afteranimationends
				for(int i=0;i<LENGTHOFCODE;i++){
					mSecretComboBalls[i].setImageResource(mColors[mGame.getPartofSecretCode(i)]);
				}

				//reset game control numbers numbers
				mNumGuessesMade=0;
				mGuessSelected=0;

				enableControlButtons(true);

				mGameOver=false;

				/*///debuging
				mComboHiders[0].setVisibility(View.INVISIBLE);
				mComboHiders[1].setVisibility(View.INVISIBLE);
				mComboHiders[2].setVisibility(View.INVISIBLE);
				mComboHiders[3].setVisibility(View.INVISIBLE);
				*/
			}
		});

		mHideComboAnimation.setStartDelay(INITIALDURATION*12);

		if(mComboHiders[0].getTranslationY()==0){
			mShowComboAnimation.removeAllListeners();
			mShowComboAnimation.setStartDelay(0);
			mShowComboAnimation.start();
			mShrinkComboBalls.setStartDelay(INITIALDURATION*9);
			wheelAnimationSet.setStartDelay(INITIALDURATION*8);
			mHideComboAnimation.setStartDelay(INITIALDURATION*12+INITIALDURATION*8);
		}

		wheelAnimationSet.start();
		//rerun

		mShrinkComboBalls.start();

		//hide combo
		mHideComboAnimation.start();
	}

	private void removeGuessPrintOutViews() {

		PropertyValuesHolder shrinkX = PropertyValuesHolder.ofFloat("ScaleX", 1f,0f);
		PropertyValuesHolder shrinkY = PropertyValuesHolder.ofFloat("ScaleY", 1f,0f);

		ArrayList<Animator> rowShrinkOuts = new ArrayList<Animator>();

		for(int i=0; i<mNumGuessesMade;i++){
			ArrayList<Animator> shrinkouts = new ArrayList<Animator>();
			AnimatorSet removeGuessRow = new AnimatorSet();

			for(int x=0;x<LENGTHOFCODE;x++){
				ObjectAnimator shrinkout = ObjectAnimator.ofPropertyValuesHolder(mGuessPrintOutBalls[i][x], shrinkX,shrinkY);
				shrinkouts.add(shrinkout);
				ObjectAnimator shrinkout2 = ObjectAnimator.ofPropertyValuesHolder(mGuessHintsprintouts[i].getChildAt(x), shrinkX,shrinkY);
				shrinkouts.add(shrinkout2);
			}
			removeGuessRow.playTogether(shrinkouts);
			rowShrinkOuts.add(0,removeGuessRow);


		}
		AnimatorSet removeallrows = new AnimatorSet();
		removeallrows.playSequentially(rowShrinkOuts);
		removeallrows.setDuration(INITIALDURATION/2);
		removeallrows.addListener(new AnimatorListenerAdapter() {


			@Override
			public void onAnimationEnd(Animator animation) {
				ArrayList<Animator> slideIns = new ArrayList<Animator>();
				for(int i=0; i<mNumGuessesMade;i++){
					mGuessprintouts[i].removeAllViews();
					mGuessHintsprintouts[i].removeAllViews();

					ImageView GuessRemainingImage = new ImageView(getApplicationContext());
					GuessRemainingImage.setImageResource(R.drawable.key_guess_remaining);
					GuessRemainingImage.setLayoutParams(mFillParrentParams);
					GuessRemainingImage.setScaleType(ScaleType.CENTER_INSIDE);
					GuessRemainingImage.setScaleX(.75f);
					GuessRemainingImage.setScaleY(.75f);
					GuessRemainingImage.setTranslationX(mWidthofGuessViewforSlideIn);
					mGuessHintsprintouts[i].addView(GuessRemainingImage);

					ObjectAnimator slidein=ObjectAnimator.ofFloat(GuessRemainingImage, "TranslationX", 0);
					slideIns.add(0,slidein);
				}
				AnimatorSet slideBackIn = new AnimatorSet();
				slideBackIn.playSequentially(slideIns);
				slideBackIn.setDuration(INITIALDURATION/2);
				slideBackIn.setInterpolator(mDecceInterpolator);
				slideBackIn.start();
			}

		});
		removeallrows.setStartDelay(mRemoveGuessAnimationDelay);
		removeallrows.start();
		mRemoveGuessAnimationDelay=0;

	}
	
	public AlertDialog wouldYouLikeHelpDialog(){
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		// Add the buttons
		builder.setPositiveButton("Go To Help", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				//go to help
				Intent result = new Intent();
				result.putExtra(GameSplashScreen.GO_TO_HELP_KEY, true);
				setResult(RESULT_OK, result);
				finish();
			}
		});
		builder.setNeutralButton("Change the Difficulty", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				//go to change difficulty
				//modify to finish with result code
				Intent result = new Intent();
				result.putExtra(GameSplashScreen.GO_TO_SETTINGS_KEY, true);
				setResult(RESULT_OK, result);
				finish();
			}
		});
		builder.setNegativeButton("Keep Playing", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				//do nothing, keep playing
				
			}
		});
		
		// Set other dialog properties, LABEL STRING

		Resources myResouces = getResources();
		
		builder.setMessage(
				"Turn on Help Text anytime in the Help page." +
				" or go there to learn more about how to play  " +  myResouces.getString(R.string.app_name) + "!");

		builder.setCancelable(false);
		
		// Create the AlertDialog
		return  builder.create();

	}

	private void completeGame(boolean hasWon){
		
		
		int gamesCompleted;
		int gamesWon;
		int gamesLost;
		float averegeNumGuesses;
		float newAverage=0;
		
		int difficulty =getSharedPreferences(GamePrefrences.SETTINGS, MODE_PRIVATE).getInt(GamePrefrences.DIFFICULTY, GamePrefrences.NORMAL_DIFFICULTY);
		
		SharedPreferences records = getSharedPreferences(GamePrefrences.RECORDS, MODE_PRIVATE);
		gamesCompleted = records.getInt(GamePrefrences.GAMES_FINISHED+difficulty, 0);
		gamesWon = records.getInt(GamePrefrences.GAMES_WON+difficulty, 0);
		gamesLost = records.getInt(GamePrefrences.GAMES_LOST+difficulty, 0);
		averegeNumGuesses = records.getFloat(GamePrefrences.AVERAGE_GUESSES_TO_WIN+difficulty, 0);
		
		SharedPreferences.Editor recordsEdit = records.edit();
		
		if(hasWon){
			newAverage = (gamesWon*averegeNumGuesses+mNumGuessesMade)/(gamesWon+1);
			
			gamesWon++;
			
			recordsEdit.putFloat(GamePrefrences.AVERAGE_GUESSES_TO_WIN+difficulty, newAverage);
			recordsEdit.putInt(GamePrefrences.GAMES_WON+difficulty, gamesWon);	
		}else {
			gamesLost++;
			recordsEdit.putInt(GamePrefrences.GAMES_LOST+difficulty, gamesLost);	
		}
		gamesCompleted++;
		recordsEdit.putInt(GamePrefrences.GAMES_FINISHED+difficulty, gamesCompleted);
		recordsEdit.commit();
		
		SharedPreferences tutorialSettings = getSharedPreferences(GamePrefrences.SETTINGS, MODE_PRIVATE);
		int tutorialCount = tutorialSettings.getInt(GamePrefrences.TUTORIAL_PERIOD_COUNT, 0);
		
		if(tutorialCount<GamePrefrences.TUTORIAL_PERIOD_LENGTH){
			tutorialCount++;
			SharedPreferences.Editor settingsEdit = tutorialSettings.edit();
			settingsEdit.putInt(GamePrefrences.TUTORIAL_PERIOD_COUNT, tutorialCount);
			
			if(tutorialCount==GamePrefrences.TUTORIAL_PERIOD_LENGTH){
				mPleaseToastMe=false;
				
				settingsEdit.putBoolean(GamePrefrences.HELP_TEXT, false);
				
				//dialog here
				//wouldYouLikeHelpDialog().show();
				mActivityHandler.sendEmptyMessageDelayed(SHOWTUTORAILDIALOGKEY, INITIALDURATION*LENGTHOFCODE*3);
				
			}
			settingsEdit.commit();
			
		}
		
		
		endGame(hasWon);
		
	}

	private void endGame(boolean hasWon) {
		

		mGameOver=true;
		enableControlButtons(false);

		

		///reveal combo//
		for(int i=0;i<LENGTHOFCODE;i++){
			mSecretComboBalls[i].setImageResource(mColors[mGame.getPartofSecretCode(i)]);
		}

		String endGameToastString = new String();
		if(hasWon)endGameToastString="Hooray You Won!";
		else endGameToastString="You Lost, Try Again";

		mEndGameToast = Toast.makeText(this, endGameToastString, Toast.LENGTH_LONG);
		mEndGameToast.setGravity(Gravity.CENTER, 0, 0);

		mShowComboAnimation.setStartDelay(INITIALDURATION*LENGTHOFCODE);
		mShowComboAnimation.addListener(new AnimatorListenerAdapter() {

			@Override
			public void onAnimationEnd(Animator animation) {
				
				if(mGameOver){
					mEndGameToast.show();
					
					///is this the animation that has a listener
					mNewGameWheel.animate().translationY(mSecretComboViewHeight).setDuration(INITIALDURATION).setInterpolator(mAnticipateInterpolator);
					mEndGameWheelUp.start();
					
					
				}
			}

		});
		mShowComboAnimation.start();


		//make winning animation
		if(hasWon){
			mWinningAnimation = new AnimatorSet(); 
			ArrayList<Animator> rowRotations = new ArrayList<Animator>();
			
			long oneRotation = INITIALDURATION*10;
			long halfRotaton = oneRotation/2;
			
			for(int i=0; i<mNumGuessesMade;i++){
				ObjectAnimator rotate = ObjectAnimator.ofFloat(mGuessprintouts[i], "RotationY", -360,0);
				rotate.setDuration(oneRotation);
				rotate.setRepeatCount(Animation.INFINITE);
				rotate.setInterpolator(mLinearInterpolator);
				rotate.setStartDelay(halfRotaton/mNumGuessesMade*i);
				
				rowRotations.add(rotate);
			}
			
			mWinningAnimation.playTogether(rowRotations);
			mWinningAnimation.setStartDelay(INITIALDURATION*LENGTHOFCODE*2);
			mWinningAnimation.start();
		}


	}


	public void scrollCollumn(Boolean increment, int mCol){
		if(mBalls.get(mCol).size()<4) addBall(increment, mCol);
	}

	private void addBall(final Boolean increment,final int col) {
		ImageView ball = new ImageView(this);

		ball.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
		ball.setScaleX(0);
		ball.setScaleY(0);
		ball.setLayoutParams(mWeightedHeight);
		ball.setTag(col);

		int animateNext=0;
		int animateOrder=0;


		if (increment) {
			ball.setImageResource(mColors[(mBallSelected[col] + 2) % NUMCOLORS]);
			mBalls.get(col).addLast(ball);
			mBallSelected[col] = (mBallSelected[col] + 1) % NUMCOLORS;

			mColorWheels.get(col).addView(ball);

			animateNext=0;
			animateOrder=1;

		} else {
			ball.setImageResource(mColors[(mBallSelected[col] + NUMCOLORS - 2) % NUMCOLORS]);
			mBalls.get(col).addFirst(ball);
			mBallSelected[col] = (mBallSelected[col] + NUMCOLORS - 1) % NUMCOLORS;

			mColorWheels.get(col).addView(ball, 0);

			animateNext=3;
			animateOrder=-1;
		}

		if(!mColorWheelsInitializing){


			mBalls.get(col).get(animateNext).animate().setInterpolator(mLinearInterpolator).scaleX(0).scaleY(0).setDuration(mAcceleratedDuration[col]).setInterpolator(mAccelandDeceInterpolator).setListener(new AnimatorListenerAdapter() {

				public void onAnimationStart(Animator animation) {
					toggleControlsEnabledforScrolling();	
				}

				@Override
				public void onAnimationEnd(Animator animation) {
					removeBall(increment, col);
					if(mAcceleratedDuration[col]>INITIALDURATION/1.3)mAcceleratedDuration[col]=(mAcceleratedDuration[col]/15)*14;
					if(mColorWheelButtonHeld[col]){
						if(!mDisableColorWheels)scrollCollumn(increment, col);
						else if (mShuffling)scrollCollumn(increment, col);					
						else if (mDisableColorWheels&&mBallSelected[col]!=mTargetIndex[col])scrollCollumn(increment, col);
						else{
							mColorWheelButtonHeld[col]=false;
						}
					}
					toggleControlsEnabledforScrolling();
				}
				@Override
				public void onAnimationCancel(Animator animation) {
					removeBall(increment, col);
					mBalls.get(col).getFirst().setScaleX(.5f);
					mBalls.get(col).getFirst().setScaleY(.5f);
					mBalls.get(col).get(1).setScaleX(1);
					mBalls.get(col).get(1).setScaleX(1);
					mBalls.get(col).getLast().setScaleX(.5f);
					mBalls.get(col).getLast().setScaleY(.5f);
					
				}
			});
			animateNext+=animateOrder;
			mBalls.get(col).get(animateNext).animate().setInterpolator(mLinearInterpolator).scaleX(.5f).scaleY(.5f).setDuration(mAcceleratedDuration[col]);
			mBalls.get(col).get(animateNext).setOnTouchListener(null);
			animateNext+=animateOrder;
			mBalls.get(col).get(animateNext).animate().setInterpolator(mLinearInterpolator).scaleX(1).scaleY(1).setDuration(mAcceleratedDuration[col]);
			mBalls.get(col).get(animateNext).setOnTouchListener(mBallTouchListener);
			animateNext+=animateOrder;
			mBalls.get(col).get(animateNext).animate().setInterpolator(mLinearInterpolator).scaleX(.5f).scaleY(.5f).setDuration(mAcceleratedDuration[col]);

		}

	}

	private void removeBall(Boolean increment, int col) {


		if (increment) {
			mColorWheels.get(col).removeView(mBalls.get(col).getFirst());
			mBalls.get(col).removeFirst();
		} else {
			mColorWheels.get(col).removeView(mBalls.get(col).getLast());
			mBalls.get(col).removeLast();
		}
	}
	
	

	@Override
	public boolean onTouch(View v, MotionEvent event) {


		if(!mDisableColorWheels){


			int collumn=(Integer) v.getTag();

			if (event.getAction()==MotionEvent.ACTION_DOWN){

				//Log.d("TOUCH", "action down on arrow,  increment is"+ (Boolean) v.getTag(R.id.INCREMENT) );

				mColorWheelButtonHeld[collumn]=true;
				mAcceleratedDuration[collumn]=INITIALDURATION;
				scrollCollumn((Boolean) v.getTag(R.id.INCREMENT), collumn);
			}



			if (event.getAction()==MotionEvent.ACTION_UP){
				//Log.d("TOUCH", "Action up on collumn"+ collumn);
				mColorWheelButtonHeld[collumn]=false;


			}
		}
		return false;

	}

	private void toggleControlsEnabledforScrolling() {
		//Log.d("controls", "checking enable");
		//Log.d("controls", "button held is " + mColorWheelButtonHeld[0] + " " + mColorWheelButtonHeld[1] + " "+ mColorWheelButtonHeld[2] + " "+ mColorWheelButtonHeld[3] + " ");


		boolean buttonHeldNow=false;
		for(int i=0;i<LENGTHOFCODE;i++)if(mColorWheelButtonHeld[i]==true)buttonHeldNow=true;

		if(Arrays.equals(mTargetIndex, mBallSelected)&&!mShuffling){
			mShuffleButtonHeld=false;
		}

		/*if(mColorWheelControlsEnabled&& buttonHeldNow){
			//Log.d("controls", "button held, disabling");
			enableControlButtons(false);
			mColorWheelControlsEnabled=false;

		}
		else if(!mColorWheelControlsEnabled&&!buttonHeldNow){
			//Log.d("controls", "buttons released, enabling");
			enableControlButtons(true);
			mColorWheelControlsEnabled=true;
		}*///
		if(buttonHeldNow&&mColorWheelControlsEnabled){
			findViewById(R.id.guessbutton).setEnabled(false);	
			findViewById(R.id.backbutton).setEnabled(false);	
			findViewById(R.id.shufflebutton).setEnabled(false);
			mNewGameFrame.setEnabled(false);
		}
		else if(!buttonHeldNow&&mColorWheelControlsEnabled){
			findViewById(R.id.guessbutton).setEnabled(true);	
			findViewById(R.id.backbutton).setEnabled(true);	
			findViewById(R.id.shufflebutton).setEnabled(true);
			mNewGameFrame.setEnabled(true);
		}

	}
	
	public void shufflebutton(View V){


		shufflebuttonanimate(true);
		enableControlButtons(false);

		mActivityHandler.sendEmptyMessageDelayed(ANIMATIONCOMMENCEKEY, INITIALDURATION*2);


	}



	private void shufflebuttonanimate(boolean forward) {

		

		if(!mShuffleButtonHeld)mShuffleButtonHeld=true;
		if(mShuffleButtonRotation<0)mShuffleButtonRotation=mShuffleButtonRotation*-1;
		if (!forward)mShuffleButtonRotation=mShuffleButtonRotation*-1;

		//Log.d("back", "initialized shufflebuttonanimate  rotate is " + mShuffleButtonRotation);


		
		mShuffleButtonAnimationEnded=false;
		findViewById(R.id.shufflebutton).animate().scaleX(1f).scaleY(1f).setDuration(INITIALDURATION/2);
		findViewById(R.id.shufflebutton).animate().rotationBy(mShuffleButtonRotation*4).setInterpolator(mAccelInterpolator).setDuration(INITIALDURATION*2).setListener(new AnimatorListenerAdapter() {
			public void onAnimationEnd(Animator animation) {
				if(mShuffleButtonHeld)findViewById(R.id.shufflebutton).animate().rotationBy(mShuffleButtonRotation).setDuration(INITIALDURATION/2).setInterpolator(mLinearInterpolator);
				else {
					if(!mShuffleButtonAnimationEnded)findViewById(R.id.shufflebutton).animate().scaleX(.75f).scaleY(.75f).setDuration(INITIALDURATION*2).rotationBy(mShuffleButtonRotation).setInterpolator(mDecceInterpolator).setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {mDisableColorWheels=false; enableControlButtons(true);}
					});
					mShuffleButtonAnimationEnded=true;	
				}
				
			}
		});

	}



	private void shuffleballs() {

		boolean duplicateSelection = true;
		boolean checkForStale=true;
		
		if(LENGTHOFCODE==NUMCOLORS){
			for (int i = 0; i < mBallSelected.length; i++) {
				if(mBallSelected[i]!=mBallSelected[0]){
					duplicateSelection=false;
					break;
				}
			}
			
			if(duplicateSelection) checkForStale=false;
		}

		int randomIndexs[]=new int[LENGTHOFCODE];
		boolean same;
		do{
			randomIndexs=mGame.generateRandomCodeasArray();
			same=false;
			for (int i = 0; i < randomIndexs.length; i++) {
				if(randomIndexs[i]==mBallSelected[i])same=true;
			}
		}while(same && checkForStale);
		
		shuffleballs(randomIndexs);


	}
	private void shuffleballs(int targetindexes[]){

		mDisableColorWheels=true;
		mTargetIndex=targetindexes.clone();

		for(int i=0;i<LENGTHOFCODE;i++){
			if(targetindexes[i]!=mBallSelected[i]){
				mColorWheelButtonHeld[i]=true;

				Message shuffledelayMessage= new Message();
				shuffledelayMessage.what=SHUFFLECOLLUMNDELAY;
				shuffledelayMessage.arg1=i;
				shuffledelayMessage.arg2=targetindexes[i];
				mActivityHandler.sendMessageDelayed(shuffledelayMessage, INITIALDURATION/(mGenerator.nextInt(4)+1));	
			}

		}



	}
	private Boolean isCloserByIncrement(int ballselected, int target) {
		if(ballselected<target){
			if((target-ballselected)<(ballselected+NUMCOLORS-target))return true;
			else return false;
		}else{
			if((target+NUMCOLORS-ballselected)<(ballselected-target))return true;
			else return false;
		}
	}

	public void guessButtonClick(View v){

		//Log.d("Guess","guess button pressed");


		if(mNumGuessesMade<NUMGUESSES)
		{
			//temporary, replace with button disabling when combo is same
			if(mNumGuessesMade==0){
				makeGuess();
			}
			else {
				int guessCheck=mGame.guessHasBeenMade(mNumGuessesMade, mBallSelected);
				if(guessCheck==GameModel.UNIQUEGUESS)makeGuess();
				else{
					if(mPleaseToastMe)Toast.makeText(this, "You have made this guess before", Toast.LENGTH_SHORT).show();
					highlightGuess(guessCheck);
				}
			}

			//if we are within number of guesses
		}

	}



	private void highlightGuess(final int index) {


		AnimatorListener myListner = new AnimatorListenerAdapter() {

			@Override
			public void onAnimationStart(Animator animation) {
				enableControlButtons(false);	
			}


			@Override
			public void onAnimationEnd(Animator animation) {

				enableControlButtons(true);
				for(int i=0;i<LENGTHOFCODE;i++){
					mGuessPrintOutBalls[index][i].setScaleX(1);
					mGuessPrintOutBalls[index][i].setScaleY(1);
				}

			}

		};


		AnimatorSet animations= new AnimatorSet();


		ArrayList<Animator> shrinkAnimations = new ArrayList<Animator>();

		for(int i=0;i<LENGTHOFCODE;i++){
			ObjectAnimator thisAnimator=ObjectAnimator.ofPropertyValuesHolder(mGuessPrintOutBalls[index][i], mPVScaleX,mPVScaleY);
			shrinkAnimations.add(thisAnimator);

		} 

		animations.playTogether(shrinkAnimations);
		animations.setDuration(INITIALDURATION*4);
		animations.setInterpolator(mBounceInterpolator);
		animations.addListener(myListner);
		animations.start();



	}


	private void makeGuess() {
		int GuessResult=mGame.makeGuess(mBallSelected.clone(), mNumGuessesMade);
		//Log.d("Guess","submited to model");


		updateGuessPaneViews(mNumGuessesMade);
		//mMakeGuessAnimation.end();   //FOR SAVED INSTANCE STATE CALL IN SEQUENCE AND CALL END ON ANIMATION

		///guesss has been made, increment position


		mNumGuessesMade++;
		mGuessSelected=mNumGuessesMade-1;
		
		///new game functions
		if(GuessResult==GameModel.HASWON)completeGame(true);
		if(GuessResult==GameModel.HASLOST)completeGame(false);

	}



	private void updateGuessPaneViews(int guess) {


		ImageView guessBalls[]= new ImageView[LENGTHOFCODE];


		for(int i=0;i<LENGTHOFCODE;i++) {
			guessBalls[i]=new ImageView(this);
			guessBalls[i].setImageResource(mColors[mGame.getPartOFGuess(guess, i)]);
			guessBalls[i].setScaleType(ImageView.ScaleType.CENTER_INSIDE);
			guessBalls[i].setVisibility(View.INVISIBLE);
			mGuessprintouts[guess].addView(guessBalls[i], mWeightedWidth);
			mGuessPrintOutBalls[guess][i]=guessBalls[i];
		}

		/////////////////////////
		////add guess hints here
		/////////////////////////


		View outgoingImage =mGuessHintsprintouts[guess].getChildAt(0); 
		View incomingHints[] = new ImageView[LENGTHOFCODE];

		int numGuessHints = mGame.getHintsCorrectColor(guess)+mGame.getHintsCorrectPlace(guess);

		//Log.d("HINTS", "there are " + numGuessHints + "to display");

		///addguesshints to guesshints view
		for(int i=0;i<LENGTHOFCODE;i++){
			ImageView hint = new ImageView(this);

			hint.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
			hint.setLayoutParams(new LinearLayout.LayoutParams(mGuessHintDisplay.getWidth()/LENGTHOFCODE,LinearLayout.LayoutParams.MATCH_PARENT));
			hint.setAlpha(0f);
			hint.setScaleX(0f);
			hint.setScaleY(0f);



			if(i<mGame.getHintsCorrectPlace(guess))hint.setImageResource(R.drawable.guess_hint_correct_position);
			else hint.setImageResource(R.drawable.guess_hint_correct_color);

			if(numGuessHints==0)hint.setImageResource(R.drawable.guess_hint_all_wrong);
			if(i>=numGuessHints&&numGuessHints>0)hint.setVisibility(View.INVISIBLE);


			mGuessHintsprintouts[guess].addView(hint);
			incomingHints[i]=hint;


			//Log.d("transition", "fadeintarget added "+ i);
		}

		if(numGuessHints==0)numGuessHints=LENGTHOFCODE;


		animateGuessMade(outgoingImage, incomingHints, numGuessHints, guessBalls, guess);


	}

	public void animateGuessMade(View outgoingView, View incomingHints[], int numGuessHints, View guessDisplayBalls[], final int guess){


		////////////////////////////////
		////animating guess display/////  second try///////
		///////////////////////////////
		mMakeGuessAnimation = new AnimatorSet();


		ArrayList<Animator> ballMovers = new ArrayList<Animator>();

		for(int i=LENGTHOFCODE-1;i>-1;i--){

			float tranlation =(mWidthofGuessViewforSlideIn/LENGTHOFCODE)*(i+1)*-1;

			///make property value holders
			PropertyValuesHolder TranslateX= PropertyValuesHolder.ofFloat("TranslationX", tranlation,0);
			///make animators
			ObjectAnimator ballAnimator = ObjectAnimator.ofPropertyValuesHolder(guessDisplayBalls[i], TranslateX);
			if(i==LENGTHOFCODE-1)ballAnimator.setInterpolator(mDecceInterpolator);
			else ballAnimator.setInterpolator(mOvershootInterpolator);

			//set ball translate to initial value and visible;
			guessDisplayBalls[i].setTranslationX(tranlation);
			guessDisplayBalls[i].setVisibility(View.VISIBLE);

			///add animator to arraylist
			ballMovers.add(ballAnimator);

		}
		AnimatorSet ballsSlideIn = new AnimatorSet();
		ballsSlideIn.playSequentially(ballMovers);
		ballsSlideIn.setDuration(INITIALDURATION);

		//mMakeGuessAnimation.play(ballsSlideIn);

		////////////////////////////////
		////animating guess hints//////
		///////////////////////////////

		PropertyValuesHolder scaleX= PropertyValuesHolder.ofFloat("ScaleX", 0f,1f);
		PropertyValuesHolder scaleY= PropertyValuesHolder.ofFloat("ScaleY", 0f,1f);
		PropertyValuesHolder alpha= PropertyValuesHolder.ofFloat("Alpha", 0f,1f);





		mSlideOut.setTarget(outgoingView);
		mSlideOut.setDuration(mSlideOutLength);
		mSlideOut.removeAllListeners();
		mSlideOut.addListener(new AnimatorListenerAdapter() {	
			@Override
			public void onAnimationEnd(Animator animation) {
				mGuessHintsprintouts[guess].removeViewAt(0);

			}
		});

		////mMakeGuessAnimation.play(mSlideOut).with(ballsSlideIn);

		AnimatorSet fadeInSet = new AnimatorSet();
		long fadeduration = INITIALDURATION;

		ArrayList<Animator> fadeInsCollection = new ArrayList<Animator>();

		for(int i=0;i<numGuessHints;i++){
			//Log.d("transition", "making fadeinanimater "+ i);
			ObjectAnimator fadeIn = ObjectAnimator.ofPropertyValuesHolder(incomingHints[i], scaleX, scaleY, alpha);
			fadeIn.setDuration(fadeduration);
			if(i>0)fadeIn.setStartDelay(fadeduration*(i-1)+fadeduration/2);

			fadeInsCollection.add(fadeIn);

		}


		fadeInSet.playTogether(fadeInsCollection);
		fadeInSet.setStartDelay(INITIALDURATION*LENGTHOFCODE);
		
		//mMakeGuessAnimation.play(fadeInSet).after(ballsSlideIn);
		
		mMakeGuessAnimation.playTogether(mSlideOut,ballsSlideIn,fadeInSet);
		
		mMakeGuessAnimation.addListener(new AnimatorListenerAdapter(){

			@Override
			public void onAnimationStart(Animator animation) {
				enableControlButtons(false);
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				if(mPleaseToastMe && !mGameOver) sendToasts(guess);  
				if(!mGameOver){
					enableControlButtons(true);   
				}
			}
		});
		mMakeGuessAnimation.start();

	}



	public void sendToasts(int guess) {

		int correctPlace = mGame.getHintsCorrectPlace(guess);      
		int correctColor = mGame.getHintsCorrectColor(guess);

		String toastText = new String();
 
		if(correctPlace+correctColor==0){
			toastText="The code contains none of those colors\nTry Harder";
		}else if(correctPlace>0){
			///start with number of correct place
			if(correctPlace==1) toastText="1 ball is correct";
			else toastText = correctPlace + " balls are correct";

			if(correctColor>0){
				if(correctColor==1) toastText=toastText+"\nAnd 1 ball are the right color \nBut in the wrong place";
				else toastText =toastText+"\nAnd "+correctColor+" balls are the right colors \nBut in the wrong places";
			}
		}else if(correctColor>0){
			if(correctColor==1) toastText="1 ball is the right color \nBut in the wrong place";
			else toastText =+correctColor+" balls are the right colors \nBut in the wrong places";
		}

		Toast.makeText(this, toastText, Toast.LENGTH_LONG).show();

	}


	public void onGoBackClick(View v){
		///revert to previous guess

		//Log.d("back", "go back clicked   guesses made:"+ mNumGuessesMade + " guessselected" + mGuessSelected);
		if(mNumGuessesMade>0&&mGuessSelected>=0){
			//Log.d("back", "go back clicked");

			if(mGuessSelected==mNumGuessesMade-1)
			{
				//Log.d("back", "first back after guess");
				if(Arrays.equals(mGame.getGuess(mGuessSelected),mBallSelected)){
					//Log.d("back", "havent changed the balls");
					if(mGuessSelected>0)mGuessSelected--;
				}
			}else if(mGuessSelected>0)mGuessSelected--;

			if(!Arrays.equals(mGame.getGuess(mGuessSelected),mBallSelected)){
				shufflebuttonanimate(false);
				highlightGuess(mGuessSelected);
				shuffleballs(mGame.getGuess(mGuessSelected));
 
			}

			
		}
	}




	private void dismissNewGameWheel() {
		mNewGameText.setVisibility(View.INVISIBLE);
		findViewById(R.id.MasterLayout).setOnTouchListener(null);
		
		mNewGameFrame.setEnabled(false);
		ObjectAnimator moveDown = ObjectAnimator.ofFloat(mNewGameWheel, "TranslationY", 0,mSecretComboViewHeight/2);
		moveDown.setDuration(INITIALDURATION);
		moveDown.addListener(new AnimatorListenerAdapter() {
			
			@Override
			public void onAnimationEnd(Animator animation) {
				mNewGameFrame.setEnabled(true);
			}
			
		});
		moveDown.start();
	}




	public final class BallTouchListener implements OnTouchListener {


		public boolean onTouch(View view, MotionEvent motionEvent) {
			
			boolean buttonHeldNow=false;
			for(int i=0;i<LENGTHOFCODE;i++)if(mColorWheelButtonHeld[i]==true)buttonHeldNow=true;

			if(!mDisableColorWheels&&!mGameOver &&!buttonHeldNow){

				int collumn = mColorWheels.indexOf(view.getParent());

				if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
					ClipData data = ClipData.newPlainText("", "");
					DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(
							view);
					view.startDrag(data, shadowBuilder, view, 0);
					view.setVisibility(View.INVISIBLE);

					for (int i = 0; i < LENGTHOFCODE; i++) {
						mBalls.get(i).get(0).setAlpha(.3f);
						if(i!=collumn){
							mBalls.get(i).get(1).setScaleX(.75f);
							mBalls.get(i).get(1).setScaleY(.75f);
						}
						mBalls.get(i).get(2).setAlpha(.3f);
					}

					return true;
				}
				else {
					return false;
				}
			}else return false;
		}
	}

	
	
	class BallDragListener implements OnDragListener {


		@Override
		public boolean onDrag(View v, DragEvent event) {

			View draggedView = (View) event.getLocalState();
			//int action = event.getAction();


			switch (event.getAction()) {
			case DragEvent.ACTION_DRAG_LOCATION:

			case DragEvent.ACTION_DRAG_STARTED:
				//Log.d("drag", "action drag started");
				// Do nothing
				break;
			case DragEvent.ACTION_DRAG_ENTERED:
				//Log.d("drag", "action drag entered");
				//entering another view
				int swapcollum=(Integer) v.getTag();
				int originalcollumn = (Integer) draggedView.getTag();

				

				mBalls.get((Integer) draggedView.getTag()).get(1).setScaleX(1f);
				mBalls.get((Integer) draggedView.getTag()).get(1).setScaleY(1f);
				//mBalls.get(swapcollum).get(1).setVisibility(View.INVISIBLE);
				
				
				mBalls.get(originalcollumn).get(1).setImageResource(mColors[mBallSelected[swapcollum]]);
				mBalls.get(originalcollumn).get(1).setVisibility(View.VISIBLE);
				
				mBalls.get(swapcollum).get(1).setImageResource(mColors[mBallSelected[originalcollumn]]);
				mBalls.get(swapcollum).get(1).setScaleX(1f);
				mBalls.get(swapcollum).get(1).setScaleY(1f);
					
				
				break;
			case DragEvent.ACTION_DRAG_EXITED:
				//Log.d("drag", "action drag exited");
				mBalls.get((Integer) v.getTag()).get(1).setVisibility(View.VISIBLE);
				mBalls.get((Integer) draggedView.getTag()).get(1).setImageResource(mColors[mBallSelected[(Integer) draggedView.getTag()]]);
				mBalls.get((Integer) draggedView.getTag()).get(1).setScaleX(.75f);
				mBalls.get((Integer) draggedView.getTag()).get(1).setScaleY(.75f);
				
				mBalls.get((Integer) v.getTag()).get(1).setImageResource(mColors[mBallSelected[(Integer) v.getTag()]]);
				mBalls.get((Integer) v.getTag()).get(1).setScaleX(.75f);
				mBalls.get((Integer) v.getTag()).get(1).setScaleY(.75f);

				break;
			case DragEvent.ACTION_DROP:
				// Dropped, reassign View to ViewGroup
				//Log.d("drag", "action drop");


				int startingcollumn = (Integer) draggedView.getTag();

				mBalls.get((Integer) v.getTag()).get(1).setImageResource(mColors[mBallSelected[(Integer) v.getTag()]]);


				int switchCollumn=(Integer) v.getTag();

				if(switchCollumn!=startingcollumn && mBallSelected[switchCollumn]!=mBallSelected[startingcollumn]){


					int newTargets[] = new int[LENGTHOFCODE];
					newTargets=mBallSelected.clone();
					int swapValue=newTargets[switchCollumn];
					newTargets[switchCollumn]=newTargets[startingcollumn];
					newTargets[startingcollumn]=swapValue;


					shufflebuttonanimate(false);
					shuffleballs(newTargets);
				}

				break;
			case DragEvent.ACTION_DRAG_ENDED:
				//Log.d("drag", "action drag ended");

				for (int i = 0; i < LENGTHOFCODE; i++) {
					mBalls.get(i).get(0).setAlpha(1f);
					mBalls.get(i).get(1).setScaleX(1f);
					mBalls.get(i).get(1).setScaleY(1f);
					mBalls.get(i).get(2).setAlpha(1f);
				}

				mBalls.get((Integer) draggedView.getTag()).get(1).setImageResource(mColors[mBallSelected[(Integer) draggedView.getTag()]]);

				draggedView.setVisibility(View.VISIBLE);
			default:
				break;
			}
			return true;
		}
	}
	
	
	public final class NewGameFrameClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			if(mNewGameWheel.getTranslationY()!=0){
				mActivityHandler.sendEmptyMessageDelayed(DISMISSNEWGAMEWHEEL, INITIALDURATION*12);
				mNewGameFrame.setEnabled(false);
				ObjectAnimator moveUp = ObjectAnimator.ofFloat(mNewGameWheel, "TranslationY", mNewGameWheel.getTranslationY(),0);
				moveUp.setDuration(INITIALDURATION);
				moveUp.addListener(new AnimatorListenerAdapter() {
					
					@Override
					public void onAnimationEnd(Animator animation) {
						mNewGameText.setVisibility(View.VISIBLE);
						findViewById(R.id.MasterLayout).setOnTouchListener(new TouchtoDismissListener());
						mNewGameFrame.setEnabled(true);
					}
					
				});
				moveUp.start();
			}
			else {
				mActivityHandler.removeMessages(DISMISSNEWGAMEWHEEL);
				mNewGameText.setVisibility(View.INVISIBLE);
				findViewById(R.id.MasterLayout).setOnTouchListener(null);
				beginGame(); 
			}
			
		}
		
	}
	
	public final class endGameWheelClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			mNewGameText.setVisibility(View.INVISIBLE);
			
			mEndGameWheel.setEnabled(false);
			
			mContinueEndWheelAnimation=false;
			mEndGameWheelGrowShrink.end();
			mEndGameWheelDown.start();
			
			beginGame();
		}
		
	}
	

	public final class TouchtoDismissListener implements OnTouchListener{
	
	
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			mActivityHandler.removeMessages(DISMISSNEWGAMEWHEEL);
			dismissNewGameWheel();
			
			return false;
		}
		
	}
	
	private String getBallsSelectedAsString(){
		StringBuilder ballsSelectedAsString = new StringBuilder();
		for (int i = 0; i < LENGTHOFCODE; i++) {
			ballsSelectedAsString.append(mBallSelected[i]).append(",");
		}
		return ballsSelectedAsString.toString();
	}

	@Override
	protected void onPause() {
		super.onPause();
		SharedPreferences savedGame = getSharedPreferences(GamePrefrences.SavedGame, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = savedGame.edit();
		if(mNumGuessesMade>0 && !mGameOver){
			editor.putBoolean(GamePrefrences.GAME_IN_PROGRESS, true);
			editor.putString(GamePrefrences.GUESSES_AS_STRING, mGame.getStringofGuesses(mNumGuessesMade));
			editor.putInt(GamePrefrences.GUESSES_MADE, mNumGuessesMade);
			editor.putString(GamePrefrences.SECRET_CODE_AS_STRING, mGame.getStringofSecretCode());
			editor.putString(GamePrefrences.BALLS_SELECTED, getBallsSelectedAsString());
		}else{
			editor.clear();
			editor.putBoolean(GamePrefrences.GAME_IN_PROGRESS, false);
		}
		editor.commit();
	}
	
}


