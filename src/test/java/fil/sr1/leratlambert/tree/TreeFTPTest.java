package fil.sr1.leratlambert.tree;

import fil.sr1.leratlambert.client.ClientFTP;
import org.junit.jupiter.api.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Class Test for TreeFTP
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TreeFTPTest {

    /** The ClientFTP for test. */
    private static TreeFTP tree;
    /** ByteArrayOutputStream to test output */
    private static ByteArrayOutputStream outputStream;
    /** Memorise our current out for print */
    private static PrintStream oldOut;
    /** String for test */
    private static String EXPECTED_OUTPUT_1 = "├── test1\n" +
            " \t \t├── test2\n" +
            "│\t│\t└── test3\n";
    /** String for test */
    private static String EXPECTED_OUTPUT_2 = "├── cdimage\n" +
            "├── cloud-images\n" +
            "├── maas-images\n" +
            "├── old-images\n" +
            "├── releases\n" +
            "├── simple-streams\n" +
            "├── ubuntu\n" +
            "├── ubuntu-cloud-archive\n" +
            "└── ubuntu-ports\n";
    /** String for test */
    private static String EXPECTED_OUTPUT_3 = ".\n" +
            "├── cdimage\n" +
            "├── cloud-images\n" +
            "├── maas-images\n" +
            "├── old-images\n" +
            "├── releases\n" +
            "├── simple-streams\n" +
            "├── ubuntu\n" +
            "├── ubuntu-cloud-archive\n" +
            "└── ubuntu-ports\n";

    /**
     * Sets up the test fixture
     * @throws IOException
     */
    @BeforeAll
    public static void setUp() throws IOException {
        tree = new TreeFTP("ftp.ubuntu.com", 21, null, null, false, 1);
        oldOut = System.out; /* Memorise old System.out */
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream)); /* Set outputStream as output for print method */
    }

    /**
     * Called before each test, it will reset our outputStream.
     * We need this reset to compare expected output to effective output.
     * @throws IOException
     */
    @Test
    @BeforeEach
    public void beforeEach() throws IOException {
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
    }

    /**
     * Tears down the test fixture.
     */
    @AfterAll
    public static void tearDown() {
        tree = null;
        System.setOut(oldOut); /* set oldOut as output for print method */
    }

    /**
     * Test for dive
     * @throws IOException
     */
    @Test
    @Order(1)
    public void testDive() throws IOException {
        assert tree.getClient().isConnect();
        String dirToDive;
        List<String> list;
        List<String> nList;
        assertTrue(tree.getClient().PASV()); /* Call pasv to call list after */
        assertTrue(tree.getClient().LIST());
        list = tree.getClient().getListCurrentDir();
        nList = tree.transformListToGetOnlyFileName(list);
        dirToDive = "/" + nList.get((int) Math.random() * (nList.size() + 1)); /* Get a random directory to go in */
        assertTrue(tree.dive(dirToDive));
        tree.getClient().PWD();
        assertTrue(tree.getClient().getCurrentDir().equals(dirToDive));
    }

    /**
     * Test for climb
     * @throws IOException
     */
    @Test
    @Order(2)
    public void testClimb() throws IOException {
        assert tree.getClient().isConnect();
        tree.climb();
        tree.getClient().PWD();
        assertTrue(tree.getClient().getCurrentDir().equals("/"));
    }

    /**
     * Test for printWithIdent.
     */
    @Test
    @Order(3)
    public void testPrintWithIdent() {
        tree.printWithIdent("test1", 0, "", false, false);
        tree.printWithIdent("test2", 2, "", false, true);
        tree.printWithIdent("test3", 2, "", true, false);
        String output = new String(outputStream.toByteArray());
        assertEquals(output, EXPECTED_OUTPUT_1);
    }

    /**
     * Test for run.
     * @throws IOException
     */
    @Test
    @Order(4)
    public void testRun() throws IOException {
        assert tree.getClient().isConnect();
        tree.run(0, false);
        String output = new String(outputStream.toByteArray());
        assertEquals(output, EXPECTED_OUTPUT_2);    }

    /**
     * Test transformListTOGetOnlyFileName.
     */
    @Test
    @Order(5)
    public void testTransformListToGetOnlyFileName() {
        List<String> listToTest = new ArrayList<>();
        List<String> newList = new ArrayList<>();
        listToTest.add("drwxrwxr-x    5 997      997          4096 Aug 06 23:38 daily-live");
        listToTest.add("lrwxrwxrwx    1 997      997            23 Jul 26  2018 source -> ../source/bionic/source");
        newList = tree.transformListToGetOnlyFileName(listToTest);
        assertTrue(newList.contains("daily-live"));
        assertTrue(newList.contains("source -> ../source/bionic/source"));
    }

    /**
     * Test exec.
     * @throws IOException
     */
    @Test
    @Order(6)
    public void testExec() throws IOException {
        assert tree.getClient().isConnect();
        ClientFTP c= tree.getClient();
        tree.exec();
        assertFalse(c.isConnect());
        String output = new String(outputStream.toByteArray());
        assertEquals(output, EXPECTED_OUTPUT_3);
    }
}
