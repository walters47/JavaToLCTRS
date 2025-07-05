import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import transformer.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Main class of JavaToLCTRS
 */
public class JavaToLCTRS {

    /**
     * Main function
     * @param args first arg should be filepath of java file
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Filepath to source code required.");
            return;
        }
        try {
            String sourceCode = new String((Files.readAllBytes(Paths.get(args[0]))));
            CompilationUnit cu = StaticJavaParser.parse(sourceCode);
            TreeTransformer transformer = new ASTTransformer();
            ASTNode root = transformer.transformTree(cu);
            TextGenerator textGenerator = new LCTRSTextGenerator();
            String output = textGenerator.generateText(root);
            textGenerator.writeToFile(output, Paths.get(args[0]).toFile());
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error parsing Java source code: " + e.getMessage());
        }
    }
}
