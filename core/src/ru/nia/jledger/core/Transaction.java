package ru.nia.jledger.core;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import ru.nia.jledger.core.AccountTree.Account;

public class Transaction {
    private String description;
    private String date;
    private LinkedHashMap<Account,BigDecimal> postings = new LinkedHashMap<Account, BigDecimal>();

    public Transaction(String date, String description) {
        this.date = date;
        this.description = description;
    }

    public void addPosting(Account account, BigDecimal amount) {
        postings.put(account, amount);
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public Map<Account, BigDecimal> getPostings() {
        return Collections.unmodifiableMap(postings);
    }

    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(date).append(' ').append(description).append('\n');

        //TODO: customize indent size
        for (Map.Entry<Account, BigDecimal> entry : postings.entrySet()) {
            Account account = entry.getKey();
            BigDecimal amount = entry.getValue();

            sb.append("  ").append(account.toString());
            if (amount != null) {
                sb.append("  ").append(amount);
            }
            sb.append('\n');
        }
        return sb.toString();
    }
}
