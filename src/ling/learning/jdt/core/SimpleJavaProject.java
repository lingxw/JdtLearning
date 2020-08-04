package ling.learning.jdt.core;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.internal.resources.ResourceInfo;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.internal.core.JavaProject;
import org.eclipse.jdt.internal.core.util.Messages;
import org.eclipse.jdt.internal.core.util.Util;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import ling.learning.jdt.parser.ParsingEnvironment;

public class SimpleJavaProject {
	
	/**
	 * Whether the underlying file system is case sensitive.
	 */
	protected static final boolean IS_CASE_SENSITIVE = !new File("Temp").equals(new File("temp")); //$NON-NLS-1$ //$NON-NLS-2$
	
	private IPath path;
	
	public SimpleJavaProject(String projectPath) {
		this.path = new Path(projectPath);
	}
	
	public SimpleJavaProject(IPath projectPath) {
		this.path = projectPath;
	}
	
	public String getParent() throws IOException {
		return path.removeLastSegments(1).toString();
	}
	
	public IPath getFullPath() {
		return path.makeAbsolute();
	}
	
	public String getElementName() {
		return path.lastSegment();
	}
	
	/**
	 * Reads the classpath file entries of this project's .classpath file.
	 * Returns a two-dimensional array, where the number of elements in the row is fixed to 2.
	 * The first element is an array of raw classpath entries, which includes the output entry,
	 * and the second element is an array of referenced entries that may have been stored 
	 * by the client earlier. 
	 * See {@link IJavaProject#getReferencedClasspathEntries()} for more details.
	 * As a side effect, unknown elements are stored in the given map (if not null)
	 * Throws exceptions if the file cannot be accessed or is malformed.
	 */
	public IClasspathEntry[][] readFileEntriesWithException(Map unknownElements) throws IOException {
//		IFile rscFile = this.project.getFile(JavaProject.CLASSPATH_FILENAME);
//		byte[] bytes;
//		if (rscFile.exists()) {
//			bytes = Util.getResourceContentsAsByteArray(rscFile);
//		} else {
//			// when a project is imported, we get a first delta for the addition of the .project, but the .classpath is not accessible
//			// so default to using java.io.File
//			// see https://bugs.eclipse.org/bugs/show_bug.cgi?id=96258
//			URI location = rscFile.getLocationURI();
//			if (location == null)
//				throw new IOException("Cannot obtain a location URI for " + rscFile); //$NON-NLS-1$
//			File file = Util.toLocalFile(location, null/*no progress monitor available*/);
//			if (file == null)
//				throw new IOException("Unable to fetch file from " + location); //$NON-NLS-1$
//			try {
//				bytes = org.eclipse.jdt.internal.compiler.util.Util.getFileByteContent(file);
//			} catch (IOException e) {
//				if (!file.exists())
//					return new IClasspathEntry[][]{defaultClasspath(), SimpleClasspathEntry.NO_ENTRIES};
//				throw e;
//			}
//		}
		File file = new File (path.toFile().getCanonicalFile() + File.separator + JavaProject.CLASSPATH_FILENAME);
		byte[] bytes;
		if(file.exists()) {
			bytes = org.eclipse.jdt.internal.compiler.util.Util.getFileByteContent(file);
		} else {
			throw new IOException("Unable to fetch file from " + path.toFile().getCanonicalFile()); //$NON-NLS-1$
		}
		if (hasUTF8BOM(bytes)) { // see https://bugs.eclipse.org/bugs/show_bug.cgi?id=240034
			int length = bytes.length-IContentDescription.BOM_UTF_8.length;
			System.arraycopy(bytes, IContentDescription.BOM_UTF_8.length, bytes = new byte[length], 0, length);
		}
		String xmlClasspath;
		try {
			xmlClasspath = new String(bytes, org.eclipse.jdt.internal.compiler.util.Util.UTF_8); // .classpath always encoded with UTF-8
		} catch (UnsupportedEncodingException e) {
			Util.log(e, "Could not read .classpath with UTF-8 encoding"); //$NON-NLS-1$
			// fallback to default
			xmlClasspath = new String(bytes);
		}
		return decodeClasspath(xmlClasspath, unknownElements);
	}
	
	/**
	 * Reads and decode an XML classpath string. Returns a two-dimensional array, where the number of elements in the row is fixed to 2.
	 * The first element is an array of raw classpath entries and the second element is an array of referenced entries that may have been stored
	 * by the client earlier. See {@link IJavaProject#getReferencedClasspathEntries()} for more details. 
	 * 
	 */
	public IClasspathEntry[][] decodeClasspath(String xmlClasspath, Map unknownElements) throws IOException, SimpleClasspathEntry.AssertionFailedException {

		ArrayList paths = new ArrayList();
		IClasspathEntry defaultOutput = null;
		StringReader reader = new StringReader(xmlClasspath);
		Element cpElement;
		try {
			DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			cpElement = parser.parse(new InputSource(reader)).getDocumentElement();
		} catch (SAXException | ParserConfigurationException e) {
			throw new IOException(Messages.file_badFormat, e);
		} finally {
			reader.close();
		}

		if (!cpElement.getNodeName().equalsIgnoreCase("classpath")) { //$NON-NLS-1$
			throw new IOException(Messages.file_badFormat);
		}
		NodeList list = cpElement.getElementsByTagName(SimpleClasspathEntry.TAG_CLASSPATHENTRY);
		int length = list.getLength();

		for (int i = 0; i < length; ++i) {
			Node node = list.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				IClasspathEntry entry = SimpleClasspathEntry.elementDecode((Element)node, this, unknownElements);
				if (entry != null){
					if (entry.getContentKind() == SimpleClasspathEntry.K_OUTPUT) {
						defaultOutput = entry; // separate output
					} else {
						paths.add(entry);
					}
				}
			}
		}
		int pathSize = paths.size();
		IClasspathEntry[][] entries = new IClasspathEntry[2][];
		entries[0] = new IClasspathEntry[pathSize + (defaultOutput == null ? 0 : 1)];
		paths.toArray(entries[0]);
		if (defaultOutput != null) entries[0][pathSize] = defaultOutput; // ensure output is last item
		
