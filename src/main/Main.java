package main;

import problog.parser.Parser;

import java.util.Calendar;
import java.util.Scanner;

import problog.evaluation.NaiveEvaluator;
import problog.evaluation.SemiNaiveEvaluator;

public class Main {
	
	private static final long MEGABYTE = 1024L * 1024L;

	  public static long bytesToMegabytes(long bytes) {
	    return bytes / MEGABYTE;
	  }

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

	/* Call to naive evaluator. */
	private static void naiveEval(Parser parser) {
		Calendar startTime = Calendar.getInstance();
		NaiveEvaluator naiveEvaluator = new NaiveEvaluator();
		naiveEvaluator.performNaiveEvaluation(parser.db);
		Calendar endTime = Calendar.getInstance();
		printStats(startTime, endTime, parser);
	}

    /* Call to semi-naive evaluator. */
	private static void semiNaiveEval(Parser parser) {
		Calendar startTime = Calendar.getInstance();
		SemiNaiveEvaluator seminaiveEvaluator = new SemiNaiveEvaluator();
		seminaiveEvaluator.performSemiNaiveEvaluation(parser.db);
		Calendar endTime = Calendar.getInstance();
		printStats(startTime, endTime, parser);
	}

	/* Output evaluation stats */
	private static void printStats(Calendar startTime, Calendar endTime, Parser parser) {
		System.out.println("Time taken: " + (endTime.getTimeInMillis() - startTime.getTimeInMillis()) + " ms");
		System.out.println("Total Facts : " + parser.getFactCount());
		//printMemoryConsumption();
		System.out.println("Output file : " + parser.outPutFilePath);
	}
	
	private static void printMemoryConsumption() {
		Runtime runtime = Runtime.getRuntime();
	    // Run the garbage collector
	    runtime.gc();
	    // Calculate the used memory
	    long memory = runtime.totalMemory() - runtime.freeMemory();
	    System.out.println("Used memory : "
	            + bytesToMegabytes(memory) + " MB");
	}

}
