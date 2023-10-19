import java.io.File;
import java.util.*;

/**
 * Code for benchmarking the time taken to simulate cutting
 * and splicing strands of DNA. These benchmark methods are
 * intended to be used in reasoning about tradeoffs in using 
 * a linked list to represent a strand of DNA and to compare
 * this representation with a simple String representation.
 * @author Owen Astrachan
 * @author Brandon Fain
 */

public class DNABenchmark {
	
	// Select/uncomment which implementation you want to benchmark
	//private static final String strandType = "StringStrand";
	private static final String strandType = "StringBuilderStrand";
	//private static final String strandType = "LinkStrand";
	
	
	// Benchmark parameters
	private static final String ENZYME = "gaattc";		// enzyme to cut
	private static final int DEFAULT_SPLICEE = 10000;	// default size of splicee
	private static final int SPLICEE_ROWS = 8;			// rows of spliceeBenchmark data
	private static final int SOURCE_ROWS = 5;			// rows of sourceBenchmark data
	private static final int TRIALS = 10;				// number of trials per run
	private static final int MEMORY_TRIALS = 2;
	
	private static String mySource;

	public static void main(String[] args)
			throws Exception {
		// Set the data file to benchmark here
		String fileName = "data/ecoli_small.txt";
		File file = new File(fileName);
		mySource = dnaFromScanner(new Scanner(file));

		printHeader();
		spliceeBenchmark();
		sourceBenchmark();
		System.out.println("--------------- Memory Exhaustion Benchmark ---------------");

		/**
		 *  Set the large data file to benchmark here
		fileName = "data/ecoli.txt";
		file = new File(fileName);
		mySource = dnaFromScanner(new Scanner(file));

		System.out.printf("Class\t%23s\t%12s\ttime\t%s\n", "splicee", "recomb","appends");
		exhaustMemoryBenchmark();
		**/
	}

	/**
	 * Timing benchmark for cutAndSplice on IDnaStrand.
	 * Scales size of source strand & number of breaks, holding splicee constant.
	 * Note that this benchmark depends on instance variable mySource holding
	 * the String source of the DNA being subject to recombinant simulation,
	 * @throws Exception
	 */
	public static void sourceBenchmark() throws Exception {
		String splicee = mySource.substring(0, DEFAULT_SPLICEE);
		strandSpliceBenchmark(ENZYME, splicee, strandType);
		for(int j=0; j<SOURCE_ROWS; j++) {
			String results = strandSpliceBenchmark(ENZYME, splicee, strandType);
			System.out.println(results);
			mySource += mySource;
		}
	}

	/**
	 * Timing benchmark for cutAndSplice on IDnaStrand.
	 * Scales size of splicee holding source strand constant.
	 * Note that this benchmark depends on instance variable mySource holding
	 * the String source of the DNA being subject to recombinant simulation.
	 * @throws Exception
	 */
	public static void spliceeBenchmark() throws Exception {
		String splicee = mySource.substring(0, DEFAULT_SPLICEE);
		for (int j=0; j<SPLICEE_ROWS; j++) {
			String results = strandSpliceBenchmark(ENZYME, splicee, strandType);
			System.out.println(results);
			splicee += splicee;
		}
	}
 
