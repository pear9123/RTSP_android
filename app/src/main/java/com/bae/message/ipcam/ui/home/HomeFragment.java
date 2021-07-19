package com.bae.message.ipcam.ui.home;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bae.message.MainActivity;
import com.bae.message.R;
import com.bae.message.ipcam.CamListActivity;
import com.bae.message.ipcam.VideoActivity;
import com.bae.message.ipcam.ui.dashboard.MemoActivity;
import com.bae.message.sqlite.CameraContract;
import com.bae.message.sqlite.CameraDbHelper;
import com.bae.message.sqlite.MemoContract;
import com.bae.message.sqlite.MemoDbHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements RecyclerAdapter.RecyclerViewClickListener {
    private HomeViewModel homeViewModel;
    private FirebaseAuth mAuth;
    private Context context;
    private List<CardItem> dataList = new ArrayList<>();
    private RecyclerAdapter adapter;
    private RecyclerView recyclerView;
    private final static String SAVE_TAG = "SAVE_CAMERA_INFO";
    private final static String UPDATE_TAG = "UPDATE_CAMERA_INFO";

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = container.getContext();
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
        // 리사이클러뷰
        recyclerView = root.findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        // 메인화면 로드
        getCameraCursor();

        // (+)추가 아이콘
        FloatingActionButton fab = root.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog.newInstance(new CustomDialog.OnClickListener() {
                    @Override
                    public void sendRequestCode(int code) {
                        if(code == 2000) {
                            getCameraCursor();
                        } else {}
                    }
                }).show(getActivity().getSupportFragmentManager(), "CustomDialog");
            }
        });
        return root;
    }


    @Override
    public void onItemClicked(int position) {
        Intent intent = new Intent(context, VideoActivity.class);
        intent.putExtra(VideoActivity.RTSP_URL, dataList.get(position).get_id());
        startActivity(intent);
    }

    // 수정
    @Override
    public void onEditButtonClicked(int position, int _id) {
        Log.d("_ID 1 ::::: ", String.valueOf(_id));
        Bundle args = new Bundle();
        args.putInt("_id", _id);
        CustomDialog fragment = new CustomDialog();
        fragment.setArguments(args);
        fragment.setOnClickListener(new CustomDialog.OnClickListener() {
            @Override
            public void sendRequestCode(int code) {
                if(code == 2000) {
                    getCameraCursor();
                } else {}
            }
        });
        fragment.show(getActivity().getSupportFragmentManager(), "CustomDialog");

//        CustomDialog.newInstance(new CustomDialog.OnClickListener() {
//            @Override
//            public void sendRequestCode(int code) {
//                if(code == 2000) {
//                    getCameraCursor();
//                } else {}
//            }
//        }).show(getActivity().getSupportFragmentManager(), "CustomDialog");
    }

    // 삭제
    @Override
    public void onDeleteButtonClicked(int position, int _id) {
        Log.d("_ID : ", String.valueOf(_id));
        final int deleteId = _id;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("장비 정보 삭제");
        builder.setMessage("장비 정보를 삭제하시겠습니까?");
        builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SQLiteDatabase db = CameraDbHelper.getInstance(context).getWritableDatabase();
                int deleteCount = db.delete(CameraContract.CameraEntry.TABLE_NAME,
                        CameraContract.CameraEntry._ID + " = " + deleteId, null);
                if(deleteCount == 0) {
                    Toast.makeText(context, "삭제에 문제가 발생했습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    //Toast.makeText(context, "삭제되었습니다", Toast.LENGTH_SHORT).show();
                    DynamicToast.makeSuccess(context, "삭제되었습니다").show();
                    getCameraCursor();
                }
            }
        });
        builder.setNegativeButton("취소", null);
        builder.show();
    }

    private void getCameraCursor() {
        CameraDbHelper dbHelper = CameraDbHelper.getInstance(context);
        Cursor cursor = dbHelper.getReadableDatabase()
                                .query(CameraContract.CameraEntry.TABLE_NAME,
                        null, null, null, null, null, CameraContract.CameraEntry._ID+" DESC");
        dataList.clear();
        while (cursor.moveToNext()) {
            CardItem cardItem = new CardItem();
            cardItem.set_id(cursor.getInt(cursor.getColumnIndex(CameraContract.CameraEntry._ID)));
            cardItem.setTitle(cursor.getString(cursor.getColumnIndex(CameraContract.CameraEntry.COLUMN_NAME_TITLE)));
            cardItem.setContents(cursor.getString(cursor.getColumnIndex(CameraContract.CameraEntry.COLUMN_NAME_CONTENTS)));
            cardItem.setIp(cursor.getString(cursor.getColumnIndex(CameraContract.CameraEntry.COLUMN_NAME_IP)));
            cardItem.setPort(cursor.getString(cursor.getColumnIndex(CameraContract.CameraEntry.COLUMN_NAME_PORT)));
            cardItem.setId(cursor.getString(cursor.getColumnIndex(CameraContract.CameraEntry.COLUMN_NAME_ID)));
            cardItem.setPw(cursor.getString(cursor.getColumnIndex(CameraContract.CameraEntry.COLUMN_NAME_PW)));
            dataList.add(cardItem);
        }
        adapter = new RecyclerAdapter(dataList);
        recyclerView.setAdapter(adapter);
        // 클릭 활성화
        adapter.setOnClickListener(this);
    }





}