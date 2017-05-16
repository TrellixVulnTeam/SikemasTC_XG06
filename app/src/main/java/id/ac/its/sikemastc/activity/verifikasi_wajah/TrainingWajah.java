package id.ac.its.sikemastc.activity.verifikasi_wajah;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;

import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import ch.zhaw.facerecognitionlibrary.Helpers.FileHelper;
import ch.zhaw.facerecognitionlibrary.Helpers.MatName;
import ch.zhaw.facerecognitionlibrary.PreProcessor.PreProcessorFactory;
import ch.zhaw.facerecognitionlibrary.Recognition.Recognition;
import ch.zhaw.facerecognitionlibrary.Recognition.RecognitionFactory;
import id.ac.its.sikemastc.R;
import id.ac.its.sikemastc.utilities.LibraryPreference;

import static org.opencv.imgcodecs.Imgcodecs.imread;

public class TrainingWajah extends Activity {

    private static final String TAG = "Training";

    public LibraryPreference preferences;
    TextView progress;
    Thread thread;

    static {
        if (!OpenCVLoader.initDebug()) {
            // Handle initialization error
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_training);
        progress = (TextView) findViewById(R.id.tv_training_progress);
        progress.setMovementMethod(new ScrollingMovementMethod());

        preferences = new LibraryPreference(this);
        preferences.getCameraSettings();
//        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        final Handler handler = new Handler(Looper.getMainLooper());
        thread = new Thread(new Runnable() {
            public void run() {
                if (!Thread.currentThread().isInterrupted()) {
                    Log.d("cek masuk handler", "masuk handler");
                    PreProcessorFactory ppF = new PreProcessorFactory(getApplicationContext());
//                    PreferencesHelper preferenceHelper = new PreferencesHelper(getApplicationContext());
                    HashMap<String, String> trainingSettings = preferences.getCameraSettings();
                    String algorithm = String.valueOf(trainingSettings.get(LibraryPreference.KEY_CLASSIFICATION_METHOD));
//                    String algorithm = preferenceHelper.getClassificationMethod();

//                    AssetManager assetManager = getAssets();
//                    OutputStream outputStream = null;
                    FileHelper fileHelper = new FileHelper();
                    fileHelper.createDataFolderIfNotExsiting();
                    final File[] persons = fileHelper.getTrainingList();
//                    final String[] assetFiles = assetManager.list("DataSetWajah");
//                    Log.d("jumlah file", String.valueOf(assetFiles.length));
                    Log.d("jumlah file person", String.valueOf(persons.length));
//                    int counter = 1;
//                    if (persons.length > 0 && assetFiles.length > 0)
                    if(persons.length > 0) {
                        Recognition rec = RecognitionFactory.getRecognitionAlgorithm(getApplicationContext(),
                                Recognition.TRAINING, algorithm);
//                            for (int i = 0; i < assetFiles.length; i++) {
//                                InputStream inDataSet = assetManager.open(assetFiles[i]);
//                                final File outputFile = new File(assetFiles[i]);
//                                outputStream = new FileOutputStream(outputFile);
//                                int read = 0;
//                                byte[] bytes = new byte[1024];
//                                while ((read = inDataSet.read(bytes)) != -1) {
//                                    outputStream.write(bytes, 0, read);
//                                }
//                                if (FileHelper.isFileAnImage(outputFile)) {
//                                    Mat imgRgb = imread(outputFile.getAbsolutePath());
//                                    Imgproc.cvtColor(imgRgb, imgRgb, Imgproc.COLOR_BGRA2RGBA);
//                                    Mat processedImage = new Mat();
//                                    imgRgb.copyTo(processedImage);
//                                    List<Mat> images = ppF.getProcessedImage(processedImage, PreProcessorFactory.PreprocessingMode.RECOGNITION);
//                                    if (images == null || images.size() > 1) {
//                                        // More than 1 face detected --> cannot use this file for training
//                                        continue;
//                                    } else {
//                                        processedImage = images.get(0);
//                                    }
//                                    if (processedImage.empty()) {
//                                        continue;
//                                    }
//                                    // The last token is the name --> Folder name = Person name
////                                    String[] tokens = file.getParent().split("/");
////                                    final String name = tokens[tokens.length - 1];
//
//                                    MatName m = new MatName("processedImage", processedImage);
//                                    fileHelper.saveMatToImage(m, FileHelper.DATA_PATH);
//
//                                    rec.addImage(processedImage, outputFile.getParent(), false);
//
//                                    // Update screen to show the progress
//                                    final int counterPost = counter;
//                                    final int filesLength = assetFiles.length;
//                                    progress.post(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            progress.append("Image " + counterPost + " of " + filesLength + " from " + outputFile.getParent() + " imported.\n");
//                                        }
//                                    });
//                                    counter++;
//
//                                }
//                            }
                        for (File person : persons) {
                            if (person.isDirectory()) {
                                File[] files = person.listFiles();
                                 int counter = 1;
                                for (File file : files) {
                                    if (FileHelper.isFileAnImage(file)) {
                                        Mat imgRgb = imread(file.getAbsolutePath());
                                        Log.d("file absolut path", file.getAbsolutePath());
                                        Imgproc.cvtColor(imgRgb, imgRgb, Imgproc.COLOR_BGRA2RGBA);
                                        Mat processedImage = new Mat();
                                        imgRgb.copyTo(processedImage);
                                        List<Mat> images = ppF.getProcessedImage(processedImage,
                                                PreProcessorFactory.PreprocessingMode.RECOGNITION);
                                        if (images == null || images.size() > 1) {
                                            // More than 1 face detected --> cannot use this file for training
                                            continue;
                                        } else {
                                            processedImage = images.get(0);
                                        }
                                        if (processedImage.empty()) {
                                            continue;
                                        }
                                        // The last token is the name --> Folder name = Person name
                                        String[] tokens = file.getParent().split("/");
                                        final String name = tokens[tokens.length - 1];
                                        Log.d("tokens", Arrays.toString(tokens));
                                        Log.d("name", name);

                                        MatName m = new MatName("processedImage", processedImage);
                                        fileHelper.saveMatToImage(m, FileHelper.DATA_PATH);

                                        rec.addImage(processedImage, name, false);

                                        // Update screen to show the progress
                                        final int counterPost = counter;
                                        final int filesLength = files.length;
                                        progress.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                progress.append("Image " + counterPost + " of "
                                                        + filesLength + " from " + name + " imported.\n");
                                            }
                                        });

                                        counter++;
                                    }
                                }
                            }
                        }
                        final Intent intent = new Intent(getApplicationContext(), KelolaDataSetWajah.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        if (rec.train()) {
                            intent.putExtra("training", "Training successful");
                        } else {
                            intent.putExtra("training", "Training failed");
                        }
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(intent);
                            }
                        });
                    } else {
                        Thread.currentThread().interrupt();
                    }
//                    finally {
//                        if (outputStream != null) {
//                            try {
//                                // outputStream.flush();
//                                outputStream.close();
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
                }
            }
        });
        thread.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        thread.interrupt();
    }

    @Override
    protected void onStop() {
        super.onStop();
        thread.interrupt();
    }
}
