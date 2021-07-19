package com.bae.message.ipcam.ui.dashboard;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.bae.message.R;
import com.bae.message.sqlite.MemoContract;
import com.bae.message.sqlite.MemoDbHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class DashboardFragment extends Fragment {
    private Context context;
    private DashboardViewModel dashboardViewModel;
    private MemoAdapter mAdapter;
    public static final int REQUEST_CODE_INSERT = 1000;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = container.getContext();
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        // 추가하기
        FloatingActionButton fab = root.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(context, MemoActivity.class),
                        REQUEST_CODE_INSERT);
            }
        });

        ListView listView = root.findViewById(R.id.memo_list);

        Cursor cursor = getMemoCursor();
        mAdapter = new MemoAdapter(context, cursor);
        listView.setAdapter(mAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(context, MemoActivity.class);
                Cursor cursor = (Cursor) mAdapter.getItem(position);
                String title = cursor.getString(cursor.getColumnIndexOrThrow(MemoContract.MemoEntry.COLUMN_NAME_TITLE));
                String contents = cursor.getString(cursor.getColumnIndexOrThrow(MemoContract.MemoEntry.COLUMN_NAME_CONTENTS));

                intent.putExtra("id", id);
                intent.putExtra("title", title);
                intent.putExtra("contents", contents);

                startActivityForResult(intent, REQUEST_CODE_INSERT);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final long deleteId = id;
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("메모 삭제");
                builder.setMessage("메모를 삭제하시겠습니까?");
                builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SQLiteDatabase db = MemoDbHelper.getInstance(context).getWritableDatabase();
                        int deleteCount = db.delete(MemoContract.MemoEntry.TABLE_NAME,
                                MemoContract.MemoEntry._ID + " = " + deleteId, null);
                        if(deleteCount == 0) {
                            Toast.makeText(context, "삭제에 문제가 발생했습니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            mAdapter.swapCursor(getMemoCursor());
                            Toast.makeText(context, "메모가 삭제되었습니다", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.setNegativeButton("취소", null);
                builder.show();
                return true;
            }
        });

        return root;
    }

    private Cursor getMemoCursor() {
        MemoDbHelper dbHelper = MemoDbHelper.getInstance(context);
        return dbHelper.getReadableDatabase()
                .query(MemoContract.MemoEntry.TABLE_NAME,
                        null, null, null, null, null, MemoContract.MemoEntry._ID+" DESC");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_INSERT ) {
            mAdapter.swapCursor(getMemoCursor());
        }
    }



    private static class MemoAdapter extends CursorAdapter {

        public MemoAdapter(Context context, Cursor c) {
            super(context, c, false);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return LayoutInflater.from(context)
                    .inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            TextView titleText = view.findViewById(android.R.id.text1);
            titleText.setText(cursor.getString(cursor.getColumnIndexOrThrow(MemoContract.MemoEntry.COLUMN_NAME_TITLE)));
        }
    }
}