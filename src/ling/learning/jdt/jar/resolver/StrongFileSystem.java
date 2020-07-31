package ling.learning.jdt.jar.resolver;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.eclipse.jdt.internal.compiler.batch.ClasspathJar;
import org.eclipse.jdt.internal.compiler.batch.ClasspathJmod;
import org.eclipse.jdt.internal.compiler.batch.ClasspathMultiReleaseJar;
import org.eclipse.jdt.internal.compiler.batch.ClasspathSourceJar;
import org.eclipse.jdt.internal.compiler.batch.FileSystem;
import org.eclipse.jdt.internal.compiler.batch.Main;

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
	
	private static String convertPathSeparators(String path) {
		return File.separatorChar == '/'
			? path.replace('\\', '/')
			 : path.replace('/', '\\');
	}
}
