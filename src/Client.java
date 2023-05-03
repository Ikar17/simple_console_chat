import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    //private String username;

    public Client(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            //this.username = username;
        }catch(IOException e){
            closeEverything(socket, bufferedWriter, bufferedReader);
        }
    }

    public void sender(){
        try{
            Scanner console = new Scanner(System.in);
            while(socket.isConnected()){
                System.out.println("Wyślij wiadomość do serwera: ");
                String message = console.nextLine();
                System.out.println("Podaj czas odesłania notyfikacji (format HH:MM): ");
                String time = console.nextLine();
                bufferedWriter.write(message);
                bufferedWriter.newLine();
                bufferedWriter.write(time);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        }catch(IOException e){
            closeEverything(socket, bufferedWriter, bufferedReader);
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
                    closeEverything(socket,bufferedWriter, bufferedReader);
                }
            }
        }).start();
    }

    public void closeEverything(Socket socket, BufferedWriter bufferedWriter, BufferedReader bufferedReader){
        try{
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
