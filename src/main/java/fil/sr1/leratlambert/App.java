package fil.sr1.leratlambert;

import fil.sr1.leratlambert.tree.TreeFTP;

import java.io.IOException;

/**
 * Main for TreeFTP
 */
public class App {
    private static String serverAddress;
    private static String login;
    private static String password;

    /**
     * Print usage.
     */
    public static void usage() {
        System.out.println( "Usage : serverAddress [login] [options]");
        System.out.println( "Options :\n" +
                            "-u <String> : your login\n" +
                            "-p <String> : your password\n" +
                            "-c : set color mode at true\n" +
                            "-d <int> : max depth to reach");
    }

    /**
     * Call our treeFTP.
     * @param args
     * @throws IOException
     */
    public static void main( String[] args ) throws IOException {
        TreeFTP tree;
        String serverAddress = "";
        String login = null;
        String password = null;
        boolean colorMode = false;
        int maxDepth = -1;
        if (args.length == 0) {
            usage();
        }
        else {
            serverAddress = args[0];
            if (args.length >= 3) {
                for (int i = 1; i < args.length; i = i + 1) {
                    switch (args[i]) {
                        case "-u":
                            login = args[i + 1];
                            break;
                        case "-p":
                            password = args[i + 1];
                            break;
                        case "-c":
                            colorMode = true;
                            break;
                        case "-d":
                            maxDepth = Integer.parseInt(args[i + 1]);
                            ;
                            break;
                        default:
                            break;
                    }
                }
            }
            if (login == null || password == null) {
                tree = new TreeFTP(serverAddress, 21, null, null, colorMode, maxDepth);

            } else {
                tree = new TreeFTP(serverAddress, 21, login, password, colorMode, maxDepth);
            }
            tree.exec();
        }
    }
}
