import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import org.junit.jupiter.api.Test;
import transformer.ASTNode;
import transformer.ASTTransformer;
import transformer.TreeTransformer;

import static org.junit.jupiter.api.Assertions.*;

public class ASTTransformerTest {
    String testClass = """
            public class IntAddFiveTest {
                        
                public static int addFive(int x) {
                    return x + 5;
                }
                        
            }""";

    CompilationUnit testCU = StaticJavaParser.parse(testClass);
    TreeTransformer transformer = new ASTTransformer();
    ASTNode resultRoot = transformer.transformTree(testCU);


    @Test
    public void testTransformTreeForRoot () {
        String rootType = resultRoot.getType();
        assertEquals(rootType, "Program");
        assertNull(resultRoot.getValue());
    }

    @Test
    public void testTransformTreeForTheory () {
        ASTNode theoryNode = resultRoot.getChildren().getFirst();
        String nodeType = theoryNode.getType();
        String theory = theoryNode.getValue();
        assertEquals(nodeType, "Theory");
        assertEquals(theory, "Ints");
    }

    @Test
    public void testTransformTreeForFunctionsNode () {
        ASTNode functionNode = resultRoot.getChildren().get(1);
        String nodeType = functionNode.getType();
        assertEquals(nodeType, "Functions");
        assertNull(functionNode.getValue());
    }

    @Test
    public void testTransformTreeForFunctionNameAndParams () {
        ASTNode functionNode = resultRoot.getChildren().get(1);
        ASTNode functionDecl = functionNode.getChildren().getFirst();
        ASTNode param = functionDecl.getChildren().getFirst();
        ASTNode returnType = functionDecl.getChildren().get(1);
        assertEquals(functionDecl.getType(), "Name");
        assertEquals(functionDecl.getValue(), "addFive");
        assertEquals(param.getType(), "ParameterType");
        assertEquals(param.getValue(), "Int");
        assertEquals(returnType.getType(), "ReturnType");
        assertEquals(returnType.getValue(), "Int");
    }
}
