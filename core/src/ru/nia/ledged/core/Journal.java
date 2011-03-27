package ru.nia.ledged.core;

import ru.nia.ledged.core.AccountTree.Account;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

public class Journal {
    ArrayList<Transaction> transactions = new ArrayList<Transaction>();
    AccountTree accounts = new AccountTree();


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

    public List<Account> filterAccounts(CharSequence constraint) {
        return accounts.filterAccounts(constraint.toString());
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
        AccountTree.Account account = accounts.findOrCreateChild(accountName.split(AccountTree.ACCOUNT_SEPARATOR));
        BigDecimal amount = (strAmount == null) ? null : new BigDecimal(strAmount);
        t.addPosting(account, amount);
    }
}
