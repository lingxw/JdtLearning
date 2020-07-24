package ling.learning.jdt.requestor;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FileASTRequestor;

public class VariableRequestor extends FileASTRequestor {

	private ASTVisitor visitor;
	public VariableRequestor(ASTVisitor visitor) {
		this.visitor = visitor;
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
