import java.io.*;
import java.net.*;
import java.util.*;

public class FTPServer {
    private static final int PORT = 9090;
    private static final String STORAGE_DIR = "ftp_storage";
    private ServerSocket serverSocket;
    private boolean running;

    public FTPServer() {
        File storageDir = new File(STORAGE_DIR);
        if (!storageDir.exists()) {
            storageDir.mkdir();
        }
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(PORT);
            running = true;
            System.out.println("Servidor FTP iniciado na porta " + PORT);

            while (running) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Novo cliente conectado: " + clientSocket.getInetAddress());
                
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class ClientHandler implements Runnable {
        private Socket clientSocket;
        private DataInputStream dataInputStream;
        private DataOutputStream dataOutputStream;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
            try {
                dataInputStream = new DataInputStream(socket.getInputStream());
                dataOutputStream = new DataOutputStream(socket.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                while (true) {
                    String command = dataInputStream.readUTF();
                    
                    switch (command) {
                        case "LIST":
                            listFiles();
                            break;
                        case "UPLOAD":
                            receiveFile();
                            break;
                        case "DOWNLOAD":
                            sendFile();
                            break;
                        case "DELETE":
                            deleteFile();
                            break;
                        case "EXIT":
                            clientSocket.close();
                            return;
                    }
                }
            } catch (IOException e) {
                System.out.println("Cliente desconectado");
            }
        }

        private void listFiles() throws IOException {
            File directory = new File(STORAGE_DIR);
            String[] files = directory.list();
            dataOutputStream.writeInt(files.length);
            for (String file : files) {
                dataOutputStream.writeUTF(file);
            }
            dataOutputStream.flush();
        }

        private void receiveFile() throws IOException {
            String fileName = dataInputStream.readUTF();
            long fileSize = dataInputStream.readLong();
            
            File file = new File(STORAGE_DIR + File.separator + fileName);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            byte[] buffer = new byte[4096];
            int bytesRead;
            long totalBytesRead = 0;
            
            while (totalBytesRead < fileSize) {
                bytesRead = dataInputStream.read(buffer);
                fileOutputStream.write(buffer, 0, bytesRead);
                totalBytesRead += bytesRead;
            }
            
            fileOutputStream.close();
            System.out.println("Arquivo recebido: " + fileName);
        }

        private void sendFile() throws IOException {
            String fileName = dataInputStream.readUTF();
            File file = new File(STORAGE_DIR + File.separator + fileName);
            
            if (!file.exists()) {
                dataOutputStream.writeBoolean(false);
                System.out.println("Arquivo não encontrado: " + fileName);
                return;
            }
            
            dataOutputStream.writeBoolean(true);
            dataOutputStream.writeLong(file.length());
            
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] buffer = new byte[4096];
            int bytesRead;
            
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                dataOutputStream.write(buffer, 0, bytesRead);
            }
            
            fileInputStream.close();
            dataOutputStream.flush();
            System.out.println("Arquivo enviado: " + fileName);
        }

        private void deleteFile() throws IOException {
            String fileName = dataInputStream.readUTF();
            File file = new File(STORAGE_DIR + File.separator + fileName);
            
            if (!file.exists()) {
                dataOutputStream.writeBoolean(false);
                System.out.println("Arquivo não encontrado para exclusão: " + fileName);
                return;
            }
            
            boolean deleted = file.delete();
            dataOutputStream.writeBoolean(deleted);
            
            if (deleted) {
                System.out.println("Arquivo excluído com sucesso: " + fileName);
            } else {
                System.out.println("Erro ao excluir arquivo: " + fileName);
            }
        }
    }

    public static void main(String[] args) {
        FTPServer server = new FTPServer();
        server.start();
    }
} 