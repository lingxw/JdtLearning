package ling.learning.jdt.astvisitor;

import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.IPackageBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

public class TypeFinderVisitor extends ASTVisitor {
	
	public boolean visit(PackageDeclaration node){
		String line = String.format(
				"PackageDeclaration[%s+%s]"
				, node.getStartPosition()
				, node.getLength()
				);
		System.out.println(line);
		
		line = String.format(
				" > package name:%s"
				, node.getName()
				);
		System.out.println(line);
		
		IPackageBinding binding = node.resolveBinding();
		line = String.format(
				" > package binding:%s"
				, binding.getName()
				);
		System.out.println(line);

		return true;
	}
	
	public boolean visit(ImportDeclaration node){
		String line = String.format(
				"ImportDeclaration[%s+%s]"
				, node.getStartPosition()
				, node.getLength()
				);
		System.out.println(line);
		
		line = String.format(
				" > import name:%s"
				, node.getName()
				);
		System.out.println(line);
		
		IBinding binding = node.resolveBinding();
		
		if(binding.getKind() == IBinding.TYPE)
		{
			ITypeBinding typeBinding = (ITypeBinding)binding;
			line = String.format(
					" > type binding:%s"
					, typeBinding.getQualifiedName()
					);
			System.out.println(line);
		}

		return true;
	}
	
	public boolean visit(TypeDeclaration node){
		String line = String.format(
				"TypeDeclaration[%s+%s]"
				, node.getStartPosition()
				, node.getLength()
				);
		System.out.println(line);
		
		line = String.format(
				" > type name:%s"
				, node.getName()
				);
		System.out.println(line);
		
		ITypeBinding typeBinding = node.resolveBinding();
		line = String.format(
				" > type binding:%s"
				, typeBinding.getQualifiedName()
				);
		System.out.println(line);
		
		line = String.format(
				" > is class:%s"
				, typeBinding.isClass()
				);
		System.out.println(line);
		
		if(typeBinding.getSuperclass() != null) {
			line = String.format(
					" > super class type:%s"
					, typeBinding.getSuperclass().getQualifiedName()
					);
			System.out.println(line);
			line = String.format(
					" > super class key:%s"
					, typeBinding.getSuperclass().getKey()
					);
			System.out.println(line);
		}

		return true;
	}
	
	public boolean visit(MethodDeclaration node){
		String line = String.format(
				"   MethodDeclaration[%s+%s]"
				, node.getStartPosition()
				, node.getLength()
				);
		System.out.println(line);
		
		line = String.format(
				"    > method name:%s"
				, node.getName()
				);
		System.out.println(line);
		
		line = String.format(
				"    > method modifiers:%s"
				, node.modifiers()
				);
		System.out.println(line);
		
		line = String.format(
				"    > method parameters:%s"
				, node.parameters()
				);
		System.out.println(line);
		
		line = String.format(
				"    > method return type:%s"
				, node.getReturnType2()
				);
		System.out.println(line);
		
		IMethodBinding methodBinding = node.resolveBinding();
		line = String.format(
				"    > method binding key:%s"
				, methodBinding.getKey()
				);
		System.out.println(line);
		
		line = String.format(
				"    > method binding return key:%s"
				, methodBinding.getReturnType().getKey()
				);
		System.out.println(line);

		return true;
	}
	 
	public boolean visit(VariableDeclarationStatement node){
		String line = String.format(
				"      VariableDeclarationStatement[%s+%s]"
				, node.getStartPosition()
				, node.getLength()
				);
		System.out.println(line);
		
		line = String.format(
				"       > variable modifiers:%s"
				, node.modifiers()
				);
		System.out.println(line);
		
		line = String.format(
				"       > variable type:%s"
				, node.getType().resolveBinding().getKey()
				);
		System.out.println(line);
		
		List<VariableDeclarationFragment> list = node.fragments();
		for(VariableDeclarationFragment fragment : list) {
			line = String.format(
					"       > variable name:%s"
					, fragment.getName().getFullyQualifiedName()
					);
			System.out.println(line);
			line = String.format(
					"       > variable init:%s"
					, fragment.getInitializer()
					);
			System.out.println(line);
		}

		return true;
	}
}