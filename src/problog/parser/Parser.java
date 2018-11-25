package problog.parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import problog.model.DB;
import problog.model.Expression;

public class Parser {
    public DB db;
    public Parser(){
        db = new DB();
        readFile();
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
        Scanner consoleScanner = new Scanner(System.in);
        System.out.print("Enter file path: ");
        String filePath = consoleScanner.nextLine();
        consoleScanner.close();
        File file = new File(filePath);

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
}
