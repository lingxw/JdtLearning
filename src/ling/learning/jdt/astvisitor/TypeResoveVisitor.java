package ling.learning.jdt.astvisitor;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;

public class TypeResoveVisitor extends ASTVisitor {
	
	public boolean visit(SimpleType node){
		String line = "";

		if(node.getRoot() instanceof CompilationUnit) {
			CompilationUnit cu = (CompilationUnit)node.getRoot();
			line = String.format(
					"%s[%s+%s] (%s,%s)"
					, node.getClass().getSimpleName()
					, node.getStartPosition()
					, node.getLength()
					, cu.getLineNumber(node.getStartPosition())
					, cu.getColumnNumber(node.getStartPosition())
					);
			System.out.println(line);
		} else {
			line = String.format(
					"%s[%s+%s] (?,?)"
					, node.getClass().getSimpleName()
					, node.getStartPosition()
					, node.getLength()
					);
			System.out.println(line);
		}
		
		ITypeBinding binding = node.resolveBinding();
		if (binding != null) {
			line = String.format(
					" > type binding name:%s"
					, binding.getName()
					);
			System.out.println(line);
			line = String.format(
					" > type binding qualified name:%s"
					, binding.getQualifiedName()
					);
			System.out.println(line);
			line = String.format(
					" > type binding binary name:%s"
					, binding.getBinaryName()
					);
			System.out.println(line);
			line = String.format(
					" > type binding key:%s"
					, binding.getKey()
					);
			System.out.println(line);
		}

		return true;
	}
	
	public boolean visit(SimpleName node){
		String line = "";

		if(node.getRoot() instanceof CompilationUnit) {
			CompilationUnit cu = (CompilationUnit)node.getRoot();
			line = String.format(
					"%s[%s+%s] (%s,%s)"
					, node.getClass().getSimpleName()
					, node.getStartPosition()
					, node.getLength()
					, cu.getLineNumber(node.getStartPosition())
					, cu.getColumnNumber(node.getStartPosition())
					);
			System.out.println(line);
		} else {
			line = String.format(
					"%s[%s+%s] (?,?)"
					, node.getClass().getSimpleName()
					, node.getStartPosition()
					, node.getLength()
					);
			System.out.println(line);
		}
		
		IBinding binding = node.resolveBinding();
		if (binding != null && binding.getKind() == IBinding.VARIABLE) {
			line = String.format(
					" > variable binding name:%s"
					, binding.getName()
					);
			System.out.println(line);
			line = String.format(
					" > variable binding key:%s"
					, binding.getKey()
					);
			System.out.println(line);
		}

		return true;
	}
	
	public boolean visit(MethodInvocation node){
		String line = "";

		if(node.getRoot() instanceof CompilationUnit) {
			CompilationUnit cu = (CompilationUnit)node.getRoot();
			line = String.format(
					"%s[%s+%s] (%s,%s)"
					, node.getClass().getSimpleName()
					, node.getStartPosition()
					, node.getLength()
					, cu.getLineNumber(node.getStartPosition())
					, cu.getColumnNumber(node.getStartPosition())
					);
			System.out.println(line);
		} else {
			line = String.format(
					"%s[%s+%s] (?,?)"
					, node.getClass().getSimpleName()
					, node.getStartPosition()
					, node.getLength()
					);
			System.out.println(line);
		}
		
		IMethodBinding binding = node.resolveMethodBinding();
		if (binding != null) {
			line = String.format(
					" > method binding name:%s"
					, binding.getName()
					);
			System.out.println(line);
			line = String.format(
					" > method binding key:%s"
					, binding.getKey()
					);
			System.out.println(line);
			line = String.format(
					" > method binding is recovered:%s"
					, binding.isRecovered()
					);
			System.out.println(line);
		}

		return true;
	}
}
