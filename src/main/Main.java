package main;

import java.util.ArrayList;
import java.util.List;

import problog.evaluation.NaiveEvaluator;
import problog.model.Expression;
import problog.parser.Parser;

public class Main {

    public  static  void main(String[] args){

        Parser parser = new Parser();
        NaiveEvaluator naiveEvaluator = new NaiveEvaluator();
        naiveEvaluator.performNaiveEvaluation(parser.db);
    }

}
