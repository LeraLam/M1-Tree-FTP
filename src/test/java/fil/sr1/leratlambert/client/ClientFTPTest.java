package fil.sr1.leratlambert.client;

import org.junit.jupiter.api.*;

import java.io.*;

import static fil.sr1.leratlambert.client.utils.EnumCommand.*;
import static org.junit.Assert.*;

/**
 * Class Test for ClientFTP
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ClientFTPTest {

    /** The ClientFTP for test. */
    private static ClientFTP clientFTP = new ClientFTP(); /* ClientFTP for test */

    /**
     * Tears down the test fixture.
     */
    @AfterAll
    public static void tearDown() {
        clientFTP = null;
    }

    /**
     * Test connection
     * @throws IOException
     */
    @Test
    @Order(1)
    public void TestConnect() throws IOException {
        clientFTP.connect("ftp.ubuntu.com", 21, "anonymous", "paul.leratlambert.etu@univ-lille.fr");
        assertTrue(clientFTP.isConnect());
    }

    /**
     * Test isConnect while Client FTP is connect to a server.
     */
    @Test
    @Order(2)
    public void TestIsConnectWhileConnected() {
        assertTrue(this.clientFTP.isConnect());
    }

    /**
     * Test sendingMessage to server.
     * @throws IOException
     */
    @Test
    @Order(3)
    public void TestSendMessageAndGetResponseFromServer() throws IOException {
        clientFTP.sendMessageToServer("NOOP");
        clientFTP.getResponseFromServer();
        String response = clientFTP.getResponse();
        assertEquals(response, "200 NOOP ok.");
    }

    /**
     * test for sendCommand.
     * @throws IOException
     */
    @Test
    @Order(4)
    public void testSendCommand() throws IOException { /* Testing some command, i have chosen those ones because they won't create unexpected issues*/
        assertTrue(clientFTP.sendCommand(NOOP));
        assertTrue(clientFTP.sendCommand(PWD));
        assertTrue(clientFTP.sendCommand(CWD, "/"));
        assertTrue(clientFTP.sendCommand(CDUP));
    }

    /**
     * test disconnection.
     * @throws IOException
     */
    @Test
    @Order(5)
    public void TestDisconnect() throws IOException {
        clientFTP.disconnect();
        assertFalse(clientFTP.isConnect());
    }

    /**
     * Test isConnect while Client FTP is not connect to a server.
     */
    @Test
    @Order(6)
    public void TestIsConnectWhileDisconnected() {
        assertFalse(this.clientFTP.isConnect());
    }

}
