package com.example.chat;

import android.util.Log;
import android.util.Pair;

import androidx.core.util.Consumer;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;



public class Server {
    private WebSocketClient client;
    private Map<Long, String> names = new ConcurrentHashMap<>();

    private Consumer<Pair<String, String>> onMessageReceived;
    private Consumer<Integer> onUpdateStatus;
    private Consumer<String> onUserConnect;

    //public Server(Consumer<Pair<String, String>> onMessageReceived) {
     //   this.onMessageReceived = onMessageReceived;

    //}
    public Server (
            Consumer<Pair<String, String>> onMessageReceived
         //   Consumer<Integer> onUpdateStatus,
         //   Consumer<String> onUserConnect
    ) {
        this.onMessageReceived = onMessageReceived;
      //  this.onUpdateStatus = onUpdateStatus;
      //  this.onUserConnect = onUserConnect;
    }

    public void connect() {
        //Выполняем подключение к серверу
        //35.214.1.221
        URI address = null;
        try {
            address = new URI("ws://35.214.1.221:8881");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        client = new WebSocketClient(address) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                Log.i("SERVER", "Connected to server");
               // sendName("Anatolyie");

            }

            @Override
            public void onMessage(String json) {
                Log.i("SERVER", "Got json from server:" + json);
                int type = Protocol.getType(json);
                if(type == Protocol.MESSAGE){
                    //Пришло входящее сообщение
                    displayIncoming(Protocol.unpackMessage(json));
                }
                if (type == Protocol.USER_STATUS){
                    //Пришел статус пользователя
                    updateStatus(Protocol.unpackStatus(json));
                }
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                Log.i("SERVER", "Connected closed");
            }

            @Override
            public void onError(Exception ex) {
                Log.i("SERVER", "ERROR:" + ex.getMessage());
            }
        };
        client.connect();
    }

    public void sendName(String name) {
        Protocol.UserName userName = new Protocol.UserName(name);
        if (client != null && client.isOpen()) {
            client.send(Protocol.packName(userName));
        }
    }

    public void sendMessage (String text){
        try {
            text = Crypto.encrypt(text);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Protocol.Message mess = new Protocol.Message(text);
        if (client != null && client.isOpen()){
            client.send(Protocol.packMessage(mess));
        }
    }

    private void updateStatus(Protocol.UserStatus status){
        //Запомнить что какой то пользвателеь (имя и id) имеет какой то статус
        Protocol.User user = status.getUser();
        if (status.isConnected()){
          //  String userName = user.getName();
            //при подключении - кладем
            names.put(user.getId(), user.getName());
          //  onUserConnect.accept(userName);
        } else {
            //при отключении - удаляем
             names.remove(user.getId());
        }
        //onUpdateStatus.accept(names.size());
    }
    private void displayIncoming(Protocol.Message message){
        String name = names.get(message.getSender());
        if (name == null) {
            name = "Unnamed";
        }
        String text = null;
        try {
             text = Crypto.decrypt(message.getEncodedText());
        } catch (Exception e) {
            e.printStackTrace();
        }

        onMessageReceived.accept(
                new Pair<>(name, text) // отправка в Маин пришедшее сообщение
        );
    }
}

