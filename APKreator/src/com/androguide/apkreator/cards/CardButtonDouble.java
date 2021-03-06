/*
 * *   Copyright (C) 2013  Louis Teboul (a.k.a Androguide)
 *  *
 *  *    admin@pimpmyrom.org  || louisteboul@gmail.com
 *  *    http://pimpmyrom.org || http://androguide.fr
 *  *
 *  *     This program is free software; you can redistribute it and/or modify
 *  *     it under the terms of the GNU General Public License as published by
 *  *     the Free Software Foundation; either version 2 of the License, or
 *  *     (at your option) any later version.
 *  *
 *  *     This program is distributed in the hope that it will be useful,
 *  *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  *     GNU General Public License for more details.
 *  *
 *  *      You should have received a copy of the GNU General Public License along
 *  *      with this program; if not, write to the Free Software Foundation, Inc.,
 *  *      51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *  *
 */

package com.androguide.apkreator.cards;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.androguide.apkreator.R;
import com.androguide.apkreator.helpers.CMDProcessor.CMDProcessor;
import com.fima.cardsui.objects.Card;

public class CardButtonDouble extends Card {

    public CardButtonDouble(String title, String desc, String buttonText1, String buttonText2, String cmd1, String cmd2, ActionBarActivity fa) {
        super(title, desc, buttonText1, buttonText2, cmd1, cmd2, fa);
    }

    @Override
    public View getCardContent(Context context) {
        View v = LayoutInflater.from(context).inflate(R.layout.card_button_double, null);
        SharedPreferences config = fa.getSharedPreferences("CONFIG", 0);

        assert v != null;
        TextView t = (TextView) v.findViewById(R.id.title);
        TextView d = (TextView) v.findViewById(R.id.desc);
        d.setText(desc);
        t.setText(title);
        t.setTextColor(Color.parseColor(config.getString("APP_COLOR", "#96AA39")));
        Button btn1 = (Button) v.findViewById(R.id.button1);
        btn1.setText(buttonText1);
        btn1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        CMDProcessor.runSuCommand(cmd1);
                    }
                }).start();
            }
        });

        Button btn2 = (Button) v.findViewById(R.id.button2);
        btn2.setText(buttonText2);
        btn2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        CMDProcessor.runSuCommand(cmd2);
                    }
                }).start();
            }
        });
        return v;
    }
}

