package emailUtils;

import javax.mail.Message;
import java.util.Date;

public class RetrievedEmail
{

	private String subject;
	private String content;
	Date receivedDate;
	public Message message;

	public RetrievedEmail(String subject, String content, Date receivedDate, Message message) 
	{
		this.subject = subject;
		this.content = content;
		this.receivedDate = receivedDate;
		this.message = message;
	}

	public RetrievedEmail() {
	}


	public Message getMessage() {
		return message;
	}

	public String getSubject() {
		return subject;
	}

	public void setMessage(Message message) {
		this.message = message;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}


	public String getContent() {
		return content;
	}


	public void setContent(String content) {
		this.content = content;
	}


	public Date getReceivedDate() {
		return receivedDate;
	}


	public void setReceivedDate(Date receivedDate) {
		this.receivedDate = receivedDate;
	}
	
}
