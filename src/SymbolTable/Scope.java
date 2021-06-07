package SymbolTable;

import java.util.LinkedHashMap;
import java.util.Map;

public class Scope {


    private String id;
    private Scope myparent;
    private boolean isParent = false;
    public Map<String, Symbol> symbolMap = new LinkedHashMap<String, Symbol>();


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Scope getmyParent() {
        return myparent;
    }

    public void setmyParent(Scope parent) {
        this.myparent = parent;
    }


    public boolean getisparent() {
        return isParent;
    }
    public void setisParent(boolean parent) {
        isParent = parent;
    }

    public Map<String, Symbol> getSymbolMap() {
        return symbolMap;
    }

    public void setSymbolMap(Map<String, Symbol> symbolMap) {
        this.symbolMap = symbolMap;
    }

    public void addSymbol(String name, Symbol symbol) {
        this.symbolMap.put(name,symbol);
    }
}
