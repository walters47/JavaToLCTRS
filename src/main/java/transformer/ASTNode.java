package transformer;

import java.util.List;

/**
 * Represents a node in an Abstract Syntax Tree (AST).
 * Each node contains a type, a value which can be null, and may have child nodes.
 */
public interface ASTNode {

    /**
     * Adds a child node to this node's list of children.
     *
     * @param child the child to be added
     */
    void addChild(ASTNode child);

    /**
     * Retrieves the type of this AST node.
     *
     * @return a string representing the type of this node
     */
    String getType();

    /**
     * Sets the type of this AST node.
     *
     * @param type a string representing the type to set for this node
     */
    void setType(String type);

    /**
     * Retrieves the value associated with this AST node.
     *
     * @return a string representing the value of this node
     */
    String getValue();

    /**
     * Sets the value associated with this AST node.
     *
     * @param value a string representing the value to set for this node
     */
    void setValue(String value);

    /**
     * Retrieves the list of child nodes for this AST node.
     *
     * @return a list of transformer.ASTNode objects representing this node's children
     */
    List<ASTNode> getChildren();

    /**
     * Sets the list of child nodes for this AST node.
     *
     * @param children a list of transformer.ASTNode objects to set as this node's children
     */
    void setChildren(List<ASTNode> children);

    void printTree(String indent);
}

