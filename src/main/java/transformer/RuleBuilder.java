package transformer;

import com.github.javaparser.ast.body.MethodDeclaration;

/**
 * Builds rule statements for LCTRS ASTs
 */
public interface RuleBuilder {

    /**
     * Builds rule statement branches of LCTRS AST
     * @param method method for body to be parsed into rule statements
     * @return node of function name with statements as children
     */
    ASTNode buildRule (MethodDeclaration method);
}
