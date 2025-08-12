package WaitNotify;

public class Producer extends Thread {
	private final Buffer buffer;
	private int itens;
	private int producerSpeed;
	private int i = 0;
		
	public Producer(Buffer buffer){
		this.buffer = buffer;
		this.itens = 10;
		this.producerSpeed = 1000;
	}
	
	public void setProducerSpeed (int itens, int milliseconds) {
		if (itens > 0 && milliseconds > 0)
			this.producerSpeed = milliseconds/itens;
		else 
			producerSpeed = 1000;
	}
	
	public void setItensProduction (int itens) {
		this.itens = itens;
	}
	
	@Override
	public void run(){
		while (!isInterrupted()) {
			if (i < itens) {
				try {			
					long item = System.currentTimeMillis();
					Thread.sleep(producerSpeed);
					buffer.producer(item);
					i++;
				} catch (InterruptedException e) {
					interrupt();
					e.printStackTrace();
				} 
			}
		}
	}
}
