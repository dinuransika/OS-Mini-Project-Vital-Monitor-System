//////////////////////////////////////////////////////////////////////////////
// Program: Gateway for vital monitors
// Author:  Dissanayake D.M.D.R.
// E Number: E/17/072
// Date:    07.04.2022
//////////////////////////////////////////////////////////////////////////////

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

// Implement a gateway that discovers all of these vital monitors. 
public class Gateway{
    // Creating the data gram socket for packet recieving on port 6000
    private static DatagramSocket createRecieveSocket(){
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(6000);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return socket;
    }
    // Recieve datagram packets from the vital monitor
    private static DatagramPacket recievePacket(DatagramSocket socket){
        DatagramPacket packet = null;
        try {
            packet = new DatagramPacket(new byte[1024], 1024);
            socket.receive(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return packet;
    }


    private static void createSocket(Monitor monitor){
        try {
            Socket client = new Socket(monitor.getIp(), monitor.getPort());
            // System.out.println("Establishing a connection to Monitor ID: " + monitor.getMonitorID() + " IP: " + monitor.getIp() + " PORT:" + monitor.getPort());
            OutputStream os = client.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os);
            osw.write("Request: " + monitor.getMonitorID());
            osw.flush();
            recieveMessages(client);

        } catch (Exception e) {
            e.printStackTrace();
        }        
    }

    private static Monitor getMonitor(DatagramSocket recieveSocket, DatagramPacket recievePacket){
        Monitor monitor = null;
        try{
            ByteArrayInputStream inputStream = new ByteArrayInputStream(recievePacket.getData());
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
            monitor = (Monitor) objectInputStream.readObject();
        }catch(Exception e){
            e.printStackTrace();
        }
        return monitor;
    }

    private static void recieveMessages(Socket client){
        try {
            InputStream is = client.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            StringBuffer sb = new StringBuffer();
            while(true){
                int i = isr.read();
                while(i != '\n'){
                    sb.append((char)i);
                    i = isr.read();
                }
                if (sb.toString().equals("end")){
                    break;
                }
                System.out.println(sb.toString());
                sb.delete(0, sb.length());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        DatagramSocket recieveSocket = createRecieveSocket();
        // Recieve the vital monitor packets

        // list to store ips of the monitors
        List<String> monitors = new ArrayList<String>();

        while(true){
            DatagramPacket recievePacket = recievePacket(recieveSocket);
            Monitor monitor = getMonitor(recieveSocket, recievePacket);
            InetAddress ipAdd = monitor.getIp();
            int port = monitor.getPort();
            if (!monitors.contains(ipAdd+":"+port)) {
                monitors.add(ipAdd + ":" + port);
    
                System.out.println("Establishing a connection to Monitor ID: " + recievePacket.getAddress().toString() + " IP: " + recievePacket.getAddress().getHostAddress() + " PORT:" + recievePacket.getPort());
                
                // Create a new thread to send the vital monitor packets
                Thread thread = new Thread(new Runnable() {
                    @Override
                    synchronized public void run() {
                        createSocket(monitor);
                    }
                });
                thread.start();
                
            }
        }
    }
}
