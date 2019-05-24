package com.example.androidpractice;

import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.util.Log;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.content.Context;
import com.example.androidpractice.db.*;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private TaskDbHelper mHelper;
    private ListView mTaskListView;
    private ArrayAdapter<String> mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHelper = new TaskDbHelper(this);
        mTaskListView = (ListView)findViewById(R.id.list_todo);



        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Todolist"); //string is custom name you want

 /*
        Button button1 = findViewById(R.id.button1);
        final EditText enternameT = findViewById(R.id.enternameT);
        final TextView shownameT = findViewById(R.id.shownameT);
        button1.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                String username = enternameT.getText().toString().trim();
                shownameT.setText("Hello,"+username);
            }
        });*/
        updateUI();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()){

            case R.id.action_add_task:
                final EditText taskEditText = new EditText(this);
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("Add a new task")
                        .setMessage("What's next?")
                        .setView(taskEditText)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                String task = String.valueOf(taskEditText.getText());
                                //Log.d(TAG, "Task to add"+task);
                                SQLiteDatabase db = mHelper.getWritableDatabase();
                                ContentValues values = new ContentValues();
                                values.put(TaskContract.TaskEntry.COL_TASK_TITLE, task);
                                db.insertWithOnConflict(TaskContract.TaskEntry.TABLE,
                                        null,
                                        values,
                                        SQLiteDatabase.CONFLICT_REPLACE);
                                db.close();
                                updateUI();
                            }

                        })

                        .setNegativeButton("Cancel", null)
                        .create();

                dialog.show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }



    }


    //I wanna a swipe delete feature hmm
    // But strike and unstrike would do as now
    public void crossIt(View view) {
        Log.d(TAG, "hmm I'm done.");
        View parent = (View) view.getParent();
        TextView taskTextView = parent.findViewById(R.id.task_title);
        //String task = String.valueOf(taskTextView.getText());
        if(taskTextView.getPaint().isStrikeThruText()){
            taskTextView.setPaintFlags(taskTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }else{
            taskTextView.setPaintFlags(taskTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        updateUI();
    }
    public void deleteTask(View view) {
        Log.d(TAG, "I'm deleting.");
        View parent = (View) view.getParent();
        TextView taskTextView = parent.findViewById(R.id.task_title);
        String task = String.valueOf(taskTextView.getText());
        SQLiteDatabase db = mHelper.getWritableDatabase();
        db.delete(TaskContract.TaskEntry.TABLE,
                TaskContract.TaskEntry.COL_TASK_TITLE + " = ?",
                new String[]{task});
        db.close();
        updateUI();
    }
    private void updateUI() {
        ArrayList<String> taskList = new ArrayList<>();
        Log.d(TAG,"I'm in update "+taskList);
       SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor cursor = db.query(TaskContract.TaskEntry.TABLE,
                new String[]{TaskContract.TaskEntry._ID, TaskContract.TaskEntry.COL_TASK_TITLE},
                null,null,null,null,null);

        while(((Cursor) cursor).moveToNext()){
            int idx = cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_TITLE);
            taskList.add(cursor.getString(idx));
        }

        if(mAdapter == null) {
            Log.d(TAG,"mAdapter is null "+taskList);
            mAdapter = new ArrayAdapter<>(this,
                    R.layout.item_todo,
                    R.id.task_title,
                    taskList);
            mTaskListView.setAdapter(mAdapter);

        }else{
            Log.d(TAG,"mAdapter isn't null "+taskList);
            mAdapter.clear();
            mAdapter.addAll(taskList);
            mAdapter.notifyDataSetChanged();
        }

       cursor.close();
        db.close();/**/
    }

}
