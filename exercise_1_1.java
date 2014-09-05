import java.io.IOException;

public class exercise_1_1 {

	public static void main(String[] args) throws IOException {
		/**
		 * 1. We will always get values lower than 20.000.000 as the operations
		 * in the threads are not synchronized i.e. lost updates occur.
		 * 
		 * Count is 10634462 and should be 20000000 Count is 11443119 and should
		 * be 20000000
		 */
		final int count1 = 10_000_000;
		printResult(countTo(count1), count1 * 2);

		/***
		 * 2. As it still is not synchronized the result should in theory adhere
		 * to the same principles of lost updates. As we are running fewer
		 * operations the probability is lowered.
		 */
		for (int i = 0; i < 1000; i++) {
			int result = countTo(100);
			if (result != 200) {
				printResult(result, 200);
			}
		}

		/***
		 * 3. The different expressions do not make a difference as they all
		 * perform read-modify-write operations. As an alternative one could use
		 * an AtomicInteger.
		 */

		/***
		 * 4. The result should be 0, as we increment the same amount as we
		 * decrement. Same problem arises as previously and we end up with non 0
		 * numbers, even negative.
		 * 
		 * Count is -14760 and should be 0
		 */

		final LongCounter counter = new LongCounter();
		Thread t_increment = constructCounterThread(counter, count1,
				OPERATION.INCREMENT);
		Thread t_decrement = constructCounterThread(counter, count1,
				OPERATION.DECREMENT);
		t_increment.start();
		t_decrement.start();
		try {
			t_increment.join();
			t_decrement.join();
		} catch (InterruptedException exn) {
			System.out.println("Some thread was interrupted");
		}
		printResult(counter.get(), 0);

		System.out.println("Exercise 1.1.5:");
		/***
		 * 5.i
		 * 
		 * 
		 */
		LongCounter lc_1 = new LongCounter();
		Thread t1 = constructCounterThread(lc_1, count1, OPERATION.INCREMENT);
		t1.start();
		try {
			t1.join();
		} catch (Exception e) {
		}
		printResult(lc_1.get(), count1);

		/***
		 * 5.ii
		 * 
		 * 
		 */
		LongCounter lc_2 = new SynchronizedLongCounter();
		Thread t2 = constructCounterThread(lc_2, count1, OPERATION.DECREMENT);
		t2.start();
		try {
			t2.join();
		} catch (Exception e) {
		}
		printResult(lc_2.get(), -count1);

		/***
		 * 5.iii
		 * 
		 * 
		 */
		LongCounter lc_3 = new SynchronizedLongCounter();
		Thread t3 = constructCounterThread(lc_3, count1, OPERATION.INCREMENT);
		t3.start();
		try {
			t3.join();
		} catch (Exception e) {
		}
		printResult(lc_3.get(), count1);

		/***
		 * 5.iv
		 * 
		 * 
		 */
		LongCounter lc_4 = new SynchronizedLongCounter();
		Thread t4_INCREMENT = constructCounterThread(lc_4, count1,
				OPERATION.INCREMENT);
		Thread t4_DECREMENT = constructCounterThread(lc_4, count1,
				OPERATION.DECREMENT);
		t4_INCREMENT.start();
		t4_DECREMENT.start();
		try {
			t4_INCREMENT.join();
			t4_DECREMENT.join();
		} catch (Exception e) {
		}
		printResult(lc_4.get(), count1 - count1);
		
		

		
	}

	enum OPERATION {
		INCREMENT, DECREMENT
	}

	private static Thread constructCounterThread(final LongCounter counter,
			final int X, final OPERATION op) {
		return new Thread(new Runnable() {
			public void run() {
				for (int i = 0; i < X; i++)
					if (op == OPERATION.INCREMENT)
						counter.increment();
					else
						counter.decrement();
			}
		});
	}

	private static int countTo(final int counts) {
		final LongCounter lc = new LongCounter();
		Thread t1 = constructCounterThread(lc, counts, OPERATION.INCREMENT);
		Thread t2 = constructCounterThread(lc, counts, OPERATION.INCREMENT);
		t1.start();
		t2.start();
		try {
			t1.join();
			t2.join();
		} catch (InterruptedException exn) {
			System.out.println("Some thread was interrupted");
		}
		return (int) lc.get();

	}

	private static void printResult(long result, long expected) {
		System.out.println("Count is " + result + " and should be " + expected);
	}
}

class SynchronizedLongCounter extends LongCounter {
	public synchronized void increment() {
		super.increment();
	}

	public synchronized void decrement() {
		super.decrement();
	}
}

class LongCounter {
	protected long count = 0;

	public void increment() {
		count = count + 1;
	}

	public void decrement() {
		count--;
	}

	public synchronized long get() {
		return count;
	}
}