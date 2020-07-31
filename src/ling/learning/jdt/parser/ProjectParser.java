package ling.learning.jdt.parser;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.FileASTRequestor;

import ling.learning.common.FileUtil;
import ling.learning.jdt.jar.resolver.StrongFileSystem;
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
		parser.setStatementsRecovery(needResolve);

		//to set the options of java core
		Map<String, String> options = JavaCore.getOptions();
		JavaCore.setComplianceOptions(JavaCore.VERSION_1_8, options);
		parser.setCompilerOptions(options);
		parser.setEnvironment(pe.getClassPathArray(),pe.getSrcPathArray(),pe.getCodePageArray(),true);
		//StrongFileSystem fileSystem = StrongFileSystem.newFileSystem(pe.getClassPathArray(),pe.getSrcPathArray(),pe.getCodePageArray(),true);
		StrongFileSystem fileSystem = StrongFileSystem.newFileSystem(pe);
		
		String[] bindingKeys = new String[] {};
		FileASTRequestor requestor = new VariableRequestor(visitor, fileSystem);
		
		parser.createASTs(pe.getFilePathArray(), null, bindingKeys, requestor, null);
	}
	

	
	// loop directory to get file list
	public void ParseFilesInDir(String projectPath, ASTVisitor visitor, boolean bSourceJar) throws IOException {
		File project = new File(projectPath);
		String projectPath2 = project.getCanonicalPath();
		String projectName = projectPath2.substring(projectPath2.lastIndexOf(File.separator) + 1);
				
		System.out.println("Java Project Parser starting!");
		System.out.println("Java Project [" + projectPath2 + "] Parsing start" );
		//get all the parsing target
		String srcPath = project.getCanonicalPath() + File.separator + "src" + File.separator;
		List<File> listFiles = FileUtil.GetAllFiles(srcPath);
		
		//prepare parsing environment info
		ParsingEnvironment pe = new ParsingEnvironment();		
		String libPath = project.getCanonicalPath() + File.separator + "lib" + File.separator;
		List<File> listLib = FileUtil.GetAllFiles(libPath);
		HashMap<String, String> mapClasspath = new HashMap<String, String>();
		

		pe.setProjectPath(projectPath2);
		pe.setProjectJar(projectName + ".jar");
		if (bSourceJar) {
			for(File f: listLib) {
				String jarFile = f.getAbsolutePath();
				int find = f.getName().indexOf('_');
				if(find != -1) {
					String packageName = f.getName().substring(0, find);
					if (packageName.endsWith(".source")) {
						mapClasspath.put(packageName, jarFile);
					}
				}
			}
		}
		
		for(File f: listLib) {
			String jarFile = f.getAbsolutePath();
			int find = f.getName().indexOf('_');
			String packageName = null;
			if(find != -1) {
				packageName = f.getName().substring(0, find);
			} else {
				packageName = f.getName();
			}
			
			if (packageName.endsWith(".source")) {
				continue;
			}
			
			if (bSourceJar) {
				if(mapClasspath.containsKey(packageName + ".source")) {
					pe.addSrcPath(mapClasspath.get(packageName + ".source"));
					pe.addCodePage("UTF-8");
				} else {
					pe.addClassPath(jarFile);
				}
			} else {
				pe.addClassPath(jarFile);
			}
		}
		
		for (File f : listFiles) {
			String srcFile = f.getAbsolutePath();
			pe.addFilePath(srcFile);	
		}
		
		analyze(pe, visitor);
		System.out.println("Java Project [" + projectPath2 + "] Parsing finished" );
	}
}
