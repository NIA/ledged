package ru.nia.jledger.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import ru.nia.jledger.core.AccountTree.Account;

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
                AccountTree.Account account = accounts.findOrCreateChild(accountName.split(":"));
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
