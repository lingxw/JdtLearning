package ling.learning.jdt;

import java.io.IOException;

import ling.learning.jdt.astvisitor.PropertyVisitor;
import ling.learning.jdt.astvisitor.TypeFinderVisitor;
import ling.learning.jdt.parser.ParserWithResove;

public class App {

	public static void main(String[] args) throws IOException {
		
		ParserWithResove parser = new ParserWithResove();
		TypeFinderVisitor visitor = new TypeFinderVisitor();
		
		//parser.ParseFilesInDir("../testproj", visitor, -1);
		//parser.ParseFilesInDir("../JdtParserBinding", visitor, -1);
		//parser.ParseFilesInDir("./", visitor, 1);//JdtLearning
		
		PropertyVisitor propVisitor = new PropertyVisitor();
		parser.ParseFilesInDir("../testproj", propVisitor, -1);
		//parser.ParseFilesInDir("./", propVisitor, 1);//JdtLearning
	}
}
