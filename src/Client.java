import java.net.*;
import java.io.*;
import java.util.Scanner;
public class Client {
    private static final String SERVEUR = "localhost";
    private static final int PORT = 1234;
    private static boolean enCours = true;
    public static void main(String[] args) throws IOException {
        DatagramSocket socket = new DatagramSocket();
        Scanner scanner = new Scanner(System.in);
        System.out.print("Entrez votre nom: ");
        String nom = scanner.nextLine();
        System.out.println("Connecté au chat,Tapez vos messages");
        // Thread pour recevoir les messages
        Thread reception = new Thread(new ReceptionMessages(socket));
        reception.start();
        // Envoi des messages
        while (enCours) {
            System.out.print("> ");
            String message = scanner.nextLine();
            if ("quit".equalsIgnoreCase(message)) {
                enCours = false;
                break;
            }
            String messageComplet = "[" + nom + "]: " + message;
            byte[] data = messageComplet.getBytes();
            DatagramPacket paquet = new DatagramPacket(
                data, data.length, InetAddress.getByName(SERVEUR), PORT
            );
            socket.send(paquet);
        }
        socket.close();
        scanner.close();
        System.out.println("Déconnexion");
    }
    //implémentation du thread de réception
    static class ReceptionMessages implements Runnable {
        private DatagramSocket socket;
        public ReceptionMessages(DatagramSocket socket) {
            this.socket = socket;
        }
        public void run() {
            byte[] buffer = new byte[1024];
            while (enCours) {
                try {
                    DatagramPacket paquet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(paquet);
                    String message = new String(paquet.getData(), 0, paquet.getLength());
                    System.out.println("\n" + message + "\n> ");
                } catch (IOException e) {
                    if (enCours) {
                        System.out.println("Erreur de reception!!!");
                    }
                }
            }
        }
    }
}