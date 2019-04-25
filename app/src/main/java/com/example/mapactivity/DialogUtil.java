package com.example.mapactivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;


public class DialogUtil {

    public static void showOkDialogBox(Context context, int message, final CallBack callBack) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_layout_for_single_message);
        WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.dimAmount = 0.5f;
        dialog.getWindow().setAttributes(layoutParams);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView txt_dialog_message = (TextView) dialog.findViewById(R.id.txt_message);
        txt_dialog_message.setText(message);
        Button btn_ok = (Button) dialog.findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (callBack != null) {
                    callBack.onDismiss(true);
                }
                dialog.dismiss();
            }
        });
        if (!((Activity) context).isFinishing()) {
            dialog.show();
        }

    }

    public static void showOkDialogBox(Context context, String message, final CallBack callBack) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_layout_for_single_message);
        WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.dimAmount = 0.5f;
        dialog.getWindow().setAttributes(layoutParams);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView txt_dialog_message = (TextView) dialog.findViewById(R.id.txt_message);
        txt_dialog_message.setText(message);
        Button btn_ok = (Button) dialog.findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (callBack != null) {
                    callBack.onDismiss(true);
                }
                dialog.dismiss();
            }
        });
        if (!((Activity) context).isFinishing()) {
            dialog.show();
        }
    }

    public static void showOkCancelDialog(Context context, int message, final CallBack callBack) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message);
        builder.setTitle(context.getResources().getString(R.string.app_name));
        builder.setCancelable(true);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (callBack != null) {
                    callBack.onDismiss(true);
                }
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (callBack != null) {
                    callBack.onDismiss(false);
                }
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (callBack != null) {
                    callBack.onDismiss(false);
                }
            }
        });


        builder.show();

    }

    public interface CallBack {
        void onDismiss(boolean isPressedOK);
    }


//    public static void showOkDialog(Context context, String message, final CallBack callBack) {
//        final Dialog dialog = new Dialog(context);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setContentView(R.layout.dialog_layout_for_single_message);
//        WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
//        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
//        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
//        layoutParams.dimAmount = 0.5f;
//        dialog.getWindow().setAttributes(layoutParams);
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//        dialog.setCanceledOnTouchOutside(false);
//
//
//        Button btn_ok = (Button) dialog.findViewById(R.id.btn_ok);
//        RelativeLayout layout_main = (RelativeLayout) dialog.findViewById(R.id.layout_main);
//        TextView txt_message1 = (TextView) dialog.findViewById(R.id.txt_message);
//        txt_message1.setText(message);
//        btn_ok.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (callBack != null) {
//                    callBack.onDismiss(true);
//                }
//                dialog.dismiss();
//            }
//        });
//        layout_main.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dialog.cancel();
//            }
//        });
//
//        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
//            @Override
//            public void onCancel(DialogInterface dialogInterface) {
//                if (callBack != null) {
//                    callBack.onDismiss(false);
//                }
//            }
//        });
//        if (!((Activity) context).isFinishing()) {
//            dialog.show();
//        }
//    }

}



