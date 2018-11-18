package problog.evaluation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import problog.model.DB;
import problog.model.Expression;

public class NaiveEvaluator {

	public void performNaiveEvaluation(DB db) {
		boolean isSame = true;
		while(isSame) {
			for (Expression head : db.idb.rules.keySet()) {
				ArrayList<Expression> body = db.idb.rules.get(head);
				HashMap<String, String> variables = new HashMap<>();
				naiveEvaluator(head, body, 0, variables, db);
			}
			isSame = putTempEDBtoEDB(db);
			if(!isSame) {
				db.edb_temp.facts = new HashMap<>();
				isSame = true;
			} else {
				printEDB(db);
				return;
			}
		}
		
	}

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

	private void printEDB(DB db) {
		for (String predicate : db.edb.facts.keySet()) {
			HashMap<List<String>, Double> factList = db.edb.facts.get(predicate);
			for (List<String> terms : factList.keySet()) {
				System.out.println(predicate + terms.toString() + ". : " + factList.get(terms));
			}
		}
	}

	private void naiveEvaluator(Expression head, ArrayList<Expression> body, Integer bodyIndex,
			HashMap<String, String> variables, DB db) {

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
							if (variables.containsKey(headVariable)) {
								newFact.add(variables.get(headVariable));
							} else if (newVariables.containsKey(headVariable)) {
								newFact.add(newVariables.get(headVariable));
							}
						}
						if (newFact.size() == head.terms.size()) {
							Double probability = calculateProbability(head, body, db, oldPlusNewVariables);
							Expression newFactExp = new Expression(head.predicate, newFact, probability);
							db.edb_temp.addFactToTempEDB(newFactExp);
						}

					} else {
						naiveEvaluator(head, body, bodyIndex + 1, oldPlusNewVariables, db);
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
