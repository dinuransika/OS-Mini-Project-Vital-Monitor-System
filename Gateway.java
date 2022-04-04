import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.TimeUnit;

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
                System.out.println("Received packet from: " + recievePacket.getAddress().getHostAddress() + ":" + recievePacket.getPort());
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
