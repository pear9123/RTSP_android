package com.bae.message.ipcam.ui.dashboard;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.bae.message.R;
import com.bae.message.sqlite.MemoContract;
import com.bae.message.sqlite.MemoDbHelper;

public class MemoActivity extends AppCompatActivity {
    private EditText mTitleEditText;
    private EditText mContentsEditText;
    private long mMemoId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo);
        mTitleEditText = findViewById(R.id.title_edit);
        mContentsEditText = findViewById(R.id.contents_edit);

        Intent intent = getIntent();
        if(intent != null) {
            mMemoId = intent.getLongExtra("id", -1);
            String title = intent.getStringExtra("title");
            String contents = intent.getStringExtra("contents");

            mTitleEditText.setText(title);
            mContentsEditText.setText(contents);
        }
    }

    // 뒤로가기
    @Override
    public void onBackPressed() {
        String title = mTitleEditText.getText().toString();
        String contents = mContentsEditText.getText().toString();

        ContentValues contentValues = new ContentValues();
        contentValues.put(MemoContract.MemoEntry.COLUMN_NAME_TITLE, title);
        contentValues.put(MemoContract.MemoEntry.COLUMN_NAME_CONTENTS, contents);

        SQLiteDatabase db = MemoDbHelper.getInstance(this).getWritableDatabase();
        if(mMemoId == -1) {
            // INSERT
            long newRowId = db.insert(MemoContract.MemoEntry.TABLE_NAME,
                    null,
                    contentValues);

            if(newRowId == -1) {
                Toast.makeText(this, "저장에 문제가 발생했습니다.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "메모가 저장되었습니다.", Toast.LENGTH_SHORT).show();
            }
        } else {
            // UPDATE
            int count = db.update(MemoContract.MemoEntry.TABLE_NAME, contentValues,
                    MemoContract.MemoEntry._ID + " = "+mMemoId, null);
            if(count == 0) {
                Toast.makeText(this, "수정에 문제가 발생했습니다.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "메모가 수정되었습니다.", Toast.LENGTH_SHORT).show();
            }
        }

        super.onBackPressed();
    }
}