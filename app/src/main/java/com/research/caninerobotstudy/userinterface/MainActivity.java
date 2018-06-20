package com.research.caninerobotstudy.userinterface;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;

public class MainActivity extends AppCompatActivity {
    private String ip = "";
    private int port = -1;
    private String ownerName = "";
    private String dogName = "";
    private String dogGender = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onDogGenderRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.dogGenderFemaleButton:
                if (checked)
                    dogGender = getString(R.string.dog_gender_female_robot_pronunciation);
                    break;
            case R.id.dogGenderMaleButton:
                if (checked)
                    dogGender = getString(R.string.dog_gender_male_robot_pronunciation);
                    break;
        }
    }

    public void connect(View view) {
        ip = ((EditText) findViewById(R.id.serverIPTextview)).getText().toString();
        port = Integer.parseInt(((EditText) findViewById(R.id.serverPortTextview)).getText().toString());
        ownerName = ((EditText) findViewById(R.id.ownerNameTextview)).getText().toString();
        dogName = ((EditText) findViewById(R.id.dogNameTextview)).getText().toString();

        if (ip.isEmpty() || port == -1 || port == 0 ||ownerName.isEmpty() || dogName.isEmpty() || dogGender.isEmpty()) {
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                RobotCommand robotCommand = new RobotCommand(ip, port, ownerName, dogName, dogGender);
                if (robotCommand.sendInfoViaSocket(getBaseContext(), getString(R.string.initial_connection_request))) {
                    Intent intent = new Intent(getApplicationContext(), ActivityGalleries.class);
                    startActivity(intent);
                }
            }
        }).start();
    }
}
