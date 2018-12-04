package problog.parser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import problog.model.DB;
import problog.model.Expression;

public class Parser {
    public DB db;
    public String filePath;
    public String outPutFilePath;
    public Parser(){
        db = new DB();
    }
    /* Method to add rules to DB. */
    public void  addRule(String rule){
        String[] splitRule = rule.split(":-");

        /* Retrieve rule predicate name.*/
        String[] rulePredicate = splitRule[0].split("\\(");
        String predicate = rulePredicate[0];

        /* Retrieve rule probability. */
        int index = rule.lastIndexOf(":");
        String probability = rule.substring(index+1,rule.length()-1);
        Double prob = Double.parseDouble(probability);

        /* Retrieve rule head terms*/
        rulePredicate[1] = rulePredicate[1].substring(0,rulePredicate[1].length()-1);
        List<String> listOfTerms = new ArrayList<>();
        String[] addAtoms = rulePredicate[1].split(",");
        for (String y : addAtoms) {
            listOfTerms.add(y);
        }
        Expression headExpression = new Expression(predicate,listOfTerms,prob);

        /* Expression for rule body */
        String body = splitRule[1].substring(0,splitRule[1].lastIndexOf(":"));
        body = body.concat(",");
        String[] bodyExpressions = body.split("\\),");
        ArrayList<Expression> bodyPredicates = new ArrayList<>();
        for (String y : bodyExpressions) {
            List<String> listOfBodyTerms = new ArrayList<>();
            /* Body Predicates. */
            String[] ruleBodyPredicate = y.split("\\(");
            String bodyPredicate = ruleBodyPredicate[0];
            String[] bodyTerms = ruleBodyPredicate[1].split(",");
            for (String z : bodyTerms) {
                listOfBodyTerms.add(z);
            }
            Expression bodyExpression = new Expression(bodyPredicate,listOfBodyTerms,null);
            bodyPredicates.add(bodyExpression);
        }
        db.idb.addRule(headExpression,bodyPredicates);
    }

    /* Method to add facts to DB*/
    public  void addFact(String fact){
        /* Retrieve fact predicate name. */
        String[] splitFact = fact.split("\\(");
        String predicate = splitFact[0];

        /* Retrieve fact probability. */
        String[] splitFactProb = fact.split(":");
        String probability = splitFactProb[1];
        probability = probability.substring(0,probability.length()-1);
        Double prob = Double.parseDouble(probability);

        /* Retrieve fact terms as a List. */
        String atoms = splitFact[1];
        String[] splitAtoms = atoms.split("\\)");
        List<String> listOfTerms = new ArrayList<>();
        atoms = splitAtoms[0];
        String[] addAtoms = atoms.split(",");
        for (String y : addAtoms) {
            listOfTerms.add(y);
        }
        /* Add the given fact in db. */
        Expression expression = new Expression(predicate, listOfTerms, prob);
        db.edb.addFact(expression);
    }

    public void readFile() {

        /* Read clauses from file. */
        File file = new File(filePath);
        this.outPutFilePath = file.getParent() + "/output.pl";

        try{
            Scanner lineScanner = new Scanner(file);
            while(lineScanner.hasNextLine()) {
                String clause = lineScanner.nextLine();

                /* Remove unnecessary whitespace. */
                clause = clause.replaceAll(" ", "");

                /* Differentiate between a rule and a fact.*/
                CharSequence test = ":-";
                boolean test_result = clause.contains(test);

                /* Line should not be empty. */
                if (!clause.isEmpty()) {

                    /* Add Rule*/
                    if (test_result) {
                        addRule(clause);
                    }

                    /* Add a fact. */
                    else {
                        addFact(clause);
                    }
                }
            }
            lineScanner.close();
        }
        catch (FileNotFoundException e){
            e.printStackTrace();
        }
    }

    /* Output the fact results to a file.*/
    public void writeFile() {
    	File file = new File(filePath);
    	File outputFile = new File(this.outPutFilePath);
    	if(outputFile.exists()) {
    		outputFile.delete();
    	}
    	try {
		outputFile.createNewFile();
    	FileOutputStream fos = new FileOutputStream(outputFile);
    	BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
    	
    	for (String predicate : db.edb.facts.keySet()) {
			HashMap<List<String>, Double> factList = db.edb.facts.get(predicate);
			for (List<String> terms : factList.keySet()) {
				String fact = predicate + terms.toString() + ". : " + factList.get(terms);
				fact = fact.replaceAll("\\[", "\\(");
				fact = fact.replaceAll("\\]", "\\)");
				bw.write(fact);
				bw.newLine();
			}
		}

    	bw.close();
    	} catch (IOException e1) {
			e1.printStackTrace();
		}
    }

    /* Total number of facts derived from the naive or semi-naive evaluator. */
    public Integer getFactCount() {
    	Integer factCount = 0;
    	for (String predicate : db.edb.facts.keySet()) {
			HashMap<List<String>, Double> factList = db.edb.facts.get(predicate);
			for (List<String> terms : factList.keySet()) {
				factCount++;
			}
		}
    	return factCount;
    }
}
