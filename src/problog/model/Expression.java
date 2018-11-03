package problog.model;

import java.util.List;

public class Expression {
	
	public String predicate;
	public List<String> terms;
	public double probability;
	
	public Expression(String predicate, List<String> terms, double probability) {
		this.predicate = predicate;
		this.terms = terms;
		this.probability = probability;
	}
	
}
