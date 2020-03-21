package Sincronizado;

import java.security.SecureRandom;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
interface Buffer {

	public void blockingPut(int value) throws InterruptedException;

	public int blockingGet() throws InterruptedException;
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

class SynchronizedBuffer implements Buffer {
	private int buffer = -1; 
	private boolean occupied = false; 
	
	public synchronized void blockingPut (int value) 
		throws InterruptedException
		{
		while (occupied)
		{
			System.out.println("Producer tries to write");
			displayState("Buffer full. Producer Waits");
			wait();
		}
		buffer = value; 
		
		occupied = true; 
		
		displayState("Producer writes " + buffer);
		
		notifyAll();
		}
	public synchronized int blockingGet() throws InterruptedException
	{
		while(!occupied)
		{
		System.out.println("Consumer tries to read.");	
		displayState("Buffer empty. Consumer waits");
		wait();
		}
		occupied = false; 
		
		displayState("Consumer reads " + buffer); 
		
		notifyAll();
		return buffer; 	
	}	
	private synchronized void displayState(String operation)
	{
		System.out.printf("%-40s%d\t\t%b%n%n", operation, buffer,
				occupied);
	}
}
public class PruebaSincronizado {
	public static void main(String[] args) throws InterruptedException {
	ExecutorService executorService = Executors.newCachedThreadPool();
	Buffer sharedLocalitation = new SynchronizedBuffer(); 
	System.out.printf("%-40s%s\t\t%s%n%-40s%s%n%n", "Operation",
			"Buffer", "Occupied", "--------", "------\t\t------");
	executorService.execute(new Productor(sharedLocalitation));
	executorService.execute(new Consumer(sharedLocalitation));	
	executorService.shutdown();
	executorService.awaitTermination(1, TimeUnit.MINUTES);
	}
}