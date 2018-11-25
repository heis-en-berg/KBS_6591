package main;

import problog.parser.Parser;
import problog.evaluation.SemiNaiveEvaluator;

public class Main {

    public  static  void main(String[] args){

        Parser parser = new Parser();
//
////        NaiveEvaluator naiveEvaluator = new NaiveEvaluator();
////        naiveEvaluator.performNaiveEvaluation(parser.db);
//
        SemiNaiveEvaluator seminaiveEvaluator = new SemiNaiveEvaluator();
        seminaiveEvaluator.performSemiNaiveEvaluation(parser.db);
//        List<String> listex = new ArrayList<>();
//        listex.add("Sodhi");
//        String sahil = "Sahil";
//        Double val = 0.5;
//        HashMap<String,HashMap<List<String>,Double>> fact = new HashMap<>();
//        HashMap<List<String>,Double> factList = new HashMap<>();
//        factList.put(listex,val);
//        fact.put(sahil,factList);
//        System.out.println("factList keyset value:" +factList.get(listex));
    }
}

// /Users/sahilsodhi/Documents/COMP6591/Sample.txt