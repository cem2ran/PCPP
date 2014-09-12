public class exercise_1_2 {

	public static void main(String[] args) {
		/**
		 * 1. writing doTwice and print "Hello, World!" twice
		 */
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				printString("Hello, World!");
			}
		};
		System.out.println("----1----");
		doTwice(runnable);

		/**
		 * 2. write doNTimes
		 */
		/**
		 * 3. call doNTimes with n=14
		 */
		System.out.println("----3----");
		doNTimes(runnable, 14);

		/**
		 * 4. write do14Times(s) that uses doNTimes
		 */
		System.out.println("----4----");
		write14Times("write something 14 times");
		
		/**
		 * 5. OPTIONAL
		 * use lambda with java 8
		 */
//		Runnable r = () -> System.out.println("rgrg");;
	}

	public static void doTwice(Runnable r) {
		r.run();
		r.run();
	}

	public static void doNTimes(Runnable r, int n) {
		for (int i = 0; i < n; i++) {
			r.run();
		}
	}
	
	public static void write14Times(final String s){
		Runnable r = new Runnable() {
			@Override
			public void run() {
				printString(s);
			}
		};
		doNTimes(r, 14);
	}

	public static void printString(String str) {
		System.out.println(str);
	}
}
