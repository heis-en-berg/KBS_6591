package problog.evaluation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import problog.model.DB;
import problog.model.Expression;

public class NaiveEvaluator {

    /* ProbLog evaluation using Naive Evaluation. */
	public void performNaiveEvaluation(DB db) {
		boolean isSame = false;
		Integer numberOfIterations = 1;

        /* isSame verifies if facts in temporary EDB are same as in EDB. */
		while(!isSame) {
			for (Expression head : db.idb.rules.keySet()) {
				ArrayList<Expression> body = db.idb.rules.get(head);
				HashMap<String, String> variables = new HashMap<>();
				naiveEvaluator(head, body, 0, variables, db);
			}
			isSame = putTempEDBtoEDB(db);

			/*  Continue with the naive evaluation if isSame is false.
			    Else print the facts to the output file.
			 */
			if(!isSame) {
				db.edb_temp.facts = new HashMap<>();
			}
			numberOfIterations++;
		}
		System.out.println("Number of iterations : " + numberOfIterations);
	}

	/* Replace facts from temporary EDB to EDB. */
	private Boolean putTempEDBtoEDB(DB db) {
		Boolean isSame = true;
		for (String predicate : db.edb_temp.facts.keySet()) {
			HashMap<List<String>, Double> newFactList = db.edb_temp.facts.get(predicate);
			for (List<String> terms : newFactList.keySet()) {
				Expression newExp = new Expression(predicate, terms, newFactList.get(terms));
				if(!db.edb.addFact(newExp)) {
					isSame = false;
				}
			}
		}
		return isSame;
	}

	/* Derive facts for each rule till all atoms in the body are evaluated. */
	private void naiveEvaluator(Expression head, ArrayList<Expression> body, Integer bodyIndex,
			HashMap<String, String> variables, DB db) {

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
							db.edb_temp.addFactToTempEDB(newFactExp, db.disjunctionFunctionType);
						}

					} else {
						naiveEvaluator(head, body, bodyIndex + 1, oldPlusNewVariables, db);
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
