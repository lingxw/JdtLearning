package ling.learning.jdt.jar.resolver;

import org.eclipse.jdt.internal.compiler.batch.CompilationUnit;

public class StrongCompilationUnit extends CompilationUnit {

	private java.util.zip.ZipFile zipFile;
	
	public void setZipFile(java.util.zip.ZipFile zipFile) {
		this.zipFile = zipFile;
	}

	public java.util.zip.ZipFile getZipFile() {
		return zipFile;
	}
	
	public StrongCompilationUnit(char[] contents, String fileName, String encoding, String destinationPath,
			boolean ignoreOptionalProblems, String modName) {
		super(contents, fileName, encoding, destinationPath, ignoreOptionalProblems, modName);
	}

	public StrongCompilationUnit(char[] contents, String fileName, String encoding) {
		this(contents, fileName, encoding, null);
	}
	public StrongCompilationUnit(char[] contents, String fileName, String encoding,
			String destinationPath) {
		this(contents, fileName, encoding, destinationPath, false, null);
	}

}
