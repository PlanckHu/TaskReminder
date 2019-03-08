package com.hu.tr_v1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class TaskAdding extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_adding);
    }

    public void onClickConfirm(View view){
        Intent intent = new Intent();
        intent.putExtra("data_return", "this is the returned value");
        // TODO: 加入结果检验，是否符合格式/是否为空 之类的
        String name = getTextFromEdittext(R.id.task_adding_activity_task_name);
        if(name.isEmpty()){
            onClickCancel(view);
            return;
        }
        String time = getTextFromEdittext(R.id.task_adding_activity_task_time);
        String deadline = getTextFromEdittext(R.id.task_adding_activity_deadline);
        String content = getTextFromEdittext(R.id.task_adding_activity_task_content);

        SQLOperator.insertTask(new String[]{"name", "begin_time", "deadline", "content"},
                new String[]{name, time, deadline, content});

        // 先写入文件，再传回上一个activity
        setResult(RESULT_OK, intent);
        finish();
    }

    public void onClickCancel(View view){
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    private String getTextFromEdittext(int id){
        EditText editText = findViewById(id);
        String data = editText.getText().toString();
        return data;
    }
}
