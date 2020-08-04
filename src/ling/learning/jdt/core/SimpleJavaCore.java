package ling.learning.jdt.core;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IAccessRule;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.core.JavaProject;

public class SimpleJavaCore {
	/**
	 * Creates and returns a new classpath entry of kind <code>CPE_LIBRARY</code> for the JAR or folder
	 * identified by the given absolute path. This specifies that all package fragments within the root
	 * will have children of type <code>IClassFile</code>.
	 * <p>
	 * A library entry is used to denote a prerequisite JAR or root folder containing binaries.
	 * The target JAR can either be defined internally to the workspace (absolute path relative
	 * to the workspace root), or externally to the workspace (absolute path in the file system).
	 * The target root folder can also be defined internally to the workspace (absolute path relative
	 * to the workspace root), or - since 3.4 - externally to the workspace (absolute path in the file system).
	 * Since 3.5, the path to the library can also be relative to the project using ".." as the first segment. 
	 * </p>
	 * <p>
	 * e.g. Here are some examples of binary path usage
	 * </p>
	 *	<ul>
	 *	<li><code> "c:\jdk1.2.2\jre\lib\rt.jar" </code> - reference to an external JAR on Windows</li>
	 *	<li><code> "/Project/someLib.jar" </code> - reference to an internal JAR on Windows or Linux</li>
	 *	<li><code> "/Project/classes/" </code> - reference to an internal binary folder on Windows or Linux</li>
	 *	<li><code> "/home/usr/classes" </code> - reference to an external binary folder on Linux</li>
	 *	<li><code> "../../lib/someLib.jar" </code> - reference to an external JAR that is a sibling of the workspace on either platform</li>
	 * </ul>
	 * Note that on non-Windows platform, a path <code>"/some/lib.jar"</code> is ambiguous.
	 * It can be a path to an external JAR (its file system path being <code>"/some/lib.jar"</code>)
	 * or it can be a path to an internal JAR (<code>"some"</code> being a project in the workspace).
	 * Such an ambiguity is solved when the classpath entry is used (e.g. in {@link IJavaProject#getPackageFragmentRoots()}).
	 * If the resource <code>"lib.jar"</code> exists in project <code>"some"</code>, then it is considered an
	 * internal JAR. Otherwise it is an external JAR.
	 * <p>Also note that this operation does not attempt to validate or access the
	 * resources at the given paths.
	 * </p><p>
	 * The access rules determine the set of accessible class files
	 * in the library. If the list of access rules is empty then all files
	 * in this library are accessible.
	 * See {@link IAccessRule} for a detailed description of access
	 * rules.
	 * </p>
	 * <p>
	 * The <code>extraAttributes</code> list contains name/value pairs that must be persisted with
	 * this entry. If no extra attributes are provided, an empty array must be passed in.<br>
	 * Note that this list should not contain any duplicate name.
	 * </p>
	 * <p>
	 * The <code>isExported</code> flag indicates whether this entry is contributed to dependent
	 * projects. If not exported, dependent projects will not see any of the classes from this entry.
	 * If exported, dependent projects will concatenate the accessible files patterns of this entry with the
	 * accessible files patterns of the projects, and they will concatenate the non accessible files patterns of this entry
	 * with the non accessible files patterns of the project.
	 * </p>
	 * <p>
	 * Since 3.5, if the library is a ZIP archive, the "Class-Path" clause (if any) in the "META-INF/MANIFEST.MF" is read
	 * and referenced ZIP archives are added to the {@link IJavaProject#getResolvedClasspath(boolean) resolved classpath}.
	 * </p>
	 *
	 * @param path the path to the library
	 * @param sourceAttachmentPath the absolute path of the corresponding source archive or folder,
	 *    or <code>null</code> if none. Note, since 3.0, an empty path is allowed to denote no source attachment.
	 *   and will be automatically converted to <code>null</code>. Since 3.4, this path can also denote a path external
	 *   to the workspace.
	 * @param sourceAttachmentRootPath the location of the root of the source files within the source archive or folder
	 *    or <code>null</code> if this location should be automatically detected.
	 * @param accessRules the possibly empty list of access rules for this entry
	 * @param extraAttributes the possibly empty list of extra attributes to persist with this entry
	 * @param isExported indicates whether this entry is contributed to dependent
	 * 	  projects in addition to the output location
	 * @return a new library classpath entry
	 * @since 3.1
	 */
	public static IClasspathEntry newLibraryEntry(
			IPath path,
			IPath sourceAttachmentPath,
			IPath sourceAttachmentRootPath,
			IAccessRule[] accessRules,
			IClasspathAttribute[] extraAttributes,
			boolean isExported) {

		if (path == null) throw new SimpleClasspathEntry.AssertionFailedException("Library path cannot be null"); //$NON-NLS-1$
		if (accessRules == null || accessRules.length==0) {
			accessRules = SimpleClasspathEntry.NO_ACCESS_RULES;
		}
		if (extraAttributes == null || extraAttributes.length==0) {
			extraAttributes = SimpleClasspathEntry.NO_EXTRA_ATTRIBUTES;
		}
		boolean hasDotDot = SimpleClasspathEntry.hasDotDot(path);
		if (!hasDotDot && !path.isAbsolute()) throw new SimpleClasspathEntry.AssertionFailedException("Path for IClasspathEntry must be absolute: " + path); //$NON-NLS-1$
		if (sourceAttachmentPath != null) {
			if (sourceAttachmentPath.isEmpty()) {
				sourceAttachmentPath = null; // treat empty path as none
			} else if (!sourceAttachmentPath.isAbsolute()) {
				throw new SimpleClasspathEntry.AssertionFailedException("Source attachment path '" //$NON-NLS-1$
						+ sourceAttachmentPath
						+ "' for IClasspathEntry must be absolute"); //$NON-NLS-1$
			}
		}
		return new SimpleClasspathEntry(
			IPackageFragmentRoot.K_BINARY,
			IClasspathEntry.CPE_LIBRARY,
			hasDotDot ? path : SimpleWorkspace.getProject().canonicalizedPath(path),
			SimpleClasspathEntry.INCLUDE_ALL, // inclusion patterns
			SimpleClasspathEntry.EXCLUDE_NONE, // exclusion patterns
			sourceAttachmentPath,
			sourceAttachmentRootPath,
			null, // specific output folder
			isExported,
			accessRules,
			false, // no access rules to combine
			extraAttributes);
	}
	
