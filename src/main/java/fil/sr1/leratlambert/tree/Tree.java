package fil.sr1.leratlambert.tree;

import java.io.IOException;

/**
 * Interface describing usual method for tree command
 */
public interface Tree {

    /**
     * Dive into our files structure.
     * @param dir, the dir to dive.
     * @throws IOException
     * @return
     */
    public boolean dive(String dir) throws IOException;

    /**
     * Climb into our files structure
     * @throws IOException
     * @return
     */
    public boolean climb() throws IOException;

    /**
     * Print file or directory send as parameters.
     * @param fileOrDirName, name to print.
     * @param depth, depth of the file or directory.
     * @param color, color to print.
     * @param isLast, boolean to print the good prefix of indentation.
     * @param parentDirectoryIsLast, to print the good prefix of indentation.
     */
    public void printWithIdent(String fileOrDirName, int depth, String color, boolean isLast, boolean parentDirectoryIsLast);

    /**
     * Run the tree command.
     * @param depth, current depth.
     * @param parentDirIsLast, is the parent directory which call run the last of his own parent directory.
     * @throws IOException
     */
    public void run(int depth, boolean parentDirIsLast) throws IOException;
}
