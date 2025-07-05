import org.junit.jupiter.api.Test;
import transformer.ASTNode;
import transformer.DefaultASTNode;
import transformer.LCTRSTextGenerator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

    class LCTRSTextGeneratorTest {

        @Test
        void testGenerateText() {
            ASTNode root = createTree();
            LCTRSTextGenerator generator = new LCTRSTextGenerator();
            String result = generator.generateText(root);
            System.out.println(result);
            String expectedStart = "(format LCTRS :smtlib 2.6)\n(theory Ints)\n(fun f (-> Int Int))";
            assertTrue(result.contains(expectedStart));
            assertTrue(result.contains("\n(rule (f x y) (+ x y))"));
        }

        @Test
        void testWriteToFile() throws IOException {
            Path tempInputFile = Files.createTempFile("testInput", ".java");
            File inputFile = tempInputFile.toFile();
            String testContent = "(format LCTRS :smtlib 2.6)\n(theory Ints)";
            LCTRSTextGenerator generator = new LCTRSTextGenerator();
            generator.writeToFile(testContent, inputFile);
            String expectedFileName = tempInputFile.getParent().resolve(tempInputFile.getFileName().toString().replace(".java", ".ari")).toString();
            File outputFile = new File(expectedFileName);
            assertTrue(outputFile.exists(), "Output file should be created with .ari extension.");
            String fileContent = Files.readString(outputFile.toPath());
            assertEquals(testContent, fileContent, "File content should match the generated text.");
            Files.deleteIfExists(outputFile.toPath());
            Files.deleteIfExists(tempInputFile);
        }

        private ASTNode createTree () {
            ASTNode root = new DefaultASTNode("Program");
            ASTNode theoryNode = new DefaultASTNode("Theory", "Ints");
            ASTNode functionDeclarations = new DefaultASTNode("Functions");
            ASTNode ruleDeclarations = new DefaultASTNode("Rules");
            ASTNode function = new DefaultASTNode("Name", "f");
            function.addChild(new DefaultASTNode("ParameterType", "Int"));
            function.addChild(new DefaultASTNode("ParameterType", "Int"));
            functionDeclarations.addChild(function);
            ASTNode name = new DefaultASTNode("Name", "f");
            name.addChild(new DefaultASTNode("Parameter", "x"));
            name.addChild(new DefaultASTNode("Parameter", "y"));
            ASTNode rules = new DefaultASTNode("Rules");
            ASTNode rule = new DefaultASTNode("Rule");
            rule.addChild(new DefaultASTNode("Operator", "+"));
            rule.addChild(new DefaultASTNode("Variable", "x"));
            rule.addChild(new DefaultASTNode("Variable", "y"));
            name.addChild(rules);
            rules.addChild(rule);
            ruleDeclarations.addChild(name);
            root.addChild(theoryNode);
            root.addChild(functionDeclarations);
            root.addChild(ruleDeclarations);
            return root;
        }
    }