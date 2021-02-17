package fil.sr1.leratlambert.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static fil.sr1.leratlambert.client.utils.EnumCommand.*;

public class TreeFTPClient extends ClientFTP  {

    /** The socket which is used for passive mode */
    private PassiveModeClient passiveModeClient;
    /** List of file and directory */
    private List<String> listCurrentDir;
    /** Current dir */
    private String currentDir;

    /**
     * Create an instance of TreeFTPClient.
     */
    public TreeFTPClient() {
        super();
        this.passiveModeClient = new PassiveModeClient();   /* Client to get data in passive mode */
    }

    /**
     * Execute the FTP command : PWD, using EnumCommand.PWD and methode sendCommand inherited from ClientFTP.
     * @return <code>true</code> if the command was successful.
     *         <code>false</code> otherwise.
     * @throws IOException
     */
    public  Boolean PWD() throws IOException {
        if (this.sendCommand(PWD)) {
            Pattern pattern = Pattern.compile("\"\\S+\"");  /* Pattern using regex to get only the current dir */
            Matcher matcher = pattern.matcher(this.response);
            if (matcher.find())
            {
                this.currentDir = matcher.group().replace("\"", "");
            }
            return true;
        }
        return false;
    }

    /**
     * Execute the FTP command : CWD, using EnumCommand.CWD and methode sendCommand inherited from ClientFTP.
     * @return <code>true</code> if the command was successful.
     *         <code>false</code> otherwise.
     * @throws IOException
     */
    public boolean CWD(String dir) throws IOException {
        return this.sendCommand(CWD, dir);
    }

        /**
         * Execute the FTP command : PASV, using EnumCommand.PASV and methode sendCommand inherited from ClientFTP.
         * It also connect our PassiveModeClient to the ip and port given by the server.
         * @return <code>true</code> if the command was successful and the PassiveModeClient successfully connected..
         *         <code>false</code> otherwise.
         * @throws IOException
         */
        public boolean PASV() throws IOException {
            if (this.sendCommand(PASV)) {
                Pattern pattern = Pattern.compile("(\\d+,)+\\d+");  /* Pattern regex to get only the number for ip and port. */
                Matcher matcher = pattern.matcher(this.response);
                if (matcher.find()) {
                    List<String> myList = new ArrayList<String>(Arrays.asList(matcher.group().split(","))); /* Splitting all number into a list */
                    int i = 0;
                    int portPassiveMode = 0;
                    String ipPassiveMode = new String();
                    for(String number : myList) {
                        if(i < 4){
                            ipPassiveMode = ipPassiveMode + number + "."; /* Generate ip */
                        }
                        else if (i == 4){
                            ipPassiveMode = ipPassiveMode.substring(0, ipPassiveMode.length() - 1); /* removing the last . */
                            portPassiveMode = Integer.parseInt(number) * 256;   /* port = 256 * mylist[4] + myList[5] */
                        }
                        else {
                            portPassiveMode = portPassiveMode + Integer.parseInt(number);   /* port = 256 * mylist[4] + myList[5] */
                        }
                        i = i + 1;
                    }
                    this.passiveModeClient.connect(ipPassiveMode, portPassiveMode, this.login, this.password);  /* Connecting our passive mode client with ip and port given by pasv command  */
                    if (this.passiveModeClient.isConnect()) { /* Check if connection going well */
                    } else {
                        System.out.println("Connection passive mode failed");
                    }
                    return(this.passiveModeClient.isConnect());
                }
            }
            return false;
        }

    /**
     * Execute the FTP command : LIST, using EnumCommand.LIST and methode sendCommand inherited from ClientFTP.
     * It also fill the list this.listCurrentDir with the result of the LIST command.
     * @return <code>true</code> if the command was successful.
     *         <code>false</code> otherwise.
     * @throws IOException
     */
    public boolean LIST() throws IOException {
        if (this.sendCommand(LIST)) {
            this.passiveModeClient.getResponseFromServer();
            this.listCurrentDir = null;
            this.listCurrentDir = new ArrayList<String>(Arrays.asList(this.passiveModeClient.getResponse().split("\\n")));  /* Create a list with all fies/directory and info about them*/
            this.passiveModeClient.disconnect();    /* disconnect our passive mode client */
            this.getResponseFromServer();   /* LIST command return two response, so we need to read another one */
            return true;
        }
        return false;
    }

    /**
     * Execute the FTP command : CDUP, using EnumCommand.CDUP and methode sendCommand inherited from ClientFTP.
     * @return <code>true</code> if the command was successful.
     *         <code>false</code> otherwise.
     * @throws IOException
     */
    public boolean CDUP() throws IOException {
        return this.sendCommand(CDUP);
    }

    /**
     * Getter for current dir.
     * @return this.currentDir
     */
    public String getCurrentDir() { return currentDir; }

    /**
     * Getter for this.listCurrentDir.
     * @return this.listCurrentDir.
     */
    public List<String> getListCurrentDir() {
        return listCurrentDir;
    }
}
