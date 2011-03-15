package ru.nia.jledger.ledged;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.Toast;
import ru.nia.jledger.core.Journal;
import ru.nia.jledger.core.Parser;
import ru.nia.jledger.core.Transaction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

public class TransactionEditor extends Activity {
    Journal journal;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transaction_editor);

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
        try {
            journal = new Journal(input);
        } catch (IOException e) {
            Toast.makeText(this, R.string.io_error, Toast.LENGTH_SHORT).show();
        } catch (Parser.ParserException e) {
            Toast.makeText(this, R.string.parse_error, Toast.LENGTH_SHORT).show();
        }

        ListView transactions = (ListView) findViewById(R.id.transactions);
        transactions.setAdapter(
                new ArrayAdapter<Transaction>(this, R.layout.transaction, R.id.transaction_text, journal.getTransactions()));

        final AutoCompleteTextView accName = (AutoCompleteTextView) findViewById(R.id.account);
        accName.setAdapter(new AutoCompleteAdapter(this, R.layout.completion_item, journal));
    }

    private BufferedReader buildInput(String... strings) {
        StringBuilder sb = new StringBuilder();
        for (String s : strings) {
            sb.append(s).append('\n');
        }
        return new BufferedReader(new StringReader(sb.toString()));
    }
}