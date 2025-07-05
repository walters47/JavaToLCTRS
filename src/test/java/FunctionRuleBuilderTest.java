import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import org.junit.jupiter.api.Test;
import transformer.ASTNode;
import transformer.FunctionRuleBuilder;

import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

public class FunctionRuleBuilderTest {

    private final FunctionRuleBuilder ruleBuilder = new FunctionRuleBuilder();
    private final JavaParser parser = new JavaParser();

    @Test
    public void testBuildRule_SimpleMethod() {
        String methodCode = """
                public int add(int a, int b) {
                    return a + b;
                }
                """;
        MethodDeclaration method = parseMethod(methodCode);
        ASTNode functionNode = ruleBuilder.buildRule(method);
        assertEquals("Name", functionNode.getType());
        assertEquals("add", functionNode.getValue());
        assertEquals(3, functionNode.getChildren().size());
    }

    @Test
    public void testBuildRule_WithConditional() {
        String methodCode = """
                public int max(int x, int y) {
                    if (x > y) {
                        return x;
                    } else {
                        return y;
                    }
                }
                """;
        MethodDeclaration method = parseMethod(methodCode);
        ASTNode functionNode = ruleBuilder.buildRule(method);
        ASTNode rulesNode = functionNode.getChildren().getLast();
        assertEquals("Rules", rulesNode.getType());
        assertFalse(rulesNode.getChildren().isEmpty());
        ASTNode ifRule = rulesNode.getChildren().get(0);
        assertEquals("Rule", ifRule.getType());
        ASTNode guard = ifRule.getChildren().getLast();
        assertEquals("Guard", guard.getType());
    }

    @Test
    public void testGetParameterNames() {
        String methodCode = """
                public void example(int a, double b, boolean c) {}
                """;

        MethodDeclaration method = parseMethod(methodCode);
        List<ASTNode> params = ruleBuilder.buildRule(method).getChildren();

        assertEquals(4, params.size());
        assertEquals("Parameter", params.get(0).getType());
        assertEquals("a", params.get(0).getValue());
        assertEquals("b", params.get(1).getValue());
        assertEquals("c", params.get(2).getValue());
    }

    @Test
    public void testHandleIfStatement() {
        String methodCode = """
                public boolean isPositive(int num) {
                    if (num > 0) {
                        return true;
                    } else {
                        return false;
                    }
                }
                """;

        MethodDeclaration method = parseMethod(methodCode);
        List<ASTNode> expressions = ruleBuilder.buildRule(method).getChildren().getLast().getChildren();

        assertEquals(2, expressions.size()); // One for if, one for else

        ASTNode ifRule = expressions.get(0);
        assertEquals("Rule", ifRule.getType());

        ASTNode guardNode = ifRule.getChildren().getLast();
        assertEquals("Guard", guardNode.getType());
    }

    private MethodDeclaration parseMethod(String methodCode) {
        CompilationUnit cu = parser.parse("class Test { " + methodCode + " }").getResult().orElseThrow();
        return cu.findFirst(MethodDeclaration.class).orElseThrow();
    }
}