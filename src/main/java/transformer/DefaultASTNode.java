package transformer;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing a simple node for an abstract syntax tree
 */
public class DefaultASTNode implements ASTNode{
    private String type;
    private String value;
    private List<ASTNode> children;

    /**
     * Initialise node with type and value
     * @param type Type of the node
     * @param value Value of the node (can be null)
     */
    public DefaultASTNode (String type, String value) {
        this.type = type;
        this.value = value;
        this.children = new ArrayList<>();
    }

    /**
     * Initialise with just a type
     * @param type Type of the node
     */
    public DefaultASTNode (String type) {
        this(type, null);
    }

    /**
     * Adds a child to the node
     * @param child Child to add
     */
    public void addChild (ASTNode child) {
        children.add(child);
    }

    /**
     * Returns type of the node
     * @return Type of the node
     */
    public String getType () {
        return type;
    }

    /**
     * Sets the type of the node
     * @param type Type of the node
     */
    public void setType (String type) {
        this.type = type;
    }

    /**
     * Returns the value of the node
     * @return The value of the node
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the node
     * @param value The value of the node
     */
    public void setValue (String value) {
        this.value = value;
    }

    /**
     * Return a list of child nodes of the node
     * @return The children of the node
     */
    public List<ASTNode> getChildren () {
        return children;
    }

    /**
     * Sets a list of child nodes
     * @param children The list of children of the node
     */
    public void setChildren (List<ASTNode> children) {
        this.children = children;
    }

    /**
     * Return a formatted string representation of the node
     * @return A string representation of the node
     */
    @Override
    public String toString () {
        StringBuilder sb = new StringBuilder();
        sb.append("transformer.ASTNode{")
                .append("type='").append(type).append('\'')
                .append(", value='").append(value).append('\'')
                .append(", children=").append(children.size())
                .append('}');
        return sb.toString();
    }

    /**
     * Prints formatted tree to terminal
     * @param indent desired indent (can be empty string)
     */
    public void printTree (String indent) {
        System.out.println(indent + type + (value != null ? " (" + value + ")" : ""));
        for (ASTNode child : children) {
            child.printTree(indent + " ");
        }
    }
}
