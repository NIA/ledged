package ru.nia.jledger.core;

import org.junit.*;

import java.io.BufferedReader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.*;

import ru.nia.jledger.core.AccountTree.Account;

import static org.junit.Assert.*;

public class JournalTest {
    Journal journal;

    @Before
    public void setUp() throws Exception {
        BufferedReader input = buildInput(
                "Y2011",
                "",
                "3-13 first transaction",
                "  expenses:smth  10",
                "  assets",
                "",
                "3-14 grill bar",
                "  expenses:food  30",
                "  people:smith  -10",
                "  assets:cash"
        );
        journal = new Journal(input);
    }

    @Test
    public void testAccountsInitialization() {
        Account expenses;
        assertNotNull(expenses = findAccount("expenses", journal.getRootAccounts()));
        assertNotNull(findAccount("food", expenses.getChildren()));
    }

    @Test
    public void testTransactionsInitialization() throws Exception {
        Transaction t = journal.getTransactions().get(0);

        assertEquals("3-13", t.getDate());
        assertEquals("first transaction", t.getDescription());

        assertPostingsEqual(t.getPostings(), "expenses:smth", "10", "assets", null);
    }

    private BufferedReader buildInput(String... strings) {
        StringBuilder sb = new StringBuilder();
        for (String s : strings) {
            sb.append(s).append('\n');
        }
        return new BufferedReader(new StringReader(sb.toString()));
    }

    private Account findAccount(String name, Set<Account> accounts) {
        for (Account a : accounts) {
            if (a.getName().equals(name)) {
                return a;
            }
        }
        return null;
    }

    private void assertPostingsEqual(Map<Account,BigDecimal> postings, String... args) {
        assert args.length % 2 == 0;

        List<Account> keys = new ArrayList<Account>(postings.keySet());
        assertEquals(args.length/2, keys.size());
        for (int i = 0; i < args.length/2; ++i) {
            assertEquals(args[2*i], keys.get(i).toString());
            BigDecimal amount = postings.get(keys.get(i));
            assertEquals(args[2*i+1], (amount == null) ? null : amount.toString());
        }
    }
}
