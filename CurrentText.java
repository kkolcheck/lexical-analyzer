//CurrentText.java
//Defines the CurrentText class

import java.io.*;
import java.util.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class CurrentText {
	private
		SentenceNode head;
		SentenceNode current;
		int totalSentences;
		int totalWords;
		int totalCharacters;
		Map<String, Integer> wordFrequency;
		
	public CurrentText(){
		//instantiate new SentenceNode
		SentenceNode temp = new SentenceNode();
		current = temp;
		//Set current SentenceNode as the linkedlist head
		head = current;
		totalSentences = 0;
		totalWords = 0;
		totalCharacters = 0;
		wordFrequency = new HashMap<String, Integer>();
	}//end constructor

	public void nextNode(){
		if (current.getNext() != null){
			//switch current SentenceNode to next
			current = current.getNext();
		} else {
			//if at the end
			//save current node location, create new node, save new node location
			SentenceNode previousLocation = current;
			current = new SentenceNode();
			SentenceNode newTail = current;
			//add new node location to previous nodes "next"
			current = previousLocation;
			current.setNext(newTail);
			//switch back to the tail node
			current = newTail;		
		}//end if
	}//end nextnode
	
	public void resetToHead(){
		current = head;
	}
	
	public void setTotalSentences(int totalSentences){
		this.totalSentences = totalSentences;
	}//end setTotalSentences
	
	public int getTotalSentences(){
		return (this.totalSentences);
	}//end getTotalSentences
		
	public void setTotalWords(){
		//get total number of words in all sentences
		int sum = 0;
		for (int i = 0; i < getTotalSentences(); i++){
			if (i == 0){
				//if first pass, set to head
				resetToHead();
			} else {
				//if not first pass, go to the next node
				nextNode();
			}//end else
			sum += current.getTotalWordsInSentence();
		}//end for
		this.totalWords = sum;
	}//end setTotalWords
	
	public int getTotalWords(){
		return (this.totalWords);
	}//end getTotalWords
	
	public void setTotalCharacters(){
		//loop through all nodes and add the total number of characters together
		int sum = 0;
		for (int i = 0; i < getTotalSentences(); i++){
			if (i == 0){
				//if first pass, set to head
				resetToHead();
			} else {
				//if not first pass, go to the next node
				nextNode();
			}//end else
			sum += current.getTotalCharactersInSentence();
		}//end for
		this.totalCharacters = sum;
	}//end setTotalCharacters
	
	public int getTotalCharacters(){
		return (this.totalCharacters);
	}//end getTotalCharacters
	
	public void setWordFrequency(){							
		//loop through words and put them as keys in the HashMap,
		//increment everytime the same key appears
		boolean firstPass = true;
		for (int i = 0; i < getTotalSentences(); i++){
			if (i == 0){
				//if first pass, set Node to head and continue
				resetToHead();
				firstPass = false;
			} else {
				//if not first pass, go to the next node
				nextNode();	
			}
			//get rid of punctuation, whitespace, convert to lowercase letters, and split into an array of words
			String delims = "[ ]+";
			String[] tokens = current.getPayload().replaceAll("[^A-Za-z' ]", "").toLowerCase().split(delims);
			
			//create another for loop to iterate through the new list of words
			for (String word : tokens) {
				//Find the current word in the table
				Integer frequency = wordFrequency.get(word);

				//If key doesn't exist, create it and give it a frequency of 1, otherwise increment it
				if (frequency == null){
					wordFrequency.put(word, 1);
				} else {
					wordFrequency.put(word, (frequency + 1));
				}//end if 

			}//end for
		}//end for
		//remove blank new lines that may have been added
		if (wordFrequency.containsKey("") == true){
			wordFrequency.remove("");
		}//end if
	}//end setWordFrequency
	
	public String[] getKeyWords(int limit, boolean stopWords){
		//The user indicates how many keywords they would like to see
		//and whether they want stop words included in the list	
	
		//make a copy of the wordFrequency hashmap
		Map<String, Integer> words = new HashMap<String, Integer>();
		words.putAll(wordFrequency);
		
		//if stop words are removed from the keyword list loop through keys and subtract the number present in map from total size
		if (stopWords == false) {
			//Glasgow Stop Words list from "http://ir.dcs.gla.ac.uk/resources/linguistic_utils/stop_words"
			try (BufferedReader bReader = new BufferedReader(new FileReader("./assets/stopWords.txt"))) {
				//string for parsing
				String currentLine;
				
				//loop through file line by line, bReader closes itself after loop
				while ((currentLine = bReader.readLine()) != null){			
					//loop through stop word list, if it is in hashmap, delete it
					if (words.containsKey(currentLine) == true){
						words.remove(currentLine);
					}//end if
				}//end while
			} catch (Exception e){
				//If any error occurs
				System.out.println(e.getMessage());
			} // end try //end while
		}//end if
		
		//if limit is larger than size of map entries, reduce limit to size of map
		if (limit > words.size()) {
			limit = words.size();
		}//end if
		
		//create a string array that contains the keywords
		String[] keyWords = new String[limit];
		
		//repeat outer loop based on how many keywords to be retrieved	
		for (int i = 0; i < limit; i++){
			int highestNumber = 0;
			String keyWord = "";
			
			//step through the list of keys in the copy (will decrease as entries are deleted)
			for (String word : words.keySet()){
				if (words.get(word) > highestNumber) {
					//set new highest number, and save word as new keyword
					highestNumber = words.get(word);
					keyWord = word;
				}//end if
			}//end for
			
			//put the new word in the keyword array and delete it from the map
			keyWords[i] = keyWord;
			words.remove(keyWord);
		}//end for
		words.clear();
		return(keyWords);
	}//end getKeyWords
	
	public String[] getFunctionWords(String filePath){
		//Can return any list of function words based on which file read from
		//Word lists from "http://www.sequencepublishing.com/academic.html"
		String wordString = "";
		try (BufferedReader bReader = new BufferedReader(new FileReader(filePath))) {
			//string for parsing
			String currentLine;
			
			//loop through file line by line, bReader closes itself after loop
			while ((currentLine = bReader.readLine()) != null){			
				//loop through stop word list, if it is in hashmap, delete it
				if (wordFrequency.containsKey(currentLine) == true){
					//make a long string containing each 
					wordString += " " + currentLine;
				}//end if
			}//end while
		} catch (Exception e){
			//If any error occurs
			System.out.println(e.getMessage());
		} // end try //end while
		
		//Parse wordString into a string array
		String delims = "[ ]+";
		String[] wordList = wordString.trim().split(delims);
		return (wordList);
	}//end getAuxiliaryVerbs
	
	public float getAverage(int numerator, int denominator){
		//cast numerator and denominator as floats and divide to get the average
		float average = (float) numerator / (float) denominator;
		return(average);
	}//end getAverage
	
	public boolean isAlphaNumeric(String string){
	boolean alphaNumeric = true;
    String pattern= "^[pLpN]*$";
		//check characters in the string
        if(string.matches(pattern)){
            alphaNumeric = false;
        }//end if
        return (alphaNumeric);   
	}//end isAlphaNumeric
	
	public void dereferenceEmptyNodes(){
		//this is purely a check for and remove any nodes that contain empty payloads
		//This usually happens when a text file has not been formated well
		resetToHead();
		//create Nodes to store the references to the previous and next node
		SentenceNode previous = head;
		SentenceNode next;
		for (int i = 0; i < getTotalSentences(); i++){
			//if payload string is NOT empty after stripping
			boolean containsSomething = isAlphaNumeric(current.getPayload());
			if (containsSomething == true){
				//if the current payload is NOT empty
				previous = current;
				nextNode();
			} else {
				//if current payload IS empty after stripping
				if (current != head){
					//go to the next node, set nextNode to the current node
					nextNode();
					next = current;
					//previous contains reference to the last node that contained a sentence
					//so go back to that one, and set it to point to next. Return to next node
					current = previous;
					current.setNext(next);
					nextNode();
					setTotalSentences(getTotalSentences() - 1);
				} else {
					//if head node is empty, change the head to the next node
					nextNode();
					head = current;
					setTotalSentences(getTotalSentences() - 1);
				}//end if
			}//end if
		}//end for
	}//end dereferenceEmptyNodes

	public void printFunctionWords(){
		//Print all function words to console
		System.out.println("");
		System.out.println("Auxiliary Verbs");
		System.out.println("--------");
		int totalFunctionWords = 0;
		int counter = 1;
		for (String word : getFunctionWords("./assets/EnglishAuxiliaryVerbs.txt")){
			//print all auxiliary words that are in the hashmap
			System.out.println(counter + ") " + word + ": " + wordFrequency.get(word));
			totalFunctionWords += wordFrequency.get(word);
			counter++;
		}//end for
		System.out.println(totalFunctionWords + " total auxiliary verbs");
		
		System.out.println("");
		System.out.println("Conjunctions");
		System.out.println("--------");
		totalFunctionWords = 0;
		counter = 1;
		for (String word : getFunctionWords("./assets/EnglishConjunctions.txt")){
			//print all auxiliary verbs in the hashmap
			System.out.println(counter + ") " + word + ": " + wordFrequency.get(word));
			totalFunctionWords += wordFrequency.get(word);
			counter++;
		}//end for
		System.out.println(totalFunctionWords + " total conjunctions");
		
		System.out.println("");
		System.out.println("Determiners");
		System.out.println("--------");
		totalFunctionWords = 0;
		counter = 1;
		for (String word : getFunctionWords("./assets/EnglishDeterminers.txt")){
			//print all determiners in the hashmap
			System.out.println(counter + ") " + word + ": " + wordFrequency.get(word));
			totalFunctionWords += wordFrequency.get(word);
			counter++;
		}//end for
		System.out.println(totalFunctionWords + " total determiners");
		
		System.out.println("");
		System.out.println("Prepositions");
		System.out.println("--------");
		totalFunctionWords = 0;
		counter = 1;
		for (String word : getFunctionWords("./assets/EnglishPrepositions.txt")){
			//print all prepositions that are in the hashmap
			System.out.println(counter + ") " + word + ": " + wordFrequency.get(word));
			totalFunctionWords += wordFrequency.get(word);
			counter++;
		}//end for
		System.out.println(totalFunctionWords + " total prepositions");
		
		System.out.println("");
		System.out.println("Pronouns");
		System.out.println("--------");
		totalFunctionWords = 0;
		counter = 1;
		for (String word : getFunctionWords("./assets/EnglishPronouns.txt")){								
			//print all pronouns that are in the hashmap
			System.out.println(counter + ") " + word + ": " + wordFrequency.get(word));
			totalFunctionWords += wordFrequency.get(word);
			counter++;
		}//end for
		System.out.println(totalFunctionWords + " total pronouns");
		
		System.out.println("");
		System.out.println("Quantifiers");
		System.out.println("--------");
		totalFunctionWords = 0;
		counter = 1;
		for (String word : getFunctionWords("./assets/EnglishQuantifiers.txt")){
			//print all quantifiers that are in the hashmap
			System.out.println(counter + ") " + word + ": " + wordFrequency.get(word));
			totalFunctionWords += wordFrequency.get(word);
			counter++;
		}//end for
		System.out.println(totalFunctionWords + " total quantifiers");
	}//end printFunctionWords
	
	public void writeReportCsv(String inputFileName) {
			//write comprehensive report of text to a .txt file
		try {
			
			//replace txt extension with csv
			String outFileName = "measurements_" + inputFileName.replaceAll("txt","csv");
		    FileWriter outFile = new FileWriter("./reports/" + outFileName, false);
		    PrintWriter output = new PrintWriter(outFile);
			
			//print out all measurements
			output.println("Total sentences," + getTotalSentences());
			output.println("Total words," + getTotalWords());
			output.println("Unique words," + wordFrequency.size());
			output.println("Total characters," + getTotalCharacters());
			//average of words per sentence
			output.println("Average # of words per sentence," + getAverage(getTotalWords(), getTotalSentences()));
			//average word length
			output.println("Average word length," + getAverage(getTotalCharacters(), getTotalWords()));
			output.println("");
			
			
			//print top 50 keywords (no stopwords)
			output.println("Top 50 Keywords");
					
			for (String word : getKeyWords(50, false)){
				//print keyword and frequency
				output.println(word + "," + wordFrequency.get(word));
			}//end for

			//Print all function words
			output.println("");
			output.println("Auxiliary Verbs");
			int totalFunctionWords = 0;
			for (String word : getFunctionWords("./assets/EnglishAuxiliaryVerbs.txt")){
				//print all auxiliary words that are in the hashmap
				output.println(word + "," + wordFrequency.get(word));
				totalFunctionWords += wordFrequency.get(word);
			}//end for
			output.println("Total," + totalFunctionWords);
			
			output.println("");
			output.println("Conjunctions");
			totalFunctionWords = 0;
			for (String word : getFunctionWords("./assets/EnglishConjunctions.txt")){
				//print all auxiliary verbs in the hashmap
				output.println(word + "," + wordFrequency.get(word));
				totalFunctionWords += wordFrequency.get(word);
			}//end for
			output.println("Total," + totalFunctionWords);
			
			output.println("");
			output.println("Determiners");
			totalFunctionWords = 0;
			for (String word : getFunctionWords("./assets/EnglishDeterminers.txt")){
				//print all determiners in the hashmap
				output.println(word + "," + wordFrequency.get(word));
				totalFunctionWords += wordFrequency.get(word);
			}//end for
			output.println("Total," + totalFunctionWords);
			
			output.println("");
			output.println("Prepositions");
			totalFunctionWords = 0;
			for (String word : getFunctionWords("./assets/EnglishPrepositions.txt")){
				//print all prepositions that are in the hashmap
				output.println(word + "," + wordFrequency.get(word));
				totalFunctionWords += wordFrequency.get(word);
			}//end for
			output.println("Total," + totalFunctionWords);
			
			output.println("");
			output.println("Pronouns");
			totalFunctionWords = 0;
			for (String word : getFunctionWords("./assets/EnglishPronouns.txt")){								
				//print all pronouns that are in the hashmap
				output.println(word + "," + wordFrequency.get(word));
				totalFunctionWords += wordFrequency.get(word);
			}//end for
			output.println("Total," + totalFunctionWords);
			
			output.println("");
			output.println("Quantifiers");
			totalFunctionWords = 0;
			for (String word : getFunctionWords("./assets/EnglishQuantifiers.txt")){
				//print all quantifiers that are in the hashmap
				output.println(word + "," + wordFrequency.get(word));
				totalFunctionWords += wordFrequency.get(word);
			}//end for
			output.println("Total," + totalFunctionWords);
		    output.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		} // end try
	}//end writeReportCSV
	
	public void writeReportTxt(String inputFileName) {
		//write comprehensive report of text to a .txt file
		try {
			
			//replace txt extension with csv
			String outFileName = "full_report_" + inputFileName;
		    FileWriter outFile = new FileWriter("./reports/" + outFileName, false);
		    PrintWriter output = new PrintWriter(outFile);
		    
			
			//get date and time
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Date date = new Date();
			
		    output.println(outFileName);
			output.println(dateFormat.format(date));
			output.println("");
			//print out all measurements
			output.println(getTotalSentences() + " total sentences");
			output.println(getTotalWords() + " total words");
			output.println(wordFrequency.size() + " unique words");
			output.println(getTotalCharacters() + " total characters");
			//average of words per sentence
			output.println(getAverage(getTotalWords(), getTotalSentences()) + " is the average number of words per sentence");
			//average word length
			output.println(getAverage(getTotalCharacters(), getTotalWords()) + " is the average word length");
			output.println("");
			
			
			//print top 50 keywords (no stopwords)
			output.println("Top 50 Keywords");
			output.println("--------");
			
			int counter = 1;		
			for (String word : getKeyWords(50, false)){
				//print keyword and frequency
				output.println(counter + ") " + word + ": " + wordFrequency.get(word));
				counter++;
			}//end for

			//Print all function words
			output.println("");
			output.println("Auxiliary Verbs");
			output.println("--------");
			int totalFunctionWords = 0;
			counter = 1;
			for (String word : getFunctionWords("./assets/EnglishAuxiliaryVerbs.txt")){
				//print all auxiliary words that are in the hashmap
				output.println(counter + ") " + word + ": " + wordFrequency.get(word));
				totalFunctionWords += wordFrequency.get(word);
				counter++;
			}//end for
			output.println(totalFunctionWords + " total auxiliary verbs");
			
			output.println("");
			output.println("Conjunctions");
			output.println("--------");
			totalFunctionWords = 0;
			counter = 1;
			for (String word : getFunctionWords("./assets/EnglishConjunctions.txt")){
				//print all auxiliary verbs in the hashmap
				output.println(counter + ") " + word + ": " + wordFrequency.get(word));
				totalFunctionWords += wordFrequency.get(word);
				counter++;
			}//end for
			output.println(totalFunctionWords + " total conjunctions");
			
			output.println("");
			output.println("Determiners");
			output.println("--------");
			totalFunctionWords = 0;
			counter = 1;
			for (String word : getFunctionWords("./assets/EnglishDeterminers.txt")){
				//print all determiners in the hashmap
				output.println(counter + ") " + word + ": " + wordFrequency.get(word));
				totalFunctionWords += wordFrequency.get(word);
				counter++;
			}//end for
			output.println(totalFunctionWords + " total determiners");
			
			output.println("");
			output.println("Prepositions");
			output.println("--------");
			totalFunctionWords = 0;
			counter = 1;
			for (String word : getFunctionWords("./assets/EnglishPrepositions.txt")){
				//print all prepositions that are in the hashmap
				output.println(counter + ") " + word + ": " + wordFrequency.get(word));
				totalFunctionWords += wordFrequency.get(word);
				counter++;
			}//end for
			output.println(totalFunctionWords + " total prepositions");
			
			output.println("");
			output.println("Pronouns");
			output.println("--------");
			totalFunctionWords = 0;
			counter = 1;
			for (String word : getFunctionWords("./assets/EnglishPronouns.txt")){								
				//print all pronouns that are in the hashmap
				output.println(counter + ") " + word + ": " + wordFrequency.get(word));
				totalFunctionWords += wordFrequency.get(word);
				counter++;
			}//end for
			output.println(totalFunctionWords + " total pronouns");
			
			output.println("");
			output.println("Quantifiers");
			output.println("--------");
			totalFunctionWords = 0;
			counter = 1;
			for (String word : getFunctionWords("./assets/EnglishQuantifiers.txt")){
				//print all quantifiers that are in the hashmap
				output.println(counter + ") " + word + ": " + wordFrequency.get(word));
				totalFunctionWords += wordFrequency.get(word);
				counter++;
			}//end for
			output.println(totalFunctionWords + " total quantifiers");
			
			output.println("");
			output.println("--------");
			//This will print a sentence
			//followed by the number of characters per word,
			//total characters, and number of words in that sentence
			for (int i = 0; i < getTotalSentences(); i++){
				if (i == 0){
					//if first pass, set Node to head
					resetToHead();
				} else {
					//if not first pass, go to the next node
					nextNode();
				}//end else
				output.println((i + 1) + ") " + current.getPayload());
				output.println("Characters Per Word: " + current.getLettersPerWord());
				output.println("Characters In Sentence: " + current.getTotalCharactersInSentence());
				output.println("Words In Sentence: " + current.getTotalWordsInSentence());
				output.println("");
			}//end for
		    output.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		} // end try
	}//end writeReport
}//end class definition