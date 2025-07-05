package transformer;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Transforms an abstract syntax tree from a java input to the syntax of a logically constrained term rewriting system (LCTRS)
 */
public class ASTTransformer implements TreeTransformer {

    /**
     * Default constructor with no input
     */
    public ASTTransformer() {}

    /**
     * Takes the root node of an abstract syntax tree from javaparser as input and transforms it into one representing an LCTRS
     * @param cu the compilation unit - the root node of the AST
     * @return the root node of the transformed AST
     */
    @Override
    public ASTNode transformTree(CompilationUnit cu) {
        ASTNode newRoot = new DefaultASTNode("Program");
        newRoot.addChild(defineTheory(cu));
        newRoot.addChild(getFunctionDeclarations(cu));
        newRoot.addChild(getFunctionRules(cu));
        return newRoot;
    }

    //Identifies which SMT theory is required for the LCTRS and creates a node to represent it
    private ASTNode defineTheory (CompilationUnit cu) {
        Map<String, Boolean> presentTypes = new HashMap<>();
        presentTypes.put("Ints", false);
        presentTypes.put("Reals", false);
        presentTypes.put("Chars", false);
        cu.findAll(com.github.javaparser.ast.type.PrimitiveType.class).forEach(type -> {
            switch (type.asString()) {
                case "int", "long", "short", "byte" -> presentTypes.put("Ints", true);
                case "double", "float" -> presentTypes.put("Reals", true);
                case "char" -> presentTypes.put("Chars", true);
            }
        });
        ASTNode theory = new DefaultASTNode("Theory");
        if (presentTypes.get("Chars")) {
            theory.setValue("FixedSizeBitVectors");
        } else if (presentTypes.get("Ints") && !presentTypes.get("Reals")) {
            theory.setValue("Ints");
        } else if (!presentTypes.get("Ints") && presentTypes.get("Reals")) {
            theory.setValue("Reals");
        } else if (presentTypes.get("Ints") && presentTypes.get("Reals")) {
            theory.setValue("Reals_Ints");
        } else {
            theory.setValue("Core");
        }
        return theory;
    }

    private ASTNode getFunctionDeclarations (CompilationUnit cu) {
        List<ASTNode> functions = new ArrayList<>();
        List<MethodDeclaration> methods = cu.findAll(MethodDeclaration.class);
        DeclarationBuilder functionBuilder = new FunctionDeclarationBuilder();
        for (MethodDeclaration method : methods) {
            ASTNode function = functionBuilder.functionDeclaration(method);
            functions.add(function);
        }
        ASTNode functionNode = new DefaultASTNode("Functions");
        functionNode.setChildren(functions);
        return functionNode;
    }

    private ASTNode getFunctionRules (CompilationUnit cu) {
        List<ASTNode> functionRules = new ArrayList<>();
        List<MethodDeclaration> methods = cu.findAll(MethodDeclaration.class);
        RuleBuilder ruleBuilder = new FunctionRuleBuilder();
        for (MethodDeclaration method : methods) {
            ASTNode functionRule = ruleBuilder.buildRule(method);
            functionRules.add(functionRule);
        }
        ASTNode rulesNode = new DefaultASTNode("Rules");
        rulesNode.setChildren(functionRules);
        return rulesNode;
    }
}
