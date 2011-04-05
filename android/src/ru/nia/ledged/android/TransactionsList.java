package ru.nia.ledged.android;

import android.content.Intent;
import android.net.Uri;
import android.view.MenuInflater;
import ru.nia.ledged.core.AccountTree.Account;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import ru.nia.ledged.core.Journal;
import ru.nia.ledged.core.Parser;
import ru.nia.ledged.core.Transaction;

import java.io.*;
import java.util.*;

public class TransactionsList extends ListActivity {
    Journal journal;

    public static final int ACTIVITY_CREATE = 0;

    private String filename;
    ArrayList<Transaction> unsavedTransactions = new ArrayList<Transaction>();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transaction_list);
        Uri uri = getIntent().getData();
        if (uri != null) {
            filename = uri.getPath();
        }

        try {
            parseFile();
        } catch (IOException e) {
            reportError(R.string.io_error);
            return;
        } catch (Parser.ParserException e) {
            reportError(R.string.parse_error);
            return;
        }

        setListAdapter(
                new ArrayAdapter<Transaction>(this, R.layout.transaction, R.id.transaction_text, journal.getTransactions()));
    }

    private void parseFile() throws IOException, Parser.ParserException {
        FileReader reader = null;
        try {
            reader = new FileReader(filename);
            BufferedReader input = new BufferedReader(reader);
            journal = new Journal(input);
        }
        finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return result;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_transaction:
                addTransaction();
                return true;
            case R.id.save:
                try {
                    save();
                } catch (IOException e) {
                    reportError(R.string.io_error);
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void addTransaction() {
        List<Account> leaves = journal.findLeavesAccounts();
        String[] leaveNames = new String[leaves.size()];
        for (int i = 0; i < leaveNames.length; ++i) {
            leaveNames[i] = leaves.get(i).toString();
        }

        List<Transaction> transactions = journal.getTransactions();
        String[] descriptions = new String[transactions.size()];
        for (int i = 0; i < descriptions.length; ++i) {
            descriptions[i] = transactions.get(i).getDescription().trim();
        }
        // remove duplicates using HashSet
        HashSet<String> uniqueDescriptions = new HashSet<String>(Arrays.asList(descriptions));
        descriptions = new String[uniqueDescriptions.size()];
        uniqueDescriptions.toArray(descriptions);

        Intent i = new Intent(this, TransactionEditor.class);
        i.putExtra(TransactionEditor.KEY_LEAVES_ACCOUNTS, leaveNames);
        i.putExtra(TransactionEditor.KEY_DESCRIPTIONS, descriptions);
        startActivityForResult(i, ACTIVITY_CREATE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (RESULT_CANCELED == resultCode) {
            return;
        }

        Bundle extras = intent.getExtras();

        switch (requestCode) {
            case ACTIVITY_CREATE:
                String date = extras.getString(TransactionEditor.KEY_DATE);
                String description = extras.getString(TransactionEditor.KEY_DESC);
                String[] accounts = extras.getStringArray(TransactionEditor.KEY_ACCOUNTS);
                String[] amounts  = extras.getStringArray(TransactionEditor.KEY_AMOUNTS);

                Transaction t = journal.addTransaction(date, description, accounts, amounts);
                unsavedTransactions.add(t);
                refreshList();
                break;
        }
    }

    void refreshList() {
        ArrayAdapter<?> adapter = (ArrayAdapter<?>) getListAdapter();
        adapter.notifyDataSetChanged();
    }

    void save() throws IOException {
        FileWriter fileWriter = new FileWriter(filename, true);

        for (Transaction t : unsavedTransactions) {
            fileWriter.write(t.toString());
            fileWriter.write("\n\n");
        }
        fileWriter.close();

        unsavedTransactions.clear();
    }

    private void reportError(int message_res_id) {
        Toast.makeText(this, message_res_id, Toast.LENGTH_LONG).show();
    }
}