package Producer;

import Message.Message;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ArrayBlockingQueue;

import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.blocks.RequestOptions;
import org.jgroups.blocks.RpcDispatcher;
import org.jgroups.util.NameCache;
import org.jgroups.util.Util;

public class Producer extends Thread{
	private JChannel channel;
	private int mailboxSize;
	private ArrayBlockingQueue<Message> mailbox;
	private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
	private LocalTime time;
	private RpcDispatcher dispatcher;
	private Address consumerAddress = null;
	private int producerSpeed;
	private int messageId;
	private ProducerGUI gui;
	
	public Producer (int mailboxSize, ProducerGUI gui) {
		this.mailboxSize = mailboxSize;
		this.mailbox = new ArrayBlockingQueue<>(mailboxSize);
		this.producerSpeed = 1000;
		this.messageId = 0;
		this.gui = gui;
	}
	
	public void setProducerSpeed(int itens, int milliseconds) {
		if (itens > 0 && milliseconds > 0)
			this.producerSpeed = milliseconds/itens;
		else
			producerSpeed = 1000;
	}
	
	private void connectJGroups() throws Exception {
		channel = new JChannel();
		channel.setName("Producer");
		channel.connect("ProducerConsumerCluster");
		dispatcher = new RpcDispatcher(channel, this);
	}
	
	public int getMailboxSize() {
		return mailboxSize;
	}
	
	public synchronized boolean messagePut(Message message) throws InterruptedException {
		mailbox.put(message);
		return true;
	}
	
	public Message messageTake() throws InterruptedException {
		Message temp = mailbox.take();
		if (temp != null) {
			time = LocalTime.now();
		}
		return temp;
	}
	
	private void connectConsumer() {
		while (consumerAddress == null) {
        	for (Address member : channel.getView().getMembers()) {
                String memberName = NameCache.get(member);
                if ("Consumer".equals(memberName)) {
                    consumerAddress = member;
                    break;
                } 
            }
        }
	}
	
	private void sendFullMessage(Message message) throws Exception {
		dispatcher.callRemoteMethod(
	  			   consumerAddress, 
	  			   "messagePut", 
	  			   new Object[]{message},
	  			   new Class[]{Message.class}, 
	  			   RequestOptions.SYNC()
	  			   );
		time = LocalTime.now();
		gui.appendOutputText("<"+time.format(formatter)+"> (Produced Message) ID: "+message.getMessageId()+" | Message: "+message.getMessage());
	}
	
	@Override
	public void run() {
		try {
			connectJGroups();
			connectConsumer();
			
			while(true) {
				long message = System.currentTimeMillis();
				Message temp = messageTake();
				temp.setMessageId(messageId++);
				temp.setMessage(message);
				sendFullMessage(temp);
				Thread.sleep(producerSpeed);
			} 
		} catch (InterruptedException e) {
			Util.close(dispatcher, channel);
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			channel.close();
		}
	}
	
	/*public static void main(String[] args) {
		Producer producer = new Producer(10);
		
		producer.setProducerSpeed(1, 1000);
		
		producer.start();
	}*/
}