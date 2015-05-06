package udp_client;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Scanner;

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

			System.out.print("Please enter a Web Server name: ");
			// read in the message to send
			String sentence = in.nextLine();
			System.out.print("Enter the timeout time in ms: ");
			// read in the timeout in ms
			int timeout = in.nextInt();

			System.out.print("Enter the packet size: ");
			// read in packet size
			int packetSize = in.nextInt();
			// set the packet byte size
			byte[] sendData = new byte[Math.min(packetSize, 1460)];

			getPage(sentence, packetSize);

			sendData = sentence.getBytes();

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
		URL url;
		InputStream is = null;
		BufferedReader br;
		String line;

		try {
			url = new URL("http://" + pageUrl);
			is = url.openStream(); // throws an IOException
			br = new BufferedReader(new InputStreamReader(is));
			//ByteBuffer bbuf = ByteBuffer.allocate(size);

			while ((line = br.readLine()) != null) {
				//send response packets at a time
				System.out.println(line);
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
	}
}
