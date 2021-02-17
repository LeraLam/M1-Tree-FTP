package fil.sr1.leratlambert;


import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    static String EXPECTED_OUTPUT = "Usage : serverAddress [login] [options]\n" +
                "Options :\n" +
                "-u <String> : your login\n" +
                "-p <String> : your password\n" +
                "-c : set color mode at true\n" +
                "-d <int> : max depth to reach\n    ";

    /**
     * Test main method from App
     * @throws IOException
     */
    @Test
    public void testMain() throws IOException {
        PrintStream oldOut = System.out; /* Memorise old System.out */
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        String[] s = new String[0];
        App.main(s);
        String output = new String(outputStream.toByteArray());
        assertEquals( output, EXPECTED_OUTPUT);
        System.setOut(oldOut); /* set oldOut as output for print method */
    }
}
