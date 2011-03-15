package ru.nia.ledged.core;

import org.junit.*;
import java.util.*;
import ru.nia.ledged.core.AccountTree.Account;

import static org.junit.Assert.*;

public class AccountsTest {
    AccountTree tree;
    Account A, AA, B, BB;

    @Before
    public void setUp() {
        tree = new AccountTree();

        A = tree.findOrCreateChild(true, "A");
        AA = tree.findOrCreateChild(true, "A", "AA");
        B = tree.findOrCreateChild(true, "B");
        BB = tree.findOrCreateChild(true, "B", "BB");
    }

    private Set<Account> asSet(Account... accounts) {
        return new HashSet<Account>(Arrays.asList(accounts));
    }

    @Test
    public void testGetRoots() {
        assertEquals(asSet(A, B), tree.getRootAccounts());
    }

    @Test
    public void testGetChildren() {
        assertEquals(asSet(AA), A.getChildren());
    }

    @Test
    public void testGetChildrenOfLeaf() {
        assertEquals(asSet(), AA.getChildren());
    }

    @Test
    public void testFindRoot() {
        assertEquals(B, tree.findOrCreateChild(true, "B"));
        assertEquals(B, tree.findOrCreateChild(false, "B"));
    }

    @Test
    public void testFindNotRoot() {
        assertEquals(BB, tree.findOrCreateChild(true, "B", "BB"));
        assertEquals(BB, tree.findOrCreateChild(false, "B", "BB"));
    }

    @Test
    public void testNotFoundRootAcoount() {
        assertNull(tree.findOrCreateChild(false, "C"));
    }

    @Test
    public void testCreateRootAcoount() {
        Account created = tree.findOrCreateChild(true, "C");
        assertTrue(tree.getRootAccounts().contains(created));
    }

    @Test
    public void testNotFoundIndirectChild() {
        assertNull(tree.findOrCreateChild(false, "B", "C"));
    }

    @Test
    public void testCreateIndirectChild() {
        Account created = tree.findOrCreateChild(true, "B", "C");
        assertTrue(B.getChildren().contains(created));
    }

    @Test
    public void testNotFoundDeep() {
        assertNull(tree.findOrCreateChild(false, "D", "DD"));
    }

    @Test
    public void testCreateChildChain() {
        Account created = tree.findOrCreateChild(true, "D", "DD");
        Account hisParent = tree.findOrCreateChild(true, "D");
        assertTrue(tree.getRootAccounts().contains(hisParent));
        assertTrue(hisParent.getChildren().contains(created));
    }

    @Test
    public void testToStringOfRoot() {
        assertEquals("A", A.toString());
    }

    @Test
    public void testToStringOfNotRoot() {
        assertEquals("A:AA", AA.toString());
    }
}
