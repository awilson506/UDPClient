package client;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Scanner;



public class UDPClient {

	public static int ackCount = 0;

	public static void main(String args[]) throws Exception {

		File file = new File("output-server.txt");
		file.delete();
		file = new File("output-client.txt");
		file.delete();
		long startTime = 0;
		int packetSize = 0;

		try {
			String serverHostname = new String("127.0.0.1");

			if (args.length > 0)
				serverHostname = args[0];

			Scanner in = new Scanner(System.in);

			InetAddress IPAddress = InetAddress.getByName(serverHostname);
			System.out.println("Attemping to connect to " + IPAddress
					+ ") via UDP port 21252");

			System.out.print("Please enter a Web Server name: ");
			// read in the message to send
			String serverOne = in.nextLine();
			System.out.print("Enter the timeout time in ms: ");
			// read in the timeout in ms
			int timeout = in.nextInt();

			System.out.print("Enter the packet size: ");
			// read in packet size
			packetSize = in.nextInt();
			// set the packet byte size
			byte[] sendData = new byte[Math.min(packetSize, 1460)];
			byte[] receiveData = new byte[Math.min(packetSize, 1460)];

			// *********************************************************************

			URL url;
			InputStream is = null;
			BufferedReader br;
			startTime = System.nanoTime();
			try {
				url = new URL("http://" + serverOne);
				is = url.openStream(); // throws an IOException
				br = new BufferedReader(new InputStreamReader(is));
				
				while (is.available() > 0) {

					for (int i = 0; i < sendData.length; i++) {
						sendData[i] = (byte) is.read();
					}
					
					PrintPage p = new PrintPage(sendData, "output-client.txt");
					p.start();
					p.join();
					SendPackets sp = new SendPackets(sendData, IPAddress,
							receiveData, timeout);
					sp.start();

					sp.join();
					ackCount = sp.getACK();
				}
			} catch (MalformedURLException mue) {
				mue.printStackTrace();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			} finally {
				try {
					if (is != null)
						is.close();
				} catch (IOException ioe) {
					// nothing to see here
				}
			}

		} catch (UnknownHostException ex) {
			System.err.println(ex);
		} 
		
		long endTime = System.nanoTime();
		long duration = (endTime - startTime);
		System.out.println("DONE");
		System.out.println("Total Time: " + duration / 1000000 + " ms");
		System.out.println("Packets received: " + ackCount);

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


}


