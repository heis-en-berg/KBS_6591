package main;

import problog.parser.Parser;

import java.util.Calendar;
import java.util.Scanner;

import problog.evaluation.NaiveEvaluator;
import problog.evaluation.SemiNaiveEvaluator;

public class Main {

	public static void main(String[] args) {

		final Scanner sc = new Scanner(System.in);
		System.out.print("Enter 1 for Naive Evaluation, 2 for Semi-Naive : ");
		Integer choice = sc.nextInt();
		Parser parser = new Parser();
		System.out.print("Enter file path: ");
		sc.nextLine();
        parser.filePath = sc.nextLine();
		parser.readFile();
		sc.close();
		if(choice == 1) {
			naiveEval(parser);
		} else {
			semiNaiveEval(parser);
		}
		
		parser.writeFile();
	}

	/* Call the naive evaluator in case of naive evaluation. Also note the time of evaluation. */
	private static void naiveEval(Parser parser) {
		Calendar startTime = Calendar.getInstance();
		NaiveEvaluator naiveEvaluator = new NaiveEvaluator();
		naiveEvaluator.performNaiveEvaluation(parser.db);
		Calendar endTime = Calendar.getInstance();
		printStats(startTime, endTime, parser);
	}

    /* Call the semi-naive evaluator in case of semi-naive evaluation. Also note the time of evaluation. */
	private static void semiNaiveEval(Parser parser) {
		Calendar startTime = Calendar.getInstance();
		SemiNaiveEvaluator seminaiveEvaluator = new SemiNaiveEvaluator();
		seminaiveEvaluator.performSemiNaiveEvaluation(parser.db);
		Calendar endTime = Calendar.getInstance();
		printStats(startTime, endTime, parser);
	}

	/* Output the evaluation time, total number of facts derived and path of the output file. */
	private static void printStats(Calendar startTime, Calendar endTime, Parser parser) {
		System.out.println("Time taken: " + (endTime.getTimeInMillis() - startTime.getTimeInMillis()) + " ms");
		System.out.println("Total Facts : " + parser.getFactCount());
		System.out.println("Output file : " + parser.outPutFilePath);
	}

}
