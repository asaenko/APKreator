/**   Copyright (C) 2013  Louis Teboul (a.k.a Androguide)
 *
 *    admin@pimpmyrom.org  || louisteboul@gmail.com
 *    http://pimpmyrom.org || http://androguide.fr
 *    71 quai Clémenceau, 69300 Caluire-et-Cuire, FRANCE.
 *
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *      You should have received a copy of the GNU General Public License along
 *      with this program; if not, write to the Free Software Foundation, Inc.,
 *      51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 **/

package com.androguide.apkreator.pluggable.parsers;

import android.os.AsyncTask;

import com.androguide.apkreator.pluggable.objects.Config;
import com.androguide.apkreator.pluggable.objects.Tweak;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class PluginParser {
    ArrayList<Tweak> tweaks;
    ArrayList<Config> configs;
    private Tweak tweak;
    private Config config;
    private String text;

    public PluginParser() {
        tweaks = new ArrayList<Tweak>();
        configs = new ArrayList<Config>();
    }

    public List<Tweak> getTweaks() {
        return tweaks;
    }

    /**
     * @param is An InputStream of the XML file we want to open
     * @return a Collection of Tweak Objects, each delimited by <tweak></tweak> tags in the XML
     */
    public List<Tweak> parse(InputStream is) {
        try {
            return new AsyncTweaksParsing().execute(is).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return new ArrayList<Tweak>();
    }

    /**
     * @param is An InputStream of the XML file we want to open
     * @return a Collection of Config Object, each delimited by <tweak></tweak> tags in the XML
     */
    public List<Config> parseConfig(InputStream is) {
        try {
            return new AsyncConfigParsing().execute(is).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return new ArrayList<Config>();
    }

    /**
     * Do the parsing in an AsyncTask in order to avoid blocking the UI thread
     * (parsing is an expensive operation)
     */
    private class AsyncTweaksParsing extends AsyncTask<InputStream, Void, ArrayList<Tweak>> {

        @Override
        protected ArrayList<Tweak> doInBackground(InputStream... streams) {
            XmlPullParserFactory factory;
            XmlPullParser parser;
            try {
                factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                parser = factory.newPullParser();
                parser.setInput(streams[0], null);

                int eventType = parser.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    String tagName = parser.getName();
                    switch (eventType) {

                        // OPENING TAG
                        case XmlPullParser.START_TAG:
                            // <PLUGIN> TAG
                            if (tagName.equalsIgnoreCase("plugin"))
                                tweak = new Tweak();

                                // <CARD> TAG
                            else if (tagName.equalsIgnoreCase("card")) {
                                tweak = new Tweak();
                                tweak.setType(parser.getAttributeValue(null, "type"));

                                if (parser.getAttributeValue(null, "type").equalsIgnoreCase("image"))
                                    tweak.setUrl(parser.getAttributeValue(null, "url"));

                                // <CONTROL> TAG
                            } else if (tagName.equalsIgnoreCase("control")) {
                                tweak.setControl(parser.getAttributeValue(null, "type"));

                                // switch type
                                if (parser.getAttributeValue(null, "type").equalsIgnoreCase("switch")) {
                                    tweak.setBooleanOn(parser.getAttributeValue(null, "on"));
                                    tweak.setBooleanOff(parser.getAttributeValue(null, "off"));

                                    // spinner type
                                } else if (parser.getAttributeValue(null, "type").equalsIgnoreCase("spinner")) {
                                    ArrayList<String> entries = new ArrayList<String>();
                                    ArrayList<String> cmds = new ArrayList<String>();
                                    int values = Integer.parseInt(parser.getAttributeValue(null, "entries-amount"));
                                    for (int i = 0; i < values; i++) {
                                        entries.add(parser.getAttributeValue(null, "entry" + i));
                                        cmds.add(parser.getAttributeValue(null, "command" + i));
                                    }
                                    tweak.setSpinnerEntries(entries);
                                    tweak.setSpinnerCommands(cmds);

                                    // button type
                                } else if (parser.getAttributeValue(null, "type").equalsIgnoreCase("button")) {
                                    tweak.setButtonText(parser.getAttributeValue(null, "text"));

                                    // double-button type
                                } else if (parser.getAttributeValue(null, "type").equalsIgnoreCase("double-button")) {
                                    tweak.setButtonText(parser.getAttributeValue(null, "text1"));
                                    tweak.setButtonText2(parser.getAttributeValue(null, "text2"));
                                }
                            }
                            break;

                        // ENCLOSED TEXT
                        case XmlPullParser.TEXT:
                            text = parser.getText();
                            break;

                        // CLOSING TAG
                        case XmlPullParser.END_TAG:
                            if (tagName.equalsIgnoreCase("card"))
                                tweaks.add(tweak);
                            else if (tagName.equalsIgnoreCase("name"))
                                tweak.setName(text);
                            else if (tagName.equalsIgnoreCase("description"))
                                tweak.setDesc(text);
                            else if (tagName.equalsIgnoreCase("unit"))
                                tweak.setUnit(text);
                            else if (tagName.equalsIgnoreCase("prop"))
                                tweak.setProp(text);
                            else if (tagName.equalsIgnoreCase("url"))
                                tweak.setUrl(text);
                            else if (tagName.equalsIgnoreCase("path"))
                                tweak.setFilePath(text);
                            else if (tagName.equalsIgnoreCase("button"))
                                tweak.setButtonText(text);
                            else if (tagName.equalsIgnoreCase("command"))
                                tweak.setShellCmd(text);
                            else if (tagName.equalsIgnoreCase("command1"))
                                tweak.setShellCmd(text);
                            else if (tagName.equalsIgnoreCase("command2"))
                                tweak.setShellCmd2(text);
                            else if (tagName.equalsIgnoreCase("min-value"))
                                tweak.setMin(Integer.parseInt(text));
                            else if (tagName.equalsIgnoreCase("max-value"))
                                tweak.setMax(Integer.parseInt(text));
                            else if (tagName.equalsIgnoreCase("default-value"))
                                tweak.setDef(Integer.parseInt(text));
                            break;
                        default:
                            break;
                    }
                    eventType = parser.next();
                }

            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return tweaks;
        }

        protected void onPostExecute(ArrayList<Tweak> result) {
            tweaks = result;
        }

    }

    /**
     * Do the parsing in an AsyncTask in order to avoid blocking the UI thread (parsing is an expensive operation)
     */
    private class AsyncConfigParsing extends AsyncTask<InputStream, Void, ArrayList<Config>> {

        protected ArrayList<Config> doInBackground(InputStream... streams) {
            XmlPullParserFactory factory;
            XmlPullParser parser;
            try {
                factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                parser = factory.newPullParser();

                parser.setInput(streams[0], null);

                int eventType = parser.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    String tagName = parser.getName();
                    switch (eventType) {

                        // OPENING TAG
                        case XmlPullParser.START_TAG:
                            if (tagName.equalsIgnoreCase("config")) {
                                config = new Config();
                                config.setAppName(parser.getAttributeValue(null, "app-name"));
                                config.setAppColor(parser.getAttributeValue(null, "app-color"));
                                config.setWebsite(parser.getAttributeValue(null, "website"));
                                config.setXda(parser.getAttributeValue(null, "xda"));
                                config.setTwitter(parser.getAttributeValue(null, "twitter"));
                                config.setGplus(parser.getAttributeValue(null, "g-plus"));
                                config.setFacebook(parser.getAttributeValue(null, "facebook"));
                                config.setTabsAmount(Integer.parseInt(parser.getAttributeValue(null, "tabs-amount")));
                                ArrayList<String> tabs = new ArrayList<String>();
                                for (int i = 0; i < config.getTabsAmount(); i++)
                                    tabs.add(i, parser.getAttributeValue(null, "tab" + i));
                                config.setTabs(tabs);
                            } else if (tagName.equalsIgnoreCase("cpu-control"))
                                config.setCpuControlPos(Integer.parseInt(parser.getAttributeValue(null, "position")));
                            break;

                        // ENCLOSED TEXT
                        case XmlPullParser.TEXT:
                            text = parser.getText();
                            break;

                        // CLOSING TAG
                        case XmlPullParser.END_TAG:
                            if (text != null) {
                                if (tagName.equalsIgnoreCase("config"))
                                    configs.add(config);
                                break;
                            }

                        default:
                            break;
                    }
                    eventType = parser.next();
                }

            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return configs;
        }

        protected void onPostExecute(ArrayList<Config> result) {
            configs = result;
        }
    }
}