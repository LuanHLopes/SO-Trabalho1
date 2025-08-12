package Consumer;

import Message.Message;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ArrayBlockingQueue;

import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.blocks.RequestOptions;
import org.jgroups.blocks.RpcDispatcher;
import org.jgroups.util.NameCache;

public class Consumer extends Thread{
	private JChannel channel;
	public static RpcDispatcher dispatcher;
	private int mailboxSize;
	private ArrayBlockingQueue<Message> mailbox;
	private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
	private LocalTime time;
	private Address producerAddress = null;
	private int consumerSpeed;
	private ConsumerGUI gui;
	
	public Consumer (ConsumerGUI gui) {
		this.consumerSpeed = 0;
		this.gui = gui;
	}
	
	public void setConsumerSpeed(int itens, int milliseconds) {
		if (itens > 0 && milliseconds > 0)
			this.consumerSpeed = milliseconds/itens;
		else
			consumerSpeed = 1000;
	}
	
	private void setMailboxSize (int mailboxSize) {
		this.mailbox = new ArrayBlockingQueue<>(this.mailboxSize);
	}
	
	public synchronized void messagePut(Message message) throws InterruptedException {
		mailbox.put(message);
	}
	
	private Message messageTake() throws InterruptedException {
		Message temp = mailbox.take();
		return temp;
	}
	
	private void connectJGroups() throws Exception {
		channel = new JChannel();
		channel.setName("Consumer");
		channel.connect("ProducerConsumerCluster");
		dispatcher = new RpcDispatcher(channel, this);
	}
	
	private void connectProducer() {
		while (producerAddress == null) {
        	for (Address member : channel.getView().getMembers()) {
                String memberName = NameCache.get(member);
                if ("Producer".equals(memberName)) {
                    producerAddress = member;
                    break;
                } 
            }
        }
	}
	
	private void sendEmptyMessage() throws Exception {
		Message message = new Message();
		boolean sent = dispatcher.callRemoteMethod(
		  			   producerAddress, 
		  			   "messagePut", 
		  			   new Object[]{message},
		  			   new Class[]{Message.class}, 
		  			   RequestOptions.SYNC()
		  			   );
		if (sent) {
			time = LocalTime.now();
			gui.appendOutputText("<"+time.format(formatter)+"> Empty message block sent to producer");
		}
	}
	
	@Override
	public void run() {
		try {
			connectJGroups();
			connectProducer();
			mailboxSize = dispatcher.callRemoteMethod(
		    			  producerAddress, 
		    			  "getMailboxSize", 
		    			  new Object[]{},
		    			  new Class[]{}, 
		    			  RequestOptions.SYNC()
		    			  );
			setMailboxSize(mailboxSize);
			
			for (int i = 0; i < mailboxSize; i++) {
				sendEmptyMessage();
			}
			while (true) {
					Message temp = messageTake();
					time = LocalTime.now();
					gui.appendOutputText("<"+time.format(formatter)+"> (Consumed Message) ID: "+temp.getMessageId()+" | Message: "+temp.getMessage());
					sendEmptyMessage();
					Thread.sleep(consumerSpeed);
			} 
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			channel.close();
		}
	}
	
	/*public static void main(String[] args) {
		Consumer consumer = new Consumer();
		
		consumer.setConsumerSpeed(2, 1000);
		
		consumer.start();
	}*/
}