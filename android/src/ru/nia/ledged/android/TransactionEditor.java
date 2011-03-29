package ru.nia.ledged.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import ru.nia.ledged.core.AccountTree;
import ru.nia.ledged.core.Journal;

import java.util.ArrayList;
import java.util.Date;

public class TransactionEditor extends Activity {
    AccountTree accounts = new AccountTree();

    public static String KEY_LEAVES_ACCOUNTS = "leaves";
    public static final String KEY_DATE = "date";
    public static final String KEY_DESC = "desc";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transaction_editor);

        Bundle extras = getIntent().getExtras();
        ArrayList<String> leaveNames = extras.getStringArrayList(KEY_LEAVES_ACCOUNTS);
        assert leaveNames != null;
        for (String name : leaveNames) {
            accounts.findOrCreateAccount(name);
        }

        final EditText dateEdit = (EditText) findViewById(R.id.date);
        dateEdit.setText(DateFormat.format("M-d", new Date()));

        final EditText descEdit = (EditText) findViewById(R.id.description);

        AutoCompleteTextView accName = (AutoCompleteTextView) findViewById(R.id.account);
        accName.setAdapter(new AutoCompleteAdapter(this, R.layout.completion_item, accounts));

        Button confirmButtion = (Button) findViewById(R.id.confirm);
        confirmButtion.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Bundle extras = new Bundle();
                extras.putString(KEY_DATE, dateEdit.getText().toString());
                extras.putString(KEY_DESC, descEdit.getText().toString());
                Intent intent = new Intent();
                intent.putExtras(extras);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
