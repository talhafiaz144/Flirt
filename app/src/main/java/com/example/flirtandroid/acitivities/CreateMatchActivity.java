package com.example.flirtandroid.acitivities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.flirtandroid.R;
import com.example.flirtandroid.SnackBar.WarningCustomSnackbar;
import com.example.flirtandroid.Utils.PickupLine;
import com.example.flirtandroid.Utils.ProjectUtils;
import com.example.flirtandroid.acitivities.extras.Upload_AddInterestActivity;
import com.example.flirtandroid.fragments.ProgressDialogFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class CreateMatchActivity extends AppCompatActivity {

    private EditText enterName;
    private static final int REQUEST_IMAGE_PICKER = 1;
    private static final String PROGRESS_DIALOG = "CreateMatchActivity.java";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_match);

        enterName = findViewById(R.id.first_name);
        LinearLayout uploadBtn = findViewById(R.id.upload_btn);
        ImageView backBtn = findViewById(R.id.back_btn);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateMatchActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enteredText = enterName.getText().toString().trim();
                if (!enteredText.isEmpty()) {
                    showProgress();
                    // Open gallery or file picker to select an image
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, REQUEST_IMAGE_PICKER);
                    ProjectUtils.googleAnalyticEvent(CreateMatchActivity.this, "upload_screenShot");
                } else {
                    WarningCustomSnackbar.showSnackbar(CreateMatchActivity.this, "Name is required");
                }
            }
        });

        LinearLayout addManually = findViewById(R.id.add_manual_btn2);
        addManually.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enteredText = enterName.getText().toString().trim();
                if (!enteredText.isEmpty()) {
                    Intent intent = new Intent(CreateMatchActivity.this, AddInterestActivity.class);
                    intent.putExtra("text", enteredText);
                    startActivity(intent);
                    ProjectUtils.googleAnalyticEvent(CreateMatchActivity.this, "upload_manual");
                } else {
                    WarningCustomSnackbar.showSnackbar(CreateMatchActivity.this, "Name is required");
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_PICKER && resultCode == RESULT_OK) {
            if (data != null) {
                Uri imageUri = data.getData();
                processImage(imageUri);
            }
        }else if (resultCode == RESULT_CANCELED) {
            // User canceled the operation, hide the progress bar
            dismissProgress();
        }
    }

    private void processImage(Uri imageUri) {
        try {
            FirebaseVisionImage image = FirebaseVisionImage.fromFilePath(CreateMatchActivity.this, imageUri);
            FirebaseVisionTextRecognizer recognizer = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
            recognizer.processImage(image)
                    .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                        @Override
                        public void onSuccess(FirebaseVisionText firebaseVisionText) {
                            dismissProgress();
                            List<String> words = extractWords(firebaseVisionText);
                            // Create a new PickupLine object and set the extracted words
                            PickupLine pickupLine = new PickupLine();
                            if (words.size() > 10) {
                                words = words.subList(0, 10); // Select only the first 10 words
                            }
                            pickupLine.setWords(words);
                            // Print the extracted words
                            for (String word : pickupLine.getWords()) {
                                Log.e("TAG", "Extracted Word: " + word);
                            }
                            String enteredText = enterName.getText().toString().trim();
                            if (!enteredText.isEmpty()) {
                                Intent intent = new Intent(CreateMatchActivity.this, Upload_AddInterestActivity.class);
                                intent.putExtra("text", enteredText);
                                Gson gson = new Gson();
                                String pickupLineJson = gson.toJson(pickupLine);
                                intent.putExtra("pickupLine", pickupLineJson);
                                startActivity(intent);
                            } else {
                                WarningCustomSnackbar.showSnackbar(CreateMatchActivity.this, "Name is required");
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(Exception e) {
                            dismissProgress();
                            WarningCustomSnackbar.showSnackbar(CreateMatchActivity.this, "Text extraction failed");
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<String> extractWords(FirebaseVisionText firebaseVisionText) {
        List<String> words = new ArrayList<>();
        List<FirebaseVisionText.TextBlock> textBlocks = firebaseVisionText.getTextBlocks();
        if (textBlocks != null) {
            for (FirebaseVisionText.TextBlock textBlock : textBlocks) {
                List<FirebaseVisionText.Line> lines = textBlock.getLines();
                if (lines != null) {
                    for (FirebaseVisionText.Line line : lines) {
                        List<FirebaseVisionText.Element> elements = line.getElements();
                        if (elements != null) {
                            for (FirebaseVisionText.Element element : elements) {
                                String word = element.getText();
                                words.add(word);
                            }
                        }
                    }
                }
            }
        }
        return words;
    }

    public void showProgress() {
        ProgressDialogFragment f = ProgressDialogFragment.getInstance();
        getSupportFragmentManager().beginTransaction().add(f, PROGRESS_DIALOG).commitAllowingStateLoss();
    }

    public void dismissProgress() {
        FragmentManager manager = getSupportFragmentManager();
        if (manager == null) return;
        ProgressDialogFragment f = (ProgressDialogFragment) manager.findFragmentByTag(PROGRESS_DIALOG);
        if (f != null) {
            getSupportFragmentManager().beginTransaction().remove(f).commitAllowingStateLoss();
        }
    }

    @Override
    public void onBackPressed() {
        // Start new activity here
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
