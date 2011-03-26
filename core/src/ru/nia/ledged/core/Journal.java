package ru.nia.ledged.core;

import ru.nia.ledged.core.AccountTree.Account;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

public class Journal {
    ArrayList<Transaction> transactions = new ArrayList<Transaction>();
    AccountTree accounts = new AccountTree();

    public static final String ACCOUNT_SEPARATOR = ":";

    public Journal(BufferedReader input) throws IOException, Parser.ParserException {

        Parser parser = new Parser(input, new TransactionHandler() {
            public void setYear(String year) { }

            public void start(String date, String description) {
                transactions.add( new Transaction(date, description) );
            }

            public void addPosting(String accountName, String strAmount) {
                addPostingToTransaction(getLastTransaction(), accountName, strAmount);
            }

            public void finish() { }
        });
        parser.parse();
    }

    public List<Transaction> getTransactions() {
        return Collections.unmodifiableList(transactions);
    }

    public Set<Account> getRootAccounts() {
        return accounts.getRootAccounts();
    }

    public Account findChild(String... names) {
        return accounts.findOrCreateChild(false, names);
    }

    public List<Account> filterAccounts(CharSequence constraint) {
        String text = constraint.toString();
        Set<Account> setToFilter;
        if (text.contains(ACCOUNT_SEPARATOR)) {
            int sepPos = text.lastIndexOf(ACCOUNT_SEPARATOR);
            String[] names = text.substring(0, sepPos).split(ACCOUNT_SEPARATOR);
            Account parent = findChild(names);
            if (parent != null) {
                setToFilter = parent.getChildren();
            } else {
                setToFilter = Collections.emptySet();
            }
        } else {
            setToFilter = getRootAccounts();
        }

        List<Account> filtered = new ArrayList<Account>();
        for (Account a : setToFilter) {
            if(a.toString().startsWith(text)) {
                filtered.add(a);
            }
        }
        return filtered;
    }

    private Transaction getLastTransaction() {
        return transactions.get(transactions.size() - 1);
    }

    public void addTransaction(String date, String description, Map<String, String> postings) {
        Transaction t = new Transaction(date, description);
        for (Map.Entry<String, String> e : postings.entrySet()) {
            addPostingToTransaction(t, e.getKey(), e.getValue());
        }
        transactions.add(t);
    }

    private void addPostingToTransaction(Transaction t, String accountName, String strAmount) {
        AccountTree.Account account = accounts.findOrCreateChild(true, accountName.split(ACCOUNT_SEPARATOR));
        BigDecimal amount = (strAmount == null) ? null : new BigDecimal(strAmount);
        t.addPosting(account, amount);
    }
}
