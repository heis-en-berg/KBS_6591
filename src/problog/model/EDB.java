package problog.model;

import java.util.HashMap;
import java.util.List;

public class EDB {

	/*
	 * HashMap<Predicate, HashMap<Terms, Probability >>
	 */
	public HashMap<String, HashMap<List<String>, Double>> facts = new HashMap<>();

	/*
	 * Adds a new fact.
	 */
	public void addFact(Expression exp) {
		HashMap<List<String>, Double> factList = facts.get(exp.predicate);
		if (factList == null) {
			factList = new HashMap<List<String>, Double>();
		}
		if (factList.containsKey(exp.terms)) {
			Double oldProb = factList.get(exp.terms);
			Double newProb = oldProb + exp.probability - (oldProb * exp.probability);
			factList.put(exp.terms, newProb);
		} else {
			factList.put(exp.terms, exp.probability);
		}
		facts.put(exp.predicate, factList);

	}

}
