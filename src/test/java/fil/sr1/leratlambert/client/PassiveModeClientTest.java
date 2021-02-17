package fil.sr1.leratlambert.client;

import org.junit.jupiter.api.*;

import java.io.*;

import static org.junit.Assert.*;

/**
 * Class Test for PassiveModeClient
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PassiveModeClientTest{

    /** The ClientFTP for test. */
    private static PassiveModeClient passiveModeClient = new PassiveModeClient();
    /** The ThreadServer for test. */

    /**
     * Tears down the test fixture.
     */
    @AfterAll
    public static void tearDown() {
        passiveModeClient = null;
    }

    /**
     * Test connection
     * @throws IOException
     */
    @Test
    @Order(1)
    public void TestConnect() throws IOException {
        passiveModeClient.connect("ftp.free.fr", 21, "anonymous", "paul.leratlambert.etu@univ-lille.fr");
        passiveModeClient.getResponseFromServer();
        assertTrue(passiveModeClient.isConnect());
    }

    /**
     * Test isConnect while Client FTP is connect to a server.
     */
    @Test
    @Order(2)
    public void TestIsConnectWhileConnected() {
        assertTrue(this.passiveModeClient.isConnect());
    }

    /**
     * Test sendingMessage to server.
     * @throws IOException
     */
    @Test
    @Order(3)
    public void TestSendMessage() throws IOException {
        passiveModeClient.sendMessageToServer("USER" + " anonymous");
        passiveModeClient.getResponseFromServer();
        passiveModeClient.sendMessageToServer("PASS" + " paul.leratlabert.Etu@niv-lille.fr");
        passiveModeClient.getResponseFromServer();
        String response = passiveModeClient.getResponse().replaceAll("\n", "").replaceAll("\r", "");
        assertEquals(response, "230 Login successful.");
    }

    /**
     * test getResponseFromServer.
     * @throws IOException
     */
    @Test
    @Order(4)
    public void TestGetResponseFromServer() throws IOException {
        passiveModeClient.sendMessageToServer("NOOP");
        passiveModeClient.getResponseFromServer();
        String response = passiveModeClient.getResponse().replaceAll("\n", "").replaceAll("\r", "");;
        assertEquals(response, "200 NOOP ok.");
    }

    /**
     * test disconnection.
     * @throws IOException
     */
    @Test
    @Order(6)
    public void TestDisconnect() throws IOException {
        passiveModeClient.disconnect();
        assertFalse(passiveModeClient.isConnect());
    }

    /**
     * Test isConnect while Client FTP is not connect to a server.
     */
    @Test
    @Order(7)
    public void TestIsConnectWhileDisconnected() {
        assertFalse(this.passiveModeClient.isConnect());
    }
}
