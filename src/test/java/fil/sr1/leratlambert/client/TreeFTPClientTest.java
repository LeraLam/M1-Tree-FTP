package fil.sr1.leratlambert.client;

import org.junit.jupiter.api.*;

import java.io.*;

import static org.junit.Assert.*;

/**
 * Class Test for TreeFTPClient
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TreeFTPClientTest {

    /** The TreeFTPClient for test. */
    private static TreeFTPClient treeFTPClient = new TreeFTPClient();

    /**
     * Sets up the test fixture
     * @throws IOException
     */
    @BeforeAll
    public static void setUp() throws IOException {
        treeFTPClient.connect("ftp.ubuntu.com", 21, "anonymous", "paul.leratlambert.etu@univ-lille.fr");
    }

    /**
     * Tears down the test fixture.
     * @throws IOException 
     */
    @AfterAll
    public static void tearDown() throws IOException {
        treeFTPClient.disconnect();
        treeFTPClient = null;
    }

    /**
     * Test for PWD.
     * @throws IOException
     */
    @Test
    @Order(1)
    public void testPWD() throws IOException {
        assertTrue(treeFTPClient.PWD());
    }

    /**
     * Test for CWD.
     * @throws IOException
     */
    @Test
    @Order(2)
    public void testCWD() throws IOException {
        assertTrue(treeFTPClient.CWD("/"));
    }

    /**
     * Test for PASV.
     * @throws IOException
     */
    @Test
    @Order(3)
    public void testPASV() throws IOException {
        assertTrue(treeFTPClient.PASV());
    }

    /**
     * Test for LIST.
     * @throws IOException
     */
    @Test
    public void testLIST() throws IOException {
        assertTrue(treeFTPClient.LIST());;
    }

    /**
     * Test for CDUP.
     * @throws IOException
     */
    @Test
    @Order(4)
    public void testCDUP() throws IOException {
        assertTrue(treeFTPClient.CDUP());
    }
}

