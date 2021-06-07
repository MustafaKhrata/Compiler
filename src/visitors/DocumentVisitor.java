package visitors;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import SymbolTable.Scope;
import SymbolTable.Symbol;
import misc.HTMLParser;
import misc.HTMLParser.ArrayLoopRawContext;
import misc.HTMLParser.AttributeNodeContext;
import misc.HTMLParser.BodyContext;
import misc.HTMLParser.CommentContext;
import misc.HTMLParser.CommentNodeContext;
import misc.HTMLParser.ConditionalCommentContext;
import misc.HTMLParser.DefaultDirectiveContext;
import misc.HTMLParser.DocumentContext;
import misc.HTMLParser.ElementAttributesContext;
import misc.HTMLParser.ElementContentContext;
import misc.HTMLParser.ExpDirectiveContext;
import misc.HTMLParser.FilterExpressionContext;
import misc.HTMLParser.HalfElementContext;
import misc.HTMLParser.HeaderContext;
import misc.HTMLParser.IndexedArrayLoopContext;
import misc.HTMLParser.LoopStatementContext;
import misc.HTMLParser.MustacheContext;
import misc.HTMLParser.NodeContext;
import misc.HTMLParser.NormalElementContext;
import misc.HTMLParser.ParametrizedFilterContext;
import misc.HTMLParser.RawFilterContext;
import misc.HTMLParser.RawObjectLoopContext;
import misc.HTMLParser.ScriptContext;
import misc.HTMLParser.ScriptletOrSeaWsContext;
import misc.HTMLParser.SelfClosedElementContext;
import misc.HTMLParser.StmtDirectiveContext;
import misc.HTMLParser.StyleContext;
import misc.HTMLParser.TextNodeContext;
import models.*;
import models.directive.*;
import models.documents.*;
import models.enums.CommentType;
import models.expression.Expression;
import models.expression.ValueExpression;
import models.nodes.*;
import models.statements.*;

import javax.naming.event.EventContext;

public class DocumentVisitor extends Visitor<AbstractASTNode>{ 
	
	protected static Stack<Boolean> switchExists;
	
	public DocumentVisitor() {
		if (switchExists == null)
			switchExists = new Stack<Boolean>();
	}


	@Override
	public AbstractASTNode visitScript(ScriptContext ctx) {
		return new ScriptNode(ctx.getText());
	}

	@Override
	public AbstractASTNode visitStyle(StyleContext ctx) {
		return new StyleNode(ctx.getText());
	}

	@Override
	public AbstractASTNode visitDocument(DocumentContext ctx) {
		DocumentHeader header = (DocumentHeader) visit(ctx.getChild(0));
		DocumentBody body = (DocumentBody) visit(ctx.getChild(1));
		return new Document(header, body);
	}

	@Override
	public AbstractASTNode visitHeader(HeaderContext ctx) {
		List<DocumentNode> headers = new ArrayList<DocumentNode>();
		DocumentHeader header = new DocumentHeader(headers);
		for (int index = 0; index < ctx.getChildCount(); index++) {
			if (ctx.getChild(index) instanceof CommentNodeContext)
				headers.add((DocumentNode) visit(ctx.getChild(index)));
			else
				headers.add(new TextNode(ctx.getChild(index).getText()));
		}
		return header;
	}

	@Override
	public AbstractASTNode visitComment(CommentContext ctx) {
		return new CommentNode(ctx.HTML_COMMENT().getText(), CommentType.Normal);
	}

	@Override
	public AbstractASTNode visitConditionalComment(ConditionalCommentContext ctx) {
		return new CommentNode(ctx.HTML_CONDITIONAL_COMMENT().getText(), CommentType.Conditional);
	}

	@Override
	public AbstractASTNode visitBody(BodyContext ctx) {
		Scope scopeGlobal = new Scope();
		scopeGlobal.setId("GlobalScope");
		scopeGlobal.setisParent(true);
		Visitor.symbolTable.addScope(scopeGlobal);

		List<DocumentNode> contents = new ArrayList<DocumentNode>();
		DocumentBody body = new DocumentBody(contents);
		body.setScope(scopeGlobal);

		for (int index = 0; index < ctx.getChildCount(); index++)
			contents.add((DocumentNode) visit(ctx.getChild(index)));

		return body;
	}