	/**
	 * Creates and returns a new classpath entry of kind <code>CPE_SOURCE</code>
	 * for the project's source folder identified by the given absolute
	 * workspace-relative path using the given inclusion and exclusion patterns
	 * to determine which source files are included, and the given output path
	 * to control the output location of generated files.
	 * <p>
	 * The source folder is referred to using an absolute path relative to the
	 * workspace root, e.g. <code>/Project/src</code>. A project's source
	 * folders are located with that project. That is, a source classpath
	 * entry specifying the path <code>/P1/src</code> is only usable for
	 * project <code>P1</code>.
	 * </p>
	 * <p>
	 * The inclusion patterns determines the initial set of source files that
	 * are to be included; the exclusion patterns are then used to reduce this
	 * set. When no inclusion patterns are specified, the initial file set
	 * includes all relevant files in the resource tree rooted at the source
	 * entry's path. On the other hand, specifying one or more inclusion
	 * patterns means that all <b>and only</b> files matching at least one of
	 * the specified patterns are to be included. If exclusion patterns are
	 * specified, the initial set of files is then reduced by eliminating files
	 * matched by at least one of the exclusion patterns. Inclusion and
	 * exclusion patterns look like relative file paths with wildcards and are
	 * interpreted relative to the source entry's path. File patterns are
	 * case-sensitive can contain '**', '*' or '?' wildcards (see
	 * {@link IClasspathEntry#getExclusionPatterns()} for the full description
	 * of their syntax and semantics). The resulting set of files are included
	 * in the corresponding package fragment root; all package fragments within
	 * the root will have children of type <code>ICompilationUnit</code>.
	 * </p>
	 * <p>
	 * For example, if the source folder path is
	 * <code>/Project/src</code>, there are no inclusion filters, and the
	 * exclusion pattern is
	 * <code>com/xyz/tests/&#42;&#42;</code>, then source files
	 * like <code>/Project/src/com/xyz/Foo.java</code>
	 * and <code>/Project/src/com/xyz/utils/Bar.java</code> would be included,
	 * whereas <code>/Project/src/com/xyz/tests/T1.java</code>
	 * and <code>/Project/src/com/xyz/tests/quick/T2.java</code> would be
	 * excluded.
	 * </p>
	 * <p>
	 * Additionally, a source entry can be associated with a specific output location.
	 * By doing so, the Java builder will ensure that the generated ".class" files will
	 * be issued inside this output location, as opposed to be generated into the
	 * project default output location (when output location is <code>null</code>).
	 * Note that multiple source entries may target the same output location.
	 * The output location is referred to using an absolute path relative to the
	 * workspace root, e.g. <code>"/Project/bin"</code>, it must be located inside
	 * the same project as the source folder.
	 * </p>
	 * <p>
	 * Also note that all sources/binaries inside a project are contributed as
	 * a whole through a project entry
	 * (see <code>JavaCore.newProjectEntry</code>). Particular source entries
	 * cannot be selectively exported.
	 * </p>
	 * <p>
	 * The <code>extraAttributes</code> list contains name/value pairs that must be persisted with
	 * this entry. If no extra attributes are provided, an empty array must be passed in.<br>
	 * Note that this list should not contain any duplicate name.
	 * </p>
	 *
	 * @param path the absolute workspace-relative path of a source folder
	 * @param inclusionPatterns the possibly empty list of inclusion patterns
	 *    represented as relative paths
	 * @param exclusionPatterns the possibly empty list of exclusion patterns
	 *    represented as relative paths
	 * @param specificOutputLocation the specific output location for this source entry (<code>null</code> if using project default ouput location)
	 * @param extraAttributes the possibly empty list of extra attributes to persist with this entry
	 * @return a new source classpath entry with the given exclusion patterns
	 * @see IClasspathEntry#getInclusionPatterns()
	 * @see IClasspathEntry#getExclusionPatterns()
	 * @see IClasspathEntry#getOutputLocation()
	 * @since 3.1
	 */
	public static IClasspathEntry newSourceEntry(IPath path, IPath[] inclusionPatterns, IPath[] exclusionPatterns, IPath specificOutputLocation, IClasspathAttribute[] extraAttributes) {

		if (path == null) throw new SimpleClasspathEntry.AssertionFailedException("Source path cannot be null"); //$NON-NLS-1$
		if (!path.isAbsolute()) throw new SimpleClasspathEntry.AssertionFailedException("Path for IClasspathEntry must be absolute"); //$NON-NLS-1$
		if (exclusionPatterns == null) {
			exclusionPatterns = SimpleClasspathEntry.EXCLUDE_NONE;
		}
		if (inclusionPatterns == null) {
			inclusionPatterns = SimpleClasspathEntry.INCLUDE_ALL;
		}
		if (extraAttributes == null) {
			extraAttributes = SimpleClasspathEntry.NO_EXTRA_ATTRIBUTES;
		}
		
		return new SimpleClasspathEntry(
			IPackageFragmentRoot.K_SOURCE,
			IClasspathEntry.CPE_SOURCE,
			path,
			inclusionPatterns,
			exclusionPatterns,
			null, // source attachment
			null, // source attachment root
			specificOutputLocation, // custom output location
			false,
			null,
			false, // no access rules to combine
			extraAttributes);
	}
	
