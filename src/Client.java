import java.io.*;
import java.net.Socket;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private Scanner scanner;

    public Client(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.scanner = new Scanner(System.in);
        }catch(IOException e){
            closeEverything(socket, bufferedWriter, bufferedReader, scanner);
        }
    }

    public void sender(){
            while(socket.isConnected()){
                try{
                    //pobieranie wiadomości
                    System.out.println("Wyślij wiadomość do serwera: ");
                    String message = scanner.nextLine();
                    //pobieranie czasu odesłania przez serwer i walidacja
                    System.out.println("Podaj czas odesłania notyfikacji (format HH:MM): ");
                    LocalTime time = LocalTime.parse(scanner.nextLine());
                    LocalTime now = LocalTime.now();
                    if(time.isBefore(now)){
                        throw new TimeBeforeNowException();
                    }
                    //wysyłanie notifykacji i czasu do serwera
                    bufferedWriter.write(message);
                    bufferedWriter.newLine();
                    bufferedWriter.write(time.toString());
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                }catch(TimeBeforeNowException e){
                    System.out.println("Niepoprawny czas odesłania notyfikacji (musi być to czas w przyszłości). Spróbuj ponownie.");
                }catch(IOException e){
                    closeEverything(socket,bufferedWriter, bufferedReader, scanner);
                    break;
                }catch(DateTimeParseException e){
                    System.out.println("Niepoprawny format czasu odesłania notyfikacji. Spróbuj ponownie.");
                }

            }
    }

    public void listener(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (socket.isConnected()) {
                        String message = bufferedReader.readLine();
                        System.out.println(message);
                    }
                }catch(IOException e){
                    closeEverything(socket,bufferedWriter, bufferedReader, scanner);
                }
            }
        }).start();
    }

    public void closeEverything(Socket socket, BufferedWriter bufferedWriter, BufferedReader bufferedReader, Scanner scanner){
        try{
            if(scanner != null) scanner.close();
            if(bufferedWriter != null) bufferedWriter.close();
            if(bufferedReader != null) bufferedReader.close();
            if(socket != null) socket.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("127.0.0.1", 4000);
        Client client = new Client(socket);
        client.listener();
        client.sender();
    }

}
