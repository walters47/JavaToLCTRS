package transformer;

import com.github.javaparser.ast.CompilationUnit;

/**
 * Transforms an abstract syntax tree from a java input to the syntax of a logically constrained term rewriting system (LCTRS)
 */
public interface TreeTransformer {

    /**
     *Takes the root node of an abstract syntax tree from javaparser as input and transforms it into one representing an LCTRS
     * @param cu the compilation unit - the root node of the AST
     * @return the root node of the transformed AST
     */
    ASTNode transformTree(CompilationUnit cu);
}
