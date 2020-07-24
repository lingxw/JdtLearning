package ling.learning.jdt.astvisitor;

import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ChildListPropertyDescriptor;
import org.eclipse.jdt.core.dom.ChildPropertyDescriptor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Dimension;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.SimplePropertyDescriptor;
import org.eclipse.jdt.core.dom.StructuralPropertyDescriptor;
import org.eclipse.jdt.core.dom.Type;

public class PropertyVisitor extends ASTVisitor {

	public boolean preVisit2(ASTNode node) {
		
		if(node instanceof Name) {
			return true;
		}
		
		if(node instanceof Modifier) {
			return true;
		}
		
		if(node instanceof Type) {
			return true;
		}
		
		if(node instanceof Expression) {
			return true;
		}
		
		if(node instanceof Dimension) {
			return true;
		}
		
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
		
		List<StructuralPropertyDescriptor> list = node.structuralPropertiesForType();
		for(StructuralPropertyDescriptor prop:list) {		
			if (prop.isSimpleProperty()) {
				SimplePropertyDescriptor simple = (SimplePropertyDescriptor)prop;
				line = String.format(
						" > %s(%s):%s"
						, prop.getId()
						, simple.getValueType().getSimpleName()
						, node.getStructuralProperty(prop)
						);
				System.out.println(line);
			} else if (prop.isChildProperty()) {
				ChildPropertyDescriptor child = (ChildPropertyDescriptor)prop;
				ASTNode childNode = (ASTNode)node.getStructuralProperty(prop);
				
				if(childNode == null) {
					continue;
				}
				
				String text = childNode == null ? "" : childNode.toString().trim();
				
				if(text.indexOf("\r") == -1 && text.indexOf("\n") == -1) {
					line = String.format(
							" > %s(%s):%s"
							, prop.getId()
							, child.getChildType().getSimpleName()
							, text
							);
					System.out.println(line);
				} else {
					line = String.format(
							" > %s(%s):%s"
							, prop.getId()
							, child.getChildType().getSimpleName()
							, text.replace("\r", "\\r").replace("\n", "\\n").substring(0,64) + "..."
							);
					System.out.println(line);
				}
			} else if (prop.isChildListProperty()) {
				ChildListPropertyDescriptor childList = (ChildListPropertyDescriptor)prop;
				List<ASTNode> nodeList = (List<ASTNode>) node.getStructuralProperty(prop);
				String text = "";
				
				for(ASTNode childNode: nodeList) {
					if(childNode == null) {
						continue;
					}
					text = childNode == null ? "" : childNode.toString().trim();
					if(text.indexOf("\r") == -1 && text.indexOf("\n") == -1) {
						line = String.format(
								" > %s(%s):%s"
								, prop.getId()
								, childList.getElementType().getSimpleName()
								, text
								);
						System.out.println(line);
					} else {
						line = String.format(
								" > %s(%s):%s"
								, prop.getId()
								, childList.getElementType().getSimpleName()
								, text.replace("\r", "\\r").replace("\n", "\\n").substring(0,64) + "..."
								);
						System.out.println(line);
					}
				}
			}
		}
		return true;
	}
}
