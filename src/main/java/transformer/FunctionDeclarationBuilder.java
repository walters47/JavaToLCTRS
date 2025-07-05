package transformer;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;

import java.util.ArrayList;
import java.util.List;

/**
 * Builds function declaration branches for LCTRS ASTs
 */
public class FunctionDeclarationBuilder implements DeclarationBuilder{

    /**
     * Default constructor
     */
    public FunctionDeclarationBuilder() {};

    /**
     * Builds function declaration branches of an AST
     * @param method method to be converted to a function declaration
     * @return method name node of branch
     */
    public ASTNode functionDeclaration (MethodDeclaration method) {
        if (isVoidMethod(method)) {
            return null;
        }
        ASTNode function = getFunctionName(method);
        for (ASTNode param : getParameterTypes(method)) {
            function.addChild(param);
        }
        function.addChild(getReturnType(method));
        return function;
    }

    private boolean isVoidMethod(MethodDeclaration method) {
        return "void".equals(method.getTypeAsString());
    }

    private ASTNode getFunctionName (MethodDeclaration method) {
        String methodName = method.getNameAsString();
        return new DefaultASTNode("Name", methodName);
    }

    private List<ASTNode> getParameterTypes (MethodDeclaration method) {
        List<ASTNode> parameterTypeList = new ArrayList<>();
        NodeList<Parameter> parameters = method.getParameters();
        for (Parameter param : parameters) {
            String smtType = mapToSMTType(param.getTypeAsString());
            parameterTypeList.add(new DefaultASTNode("ParameterType", smtType));
        }
        return parameterTypeList;
    }

    private ASTNode getReturnType (MethodDeclaration method) {
        String smtType = mapToSMTType(method.getTypeAsString());
        return new DefaultASTNode("ReturnType", smtType);
    }

    private String mapToSMTType(String javaType) {
        return switch (javaType) {
            case "int", "short", "byte", "long" -> "Int";
            case "double", "float" -> "Real";
            case "boolean" -> "Boolean";
            case "char" -> "(_ BitVec 32)";
            default -> javaType;
        };
    }
}
