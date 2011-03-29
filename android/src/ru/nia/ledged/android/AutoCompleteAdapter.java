package ru.nia.ledged.android;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import ru.nia.ledged.core.AccountTree;
import ru.nia.ledged.core.AccountTree.Account;

import java.util.Collections;
import java.util.List;

public class AutoCompleteAdapter extends ArrayAdapter<Account> {
    private List<Account> mData = Collections.emptyList();
    private AccountTree accounts;

    public AutoCompleteAdapter(Context context, int textViewResourceId, AccountTree accounts) {
        super(context, textViewResourceId);
        this.accounts = accounts;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Account getItem(int index) {
        return mData.get(index);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if(constraint != null) {
                    mData = accounts.filterAccounts(constraint.toString());
                    filterResults.values = mData;
                    filterResults.count = mData.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence contraint, FilterResults results) {
                if(results != null && results.count > 0) {
                    notifyDataSetChanged();
                }
                else {
                    notifyDataSetInvalidated();
                }
            }
        };
    }
}
