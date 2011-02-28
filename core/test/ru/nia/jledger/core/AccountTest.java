package ru.nia.jledger.core;

import org.junit.*;
import static org.junit.Assert.*;

import java.util.*;

public class AccountTest {
    Account root;
    Account A, AA, B, BB;

    @Before
    public void setUp() {
        root = new Account("root", null);
        A = root.addChild("A");
        AA = A.addChild("AA");
        B = root.addChild("B");
        BB = B.addChild("BB");
    }

    @Test
    public void testGetParent() {
        assertSame(root, A.getParent());
    }

    private Set<Object> asSet(Object... objs) {
        return new HashSet<Object>(Arrays.asList(objs));
    }

    @Test
    public void testGetChildren() {
        assertEquals(root.getChildren(), asSet(A, B));
    }

    @Test
    public void testGetChildrenOfLeaf() {
        assertEquals(AA.getChildren(), asSet());
    }

    @Test
    public void testFindItself() {
        Account found = root.findOrCreateChild();
        assertSame(root, found);
    }

    @Test
    public void testFindDirectChild() {
        Account found = root.findOrCreateChild("B");
        assertSame(B, found);
    }

    @Test
    public void testFindIndirectChild() {
        Account found = root.findOrCreateChild("B", "BB");
        assertSame(BB, found);
    }

    @Test
    public void testCreateDirectChild() {
        Account created = root.findOrCreateChild("C");
        assertEquals("C", created.getName());
        assertSame(root, created.getParent());
        assertTrue(root.getChildren().contains(created));
    }

    @Test
    public void testCreateIndirectChild() {
        Account created = root.findOrCreateChild("B", "C");
        assertEquals("C", created.getName());
        assertSame(B, created.getParent());
        assertTrue(B.getChildren().contains(created));
    }

    @Test
    public void testCreateChildChain() {
        Account created = root.findOrCreateChild("D", "DD");
        Account parent = created.getParent();
        assertEquals("DD", created.getName());
        assertEquals("D", parent.getName());
        assertSame(root, parent.getParent());
        assertTrue(root.getChildren().contains(parent));
        assertTrue(parent.getChildren().contains(created));
    }
}
