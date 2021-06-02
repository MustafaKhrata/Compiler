package models.expression.value.reference;

import SymbolTable.Scope;
import SymbolTable.Symbol;
import models.expression.ValueExpression;
import models.util.Formatter;

public class ReferenceExpression extends ValueExpression {

	protected String name;

	private Symbol symbol;




	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Symbol getSymbol() {
		return symbol;
	}

	public void setSymbol(Symbol symbol) {
		this.symbol = symbol;
	}

	public ReferenceExpression(String name) {
		this.name = name;
	}

	@Override
	protected String nodeName() {
		return "ReferenceExpression";
	}

	@Override
	protected Formatter nodeValue(Formatter formatter) {
		return formatter.addProperty("reference", name);
	}
	
}
