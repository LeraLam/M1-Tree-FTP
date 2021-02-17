package fil.sr1.leratlambert.client;

import java.io.*;

/**
 * Interface client, to simulate client in Server/Client model.
 */
public interface Client {

    /**
     * Connect to a server described by his address and his port.
     * @param address Server's address.
     * @param port  Server's port.
     * @param Login If needed, the login used for connection.
     * @param password If needed, the password used for connection.
     * @throws IOException
     * @return <code>true</code> if the login was successful,
     *         <code>false</code> otherwise.
     */
    public boolean connect(String address, int port, String Login, String password) throws IOException;

    /**
     * Disconnect from a server.
     * @throws IOException
     */
    public void disconnect() throws IOException;

    /**
     * get the response from the server.
     * @throws IOException
     */
    public void getResponseFromServer() throws IOException;

    /**
     * Send a message to the server.
     * @param data The message to send.
     * @throws IOException
     */
    public void sendMessageToServer(String data) throws IOException;

    /**
     * Check if the client is connected.
     * @return  <code>true</code> if this ClientFTP is connect,
     *          <code>false</code> otherwise.
     */
    public boolean isConnect();
}
