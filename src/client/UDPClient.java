package client;

import java.io.*;
import java.net.*;
import java.util.Scanner;

import server.UDPServer;
import threads.PrintPage;
import threads.SendPackets;

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
				Thread thread;
				while (is.available() > 0) {

					for (int i = 0; i < sendData.length; i++) {

						sendData[i] = (byte) is.read();
						// System.out.print(new String(sendData, i,1));

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
		} catch (IOException ex) {
			System.err.println(ex);
		}
		long endTime = System.nanoTime();
		long duration = (endTime - startTime);
		System.out.println("DONE");
		System.out.println("Total Time: " + duration / 1000000 + " ms");
		System.out.println("Packets received: " + ackCount);

	}

}