	@Override
	public AbstractASTNode visitExpDirective(ExpDirectiveContext ctx) {

		AbstractASTNode value = expressionVisitor.visit(ctx.getChild(3));

		if(ctx.getChild(0).getText().equals("cp-if")){
			Scope scopeIf = new Scope();
			scopeIf.setId(value.createId());
			scopeIf.setisParent(true);
			scopeIf.setmyParent(Visitor.symbolTable.getScopes().get(Visitor.symbolTable.getScopes().size() -1));
			Visitor.symbolTable.addScope(scopeIf);

			Directive directive =  new Directive(ctx.getChild(0).getText()  , scopeIf,value);
			return directive;
		}

		if(ctx.getChild(0).getText().equals("cp-switch-case")){
			Scope scopeSwitchCase = new Scope();
			scopeSwitchCase.setId(value.createId());
			scopeSwitchCase.setisParent(true);
			scopeSwitchCase.setmyParent(Visitor.symbolTable.getScopes().get(Visitor.symbolTable.getScopes().size() -1));
			Visitor.symbolTable.addScope(scopeSwitchCase);

			Directive directive =  new Directive(ctx.getChild(0).getText()  , scopeSwitchCase,value);
			return directive;
		}




		return new Directive(ctx.getChild(0).getText()  ,null, value);
	}

	@Override
	public AbstractASTNode visitStmtDirective(StmtDirectiveContext ctx) {

		if(ctx.getChild(3).getChild(0) instanceof HTMLParser.ArrayLoopContext) {
			ArrayLoopStatement arrayLoopStatement = (ArrayLoopStatement) visit(ctx.getChild(3).getChild(0));
			Scope  scope =arrayLoopStatement.symbols.get(0).getScope();
			return new Directive(ctx.getChild(0).getText() , scope,arrayLoopStatement);
		}
		else{
			ObjectLoopStatement objectLoopStatement = (ObjectLoopStatement) visit(ctx.getChild(3).getChild(0));
			Scope  scope =objectLoopStatement.symbols.get(0).getScope();
			return new Directive(ctx.getChild(0).getText()  , scope,objectLoopStatement);
		}




	}

	@Override
	public AbstractASTNode visitLoopStatement(LoopStatementContext ctx) {

//		if(ctx.getChild(0) instanceof ArrayLoopRawContext) {
//			AbstractASTNode arrayLoopStatement = (ArrayLoopStatement) visit(ctx.getChild(0));
//			return arrayLoopStatement ;
//		}else {
//			AbstractASTNode objectLoopStatement = (ObjectLoopStatement) visit(ctx.getChild(0));
//			return  objectLoopStatement ;
//		}

					AbstractASTNode abstractASTNode =  visit(ctx.getChild(0));
					return abstractASTNode;


	}

	@Override
	public AbstractASTNode visitArrayLoopRaw(ArrayLoopRawContext ctx) {
		AbstractASTNode arrayToLoopOn = expressionVisitor.visit(ctx.getChild(2));

		Scope scopeFor = new Scope();
		scopeFor.setId(AbstractASTNode.createId());
		scopeFor.setisParent(true);
		scopeFor.setmyParent(Visitor.symbolTable.getScopes().get(Visitor.symbolTable.getScopes().size() -1));
		Visitor.symbolTable.addScope(scopeFor);

		String loopVariable = ctx.getChild(0).getText();
		Symbol symbol = new Symbol();
		symbol.setName(loopVariable);
		symbol.setScope(scopeFor);

		ArrayLoopStatement arrayLoopStatement = new ArrayLoopStatement(loopVariable,(ValueExpression) arrayToLoopOn);
		arrayLoopStatement.symbols.add(symbol);

		return arrayLoopStatement;
	}

	@Override
	public AbstractASTNode visitIndexedArrayLoop(IndexedArrayLoopContext ctx) {
		AbstractASTNode arrayToLoopOn = expressionVisitor.visit(ctx.getChild(2));

		Scope scopeFor = new Scope();
		scopeFor.setId(AbstractASTNode.createId());
		scopeFor.setisParent(true);
		scopeFor.setmyParent(Visitor.symbolTable.getScopes().get(Visitor.symbolTable.getScopes().size() -1));
		Visitor.symbolTable.addScope(scopeFor);

		String loopVariable = ctx.getChild(0).getText();
		Symbol symbolLoopVar = new Symbol();
		symbolLoopVar.setName(loopVariable);
		symbolLoopVar.setScope(scopeFor);

		String indexVariable = ctx.getChild(4).getText();
		Symbol symbolIndexVar = new Symbol();
		symbolIndexVar.setName(indexVariable);
		symbolIndexVar.setScope(scopeFor);

		ArrayLoopStatement arrayLoopStatement = new ArrayLoopStatement(loopVariable,indexVariable,(ValueExpression) arrayToLoopOn);
		arrayLoopStatement.symbols.add(symbolLoopVar);
		arrayLoopStatement.symbols.add(symbolIndexVar);

		return arrayLoopStatement;
	}

