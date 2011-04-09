package ru.nia.ledged.android;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import ru.nia.ledged.core.AccountTree;
import ru.nia.ledged.core.AccountTree.Account;

import java.util.Collections;
import java.util.List;

public class AutoCompleteAdapter extends ArrayAdapter<String> {
    private List<String> filtered = Collections.emptyList();
    private AccountTree accounts;

    public AutoCompleteAdapter(Context context, int textViewResourceId, AccountTree accounts) {
        super(context, textViewResourceId);
        this.accounts = accounts;
    }

    @Override
    public int getCount() {
        return filtered.size();
    }

    @Override
    public String getItem(int index) {
        return filtered.get(index);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if(constraint != null) {
                    filtered = accounts.filterAccountNames(constraint.toString());
                    Collections.sort(filtered);
                    filterResults.values = filtered;
                    filterResults.count = filtered.size();
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
