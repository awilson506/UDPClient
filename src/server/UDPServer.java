package server;

import java.io.*; 
import java.net.*; 
  
class UDPServer { 
  public static void main(String args[]) throws Exception 
    { 
     try
     { 
      DatagramSocket serverSocket = new DatagramSocket(21252); 
  
      byte[] receiveData = new byte[1024]; 
      byte[] sendData  = new byte[1024]; 
  
      while(true) 
        { 
  
          receiveData = new byte[16]; 

          DatagramPacket receivePacket = 
             new DatagramPacket(receiveData, receiveData.length); 

          System.out.println ("Waiting for datagram packet");

          serverSocket.receive(receivePacket); 

          String sentence = new String(receivePacket.getData()); 
  
          InetAddress IPAddress = receivePacket.getAddress(); 
  
          int port = receivePacket.getPort(); 
  
          System.out.println ("From: " + IPAddress + ":" + port);
          System.out.println ("Message: " + sentence);

          String capitalizedSentence = sentence.toUpperCase(); 

          sendData = capitalizedSentence.getBytes(); 
  
          DatagramPacket sendPacket = 
             new DatagramPacket(sendData, sendData.length, IPAddress, 
                               port); 
  
          serverSocket.send(sendPacket); 

        } 

     }
      catch (SocketException ex) {
        System.out.println("UDP Port 21252 is occupied.");
        System.exit(1);
      }

    } 
}  