	@Override
	public AbstractASTNode visitDefaultDirective(DefaultDirectiveContext ctx) {

		Scope scopeDefaultCase = new Scope();
		scopeDefaultCase.setId(AbstractASTNode.createId());
		scopeDefaultCase.setisParent(true);
		scopeDefaultCase.setmyParent(Visitor.symbolTable.getScopes().get(Visitor.symbolTable.getScopes().size() -1));
		Visitor.symbolTable.addScope(scopeDefaultCase);

		return new Directive(ctx.getChild(0).getText());
	}

	@Override////////
	public AbstractASTNode visitScriptletOrSeaWs(ScriptletOrSeaWsContext ctx) {
		return new TextNode(ctx.getText());
	}

	@Override
	public AbstractASTNode visitRawObjectLoop(RawObjectLoopContext ctx) {
		AbstractASTNode objectToLoopOn = expressionVisitor.visit(ctx.getChild(4));
		Scope scopeFor = new Scope();
		scopeFor.setisParent(true);
		scopeFor.setId(AbstractASTNode.createId());
		Visitor.symbolTable.addScope(scopeFor);

		String KeyVariable = ctx.getChild(0).getText();
		Symbol symbolKeyVariable = new Symbol();
		symbolKeyVariable.setName(KeyVariable);
		symbolKeyVariable.setScope(scopeFor);

		String valueVariable = ctx.getChild(2).getText();
		Symbol symbolvalueVariable = new Symbol();
		symbolvalueVariable.setName(valueVariable);
		symbolvalueVariable.setScope(scopeFor);

		ObjectLoopStatement objectLoopStatement=new ObjectLoopStatement(KeyVariable, valueVariable, (ValueExpression) objectToLoopOn);
		objectLoopStatement.symbols.add(symbolKeyVariable)	;
		objectLoopStatement.symbols.add(symbolvalueVariable)	;

		return objectLoopStatement;
	}

	@Override/////////
	public AbstractASTNode visitFilterExpression(FilterExpressionContext ctx) {
		Expression oprand = expressionVisitor.visit(ctx.getChild(0));
		FilterStatement filter = (FilterStatement) visit(ctx.getChild(1));
		filter.setOprand(oprand);
		return filter;
	}

	@Override
	public AbstractASTNode visitRawFilter(RawFilterContext ctx) {
		AbstractASTNode filter = expressionVisitor.visit(ctx.getChild(1));
		return new FilterStatement((Expression) filter);
	}

	@Override
	public AbstractASTNode visitParametrizedFilter(ParametrizedFilterContext ctx) {
		AbstractASTNode filter = expressionVisitor.visit(ctx.getChild(1));
		List<Expression> parameters = new ArrayList<Expression>();
		for (int index = 0; index < ctx.getChild(3).getChildCount(); index += 2 ) {
			parameters.add(expressionVisitor.visit(ctx.getChild(3).getChild(index)));
		}
		return new FilterStatement((Expression) filter, parameters);
	}

	@Override
	public AbstractASTNode visitNode(NodeContext ctx) {
//		boolean oneHtml=false;
//		if(ctx.getChild(0) instanceof HTMLParser.ElementNodeContext && ctx.getChild(0).getChild(1).getText().equals("html") ){
//			oneHtml=true;
//		}
//		if (oneHtml){
//
//		}
//		else return null;

		return visit(ctx.getChild(0));

	}

	public List<AbstractASTNode> getContent(ElementContentContext ctx) {
		List<AbstractASTNode> contents = new ArrayList<AbstractASTNode>();
		for (int index = 0; index < ctx.getChildCount(); index++) {
			if (ctx.getChild(index) instanceof NodeContext || ctx.getChild(index) instanceof MustacheContext)
				contents.add(visit(ctx.getChild(index)));
			else 
				contents.add(new TextNode(ctx.getChild(index).getText()));
		}
		return contents;
	}

	@Override
	public AbstractASTNode visitHalfElement(HalfElementContext ctx) {
		String tagName = ctx.getChild(1).getText();
		List<AbstractASTNode> attributes = new ArrayList<AbstractASTNode>();
		
		if (ctx.getChild(2) instanceof ElementAttributesContext)
			attributes = getAttributes((ElementAttributesContext) ctx.getChild(2));
		
		ElementNode element = new ElementNode(tagName, attributes.toArray(new DocumentNode[attributes.size()]));
		return element;
	}

