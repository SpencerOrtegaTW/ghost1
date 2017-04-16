/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.ghost;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;


public class GhostActivity extends AppCompatActivity {
    private static final String COMPUTER_TURN = "Computer's turn";
    private static final String USER_TURN = "Your turn";
    private GhostDictionary dictionary;
    private boolean userTurn = false;
    private Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ghost);
        AssetManager assetManager = getAssets();
        try {
            dictionary = new SimpleDictionary(assetManager.open("words.txt"));
        }
        catch(IOException e){
            System.exit(-1);
        }


        onStart(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ghost, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Handler for the "Reset" button.
     * Randomly determines whether the game starts with a user turn or a computer turn.
     * @param view
     * @return true
     */
    public boolean onStart(View view) {
        // reset
        challengeBtnOnOff(1);
        userTurn = random.nextBoolean();
        TextView text = (TextView) findViewById(R.id.ghostText);
        text.setText("");
        TextView label = (TextView) findViewById(R.id.gameStatus);
        if (userTurn) {
            label.setText(USER_TURN);
        } else {
            label.setText(COMPUTER_TURN);
            computerTurn();
        }
        return true;
    }
    public void challengeBtnOnOff(int action){
        Button challengeBtn = (Button) findViewById(R.id.challengeBtn);
        if(action == 1){
            challengeBtn.setVisibility(View.VISIBLE);
        }
        else {
            challengeBtn.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Handler for the "Challenge" button.
     * Randomly determines whether the game starts with a user turn or a computer turn.
     * @return true
     */
    public boolean challenge(View view){
        TextView text = (TextView) findViewById(R.id.ghostText);
        TextView label = (TextView) findViewById(R.id.gameStatus);

        // get fragment from board
        String fragment = text.getText().toString();
        // check size
        if(fragment.length() >= 4) {
            // turn off challenge button
            challengeBtnOnOff(0);

            // store result of challenge (WIN,LOSE)
            String result = "";

            // fetch results
            boolean fragmentIsWord = dictionary.isWord(fragment);
            String possibleWord = dictionary.getAnyWordStartingWith(fragment);

            // User has won
            if (fragmentIsWord || possibleWord.equals("")) {
                // opponent has completed word
                //  OR
                // impossible to create word
                result = "WON!";
            }
            else {
                // fragment doesnt form valid word
                //  BUT
                // there is possible word candidate
                result = "LOST!";
            }

            // output results
            label.setText("User has challenged, User has " + result);
        }
        return true;
    }

    private void computerTurn() {
        TextView label = (TextView) findViewById(R.id.gameStatus);
        TextView text = (TextView) findViewById(R.id.ghostText);

        // get ghost frag
        String fragment = text.getText().toString();

        // fetch results if computer can challenge and win!
        boolean fragmentIsWord = dictionary.isWord(fragment);
        String possible = dictionary.getAnyWordStartingWith(fragment);

        // computer wins
        if((fragmentIsWord && fragment.length()>= 4)   || possible.equals("")){
            // if user has completed a word and size >= 4
            //  OR
            // no possible words could be made
            challengeBtnOnOff(0);
            label.setText("Computer has challenged, Computer has WON");
            return;
        }
        else{
            // get substring of possible word
            String newFrag = possible.substring(0,fragment.length()+1);

            // output
            text.setText(newFrag);
        }

        userTurn = true;
        label.setText(USER_TURN);
    }

    /**
     * Handler for user key presses.
     * @param keyCode
     * @param event
     * @return whether the key stroke was handled.
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        // valid key from a-z has been entered
        if(keyCode >= KeyEvent.KEYCODE_A && keyCode <= KeyEvent.KEYCODE_Z) {
            TextView fragment = (TextView) findViewById(R.id.ghostText);

            // get current fragment
            String getText = fragment.getText().toString();

            // add valid key to fragment
            getText += (char) event.getUnicodeChar();

            // print new fragment
            fragment.setText(getText);

            // end of user turn
            userTurn = false;
            computerTurn();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }
}
