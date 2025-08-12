package WaitNotify;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Buffer {
   private ArrayBlockingQueue<Long> buffer;
   private int bufferSize;
   private float bufferUsage;
   private ReentrantLock lock = new ReentrantLock(true);
   private Condition notEmpty = lock.newCondition();
   private Condition notFull = lock.newCondition();
   private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
   private LocalTime time;
   private final MainGUI gui;
   
   public Buffer(int bufferSize, MainGUI gui) {
       this.buffer = new ArrayBlockingQueue<>(bufferSize);
       this.bufferSize = bufferSize;
       this.bufferUsage = 0;
       this.gui = gui;
   }
   
   public void setBufferSize(int bufferSize) {
	   this.bufferSize = bufferSize;
   }
   
   private void setBufferUsage(){
	   bufferUsage = ((float) buffer.size()/bufferSize) * 100;
   }
   
   public float getBufferUsage() {
	   return bufferUsage;
   }
   
   public void producer(long item) throws InterruptedException {
       lock.lock();
       
       try {
           while (buffer.size() == bufferSize) {
        	   time = LocalTime.now();
        	   gui.appendProducerText("<" + time.format(formatter) + "> PRODUCER WAITING (BUFFER FULL)");
               notFull.await();
           }
           
           buffer.put(item);
           setBufferUsage();
    	   time = LocalTime.now();
    	   gui.appendProducerText("<" + time.format(formatter) + "> " + Thread.currentThread().getName() + " Produced " + item + " item");
           
           notEmpty.signalAll();
       } finally {
    	   lock.unlock();
       }
   }
   
   public void consumer() throws InterruptedException {
       lock.lock();
       
       try {
           while (buffer.size() == 0){ 
        	   time = LocalTime.now();
        	   gui.appendConsumerText("<" + time.format(formatter) + "> CONSUMER WAITING (BUFFER EMPTY)");
               notEmpty.await();
           }
           
           long item = buffer.take();
           setBufferUsage();
    	   time = LocalTime.now();
    	   gui.appendConsumerText("<" + time.format(formatter) + "> " + Thread.currentThread().getName() + " Consumed " + item + " item");
           
           notFull.signalAll();
       } finally {
           lock.unlock();
       }
   }
}