	/**
	 * Runs a single benchmarking test cutting and splicing mySource at
	 * every occurrence of enzyme and splicing in splicee. Runs TRIALS
	 * times and returns data from run. Relies on mySource being
	 * the original dna strand to cut and splice, already initialized.
	 * @param enzyme The string to cut/break mySource at
	 * @param splicee DNA to splice in at every occurrence of enzyme
	 * @param className String name of IDnaStrand implementation to use
	 * @return A formatted string containing the implementing class used, 
	 * size of the original strand, length of the splicee, length of the 
	 * recombinant strand after cutting & splicing, average time in 
	 * milliseconds to perform the cut and splice simulation, and the 
	 * number of occurrences of the enzyme / number of breaks.
	 * @throws Exception if className cannot be used to create an IDnaStrand
	 */
	public static String strandSpliceBenchmark(String enzyme, String splicee, String className)
			throws Exception {
		String dna = mySource;
		IDnaStrand strand;
		try {
			strand = (IDnaStrand) Class.forName(className).getDeclaredConstructor().newInstance();
			strand.initialize(dna);

			double before = System.nanoTime();
			IDnaStrand recomb = strand.cutAndSplice(enzyme, splicee);
			for (int i=0; i<TRIALS-1; i++) {
				strand.cutAndSplice(enzyme, splicee);
			}
			double after = System.nanoTime();

			long recLength = recomb.size();			
			String ret = String.format("%s:%,12d%,12d%,14d%,10d%,10d", 
				className.substring(0,10), strand.size(), 
				splicee.length(), recLength, (int) ((after-before) / (1E6*TRIALS)),
				(recomb.getAppendCount()-1)/2);
			return ret;
		} catch (ClassNotFoundException e) {
			return "could not create class " + className;
		}
	}

	

	/**
	 * Return a string representing the DNA read from the scanner, ignoring any
	 * characters can't be part of DNA and converting all characters to lower
	 * case.
	 * @param s is the Scanner read from
	 * @return a string representing the DNA read, characters in the returned
	 *         string are restricted to 'c', 'g', 't', 'a'
	 */
	public static String dnaFromScanner(Scanner s) {
		StringBuilder buf = new StringBuilder();
		while (s.hasNextLine()) {
			String line = s.nextLine().toLowerCase();
			for (int k = 0; k < line.length(); k++) {
				char ch = line.charAt(k);
				if ("acgt".indexOf(ch) != -1) {
					buf.append(ch);
				}
			}
		}
		return buf.toString();
	}

	/**
	 * Prints header for the data formatted by the 
	 * strandSpliceBenchmark method.
	 */
	public static void printHeader() {
		System.out.printf("dna length = %,d\n", mySource.length());
		System.out.println("cutting at enzyme " + ENZYME);
		System.out.printf("------------------------------------");
		System.out.printf("----------------------------------\n");
		System.out.printf("Class%18s%12s%14s%10s%10s\n",
			"dna,N", "splicee,S", "recomb", "time(ms)", "breaks,b");
			System.out.printf("------------------------------------");
			System.out.printf("----------------------------------\n");
	}

	public static void exhaustMemoryBenchmark() throws Exception {
		int startPower = 8;
		int endPower = 32;
		boolean firstRun = true;
		for (int j = startPower-1; j <= endPower; j++) {
			StringBuilder b = new StringBuilder("");
			int spSize = (int) Math.pow(2, j);
			for (int k = 0; k < spSize; k++) {
				b.append("c");
			}
			String splicee = b.toString();
			String results = strandSpliceBenchmarkII(ENZYME, splicee, strandType);
			if (! firstRun) {
				System.out.println(results);
			}
			else {
				firstRun = false;
			}
		}

	}

	private static String strandSpliceBenchmarkII(String enzyme, String splicee, String className)  throws Exception {
		String dna = mySource;
		IDnaStrand strand;
		try {
			strand = (IDnaStrand) Class.forName(className).getDeclaredConstructor().newInstance();
			strand.initialize(dna);
			long length = strand.size();
			IDnaStrand recomb = strand.cutAndSplice(enzyme, splicee);
			long length2 = strand.size();
			if (length != length2) {
				System.err.printf("trouble splicing %d strand to %d\n", length, length2);
			}
			long recLength = recomb.size();
			double time = 0;
			for (int i = 0; i < MEMORY_TRIALS; i++) {
				Thread thread = new Thread(() -> {
					strand.cutAndSplice(enzyme, splicee);
				});
				double start = System.nanoTime();
				thread.run();
				thread.join();
				time += (System.nanoTime() - start) / TRIALS / 1e9;
			}
			String ret = String.format("%s:\t%,15d\t%,15d\t%1.3f\t%d", className.substring(0,10), splicee.length(), recLength, time,
					recomb.getAppendCount());

			return ret;
		} catch (ClassNotFoundException e) {
			return "could not create class " + className;
		}
	}
}
