package client;

import java.awt.List;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import threads.PrintPage;
import threads.SendPackets;

class UDPClient {
	public static void main(String args[]) throws Exception {
		File file = new File("output.txt");
		file.delete();
		try {
			String serverHostname = new String("127.0.0.1");

			if (args.length > 0)
				serverHostname = args[0];

			Scanner in = new Scanner(System.in);

			InetAddress IPAddress = InetAddress.getByName(serverHostname);
			System.out.println("Attemping to connect to " + IPAddress
					+ ") via UDP port 21252");

			byte[] receiveData = new byte[1024];

			// System.out.print("Please enter a Web Server name: ");
			// read in the message to send
			// String serverOne = in.nextLine();
			// System.out.print("Enter the timeout time in ms: ");
			// read in the timeout in ms
			int timeout = 10000; /* in.nextInt(); */

			// System.out.print("Enter the packet size: ");
			// read in packet size
			// int packetSize = in.nextInt();
			// set the packet byte size
			byte[] sendData = new byte[Math.min(1000, 1460)];

			// getPage(sentence, packetSize);
			// *********************************************************************

			URL url;
			InputStream is = null;
			BufferedReader br;

			try {
				url = new URL("http://www.towson.edu" /* + serverOne */);
				is = url.openStream(); // throws an IOException
				br = new BufferedReader(new InputStreamReader(is));
				Thread thread;
				while (is.available() > 0) {

					for (int i = 0; i < sendData.length; i++) {

						sendData[i] = (byte) is.read();
						// System.out.print(new String(sendData, i,1));

					}
					PrintPage p = new PrintPage(sendData);
					p.start();
					p.join();
					SendPackets sp = new SendPackets(sendData, IPAddress,
							receiveData, timeout);
					sp.start();
					sp.join();

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
		} catch (IOException ex) {
			System.err.println(ex);
		}

	}
}
