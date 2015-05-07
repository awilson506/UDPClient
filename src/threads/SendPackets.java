package threads;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class SendPackets extends Thread{
	
	private byte[] sendData;
	private byte[] receiveData;
	private InetAddress IPAddress;
	private int timeout;
	


	public SendPackets(byte[] sendData, InetAddress IPAddress, byte[] receiveData, int timeout){
		this.sendData = sendData;
		this.IPAddress = IPAddress;
		this.receiveData = receiveData;
		this.timeout = timeout;
	}

	
	public void run(){
		DatagramSocket clientSocket;
		try {
			
			clientSocket = new DatagramSocket();
			System.out.println("***********************");
			System.out.println("Sending " + sendData.length
					+ " bytes to server.");
			DatagramPacket sendPacket = new DatagramPacket(sendData,
					sendData.length, IPAddress, 21252);

			clientSocket.send(sendPacket);

			DatagramPacket receivePacket = new DatagramPacket(receiveData,
					receiveData.length);

			System.out.println("Waiting for return packet");
			clientSocket.setSoTimeout(timeout);

			try {
				clientSocket.receive(receivePacket);
				//String modifiedSentence = new String(receivePacket.getData());
				int test = constructToInt(receivePacket.getData(),ByteOrder.BIG_ENDIAN);
				InetAddress returnIPAddress = receivePacket.getAddress();

				int port = receivePacket.getPort();

				System.out.println("From server at: " + returnIPAddress + ":"
						+ port);
				System.out.println("ACK: " + test);
				clientSocket.close();
				System.out.println("------------------------");
			} catch (SocketTimeoutException ste) {
				//try three times need to loop this thread and so something here if we're at count 3
				System.out.println("Timeout Occurred: Packet assumed lost");
			}
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	
	}
	public static int constructToInt(byte[] byteArray, ByteOrder order)
    {
        ByteBuffer buffer = ByteBuffer.wrap(byteArray);
        buffer.order(order);
        return buffer.getInt();
    }
}
