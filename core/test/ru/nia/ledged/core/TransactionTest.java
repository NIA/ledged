package ru.nia.ledged.core;

import org.junit.*;
import ru.nia.ledged.core.AccountTree.Account;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class TransactionTest {
    AccountTree tree = new AccountTree();
    Account A, AA, B;

    Transaction transaction;

    @Before
    public void setUp() {
        // TODO: some common fixtures for all test classes
        A = tree.findOrCreateAccount("A");
        AA = tree.findOrCreateAccount("A:AA");
        B = tree.findOrCreateAccount("B");

        transaction = new Transaction("3-4", "Grill Bar");
    }

    private Set asSet(Object... objs) {
        return new HashSet<Object>(Arrays.asList(objs));
    }

    @Test
    public void testProperties() {
        assertEquals("3-4", transaction.getDate());
        assertEquals("Grill Bar", transaction.getDescription());
    }

    @Test
    public void testAddPosting() {
        transaction.addPosting(A, "2");

        assertEquals(asSet(A), transaction.getPostings().keySet());
        assertEquals("2", transaction.getPostings().get(A));
    }

    @Test
    public void testToString() {
        transaction.addPosting(AA, "2.8");
        transaction.addPosting(B, null);

        assertEquals(
                "3-4 Grill Bar\n" +
                "  A:AA  2.8\n" +
                "  B",
                transaction.toString());
    }
}
