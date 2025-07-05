package transformer;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;

import java.util.ArrayList;
import java.util.List;

/**
 * Builds rule statements for LCTRS ASTs
 */
public class FunctionRuleBuilder implements RuleBuilder {

    /**
     * Default constructor
     */
    public FunctionRuleBuilder () {}

    /**
     * Builds rule statement branches of LCTRS AST
     * @param method method for body to be parsed into rule statements
     * @return node of function name with statements as children
     */
    public ASTNode buildRule (MethodDeclaration method) {
        ASTNode function = new DefaultASTNode("Name", method.getNameAsString());
        for (ASTNode param : getParameterNames(method)) {
            function.addChild(param);
        }
        ASTNode rules = new DefaultASTNode("Rules");
        function.addChild(rules);
        rules.setChildren(parseMethodExpressions(method));
        return function;
    }

    private List<ASTNode> getParameterNames (MethodDeclaration method) {
        List<ASTNode> parameterNameList = new ArrayList<>();
        NodeList<Parameter> parameters = method.getParameters();
        for (Parameter param : parameters) {
            String name = param.getNameAsString();
            parameterNameList.add(new DefaultASTNode("Parameter", name));
        }
        return parameterNameList;
    }

    private List<ASTNode> parseMethodExpressions(MethodDeclaration method) {
        List<ASTNode> expressionNodes = new ArrayList<>();
        if (method.getBody().isPresent()) {
            BlockStmt body = method.getBody().get();
            for (Statement stmt : body.getStatements()) {
                ASTNode ruleNode = new DefaultASTNode("Rule");
                if (stmt.isExpressionStmt()) {
                    Expression expr = stmt.asExpressionStmt().getExpression();
                    if (expr.isVariableDeclarationExpr()) {
                        handleVariableDeclaration(ruleNode, expr.asVariableDeclarationExpr());
                    } else {
                        handleExpressionStatement(ruleNode, expr);
                    };
                    expressionNodes.add(ruleNode);
                } else if (stmt.isReturnStmt() && stmt.asReturnStmt().getExpression().isPresent()) {
                    handleExpressionStatement(ruleNode, stmt.asReturnStmt().getExpression().get());
                    expressionNodes.add(ruleNode);
                } else if (stmt.isIfStmt()) {
                    expressionNodes.addAll(handleIfStatement(stmt.asIfStmt()));
                }
            }
        }
        return expressionNodes;
    }

    private List<ASTNode> parseExpression (Expression expression) {
        List<ASTNode> nodes = new ArrayList<>();
        if (expression.isBinaryExpr()) {
            BinaryExpr binaryExpr = expression.asBinaryExpr();
            nodes.add(new DefaultASTNode("Operator", binaryExpr.getOperator().asString()));
            nodes.addAll(parseExpression(binaryExpr.getLeft()));
            nodes.addAll(parseExpression(binaryExpr.getRight()));
        } else if (expression.isNameExpr()) {
            nodes.add(new DefaultASTNode("Variable", expression.asNameExpr().getNameAsString()));
        } else if (expression.isLiteralExpr()) {
            nodes.add(new DefaultASTNode("Value", expression.toString()));
        }
        return nodes;
    }

    private void handleMethodCall(ASTNode ruleNode, MethodCallExpr callExpr, Expression guardExpr) {
        ruleNode.addChild(new DefaultASTNode("Function", callExpr.getNameAsString()));
        for (Expression arg : callExpr.getArguments()) {
            ruleNode.addChild(new DefaultASTNode("Parameter", arg.toString()));
        }
        if (guardExpr != null) {
            ASTNode guardNode = new DefaultASTNode("Guard", ":guard");
            guardNode.setChildren(parseConditional(guardExpr));
            ruleNode.addChild(guardNode);
        }
    }

    private void addExpressionAndGuard(ASTNode ruleNode, Expression expr, String guardType) {
        List<ASTNode> parsedNodes = new ArrayList<>(parseExpression(expr));
        if (guardType != null) {
            ASTNode guardNode = new DefaultASTNode("Guard", ":guard");
            guardNode.setChildren(parseConditional(expr));
            parsedNodes.add(guardNode);
        }
        ruleNode.setChildren(parsedNodes);
    }

    private void handleExpressionStatement(ASTNode ruleNode, Expression expr) {
        if (expr.isMethodCallExpr()) {
            handleMethodCall(ruleNode, expr.asMethodCallExpr(), null);
        } else {
            addExpressionAndGuard(ruleNode, expr, null);
        }
    }

    private void handleVariableDeclaration(ASTNode ruleNode, VariableDeclarationExpr varExpr) {
        for (VariableDeclarator var : varExpr.getVariables()) {
            ASTNode varNode = new DefaultASTNode("VariableDec   ", "var");
            ASTNode varNameNode = new DefaultASTNode("VariableName", var.getNameAsString());
            ASTNode typeNode = new DefaultASTNode("Type", getTypeString(var.getType().asString()));
            varNode.addChild(varNameNode);
            varNode.addChild(typeNode);
            ruleNode.addChild(varNode);
        }
    }