		paths.clear();
		list = cpElement.getElementsByTagName(SimpleClasspathEntry.TAG_REFERENCED_ENTRY);
		length = list.getLength();

		for (int i = 0; i < length; ++i) {
			Node node = list.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				IClasspathEntry entry = SimpleClasspathEntry.elementDecode((Element)node, this, unknownElements);
				if (entry != null){
					paths.add(entry);
				}
			}
		}
		entries[1] = new IClasspathEntry[paths.size()];
		paths.toArray(entries[1]);

		return entries;
	}

	public IClasspathEntry decodeClasspathEntry(String encodedEntry) {

		try {
			if (encodedEntry == null) return null;
			StringReader reader = new StringReader(encodedEntry);
			Element node;

			try {
				DocumentBuilder parser =
					DocumentBuilderFactory.newInstance().newDocumentBuilder();
				node = parser.parse(new InputSource(reader)).getDocumentElement();
			} catch (SAXException | ParserConfigurationException e) {
				return null;
			} finally {
				reader.close();
			}

			if (!node.getNodeName().equalsIgnoreCase(SimpleClasspathEntry.TAG_CLASSPATHENTRY)
					|| node.getNodeType() != Node.ELEMENT_NODE) {
				return null;
			}
			return SimpleClasspathEntry.elementDecode(node, this, null/*not interested in unknown elements*/);
		} catch (IOException e) {
			// bad format
			return null;
		}
	}
	
	public IPath findMember(IPath childPath) {
		return findMember(childPath, false);
	}

	public IPath findMember(IPath childPath, boolean phantom) {
		//path = SimpleWorkspace.getProject().getFullPath().append(path.removeFirstSegments(0));
		//childPath = getFullPath().append(childPath);
		return childPath == null ? null : childPath;
	}
	
	/**
	 * Returns a canonicalized path from the given external path.
	 * Note that the return path contains the same number of segments
	 * and it contains a device only if the given path contained one.
	 * @param externalPath IPath
	 * @see java.io.File for the definition of a canonicalized path
	 * @return IPath
	 */
	public IPath canonicalizedPath(IPath externalPath) {

		if (externalPath == null)
			return null;

		if (IS_CASE_SENSITIVE) {
			return externalPath;
		}
		
		// if not external path, return original path
		IPath workspaceRoot = SimpleWorkspace.getRoot();
		if (workspaceRoot == null) return externalPath; // protection during shutdown (30487)
		if (findMember(externalPath) != null) {
			return externalPath;
		}
		IPath canonicalPath = null;
		try {
			canonicalPath =
				new Path(new File(externalPath.toOSString()).getCanonicalPath());
		} catch (IOException e) {
			// default to original path
			return externalPath;
		}

		IPath result;
		int canonicalLength = canonicalPath.segmentCount();
		if (canonicalLength == 0) {
			// the java.io.File canonicalization failed
			return externalPath;
		} else if (externalPath.isAbsolute()) {
			result = canonicalPath;
		} else {
			// if path is relative, remove the first segments that were added by the java.io.File canonicalization
			// e.g. 'lib/classes.zip' was converted to 'd:/myfolder/lib/classes.zip'
			int externalLength = externalPath.segmentCount();
			if (canonicalLength >= externalLength) {
				result = canonicalPath.removeFirstSegments(canonicalLength - externalLength);
			} else {
				return externalPath;
			}
		}

		// keep device only if it was specified (this is because File.getCanonicalPath() converts '/lib/classes.zip' to 'd:/lib/classes/zip')
		if (externalPath.getDevice() == null) {
			result = result.setDevice(null);
		}
		// keep trailing separator only if it was specified (this is because File.getCanonicalPath() converts 'd:/lib/classes/' to 'd:/lib/classes')
		if (externalPath.hasTrailingSeparator()) {
			result = result.addTrailingSeparator();
		}
		return result;
	}
	
	private boolean hasUTF8BOM(byte[] bytes) {
		if (bytes.length > IContentDescription.BOM_UTF_8.length) {
			for (int i = 0, length = IContentDescription.BOM_UTF_8.length; i < length; i++) {
				if (IContentDescription.BOM_UTF_8[i] != bytes[i])
					return false;
			}
			return true;
		}
		return false;
	}
}
