package fil.sr1.leratlambert.client.utils;

/**
 * Enum for expected responses for each command.
 */
public enum EnumCommand {

    /** CONNECT : Expected response : 220 */
    CONNECT("220"),
    /** USER : Expected response : 331 */
    USER("331"),
    /** PASS : Expected response : 230 */
    PASS("230"),
    /** QUIT : Expected response : 221 */
    QUIT("221"),
    /** PWD : Expected response : 257 */
    PWD("257"),
    /** CWD : Expected response : 250 */
    CWD("250"),
    /** CDUP : Expected response : 250 */
    CDUP("250"),
    /** PASV : Expected response : 227 */
    PASV("227"),
    /** LIST : Expected response : 150 */
    LIST("150"),
    /** NOOP : exepeced response : 200. Used for test */
    NOOP("200");

    private String expectedResponse;

    /**
     * Get the expected response.
     * @return expected response.
     */
    public String getExpectedResponse()
    {
        return this.expectedResponse;
    }

    /**
     * The constructor for this enum
     * @param expectedResponse, the expected response of command send.
     */
    private EnumCommand(String expectedResponse)
    {
        this.expectedResponse = expectedResponse;
    }
}
