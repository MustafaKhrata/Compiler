package SymbolTable;

import java.util.ArrayList;

public class SymbolTable {


    private ArrayList<Scope> scopes = new ArrayList<Scope>();



    public ArrayList<Scope> getScopes() {
        return scopes;
    }

    public void setScopes(ArrayList<Scope> scopes) {
        this.scopes = scopes;
    }

    public void addScope(Scope scope){
        this.scopes.add(scope);
    }

}
