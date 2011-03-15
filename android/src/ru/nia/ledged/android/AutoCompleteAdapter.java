package ru.nia.ledged.android;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import ru.nia.ledged.core.AccountTree.Account;
import ru.nia.ledged.core.Journal;

import java.util.Collections;
import java.util.List;

public class AutoCompleteAdapter extends ArrayAdapter<Account> {
    private List<Account> mData = Collections.emptyList();
    private Journal journal;

    public AutoCompleteAdapter(Context context, int textViewResourceId, Journal journal) {
        super(context, textViewResourceId);
        this.journal = journal;
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
                    mData = journal.filterAccounts(constraint);
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
