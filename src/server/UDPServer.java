package server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import client.UDPClient.Singleton;



public class UDPServer {

	public static void main(String args[]) throws Exception {
		// System.out.println(args);
		Integer packetsReceived = 0;
		try {
			DatagramSocket serverSocket = new DatagramSocket(21252);

			int packetSize = 0;

			while (true) {
				// System.out.println(packetSize);
				byte[] receiveData = new byte[1460];

				DatagramPacket receivePacket = new DatagramPacket(receiveData,
						receiveData.length);

				System.out.println("Waiting packets...");

				serverSocket.receive(receivePacket);
				// print the data from the server

				String sentence = new String(receivePacket.getData());
				for (byte b : receivePacket.getData()) {
					if (b != 0) {
						packetSize++;
					}
				}
				byte[] sendData = new byte[Math.min(packetSize, 1460)];

				InetAddress IPAddress = receivePacket.getAddress();
				PrintPage p = new PrintPage(receivePacket.getData(),
						"output-server.txt");
				p.start();
				p.join();
				int port = receivePacket.getPort();

				packetsReceived += packetSize;

				sendData = convertToBytes(packetsReceived, ByteOrder.BIG_ENDIAN);
				DatagramPacket sendPacket = new DatagramPacket(sendData,
						sendData.length, IPAddress, port);

				serverSocket.send(sendPacket);

			}

		} catch (SocketException ex) {
			System.out.println("UDP Port 21252 is occupied.");
			System.exit(1);
		}

	}

	public static byte[] convertToBytes(int value, ByteOrder order) {
		ByteBuffer buffer = ByteBuffer.allocate(4); 
		buffer.order(order);
		return buffer.putInt(value).array();
	}
	
	public static class SendPackets extends Thread {

		private byte[] sendData;
		private byte[] receiveData;
		private InetAddress IPAddress;
		private int timeout;
		private static int ackCount;

		public SendPackets(byte[] sendData, InetAddress IPAddress,
				byte[] receiveData, int timeout) {
			this.sendData = sendData;
			this.IPAddress = IPAddress;
			this.receiveData = receiveData;
			this.timeout = timeout;
		}

		public void run() {
			for (int i = 0; i < 3; i++) {
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
						// String modifiedSentence = new
						// String(receivePacket.getData());
						int test = constructToInt(receivePacket.getData(),
								ByteOrder.BIG_ENDIAN);
						InetAddress returnIPAddress = receivePacket.getAddress();

						int port = receivePacket.getPort();

						System.out.println("From server at: " + returnIPAddress
								+ ":" + port);
						System.out.println("ACK");
						ackCount = test;
						clientSocket.close();
						i = 3;
						System.out.println("------------------------");
					} catch (SocketTimeoutException ste) {

						// System.out.println("Attempt: " + i);
						if (i == 2) {
							System.out.println("FAIL");
							System.exit(1);
						}
						// try three times need to loop this thread and so something
						// here if we're at count 3
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

		}

		public static int getACK() {
			return ackCount;
		}

		public static int constructToInt(byte[] byteArray, ByteOrder order) {
			ByteBuffer buffer = ByteBuffer.wrap(byteArray);
			buffer.order(order);
			return buffer.getInt();
		}
	}
public static class PrintPage extends Thread {
		
		byte[] b;
		private String fileName;
		
		public PrintPage(byte[] b, String fileName){
			this.b = b;
			this.fileName = fileName;
		}
		
		
		public void run(){
			Singleton.getInstance().writeToFile(b, fileName);
		}
	}
	public static class Singleton {
		private static final Singleton inst = new Singleton();

		private Singleton() {
			super();
		}

		public synchronized void writeToFile(byte[] str, String fileName) {

			FileOutputStream fop = null;
			File file;

			try {

				file = new File(fileName);
				fop = new FileOutputStream(file.getAbsoluteFile(), true);
				if (!file.exists()) {
					file.createNewFile();
				}
				try {
					fop.write(str);
				} finally {

				}

				fop.flush();
				fop.close();

			} catch (IOException e) {
				System.out.print("error");
			} finally {

				try {
					if (fop != null) {
						fop.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		public static Singleton getInstance() {
			return inst;
		}

	}
}