	@Override
	public AbstractASTNode visitNormalElement(NormalElementContext ctx) {
		String tagName = ctx.getChild(1).getText();
		String tagClose;

		System.out.println("here: "+tagName);

		if (ctx.getChild(2) instanceof ElementAttributesContext) {
			tagClose = ctx.getChild(6).getText();
		} else
			tagClose = ctx.getChild(5).getText();
		if (!testName(tagName, tagClose))
			System.err.println("name does not match!");
		List<AbstractASTNode> attributes = new ArrayList<AbstractASTNode>();
		List<AbstractASTNode> contents = new ArrayList<AbstractASTNode>();
		;

		if (ctx.getChild(2) instanceof ElementAttributesContext)
			attributes = getAttributes((ElementAttributesContext) ctx.getChild(2));

		boolean switchElement = false;
		for (AbstractASTNode node : attributes)
			if (node instanceof Directive && testName(((Directive) node).getName(), "cp-switch")) {
				switchExists.push(true);
				switchElement = true;
			}
		for (AbstractASTNode node : attributes)
			if (node instanceof Directive && (testName(((Directive) node).getName(), "cp-switch-case") || testName(((Directive) node).getName(), "cp-switchDefault")))
				if (switchExists.isEmpty())
					System.err.println("Invalid switch");
		if (ctx.getChild(3) instanceof ElementContentContext)
			contents = getContent((ElementContentContext) ctx.getChild(3));
		if (ctx.getChild(4) instanceof ElementContentContext)
			contents = getContent((ElementContentContext) ctx.getChild(4));
		if (switchElement)
			switchExists.pop();

//		if(ctx.getChild(2) instanceof ElementAttributesContext){
//		if (((ElementAttributesContext) ctx.getChild(2)).directive().size() > 0 &&
//				!((ElementAttributesContext) ctx.getChild(2)).directive(0).getChild(0).equals("cp-switch") &&
//				!((ElementAttributesContext) ctx.getChild(2)).directive(0).getChild(0).equals("cp-switchDefault") &&
//				!(((ElementAttributesContext) ctx.getChild(2)).directive(0).getChild(0) instanceof EventContext)
//		) {
//
//
////			List<Scope>  scopes = Visitor.symbolTable.getScopes();
////			for (int i = scopes.size()-1; i>=0;i--){
////				if(scopes.get(i).getisparent()){
////					scopes.get(i).setisParent(false);
////					break;
////				}
////
////			}
//
//
//		}
//
//
//
//
//	}

		ElementNode element = new ElementNode(tagName, attributes.toArray(new DocumentNode[attributes.size()]), contents.toArray(new DocumentNode[contents.size()]));
		return element;

	}

	@Override
	public AbstractASTNode visitSelfClosedElement(SelfClosedElementContext ctx) {
		String tagName = ctx.getChild(1).getText();
		List<AbstractASTNode> attributes = new ArrayList<AbstractASTNode>();
		
		if (ctx.getChild(2) instanceof ElementAttributesContext)
			attributes = getAttributes((ElementAttributesContext) ctx.getChild(2));
		
		ElementNode element = new ElementNode(tagName, attributes.toArray(new DocumentNode[attributes.size()]));
		return element;
	}

	@Override
	public AbstractASTNode visitTextNode(TextNodeContext ctx) {
		return new TextNode(ctx.getText());
	}

	public List<AbstractASTNode> getAttributes(ElementAttributesContext ctx) {
		List<AbstractASTNode> attributes = new ArrayList<AbstractASTNode>();
		for (int index = 0; index < ctx.getChildCount(); index++) {
			attributes.add(visit(ctx.getChild(index)));

		}
		return attributes;
	}

	@Override
	public AbstractASTNode visitAttributeNode(AttributeNodeContext ctx) {
		String name = ctx.getChild(0).getText();
		String value = null;
		if (ctx.getChildCount() > 1)
			value = ctx.getChild(2).getText();
		return new AttributeNode(name, value);
	}

	@Override
	public AbstractASTNode visitMustache(MustacheContext ctx) {
		Symbol symbol = new Symbol();

		MustachNode mustache;
		if (ctx.getChildCount() > 2) {
			mustache = new MustachNode(expressionVisitor.visit(ctx.getChild(1)));
		}
		else
			mustache = new MustachNode();


		return mustache;
	}
	
	protected boolean testName(String openTag, String closeTag) {
		return openTag.compareToIgnoreCase(closeTag) == 0;
	}

}