	/**
	 * Creates and returns a new classpath entry of kind <code>CPE_PROJECT</code>
	 * for the project identified by the given absolute path.
	 * <p>
	 * A project entry is used to denote a prerequisite project on a classpath.
	 * The referenced project will be contributed as a whole, either as sources (in the Java Model, it
	 * contributes all its package fragment roots) or as binaries (when building, it contributes its
	 * whole output location).
	 * </p>
	 * <p>
	 * A project reference allows to indirect through another project, independently from its internal layout.
	 * </p><p>
	 * The prerequisite project is referred to using an absolute path relative to the workspace root.
	 * </p>
	 * <p>
	 * The access rules determine the set of accessible class files
	 * in the project. If the list of access rules is empty then all files
	 * in this project are accessible.
	 * See {@link IAccessRule} for a detailed description of access rules.
	 * </p>
	 * <p>
	 * The <code>combineAccessRules</code> flag indicates whether access rules of one (or more)
	 * exported entry of the project should be combined with the given access rules. If they should
	 * be combined, the given access rules are considered first, then the entry's access rules are
	 * considered.
	 * </p>
	 * <p>
	 * The <code>extraAttributes</code> list contains name/value pairs that must be persisted with
	 * this entry. If no extra attributes are provided, an empty array must be passed in.<br>
	 * Note that this list should not contain any duplicate name.
	 * </p>
	 * <p>
	 * The <code>isExported</code> flag indicates whether this entry is contributed to dependent
	 * projects. If not exported, dependent projects will not see any of the classes from this entry.
	 * If exported, dependent projects will concatenate the accessible files patterns of this entry with the
	 * accessible files patterns of the projects, and they will concatenate the non accessible files patterns of this entry
	 * with the non accessible files patterns of the project.
	 * </p>
	 *
	 * @param path the absolute path of the prerequisite project
	 * @param accessRules the possibly empty list of access rules for this entry
	 * @param combineAccessRules whether the access rules of the project's exported entries should be combined with the given access rules
	 * @param extraAttributes the possibly empty list of extra attributes to persist with this entry
	 * @param isExported indicates whether this entry is contributed to dependent
	 * 	  projects in addition to the output location
	 * @return a new project classpath entry
	 * @since 3.1
	 */
	public static IClasspathEntry newProjectEntry(
			IPath path,
			IAccessRule[] accessRules,
			boolean combineAccessRules,
			IClasspathAttribute[] extraAttributes,
			boolean isExported) {

		if (!path.isAbsolute()) throw new SimpleClasspathEntry.AssertionFailedException("Path for IClasspathEntry must be absolute"); //$NON-NLS-1$
		if (accessRules == null || accessRules.length == 0) {
			accessRules = SimpleClasspathEntry.NO_ACCESS_RULES;
		}
		if (extraAttributes == null || extraAttributes.length == 0) {
			extraAttributes = SimpleClasspathEntry.NO_EXTRA_ATTRIBUTES;
		}
		return new SimpleClasspathEntry(
			IPackageFragmentRoot.K_SOURCE,
			IClasspathEntry.CPE_PROJECT,
			path,
			SimpleClasspathEntry.INCLUDE_ALL, // inclusion patterns
			SimpleClasspathEntry.EXCLUDE_NONE, // exclusion patterns
			null, // source attachment
			null, // source attachment root
			null, // specific output folder
			isExported,
			accessRules,
			combineAccessRules,
			extraAttributes);
	}
	
