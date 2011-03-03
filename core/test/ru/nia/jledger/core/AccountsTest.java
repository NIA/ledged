package ru.nia.jledger.core;

import org.junit.*;
import java.util.*;
import ru.nia.jledger.core.AccountTree.Account;

import static org.junit.Assert.*;

public class AccountsTest {
    AccountTree tree;
    Account A, AA, B, BB;

    @Before
    public void setUp() {
        tree = new AccountTree();

        A = tree.findOrCreateChild("A");
        AA = tree.findOrCreateChild("A", "AA");
        B = tree.findOrCreateChild("B");
        BB = tree.findOrCreateChild("B", "BB");
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
        assertEquals(B, tree.findOrCreateChild("B"));
    }

    @Test
    public void testFindNotRoot() {
        assertEquals(BB, tree.findOrCreateChild("B", "BB"));
    }

    @Test
    public void testCreateRootAcoount() {
        Account created = tree.findOrCreateChild("C");
        assertTrue(tree.getRootAccounts().contains(created));
    }

    @Test
    public void testCreateIndirectChild() {
        Account created = tree.findOrCreateChild("B", "C");
        assertTrue(B.getChildren().contains(created));
    }

    @Test
    public void testCreateChildChain() {
        Account created = tree.findOrCreateChild("D", "DD");
        Account hisParent = tree.findOrCreateChild("D");
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
