package ru.nia.jledger.core;

import ru.nia.jledger.core.AccountTree.Account;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

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
                AccountTree.Account account = accounts.findOrCreateChild(true, accountName.split(ACCOUNT_SEPARATOR));
                BigDecimal amount = (strAmount == null) ? null : new BigDecimal(strAmount);
                getLastTransaction().addPosting(account, amount);
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

    private Transaction getLastTransaction() {
        return transactions.get(transactions.size() - 1);
    }
}
