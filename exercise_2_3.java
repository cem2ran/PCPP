import java.util.concurrent.atomic.AtomicInteger;

public class exercise_2_3 {

	public static void main(String[] args) {
		/***
		 * 1.
		 */
		final int range = 5_000_000;
		int count = sequentialPSum(0, range);
		System.out.printf("Total number of factors is %9d%n", count);
		
		/***
		 * 2.3.3
		 */
		MyAtomicInteger counter = new MyAtomicInteger();
		System.out.println(parallelPSum(range, 10, counter));
		
		/***
		 * 2.3.5
		 */
		AtomicIntegerClone counter2 = new AtomicIntegerClone();
		System.out.println(parallelPSum(range, 10, counter2));
	}

	public static int sequentialPSum(int from, int to) {
		int count = 0;
		for (;from < to; from++)
			count += countFactors(from);
		return count;
	}
	
	public static long parallelPSum(int range, int threadCount, final Counter counter){
		/* final (not necessary)*/ 
		Thread[] threads = new Thread[threadCount];
		
		final int rangePerThread = (int) Math.floor(range / threadCount);
		
		
		for (int i = 0; i < threadCount; i++) {
			final int i2 = i;
			threads[i] = new Thread(new Runnable() {
				@Override
				public void run() {
					int from = i2 * rangePerThread;
					int to = (i2+1) * rangePerThread;
					counter.addAndGet((sequentialPSum(from, to)));
				}
			});
			
		}
		
		for (Thread thread : threads) {
			thread.start();
		}
		for (Thread thread : threads) {
			try {
				thread.join();
			} catch (InterruptedException e) {}
		}
		
		return counter.get();
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

interface Counter {
	int addAndGet(int amount);
	
	int get();
}

class AtomicIntegerClone extends AtomicInteger implements Counter{}


class MyAtomicInteger implements Counter{
	private int i;
	
	public synchronized int addAndGet(int amount){
		i += amount;
		return i;
	}
	
	public synchronized int get(){
		return i;
	}
}