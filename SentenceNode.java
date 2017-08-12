//SentenceNode.java
//Defines what a sentence node will be
//Single Link-List

public class SentenceNode {
	private
		SentenceNode next;
		String payload;
		int totalWordsInSentence;
		int totalCharactersInSentence;
	
	public SentenceNode(){
		//System.out.println("New, generic SentenceNode");
		setNext(null);
		setPayload("");
		totalWordsInSentence = 0;
		totalCharactersInSentence = 0;
	}//end constructor

	public SentenceNode(SentenceNode next, String payload){
		//System.out.println("New, overloaded SentenceNode");
		setNext(next);
		setPayload(payload);
		setTotalWordsInSentence();
		setTotalCharactersInSentence();
	}//end overload constructor

	//Setters and getters
	public void setNext(SentenceNode next){
		this.next = next;
	}//end setNext
	
	public SentenceNode getNext(){
		return (this.next);
	}//end getNext
	
	public void setPayload(String payload){
		this.payload = payload;
	}//end setPayload
	
	public String getPayload(){
		return (this.payload);
	}//end getPayload
	
	public void setTotalCharactersInSentence(){
		//get rid of punctuation, whitespace, and split into an array of words
		String delims = "[ ]+";
		String[] tokens = getPayload().replaceAll("[^A-Za-z ]", "").split(delims);

		//loop through each word, retrieving the length of each word
		int sum = 0;
		for (String word : tokens){
			sum += word.length();
		}//end for
		this.totalCharactersInSentence = sum;
	}//end setCharactersPerSentence()
	
	public int getTotalCharactersInSentence(){
		return (this.totalCharactersInSentence);		
	}//end getCharactersPerSentence
	
	public void setTotalWordsInSentence(){
		//remove whitespace and split into array of words, returning the length
		String delims = "[ ]+";
		String[] tokens = getPayload().split(delims);
		this.totalWordsInSentence = tokens.length;
	}//end setTotalWordsInSentence
	
	public int getTotalWordsInSentence(){
		return (this.totalWordsInSentence);
	}//end getWordsPerSentence

	public String getLettersPerWord(){
		//retrieve the number of letters in each word
		//get rid of punctuation, whitespace, and split into an array of words
		String delims = "[ ]+";
		String[] tokens = getPayload().replaceAll("[^A-Za-z ]", "").split(delims);

		//loop through each word, retrieving the length of each word
		String lettersPerWord = "";
		boolean firstPass = true;
		for (String word : tokens){
			if (firstPass == true){
				lettersPerWord = "" + word.length();
				firstPass = false;
			} else {
				//add a space before each word length
				lettersPerWord += " " + word.length();
			}//end if
		}//end for
		return (lettersPerWord);
	}//end getNumberOfLettersPerWord
}//end class definition