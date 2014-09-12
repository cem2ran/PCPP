import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicIntegerArray;

// For week 3
// sestoft@itu.dk * 2014-09-04

class SimpleHistogram {
	public static void main(String[] args) {
		// final Histogram histogram = new Histogram1(30);
		// histogram.increment(7);
		// histogram.increment(13);
		// histogram.increment(7);
		// dump(histogram);
		int range = 5_000_000;
		long start1 = System.nanoTime();
//		 dump();
		TestCountPrimes.countParallelN(range, 10, new Histogram2(range));
		long end1 = System.nanoTime();
		long start2 = System.nanoTime();
//		dump();
		TestCountPrimes.countParallelN(range, 10, new Histogram3(range));
		long end2 = System.nanoTime();
		
		System.out.println("using Histogram2: "+TimeUnit.MILLISECONDS.convert(end1-start1, TimeUnit.NANOSECONDS));
		System.out.println("using Histogram3: "+TimeUnit.MILLISECONDS.convert(end2-start2, TimeUnit.NANOSECONDS));
//		System.out.println("faster by: "+);
	}

	public static void dump(Histogram histogram) {
		int totalCount = 0;
		for (int item = 0; item < histogram.getSpan(); item++) {
			System.out.printf("%4d: %9d%n", item, histogram.getCount(item));
			totalCount += histogram.getCount(item);
		}
		System.out.printf("      %9d%n", totalCount);
	}

}

interface Histogram {
	public void increment(int item);

	public int getCount(int item);

	public int getSpan();
}

class Histogram1 implements Histogram {
	private int[] counts;

	public Histogram1(int span) {
		this.counts = new int[span];
	}

	public void increment(int item) {
		counts[item] = counts[item] + 1;
	}

	public int getCount(int item) {
		return counts[item];
	}

	public int getSpan() {
		return counts.length;
	}
}

class Histogram2 implements Histogram {
	// Doesn't need final, because it is not accessible from anywhere
	// (encapsulated)
	private int[] counts;

	public Histogram2(int span) {
		this.counts = new int[span];
	}

	// Needs to be synchronized because if potential race condition
	public synchronized void increment(int item) {
		counts[item] = counts[item] + 1;
	}

	// Needs to be synchronized because if potential race condition
	public synchronized int getCount(int item) {
		return counts[item];
	}

	// does not need to be synchronized, because it is considered effectively
	// immutable
	public int getSpan() {
		return counts.length;
	}
}

class Histogram3 implements Histogram{

	private AtomicIntegerArray counts;
	
	public Histogram3(int span){
		counts = new AtomicIntegerArray(span);
	}

	public void increment(int item) {
		counts.incrementAndGet(item);
	}

	public int getCount(int item) {
		return counts.get(item);
	}

	public int getSpan() {
		return counts.length();
	}
}

class TestCountPrimes {
	// General parallel solution, using multiple threads
	public static Histogram countParallelN(int range, int threadCount, final Histogram histogram) {
		final int perThread = range / threadCount;

		Thread[] threads = new Thread[threadCount];
		for (int t = 0; t < threadCount; t++) {
			final int from = perThread * t, to = (t + 1 == threadCount) ? range
					: perThread * (t + 1);
			threads[t] = new Thread(new Runnable() {
				public void run() {
					for (int i = from; i < to; i++) 
						histogram.increment(countFactors(i));
				}
			});
		}
		for (int t = 0; t < threadCount; t++)
			threads[t].start();
		try {
			for (int t = 0; t < threadCount; t++)
				threads[t].join();
		} catch (InterruptedException exn) {
		}
		return histogram;
	}

	public static int countFactors(int p) {
		if (p < 2)
			return 0;
		int factorCount = 1, k = 2;
		while (p >= k * k) {
			if (p % k == 0) {
				factorCount++;
				p /= k;
			} else
				k++;
		}
		return factorCount;
	}
}