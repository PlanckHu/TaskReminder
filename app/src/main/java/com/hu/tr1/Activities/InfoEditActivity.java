package com.hu.tr1.Activities;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.hu.tr1.R;
import com.hu.tr1.Task;

public class InfoEditActivity extends AppCompatActivity {

    private final String TAG = "InfoEditActivity";
    private int tempTaskId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_edit);
        // toolbar
        Toolbar toolbar = findViewById(R.id.info_edit_toolbar);
        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.setNavigationIcon(R.mipmap.cancel);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "click Navigation icon", Toast.LENGTH_SHORT).show();
                backToMainActivity();
            }
        });

        // init
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null && !bundle.getBoolean("newly"))
            readIntent(bundle);

    }

    private void readIntent(Bundle bundle) {
        Log.v(TAG, "reading!");

        tempTaskId = bundle.getInt("id");

        EditText editText = findViewById(R.id.task_conclusion_input);
        editText.setText(bundle.getString("name"));
        EditText start = this.findViewById(R.id.task_start_time_input);
        start.setText(bundle.getString("start_time"));
        EditText end = this.findViewById(R.id.task_end_time_input);
        end.setText(bundle.getString("end_time"));
        EditText contents = this.findViewById(R.id.content_input);
        contents.setText(bundle.getString("contents"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.infoedit_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.confirm:
                Toast.makeText(this, "confirm clicked!", Toast.LENGTH_SHORT).show();
                confirmItemAdded();
                break;
            default:
                break;
        }
        return true;
    }

    public void onClickNav(View view) {
        Toast.makeText(this, "back", Toast.LENGTH_SHORT).show();
        Log.v(TAG, "back");
    }

    private void confirmItemAdded() {
        EditText name = findViewById(R.id.task_conclusion_input);
        EditText startTime = findViewById(R.id.task_start_time_input);
        EditText endTime = findViewById(R.id.task_end_time_input);
        EditText content = findViewById(R.id.content_input);
        Intent intent = new Intent();
        intent.putExtra("conclusion", name.getText().toString());
        intent.putExtra("start_time", startTime.getText().toString());
        intent.putExtra("end_time", endTime.getText().toString());
        intent.putExtra("content", content.getText().toString());
        intent.putExtra("id", tempTaskId);

        setResult(RESULT_OK, intent);
        finish();

    }

    public void backToMainActivity() {
        Intent intent = new Intent();
        intent.putExtra("data_return", "back to main");
        setResult(RESULT_CANCELED, intent);
        finish();
    }
}
