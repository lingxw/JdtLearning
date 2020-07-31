package ling.learning.jdt.jar.resolver;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;

import org.eclipse.jdt.internal.compiler.batch.ClasspathSourceJar;
import org.eclipse.jdt.internal.compiler.env.AccessRuleSet;
import org.eclipse.jdt.internal.compiler.env.NameEnvironmentAnswer;
import org.eclipse.jdt.internal.compiler.lookup.ModuleBinding;
import org.eclipse.jdt.internal.compiler.util.Util;

public class StrongClasspathSourceJar extends ClasspathSourceJar {

	private String encoding;
	
	public StrongClasspathSourceJar(File file, boolean closeZipFileAtEnd, AccessRuleSet accessRuleSet, String encoding,
			String destinationPath) {
		super(file, closeZipFileAtEnd, accessRuleSet, encoding, destinationPath);
		this.encoding = encoding;
	}

	@Override
	public NameEnvironmentAnswer findClass(char[] typeName, String qualifiedPackageName, String moduleName, String qualifiedBinaryFileName, boolean asBinaryOnly) {
		if (!isPackage(qualifiedPackageName, moduleName))
			return null; // most common case

		ZipEntry sourceEntry = this.zipFile.getEntry(qualifiedBinaryFileName.substring(0, qualifiedBinaryFileName.length() - 6)  + SUFFIX_STRING_java);
		if (sourceEntry != null) {
			try {
				InputStream stream = null;
				char[] contents = null; 
				try {
					stream = this.zipFile.getInputStream(sourceEntry);
					contents = Util.getInputStreamAsCharArray(stream, -1, this.encoding);
				} finally {
					if (stream != null)
						stream.close();
				}
				StrongCompilationUnit compilationUnit = new StrongCompilationUnit(
					contents,
					qualifiedBinaryFileName.substring(0, qualifiedBinaryFileName.length() - 6) + SUFFIX_STRING_java,
					this.encoding,
					this.destinationPath);
				compilationUnit.setZipFile(zipFile);
				char[] modName = singletonModuleNameIf(true)[0];
				if(modName == ModuleBinding.UNNAMED) {
					modName = null;
				}
				compilationUnit.module = modName;
				return new NameEnvironmentAnswer(
					compilationUnit,
					fetchAccessRestriction(qualifiedBinaryFileName));
			} catch (IOException e) {
				// treat as if source file is missing
			}
		}
		return null;
	}
}
