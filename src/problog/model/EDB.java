package problog.model;

import java.util.HashMap;
import java.util.List;

public class EDB {

	/*
	 * HashMap<Predicate, HashMap<Terms, Probability >>
	 */
	public HashMap<String, HashMap<List<String>, Double>> facts = new HashMap<>();
	
	/*
	 * HashMap<Predicate, HashMap<Terms, Probability >>
	 */
	public HashMap<String, HashMap<List<String>, HashMap<String, Double>>> ruleFact = new HashMap<>();
	
	/*
	 * Adds a new fact to EDB.
	 */
	public Boolean addFact(Expression exp) {
		Boolean isSameExpression = false;
		HashMap<List<String>, Double> factList = facts.get(exp.predicate);
		if (factList == null) {
			factList = new HashMap<List<String>, Double>();
		}
		if(factList.containsKey(exp.terms) && factList.get(exp.terms).equals(exp.probability)) {
			isSameExpression = true;
		} else {
			factList.put(exp.terms, exp.probability);
			facts.put(exp.predicate, factList);
		}
		return isSameExpression;
	}
	
	/*
	 * Adds a new fact to temp EDB.
	 */
	public void addFactToTempEDB(Expression exp, Integer disjunctionFunctionType) {
		HashMap<List<String>, Double> factList = facts.get(exp.predicate);
		if (factList == null) {
			factList = new HashMap<List<String>, Double>();
		}
		if(factList.containsKey(exp.terms)) {
			Double oldProb = factList.get(exp.terms);
			factList.remove(exp.terms);
			Double newProb = 0.0;
			if(disjunctionFunctionType.equals(1)) {
				newProb = oldProb + exp.probability - (oldProb * exp.probability);
			} else if(disjunctionFunctionType.equals(2)) {
				newProb = Math.max(oldProb, exp.probability);
			}
			factList.put(exp.terms, newProb);
		} else {
			factList.put(exp.terms, exp.probability);
		}
		facts.put(exp.predicate, factList);

	}

	/* Add facts to the respective rule's EDB. */
	public void addFactToRuleEDB(Expression exp, String variables) {
		HashMap<List<String>, HashMap<String, Double>> factList = ruleFact.get(exp.predicate);
		if (factList == null) {
			factList = new HashMap<List<String>, HashMap<String, Double>>();
		}
		if(factList.containsKey(exp.terms)) {
			HashMap<String, Double> fromFacts = factList.get(exp.terms);
			fromFacts.put(variables, exp.probability);
			factList.put(exp.terms, fromFacts);
		} else {
			HashMap<String, Double> fromFacts = new HashMap<>();
			fromFacts.put(variables,exp.probability);
			factList.put(exp.terms, fromFacts);
		}
		ruleFact.put(exp.predicate, factList);
	}
}
