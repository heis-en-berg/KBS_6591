package problog.model;

import java.util.List;

/* Basic form of an atom represented as an expression. */
public class Expression {
	
	public String predicate;
	public List<String> terms;
	public Double probability;
	
	public Expression(String predicate, List<String> terms, Double probability) {
		this.predicate = predicate;
		this.terms = terms;
		this.probability = probability;
	}
}
