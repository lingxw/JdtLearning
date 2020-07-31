package ling.learning.jdt.jar.resolver;

import java.io.File;
import java.io.IOException;

import org.eclipse.jdt.internal.compiler.batch.ClasspathJar;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFormatException;
import org.eclipse.jdt.internal.compiler.classfmt.ExternalAnnotationDecorator;
import org.eclipse.jdt.internal.compiler.env.AccessRuleSet;
import org.eclipse.jdt.internal.compiler.env.IBinaryType;
import org.eclipse.jdt.internal.compiler.env.NameEnvironmentAnswer;
import org.eclipse.jdt.internal.compiler.lookup.ModuleBinding;
import org.eclipse.jdt.internal.compiler.lookup.BinaryTypeBinding.ExternalAnnotationStatus;
import org.eclipse.jdt.internal.compiler.util.SuffixConstants;

public class StrongClasspathJar extends ClasspathJar{

	public StrongClasspathJar(File file, boolean closeZipFileAtEnd, AccessRuleSet accessRuleSet,
			String destinationPath) {
		super(file, closeZipFileAtEnd, accessRuleSet, destinationPath);
	}
	
	@Override
	public NameEnvironmentAnswer findClass(char[] typeName, String qualifiedPackageName, String moduleName, String qualifiedBinaryFileName, boolean asBinaryOnly) {
		if (!isPackage(qualifiedPackageName, moduleName))
			return null; // most common case

		try {
			IBinaryType reader = StrongClassFileReader.read(this.zipFile, qualifiedBinaryFileName);
			if (reader != null) {
				char[] modName = singletonModuleNameIf(true)[0];
				if(modName == ModuleBinding.UNNAMED) {
					modName = null;
				}
				if (reader instanceof StrongClassFileReader) {
					StrongClassFileReader classReader = (StrongClassFileReader) reader;
					if (classReader.getBinding().moduleName == null)
						classReader.getBinding().moduleName = modName;
					else
						modName = classReader.getBinding().moduleName;
				}
				searchPaths:
				if (this.annotationPaths != null) {
					String qualifiedClassName = qualifiedBinaryFileName.substring(0, qualifiedBinaryFileName.length()-SuffixConstants.EXTENSION_CLASS.length()-1);
					for (String annotationPath : this.annotationPaths) {
						try {
							if (this.annotationZipFile == null) {
								this.annotationZipFile = ExternalAnnotationDecorator.getAnnotationZipFile(annotationPath, null);
							}
							reader = ExternalAnnotationDecorator.create(reader, annotationPath, qualifiedClassName, this.annotationZipFile);

							if (reader.getExternalAnnotationStatus() == ExternalAnnotationStatus.TYPE_IS_ANNOTATED) {
								break searchPaths;
							}
						} catch (IOException e) {
							// don't let error on annotations fail class reading
						}
					}
					// location is configured for external annotations, but no .eea found, decorate in order to answer NO_EEA_FILE:
					reader = new ExternalAnnotationDecorator(reader, null);
				}
				return new NameEnvironmentAnswer(reader, fetchAccessRestriction(qualifiedBinaryFileName), modName);
			}
		} catch (ClassFormatException | IOException e) {
			// treat as if class file is missing
		}
		return null;
	}
}