    private String getTypeString(String type) {
        return switch (type) {
            case "boolean" -> "Boolean";
            case "int", "byte", "long", "short" -> "Int";
            case "double", "float" -> "Real";
            default -> "(_ BitVec 32)";
        };
    }

    private List<ASTNode> handleIfStatement(IfStmt ifStmt) {
        List<ASTNode> nodes = new ArrayList<>();
        ASTNode ifRuleNode = new DefaultASTNode("Rule");
        handleThenStatement(ifRuleNode, ifStmt);
        ASTNode guardNode = new DefaultASTNode("Guard", "guard");
        guardNode.setChildren(parseConditional(ifStmt.getCondition()));
        ifRuleNode.addChild(guardNode);
        nodes.add(ifRuleNode);
        if (ifStmt.getElseStmt().isPresent()) {
            Statement elseStmt = ifStmt.getElseStmt().get();
            if (elseStmt.isIfStmt()) {
                nodes.addAll(handleElseIfStatement(elseStmt.asIfStmt())); // Add "else if" rules
            } else {
                nodes.add(handleElseStatement(elseStmt)); // Add "else" rule
            }
        }
        return nodes;
    }

    private void handleThenStatement(ASTNode parentNode, IfStmt ifStmt) {
        Statement thenStmt = ifStmt.getThenStmt();
        if (thenStmt instanceof BlockStmt thenBlock) {
            for (Statement stmt : thenBlock.getStatements()) {
                handleStatement(parentNode, stmt);
            }
        } else {
            handleStatement(parentNode, thenStmt);
        }
    }

    private List<ASTNode> handleElseIfStatement(IfStmt elseIfStmt) {
        List<ASTNode> nodes = new ArrayList<>();
        ASTNode elseIfRuleNode = new DefaultASTNode("Rule");
        ASTNode guardNode = new DefaultASTNode("Guard", "guard");
        guardNode.setChildren(parseConditional(elseIfStmt.getCondition()));
        handleThenStatement(elseIfRuleNode, elseIfStmt);
        elseIfRuleNode.addChild(guardNode);
        nodes.add(elseIfRuleNode);
        if (elseIfStmt.getElseStmt().isPresent()) {
            Statement nextElseStmt = elseIfStmt.getElseStmt().get();
            if (nextElseStmt.isIfStmt()) {
                nodes.addAll(handleElseIfStatement(nextElseStmt.asIfStmt()));
            } else {
                nodes.add(handleElseStatement(nextElseStmt));
            }
        }
        return nodes;
    }

    private ASTNode handleElseStatement(Statement elseStmt) {
        ASTNode elseRuleNode = new DefaultASTNode("Rule");
        if (elseStmt instanceof BlockStmt elseBlock) {
            for (Statement stmtInElse : elseBlock.getStatements()) {
                handleStatement(elseRuleNode, stmtInElse);
            }
        } else {
            handleStatement(elseRuleNode, elseStmt);
        }
        return elseRuleNode;
    }

    private void handleStatement(ASTNode parentNode, Statement stmt) {
        if (stmt.isExpressionStmt()) {
            handleExpressionStatement(parentNode, stmt.asExpressionStmt().getExpression());
        } else if (stmt.isReturnStmt()) {
            handleExpressionStatement(parentNode, stmt.asReturnStmt().getExpression().get());
        }
    }

    private List<ASTNode> parseConditional (Expression expression) {
        List<ASTNode> nodes = new ArrayList<>();
        if (expression.isBinaryExpr()) {
            BinaryExpr binaryExpr = expression.asBinaryExpr();
            ASTNode node = getOperatorNode(binaryExpr);
            nodes.add(node);
            nodes.addAll(parseConditional(binaryExpr.getLeft()));
            nodes.addAll(parseConditional(binaryExpr.getRight()));
        } else if (expression.isUnaryExpr()) {
            UnaryExpr unaryExpr = expression.asUnaryExpr();
            String operator = unaryExpr.getOperator().asString();
            if (operator.equals("!")) {
                ASTNode node = new DefaultASTNode("Operator", "not");
                nodes.add(node);
                nodes.addAll(parseConditional(unaryExpr.getExpression()));
            }
        } else if (expression.isNameExpr()) {
            NameExpr nameExpr = expression.asNameExpr();
            nodes.add(new DefaultASTNode("Variable", nameExpr.getNameAsString()));
        } else if (expression.isLiteralExpr()) {
            nodes.add(new DefaultASTNode("Value", expression.toString()));
        }
        return nodes;
    }

    private static ASTNode getOperatorNode(BinaryExpr binaryExpr) {
        ASTNode node = new DefaultASTNode("Operator");
        switch (binaryExpr.getOperator()) {
            case AND -> node.setValue("and");
            case OR -> node.setValue("or");
            case GREATER_EQUALS -> node.setValue(">=");
            case LESS_EQUALS -> node.setValue("<=");
            case EQUALS -> node.setValue("==");
            case GREATER -> node.setValue(">");
            case LESS -> node.setValue("<");
            default -> node.setValue(binaryExpr.getOperator().asString());
        }
        return node;
    }
}
