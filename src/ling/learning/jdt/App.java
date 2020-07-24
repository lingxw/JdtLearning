package ling.learning.jdt;

import java.io.IOException;

import ling.learning.jdt.astvisitor.PropertyVisitor;
import ling.learning.jdt.astvisitor.TypeFinderVisitor;
import ling.learning.jdt.astvisitor.TypeResoveVisitor;
import ling.learning.jdt.parser.ParserWithResove;
import ling.learning.jdt.parser.ProjectParser;

public class App {

	public static void main(String[] args) throws IOException {
		Integer nSwitch = 0;
		if(args.length > 1) {
			nSwitch = Integer.valueOf(args[1]);
		}
		switch(nSwitch) {
		case 1:
			test1();
			break;
		case 2:
			test2();
			break;
		case 3:
			test3();
			break;
		default:
			test4();
			break;
		}
	}
	
	public static void test1() throws IOException {
		ParserWithResove parser = new ParserWithResove();
		TypeFinderVisitor visitor = new TypeFinderVisitor();
		
		//parser.ParseFilesInDir("../testproj", visitor, -1);
		//parser.ParseFilesInDir("../JdtParserBinding", visitor, -1);
		parser.ParseFilesInDir("./", visitor, 1);//JdtLearning
	}
	
	public static void test2() throws IOException {
		ParserWithResove parser = new ParserWithResove();
		PropertyVisitor visitor = new PropertyVisitor();
		
		//parser.ParseFilesInDir("./test/testproj", visitor, -1);
		parser.ParseFilesInDir("./", visitor, 1);//JdtLearning
	}
	
	public static void test3() throws IOException {
		ProjectParser parser = new ProjectParser(true);
		TypeFinderVisitor visitor = new TypeFinderVisitor();
		
		//parser.ParseFilesInDir("../testproj", visitor);
		parser.ParseFilesInDir("./", visitor);//JdtLearning
	}
	
	public static void test4() throws IOException {
		ProjectParser parser = new ProjectParser(true);
		TypeResoveVisitor visitor = new TypeResoveVisitor();
		
		//parser.ParseFilesInDir("../testproj", visitor);
		parser.ParseFilesInDir("./", visitor);//JdtLearning
	}
}
