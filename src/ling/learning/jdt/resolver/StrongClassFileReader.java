/*******************************************************************************
 * Copyright (c) 2000, 2019 IBM Corporation and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Stephan Herrmann - Contribution for
 *								Bug 365992 - [builder] [null] Change of nullness for a parameter doesn't trigger a build for the files that call the method
 *								Bug 440477 - [null] Infrastructure for feeding external annotations into compilation
 *								Bug 440687 - [compiler][batch][null] improve command line option for external annotations
 *     Andy Clement (GoPivotal, Inc) aclement@gopivotal.com - Contributions for
 *         						bug 407191 - [1.8] Binary access support for type annotations
 *******************************************************************************/
package ling.learning.jdt.resolver;

import org.eclipse.jdt.internal.compiler.classfmt.ClassFileReader;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFormatException;
import org.eclipse.jdt.internal.compiler.env.IBinaryAnnotation;
import org.eclipse.jdt.internal.compiler.env.IBinaryField;
import org.eclipse.jdt.internal.compiler.env.IBinaryMethod;
import org.eclipse.jdt.internal.compiler.env.IBinaryNestedType;
import org.eclipse.jdt.internal.compiler.env.IBinaryType;
import org.eclipse.jdt.internal.compiler.env.IBinaryTypeAnnotation;
import org.eclipse.jdt.internal.compiler.env.ITypeAnnotationWalker;
import org.eclipse.jdt.internal.compiler.lookup.BinaryTypeBinding.ExternalAnnotationStatus;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;

public class StrongClassFileReader implements IBinaryType {
	
	private java.util.zip.ZipFile zip;
	private ClassFileReader binding;
	
	public StrongClassFileReader(java.util.zip.ZipFile zip, ClassFileReader binding) throws ClassFormatException {
		this.zip = zip;
		this.binding = binding;
	}

	public java.util.zip.ZipFile getZip() {
		return zip;
	}
	
	public ClassFileReader getBinding() {
		return binding;
	}
	
	public static StrongClassFileReader read(
		java.util.zip.ZipFile zip,
		String filename)
		throws ClassFormatException, java.io.IOException {
			return read(zip, filename, false);
	}

	public static StrongClassFileReader read(
		java.util.zip.ZipFile zip,
		String filename,
		boolean fullyInitialize)
		throws ClassFormatException, java.io.IOException {
		ClassFileReader classFileReader = ClassFileReader.read(zip, filename, fullyInitialize);
		if (classFileReader != null) {
			return new StrongClassFileReader(zip, classFileReader);
		}
		return null;
	}

	@Override
	public int getModifiers() {
		return binding.getModifiers();
	}

	@Override
	public boolean isBinaryType() {
		return binding.isBinaryType();
	}

	@Override
	public char[] getFileName() {
		return binding.getFileName();
	}

	@Override
	public IBinaryAnnotation[] getAnnotations() {
		return binding.getAnnotations();
	}

	@Override
	public IBinaryTypeAnnotation[] getTypeAnnotations() {
		return binding.getTypeAnnotations();
	}

	@Override
	public char[] getEnclosingMethod() {
		return binding.getEnclosingMethod();
	}

	@Override
	public char[] getEnclosingTypeName() {
		return binding.getEnclosingTypeName();
	}

	@Override
	public IBinaryField[] getFields() {
		return binding.getFields();
	}

	@Override
	public char[] getModule() {
		return binding.getModule();
	}

	@Override
	public char[] getGenericSignature() {
		return binding.getGenericSignature();
	}

	@Override
	public char[][] getInterfaceNames() {
		return binding.getInterfaceNames();
	}

	@Override
	public IBinaryNestedType[] getMemberTypes() {
		return binding.getMemberTypes();
	}

	@Override
	public IBinaryMethod[] getMethods() {
		return binding.getMethods();
	}

	@Override
	public char[][][] getMissingTypeNames() {
		return binding.getMissingTypeNames();
	}

	@Override
	public char[] getName() {
		return binding.getName();
	}

	@Override
	public char[] getSourceName() {
		return binding.getSourceName();
	}

	@Override
	public char[] getSuperclassName() {
		return binding.getSuperclassName();
	}

	@Override
	public long getTagBits() {
		return binding.getTagBits();
	}

	@Override
	public boolean isAnonymous() {
		return binding.isAnonymous();
	}

	@Override
	public boolean isLocal() {
		return binding.isLocal();
	}

	@Override
	public boolean isMember() {
		return binding.isMember();
	}

	@Override
	public char[] sourceFileName() {
		return binding.sourceFileName();
	}

	@Override
	public ITypeAnnotationWalker enrichWithExternalAnnotationsFor(ITypeAnnotationWalker walker, Object member,
			LookupEnvironment environment) {
		return binding.enrichWithExternalAnnotationsFor(walker, member, environment);
	}

	@Override
	public ExternalAnnotationStatus getExternalAnnotationStatus() {
		return binding.getExternalAnnotationStatus();
	}
}
