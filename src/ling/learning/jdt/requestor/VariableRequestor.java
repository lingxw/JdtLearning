package ling.learning.jdt.requestor;

import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FileASTRequestor;
import org.eclipse.jdt.internal.compiler.batch.FileSystem.Classpath;

import ling.learning.jdt.astvisitor.TypeResoveVisitor;

public class VariableRequestor extends FileASTRequestor {

	private ASTVisitor visitor;
	private List<Classpath> classpaths;
	
	public VariableRequestor(ASTVisitor visitor, List<Classpath> classpaths) {
		this.visitor = visitor;
		this.classpaths = classpaths;
		
		if(this.visitor instanceof TypeResoveVisitor) {
			((TypeResoveVisitor)this.visitor).setClasspaths(classpaths);
		}
	}
	
	@Override
	public void acceptAST(String sourceFilePath, CompilationUnit ast) {
		
		System.out.println("-------------------------------------");
		String line = String.format(
				"CompilationUnit[%s+%s]:%s"
				, ast.getStartPosition()
				, ast.getLength()
				, sourceFilePath
				);
		System.out.println(line);

		ast.accept(this.visitor);
	}
}
