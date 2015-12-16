package utils;

public class Constants {
	
	public static final int N = 30;
	public static final int SIZE_DATA = 1024;
	public static final int SIZE_HEADER = 5;
	public static final int SIZE_SEGMENT = SIZE_DATA + SIZE_HEADER;
	public static final int TIME_OUT = 40;
	public static final int SIZE_BUFFER = 20 * 1024 * 1024; // 20 MB
	public static final String ACTION_SEND = "SendAction";
	public static final String ACTION_RECEIVE = "ReceiveAction";
	public static final String ACTION_RECEIVE_ACK = "ReceiveAckAction";
	public static final String ACTION_RESEND = "ResendAction";

}
