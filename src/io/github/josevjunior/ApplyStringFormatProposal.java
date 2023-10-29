package io.github.josevjunior;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.internal.ui.text.correction.proposals.LinkedCorrectionProposal;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;

public class ApplyStringFormatProposal extends LinkedCorrectionProposal {

	private static final String STRING_CLASS_NAME = String.class.getSimpleName();
	private static final String FORMAT_METHOD_NAME = "format";
	
	private final StringLiteral expression;
	
	public ApplyStringFormatProposal(ICompilationUnit cu, StringLiteral expression) {
		super(Constants.QUICK_FIX_APPLY_FORMAT_MESSAGE, cu, null, 9999, null);
		this.expression = expression;
	}
	
	@Override
	protected ASTRewrite getRewrite() throws CoreException {
		
		AST ast = expression.getAST();
		
		StringLiteral formattedString = ast.newStringLiteral();
		formattedString.setEscapedValue(expression.getEscapedValue());
		
		// String.format arguments
		List<Expression> arguments = new ArrayList<Expression>();
		arguments.add(formattedString);
		
		Matcher matcher = FormatUtils.matcher(expression.getEscapedValue());
		while(matcher.find()) {
		
			StringLiteral emptyArgument = ast.newStringLiteral();			
			arguments.add(emptyArgument);
		}		
				
		MethodInvocation formatMethodInvocation = ast.newMethodInvocation();
		formatMethodInvocation.setExpression(ast.newName(STRING_CLASS_NAME));
		formatMethodInvocation.setName(ast.newSimpleName(FORMAT_METHOD_NAME));
		formatMethodInvocation.arguments().addAll(arguments);		
		
		ASTRewrite rewrite = ASTRewrite.create(ast);
		rewrite.replace(expression, formatMethodInvocation, null);		
		
		for (int i = 1; i < arguments.size(); i++) {
			Expression expression = arguments.get(i);
			boolean isFirstLink = (i == 1);
			addLinkedPosition(rewrite.track(expression), isFirstLink, String.valueOf(i));						
		}	
			
		return rewrite;
	}

	protected void addEdit1(IDocument document, TextEdit editRoot) throws CoreException {
		
		//try {
		
			String originalText = expression.getEscapedValue();
		
			StringBuilder formatExp = new StringBuilder("String.format(");
			formatExp.append(originalText);
			
			Matcher matcher = FormatUtils.matcher(originalText);
			while(matcher.find()) {
				formatExp.append(", \"\"");
			}
			formatExp.append(")");
			
			int nodeOffset = expression.getStartPosition();
			
			editRoot.addChild(new ReplaceEdit(nodeOffset, originalText.length(), formatExp.toString()));
			
		/*}catch (BadLocationException e) {
			throw new CoreException(JavaUIStatus.createError(IStatus.ERROR, e));
		}*/
		
				
	}
	
}
