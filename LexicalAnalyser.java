//LexicalAnalyser.java
//This program will break down a piece of writing
//Kevin Kolcheck 
//04/20/2016

import java.io.*;
import java.util.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class LexicalAnalyser {
	
	Scanner input = new Scanner(System.in);
	
	public static void main(String[] args){
		new LexicalAnalyser();		
	}//end main
	
	public LexicalAnalyser(){
		System.out.println("");
		System.out.println("**** LEXICAL ANALYSER ****");
		System.out.println("This program can parse a piece of writing");
		System.out.println("and give you some interesting information about it.");
		System.out.println("");
		System.out.println("INSTRUCTIONS: put the text file you wish to read"); 
		System.out.println("into the subdirectory /text and proceed.");
		System.out.println("NOTE: formatting the text beforehand to remove newlines");
		System.out.println("produces more accurate results for certain measurements.");
		System.out.println("http://www.textfixer.com/tools/remove-line-breaks.php");
		//START MENU
		boolean keepGoingStartMenu = true;
		while (keepGoingStartMenu){
			String response = startMenu();
			if (response.equals("1")){
				//select a file	
				//get file list from current directory and print them
				File directory = new File("./text");
				String[] children = directory.list();
				
				//user enters file name			
				boolean fileSelect = true;
				boolean back = false;
				File fileIn = null;
				
				//hold onto whatever the file name is, will need to make reports later
				String inputFileName = "";
				while (fileSelect) {
					System.out.println("");
					System.out.println("Showing all .txt files in current directory");
					System.out.println("Please enter the .txt file you would like parse. Ex: sample.txt");
					System.out.println("Enter \"b\" to go back");
					System.out.println("-------------");
					
					//print out directory contents
					for (int i = 0; i < children.length; i++) {
						String filename = children[i];
						//if it's a text file, print it. Hide everything else
						if (filename.endsWith(".txt")){
							System.out.println(filename);
						}						
					}//end for
					
					inputFileName = input.nextLine();
					//make sure the string ends in .txt
					if (inputFileName.endsWith(".txt")) {
						fileIn = new File("./text/" + inputFileName);
						
						try {
							if (fileIn.exists()){
								//File exists and is a text file, continue onward
								fileSelect = false; 			
							} else {
								//File does not exist
							}//end if
						} catch (Exception e){
						//If any error occurs
						System.out.println("Something went wrong");
						System.out.println(e.getMessage());
						} // end try
						
					} else if (inputFileName.equals("b")) {
						//send user BACK to Start Menu
						back = true;
						fileSelect = false;
					} else {
						System.out.println("Requested file does not end with .txt. Please try again or press \"b\" to go back");
					}//end if
				}//end while
				
				if (back == true){
					//go back to Start menu
				} else {
					//PROCESS FILE
					//Try and load the .txt file to a Buffered reader to parse line by line
					try (BufferedReader bReader = new BufferedReader(new FileReader(fileIn))) {
						CurrentText cText = new CurrentText();
						
						//create string and necessary delimiters to parse
						String currentLine;
						String delims = "[.!?]+";
						
						//loop through file line by line, bReader closes itself after loop
						boolean firstPass = true;
						while ((currentLine = bReader.readLine()) != null) {			
							//split string into an array of tokens that represent sentences
							String[] tokens = currentLine.split(delims);					
							//store each individual sentence as it's own node
							for (int i = 0; i < tokens.length; i++){
								//make sure current line is not blank
								if (firstPass == false ){
									// all passes but first, create a new SentenceNode
									cText.nextNode();
								} else {
									//first pass, don't make a new node, head node gets properties set
									firstPass = false;
								}//end if
								
								//set node properties
								cText.current.setPayload(tokens[i].trim());
								cText.current.setTotalWordsInSentence();
								cText.current.setTotalCharactersInSentence();
								
								//for every loop, add total number of sentences to cText.
								cText.setTotalSentences(cText.getTotalSentences() + 1);
							}//end for loop				
						} //end while
						
						//dereference any nodes containing empty strings (if the spacing is odd in the .txt file, there will be some)
						cText.dereferenceEmptyNodes();
						
						//set current text total words and total characters values.
						cText.setTotalWords();
						cText.setTotalCharacters();
						
						//create a HashMap of the frequency of every word used in the document
						cText.setWordFrequency();
						
						System.out.println("");
						System.out.println("File loaded successfully");
						//MAIN MENU OPTIONS START HERE
						boolean keepGoingMainMenu = true;
						while (keepGoingMainMenu){
							response = mainMenu();
							if (response.equals("1")){
								cText.writeReportCsv(inputFileName);
								System.out.println("Condensed report written to: reports/measurements_" + inputFileName.replaceAll("txt","csv"));
								cText.writeReportTxt(inputFileName);
								System.out.println("Extensive Report written to: reports/full_report_" + inputFileName);
							} else if (response.equals("2")){
								//Output various measurements
								System.out.println("-----------");
								System.out.println(cText.getTotalSentences() + " total sentences");
								System.out.println(cText.getTotalWords() + " total words");
								System.out.println(cText.wordFrequency.size() + " unique words");
								System.out.println(cText.getTotalCharacters() + " total characters");
								System.out.println(cText.getAverage(cText.getTotalWords(), cText.getTotalSentences()) + " is the average number of words per sentence");
								System.out.println(cText.getAverage(cText.getTotalCharacters(), cText.getTotalWords()) + " is the average word length");

							} else if (response.equals("3")){
								//Return keywords	
								//STOP WORDS MENU STARTS HERE
								boolean keepGoingStopWordsMenu = true;
								boolean stopWords = false;
								while (keepGoingStopWordsMenu){
								response = stopWordsMenu();
									if (response.equals("1")){
										//stop words WILL NOT show up in results
										keepGoingStopWordsMenu = false;
									} else if (response.equals("2")){
										//stop words WILL show up in results
										stopWords = true;
										keepGoingStopWordsMenu = false;
									} else {
										System.out.println("Sorry, I didn't understand");
									}//end if
								}//end while
								//STOP WORDS MENU ENDS HERE

								//KEYWORDS MENU STARTS HERE
								int keywordLimit = 0;
								boolean keepGoingKeywordsMenu = true;
								while (keepGoingKeywordsMenu){
								response = keywordsMenu();
									if (isInteger(response) == true){
										//user has entered an integer
										//check to make sure integer is a positive number
										keywordLimit = Integer.parseInt(response);
										if (keywordLimit < 1){
											// user has either entered 0 or a negative number
											// quietly change to 10
											keywordLimit = 10;
										} //end if
										keepGoingKeywordsMenu = false;
									} else {
										System.out.println("Please enter an integer");
									}//end if
								}//end while
								//KEYWORDS MENU ENDS HERE
								
								//print out keywords
								int counter = 1;
								for (String word : cText.getKeyWords(keywordLimit, stopWords)){
									//print keyword and frequency
									System.out.println(counter + ") " + word + ": " + cText.wordFrequency.get(word));
									counter++;
								}//end for
							} else if (response.equals("4")) {
								//Print all function words in console
								cText.printFunctionWords();
							} else if  (response.equals("5")) {
								//go back up to startMenu
								keepGoingMainMenu = false;
							} else if (response.equals("0")) {
								//quit the program entirely
								keepGoingMainMenu = false;
								keepGoingStartMenu = false;
							} else {
								System.out.println("Sorry, I didn't understand");
							}
						}//end while
						//END MAIN MENU OPTIONS
						
					} catch (Exception e){
						//If any error occurs
						System.out.println(e.getMessage());
					} // end try
					//END PROCESS FILE
				} //end if	
			} else if (response.equals("0")){
			//quit program	
				keepGoingStartMenu = false;
			} else {
				System.out.println("Sorry, I didn't understand");
			}//else
		}//end while
		//END START MENU
	}//end constructor

	public String startMenu() {
		System.out.println("");
		System.out.println("Select an option");
		System.out.println("1) Select a text");
		System.out.println("0) Quit");
		String response = input.nextLine();
		return (response);
	}//end startMenu
	
	public String mainMenu() {
		System.out.println("");
		System.out.println("Select an option");
		System.out.println("1) Generate Report");
		System.out.println("2) View Measurements");
		System.out.println("3) View Keywords");
		System.out.println("4) View Function Words");
		System.out.println("5) Back");
		System.out.println("0) Quit");
		String response = input.nextLine();
		return (response);
	}//end mainMenu
	
	public String stopWordsMenu(){
		System.out.println("");
		System.out.println("Ignore stop words?");
		System.out.println("1) Yes");
		System.out.println("2) No");
		String response = input.nextLine();
		return (response);
	}
	public String keywordsMenu(){
		System.out.println("");
		System.out.println("How many of the most frequently used words would you like to see?");
		String response = input.nextLine();
		return (response);
	}// end keywordsMenu

	//verify whether reponse is a valid integer
	public boolean isInteger(String response) {
		boolean isValidInteger = false;
		try {
			Integer.parseInt(response);
			// response is a valid integer
			isValidInteger = true;
		} catch (NumberFormatException ex) {
			// response is not an integer
		}//end try
		return (isValidInteger);
	}//end isInteger
}//end class def