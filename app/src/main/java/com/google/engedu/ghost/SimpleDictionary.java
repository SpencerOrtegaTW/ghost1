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

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

public class SimpleDictionary implements GhostDictionary {
    private ArrayList<String> words;

    public SimpleDictionary(InputStream wordListStream) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(wordListStream));
        words = new ArrayList<>();
        String line = null;
        while((line = in.readLine()) != null) {
            String word = line.trim();
            if (word.length() >= MIN_WORD_LENGTH)
              words.add(line.trim());
        }
    }

    @Override
    public boolean isWord(String word) {
        return words.contains(word);
    }

    @Override
    public String getAnyWordStartingWith(String prefix) {

        if(prefix.isEmpty()){
            // random number between 0 and words.length-1
            // return element at random index
            Random rand = new Random();
            int  n = rand.nextInt(words.size()-1);
            return words.get(n);
        }

        int l = 0, r = words.size()-1;
        boolean userFirst = (prefix.length() % 2 == 0 ? false:true);
        Log.v("before binary search", "prefix - " + prefix);
        while(r >= l){
            // get mid
            int mid = (l+r)/2;
            String word = words.get(mid);

            // found word at mid
            if(word.startsWith(prefix)){
                Log.v("word found", word);
                return word;
            }

            //
            if(prefix.compareTo(word) < 1){ // prefix <= word
                r = mid-1;
            }
            else{
                l = mid+1;
            }
        }
        Log.v("after binary search:", "no matches");

        return "";
    }

    @Override
    public String getGoodWordStartingWith(String prefix`) {
        if(prefix.isEmpty()){
            // random number between 0 and words.length-1
            // return element at random index
            Random rand = new Random();
            int  n = rand.nextInt(words.size()-1);
            return words.get(n);
        }

        int l = 0, r = words.size()-1;
        boolean userFirst = (prefix.length() % 2 == 0 ? false:true);

        Log.v("before binary search", "prefix - " + prefix);
        while(r >= l){
            // get mid
            int mid = (l+r)/2;
            String word = words.get(mid);

            // found word at mid
            if(word.startsWith(prefix)){
                // get new l and r and mid
                // search for best fit
                String bestWord = word;
                while(words.get(--mid).startsWith(prefix)){}
                while(words.get(++mid).startsWith(prefix)){
                    // current word
                    String current = words.get(mid);
                    // is current word even?
                    int mod = current.length() % 2;
                    // user went first
                    // odd number
                    if(userFirst){
                        if(mod != 0 && current.length() >= bestWord.length()){
                            bestWord = current;
                        }
                    }
                    // computer went first
                    // even number
                    else{
                        if(mod == 0 && current.length() >= bestWord.length()){
                            bestWord = current;
                        }
                    }
                }
                return bestWord;
            }

            //
            if(prefix.compareTo(word) < 1){ // prefix <= word
                r = mid-1;
            }
            else{
                l = mid+1;
            }
        }
        Log.v("after binary search:", "no matches");

        return "";
    }
}
