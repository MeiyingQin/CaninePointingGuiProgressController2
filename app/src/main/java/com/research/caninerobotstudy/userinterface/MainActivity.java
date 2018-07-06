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
    private String pointerName = "";
    private String assistantName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    private String getInitialInformationMessage() {
        String message = "";

        message += getString(R.string.initial_connection_information) + getString(R.string.robot_command_deliminator);
        message += ownerName + getString(R.string.robot_command_deliminator);
        message += dogName + getString(R.string.robot_command_deliminator);
        message += dogGender + getString(R.string.robot_command_deliminator);
        message += pointerName + getString(R.string.robot_command_deliminator);
        message += assistantName;

        return message;
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
        pointerName = ((EditText) findViewById(R.id.pointerNameTextview)).getText().toString();
        assistantName = ((EditText) findViewById(R.id.assistantNameTextview)).getText().toString();

        if (ip.isEmpty() || port == -1 || port == 0 ||ownerName.isEmpty() || dogName.isEmpty() || dogGender.isEmpty() || pointerName.isEmpty() || assistantName.isEmpty()) {
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                RobotCommand robotCommand = new RobotCommand(ip, port);
                if (robotCommand.sendInfoViaSocket(getBaseContext(), getInitialInformationMessage())) {
                    Intent intent = new Intent(getApplicationContext(), ActivityGalleries.class);
                    startActivity(intent);
                }
            }
        }).start();
    }
}
