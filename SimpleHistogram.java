import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
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
		Histogram hist2 = new Histogram2(range);
		TestCountPrimes.countParallelN(range, 10, hist2);
		long end1 = System.nanoTime();
		// dump(hist2);
		long start2 = System.nanoTime();
		Histogram hist3 = new Histogram3(range);
		TestCountPrimes.countParallelN(range, 10, hist3);
		long end2 = System.nanoTime();
		 dump(hist3);

		long start3 = System.nanoTime();
		Histogram hist4 = new Histogram4(range);
		TestCountPrimes.countParallelN(range, 10, hist4);
		long end3 = System.nanoTime();
		
		System.out.println("using Histogram2: "
				+ TimeUnit.MILLISECONDS.convert(end1 - start1,
						TimeUnit.NANOSECONDS));
		System.out.println("using Histogram3: "
				+ TimeUnit.MILLISECONDS.convert(end2 - start2,
						TimeUnit.NANOSECONDS));
		System.out.println("using Histogram4: "
				+ TimeUnit.MILLISECONDS.convert(end3 - start3,
						TimeUnit.NANOSECONDS));
	}

	public static void dump(Histogram histogram) {
		int totalCount = 0;
		for (int item = 0; item < 30; item++) {
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

	public int[] getBuckets();
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

	@Override
	public int[] getBuckets() {
		return counts;
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

	@Override
	public int[] getBuckets() {
		// a snapshot. Should not be manipulated
		// while cloning, thus the method is synchronized
		synchronized (counts) {
			return counts.clone(); 
		}
	}
}

// Exercise 3.1.3
// we can remove the synchronization because the counts item is not exposed and
// operations are done atomically in each of the AtomicInter instances
class Histogram3 implements Histogram {

	private ArrayList<AtomicInteger> counts;

	public Histogram3(int span) {
		counts = new ArrayList<AtomicInteger>();
		for (int i = 0; i < span; i++) {
			counts.add(new AtomicInteger(0));
		}
	}

	public void increment(int item) {
		counts.get(item).incrementAndGet();
	}

	public int getCount(int item) {
		return counts.get(item).intValue();
	}

	public int getSpan() {
		return counts.size();
	}

	@Override
	public int[] getBuckets() {
		// since the span won't change, we can simply copy - allowing updates to
		// items that have not been copied yet.
		int[] buckets = new int[getSpan()];
		for (int i = 0; i < buckets.length; i++) {
			buckets[i] = getCount(i);
		}
		return buckets;
	}
}

// Exercise 3.1.4
class Histogram4 implements Histogram {

	private AtomicIntegerArray counts;

	public Histogram4(int span) {
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

	@Override
	public int[] getBuckets() {
		// since the span won't change, we can simply copy - allowing updates to
		// items that have not been copied yet.
		int[] buckets = new int[getSpan()];
		for (int i = 0; i < buckets.length; i++) {
			buckets[i] = getCount(i);
		}
		return buckets;
	}
}

class TestCountPrimes {
	// General parallel solution, using multiple threads
	public static Histogram countParallelN(int range, int threadCount,
			final Histogram histogram) {
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