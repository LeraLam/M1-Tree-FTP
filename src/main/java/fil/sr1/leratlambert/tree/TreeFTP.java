package fil.sr1.leratlambert.tree;

import fil.sr1.leratlambert.client.TreeFTPClient;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * TreeFTP implements Interface Tree.
 * Will generate our TreeFTP using one TreeFTPClient.
 */
public class TreeFTP implements Tree {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_CYAN = "\u001B[36m";

    /** TreeFTPClient used for communication with the ftp server. */
    private static TreeFTPClient client;
    /** enable / disable color mode */
    private boolean colorMode;
    /** max depth to reach */
    private int maxDepth;

    /**
     * Create an instance of TreeFTP
     * @param address Server's address.
     * @param port Port to connect.
     * @param login USER from RFC 959.
     * @param password PASS from RFC 959.
     * @param colorMode, boolean to enable color mode.
     * @param maxDepth, max depth to reach into our files system.
     * @throws IOException
     */
    public TreeFTP(String address, int port, @Nullable String login, @Nullable String password, boolean colorMode, int maxDepth) throws IOException {
        this.client = new TreeFTPClient();
        String l = (login == null) ? "anonymous" : login;
        String p = (password == null) ? "paul.leratlambert.etu@univ-lille.fr" : password;   /* password for anonymous is email address*/
        this.client.connect(address, port, l, p);   /* Connect our client */
        this.colorMode = colorMode;
        this.maxDepth = maxDepth;
    }

    /**
     * Execute the tree function.
     * then disconnect
     * @throws IOException
     */
    public void exec() throws IOException {
        if (this.colorMode) {
            System.out.println(ANSI_BLUE + '.' + ANSI_RESET);
        }
        else
        {
            System.out.println('.');
        }
        this.run(0, false);
        this.client.disconnect();
        this.client = null;

    }

    /**
     * Dive into our files structure.
     * @param dir, the dir to dive.
     * @throws IOException
     * @return
     */
    @Override
    public boolean dive(String dir) throws IOException {
       return(this.client.CWD(dir));
    }

    /**
     * Climb into our files structure
     * @throws IOException
     * @return
     */
    @Override
    public boolean climb() throws IOException {
        return this.client.CDUP();
    }

    /**
     * Print file or directory send as parameters.
     * @param fileOrDirName, name to print.
     * @param depth, depth of the file or directory.
     * @param color, color to print.
     * @param isLast, boolean to print the good prefix of indentation.
     */
    @Override
    public void printWithIdent(String fileOrDirName, int depth, String color, boolean isLast, boolean parentDirIsLast) {
        String prefix = new String();
        for (int i = 0; i < depth; i = i + 1) {
            if (parentDirIsLast) {  /* Choose good prefix */
                prefix = prefix + " ";
            }
            else {
                prefix = prefix + "│";
            }
            prefix = prefix + "\t";
        }
        prefix = prefix + (isLast ? "└── " : "├── "); /* Choose good prefix */
        if (this.colorMode) {
            System.out.println(prefix + color + fileOrDirName + ANSI_RESET);
        }
        else {
            System.out.println(prefix + fileOrDirName);
        }
    }

    /**
     * Run the tree command.
     * @throws IOException
     */
    @Override
    public void run(int depth, boolean parentDirIsLast) throws IOException {
        assert this.client.isConnect();
        this.client.PWD();  /* We must PWD to get the good current dir */
        String currentDir = this.client.getCurrentDir();
        this.client.PASV(); /* We must PASV before LIST */
        this.client.LIST();
        List<String> listN = transformListToGetOnlyFileName(this.client.getListCurrentDir());
        if (this.maxDepth < 0 || depth < this.maxDepth ) {
            for (String name :  listN) {
                boolean isLast = listN.indexOf(name) == listN.size() -1;    /* Check if the current file or directory is the last from his directory */
                if (this.dive(currentDir + '/' + name)) {   /* dive into new directory */
                    this.printWithIdent(name, depth, ANSI_BLUE, isLast, parentDirIsLast); /* DIRECTORY */
                    this.run(depth + 1, isLast);
                } else {
                    if (name.contains("->")) {  /* SYMBOLIC LINK */
                        this.printWithIdent(name, depth, ANSI_CYAN, isLast, parentDirIsLast);
                    } else {    /* FILE */
                        this.printWithIdent(name, depth, ANSI_RESET, isLast, parentDirIsLast);
                    }
                }
            }
        }
        this.climb();   /* We can climb when we have browse all file in our current directory */
    }

    /**
     * Transform list get from LIST call, to get only the file name.
     * @param list, the list get from the call of LIST command.
     * @return a new list, filled only with name of file and directory.
     */
    public List<String> transformListToGetOnlyFileName(List<String> list) {
        List<String> newList = new ArrayList<>();
        for (String s : list) {
            StringTokenizer st = new StringTokenizer(s); /* String Tokenizer to split our string */
            String lastTmp = new String();
            String toAdd = new String();
            String tmp = null;
            while (st.hasMoreTokens()) {
                tmp = st.nextToken();
                if (tmp.equals("->")) /* the file is a symbolic link */
                {
                    toAdd = lastTmp + " -> ";
                }
                lastTmp = tmp;
            }
            toAdd = toAdd + tmp;
            newList.add(toAdd);
        }
        return newList;
    }

    /**
     * Getter for this.client.
     * Used for test.
     * @return this.client.
     */
    public static TreeFTPClient getClient() {
        return client;
    }
}
