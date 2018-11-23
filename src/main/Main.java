package main;

import problog.evaluation.NaiveEvaluator;
import problog.parser.Parser;
import problog.evaluation.SemiNaiveEvaluator;

public class Main {

    public  static  void main(String[] args){

        Parser parser = new Parser();

//        NaiveEvaluator naiveEvaluator = new NaiveEvaluator();
//        naiveEvaluator.performNaiveEvaluation(parser.db);

        SemiNaiveEvaluator seminaiveEvaluator = new SemiNaiveEvaluator();
        seminaiveEvaluator.performSemiNaiveEvaluation(parser.db, parser.extraEDB);

    }

}

// /Users/sahilsodhi/Documents/COMP6591/Sample.txt