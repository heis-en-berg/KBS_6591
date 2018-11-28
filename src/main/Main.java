package main;

import problog.parser.Parser;

import java.util.Calendar;

import problog.evaluation.NaiveEvaluator;
import problog.evaluation.SemiNaiveEvaluator;
import problog.model.DB;

public class Main {

	public static void main(String[] args) {

		Parser parser = new Parser();
		//naiveEval(parser);
		semiNaiveEval(parser);
		parser.writeFile();
	}

	private static void naiveEval(Parser parser) {
		Calendar startTime = Calendar.getInstance();
		NaiveEvaluator naiveEvaluator = new NaiveEvaluator();
		naiveEvaluator.performNaiveEvaluation(parser.db);
		Calendar endTime = Calendar.getInstance();
		printStats(startTime, endTime, parser);
	}

	private static void semiNaiveEval(Parser parser) {
		Calendar startTime = Calendar.getInstance();
		SemiNaiveEvaluator seminaiveEvaluator = new SemiNaiveEvaluator();
		seminaiveEvaluator.performSemiNaiveEvaluation(parser.db);
		Calendar endTime = Calendar.getInstance();
		printStats(startTime, endTime, parser);
	}

	private static void printStats(Calendar startTime, Calendar endTime, Parser parser) {
		System.out.println("Time taken: " + (endTime.getTimeInMillis() - startTime.getTimeInMillis()) + " ms");
		System.out.println("Total Facts : " + parser.getFactCount());
		System.out.println("Output file : " + parser.outPutFilePath);
	}

}