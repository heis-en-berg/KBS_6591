package problog.model;

import java.util.ArrayList;
import java.util.HashMap;

public class IDB {
    /*
     *   HashMap<Head, ArrayList<Expression>>
     */
    public HashMap<Expression, ArrayList<Expression>> rules = new HashMap<>();
    /*
     *  Adds a new rule.
     */
    public void addRule(Expression expressionHead, ArrayList<Expression> expressionBody){
        rules.put(expressionHead,expressionBody);
    }
}
