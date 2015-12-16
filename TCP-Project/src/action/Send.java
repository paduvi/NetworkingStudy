package action;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import datasegment.DataSegment;

public class Send extends Action{
	
	private int idPacket;
	private byte[] packet;
	private DatagramPacket sendPacket;
	
	public Send(String ipAdress, int connectPort) {
		this.connectionPort = connectPort;
		//this.serverWelcomePort = serverPort;
		try {
			this.clientSocket = new DatagramSocket();
			this.ipAddress = InetAddress.getByName(ipAdress);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setData(int id, byte[] data) {
		this.idPacket = id;
		this.packet = data;
	}
	
	public void execute() throws IOException{
		// can sua thay so ack, seq
		DataSegment sendSegment = new DataSegment(idPacket, 1, 0, true, true, false, packet);
		this.sendPacket = new DatagramPacket(sendSegment.toByteArray(), 
									sendSegment.toByteArray().length, ipAddress, connectionPort);
		
		clientSocket.send(sendPacket);
		//long stopTime = System.currentTimeMillis();
		//System.out.println("The packet " + idPacket + " is sent!");
		//System.out.println("Completed in " + sampleRTT + "miliseconds");
	}
	
	public DatagramSocket getClientSocket() {
		return clientSocket;
	}
	
	public DatagramPacket getDatagramPacket() {
		return sendPacket;
	}

}
