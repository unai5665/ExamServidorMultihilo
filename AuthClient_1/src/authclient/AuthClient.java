package authclient;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class AuthClient {
    
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 5000;
    
    public static void main(String[] args) {
        Socket socket = null;
        try {
            
            socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            System.out.println("Conectado al servidor de autenticación en " + SERVER_ADDRESS + ":" + SERVER_PORT);
            
            
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            Scanner scanner = new Scanner(System.in);
            
            String serverMsg;
            
            while ((serverMsg = in.readLine()) != null) {
                System.out.println("Servidor: " + serverMsg);
                
                if (serverMsg.contains("Introduce el nombre de usuario:") ||
                    serverMsg.contains("Introduce la contraseña:")) {
                    String userInput = scanner.nextLine();
                    out.println(userInput);
                }
            }
            
        } catch (IOException e) {
            System.err.println("Error en la conexión con el servidor: " + e.getMessage());
        } finally {
            if (socket != null) {
                try { 
                    socket.close(); 
                } catch (IOException e) {
                    System.err.println("Error al cerrar la conexión: " + e.getMessage());
                }
            }
        }
    }
}
