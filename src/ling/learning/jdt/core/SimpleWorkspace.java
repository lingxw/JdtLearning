package ling.learning.jdt.core;

import java.util.HashMap;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

public class SimpleWorkspace {
	
	/**
	 * The workspace managed by the single instance of this
	 * plug-in runtime class, or <code>null</code> is there is none.
	 */
	private static IPath root = null;
	
	private static SimpleJavaProject project = null;
	
	private static HashMap<String, SimpleJavaProject> projects = new HashMap<String, SimpleJavaProject>();
	
	public static void newWorkspaceRoot(String path) {
		root = new Path(path);
	}
	
	public static void newProject(String name) {
		project = new SimpleJavaProject(getRoot().makeAbsolute().append(name));
		projects.put(name, project);
	}

	/**
	 * Returns the workspace. The workspace is not accessible after the resources
	 * plug-in has shutdown.
	 *
	 * @return the workspace that was created by the single instance of this
	 *   plug-in class.
	 */
	public static IPath getRoot() {
		return root;
	}
	
	public static SimpleJavaProject getProject() {
		return project;
	}

}
