package com.nsoz.network;

public interface ISession {
    boolean isConnected();

    void setHandler(IMessageHandler messageHandler);

    void setService(Service service);

    void sendMessage(Message message);

    void close();
}
