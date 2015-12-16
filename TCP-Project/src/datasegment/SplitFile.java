package datasegment;

import java.io.IOException;
import java.util.Arrays;

import utils.Constants;

public class SplitFile {

	private int sizeOfFile;
	private byte[] data;
	public int numPacket;

	public SplitFile(byte[] data) throws IOException {
		// this.filePath = filePath;
		this.data = data;
		this.sizeOfFile = data.length;
	}

	public byte[][] splitFile() {
		this.numPacket = (sizeOfFile - 1) / Constants.SIZE_DATA + 1;
		byte[][] ret = new byte[numPacket][];

		int from = 0;
		for (int i = 0; i < numPacket; i++) {
			int to = 0;
			if (from + Constants.SIZE_DATA < sizeOfFile) {
				to = from + Constants.SIZE_DATA;
			} else {
				to = sizeOfFile;
			}

			ret[i] = Arrays.copyOfRange(data, from, to);
			from = to;
		}

		return ret;
	}

	public int getNumPacket() {
		return numPacket;
	}

}
