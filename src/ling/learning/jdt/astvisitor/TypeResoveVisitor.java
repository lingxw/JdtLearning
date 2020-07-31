package ling.learning.jdt.astvisitor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.internal.compiler.env.NameEnvironmentAnswer;

import ling.learning.jdt.jar.resolver.StrongClassFileReader;
import ling.learning.jdt.jar.resolver.StrongCompilationUnit;
import ling.learning.jdt.jar.resolver.StrongFileSystem;

public class TypeResoveVisitor extends ASTVisitor {
	
	private StrongFileSystem fileSystem = null;
	
	public void setFileSystem(StrongFileSystem fileSystem) {
		this.fileSystem = fileSystem;
	}
	
	public NameEnvironmentAnswer findType(ITypeBinding binding) {
		if(fileSystem != null) {
			char[] typeName = null;
			String qualifiedTypeName = binding.getBinaryName();
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
			return fileSystem.findType(typeName, packages);
		}
		return null;
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
	
	String[] getTypePath(ITypeBinding typebinding) {
		String[] result = new String[] {null, null};
		String line = "";
		if(typebinding != null) {
			//if parser by Java model, the following code can get type's jar and class.
			//but if paser by ProjectParser.java, because Java Element is null, it cann't get type's jar and class.
			IJavaElement element = typebinding.getJavaElement();
			if(element != null) {
				result[0] = element.getPath().toString();
				element = element.getParent();
				if(element != null) {
					result[1] = element.getElementName();
				}
			} else {
				if(typebinding.isFromSource()) {
					String key = typebinding.getKey();
					int nFind = key.indexOf('<');
					if (nFind != -1) {
						key = key.substring(1,nFind);
					} else {
						//key = key.substring(1, key.length() - 1);
					}
					nFind = key.indexOf(fileSystem.getEnvironment().getProjectPath());
					if(nFind != -1) {
						//String fileName = key.substring(key.indexOf('~') + 1);
						//String filePath = key.substring(0, nFind);
						//key = filePath + fileName + ".java";
						result[0] = fileSystem.getEnvironment().getProjectJar();
						//result[1] = key;
						result[1] = typebinding.getBinaryName().replace('.', '/') + ".java";
						return result;
					}
				}
				
				NameEnvironmentAnswer answer = findType(typebinding);
				
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
		String[] paths = getTypePath(typebinding);
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
