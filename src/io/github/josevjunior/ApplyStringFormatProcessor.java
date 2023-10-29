package io.github.josevjunior;

import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.ui.text.java.IInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jdt.ui.text.java.IProblemLocation;
import org.eclipse.jdt.ui.text.java.IQuickAssistProcessor;

public class ApplyStringFormatProcessor implements IQuickAssistProcessor{

	@Override
	public IJavaCompletionProposal[] getAssists(IInvocationContext ctx, IProblemLocation[] locations) throws CoreException {
		 
		ASTNode coveredNode = ctx.getCoveringNode();
		if(coveredNode == null) {
			return null;
		}
		
		StringLiteral stringLiteral = extractStringLiteral(coveredNode);
		if(stringLiteral == null) {
			return null;
		}
		
		boolean matches = FormatUtils.matches(stringLiteral.getLiteralValue());
		if(!matches) {
			return null;
		}
		
		return new IJavaCompletionProposal[] {
			new ApplyStringFormatProposal(ctx.getCompilationUnit(), stringLiteral)
		};
	}

	@Override
	public boolean hasAssists(IInvocationContext ctx) throws CoreException {
		
		ASTNode coveredNode = ctx.getCoveringNode();
		StringLiteral stringLiteral = extractStringLiteral(coveredNode);
		if(stringLiteral == null) {
			return false;
		}
		
		return FormatUtils.matches(stringLiteral.getLiteralValue());
	}
	
	private StringLiteral extractStringLiteral(ASTNode node) {
		if(node instanceof  StringLiteral) {
			return ((StringLiteral) node);
		}
		
		if(node instanceof VariableDeclaration) {
			
			if(((VariableDeclaration) node).getInitializer() instanceof StringLiteral) {
				return (StringLiteral)((VariableDeclaration) node).getInitializer();
			}			
		}
		
		if(node instanceof MethodInvocation) {
			List arguments = ((MethodInvocation) node).arguments();
			for (Iterator iterator = arguments.iterator(); iterator.hasNext();) {
				
				Object next = iterator.next();
				if(next instanceof StringLiteral) {
					return (StringLiteral) next;
				}				
			}
		}
		
		return null;
	}

}
