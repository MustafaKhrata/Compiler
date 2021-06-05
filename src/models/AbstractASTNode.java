package models;

import SymbolTable.Scope;
import models.util.Formatter;
import visitors.Visitor;

import java.util.UUID;

public abstract class AbstractASTNode implements ASTNodeInterface {

	abstract protected String nodeName();


	abstract protected Formatter nodeValue(Formatter formatter);
	
	public String format(Formatter formatter) {
		nodeValue(formatter);
		return formatter.format();
	}
	
	public String toString(String prefix) {
		Formatter formatter = new Formatter().name(nodeName());
		formatter.prefix(prefix);
		return format(formatter);
	}
	
	@Override
	public String toString() {
		Formatter formatter = new Formatter().name(nodeName());
		return format(formatter);
	}

	//get who is  parent to this Symbol
	public Scope getParentToNode(){

		for(int i = Visitor.symbolTable.getScopes().size()-1; i>=0 ; i--){
			if(Visitor.symbolTable.getScopes().get(i).getMyparent()){
				Scope parentScope = Visitor.symbolTable.getScopes().get(i);

				return parentScope;
			}
		}
		return null;
	}

    //Create Id Uniq for each Scope
	public static String createId() {

		String currentCounter = String.valueOf(Visitor.atomicCounter.getAndIncrement());
		String uniqueId = UUID.randomUUID().toString();

		return uniqueId + "-" + currentCounter;
	}
}
