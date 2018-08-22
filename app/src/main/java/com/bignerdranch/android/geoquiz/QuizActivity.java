package com.bignerdranch.android.geoquiz;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class QuizActivity extends AppCompatActivity {

    private static final String TAG = "QuizActivity";
    private static final String KEY_INDEX = "index";
    private static final int REQUEST_CODE_CHEAT = 0;
    private static final String EXTRA_ANSWER_IS_TRUE =
            "com.bignerdranch.android.geoquiz.answer_is_true";

    private Button mTrueButton;
    private Button mFalseButton;
    private ImageButton mNextButton;
    private ImageButton mPrevButton;
    private Button mCheatButton;
    private TextView mQuestionTextView;


    private Question[] mQuestionBank = new Question[] {
        new Question(R.string.question_australia, true),
        new Question(R.string.question_oceans,true),
        new Question(R.string.question_mideast, false),
        new Question(R.string.question_africa, false),
        new Question(R.string.question_americas,true),
        new Question(R.string.question_asia,true)
    };

    private int mCurrentIndex = 0;
    private boolean mIsCheater;

    public QuizActivity() {}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        //returns pointer to current question
        if(savedInstanceState !=  null){
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
        }

        //setup buttons and listeners
        mQuestionTextView = findViewById(R.id.question_text_view);
        mQuestionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                updateQuestion();

            }
        });

        mTrueButton  = findViewById(R.id.true_button);
        mTrueButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                checkAnswer(true);
                /*
                Toast toast = Toast.makeText(QuizActivity.this,
                        R.string.correct_toast,
                        Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP,0,0);
                toast.show();
                */
            }
        });

        mFalseButton = findViewById(R.id.false_button);
        mFalseButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                checkAnswer(false);
                /*
                Toast toast = Toast.makeText(QuizActivity.this,
                        R.string.incorrect_toast,
                        Toast.LENGTH_SHORT);

                toast.setGravity(Gravity.TOP,0,0);
                toast.show();
                */
            }
        });

        mPrevButton = findViewById(R.id.prev_button);
        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentIndex = (mCurrentIndex - 1 + mQuestionBank.length) % mQuestionBank.length;
                mIsCheater = false;
                updateQuestion();
            }
        });

        mNextButton = findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                mIsCheater = false;
                updateQuestion();
            }
        });

        mCheatButton = findViewById(R.id.cheat_button);
        mCheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Start Cheat Activity
                boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
                Intent intent = CheatActivity.newIntent(QuizActivity.this, answerIsTrue);

                startActivityForResult(intent, REQUEST_CODE_CHEAT);
            }
        });
        updateQuestion();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG,"onStart() called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called");
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.i(TAG, "onSaveInstanceState");

        ArrayList<Boolean> questionsAnswered = new ArrayList<>();
        for(Question q : mQuestionBank) {
            questionsAnswered.add(q.isAnswered());
        }

        savedInstanceState.putInt(KEY_INDEX, mCurrentIndex);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG,"onStop() called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestroy() called");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != Activity.RESULT_OK){
            return;
        }

        if(requestCode == REQUEST_CODE_CHEAT){
            if(data == null){
                return;
            }
            mIsCheater = CheatActivity.wasAnswerShown(data);
        }
    }

    private void updateQuestion() {

        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);

        boolean x = mQuestionBank[mCurrentIndex].isAnswered();
        if(x){
            disableAnswerButtons();
        } else {
            enableAnswerButtons();
        }

        if(checkAllAnswered()){
            mQuestionTextView.setText("Your score is " + gradeQuiz());
        }
    }

    private void checkAnswer(boolean userPressedTrue) {
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
        int messageResId = 0;

        if(mIsCheater){
            messageResId = R.string.judgement_toast;
        } else {

            if (userPressedTrue == answerIsTrue) {
                messageResId = R.string.correct_toast;
                isAnswerCorrect(true);

            } else {
                messageResId = R.string.incorrect_toast;
                isAnswerCorrect(false);
            }
        }
        disableAnswerButtons();
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show();

        //if(checkAllAnswered()){}
    }

    private void isAnswerCorrect(boolean isCorrectAnswer) {
        mQuestionBank[mCurrentIndex].setAnswered(true);
        mQuestionBank[mCurrentIndex].setCorrect(isCorrectAnswer);
    }

    private void disableAnswerButtons() {
        mTrueButton.setEnabled(false);
        mFalseButton.setEnabled(false);
    }

    private void enableAnswerButtons() {
        mTrueButton.setEnabled(true);
        mFalseButton.setEnabled(true);
    }

    private boolean checkAllAnswered() {
        for(Question q : mQuestionBank){
            if(!q.isAnswered()){
                return false;
            }
        }
        return true;
    }

    private float gradeQuiz() {
        int numRight = 0;
        for(Question q : mQuestionBank) {
            if(q.isCorrect()) {
                numRight++;
            }
        }
        return (float) numRight/mQuestionBank.length;
    }
}
