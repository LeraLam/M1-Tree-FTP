package fil.sr1.leratlambert.client;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class PassiveModeClient implements Client {

    /** Buffer size to read data */
    private static int BUFFER_SIZE = 1024;
    /** BufferedInputStream to read data send by server */
    private static BufferedInputStream bufferedInputStream;
    /** The socket which is used for connection */
    private static Socket socket;
    /** Writer to write message to server */
    private static  BufferedWriter bufferedWriter;
    /** String used to memorise last response read from server.*/
    private String response;

    /**
     * Create in instance of passive mode client.
     */
    public PassiveModeClient() {
        this.response = new String(); /* Create an instance of string */
    }

    /**
     * Connect to FTP server.
     * @param address Server's address.
     * @param port Port to connect.
     * @param login USER from RFC 959.
     * @param password PASS from RFC 959.
     * @throws IOException
     * @return <code>true</code> if the login was successful,
     *         <code>false</code> otherwise.
     */
    @Override
    public boolean connect(String address, int port, String login, String password) throws IOException {
        this.socket = new Socket(address, port);
        this.bufferedInputStream = new BufferedInputStream(this.socket.getInputStream()); /* buffered input to read data */
        this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
        return isConnect();
    }

    /**
     * Disconnect from a server.
     * @throws IOException
     */
    @Override
    public void disconnect() throws IOException {
        assert isConnect();
        this.bufferedInputStream.close();
        this.socket.close();
        this.socket = null;
    }

    /**
     * get the response from the server, using this.bufferedReader.
     * It will use our parameter this.response to memorise this response.
     * @throws IOException
     */
    @Override
    public void getResponseFromServer() throws IOException { /* We use byte to read all data, and not buffered reader as in our FTPClient */
        assert isConnect();
        this.response = new String();
        byte[] buffer = new byte[BUFFER_SIZE]; /* buffer for reading data */
        int bytesRead = this.bufferedInputStream.read(buffer);
        if (new String(buffer).split("\0").length > 0) {
            this.response = this.response + new String(buffer).split("\0")[0]; /* add a new String using bytes from our buffer, split will remove useless \0 at the end of our string */
        }
        if (bytesRead == BUFFER_SIZE) {
        while ((bytesRead = this.bufferedInputStream.read(buffer)) != -1) {
            this.response = this.response + new String(buffer).split("\0")[0];;
        }
        }
        this.response.replaceAll("\n", ""); /* removing useless \n at the end of our string */
    }

    /**
     * Send a message to the server.
     * @param data The message to send.
     * @throws IOException
     */
    @Override
    public void sendMessageToServer(String data) throws IOException {
        assert isConnect();
        bufferedWriter.write(data + "\n");
        bufferedWriter.flush(); /* Empty writing buffers into the output */
    }

    /**
     * Check if this ClientFTP is connected.
     * @return  <code>true</code> if this ClientFTP is connect,
     *          <code>false</code> otherwise.
     */
    @Override
    public boolean isConnect() {
        return (this.socket != null);
    }

    /**
     * Getter for this.response
     * @return this.response.
     */
    public String getResponse() {
        return response;
    }
}
