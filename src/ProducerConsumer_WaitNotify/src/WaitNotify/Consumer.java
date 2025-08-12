package WaitNotify;

public class Consumer extends Thread {
	private final Buffer buffer;
	private int consumerSpeed;
	
	public Consumer (Buffer buffer){
		this.buffer = buffer;
		this.consumerSpeed = 1000;
	}
	
	public void setConsumerSpeed(int itens, int milliseconds) {
		if (itens > 0 && milliseconds > 0)
			this.consumerSpeed = milliseconds/itens;
		else
			consumerSpeed = 1000;
	}
	
	@Override
	public void run(){
		while (!isInterrupted()) {
			try {
				Thread.sleep(consumerSpeed);
				buffer.consumer();
			} catch (InterruptedException e) {
				interrupt();
				e.printStackTrace();
			}
		}
	}
}