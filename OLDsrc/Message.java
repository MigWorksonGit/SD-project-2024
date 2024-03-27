import java.io.*;

public class Message implements Serializable
{
	public String action;
	public String msg;

	public Message(String text, String msg) {
		this.action = text;
		this.msg = msg;
	}

	public String getAction() {
		return action;
	}

	public String getMessage() {
		return msg;
	}
}