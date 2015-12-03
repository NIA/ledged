package ru.nia.ledged.android;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.widget.ArrayAdapter;
import ru.nia.ledged.core.AccountTree;
import ru.nia.ledged.core.Transaction;

import java.util.List;
import java.util.Map;

public class TransactionFormatAdapter extends ArrayAdapter<Spanned> {
    private List<Transaction> transactions;

    public TransactionFormatAdapter(Context context, int resource, int textViewResourceId, List<Transaction> transactions) {
        super(context, resource, textViewResourceId);
        this.transactions = transactions;
    }

    @Override
    public int getCount() {
        return transactions.size();
    }

    @Override
    public Spanned getItem(int position) {
        return Html.fromHtml(toHtml(transactions.get(position)));
    }

    private static String toHtml(Transaction t) {
        final StringBuilder sb = new StringBuilder();
        sb.append("<font color='#008b8b'>").append(t.getDate()).append("</font> ").append(t.getDescription()).append("<br>\n");

        String newLine = ""; // no new line yet
        //TODO: customize indent size
        for (Map.Entry<AccountTree.Account, String> entry : t.getPostings().entrySet()) {
            String account = entry.getKey().toString();
            String amount = entry.getValue();

            sb.append(newLine).append("&nbsp;&nbsp;<font color='yellow'>").append(account).append("</font>");
            if (amount != null) {
                String color = isPositive(amount) ? "green" : "red";
                sb.append(" &nbsp;<font color='").append(color).append("'>").append(amount).append("</font>");
            }
            newLine = "<br>\n"; // now add new line
        }
        return sb.toString();
    }

    public static boolean isPositive(String amount) {
        try {
            return (Integer.valueOf(amount.trim()) > 0);
        } catch (NumberFormatException e) {
            return true;
        }
    }
}
