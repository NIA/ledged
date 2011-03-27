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
        A = tree.findOrCreateChild("A");
        AA = tree.findOrCreateChild("A", "AA");
        B = tree.findOrCreateChild("B");

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
        BigDecimal amount = BigDecimal.valueOf(2);
        transaction.addPosting(A, amount);

        assertEquals(asSet(A), transaction.getPostings().keySet());
        assertEquals(amount, transaction.getPostings().get(A));
    }

    @Test
    public void testToString() {
        transaction.addPosting(AA, new BigDecimal("2.8"));
        transaction.addPosting(B, null);

        assertEquals(
                "3-4 Grill Bar\n" +
                "  A:AA  2.8\n" +
                "  B",
                transaction.toString());
    }
}
