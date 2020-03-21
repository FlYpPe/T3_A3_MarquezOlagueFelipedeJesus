import java.security.SecureRandom;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

interface Buffer {

	public void blockingPut(int value) throws InterruptedException;

	public int blockingGet() throws InterruptedException;
}

class BlockingBuffer implements Buffer {

	@Override
	public void blockingPut(int value) throws InterruptedException {
		

	}

	@Override
	public int blockingGet() throws InterruptedException {
		
		return 0;
	}

}

class Productor implements Runnable {

	private static final SecureRandom generator = new SecureRandom();
	private Buffer sharedLocation;

	public Productor(Buffer sharedLocation) {
		this.sharedLocation = sharedLocation;
	}

	@Override
	public void run() {

		int sum = 0;

		for (int i = 1; i <= 10; i++) {
			try {
				Thread.sleep(generator.nextInt(3000));
				sharedLocation.blockingPut(i);
				sum += i;
				System.out.printf("\t%2d%n", sum);

			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}

		System.out.printf("Producer done producing%n Terminaating Producer%n");

	}

}

class Consumer implements Runnable {
	private static final SecureRandom generator = new SecureRandom();
	private Buffer sharedLocation;

	public Consumer(Buffer sharedLocation) {
		this.sharedLocation = sharedLocation;

	}

	@Override
	public void run() {
		int sum = 0;

		for (int i = 1; i <= 10; i++) {
			try {
				Thread.sleep(generator.nextInt(3000));
				sum += sharedLocation.blockingGet();
				System.out.printf("\t\t\t%2d%n", sum);

			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
		System.out.printf("%n%s %d%n%s%n", "Consumer read values totaling", sum, "Terminating Consumer");

	}

}

class UnsynchronizedBuffer implements Buffer {

	private int buffer = -1;

	public void blockingPut(int value) throws InterruptedException {
		System.out.printf("Producer writes\t%2d", value);
		buffer = value;
	}

	
	public int blockingGet() throws InterruptedException {
		System.out.printf("Consumer reads\t%2d", buffer);
		return buffer;
	}
}

public class Prueba {

	public static void main(String[] args) {

		ExecutorService executorService = Executors.newCachedThreadPool();
		Buffer sharedLocation = new UnsynchronizedBuffer();
		
		System.out.println("Action\t\tValue\tSum of Produced\tSum of Consumed");
		System.out.printf("------\t\t-----\t---------------\t---------------%n%n");

		
		executorService.execute(new Productor(sharedLocation));
		executorService.execute(new Consumer(sharedLocation));


		executorService.shutdown();
		try {
			executorService.awaitTermination(1, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
