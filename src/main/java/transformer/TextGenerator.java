package transformer;

import java.io.File;

/**
 * Default constructor
 */
public interface TextGenerator {

    /**
     * Generates text with LCTRS grammar from AST
     * @param root root node of AST
     * @return String representation of LCTRS
     */
    String generateText (ASTNode root);

    /**
     * Creates and writes output file in same directory as input file. Name is same as input file with .ari extension
     * @param text String representation of formatted LCTRS
     * @param inputFile initial input file
     */
    void writeToFile (String text, File inputFile);
}
