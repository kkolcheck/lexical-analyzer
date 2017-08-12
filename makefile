LexicalAnalyser.class: LexicalAnalyser.java CurrentText.class SentenceNode.class
	javac LexicalAnalyser.java
CurrentText.class: CurrentText.java SentenceNode.class
	javac CurrentText.java
SentenceNode.class: SentenceNode.java
	javac SentenceNode.java
clean:
	rm *.class
run: LexicalAnalyser.class
	java LexicalAnalyser