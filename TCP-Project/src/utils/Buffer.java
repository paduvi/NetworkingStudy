package utils;

public class Buffer {

	private int maxSize;
	private int front;
	private int rear;
	private int bufLen;
	private byte[] buf;
	// private String TAG = getClass().getSimpleName();

	/** constructor **/
	public Buffer(int size) {
		maxSize = size;
		front = 0;
		rear = 0;
		bufLen = 0;
		buf = new byte[maxSize];
	}

	/** function to get size of buffer **/
	public int getSize() {
		return bufLen;
	}

	/** function to clear buffer **/
	public void clear() {
		front = rear = 0;
		rear = 0;
		bufLen = 0;
		buf = new byte[maxSize];
	}

	/** check if buffer is empty **/
	public boolean isEmpty() {
		return bufLen == 0;
	}

	/** check if buffer is full **/
	public boolean isFull() {
		return bufLen == maxSize;
	}

	/** insert an element **/
	public synchronized void insert(byte c) {

		if (!isFull()) {
			bufLen++;
			buf[rear] = c;
			rear = (rear + 1) % maxSize;
		} else
			System.out.println("Error : Underflow Exception");

		// System.out.println(bufLen);
	}

	/** insert an array byte **/
	public synchronized void insert(byte[] b) {
		int sizeB = b.length;
		for (int i = 0; i < sizeB; i++) {
			if (!isFull()) {
				bufLen++;
				buf[rear] = b[i];
				rear = (rear + 1) % maxSize;
				// System.out.println(bufLen);
			} else {
				System.out.println("Error: Underflow Exception");
				break;
			}
		}
	}

	/** delete an element **/
	public synchronized byte delete() {
		if (!isEmpty()) {
			bufLen--;
			byte res = buf[front];
			front = (front + 1) % maxSize;

			return res;
		} else {
			System.out.println("Error : Underflow Exception");
			return ' ';
		}
	}

	/** print buffer, only if front is less than rear **/
	public void display() {
		System.out.println("Buffer : ");
		for (int i = front; i < rear; i++)
			System.out.print(buf[i] + " ");
		System.out.println();
	}

	public byte[] getBytesAndClear() {
		byte[] res = new byte[bufLen];

		int dataLength = bufLen;
		for (int i = 0; i < dataLength; i++) {
			res[i] = delete();
		}

		// Utils.log(TAG, Arrays.toString(res));
		return res;
	}

}