	/**
	 * Creates and returns a new classpath entry of kind <code>CPE_VARIABLE</code>
	 * for the given path. The first segment of the path is the name of a classpath variable.
	 * The trailing segments of the path will be appended to resolved variable path.
	 * <p>
	 * A variable entry allows to express indirect references on a classpath to other projects or libraries,
	 * depending on what the classpath variable is referring.
	 * </p>
	 * <p>
	 * It is possible to register an automatic initializer (<code>ClasspathVariableInitializer</code>),
	 * which will be invoked through the extension point "org.eclipse.jdt.core.classpathVariableInitializer".
	 * After resolution, a classpath variable entry may either correspond to a project or a library entry.
	 * </p>
	 * <p>
	 * e.g. Here are some examples of variable path usage
	 * </p>
	 * <ul>
	 * <li> "JDTCORE" where variable <code>JDTCORE</code> is
	 *		bound to "c:/jars/jdtcore.jar". The resolved classpath entry is denoting the library "c:\jars\jdtcore.jar"</li>
	 * <li> "JDTCORE" where variable <code>JDTCORE</code> is
	 *		bound to "/Project_JDTCORE". The resolved classpath entry is denoting the project "/Project_JDTCORE"</li>
	 * <li> "PLUGINS/com.example/example.jar" where variable <code>PLUGINS</code>
	 *      is bound to "c:/eclipse/plugins". The resolved classpath entry is denoting the library "c:\eclipse\plugins\com.example\example.jar"</li>
	 * </ul>
	 * <p>
	 * The access rules determine the set of accessible class files
	 * in the project or library. If the list of access rules is empty then all files
	 * in this project or library are accessible.
	 * See {@link IAccessRule} for a detailed description of access rules.
	 * </p>
	 * <p>
	 * The <code>extraAttributes</code> list contains name/value pairs that must be persisted with
	 * this entry. If no extra attributes are provided, an empty array must be passed in.<br>
	 * Note that this list should not contain any duplicate name.
	 * </p>
	 * <p>
	 * The <code>isExported</code> flag indicates whether this entry is contributed to dependent
	 * projects. If not exported, dependent projects will not see any of the classes from this entry.
	 * If exported, dependent projects will concatenate the accessible files patterns of this entry with the
	 * accessible files patterns of the projects, and they will concatenate the non accessible files patterns of this entry
	 * with the non accessible files patterns of the project.
	 * </p>
	 * <p>
	 * Note that this operation does not attempt to validate classpath variables
	 * or access the resources at the given paths.
	 * </p>
	 *
	 * @param variablePath the path of the binary archive; first segment is the
	 *   name of a classpath variable
	 * @param variableSourceAttachmentPath the path of the corresponding source archive,
	 *    or <code>null</code> if none; if present, the first segment is the
	 *    name of a classpath variable (not necessarily the same variable
	 *    as the one that begins <code>variablePath</code>)
	 * @param variableSourceAttachmentRootPath the location of the root of the source files within the source archive
	 *    or <code>null</code> if <code>variableSourceAttachmentPath</code> is also <code>null</code>
	 * @param accessRules the possibly empty list of access rules for this entry
	 * @param extraAttributes the possibly empty list of extra attributes to persist with this entry
	 * @param isExported indicates whether this entry is contributed to dependent
	 * 	  projects in addition to the output location
	 * @return a new variable classpath entry
	 * @since 3.1
	 */
	public static IClasspathEntry newVariableEntry(
			IPath variablePath,
			IPath variableSourceAttachmentPath,
			IPath variableSourceAttachmentRootPath,
			IAccessRule[] accessRules,
			IClasspathAttribute[] extraAttributes,
			boolean isExported) {

		if (variablePath == null) throw new SimpleClasspathEntry.AssertionFailedException("Variable path cannot be null"); //$NON-NLS-1$
		if (variablePath.segmentCount() < 1) {
			throw new SimpleClasspathEntry.AssertionFailedException("Illegal classpath variable path: \'" + variablePath.makeRelative().toString() + "\', must have at least one segment"); //$NON-NLS-1$//$NON-NLS-2$
		}
		if (accessRules == null || accessRules.length == 0) {
			accessRules = SimpleClasspathEntry.NO_ACCESS_RULES;
		}
		if (extraAttributes == null || extraAttributes.length == 0) {
			extraAttributes = SimpleClasspathEntry.NO_EXTRA_ATTRIBUTES;
		}

		return new SimpleClasspathEntry(
			IPackageFragmentRoot.K_SOURCE,
			IClasspathEntry.CPE_VARIABLE,
			variablePath,
			SimpleClasspathEntry.INCLUDE_ALL, // inclusion patterns
			SimpleClasspathEntry.EXCLUDE_NONE, // exclusion patterns
			variableSourceAttachmentPath, // source attachment
			variableSourceAttachmentRootPath, // source attachment root
			null, // specific output folder
			isExported,
			accessRules,
			false, // no access rules to combine
			extraAttributes);
	}
	
