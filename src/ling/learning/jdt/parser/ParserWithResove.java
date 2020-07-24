package ling.learning.jdt.parser;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;

import ling.learning.common.FileUtil;
import ling.learning.jdt.parser.ParsingEnvironment;

public class ParserWithResove {

	public void analyze(ParsingEnvironment pe, ASTVisitor visitor)  throws IOException{
		
		//to create the parser
		ASTParser parser = ASTParser.newParser(AST.JLS8);
		
		//to set the properties of binding
		parser.setResolveBindings(true); // enable binding analyze
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setBindingsRecovery(true);
		
		//to set the options of java core
		Map options = JavaCore.getOptions();
		parser.setCompilerOptions(options);
		
		parser.setUnitName(pe.getFileName());
		parser.setEnvironment(pe.getClassPathArray(),pe.getSrcPathArray(),pe.getCodePageArray(),true);

		parser.setSource(FileUtil.readFileToString(pe.getFilePath()).toCharArray());
		
		final CompilationUnit cu = (CompilationUnit) parser.createAST(null);
		
		if (cu.getAST().hasBindingsRecovery()) {
			System.out.println("Binding activated.");
		}else {
			System.out.println("Binding Failed!");
		}

		cu.accept(visitor);
	}
	
	// loop directory to get file list
	public void ParseFilesInDir(String projectPath, ASTVisitor visitor, int target) throws IOException {
		File project = new File(projectPath);
		
		//get all the parsing target
		String srcPath = project.getCanonicalPath() + File.separator + "src" + File.separator;
		List<File> listFiles = FileUtil.GetAllFiles(srcPath);
		System.out.println(" Java Parser starting!");
		
		//prepare parsing environment info
		ParsingEnvironment pe = new ParsingEnvironment();
		pe.addSrcPath(srcPath);
		
		String libPath = project.getCanonicalPath() + File.separator + "lib" + File.separator;
		List<File> listLib = FileUtil.GetAllFiles(libPath);
		for(File f: listLib) {
			String jarFile = f.getAbsolutePath();
			pe.addClassPath(jarFile);		
		}
		
		pe.addCodePage("UTF-8");
		
		String filePath = null;
		String fileName = null;
		int n = 0;
		for (File f : listFiles) {
			n++;
			String strNo = "[" + n + " / " + listFiles.size() + "]" ; 
			System.out.println( strNo + "Java File [" + f.getAbsolutePath() + "] Parsing start" );
			filePath = f.getAbsolutePath();			
			fileName = f.getName();
			pe.setFilePath(filePath);
			pe.setFileName(fileName);
			
			analyze(pe, visitor);
			System.out.println( strNo + "Java File [" + f.getAbsolutePath() + "] Parsing finished" );
			
			if(n == target ) {
				break;
			}
		}
	}
}
