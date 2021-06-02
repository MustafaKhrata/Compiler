package visitors;

import java.util.Stack;
import java.util.concurrent.atomic.AtomicLong;

import SymbolTable.SymbolTable;
import misc.HTMLParserBaseVisitor;

public class Visitor<T> extends HTMLParserBaseVisitor<T> {
	static DocumentVisitor documentVisitor = new DocumentVisitor();
	static ExpressionVisitor expressionVisitor = new ExpressionVisitor();
	 public static SymbolTable symbolTable = new SymbolTable();
	public static AtomicLong atomicCounter = new AtomicLong();


}
