package ru.nia.ledged.core;

import org.junit.*;
import java.util.*;
import ru.nia.ledged.core.AccountTree.Account;

import static org.junit.Assert.*;

public class AccountsTest {
    AccountTree tree, filterTree;
    Account A, AA, B, BB;
    String[] filterTreeLeaves;

    @Before
    public void setUp() {
        tree = new AccountTree();

        A = tree.findOrCreateAccount("A");
        AA = tree.findOrCreateAccount("A:AA");
        B = tree.findOrCreateAccount("B");
        BB = tree.findOrCreateAccount("B:BB");

        filterTreeLeaves = new String[] {
                "expenses:smth",
                "expenses:food",
                "extra:smth",
                "people:smith",
                "assets:cash",
        };
        filterTree = new AccountTree();
        for (String name : filterTreeLeaves) {
            filterTree.findOrCreateAccount(name);
        }
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
        assertEquals(B, tree.findOrCreateAccount("B"));
        assertEquals(B, tree.findAccount("B"));
    }

    @Test
    public void testFindNotRoot() {
        assertEquals(BB, tree.findOrCreateAccount("B:BB"));
        assertEquals(BB, tree.findAccount("B:BB"));
    }

    @Test
    public void testNotFoundRootAcoount() {
        assertNull(tree.findAccount("C"));
    }

    @Test
    public void testCreateRootAcoount() {
        Account created = tree.findOrCreateAccount("C");
        assertTrue(tree.getRootAccounts().contains(created));
    }

    @Test
    public void testNotFoundIndirectChild() {
        assertNull(tree.findAccount("B:C"));
    }

    @Test
    public void testCreateIndirectChild() {
        Account created = tree.findOrCreateAccount("B:C");
        assertTrue(B.getChildren().contains(created));
    }

    @Test
    public void testNotFoundDeep() {
        assertNull(tree.findAccount("D:DD"));
    }

    @Test
    public void testCreateChildChain() {
        Account created = tree.findOrCreateAccount("D:DD");
        Account hisParent = tree.findOrCreateAccount("D");
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
        assertNamesEqual(filterTree.filterAccountNames("ex"), "expenses", "extra");

    }

    @Test
    public void testFilterChildren() throws Exception {
        assertNamesEqual(filterTree.filterAccountNames("expenses:f"), "expenses:food");
    }

    @Test
    public void testFilterAllChildren() throws Exception {
        assertNamesEqual(filterTree.filterAccountNames("expenses:"), "expenses:food", "expenses:smth");
    }

    @Test
    public void testFilterNotFoundRoot() throws Exception {
        assertNamesEmpty(filterTree.filterAccountNames("notfound"));
    }

    @Test
    public void testFilterNotFoundChild() throws Exception {
        assertNamesEmpty(filterTree.filterAccountNames("expenses:notfound"));
    }

    @Test
    public void testFilterNotFoundChildChain() throws Exception {
        assertNamesEmpty(filterTree.filterAccountNames("notfound:notfound"));
    }

    @Test
    public void testEmptyFilter() throws Exception {
        assertNamesEqual(filterTree.filterAccountNames(""), "expenses", "extra", "people", "assets");
    }

    @Test
    public void testFindLeavesInEmpty() throws Exception {
        AccountTree emptyTree = new AccountTree();
        assertNamesEmpty(emptyTree.findLeaves());
    }

    @Test
    public void testFindDeepLeaves() throws Exception {
        assertNamesEqual(tree.findLeaves(), "A:AA", "B:BB");
    }

    @Test
    public void testFindLeavesOnlyRoots() throws Exception {
        AccountTree flatTree = new AccountTree();
        flatTree.findOrCreateAccount("A");
        flatTree.findOrCreateAccount("B");
        assertNamesEqual(flatTree.findLeaves(), "A", "B");
    }

    @Test
    public void testFindLeavesMixed() throws Exception {
        assertNamesEqual(filterTree.findLeaves(), filterTreeLeaves);
    }

    private static <T> void assertNamesEmpty(List<T> accounts) {
        assertTrue(accounts.isEmpty());
    }

    private static <T> void assertNamesEqual(List<T> objects, String... expectedNames) {
        Set<String> actual = new HashSet<String>();
        for (T named : objects) {
            actual.add(named.toString());
        }
        Set<String> expected = new HashSet<String>(Arrays.asList(expectedNames));
        assertEquals(expected, actual);
    }
}
