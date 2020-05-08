package com.example.chat;

import com.google.gson.Gson;

public class Protocol {
    //взаимодействие с сервером
    //USER_STATUS - онлайн или оффлайн
    //MESSAGE - вх.исх сообщение
    //USER_NAME - сообщаем наше имя серверу

    public final static int USER_STATUS = 1;
    public final static int MESSAGE = 2;
    public final static int USER_NAME = 3;

    //USER_NAME:    3{name: "Anatolyi"}
    //USER_STATUS:  1{connected: false, user: {name:"Vasya", id: 1828282}}
    //MESSAGE       2{encodedText: "Privet", sender: 1828282}

    static class UserStatus {
        private boolean connected;
        private User user;

        public UserStatus() {
        }

        public boolean isConnected() {
            return connected;
        }

        public void setConnected(boolean connected) {
            this.connected = connected;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }
    }

       static class User {
        private String name;
        private long id;

           public User() {
           }

           public String getName() {
               return name;
           }

           public void setName(String name) {
               this.name = name;
           }

           public long getId() {
               return id;
           }

           public void setId(long id) {
               this.id = id;
           }
       }

    static class UserName {
        private String name;

        public UserName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    static class Message {
        public final static int GROUP_CHAT = 1;
        private long sender;
        private String encodedText;
        private long receiver = GROUP_CHAT;

        public long getSender() {
            return sender;
        }

        public void setSender(long sender) {
            this.sender = sender;
        }

        public String getEncodedText() {
            return encodedText;
        }

        public void setEncodedText(String encodedText) {
            this.encodedText = encodedText;
        }

        public long getReceiver() {
            return receiver;
        }

        public void setReceiver(long receiver) {
            this.receiver = receiver;
        }

        public Message(String encodedText) {
            this.encodedText = encodedText;
        }
    }

    //Будем вызывать при отправке своего имени на сервер
    public static String packName(UserName name) {
        Gson g = new Gson(); //поможет запаковать в json
        return USER_NAME + g.toJson(name);  //3{name: "Anatolyi"}
    }

    //Как получать с сервера прочую информацию: Статусы, имена, сообщения
    //Узнать что за сообщение пришло
    public static int getType(String json) {
        if (json == null || json.length() == 0) {
            return -1;
        }
        return Integer.parseInt(json.substring(0, 1));
    }

    public static String packMessage(Message mess) {
        Gson g = new Gson();
        return MESSAGE + g.toJson(mess);
    }

    public static Message unpackMessage(String json) {
        Gson g = new Gson();
       return g.fromJson(json.substring(1), Message.class);
    }

    public static UserStatus unpackStatus(String json){
        Gson g = new Gson();
        return g.fromJson(json.substring(1), UserStatus.class);
    }

}
