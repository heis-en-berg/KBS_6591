package problog.evaluation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import problog.model.DB;
import problog.model.EDB;
import problog.model.Expression;

public class SemiNaiveEvaluator {

    public void performSemiNaiveEvaluation(DB db) {
        boolean isSame = true;
        while(isSame) {
            int dbCount = 0;
            for (Expression head : db.idb.rules.keySet()) {
                ArrayList<Expression> body = db.idb.rules.get(head);
                HashMap<String, String> variables = new HashMap<>();
                semiNaiveEvaluator(head, body, 0, variables, db, dbCount);
                dbCount++;
            }
            // in case next loop does not run.
            dbCount--;
            isSame = putSemiTempEDBtoEDB(db, dbCount);
            if(!isSame) {
                db.edb.addRuleFactFromEDBTempToLastEDB();
                isSame = true;
            } else {
                printEDB(db);
                return;
            }
        }

    }

    private  Boolean putSemiTempEDBtoEDB(DB db, int dbCount) {
        Boolean isSame = true;
        List<String> terms = new ArrayList<>();
        for (String predicate : db.edb_temp.semiEdbTemp.keySet()) {
            terms.add(predicate);
            Double probability = 0.0;
            for (int i = 0; i <= dbCount; i++) {
                HashMap<List<String>, Double> idbExpression = db.edb.arrayListOfEDB.get(dbCount).ruleFacts.get(predicate);
                if (idbExpression.isEmpty()) {
                    continue;
                } else {
                    probability = probability + idbExpression.get(idbExpression.keySet()) - (probability * idbExpression.get(idbExpression.keySet()));
                }
                Expression newExp = new Expression(predicate,terms,probability);
                db.edb.addFact(newExp);
                terms.clear();
            }
        }
        return isSame;
    }

    private void printEDB(DB db) {
        for (String predicate : db.edb.facts.keySet()) {
            HashMap<List<String>, Double> factList = db.edb.facts.get(predicate);
            for (List<String> terms : factList.keySet()) {
                System.out.println(predicate + terms.toString() + ". : " + factList.get(terms));
            }
        }
    }

    private void semiNaiveEvaluator(Expression head, ArrayList<Expression> body, Integer bodyIndex,
                                HashMap<String, String> variables, DB db, int dbCount) {

        if (bodyIndex >= body.size()) {
            return;
        }
        HashMap<List<String>, Double> factList = db.edb.facts.get(body.get(bodyIndex).predicate);
        if(factList == null) {
            return;
        }
        Expression currentBodyExpression = body.get(bodyIndex);
        List<String> currentBodyExpressionVariableList = currentBodyExpression.terms;

        for (List<String> fact : factList.keySet()) {
            HashMap<String, String> newVariables = new HashMap<>();
            for (int i = 0; i < currentBodyExpressionVariableList.size(); i++) {
                if (variables.containsKey(currentBodyExpressionVariableList.get(i))) {
                    if (!fact.get(i).equals(variables.get(currentBodyExpressionVariableList.get(i)))) {
                        break;
                    }
                } else {
                    newVariables.put(currentBodyExpressionVariableList.get(i), fact.get(i));
                }

                if (i == currentBodyExpressionVariableList.size() - 1) {

                    HashMap<String, String> oldPlusNewVariables = new HashMap<>();
                    oldPlusNewVariables.putAll(variables);
                    oldPlusNewVariables.putAll(newVariables);

                    if (bodyIndex == body.size() - 1) {
                        List<String> newFact = new ArrayList<String>();
                        for (String headVariable : head.terms) {
                            if (oldPlusNewVariables.containsKey(headVariable)) {
                                newFact.add(oldPlusNewVariables.get(headVariable));
                            }
                        }
                        if (newFact.size() == head.terms.size()) {
                            Double probability = calculateProbability(head, body, db, oldPlusNewVariables);
                            Expression newFactExp = new Expression(head.predicate, newFact, probability);
                            if(db.edb.arrayListOfEDB.size() <= dbCount){
                                db.edb.arrayListOfEDB.add(dbCount, new EDB());
                            }
                            db.edb.arrayListOfEDB.get(dbCount).semiRuleWiseAddFact(newFactExp);
//                            db.edb_temp.addFactToTempEDB(newFactExp);
                            db.edb_temp.semiAddEDBTempFacts(newFactExp);
                        }

                    } else {
                        semiNaiveEvaluator(head, body, bodyIndex + 1, oldPlusNewVariables, db, dbCount);
                    }
                }

            }
        }

    }

    private Double calculateProbability(Expression head, ArrayList<Expression> body, DB db,
                                        HashMap<String, String> variables) {
        Double minBodyProbability = 1.1;
        for (Expression bodyElement : body) {
            List<String> terms = bodyElement.terms;
            List<String> termsValues = new ArrayList<>();
            for (String variable : terms) {
                termsValues.add(variables.get(variable));
            }
            minBodyProbability = Math.min(minBodyProbability, db.edb.facts.get(bodyElement.predicate).get(termsValues));
        }

        minBodyProbability = minBodyProbability * head.probability;

        return minBodyProbability;
    }

}
