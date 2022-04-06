import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;

// Implement a gateway that discovers all of these vital monitors.
public class Gateway extends Thread{
    private DatagramSocket createRecieveSocket(){
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(6000);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return socket;
    }
    private DatagramPacket recievePacket(){
        DatagramPacket packet = null;
        try {
            packet = new DatagramPacket(new byte[1024], 1024);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return packet;
    }

    private void Run(Gateway gateway){
        DatagramSocket recieveSocket = gateway.createRecieveSocket();
        DatagramPacket recievePacket = gateway.recievePacket();
        while(true){
            try {
                recieveSocket.receive(recievePacket);
                ByteArrayInputStream bis = new ByteArrayInputStream(recievePacket.getData());
                ObjectInputStream ois = new ObjectInputStream(bis);
                Monitor monitor = (Monitor) ois.readObject();
                Socket client = new Socket(monitor.getIp(), monitor.getPort());
                // System.out.println("Monitor: " + monitor.monitor_str());
                // Recieve Message
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
