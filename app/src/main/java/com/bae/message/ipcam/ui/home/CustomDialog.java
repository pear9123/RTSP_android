package com.bae.message.ipcam.ui.home;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.DialogFragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import com.bae.message.R;
import com.bae.message.sqlite.CameraContract;
import com.bae.message.sqlite.CameraDbHelper;
import com.bae.message.sqlite.MemoContract;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

public class CustomDialog extends DialogFragment {
    private OnClickListener mOnClickListener;

    private EditText mTitleEditText;
    private EditText mContentsEditText;
    private EditText mIpText;
    private EditText mPortText;
    private EditText mIdText;
    private EditText mPwText;
    private long mCameraId = -1;
    private boolean isValidation = false;
    private int _id = -1;

    public interface OnClickListener {
        void sendRequestCode(int code);
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    public static CustomDialog newInstance(OnClickListener listener) {
        Bundle args = new Bundle();
        CustomDialog fragment = new CustomDialog();
        fragment.setOnClickListener(listener);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_home_dialog, null);

        Bundle mArgs = getArguments();
        _id = mArgs.getInt("_id");
        Log.d("_ID 2 ::::: ", String.valueOf(_id));

        builder.setView(view)
                .setTitle("Camera Register")
                .setNegativeButton("cancle", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                });

        mTitleEditText = view.findViewById(R.id.edit_title);
        mContentsEditText = view.findViewById(R.id.edit_contents);
        mIpText = view.findViewById(R.id.edit_ip);
        mPortText = view.findViewById(R.id.edit_port);
        mIdText = view.findViewById(R.id.edit_id);
        mPwText = view.findViewById(R.id.edit_pw);

        if(_id >= 0) { // 수정 폼
            // Db Selection
            CameraDbHelper dbHelper = CameraDbHelper.getInstance(getContext());
            String sql = "SELECT * FROM camera WHERE _ID = "+_id;
            Cursor cursor = dbHelper.getReadableDatabase().rawQuery(sql, null);
            while (cursor.moveToNext()) {
                mTitleEditText.setText(cursor.getString(cursor.getColumnIndex("title")));
                mContentsEditText.setText(cursor.getString(cursor.getColumnIndex("contents")));
                mIpText.setText(cursor.getString(cursor.getColumnIndex("ip")));
                mPortText.setText(cursor.getString(cursor.getColumnIndex("port")));
                mIdText.setText(cursor.getString(cursor.getColumnIndex("id")));
                mPwText.setText(cursor.getString(cursor.getColumnIndex("pw")));
            }
        }

        return builder.create();
    }

    @Override
    public void onResume() {
        super.onResume();
        final AlertDialog dialog = (AlertDialog)getDialog();
        if(dialog != null) {
            Button button = (Button) dialog.getButton(Dialog.BUTTON_POSITIVE);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isValidation = CheckAllFields();
                    Log.d("VALIDATION :: ", String.valueOf(isValidation));
                    if(isValidation) {
                        String title = mTitleEditText.getText().toString();
                        String contents = mContentsEditText.getText().toString();
                        String ip = mIpText.getText().toString();
                        String port = mPortText.getText().toString();
                        String id = mIdText.getText().toString();
                        String pw = mPwText.getText().toString();

                        ContentValues contentValues = new ContentValues();
                        contentValues.put(CameraContract.CameraEntry.COLUMN_NAME_TITLE, title);
                        contentValues.put(CameraContract.CameraEntry.COLUMN_NAME_CONTENTS, contents);
                        contentValues.put(CameraContract.CameraEntry.COLUMN_NAME_IP, ip);
                        contentValues.put(CameraContract.CameraEntry.COLUMN_NAME_PORT, port);
                        contentValues.put(CameraContract.CameraEntry.COLUMN_NAME_ID, id);
                        contentValues.put(CameraContract.CameraEntry.COLUMN_NAME_PW, pw);

                        SQLiteDatabase db = CameraDbHelper.getInstance(getContext()).getWritableDatabase();
                        if(_id == -1) {  // 등록
                            long newRowId = db.insert(CameraContract.CameraEntry.TABLE_NAME,
                                    null, contentValues);
                            if(newRowId == -1) {
                                DynamicToast.makeError(getContext(), "저장에 문제가 발생했습니다.").show();
                                mOnClickListener.sendRequestCode(3000);
                            } else {
                                DynamicToast.makeSuccess(getContext(), "등록이 완료되었습니다.").show();
                                mOnClickListener.sendRequestCode(2000);
                            }
                        } else {  // 수정
                            int count = db.update(CameraContract.CameraEntry.TABLE_NAME, contentValues,
                                    CameraContract.CameraEntry._ID + " = "+_id, null);
                            if(count == 0) {
                                DynamicToast.makeError(getContext(), "수정에 문제가 발생했습니다.").show();
                                mOnClickListener.sendRequestCode(3000);
                            } else {
                                DynamicToast.makeSuccess(getContext(), "수정되었습니다.").show();
                                mOnClickListener.sendRequestCode(2000);
                            }
                        }
                        dialog.dismiss();
                    } else {

                    }
                }
            });
        }
    }

    private boolean CheckAllFields() {
        if (mTitleEditText.length() == 0) {
            mTitleEditText.setError("This field is required");
            return false;
        }
        if (mContentsEditText.length() == 0) {
            mContentsEditText.setError("This field is required");
            return false;
        }
        if (mIpText.length() == 0) {
            mIpText.setError("Email is required");
            return false;
        }
        if (mPortText.length() == 0) {
            mIpText.setError("Email is required");
            return false;
        }
        if (mIdText.length() == 0) {
            mIpText.setError("Email is required");
            return false;
        }
        if (mPwText.length() == 0) {
            mPwText.setError("Password is required");
            return false;
        }
        // after all validation return true.
        return true;
    }


}
