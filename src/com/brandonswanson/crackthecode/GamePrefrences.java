package com.brandonswanson.crackthecode;

public class GamePrefrences {
	
	///Saved Game saved variables
	
	public static final String SavedGame="SavedGame"; //PREFRENCE FILE
	
	public static final String GAME_IN_PROGRESS= "GAME_IN_PROGRESS";
	public static final String GUESSES_AS_STRING= "GUESSES_AS_STRING";
	public static final String SECRET_CODE_AS_STRING= "SECRET_CODE_AS_STRING";
	public static final String GUESSES_MADE= "GUESSES_MADE";
	public static final String BALLS_SELECTED= "BALLS_SELECTED";
	
	
	public static final String SETTINGS = "SETTINGS"; //PREFRENCE FILE
	
	//help settings
	public static final String HELP_TEXT = "HELP_TEXT";
	public static final String TUTORIAL_PERIOD_COUNT = "TUTORIAL_COUNT";
	public static final int TUTORIAL_PERIOD_LENGTH = 4;
	
	
	//Difficulty settings
	public static final String DIFFICULTY = "DIFFICULTY";
	
	public static final int EASY_DIFFICULTY = 0;
	public static final int NORMAL_DIFFICULTY = 1;
	public static final int HARD_DIFFICULTY = 2;
	public static final int INSANE_DIFFICULTY = 3;
	public static final int CUSTOM_DIFFICULTY = 4;
	
	public static final String DifficultyNames[] = new String[]{"Easy","Normal","Hard","Insane","Custom"};
	
	
	public static final int NumColors[] = 		new int[]{6, 8,8,8};
	public static final int LenghtOfCode[] = 	new int[]{4, 4,5,6};
	public static final int NumGuesses[] =	 	new int[]{10,8,6,10};
	
	public static final String CUSTOM_NUMCOLORS = "NUMCOLORS";
	public static final String CUSTOM_LENGTHOFCODE = "LENGTHOFCODE";
	public static final String CUSTOM_NUMGUESSES = "NUMGUESSES";
	
	
	
	//records
	public static final String RECORDS = "RECORDS"; //PRECRENCE FILE
	
	public static final String GAMES_FINISHED = "GAMES_FINISHED";
	public static final String GAMES_WON = "GAMES_WON";
	public static final String GAMES_LOST = "GAMES_LOST";
	public static final String AVERAGE_GUESSES_TO_WIN = "AVERAGE GUESSES";
}
