public class exercise_1_3 {

	public static void main(String[] args) {
		
		/***
		 * 1. 
		 */
		final Printer p1 = new Printer();
		p1.printForever().start();
		p1.printForever().start();
		
		
		/***
		 * 2. 
		 */
		final Printer p2 = new SynchronizedPrinter();
		p2.printForever().start();
		p2.printForever().start();
		
	}
	


}

class SynchronizedPrinter extends Printer{
	@Override
	public synchronized void print() {
		super.print();
	}
}

class Printer {
	boolean dashPrinted = false;

	public void print() {
		System.out.print(dashPrinted ? "|" : "-");
		dashPrinted = !dashPrinted;
	}
	
	public Thread printForever(){
		return new Thread(new Runnable() {
			public void run() {
				for(;;)
					print();
			}
		});
	}

}