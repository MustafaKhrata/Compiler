package models.statements;

import models.expression.ValueExpression;

public abstract class LoopStatement extends Statement {

	protected ValueExpression objectToLoopOn;

	public ValueExpression getObjectToLoopOn() {
		return objectToLoopOn;
	}

	public void setObjectToLoopOn(ValueExpression objectToLoopOn) {
		this.objectToLoopOn = objectToLoopOn;
	}

	protected LoopStatement(ValueExpression objectToLoopOn) {
		this.objectToLoopOn = objectToLoopOn;
	}
}
