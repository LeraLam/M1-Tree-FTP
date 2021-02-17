package fil.sr1.leratlambert.client;

import fil.sr1.leratlambert.client.utils.EnumCommand;

import javax.annotation.Nullable;
import java.io.*;
import java.net.Socket;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import static fil.sr1.leratlambert.client.utils.EnumCommand.*;

/**
 * A Client FTP, which can connect and communicate to a FTP server.
 * Implement Interface Client.
 */
public class ClientFTP implements Client {

    /** The socket which is used for connection */
    protected static Socket socket;
    /** DataOutputStream to communicate with server */
    protected static DataOutputStream toServer;
    /** DataInputStream to intercept response from server */
    protected static DataInputStream fromServer;
    /** Reader to read message from server */
    private static  BufferedReader bufferedReader;
    /** Writer to write message to server */
    private static  BufferedWriter bufferedWriter;
    /** Login used for connection */
    protected static String login;
    /** Password used for connection */
    protected  static String password;
    /** last response from server */
    protected String response;
    /** Logger for log file */
    private Logger logger;

    /**
     * Create an instance of ClientFTP
     */
    public ClientFTP() {
        this.response = new String();
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
        this.login = login;
        this.password = password;
        this.socket = new Socket(address, port);
        this.fromServer = new DataInputStream(this.socket.getInputStream());
        this.toServer = new DataOutputStream(this.socket.getOutputStream());
        this.setupReaderWriterAndLogger();
        this.getResponseFromServer();
        if (this.response.startsWith(CONNECT.getExpectedResponse())){
            if (this.sendCommand(USER, login) && this.sendCommand(PASS, password)) {
                    return true;
                }
        }
        return false;
    }

    /**
     *  Disconnect from server.
     *  @throws IOException
     */
    @Override
    public void disconnect() throws IOException {
        assert isConnect();
        if (this.sendCommand(QUIT)) {
            this.socket.close();    /* Closing the socket */
            this.socket = null;
        }
    }

    /**
     * Setup this.bufferedReader using DataInputStream this.fromServer.
     * Setup this.bufferedWriter using DataOutputStream this.toServer/
     * @throws IOException
     */
    public void setupReaderWriterAndLogger() throws IOException {
        assert isConnect();
        this.bufferedReader = new BufferedReader(new InputStreamReader(this.fromServer));
        this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(this.toServer));

        this.logger = Logger.getLogger("log");
        FileHandler fh;
        fh = new FileHandler("tree.log");
        logger.addHandler(fh);  /* Our logger will now writ on file tree.log */
        logger.setUseParentHandlers(false); /* Remove log to standard output */
        SimpleFormatter formatter = new SimpleFormatter();
        fh.setFormatter(formatter); /* Change formatter to get better ouput in our tree.log*/
    }

    /**
     * get the response from the server, using this.bufferedReader.
     * It will use our parameter this.response to memorise this response.
     * @throws IOException
     */
    @Override
    public void getResponseFromServer() throws IOException {
        assert isConnect();
        this.response = this.bufferedReader.readLine(); /* Read one line from the input */
        logger.info("<-- " + this.response);

    }

    /**
     * Getter for this.response
     * @return this.response.
     */
    public String getResponse() {
        return response;
    }

    /**
     * Send a message to the server, using this.bufferedWriter.
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
     * Send a command to the server
     * @param command, the command to send.
     * @param data, if the command need some data, can be null.
     * @return <code>true</code> if the command was successful.
     *         <code>false</code> otherwise.
     * @throws IOException
     */
    public boolean sendCommand(EnumCommand command, @Nullable String data) throws IOException {
        assert isConnect();
        if (data == null) {
            this.sendMessageToServer(command.name());
            logger.info("--> " + command.name());

        } else {
            this.sendMessageToServer(command.name() + " " + data);
            logger.info("--> " + command.name() + " " + data);
        }
        this.getResponseFromServer();
        if (this.response != null && this.response.startsWith(command.getExpectedResponse())) { /* Check if we get the expected response */
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Send a command to the server
     * @param command, the command to send.
     * @return <code>true</code> if the command was successful.
     *         <code>false</code> otherwise.
     * @throws IOException
     */
    public boolean sendCommand(EnumCommand command) throws IOException {
        return this.sendCommand(command, null); /* Calling sendCommand with null as data */
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
}
