package com.brandonswanson.crackthecode;

import java.util.ArrayList;
import java.util.Random;

/**
 * 
 * Crack the Code Model
 *
 */

public class GameModel {
	private static int LENGTHOFCODE;
	private static int NUMGUESSES;
	private static int NUMCOLORS;
	private ArrayList<Integer> mSecretCode;
	private int mGuesses[][];
	private int mCorrectPlaceHint[];
	private int mCorrectColorHint[];

	public static final int HASWON = 1;
	public static final int HASLOST = -1;
	public static final int UNIQUEGUESS= -1;
	
	

	/**
	 * constructor of GameModel
	 * 
	 * @param numGuess allowed number of guesses
	 * @param lengthOfCode how many balls in each guess
	 * @param numColors how many available colors
	 */
	public GameModel(int numGuesses,int lengthOfCode, int numColors){
		LENGTHOFCODE=lengthOfCode;
		NUMGUESSES=numGuesses;
		NUMCOLORS=numColors;

		mSecretCode=new ArrayList<Integer>();

		mSecretCode=generateRandomCode(LENGTHOFCODE);

		mGuesses= new int[numGuesses][lengthOfCode];

		mCorrectPlaceHint = new int[numGuesses];
		mCorrectColorHint = new int[numGuesses];

	}
	
	
	/**
	 * constructor of GameModel
	 * for restoring previous game
	 * 
	 * @param numGuess allowed number of guesses
	 * @param lengthOfCode how many balls in each guess
	 * @param numColors how many available colors
	 * @param secret code from restored game
	 */
	public GameModel(int numGuesses,int lengthOfCode, int numColors, int[] restoredSecretCombo){
		LENGTHOFCODE=lengthOfCode;
		NUMGUESSES=numGuesses;
		NUMCOLORS=numColors;

		mSecretCode=new ArrayList<Integer>();
		for (int i = 0; i < LENGTHOFCODE; i++) {
			mSecretCode.add(restoredSecretCombo[i]);
		}

		mGuesses= new int[numGuesses][lengthOfCode];

		mCorrectPlaceHint = new int[numGuesses];
		mCorrectColorHint = new int[numGuesses];

	}


	public int[] generateRandomCodeasArray(){
		ArrayList<Integer> newCode=generateRandomCode(LENGTHOFCODE);
		int newCodeArray[]= new int[newCode.size()];
		for(int i=0;i<newCode.size();i++){
			newCodeArray[i]=newCode.get(i);
		}
		return newCodeArray;

	}


	private ArrayList<Integer> generateRandomCode(int ofLength) {

		int combo[]= new int[NUMCOLORS];
		Random generator = new Random();

		for(int i=0;i<NUMCOLORS;i++) combo[i]=i;
		for(int i=0;i<NUMCOLORS;i++){
			int swap=generator.nextInt(NUMCOLORS-i)+i;
			int temp=combo[swap];
			combo[swap]=combo[i];
			combo[i]=temp;
		}
		ArrayList<Integer> shortcombo= new ArrayList<Integer>();
		for(int i=0;i<ofLength;i++)shortcombo.add(i, combo[i]);
		return shortcombo;
	}

	/*public ArrayList<Integer> getSecretCode(){
		return mSecretCode;
	}*/
	public int getPartofSecretCode(int index){
		return mSecretCode.get(index);
	}

	/**
	 * submit a guess to the model
	 * 
	 * @param guess[] array of length LENGTHOFCODE with current selections
	 * @param numGuessesMade how many guesses made so far
	 *
	 *@return int of value key either HASWON, HASLOST, or 0
	 */
	public int makeGuess(int guess[], int numGuessesMade){

		mGuesses[numGuessesMade]=guess.clone();

		ArrayList<Integer> colorsWeHaveSeen=new ArrayList<Integer>();

		int correctPlaceAndColor=0;
		int correctColor=0;
		boolean hasWon=false;
		boolean hasLost=false;

		if(numGuessesMade>=NUMGUESSES-1)hasLost=true;
		for(int i=0;i<LENGTHOFCODE;i++){
			if(mSecretCode.get(i)==guess[i]){
				correctPlaceAndColor++;
				colorsWeHaveSeen.add(guess[i]);
			}	
		}
		if(correctPlaceAndColor==LENGTHOFCODE){
			hasWon=true;
			hasLost=false;
			correctColor=0;
		}else{
			for(int i=0;i<LENGTHOFCODE;i++){
				if(mSecretCode.contains(guess[i])&&!colorsWeHaveSeen.contains(guess[i])){
					colorsWeHaveSeen.add(guess[i]);
					correctColor++;
				}
			}
		}



		mCorrectPlaceHint[numGuessesMade]=correctPlaceAndColor;
		mCorrectColorHint[numGuessesMade]=correctColor;

		if(hasLost) return HASLOST;
		else if(hasWon) return HASWON;
		else return 0;

	}


	public int guessHasBeenMade(int NumGuessesMade, int ballSelected[]) {

		ArrayList<Boolean> guessmadebefore = new ArrayList<Boolean>();
		for(int x=0;x<NumGuessesMade;x++){
			ArrayList<Boolean> guesssame= new ArrayList<Boolean>(); 
			for(int i=0;i<LENGTHOFCODE;i++){
				if(mGuesses[x][i]==ballSelected[i])guesssame.add(true);
				else guesssame.add(false);
			}if(guesssame.contains(false))guessmadebefore.add(x, false);
			else{
				guessmadebefore.add(x, true);
			}

		}
		if(!guessmadebefore.contains(true)){
			return UNIQUEGUESS;
		}
		else{
			return guessmadebefore.indexOf(true);
		}
	}

	public int[] getGuess(int atIndex){
		return mGuesses[atIndex].clone();
	}
	public int getPartOFGuess(int atIndex, int guessCollumn){
		return mGuesses[atIndex][guessCollumn];
	}

	public int getHintsCorrectPlace(int atIndex){
		return mCorrectPlaceHint[atIndex];
	}
	public int getHintsCorrectColor(int atIndex){
		return mCorrectColorHint[atIndex];
	}
	
	public String getStringofGuesses(int numGuessesMade){
		StringBuilder guesesAsString = new StringBuilder();
		for (int i = 0; i < numGuessesMade; i++) {
			for (int x = 0; x < LENGTHOFCODE; x++) {
				guesesAsString.append(mGuesses[i][x]).append(",");
			}
		}
		
		return guesesAsString.toString();
	}
	public String getStringofSecretCode(){
		StringBuilder codeAsString = new StringBuilder();
		for (int i = 0; i < LENGTHOFCODE; i++) {
			codeAsString.append(mSecretCode.get(i)).append(",");
		}
		return codeAsString.toString();
	}

}
