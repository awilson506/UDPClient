package client;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Scanner;

import threads.PrintPage;

class UDPClient {
	public static void main(String args[]) throws Exception {
		try {
			String serverHostname = new String("127.0.0.1");

			if (args.length > 0)
				serverHostname = args[0];
			
			Scanner in = new Scanner(System.in);

			DatagramSocket clientSocket = new DatagramSocket();

			InetAddress IPAddress = InetAddress.getByName(serverHostname);
			System.out.println("Attemping to connect to " + IPAddress
					+ ") via UDP port 21252");

			byte[] receiveData = new byte[1024];

			//System.out.print("Please enter a Web Server name: ");
			// read in the message to send
			//String serverOne = in.nextLine();
			//System.out.print("Enter the timeout time in ms: ");
			// read in the timeout in ms
			int timeout = 10000; /*in.nextInt(); */

			//System.out.print("Enter the packet size: ");
			// read in packet size
			//int packetSize = in.nextInt();
			// set the packet byte size
			byte[] sendData = new byte[Math.min(1000 , 1460)];

			//getPage(sentence, packetSize);
			//*********************************************************************
			
			URL url;
			InputStream is = null;
			BufferedReader br;
			int line;

			try {
				url = new URL("http://www.towson.edu" /*+ serverOne*/);
				is = url.openStream(); // throws an IOException
				br = new BufferedReader(new InputStreamReader(is));
				
				while (is.available() > 0 ) {
					
					for( int i = 0; i < sendData.length; i++){
						
							sendData[i] = (byte) is.read();
							//System.out.print(new String(sendData, i,1));
							
					
					}
					new Thread(new PrintPage(sendData)).start();
					
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
			
			
			

			//sendData = sentence.getBytes();

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
				String modifiedSentence = new String(receivePacket.getData());

				InetAddress returnIPAddress = receivePacket.getAddress();

				int port = receivePacket.getPort();

				System.out.println("From server at: " + returnIPAddress + ":"
						+ port);
				System.out.println("Message: " + modifiedSentence);

			} catch (SocketTimeoutException ste) {
				System.out.println("Timeout Occurred: Packet assumed lost");
			}

			clientSocket.close();
		} catch (UnknownHostException ex) {
			System.err.println(ex);
		} catch (IOException ex) {
			System.err.println(ex);
		}

	}

	public static void getPage(String pageUrl, int size) {
		
	}
}