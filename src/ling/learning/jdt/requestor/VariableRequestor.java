package ling.learning.jdt.requestor;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FileASTRequestor;

import ling.learning.jdt.astvisitor.TypeResoveVisitor;
import ling.learning.jdt.jar.resolver.StrongFileSystem;

public class VariableRequestor extends FileASTRequestor {

	private ASTVisitor visitor;
	private StrongFileSystem fileSystem;
	
	public VariableRequestor(ASTVisitor visitor, StrongFileSystem fileSystem) {
		this.visitor = visitor;
		this.fileSystem = fileSystem;
		
		if(this.visitor instanceof TypeResoveVisitor) {
			((TypeResoveVisitor)this.visitor).setFileSystem(fileSystem);
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
