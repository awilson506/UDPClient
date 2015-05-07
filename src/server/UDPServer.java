package server;

import java.io.*;

import client.UDPClient;

import java.net.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import threads.PrintPage;

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

				System.out.println("Waiting for datagram packet");

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

				System.out.println("From: " + IPAddress + ":" + port);
				//System.out.println("Message: " + sentence);

				String capitalizedSentence = sentence;

				sendData = capitalizedSentence.getBytes();
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
		ByteBuffer buffer = ByteBuffer.allocate(4); // in java, int takes 4
													// bytes.
		buffer.order(order);
		return buffer.putInt(value).array();
	}
}
