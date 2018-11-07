package problog.parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import problog.model.DB;
import problog.model.Expression;

public class Parser {
    public static DB db;
    public Parser(){
        db = new DB();
        readFile();
    }
    public void readFile() {

        /* Read Facts from file.*/
        Scanner consoleScanner = new Scanner(System.in);
        System.out.print("Enter file path: ");
        String filePath = consoleScanner.nextLine();
        consoleScanner.close();
        File file = new File(filePath);

        try{
            Scanner lineScanner = new Scanner(file);
            while(lineScanner.hasNextLine()){
                String fact = lineScanner.nextLine();

                /* Remove unnecessary whitespace. */
                fact = fact.replaceAll(" ","");

                /* Retrieve fact predicate name. */
                String[] splitFact = fact.split("\\(");
                String predicate = splitFact[0];

                /* Retrieve fact probability. */
                String[] splitFactProb = fact.split(":");
                String probability = splitFactProb[1];
                Double prob = Double.parseDouble(probability);

                /* Retrieve fact terms as a List. */
                String atoms = splitFact[1];
                String[] splitAtoms = atoms.split("\\)");
                List<String> listOfTerms = new ArrayList<>();
                atoms = splitAtoms[0];
                String[] addAtoms = atoms.split(",");
                for(String y: addAtoms){
                    listOfTerms.add(y);
                }

                /* Add the given fact in db. */
                Expression expression = new Expression(predicate,listOfTerms,prob);
                db.edb.addFact(expression);
            }
            lineScanner.close();
        }
        catch (FileNotFoundException e){
            e.printStackTrace();
        }
    }
}
