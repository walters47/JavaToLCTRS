
import org.junit.jupiter.api.Test;
import transformer.ASTNode;
import transformer.DefaultASTNode;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ASTNodeTest {

    @Test
    public void testConstructorDefault () {
        ASTNode node = new DefaultASTNode("Type", "Value");
        assertEquals("Type", node.getType());
        assertEquals("Value", node.getValue());
        assertTrue(node.getChildren().isEmpty());
    }

    @Test
    public void testConstructorWithOnlyType() {
        ASTNode node = new DefaultASTNode("Type");
        assertEquals("Type", node.getType());
        assertNull(node.getValue());
        assertTrue(node.getChildren().isEmpty());
    }

    @Test
    public void testGetTypeAndSetType() {
        ASTNode node = new DefaultASTNode("InitialType");
        assertEquals("InitialType", node.getType());
        node.setType("NewType");
        assertEquals("NewType", node.getType());
    }

    @Test
    public void testGetValueAndSetValue() {
        ASTNode node = new DefaultASTNode("TypeC", "InitialValue");
        assertEquals("InitialValue", node.getValue());
        node.setValue("NewValue");
        assertEquals("NewValue", node.getValue());
    }

    @Test
    public void testGetChildrenAndSetChildren() {
        ASTNode node = new DefaultASTNode("Type");
        assertTrue(node.getChildren().isEmpty());

        List<ASTNode> newChildren = new ArrayList<>();
        newChildren.add(new DefaultASTNode("ChildType", "ChildValue"));
        node.setChildren(newChildren);

        assertEquals(1, node.getChildren().size());
        assertEquals("ChildType", node.getChildren().getFirst().getType());
    }

    @Test
    public void testAddChild() {
        ASTNode node = new DefaultASTNode("Parent");
        ASTNode child = new DefaultASTNode("ChildType", "ChildValue");

        node.addChild(child);

        assertEquals(1, node.getChildren().size());
        assertEquals("ChildType", node.getChildren().getFirst().getType());
        assertEquals("ChildValue", node.getChildren().getFirst().getValue());
    }

    @Test
    public void testAddNullChild() {
        ASTNode node = new DefaultASTNode("Parent");
        node.addChild(null);
        assertEquals(1, node.getChildren().size());
        assertNull(node.getChildren().getFirst());
    }

    @Test
    public void testEmptyTypeAndValue() {
        ASTNode node = new DefaultASTNode("", "");
        assertEquals("", node.getType());
        assertEquals("", node.getValue());
    }

    @Test
    public void testLargeNumberOfChildren() {
        ASTNode node = new DefaultASTNode("Root");
        for (int i = 0; i < 1000; i++) {
            node.addChild(new DefaultASTNode("ChildType" + i, "ChildValue" + i));
        }
        assertEquals(1000, node.getChildren().size());
        assertEquals("ChildType999", node.getChildren().get(999).getType());
    }
}
