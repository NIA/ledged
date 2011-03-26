package ru.nia.ledged.android;

import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import ru.nia.ledged.core.Journal;
import ru.nia.ledged.core.Parser;
import ru.nia.ledged.core.Transaction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedHashMap;
import java.util.Map;

public class TransactionsList extends ListActivity {
    Journal journal;
    public static final int ADD_ID = Menu.FIRST;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transaction_list);

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

        fillList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        menu.add(0, ADD_ID, 0, R.string.menu_add);
        return result;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case ADD_ID:
                addTransaction();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void fillList() {
        setListAdapter(
                new ArrayAdapter<Transaction>(this, R.layout.transaction, R.id.transaction_text, journal.getTransactions()));
    }

    private void addTransaction() {
        journal.addTransaction("3-26", "new one", buildMap("expenses:smth", "10", "new account", "-10"));
        fillList();
    }


    private Map<String, String> buildMap(String... args) {
        assert args.length % 2 == 0;

        Map<String, String> map = new LinkedHashMap<String, String>();
        for (int i = 0; i < args.length/2; ++i) {
            map.put(args[2*i], args[2*i + 1]);
        }
        return map;
    }

    private BufferedReader buildInput(String... strings) {
        StringBuilder sb = new StringBuilder();
        for (String s : strings) {
            sb.append(s).append('\n');
        }
        return new BufferedReader(new StringReader(sb.toString()));
    }
}