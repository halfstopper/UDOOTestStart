package xbee.udootest;

import android.app.Activity;
import android.content.Context;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import me.palazzetti.adktoolkit.AdkManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import weka.classifiers.Classifier;
import weka.classifiers.lazy.IBk;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.CSVSaver;
import wlsvm.WLSVM;

import static android.R.attr.onClick;

//Update 3
public class MainActivity extends Activity {

// private static final String TAG = "UDOO_AndroidADKFULL";

    private AdkManager mAdkManager;
    private Classifier trial;
    private ToggleButton buttonLED;
    private TextView distance;
    private TextView pulse;
    private TextView position;
    private AdkReadTask mAdkReadTask;
    private TextView resulttext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        File root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File f = new File(root, "training.arff");
        BufferedReader inputReader;
        inputReader = readFile(f);

        mAdkManager = new AdkManager((UsbManager) getSystemService(Context.USB_SERVICE));
//  register a BroadcastReceiver to catch UsbManager.ACTION_USB_ACCESSORY_DETACHED action
        registerReceiver(mAdkManager.getUsbReceiver(), mAdkManager.getDetachedFilter());
        resulttext =(TextView) findViewById(R.id.result);
        buttonLED = (ToggleButton) findViewById(R.id.toggleButtonLED);
        distance = (TextView) findViewById(R.id.textView_distance);
        pulse = (TextView) findViewById(R.id.textView_pulse);
        position = (TextView) findViewById(R.id.textView_position);
        try {


            Toast.makeText(getApplicationContext(), "start: " + f, Toast.LENGTH_LONG).show();

            Instances data = new Instances(inputReader);
            data.setClassIndex(data.numAttributes() - 1);
            Classifier ibk = new IBk();
            ibk.buildClassifier(data);
            trial = ibk;
            System.out.println("c");
            Toast.makeText(getApplicationContext(), "start", Toast.LENGTH_LONG).show();

        }
        catch (IOException e) {
            System.out.println(e);
        }
        catch (java.lang.Exception e) {
            System.out.println(e);
        }


    }



    private BufferedReader readFile(File f) {
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(f));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            br = null;
        }
        return br;
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdkManager.open();

        mAdkReadTask = new AdkReadTask();
        mAdkReadTask.execute();
    }

    @Override
    public void onPause() {
        super.onPause();
        mAdkManager.close();

        mAdkReadTask.pause();
        mAdkReadTask = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mAdkManager.getUsbReceiver());
    }

    // ToggleButton method - send message to SAM3X
    public void blinkLED(View v) {
        if (buttonLED.isChecked()) {
            // writeSerial() allows you to write a single char or a String object.
            mAdkManager.writeSerial("1");
        } else {
            mAdkManager.writeSerial("0");
        }
    }

    /*
     * We put the readSerial() method in an AsyncTask to run the
     * continuous read task out of the UI main thread
     */
    private class AdkReadTask extends AsyncTask<Void, String, Void> {

        private boolean running = true;
        @Override
        protected Void doInBackground(Void... arg0) {

            while (running) {
                publishProgress(mAdkManager.readSerial());
                System.out.println(mAdkManager.readSerial());
                SystemClock.sleep(50);
            }
            return null;
        }

        public void pause() {

            running = false;
        }
        //      Log.i("ADK demo bi", "start adkreadtask");
        protected void onProgressUpdate(String... progress) {

            float pulseRate = (int) progress[0].charAt(0);
            float oxygenLvl = (int) progress[0].charAt(1);
            float pos = (int) progress[0].charAt(2);
            int max = 255;
            if (pulseRate > max) pulseRate = max;
            if (oxygenLvl > max) oxygenLvl = max;
            if (pos > max) {pos = max;}
            try{
                Double d = (double) pulseRate;
                Double e = (double) oxygenLvl;
                Double f = (double) pos;
                Attribute Attribute1 = new Attribute("distance");
                Attribute Attribute2 = new Attribute("pulse");
                Attribute Attribute3 = new Attribute("position");
                FastVector fvClassVal = new FastVector(2);
                fvClassVal.addElement("stress");
                fvClassVal.addElement("relax");
                Attribute ClassAttribute = new Attribute("class", fvClassVal);
                FastVector fvWekaAttributes = new FastVector(5);
                fvWekaAttributes.addElement(Attribute1);
                fvWekaAttributes.addElement(Attribute2);
                fvWekaAttributes.addElement(Attribute3);
                fvWekaAttributes.addElement(ClassAttribute);
                Instances testingSet = new Instances("TestingInstance", fvWekaAttributes, 1);
                testingSet.setClassIndex(testingSet.numAttributes() - 1);

                Instance iExample = new Instance(testingSet.numAttributes());

                iExample.setValue((Attribute) fvWekaAttributes.elementAt(0), d);
                iExample.setValue((Attribute) fvWekaAttributes.elementAt(1), e);
                iExample.setValue((Attribute) fvWekaAttributes.elementAt(2), f);
                iExample.setValue((Attribute) fvWekaAttributes.elementAt(3), "stress");

// add the instance.. so
                testingSet.add(iExample);
                try {
                    double ans =trial.classifyInstance(testingSet.instance(0));

                    Toast.makeText(getApplicationContext(), "after classify: " + ans, Toast.LENGTH_LONG).show();

                    if(ans==0.0){resulttext.setText("stress");}
                    else{resulttext.setText("rest");}
                }
                catch (Exception e1) {}
            }
            catch(ClassCastException e){}
//            DecimalFormat df = new DecimalFormat("#.#");
            distance.setText(pulseRate + " (bpm)");
            pulse.setText(oxygenLvl + " (pct)");
            position.setText(pos + "");
        }
    }
}