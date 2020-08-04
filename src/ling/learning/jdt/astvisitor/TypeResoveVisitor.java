package ling.learning.jdt.astvisitor;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import ling.learning.jdt.jar.resolver.StrongFileSystem;

public class TypeResoveVisitor extends ASTVisitor {
	
	private StrongFileSystem fileSystem = null;
	
	public void setFileSystem(StrongFileSystem fileSystem) {
		this.fileSystem = fileSystem;
	}

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
			print(binding, "type binding");
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
			IVariableBinding varBinding =  (IVariableBinding)binding;
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
			
			print(varBinding.getDeclaringClass(), "variable binding -> DeclaringClass");
			print(varBinding.getType(), "variable binding -> Type");
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
			ITypeBinding typebinding = binding.getDeclaringClass();
			print(typebinding, "method binding -> DeclaringClass");
			for(ITypeBinding type : binding.getParameterTypes()) {
				print(type, "method binding -> ParameterType");
			}
			typebinding = binding.getReturnType();
			print(typebinding, "method binding -> ReturnType");
		}

		return true;
	}
	
	void print(ITypeBinding typebinding, String head) {
		if(typebinding == null)
			return;
		String line = String.format(
				" > %s name:%s"
				, head
				, typebinding.getName()
				);
		System.out.println(line);
		line = String.format(
				" > %s qualified name:%s"
				, head
				, typebinding.getQualifiedName()
				);
		System.out.println(line);
		line = String.format(
				" > %s binary name:%s"
				, head
				, typebinding.getBinaryName()
				);
		System.out.println(line);
		line = String.format(
				" > %s key:%s"
				, head
				, typebinding.getKey()
				);
		System.out.println(line);
		if (fileSystem != null) {
			String[] paths = fileSystem.getTypePath(typebinding);
			if(paths[0] != null) {
				line = String.format(
						" > %s jar path:%s"
						, head
						, paths[0]
						);
				System.out.println(line);
			}
			if(paths[1] != null) {
				line = String.format(
						" > %s file name:%s"
						, head
						, paths[1]
						);
				System.out.println(line);
			}
		}
	}
}
