package datasegment;

import java.nio.ByteBuffer;
import java.util.BitSet;
import java.util.Timer;

public class DataSegment {

	/**
	 * DataSegment's attributes MSS: maximum segment size appData: data field of
	 * a segment seqNum: sequence number of the segment ackNum: acknowledgement
	 * number of the segment rwnd: the length of receive window in the receiving
	 * side SYN: is set to 'true' if the segment is used for handshaking purpose
	 * ACK: all packets after the initial SYN packet sent by the client should
	 * have this flag set. FIN: indicates 'no more data from sender'
	 */

	private final int MSS = 1024;
	private byte[] appData;
	private int seqNum;
	private int ackNum;
	private int rwnd;
	private boolean SYN;
	private boolean ACK;
	private boolean FIN;

	public Timer timer;

	public DataSegment(int seqNum, int ackNum, int receiveWindow, boolean SYN, boolean ACK, boolean FIN, byte[] data) {
		this.seqNum = seqNum;
		this.ackNum = ackNum;
		this.SYN = SYN;
		this.ACK = ACK;
		this.FIN = FIN;
		appData = new byte[MSS];
		appData = data;
		// timer = new Timer();
	}

	public DataSegment(byte[] inByteStream) {
		appData = new byte[MSS];
		getData(inByteStream);
		// timer = new Timer();
	}

	public int getSeqNum() {
		return seqNum;
	}

	public void setSeqNum(int seqNum) {
		this.seqNum = seqNum;
	}

	public int getAckNum() {
		return ackNum;
	}

	public void setAckNum(int ackNum) {
		this.ackNum = ackNum;
	}

	public byte[] getAppData() {
		return appData;
	}

	public void setAppData(byte[] appData) {
		this.appData = appData;
	}

	public boolean isSYN() {
		return this.SYN;
	}

	public void setSYN(boolean SYN) {
		this.SYN = SYN;
	}

	public boolean isACK() {
		return this.ACK;
	}

	public void setACK(boolean ACK) {
		this.ACK = ACK;
	}

	public boolean isFIN() {
		return this.FIN;
	}

	public void setFIN(boolean FIN) {
		this.FIN = FIN;
	}

	public byte[] toByteArray() {
		ByteBuffer byteArray = ByteBuffer.allocate(13 + MSS);
		byteArray.putInt(seqNum);
		byteArray.putInt(ackNum);
		byteArray.putInt(rwnd);

		BitSet bitArr = new BitSet(8); // a byte used to store FIN, SYN, ACK
		bitArr.set(0, ACK); // bitArr[0] = ACK
		bitArr.set(1, SYN);
		bitArr.set(2, FIN);

		byteArray.put(bitArr.toByteArray()); // put these bits to a byte in the
												// array
		byteArray.put(appData); // put data to the array

		return byteArray.array();
	}

	private void getData(byte[] inStream) {
		byte[] seqArr = new byte[4];
		byte[] ackArr = new byte[4];
		byte[] rwndArr = new byte[4];

		/*
		 * Re - calculate seqNum, ackNum, rwnd from the inStream
		 */
		for (int i = 0; i < 4; i++) {
			seqArr[i] = inStream[i];
			ackArr[i] = inStream[i + 4];
			rwndArr[i] = inStream[i + 8];
		}

		ByteBuffer wrap;
		wrap = ByteBuffer.wrap(seqArr);
		seqNum = wrap.getInt();
		wrap = ByteBuffer.wrap(ackArr);
		ackNum = wrap.getInt();
		wrap = ByteBuffer.wrap(rwndArr);
		rwnd = wrap.getInt();

		/*
		 * Set the ACK, SYN, FIN bits from the inStream
		 */
		ACK = ((inStream[12] & 1) != 0);
		SYN = ((inStream[12] & 2) != 0);
		FIN = ((inStream[12] & 4) != 0);

		/*
		 * get the data field
		 */
		for (int i = 0; i < MSS; i++) {
			appData[i] = inStream[13 + i];
		}
	}

	public String toString() {
		// Convert data field to string
		String appDataString = new String(appData);
		return appDataString;
	}

}
