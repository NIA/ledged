package ru.nia.ledged.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class MessageDialog {
    private AlertDialog dialog;

    public MessageDialog(final Activity activity, int titleRedId, String message, final boolean finishActivity) {
        dialog = new AlertDialog.Builder(activity).create();
        dialog.setTitle(titleRedId);
        dialog.setMessage(message);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, activity.getString(R.string.ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (finishActivity) {
                    activity.finish();
                }
            }
        });
    }

    void show() {
        dialog.show();
    }
}
