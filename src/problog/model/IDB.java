package problog.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class IDB {
    /*
     *   HashMap<Head, ArrayList<Expression>>
     */
    public LinkedHashMap<Expression, ArrayList<Expression>> rules = new LinkedHashMap<>();
    /*
     *  Adds a new rule.
     */
    public void addRule(Expression expressionHead, ArrayList<Expression> expressionBody){
        rules.put(expressionHead,expressionBody);
    }
}
