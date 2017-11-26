/*
package xbee.udootest;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import weka.classifiers.Classifier;
import weka.classifiers.lazy.IBk;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import wlsvm.WLSVM;

public class NextActivity extends AppCompatActivity {

    private Button trainBtn;
    private Button classifyBtn;
    private EditText slValue, swValue, plValue, pwValue;
    File svmModel;

    WLSVM svmCls = new WLSVM();
    WLSVM svmCls = (WLSVM) weka.core.SerializationHelper.read(svmModel);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);

        trainBtn = (Button) findViewById(R.id.train);
        classifyBtn = (Button) findViewById(R.id.classify);

        slValue = (EditText) findViewById(R.id.slValue);
        swValue = (EditText) findViewById(R.id.swValue);
        plValue = (EditText) findViewById(R.id.plValue);
        pwValue = (EditText) findViewById(R.id.pwValue);

        slValue.setText("4.7");
        swValue.setText("3.2");
        plValue.setText("1.6");
        pwValue.setText("0.2");

        File root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File f = new File(root, "iris_train.arff");
        BufferedReader inputReader;
        inputReader = readFile(f);

        //Build the KNN model
        Instances data = null;
        try {
            data = new Instances(inputReader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        data.setClassIndex(data.numAttributes() - 1);

        Classifier ibk = new IBk();
        try {
            ibk.buildClassifier(data);
        } catch (Exception e) {
            e.printStackTrace();

        }

        // Declare four numeric attributes
        Attribute Attribute1 = new Attribute("sepallength");
        Attribute Attribute2 = new Attribute("sepalwidth");
        Attribute Attribute3 = new Attribute("petallength");
        Attribute Attribute4 = new Attribute("petalwidth");

        // Declare the class attribute along with its values (nominal)
        FastVector fvClassVal = new FastVector(3);
        fvClassVal.addElement("Iris-setosa");
        fvClassVal.addElement("Iris-versicolor");
        fvClassVal.addElement("Iris-virginica");
        Attribute ClassAttribute = new Attribute("class", fvClassVal);

        // Declare the feature vector template
        FastVector fvWekaAttributes = new FastVector(5);
        fvWekaAttributes.addElement(Attribute1);
        fvWekaAttributes.addElement(Attribute2);
        fvWekaAttributes.addElement(Attribute3);
        fvWekaAttributes.addElement(Attribute4);
        fvWekaAttributes.addElement(ClassAttribute);

        // Creating testing instances object with name "TestingInstance"
        // using the feature vector template we declared above
        // and with initial capacity of 1

        Instances testingSet = new Instances("TestingInstance", fvWekaAttributes, 1);

// Setting the column containing class labels:
        testingSet.setClassIndex(testingSet.numAttributes() - 1);

// Create and fill an instance, and add it to the testingSet
        Instance iExample = new Instance(testingSet.numAttributes());


        iExample.setValue((Attribute)fvWekaAttributes.elementAt(0), slValue);
        iExample.setValue((Attribute)fvWekaAttributes.elementAt(1), swValue);
        iExample.setValue((Attribute)fvWekaAttributes.elementAt(2), plValue);
        iExample.setValue((Attribute)fvWekaAttributes.elementAt(3), pwValue);
        iExample.setValue((Attribute)fvWekaAttributes.elementAt(4),
                "Iris-setosa"); // dummy

// add the instance
        testingSet.add(iExample);

        // add the instance
        testingSet.add(iExample);


    }

    private BufferedReader readFile(File f) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(f));
        } catch (FileNotFoundException ex) {
            System.out.println("File Not Found");
        }
        return br;
    }
}
}*/
