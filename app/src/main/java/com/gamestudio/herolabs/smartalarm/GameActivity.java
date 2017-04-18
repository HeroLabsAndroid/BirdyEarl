package com.gamestudio.herolabs.smartalarm;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.gamestudio.herolabs.smartalarm.Interfaces.GameInterface;

import org.greenrobot.eventbus.EventBus;

import java.util.Random;

public class GameActivity extends AppCompatActivity {

    MediaPlayer wrongMediaPlayer;

    int round = 0;
    int number1 = 0;
    int number2 = 0;

    TextView number1TextView;
    TextView number2TextView;
    TextView operationTextView;

    EditText answerEditText;

    Button okBtn;

    Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        number1TextView =(TextView) findViewById(R.id.number1TextView);
        number2TextView = (TextView) findViewById(R.id.number2TextView);
        operationTextView = (TextView) findViewById(R.id.operationTextView);

        answerEditText = (EditText) findViewById(R.id.answerEditText);

        okBtn = (Button) findViewById(R.id.okBtn);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                okBtnClick();
            }
        });

        initMediaPlayers();
        startRound();
    }

    private void okBtnClick() {
        if(round>=4) {
            finish();
            EventBus.getDefault().postSticky(new Message("succccccess"));
        } else {

            int n1 = number1;
            int n2 = number2;
            String answer = answerEditText.getText().toString();

            try {
                if (operationTextView.getText() == "+") {
                    if (Integer.parseInt(answer) == n1 + n2) {
                        round++;
                        startRound();
                    } else {
                        wrongMediaPlayer.start();
                    }
                } else {
                    if (Integer.parseInt(answer) == n2 - n1) {
                        round++;
                        startRound();
                    } else {
                        wrongMediaPlayer.start();
                    }
                }
            } catch (Exception e) {

            }

            answerEditText.setText("");

        }
    }

    private void startRound() {
        number1 = random.nextInt(60) + 10;
        number1TextView.setText(""+number1);
        number2 = random.nextInt(60) + 10;
        number2TextView.setText(""+number2);

        boolean sign = random.nextBoolean();
        if(sign) operationTextView.setText("+"); else  operationTextView.setText("-");
    }

    private void initMediaPlayers() {
        wrongMediaPlayer = MediaPlayer.create(this, R.raw.wrong);
    }

    @Override
    public void onBackPressed() {
        wrongMediaPlayer.start();
    }
}
