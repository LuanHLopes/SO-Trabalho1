package Message;

import java.io.Serializable;

public class Message implements Serializable{
	private static final long serialVersionUID = 1L;
	private int messageId;
	private long message;
	private boolean isEmpty;
	
	public Message(int messageId) {
		this.messageId = messageId;
		this.isEmpty = true;		
	}
	
	public void setMessage(long message) {
		this.message = message;
		this.isEmpty = false;
	}
	
	public long getMessage() {
		return message;
	}
	
	public void setMessageId(int messageId) {
		this.messageId = messageId;
	}
	
	public int getMessageId() {
		return messageId;
	}
	
	public boolean isEmpty() {
		return isEmpty;
	}
}
