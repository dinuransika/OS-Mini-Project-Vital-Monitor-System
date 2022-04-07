//////////////////////////////////////////////////////////////////////////////
// Program: Gateway for vital monitors
// Author:  Dissanayake D.M.D.R.
// E Number: E/17/072
// Date:    07.04.2022
//////////////////////////////////////////////////////////////////////////////

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;

// Implement a gateway that discovers all of these vital monitors.
// Gateway class extends Thread class. 
public class Gateway extends Thread{
    // Creating the data gram socket for packet recieving on port 6000
    private DatagramSocket createRecieveSocket(){
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(6000);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return socket;
    }
    // Recieve datagram packets from the vital monitor
    private DatagramPacket recievePacket(){
        DatagramPacket packet = null;
        try {
            packet = new DatagramPacket(new byte[1024], 1024);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return packet;
    }
    // Use the run mehtod to recieve the vital monitor packets
    private void Run(Gateway gateway){
        DatagramSocket recieveSocket = gateway.createRecieveSocket();
        DatagramPacket recievePacket = gateway.recievePacket();
        // Recieve the vital monitor packets
        while(true){
            try {
                recieveSocket.receive(recievePacket);
                ByteArrayInputStream bis = new ByteArrayInputStream(recievePacket.getData()); // Get the monitor object from the packet as a Byte array input stream
                ObjectInputStream ois = new ObjectInputStream(bis); // Convert the Byte array input stream to an object input stream
                Monitor monitor = (Monitor) ois.readObject(); // Convert the object input stream to a monitor object
                // Create a TCP connection and recieve the message from the vital monitor.
                Socket client = new Socket(monitor.getIp(), monitor.getPort());
                byte[] buffer = new byte[1024];
                client.getInputStream().read(buffer);
                String message = new String(buffer);
                System.out.println("Message: " + message);
                client.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        Gateway gateway = new Gateway();
        gateway.Run(gateway);
    }
}
