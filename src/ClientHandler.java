import java.io.*;
import java.net.Socket;
import java.time.LocalTime;
import java.util.ArrayList;

public class ClientHandler implements Runnable{

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private ArrayList<Message> messages;

    public ClientHandler(Socket socket) {
        try{
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.messages = new ArrayList<>();
            System.out.println("Client is connected");
        }catch(IOException e){
            closeEverything(socket, bufferedWriter, bufferedReader);
        }

    }


    @Override
    public void run(){
        try{
            sender(); //wysyłanie notyfikacji do klienta - oddzielny wątek
            while(socket.isConnected()){
                String messageText = bufferedReader.readLine();
                LocalTime time = LocalTime.parse(bufferedReader.readLine());
                Message message = new Message(messageText, time);
                messages.add(message);
                for(Message msg : messages){
                    System.out.println(msg.getTime() + " " + msg.getMessage());
                }
            }
        }catch(IOException e){
            closeEverything(socket, bufferedWriter, bufferedReader);
        }

    }

    public void sender(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    while(socket.isConnected()){
                        Thread.sleep(1000);
                        if(!messages.isEmpty()){
                            Message temp = messages.get(0);
                            LocalTime now = LocalTime.now();
                            LocalTime msgTime = temp.getTime();
                            if(now.equals(msgTime) || now.isAfter(msgTime)){
                                bufferedWriter.write("SERVER: " + msgTime + " " + temp.getMessage());
                                bufferedWriter.newLine();
                                bufferedWriter.flush();
                                messages.remove(0);
                            }
                        }
                    }
                }catch(IOException e){
                    closeEverything(socket, bufferedWriter, bufferedReader);
                }catch(InterruptedException e){
                    return;
                }
            }
        }).start();
    }

    public void closeEverything(Socket socket, BufferedWriter bufferedWriter, BufferedReader bufferedReader){
        try{

            if(bufferedWriter != null) bufferedWriter.close();
            if(bufferedReader != null) bufferedReader.close();
            if(socket != null){
                socket.close();
                System.out.println("Client disconnect");
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
