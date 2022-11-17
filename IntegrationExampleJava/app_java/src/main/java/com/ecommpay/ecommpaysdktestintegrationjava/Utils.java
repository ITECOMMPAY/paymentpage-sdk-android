package com.ecommpay.ecommpaysdktestintegrationjava;

import android.app.AlertDialog;
import android.content.Context;

public class Utils {
    public static void showMessage(Context context, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.ok_text, (dialog, which) -> {});
        builder.create().show();
    }
}
