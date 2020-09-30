package com.imb.imbdemo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ConfigEditActivity extends AppCompatActivity {
    SharedPreferences sp;
    private EditText pocNumEdit;
    private EditText pocPwdEdit;

    private EditText centerNameEdit;
    private EditText centerPwdEdit;
    private EditText centerHostEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_edit);

        sp = Sp.getSp(this);
        final String pocNum = sp.getString(Sp.POC_NUM, null);
        final String pocPwd = sp.getString(Sp.POC_PWD, null);

        final String centerName = sp.getString(Sp.CENTER_NAME, null);
        final String centerPwd = sp.getString(Sp.CENTER_PWD, null);
        final String centerHost = sp.getString(Sp.CENTER_HOST, null);

        pocNumEdit = (EditText) findViewById(R.id.edit_num);
        pocPwdEdit = (EditText) findViewById(R.id.edit_pwd);

        centerNameEdit = (EditText) findViewById(R.id.edit_center_name);
        centerPwdEdit = (EditText) findViewById(R.id.edit_center_pwd);
        centerHostEdit = (EditText) findViewById(R.id.edit_center_host);

        pocNumEdit.setText(pocNum);
        pocPwdEdit.setText(pocPwd);

        centerNameEdit.setText(centerName);
        centerPwdEdit.setText(centerPwd);
        centerHostEdit.setText(centerHost);

    }

    public void onConfirmClick(View view) {
        final String num = pocNumEdit.getText().toString().trim();
        final String pwd = pocPwdEdit.getText().toString().trim();
        sp.edit().putString(Sp.POC_NUM,num).commit();
        sp.edit().putString(Sp.POC_PWD,pwd).commit();

        final String centerName = centerNameEdit.getText().toString().trim();
        final String centerPwd = centerPwdEdit.getText().toString().trim();
        final String centerHost = centerHostEdit.getText().toString().trim();
        sp.edit().putString(Sp.CENTER_NAME,centerName).commit();
        sp.edit().putString(Sp.CENTER_PWD,centerPwd).commit();
        sp.edit().putString(Sp.CENTER_HOST,centerHost).commit();

        Toast.makeText(this, "修改成功", Toast.LENGTH_SHORT).show();
    }
}
