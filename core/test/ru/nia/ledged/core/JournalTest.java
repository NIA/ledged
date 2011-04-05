package ru.nia.ledged.core;

import org.junit.*;

import java.io.BufferedReader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.*;

import ru.nia.ledged.core.AccountTree.Account;

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
                "  extra:smth  20",
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

        assertPostingsEqual(t.getPostings(), "expenses:smth", "10", "extra:smth", "20", "assets", null);
    }

    @Test
    public void testAddTransaction() throws Exception {
        journal.addTransaction("3-26", "new one", arr("expenses:smth", "new account"), arr("10", ""));
        Transaction t = getLast(journal.getTransactions());
        assertEquals("new one", t.getDescription());
        assertEquals("3-26", t.getDate());
        assertPostingsEqual(t.getPostings(), "expenses:smth", "10", "new account", null);
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

    private Map<String, String> buildMap(String... args) {
        assert args.length % 2 == 0;

        Map<String, String> map = new LinkedHashMap<String, String>();
        for (int i = 0; i < args.length/2; ++i) {
            map.put(args[2*i], args[2*i + 1]);
        }
        return map;
    }

    private <T> T getLast(List<T> list) {
        return list.get(list.size() - 1);
    }

    private <T> T[] arr(T... items) {
        return items;
    }

    private static void assertPostingsEqual(Map<Account,String> postings, String... args) {
        assert args.length % 2 == 0;

        List<Account> keys = new ArrayList<Account>(postings.keySet());
        assertEquals(args.length/2, keys.size());
        for (int i = 0; i < args.length/2; ++i) {
            assertEquals(args[2*i], keys.get(i).toString());
            String amount = postings.get(keys.get(i));
            assertEquals(args[2*i+1], amount);
        }
    }
}
