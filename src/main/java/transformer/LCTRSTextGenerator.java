package transformer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static com.github.javaparser.utils.Utils.removeFileExtension;

/**
 * Translates LCTRS AST into LCTRS grammar
 */
public class LCTRSTextGenerator implements TextGenerator {

    /**
     * Default constructor
     */
    public LCTRSTextGenerator () {}

    /**
     * Generates text with LCTRS grammar from AST
     * @param root root node of AST
     * @return String representation of LCTRS
     */
    public String generateText (ASTNode root) {
        StringBuilder builder = new StringBuilder();
        builder.append("(format LCTRS :smtlib 2.6)");
        builder.append(getTheory(root));
        builder.append(getFunctionDeclarations(root));
        builder.append(getRuleDeclarations(root));
        return builder.toString();
    }

    private String getTheory (ASTNode root) {
        StringBuilder builder = new StringBuilder();
        String theory = root.getChildren().getFirst().getValue();
        builder.append("\n(theory ").append(theory).append(")");
        return builder.toString();
    }

    private String getFunctionDeclarations (ASTNode root) {
        StringBuilder builder = new StringBuilder();
        ASTNode functionDecs = root.getChildren().get(1);
        for (ASTNode function : functionDecs.getChildren()) {
            builder.append("\n(fun ").append(function.getValue());
            if (!function.getChildren().getFirst().getType().equals("ParameterType")) {
                builder.append(" ").append(function.getChildren().getFirst().getValue()).append(")");
            } else {
                builder.append(" (->");
                for (ASTNode type : function.getChildren()) {
                    builder.append(" ").append(type.getValue());
                }
                builder.append("))");
            }
        }
        return builder.toString();
    }

    private  String getRuleDeclarations (ASTNode root) {
        StringBuilder builder = new StringBuilder();
        ASTNode ruleDecs = root.getChildren().get(2);
        for (ASTNode function : ruleDecs.getChildren()) {
            builder.append(getRules(function));
        }
        return builder.toString();
    }

    private String getRules(ASTNode function) {
        StringBuilder builder = new StringBuilder();
        ASTNode rules = function.getChildren().getLast();
        for (ASTNode rule : rules.getChildren()) {
            builder.append("\n(rule (").append(function.getValue());
            appendParameters(builder, function);
            builder.append(") (");
            appendRuleContents(builder, rule);
            builder.append(")");
        }
        return builder.toString();
    }

    private void appendParameters(StringBuilder builder, ASTNode function) {
        for (ASTNode param : function.getChildren()) {
            if (param.getType().equals("Parameter")) {
                builder.append(" ").append(param.getValue());
            }
        }
    }

    private void appendRuleContents(StringBuilder builder, ASTNode rule) {
        boolean hasGuard = false;
        for (ASTNode value : rule.getChildren()) {
            if (value.getType().equals("Guard")) {
                hasGuard = true;
                builder.append(") :").append(value.getValue()).append(" (");
                appendGuardContents(builder, value);
                builder.append(")");
            } else {
                if (value != rule.getChildren().getFirst()) {
                    builder.append(" ");
                }
                builder.append(value.getValue());
            }
        }
        if (!hasGuard) {
            builder.append(")");
        }
        for (ASTNode value : rule.getChildren()) {
            if (value.getType().equals("VariableDec")) {
                builder.append(" :var ((").append(value.getChildren().get(0).getValue())
                        .append(" ").append(value.getChildren().get(1).getValue()).append("))");
            }
        }
    }

    private void appendGuardContents(StringBuilder builder, ASTNode guard) {
        for (ASTNode child : guard.getChildren()) {
            if (child != guard.getChildren().getFirst()) {
                builder.append(" ");
            }
            builder.append(child.getValue());
        }
    }

    /**
     * Creates and writes output file in same directory as input file. Name is same as input file with .ari extension
     * @param text String representation of formatted LCTRS
     * @param inputFile initial input file
     */
    @Override
    public void writeToFile(String text, File inputFile) {
        String directory = inputFile.getParent();
        String fileName = removeFileExtension(inputFile.getName()) + ".ari";
        File outputFile = new File(directory, fileName);
        try (FileWriter writer = new FileWriter(outputFile)) {
            writer.write(text);
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }
}
