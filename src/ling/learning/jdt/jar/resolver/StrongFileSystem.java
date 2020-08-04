package ling.learning.jdt.jar.resolver;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.internal.compiler.batch.ClasspathJar;
import org.eclipse.jdt.internal.compiler.batch.ClasspathJmod;
import org.eclipse.jdt.internal.compiler.batch.ClasspathMultiReleaseJar;
import org.eclipse.jdt.internal.compiler.batch.ClasspathSourceJar;
import org.eclipse.jdt.internal.compiler.batch.FileSystem;
import org.eclipse.jdt.internal.compiler.batch.Main;
import org.eclipse.jdt.internal.compiler.env.NameEnvironmentAnswer;

import ling.learning.jdt.parser.ParsingEnvironment;

public class StrongFileSystem extends FileSystem{
	
	private ParsingEnvironment env;
	
	public StrongFileSystem(ParsingEnvironment env, Classpath[] paths, String[] initialFileNames, boolean annotationsFromClasspath, Set<String> limitedModules) {
		super(paths, initialFileNames, annotationsFromClasspath, limitedModules);
		this.env = env;
	}
	
	public ParsingEnvironment getEnvironment() {
		return env;
	}

	public static StrongFileSystem newFileSystem(ParsingEnvironment env) throws IllegalStateException {
		return new StrongFileSystem(env,
				getClasspath(env.getClassPathArray(),env.getSrcPathArray(),env.getCodePageArray(),env.isIncludeRunningVMBootclasspath())
				, null, false, null);
	}
	
	public static StrongFileSystem newFileSystem(String[] classpaths, String[] sourcepaths, String[] sourcepathsEncodings, boolean includeRunningVMBootclasspath) throws IllegalStateException {
		return new StrongFileSystem(null,
				getClasspath(classpaths, sourcepaths, sourcepathsEncodings, includeRunningVMBootclasspath)
				, null, false, null);
	}
	
	public static Classpath[] getClasspath(String[] classpaths, String[] sourcepaths, String[] sourcepathsEncodings, boolean includeRunningVMBootclasspath) throws IllegalStateException {
		Main main = new Main(new PrintWriter(System.out), new PrintWriter(System.err), false/*systemExit*/, null/*options*/, null/*progress*/);
		ArrayList<Classpath> allClasspaths = new ArrayList<Classpath>();
		HashMap<String, String> mapSourcePahts = new HashMap<String, String>();
		try {
			if (includeRunningVMBootclasspath) {
				org.eclipse.jdt.internal.compiler.util.Util.collectRunningVMBootclasspath(allClasspaths);
			}
			
			if (sourcepaths != null) {
				for (int i = 0, max = sourcepaths.length; i < max; i++) {
					String encoding = sourcepathsEncodings == null ? null : sourcepathsEncodings[i];
					mapSourcePahts.put(sourcepaths[i], encoding);
					main.processPathEntries(
							Main.DEFAULT_SIZE_CLASSPATH,
							allClasspaths, sourcepaths[i], encoding, true, false);
				}
			}
			if (classpaths != null) {
				for (int i = 0, max = classpaths.length; i < max; i++) {
					main.processPathEntries(
							Main.DEFAULT_SIZE_CLASSPATH,
							allClasspaths, classpaths[i], null, false, false);
				}
			}
			ArrayList<String> pendingErrors = main.pendingErrors;
			if (pendingErrors != null && pendingErrors.size() != 0) {
				throw new IllegalStateException("invalid environment settings"); //$NON-NLS-1$
			}
		} catch (IllegalArgumentException e) {
			throw new IllegalStateException("invalid environment settings", e); //$NON-NLS-1$
		}
		
		Classpath[] allEntries = new Classpath[allClasspaths.size()];
		for(int n=0, max=allClasspaths.size(); n < max; n++ ) {
			Classpath classPath = allClasspaths.get(n);
			if(classPath instanceof ClasspathSourceJar) {
				File file = new File(convertPathSeparators(classPath.getPath()));
				String encoding = null;
				encoding = mapSourcePahts.get(classPath.getPath());
				classPath = new StrongClasspathSourceJar(file, true, null,encoding ,null);
			} else if(classPath instanceof ClasspathJar 
					&& !(classPath instanceof ClasspathJmod)
					&& !(classPath instanceof ClasspathMultiReleaseJar)) {
				File file = new File(convertPathSeparators(classPath.getPath()));
				classPath = new StrongClasspathJar(file, true, null, null);
			}
			allEntries[n] = classPath;
		}
		return allEntries;
	}
	
	public NameEnvironmentAnswer findType(ITypeBinding binding) {
		char[] typeName = null;
		String qualifiedTypeName = binding.getBinaryName();
		if(binding.isArray()) {
			qualifiedTypeName = qualifiedTypeName.substring(binding.getDimensions());
		}
		if(qualifiedTypeName.endsWith(";")) {
			qualifiedTypeName = qualifiedTypeName.substring(1, qualifiedTypeName.length() - 1);
		}
		String[] packageName = qualifiedTypeName.replace('.', '/').split("/"); 
		List<char[]> listPackage = new ArrayList<char[]>();
		for(int n = 0, max = packageName.length - 1; n < max; n++) {
			listPackage.add(packageName[n].toCharArray());
		}
		if(!listPackage.isEmpty()) {
			typeName = packageName[packageName.length - 1].toCharArray();
		}
		char[][] packages = new char[listPackage.size()][];
		listPackage.toArray(packages);
		return findType(typeName, packages);
	}
	
	public String[] getTypePath(ITypeBinding binding) {
		String[] result = new String[] {null, null};
		String line = "";
		if(binding != null) {
			//if parser by Java model, the following code can get type's jar and class.
			//but if paser by ProjectParser.java, because Java Element is null, it cann't get type's jar and class.
			IJavaElement element = binding.getJavaElement();
			if(element != null) {
				result[0] = element.getPath().toString();
				element = element.getParent();
				if(element != null) {
					result[1] = element.getElementName();
				}
			} else {
				if(binding.isFromSource()) {
					String key = binding.getKey();
					int nFind = key.indexOf('<');
					if (nFind != -1) {
						key = key.substring(1,nFind);
					}
					nFind = key.indexOf(getEnvironment().getProjectPath());
					if(nFind != -1) {
						result[0] = getEnvironment().getProjectJar();
						result[1] = binding.getBinaryName().replace('.', '/') + ".java";
						if(binding.isArray()) {
							result[1] = result[1].substring(binding.getDimensions() + 1);
						}
						
						return result;
					}
				}
				NameEnvironmentAnswer answer = findType(binding);
				if(answer != null) {
					if(answer.getBinaryType() != null) {
						if (answer.getBinaryType() instanceof StrongClassFileReader) {
							result[0] = new String (((StrongClassFileReader)answer.getBinaryType()).getZipFile().getName());
						}
						result[1] = new String (answer.getBinaryType().getFileName());
					} else if(answer.getCompilationUnit() != null){
						if (answer.getCompilationUnit() instanceof StrongCompilationUnit) {
							result[0] = new String (((StrongCompilationUnit)answer.getCompilationUnit()).getZipFile().getName());
						}
						result[1] = new String (answer.getCompilationUnit().getFileName());
					}
				}
			}
		}
		return result;
	}
	
	private static String convertPathSeparators(String path) {
		return File.separatorChar == '/'
			? path.replace('\\', '/')
			 : path.replace('/', '\\');
	}
}
