import java.io.*;

public class OLDMessage implements Serializable
{
	public String action;
	public String msg;

	public OLDMessage(String text, String msg) {
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