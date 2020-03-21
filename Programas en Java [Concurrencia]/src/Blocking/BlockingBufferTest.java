package Blocking;

import java.security.SecureRandom;
import java.util.concurrent.ArrayBlockingQueue;
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

class BlockingBuffer implements Buffer
{
	private final ArrayBlockingQueue<Integer> buffer; 
	
	public BlockingBuffer()
	{
		buffer = new ArrayBlockingQueue<Integer>(1);	
	}
	
	public void blockingPut(int value) throws InterruptedException
	{
		buffer.put(value);
		System.out.printf("%s%2d\t%s%d%n", "Productor Escribe ", value,
				"Buffer celulas ocupadas: ", buffer.size());
	}
	public int blockingGet() throws InterruptedException
	{
		int readValue = buffer.take(); 
		System.out.printf("%s %2d\t%s%d%n", "Lectura de Consumo ",
				readValue, "Buffer celulas ocupadas: ", buffer.size());
		return readValue;
	}
	
}

public class BlockingBufferTest {

	public static void main(String[] args) throws InterruptedException {
		
		ExecutorService executorService = Executors.newCachedThreadPool(); 
		
		Buffer sharedLocation = new BlockingBuffer(); 
		
		executorService.execute(new Productor(sharedLocation));
		executorService.execute(new Consumer(sharedLocation));
		
		executorService.shutdown();
		executorService.awaitTermination(1, TimeUnit.MINUTES); 
	}

}