	/**
	 * Creates and returns a new classpath entry of kind <code>CPE_CONTAINER</code>
	 * for the given path. The path of the container will be used during resolution so as to map this
	 * container entry to a set of other classpath entries the container is acting for.
	 * <p>
	 * A container entry allows to express indirect references to a set of libraries, projects and variable entries,
	 * which can be interpreted differently for each Java project where it is used.
	 * A classpath container entry can be resolved using <code>JavaCore.getResolvedClasspathContainer</code>,
	 * and updated with <code>JavaCore.classpathContainerChanged</code>
	 * </p>
	 * <p>
	 * A container is exclusively resolved by a <code>ClasspathContainerInitializer</code> registered onto the
	 * extension point "org.eclipse.jdt.core.classpathContainerInitializer".
	 * </p>
	 * <p>
	 * A container path must be formed of at least one segment, where:
	 * </p>
	 * <ul>
	 * <li> the first segment is a unique ID identifying the target container, there must be a container initializer registered
	 * 	onto this ID through the extension point  "org.eclipse.jdt.core.classpathContainerInitializer". </li>
	 * <li> the remaining segments will be passed onto the initializer, and can be used as additional
	 * 	hints during the initialization phase. </li>
	 * </ul>
	 * <p>
	 * Example of an ClasspathContainerInitializer for a classpath container denoting a default JDK container:
	 * </p>
	 * <pre>
	 * containerEntry = JavaCore.newContainerEntry(new Path("MyProvidedJDK/default"));
	 *
	 * &lt;extension
	 *    point="org.eclipse.jdt.core.classpathContainerInitializer"&gt;
	 *    &lt;containerInitializer
	 *       id="MyProvidedJDK"
	 *       class="com.example.MyInitializer"/&gt;
	 * </pre>
	 * <p>
	 * The access rules determine the set of accessible source and class files
	 * in the container. If the list of access rules is empty, then all files
	 * in this container are accessible.
	 * See {@link IAccessRule} for a detailed description of access
	 * rules. Note that if an entry defined by the container defines access rules,
	 * then these access rules are combined with the given access rules.
	 * The given access rules are considered first, then the entry's access rules are
	 * considered.
	 * </p>
	 * <p>
	 * The <code>extraAttributes</code> list contains name/value pairs that must be persisted with
	 * this entry. If no extra attributes are provided, an empty array must be passed in.<br>
	 * Note that this list should not contain any duplicate name.
	 * </p>
	 * <p>
	 * The <code>isExported</code> flag indicates whether this entry is contributed to dependent
	 * projects. If not exported, dependent projects will not see any of the classes from this entry.
	 * If exported, dependent projects will concatenate the accessible files patterns of this entry with the
	 * accessible files patterns of the projects, and they will concatenate the non accessible files patterns of this entry
	 * with the non accessible files patterns of the project.
	 * </p>
	 * <p>
	 * Note that this operation does not attempt to validate classpath containers
	 * or access the resources at the given paths.
	 * </p>
	 *
	 * @param containerPath the path identifying the container, it must be formed of at least
	 * 	one segment (ID+hints)
	 * @param accessRules the possibly empty list of access rules for this entry
	 * @param extraAttributes the possibly empty list of extra attributes to persist with this entry
	 * @param isExported a boolean indicating whether this entry is contributed to dependent
	 *    projects in addition to the output location
	 * @return a new container classpath entry
	 *
	 * @see JavaCore#getClasspathContainer(IPath, IJavaProject)
	 * @see JavaCore#setClasspathContainer(IPath, IJavaProject[], IClasspathContainer[], IProgressMonitor)
	 * @see JavaCore#newContainerEntry(IPath, boolean)
	 * @see JavaCore#newAccessRule(IPath, int)
	 * @since 3.1
	 */
	public static IClasspathEntry newContainerEntry(
			IPath containerPath,
			IAccessRule[] accessRules,
			IClasspathAttribute[] extraAttributes,
			boolean isExported) {

		if (containerPath == null) {
			throw new SimpleClasspathEntry.AssertionFailedException("Container path cannot be null"); //$NON-NLS-1$
		} else if (containerPath.segmentCount() < 1) {
			throw new SimpleClasspathEntry.AssertionFailedException("Illegal classpath container path: \'" + containerPath.makeRelative().toString() + "\', must have at least one segment (containerID+hints)"); //$NON-NLS-1$//$NON-NLS-2$
		}
		if (accessRules == null || accessRules.length == 0) {
			accessRules = SimpleClasspathEntry.NO_ACCESS_RULES;
		}
		if (extraAttributes == null || extraAttributes.length == 0) {
			extraAttributes = SimpleClasspathEntry.NO_EXTRA_ATTRIBUTES;
		}
		return new SimpleClasspathEntry(
			IPackageFragmentRoot.K_SOURCE,
			IClasspathEntry.CPE_CONTAINER,
			containerPath,
			SimpleClasspathEntry.INCLUDE_ALL, // inclusion patterns
			SimpleClasspathEntry.EXCLUDE_NONE, // exclusion patterns
			null, // source attachment
			null, // source attachment root
			null, // specific output folder
			isExported,
			accessRules,
			true, // combine access rules
			extraAttributes);
	}
}
