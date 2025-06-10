import java.io.*;
import java.net.*;
import java.util.*;

public class FTPClient {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 9090;
    private Socket socket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;

    public void connect() throws IOException {
        socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
        dataInputStream = new DataInputStream(socket.getInputStream());
        dataOutputStream = new DataOutputStream(socket.getOutputStream());
        System.out.println("Conectado ao servidor FTP");
    }

    public void disconnect() throws IOException {
        if (socket != null && !socket.isClosed()) {
            dataOutputStream.writeUTF("EXIT");
            socket.close();
            System.out.println("Desconectado do servidor FTP");
        }
    }

    public List<String> listFiles() throws IOException {
        dataOutputStream.writeUTF("LIST");
        
        int fileCount = dataInputStream.readInt();
        List<String> files = new ArrayList<>();
        for (int i = 0; i < fileCount; i++) {
            files.add(dataInputStream.readUTF());
        }
        return files;
    }

    public void uploadFile(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("Arquivo não encontrado: " + filePath);
            return;
        }

        dataOutputStream.writeUTF("UPLOAD");
        dataOutputStream.writeUTF(file.getName());
        dataOutputStream.writeLong(file.length());

        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] buffer = new byte[4096];
        int bytesRead;

        while ((bytesRead = fileInputStream.read(buffer)) != -1) {
            dataOutputStream.write(buffer, 0, bytesRead);
        }

        fileInputStream.close();
        dataOutputStream.flush();
        System.out.println("Arquivo enviado com sucesso: " + file.getName());
    }

    public void downloadFile(String fileName, String savePath) throws IOException {
        dataOutputStream.writeUTF("DOWNLOAD");
        dataOutputStream.writeUTF(fileName);

        boolean fileExists = dataInputStream.readBoolean();
        if (!fileExists) {
            System.out.println("Arquivo não encontrado no servidor: " + fileName);
            return;
        }

        long fileSize = dataInputStream.readLong();
        File file = new File(savePath + File.separator + fileName);
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
        System.out.println("Arquivo baixado com sucesso: " + fileName);
    }

    public static void main(String[] args) {
        FTPClient client = new FTPClient();
        Scanner scanner = new Scanner(System.in);

        try {
            client.connect();

            while (true) {
                System.out.println("\nEscolha uma opção:");
                System.out.println("1. Listar arquivos");
                System.out.println("2. Enviar arquivo");
                System.out.println("3. Baixar arquivo");
                System.out.println("4. Sair");

                int choice = scanner.nextInt();
                scanner.nextLine(); // Consumir nova linha

                switch (choice) {
                    case 1:
                        List<String> files = client.listFiles();
                        System.out.println("\nArquivos no servidor:");
                        for (String file : files) {
                            System.out.println("- " + file);
                        }
                        break;

                    case 2:
                        System.out.println("Digite o caminho do arquivo para enviar:");
                        String uploadPath = scanner.nextLine();
                        client.uploadFile(uploadPath);
                        break;

                    case 3:
                        System.out.println("Digite o nome do arquivo para baixar:");
                        String fileName = scanner.nextLine();
                        System.out.println("Digite o diretório para salvar o arquivo:");
                        String savePath = scanner.nextLine();
                        client.downloadFile(fileName, savePath);
                        break;

                    case 4:
                        client.disconnect();
                        return;

                    default:
                        System.out.println("Opção inválida!");
                }
            }
        } catch (IOException e) {
            System.out.println("Erro de conexão: " + e.getMessage());
        } finally {
            try {
                client.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
            scanner.close();
        }
    }
} 