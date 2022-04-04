import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.TimeUnit;

// Implement a gateway that discovers all of these vital monitors.
public class Gateway extends Thread {
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
    // recieve packet in threads
    public void run(){
        DatagramSocket socket = createRecieveSocket();
        DatagramPacket packet = recievePacket();
        while(true){
            try {
                socket.receive(packet);
                String message = new String(packet.getData(), 0, packet.getLength());
                System.out.println(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        
    }
}
