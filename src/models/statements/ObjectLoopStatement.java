package models.statements;

import SymbolTable.Symbol;
import models.expression.ValueExpression;
import models.util.Formatter;

import java.util.ArrayList;
import java.util.List;

public class ObjectLoopStatement extends LoopStatement {

	protected String keyVariable;
	protected String valueVariable;
	public List<Symbol> symbols = new ArrayList<>();

	public ObjectLoopStatement(String keyVariable, String valueVariable, ValueExpression objectToLoopOn) {
		super(objectToLoopOn);
		this.keyVariable = keyVariable;
		this.valueVariable = valueVariable;
	}

	@Override
	protected String nodeName() {
		return "ObjectLoopStatement";
	}

	@Override
	protected Formatter nodeValue(Formatter formatter) {
		formatter.addProperty("keyVariable", keyVariable)
			.addProperty("valueVariable", valueVariable);
		formatter.object(objectToLoopOn.toString("loopOn"));
		return formatter;
	}

}
