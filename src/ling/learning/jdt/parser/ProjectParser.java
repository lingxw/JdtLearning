package ling.learning.jdt.parser;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.FileASTRequestor;

import ling.learning.common.FileUtil;
import ling.learning.jdt.requestor.VariableRequestor;

public class ProjectParser {
	
	private boolean needResolve;
	
	public ProjectParser(boolean bResolve) {
		needResolve = bResolve;
	}
	public void analyze(ParsingEnvironment pe, ASTVisitor visitor)  throws IOException{
		//to create the parser
		ASTParser parser = ASTParser.newParser(AST.JLS8);
		
		//to set the properties of binding
		parser.setResolveBindings(needResolve); // enable binding analyze
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setBindingsRecovery(needResolve);
		
		//to set the options of java core
		Map<String, String> options = JavaCore.getOptions();
		JavaCore.setComplianceOptions(JavaCore.VERSION_1_8, options);
		parser.setCompilerOptions(options);
		parser.setEnvironment(pe.getClassPathArray(),null,null,true);
		
		String[] bindingKeys = new String[] {};
		FileASTRequestor requestor = new VariableRequestor(visitor);
		
		parser.createASTs(pe.getSrcPathArray(), pe.getCodePageArray(), bindingKeys, requestor, null);
	}
	
	// loop directory to get file list
	public void ParseFilesInDir(String projectPath, ASTVisitor visitor) throws IOException {
		File project = new File(projectPath);
		
		System.out.println("Java Project Parser starting!");
		System.out.println("Java Project [" + project.getCanonicalPath() + "] Parsing start" );
		//get all the parsing target
		String srcPath = project.getCanonicalPath() + File.separator + "src" + File.separator;
		List<File> listFiles = FileUtil.GetAllFiles(srcPath);
		
		//prepare parsing environment info
		ParsingEnvironment pe = new ParsingEnvironment();		
		String libPath = project.getCanonicalPath() + File.separator + "lib" + File.separator;
		List<File> listLib = FileUtil.GetAllFiles(libPath);
		for(File f: listLib) {
			String jarFile = f.getAbsolutePath();
			pe.addClassPath(jarFile);		
		}
		
		for (File f : listFiles) {
			String srcFile = f.getAbsolutePath();
			pe.addSrcPath(srcFile);	
			pe.addCodePage("UTF-8");
		}
		
		analyze(pe, visitor);
		System.out.println("Java Project [" + project.getCanonicalPath() + "] Parsing finished" );
	}
}
