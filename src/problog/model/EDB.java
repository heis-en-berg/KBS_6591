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
	public void addFact(Expression exp, Double ruleProbability) {
		HashMap<List<String>, Double> factList = facts.get(exp.predicate);
		if (factList == null) {
			factList = new HashMap<List<String>, Double>();
		}
		Double newProb = 0.0;
		newProb = ruleProbability + exp.probability - (ruleProbability * exp.probability);
		factList.put(exp.terms, newProb);
		facts.put(exp.predicate, factList);
	}

	/*
	 * Adds a new fact to temp EDB.
	 */
	public void addFactToTempEDB(Expression exp, Double ruleProbability) {
		HashMap<List<String>, Double> factList = facts.get(exp.predicate);
		if (factList == null) {
			factList = new HashMap<List<String>, Double>();
		}
		Double newProb = 0.0;
		newProb = exp.probability;
		factList.put(exp.terms, newProb);
		facts.put(exp.predicate, factList);

	}

}
