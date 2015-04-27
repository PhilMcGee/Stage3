package com.company;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;
import java.util.Vector;

public class Main {

    public static void main(String[] args)
            throws FileNotFoundException
    {

        // Stage 2

        //**Temp Fixed - input to determine grid size and full word list source file name

        int rows = 3;
        int cols = 3;

        //**Temp fixed - import full word list into vector
        // Initialise variables
        Vector<String> wordList = new Vector<String>();
        Vector<String> fixedLengthWordList = new Vector<String>();
        String currentWord;
        String[] letters = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l",
                "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};   //set letters definition array
        //- ?IMPROVE consider using ascii numbers and loop instead.
        int[][] letterCounts = new int[26][rows+1]; //Array [letter#,character# number(last is total letter count)]



        //Create Scanner object inFile and initialise to full list text file
        Scanner inFile = new Scanner(new FileReader("C:\\Users\\Phil\\Documents\\JCU\\CP5632 Computer Programming 1\\Programming Assignment\\commonEnglish4k.txt"));

        while ((inFile.hasNext())) {
            wordList.addElement(inFile.next());
        }

        //test print vector length, first word and last word
        System.out.println("Full word list");
        System.out.println("Words = " + wordList.size()+ " ; First word : "
                + wordList.elementAt(0) + " ; Last Word : "
                + wordList.lastElement());

        //**Create vector of words of appropriate character length
        //and count occurrence of each letter in total total word list
        //and as 1st,2nd.. letter in word (array supporting for letter/word randomness equity)

        for (int i=0; i<(wordList.size()); i++) {
            if (wordList.get(i).length() == rows) {
                fixedLengthWordList.addElement(wordList.get(i));
                //do letter counts for each character position:
                for (int c=0; c<rows; c++) {
                    for (int l=0; l<26; l++) {
                        if (letters[l].equals(wordList.get(i).substring(0+c,1+c))){
                            letterCounts[l][c]=letterCounts[l][c]+1;
                        }
                    }
                }
            }
        }
        //sum total letter counts:
        for (int c=0; c<rows; c++) {
            for (int l=0; l<26; l++) {
                letterCounts[l][rows]=letterCounts[l][rows]+letterCounts[l][c];
            }
        }


        System.out.println("Refined "+rows+" character word list");
        System.out.println("Words = " + fixedLengthWordList.size()+ " ; First word : "
                + fixedLengthWordList.elementAt(0) + " ; Last Word : "
                + fixedLengthWordList.lastElement());
        System.out.println("Letter counts :");
        for (int c=0; c<rows+1; c++) {
            System.out.println();
            for (int l=0; l<26; l++) {
                System.out.printf("%7d",letterCounts[l][c]);
            }

        }
        System.out.println();
        for (int l=0; l<26; l++) {
            System.out.printf("      "+letters[l]);
        }
        System.out.println();

        //**WHILE UNIQUE GRID AND SOLUTION FIND to be added.


        //**Generate random hint letters for word grid
        // Utilises letter counts from word list to distribute randomness of selection
        // relative to letter occurrences in words
        // One random letter in a random location are generated
        // per row and column combination.

        //new Array : true = position is filled, with additional
        //last array row or column, true = hint letter within row or col. :
        boolean[][] wordGridHintLocations = new boolean[cols+1][rows+1];
        String[][] wordGridHintLetters = new String[cols][rows];
        int testColLocation;
        for (int r=0; r<rows; r++) {
            while (wordGridHintLocations[r][cols]==false) {
                testColLocation = (int) (Math.random()*cols);
                if((wordGridHintLocations[r][testColLocation]==false)
                        &&(wordGridHintLocations[rows][testColLocation]==false)){
                    wordGridHintLocations[r][testColLocation]=true;
                    wordGridHintLocations[rows][testColLocation]=true;
                    wordGridHintLocations[r][cols]=true;
                }
            }

        }
        System.out.println("Grid Hint Letters Boolean Array");
        System.out.println("       0 1 2 3 4 5 6 7 8 9");
        for (int r=0; r<rows+1; r++) {

            System.out.printf("%7d",r);

            for (int c=0; c<cols+1; c++) {
                if (wordGridHintLocations[r][c]) {
                    System.out.printf("T ");
                } else System.out.printf("F ");
            }
            System.out.println();
        }
        // Hint letters for each location, addressed row by row
        System.out.println();
        System.out.println("Hint Letters Generated:");
        for (int r=0; r<rows; r++) {
            int rowCharPos = r;
            int colCharPos = 0;
            for (int c=0; c<cols; c++) {
                if (wordGridHintLocations[r][c]) {
                    colCharPos = c;
                }
            }
            int randomLetterPoint= (int)(Math.random()*fixedLengthWordList.size());
            boolean letterFound =false;
            int accumProbability = 0;
            for(int l=0; l<26; l++){
                accumProbability = accumProbability + (letterCounts[l][rowCharPos]+letterCounts[l][colCharPos])/2;
                if (!letterFound&&(accumProbability>randomLetterPoint)){
                    letterFound=true;
                    wordGridHintLetters[rowCharPos][colCharPos]=letters[l];
                    System.out.println("row : "+rowCharPos+"; Col : "+colCharPos+"; Letter : "+letters[l]);
                }
            }

        }
        //**Find solutions - SPIRAL LOOPS INITIALISATION AND CONTROL
        //NB for loops not used around each word generator as we want to exit if 2 solutions are found
        //Create initial working grid from hint letter grids (allow grid content growth)

        //Spiral loops variables initialisation
        int wordNum=-1; //set spiral nest current word identifier (-1 = null, 0=row1, 1=col1, 2=row2, 3=col2...)
        //spiral nest variables by array control using reference wordNum :
        int [] testsCount = new int [rows+cols];//temporary test/loop limiting (arbitrary max loop counter) TO BE DELETED
        int [] triedWordCount = new int[rows+cols]; //counts number of words utilised from fixedLengthWordList
        int [] matchedLettersCount = new int[rows+cols]; //counts number of letters matched to grid so far
        int[] requiredLetterMatches = new int[rows+cols]; //stores number of predefined letters already in place for word
        boolean [] gotoNextWord = new boolean[rows+cols];//boolean to move  next word in fixedLengthWordList


        String[][] workingGridLetters = new String[rows][cols];
        Boolean[][] workingGridFilled = new Boolean[rows][cols];
        for (int r=0; r<rows; r++) {
            for (int c=0; c<cols; c++) {
                workingGridLetters[r][c]= wordGridHintLetters[r][c];
                workingGridFilled[r][c]= wordGridHintLocations[r][c];
            }
        }
        boolean noUniqueSolutionFound=true;  //boolean for loop exit
        int testNo = 0; //test grid number or solutions (0 or 1)
        String[][] testgrid=new String[rows][cols]; //word characters test grid for trial word letters

        //Row1 or SPIRAL LOOP START
        wordNum=wordNum+1;  //increment word number to current spiral nest level

        testsCount[wordNum]=0; //test limiting variable only. TO BE DELETED
        triedWordCount[wordNum]=0;
        gotoNextWord[wordNum]=true;
        //sum letter matches required:
        requiredLetterMatches[wordNum]=0;
        for (int c=0; c<cols; c++){
            if (workingGridFilled[0][c]){requiredLetterMatches[wordNum]=requiredLetterMatches[wordNum]+1;}

        }

        while (triedWordCount[wordNum]<fixedLengthWordList.size()&&noUniqueSolutionFound){

            while (gotoNextWord[wordNum]&&triedWordCount[wordNum]<fixedLengthWordList.size()){
                matchedLettersCount[wordNum] = 0;
                for(int c=0; c<cols; c++){

                    testgrid[0][c] = fixedLengthWordList.elementAt(triedWordCount[wordNum]).substring(c, c + 1);

                    if (wordGridHintLocations[0][c]&&testgrid[0][c].substring(0,1).equals(wordGridHintLetters[0][c].substring(0,1))) {

                        matchedLettersCount[wordNum]=matchedLettersCount[wordNum]+1;
                        if (requiredLetterMatches[wordNum]==matchedLettersCount[wordNum]){
                            gotoNextWord[wordNum] = false;
                            System.out.println("next word found");
                        }

                    }
                }

                triedWordCount[wordNum]=triedWordCount[wordNum]+1;

            }
            System.out.println("Row 1 next word : "+fixedLengthWordList.elementAt(triedWordCount[wordNum]-1)+" ");

            //**MANUAL BREAK ROW 1 WHILE TESTING
            gotoNextWord[wordNum] = true;
            testsCount[wordNum]=testsCount[wordNum+1];
            if(testsCount[wordNum]==5){
                triedWordCount[wordNum]=fixedLengthWordList.size();noUniqueSolutionFound=false;}


        }
        wordNum=wordNum-1;  //decrease current word number from current spiral nest level








    }


}
/*NEXT STEP: (1) UTILISE WORKING GRIDS IN ROW1 WORD LOOP TO CONTROL LETTERS AND CHARACTERS,
        AS THESE WILL BE FILLED WITH PROGRESSIVE SOLUTION AND HINT LETTERS, THUS DETERMINING
        WHAT LETTER MATCHED ARE REUIRED FOR SUBSEQUENT ROW AND COLUMN WORDS.
        LOOKING TO USE THESE TO AID IDENTICAL LOOP COMMANDS FOR EACH ROW/COL WORD LOOP.
        (2) IN WORD SOLVER LOOPS, REDEFINE ALL VARIABLES STARTING WITH row1 INTO AN ARRAY FORMAT TO ALLOW
        REPLICATION ACROSS LOOPS - current#[] 1=row1,2=col1,3=row2....
        [3] Explore use/application of switch statement

        *******DON'T FORGET TO BACK UP*******************


        */
