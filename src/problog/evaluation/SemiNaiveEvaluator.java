package problog.evaluation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import problog.model.DB;
import problog.model.EDB;
import problog.model.Expression;

public class SemiNaiveEvaluator {

    /* ProbLog evaluation using SemiNaive Evaluation. */
    public void performSemiNaiveEvaluation(DB db) {

        /* isSame verifies if facts in each of rule EDBs are same as in EDB. Defaulting the value to false. */
    	boolean isSame = false;
    	Integer numberOfIterations = 1;
    	db.last_derived_facts.facts.putAll(db.edb.facts);
        while(!isSame) {
            int currentRuleNumber = 0;

            /*  Pass head, body of rules, body atom count(bodyIndex), variables,
                number of rule getting processed and edb, idb as db as
                arguments to semiNaiveEvaluator.
             */
            for (Expression head : db.idb.rules.keySet()) {
            	for(String lastFactPredicate : db.last_derived_facts.facts.keySet()) {
            		for(List<String> lastDerivedFact : db.last_derived_facts.facts.get(lastFactPredicate).keySet()) {
            			ArrayList<Expression> body = db.idb.rules.get(head);
                		for(Expression currentBodyExpression: body) {
                			HashMap<String, String> variables = new HashMap<>();
                			if(currentBodyExpression.predicate.equals(lastFactPredicate)) {
                				fillVariablesWithLastFact(currentBodyExpression.terms, lastDerivedFact, variables);
                				semiNaiveEvaluator(head, body, 0, variables, db, currentRuleNumber);
                			}
                		}
            		}
            	}

            	/* Increment the ruleNumber after evaluating a rule. */
                currentRuleNumber++;
            }
            isSame = putTempEDBtoEDB(db);
            if(!isSame) {
            	db.last_derived_facts.facts = new HashMap<>();
            	db.last_derived_facts.facts.putAll(db.edb_temp.facts);
            	db.edb_temp.facts = new HashMap<>();
            }
            numberOfIterations++;
        }
        System.out.println("Number of iterations : " + numberOfIterations);
    }

    /* Move the variables with the last fact variables from last_derived_facts
       to currentBodyExpressionTerms.
     */
    private void fillVariablesWithLastFact(List<String> currentBodyExpressionTerms, List<String> lastDerivedFact,
			HashMap<String, String> variables) {
		for(int i = 0; i < currentBodyExpressionTerms.size(); i++) {
			variables.put(currentBodyExpressionTerms.get(i), lastDerivedFact.get(i));
		}
		
	}

    /* Replace facts from temporary EDB to EDB. */
	private  Boolean putTempEDBtoEDB(DB db) {
        Boolean isSame = true;
        for (String predicate : db.edb_temp.facts.keySet()) {
        	HashMap<List<String>, Double> newFactList = db.edb_temp.facts.get(predicate);
            Double probability = 0.0;
            for (List<String> newFact : newFactList.keySet()) {
            	for(EDB ruleEDB : db.ruleFacts) {
            		if(ruleEDB.ruleFact.containsKey(predicate) && ruleEDB.ruleFact.get(predicate).containsKey(newFact)) {
            			Collection<Double> fromFactProbs = ruleEDB.ruleFact.get(predicate).get(newFact).values();
            			for(Double prob : fromFactProbs) {
            				if(db.disjunctionFunctionType.equals(1)) {
            					probability = probability + prob - (probability * prob);
            				} else {
            					probability = Math.max(probability, prob);
            				}
            			}
            		}
            	}
                Expression newExp = new Expression(predicate,newFact,probability);
                if(!db.edb.addFact(newExp)) {
					isSame = false;
				}
                probability = 0.0;
            }
        }
        return isSame;
    }

    /* Derive facts for each rule till all atoms in the body are evaluated. */
    private void semiNaiveEvaluator(Expression head, ArrayList<Expression> body, Integer bodyIndex,
                                HashMap<String, String> variables, DB db, int currentRuleNumber) {

        if (bodyIndex >= body.size()) {
            return;
        }

        /* Retrieve all the facts pertaining to a rule or its atoms. */
        HashMap<List<String>, Double> factList = getFactList(db, body, bodyIndex, variables);
        if(factList == null) {
            return;
        }
        Expression currentBodyExpression = body.get(bodyIndex);
        List<String> currentBodyExpressionVariableList = currentBodyExpression.terms;

        for (List<String> fact : factList.keySet()) {
            HashMap<String, String> newVariables = new HashMap<>();
            for (int i = 0; i < currentBodyExpressionVariableList.size(); i++) {
                if (!variables.containsKey(currentBodyExpressionVariableList.get(i))) {
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
                            if(db.ruleFacts.size() <=  currentRuleNumber) {
                            	db.ruleFacts.add(currentRuleNumber, new EDB());
                            }
                            EDB ruleEDB = db.ruleFacts.get(currentRuleNumber);
                            ruleEDB.addFactToRuleEDB(newFactExp, variables.toString());
                            db.edb_temp.addFactToTempEDB(newFactExp, db.disjunctionFunctionType);
                        }

                    } else {
                        semiNaiveEvaluator(head, body, bodyIndex + 1, oldPlusNewVariables, db, currentRuleNumber);
                    }
                }

            }
        }

    }

    /* Retrieving facts either partially or completely from EDB based on the variables association with the fact term..*/
    private HashMap<List<String>, Double> getFactList(DB db, ArrayList<Expression> body, Integer bodyIndex,
			HashMap<String, String> variables) {
    	final HashMap<List<String>, Double> factList;
    	List<String> currentBodyExpressionVariables = body.get(bodyIndex).terms;
    	List<String> factMatched = new ArrayList<>();
    	Integer matchCount = 0;
    	for(String variableName : currentBodyExpressionVariables) {
    		if(variables.containsKey(variableName)) {
    			factMatched.add(variables.get(variableName));
    			matchCount++;
    		} else {
    			factMatched.add(null);
    		}
    	}
    	if(matchCount == 0) {
    		factList = db.edb.facts.get(body.get(bodyIndex).predicate);
    	} else if(matchCount.equals(currentBodyExpressionVariables.size())) {
    		factList = new HashMap<>();
    		
    		if(db.edb.facts.containsKey(body.get(bodyIndex).predicate) && db.edb.facts.get(body.get(bodyIndex).predicate).containsKey(factMatched)) {
    			factList.put(factMatched, db.edb.facts.get(body.get(bodyIndex).predicate).get(factMatched));
    		}
    	} else {
    		factList = new HashMap<>();
    		HashMap<List<String>, Double> tempFactList = db.edb.facts.get(body.get(bodyIndex).predicate);
    		if(tempFactList == null) {
    			return null;
    		}
    		tempFactList.entrySet().stream().forEach((entry) -> {
    			List<String> currentTerms = entry.getKey();
    			Double currentProbability = entry.getValue();
    			boolean isFactValid = true;
    			for(int i = 0; i < currentTerms.size(); i++) {
    				if(factMatched.get(i) != null && !factMatched.get(i).equals(currentTerms.get(i))) {
    					isFactValid  = false;
    				}
    			}
    			if(isFactValid) {
    				factList.put(currentTerms, currentProbability);
    			}
    		});
    	}
		return factList;
	}

    /* Calculate probability using propagation as multiplication(*) and conjunction as min. */
	private Double calculateProbability(Expression head, ArrayList<Expression> body, DB db,
                                        HashMap<String, String> variables) {
        Double minBodyProbability = 1.0;
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
