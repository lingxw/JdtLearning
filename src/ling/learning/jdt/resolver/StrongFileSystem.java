package ling.learning.jdt.resolver;

import java.io.File;
import java.io.IOException;

import org.eclipse.jdt.internal.compiler.batch.ClasspathJar;
import org.eclipse.jdt.internal.compiler.batch.ClasspathJmod;
import org.eclipse.jdt.internal.compiler.batch.ClasspathMultiReleaseJar;
import org.eclipse.jdt.internal.compiler.batch.ClasspathSourceJar;
import org.eclipse.jdt.internal.compiler.batch.FileSystem;

public class StrongFileSystem extends FileSystem{

	public StrongFileSystem(String[] classpathNames, String[] initialFileNames, String encoding) {
		super(classpathNames, initialFileNames, encoding);
		if(this.classpaths != null) {
			for(int n=0, max=this.classpaths.length; n < max; n++) {
				Classpath classPath = this.classpaths[n];
				if(classPath instanceof ClasspathJar 
						&& !(classPath instanceof ClasspathJmod)
						&& !(classPath instanceof ClasspathMultiReleaseJar)
						&& !(classPath instanceof ClasspathSourceJar)) {
					File file = new File(convertPathSeparators(classPath.getPath()));
					this.classpaths[n] = new StrongClasspathJar(file, true, null, null);
					try {
						this.classpaths[n].initialize();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	private static String convertPathSeparators(String path) {
		return File.separatorChar == '/'
			? path.replace('\\', '/')
			 : path.replace('/', '\\');
	}
}
