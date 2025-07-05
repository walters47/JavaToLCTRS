import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import org.junit.jupiter.api.Test;
import transformer.ASTNode;
import transformer.DeclarationBuilder;
import transformer.FunctionDeclarationBuilder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FunctionDeclarationBuilderTest {

    String testClass = """
            public class IntAddFiveTest {
                        
                public static int addFive(int x) {
                    return x + 5;
                }
                
                public int noParamMethod () {
                    return 5;
                }
                                        
            }""";

    CompilationUnit testCU = StaticJavaParser.parse(testClass);
    List<MethodDeclaration> mdList = testCU.findAll(MethodDeclaration.class);
    DeclarationBuilder builder = new FunctionDeclarationBuilder();

    @Test
    public void testGetMethodName () {
        MethodDeclaration md = mdList.getFirst();
        ASTNode testFunction = builder.functionDeclaration(md);
        assertEquals(testFunction.getType(), "Name");
        assertEquals(testFunction.getValue(), "addFive");
    }

    @Test
    public void testGetMethodParameters () {
        MethodDeclaration md = mdList.getFirst();
        ASTNode testFunction = builder.functionDeclaration(md);
        List<ASTNode> paramsList = testFunction.getChildren();
        ASTNode param = paramsList.getFirst();
        assertEquals(param.getType(), "ParameterType");
        assertEquals(param.getValue(), "Int");
    }

    @Test
    public void testGetMethodReturnType () {
        MethodDeclaration md = mdList.getFirst();
        ASTNode testFunction = builder.functionDeclaration(md);
        List<ASTNode> childList = testFunction.getChildren();
        ASTNode returnType = childList.getLast();
        assertEquals(returnType.getType(), "ReturnType");
        assertEquals(returnType.getValue(), "Int");
    }

    @Test
    public void testGetMethodParametersEmpty () {
        MethodDeclaration md = mdList.getLast();
        ASTNode testFunction = builder.functionDeclaration(md);
        List<ASTNode> paramsList = testFunction.getChildren();
        ASTNode param = paramsList.getFirst();
        assertNotEquals(param.getType(), "Parameter");
    }

}
