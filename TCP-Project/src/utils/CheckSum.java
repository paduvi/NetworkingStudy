package utils;

import java.net.DatagramPacket;
import java.util.Arrays;

public class CheckSum {

	public static boolean isCorrect(DatagramPacket datagramPacket) {

		byte[] data = Arrays.copyOfRange(datagramPacket.getData(), 0, datagramPacket.getLength());
		byte checkSum = data[0];

		byte sum = 0;

		for (int i = 1; i < data.length; i++) {
			sum += data[i];
		}

		if (sum == checkSum) {
			return true;
		} else {
			return false;
		}
	}

	public static byte[] getCheckSumBytes(byte[] origin) {

		byte[] result = new byte[1];

		for (int i = 0; i < result.length; i++) {
			result[0] += origin[i];
		}
		return result;

	}

}
