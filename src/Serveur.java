import java.net.*;
import java.io.*;
import java.util.*;
public class Serveur {
    private static final int PORT = 1234;
    private static byte[] buffer = new byte[1024];
    private static Set<InetSocketAddress> clients = new HashSet<>();
    public static void main(String[] args) throws IOException {
        InetSocketAddress adresse = new InetSocketAddress(PORT);
        DatagramSocket socket = new DatagramSocket(adresse);
        System.out.println("Serveur demarre sur le port " + PORT);
        while (true) {
            DatagramPacket paquet = new DatagramPacket(buffer, buffer.length);
            socket.receive(paquet);
            // Récupération des infos du client
            InetAddress adresseClient = paquet.getAddress();
            int portClient = paquet.getPort();
            InetSocketAddress client = new InetSocketAddress(adresseClient, portClient);
            // Ajout du client à la liste
            clients.add(client);
            String message = new String(paquet.getData(), 0, paquet.getLength());
            System.out.println("Message de " + client + " : " + message);
            // Diffusion à tous les autres clients
            for (InetSocketAddress autreClient : clients) {
                if (!autreClient.equals(client)) {
                    byte[] data = message.getBytes();
                    DatagramPacket paquetDiffusion = new DatagramPacket(
                        data, data.length, autreClient.getAddress(), autreClient.getPort()
                    );
                    socket.send(paquetDiffusion);
                }
            }
        }
    }
}