package ru.nia.ledged.core;

import org.junit.*;
import java.util.*;
import ru.nia.ledged.core.AccountTree.Account;

import static org.junit.Assert.*;

public class AccountsTest {
    AccountTree tree, filterTree;
    Account A, AA, B, BB;

    @Before
    public void setUp() {
        tree = new AccountTree();

        A = tree.findOrCreateChild("A");
        AA = tree.findOrCreateChild("A", "AA");
        B = tree.findOrCreateChild("B");
        BB = tree.findOrCreateChild("B", "BB");

        filterTree = new AccountTree();

        filterTree.findOrCreateChild("expenses", "smth");
        filterTree.findOrCreateChild("expenses", "food");
        filterTree.findOrCreateChild("extra", "smth");
        filterTree.findOrCreateChild("people", "smith");
        filterTree.findOrCreateChild("assets", "cash");
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
        assertEquals(B, tree.findChild("B"));
    }

    @Test
    public void testFindNotRoot() {
        assertEquals(BB, tree.findOrCreateChild("B", "BB"));
        assertEquals(BB, tree.findChild("B", "BB"));
    }

    @Test
    public void testNotFoundRootAcoount() {
        assertNull(tree.findChild("C"));
    }

    @Test
    public void testCreateRootAcoount() {
        Account created = tree.findOrCreateChild("C");
        assertTrue(tree.getRootAccounts().contains(created));
    }

    @Test
    public void testNotFoundIndirectChild() {
        assertNull(tree.findChild("B", "C"));
    }

    @Test
    public void testCreateIndirectChild() {
        Account created = tree.findOrCreateChild("B", "C");
        assertTrue(B.getChildren().contains(created));
    }

    @Test
    public void testNotFoundDeep() {
        assertNull(tree.findChild("D", "DD"));
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

    @Test
    public void testFilterRoots() throws Exception {
        assertAccountNamesEqual(filterTree.filterAccounts("ex"), "expenses", "extra");
    }

    @Test
    public void testFilterChildren() throws Exception {
        assertAccountNamesEqual(filterTree.filterAccounts("expenses:f"), "expenses:food");
    }

    @Test
    public void testFilterAllChildren() throws Exception {
        assertAccountNamesEqual(filterTree.filterAccounts("expenses:"), "expenses:food", "expenses:smth");
    }

    @Test
    public void testFilterNotFoundRoot() throws Exception {
        assertAccountNamesEmpty(filterTree.filterAccounts("notfound"));
    }

    @Test
    public void testFilterNotFoundChild() throws Exception {
        assertAccountNamesEmpty(filterTree.filterAccounts("expenses:notfound"));
    }

    @Test
    public void testFilterNotFoundChildChain() throws Exception {
        assertAccountNamesEmpty(filterTree.filterAccounts("notfound:notfound"));
    }

    @Test
    public void testEmptyFilter() throws Exception {
        assertAccountNamesEqual(filterTree.filterAccounts(""), "expenses", "extra", "people", "assets");
    }

    private static void assertAccountNamesEmpty(List<Account> accounts) {
        assertAccountNamesEqual(accounts);
    }

    private static void assertAccountNamesEqual(List<Account> accounts, String... expectedNames) {
        Set<String> actual = new HashSet<String>();
        for (Account a : accounts) {
            actual.add(a.toString());
        }
        Set<String> expected = new HashSet<String>(Arrays.asList(expectedNames));
        assertEquals(expected, actual);
    }
}
