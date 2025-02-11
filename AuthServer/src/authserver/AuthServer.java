package authserver;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class AuthServer {
    // Puerto del servidor
    private static final int PORT = 5000;
    
    // HashMap con las credenciales: usuario -> contraseña.
    private static HashMap<String, String> credentials = new HashMap<>();
    
    // Contador para asignar un número único a cada cliente.
    private static AtomicInteger clientCounter = new AtomicInteger(1);
    
    public static void main(String[] args) {
        // Rellenar las credenciales
        credentials.put("user01", "one.Password");
        credentials.put("user02", "two.Password");
        credentials.put("user03", "three.Password");

        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Servidor de autenticación iniciado en el puerto " + PORT);
            
            // Bucle infinito para aceptar conexiones entrantes.
            while (true) {
                Socket clientSocket = serverSocket.accept();
                
                // Asignar un número de cliente (único) a la conexión.
                int clientId = clientCounter.getAndIncrement();
                
                // Imprimir información del cliente que se conecta en la consola del servidor.
                System.out.println("Cliente " + clientId + " conectado desde " 
                        + clientSocket.getInetAddress().getHostAddress() + ":" 
                        + clientSocket.getPort());
                
                // Crear y lanzar un hilo para atender al cliente.
                new Thread(new ClientHandler(clientSocket, clientId)).start();
            }
        } catch (IOException ex) {
            System.err.println("Error al iniciar el servidor: " + ex.getMessage());
        } finally {
            if (serverSocket != null) {
                try { 
                    serverSocket.close(); 
                } catch (IOException e) {
                    System.err.println("Error al cerrar el servidor: " + e.getMessage());
                }
            }
        }
    }
    
    // Clase interna para manejar la conexión de cada cliente.
    static class ClientHandler implements Runnable {
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;
        
        private int attemptCount = 0;
        
        private int clientId;
        
        public ClientHandler(Socket socket, int clientId) {
            this.socket = socket;
            this.clientId = clientId;
        }
        
        @Override
        public void run() {
            try {
                
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                
                
                out.println("Bienvenido al servidor de autenticación.");
                
                out.println("Eres el cliente " + clientId + ".");
                
                
                while (true) {
                    
                    out.println("Introduce el nombre de usuario:");
                    String username = in.readLine();
                    if (username == null) break;  
                    
                    
                    out.println("Introduce la contraseña:");
                    String password = in.readLine();
                    if (password == null) break;
                    
                    
                    String clientInfo = "Cliente " + clientId + " (" 
                            + socket.getInetAddress().getHostAddress() + ":" + socket.getPort() + ")";
                    
                    
                    if (credentials.containsKey(username) && credentials.get(username).equals(password)) {
                        out.println("Acceso permitido.");
                        System.out.println(clientInfo + " - Usuario: " + username 
                                + ", Contraseña: " + password + " -> Acceso permitido.");
                        break;  
                    } else {
                        attemptCount++;
                        out.println("Acceso denegado.");
                        System.out.println(clientInfo + " - Usuario: " + username 
                                + ", Contraseña: " + password + " -> Acceso denegado. Intento " 
                                + attemptCount + " de 3.");
                        
                        
                        if (attemptCount >= 3) {
                            out.println("Tres intentos fallidos. Espere 30 segundos para volver a intentarlo.");
                            System.out.println(clientInfo + " ha excedido los intentos. Bloqueo de 30 segundos.");
                            try {
                                Thread.sleep(30000); 
                            } catch (InterruptedException e) {
                                
                            }
                            
                            attemptCount = 0;
                        }
                    }
                }
            } catch (IOException ex) {
                System.err.println("Error en la comunicación con el cliente: " + ex.getMessage());
            } finally {
                try {
                    socket.close();
                    System.out.println("Conexión con el cliente " + clientId + " (" 
                            + socket.getInetAddress().getHostAddress() + ":" 
                            + socket.getPort() + ") finalizada.");
                } catch (IOException e) {
                    System.err.println("Error al cerrar la conexión: " + e.getMessage());
                }
            }
        }
    }
}
