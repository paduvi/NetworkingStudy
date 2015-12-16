package app;

import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

import action.Handshake;
import action.ReceiveAck;
import action.Send;
import datasegment.SplitFile;
import utils.Buffer;
import utils.Constants;

public class Client {

	/**
	 * 
	 * clientSocket: Socket used by client to communicate with the server
	 */

	// private byte[] packet;
	private int maxAcked = -1;
	private Send sendAction;
	private ReceiveAck receiveAck;

	// private int idPacket;
	private String fileName;
	private final Buffer buffer = new Buffer(Constants.SIZE_BUFFER);
	private byte[][] packets;
	private int numPacket;
	private byte[] data;
	private OutputStream outputStream = new TCPOutputStream();

	private int base = 0;
	private int nextSeq = 0;

	private Semaphore semaphore = new Semaphore(0);

	private ArrayList<Timer> timers = new ArrayList<>();

	private ArrayList<DatagramPacket> datagramPackets = new ArrayList<>();

	public void setData(byte[] data) {
		this.data = data;
	}

	public byte[] getData() {
		return data;
	}

	public Client(String serverIp, int connectPort, int welcomePort, String name) throws IOException {
		try {
			// Initialize
			Handshake handshake = new Handshake(serverIp, connectPort, welcomePort);
			this.sendAction = new Send(serverIp, connectPort);
			this.receiveAck = new ReceiveAck(sendAction.getClientSocket());
			this.fileName = name;

			// handshake three-way
			handshake.executeClient();
		} catch (Exception e) {
			e.printStackTrace();
		}
		init();
	}

	public void init() throws IOException {

		SplitFile f = new SplitFile(Files.readAllBytes(Paths.get(fileName)));
		f.splitFile();
		int allPackets = f.getNumPacket();
		new Thread(() -> {
			while (true) {

				try {
					receiveAck.execute();
					doAction(Constants.ACTION_RECEIVE_ACK);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if ((maxAcked + 1) == allPackets) {
					System.out.println("The Data is successfully transfer!");
					break;
				}
			}
		}).start();
	}

	public void transfer(byte[] data) throws Exception {
		SplitFile split = new SplitFile(data);
		this.packets = split.splitFile();
		this.numPacket = split.getNumPacket();
		System.out.println("Number Packet: " + numPacket);

		for (int i = 0; i < numPacket; i++) {
			if (nextSeq >= base + Constants.N) {
				semaphore.acquire();
			}
			sendAction.setData(getNextSeq(), packets[i]);
			doAction(Constants.ACTION_SEND);
		}
		stopTimer();
		// nextBufSema.acquire();
	}

	public synchronized void startTimer() {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				// timeOut = new TimeOutAction(getBase(), datagramPackets,
				// sendAction.getClientSocket());
				try {
					doAction(Constants.ACTION_RESEND);
					startTimer();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}, Constants.TIME_OUT);

		timers.add(timer);
	}

	public synchronized void stopTimer() {
		for (Timer timer : timers) {
			timer.cancel();
		}

		timers.clear();
	}

	public synchronized void doAction(String action) throws IOException {
		if (action == Constants.ACTION_SEND) {
			sendAction.execute();
			System.out.println("The packet " + getNextSeq() + " is sent!");
			datagramPackets.add(sendAction.getDatagramPacket());
			if (getBase() == getNextSeq()) {
				startTimer();
			}
			int next = getNextSeq();
			setNextSeq(next + 1);
		} else if (action == Constants.ACTION_RESEND) {
			for (int i = getBase(); i < datagramPackets.size(); i++) {
				sendAction.getClientSocket().send(datagramPackets.get(i));
				System.out.println("The packet " + i + " is RESENT");
			}

		} else if (action == Constants.ACTION_RECEIVE_ACK) {

			System.out.println("Ack " + receiveAck.getAck() + " is received");
			if (receiveAck.getAck() == (maxAcked + 1)) {
				maxAcked = receiveAck.getAck();
			}

			setBase(maxAcked + 1);

			if (getBase() == getNextSeq()) {
				stopTimer();
			} else {
				stopTimer();
				startTimer();
			}

			if (getBase() + Constants.N > getNextSeq()) {
				if (!semaphore.tryAcquire()) {
					semaphore.release();
				}
			}
		}
	}

	public synchronized void setNextSeq(int seq) {
		this.nextSeq = seq;
	}

	public synchronized int getNextSeq() {
		return nextSeq;
	}

	public synchronized void setBase(int base) {
		this.base = base;
	}

	public synchronized int getBase() {
		return base;
	}

	public int getMaxAcked() {
		return maxAcked;
	}

	private class TCPOutputStream extends OutputStream {

		/**
		 * Writes the specified byte to this output stream. The general contract
		 * for <code>write</code> is that one byte is written to the output
		 * stream. The byte to be written is the eight low-order bits of the
		 * argument <code>b</code>. The 24 high-order bits of <code>b</code> are
		 * ignored.
		 * <p>
		 * Subclasses of <code>OutputStream</code> must provide an
		 * implementation for this method.
		 *
		 * @param b
		 *            the <code>byte</code>.
		 * @throws IOException
		 *             if an I/O error occurs. In particular, an
		 *             <code>IOException</code> may be thrown if the output
		 *             stream has been closed.
		 */
		@Override
		public void write(int b) throws IOException {
			if (buffer.isFull())
				flush();
			buffer.insert((byte) b);
		}

		@Override
		public void write(byte[] b, int off, int len) throws IOException {
			super.write(b, off, len);
		}

		@Override
		public void flush() throws IOException {
			try {
				byte[] toSend = buffer.getBytesAndClear();
				// Utils.log(TAG, "Sending " + toSend.length + " bytes ");
				transfer(toSend);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public OutputStream getOutputStream() {
		// if (!isConnect) throw new IllegalStateException("Not connected yet");
		return outputStream;
	}

}
