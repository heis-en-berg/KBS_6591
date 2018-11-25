package main;

import problog.parser.Parser;
import problog.evaluation.NaiveEvaluator;
import problog.evaluation.SemiNaiveEvaluator;

public class Main {

    public  static  void main(String[] args){

        Parser parser = new Parser();
//        NaiveEvaluator naiveEvaluator = new NaiveEvaluator();
//        naiveEvaluator.performNaiveEvaluation(parser.db);
        SemiNaiveEvaluator seminaiveEvaluator = new SemiNaiveEvaluator();
        seminaiveEvaluator.performSemiNaiveEvaluation(parser.db);
    }
}