package models.directive;


import SymbolTable.Scope;
import models.AbstractASTNode;
import models.nodes.DocumentNode;
import models.util.Formatter;
import visitors.Visitor;


public class Directive extends DocumentNode {

	protected String type;
	protected AbstractASTNode value;
	protected Scope scope;

	public Scope getScope() {
		return scope;
	}

	public void setScope(Scope scope) {
		this.scope = scope;
	}

	public Directive(String type) {
		this.type = type;
	}

	public Directive() {

	}

	public Directive(String type,Scope scope, AbstractASTNode value ) {
		this.type = type;
		this.scope = scope;
		this.value = value;

	}

	@Override
	protected String nodeName() {

				return "Directive";
	}
	
	@Override
	protected Formatter nodeValue(Formatter formatter) {
		formatter.addProperty("type", type);
		if(value != null)
			formatter.object(value.toString("value"));

//		if(this.type.equals("cp-if")){
//
//			Scope scopeIf = new Scope();
//
//			scopeIf.setId("IF_Scope");
//			this.setScope(scopeIf);
//			Visitor.symbolTable.addScope(this.scope);
//
//
//
//		}


		return formatter;
	}


	public String getName() {
		return type;
	}

}
