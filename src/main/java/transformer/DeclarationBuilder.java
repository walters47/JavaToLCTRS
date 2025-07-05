package transformer;

import com.github.javaparser.ast.body.MethodDeclaration;

/**
 * Builds function declarations giving name, parameter type and return type
 */
public interface DeclarationBuilder {

    /**
     * Builds function declaration branches of an AST
     * @param method method to be converted to a function declaration
     * @return method name node of branch
     */
    ASTNode functionDeclaration(MethodDeclaration method);
}
