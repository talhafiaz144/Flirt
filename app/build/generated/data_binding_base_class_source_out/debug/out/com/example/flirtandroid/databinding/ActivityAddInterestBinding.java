// Generated by view binder compiler. Do not edit!
package com.example.flirtandroid.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.flirtandroid.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityAddInterestBinding implements ViewBinding {
  @NonNull
  private final ScrollView rootView;

  @NonNull
  public final ImageView addBtn;

  @NonNull
  public final ImageView addBtn2;

  @NonNull
  public final ImageView addInterestIcon;

  @NonNull
  public final ImageView backBtn;

  @NonNull
  public final TextView createMatch;

  @NonNull
  public final TextView emailEditText;

  @NonNull
  public final Button generateBtn;

  @NonNull
  public final LinearLayout interestEditText;

  @NonNull
  public final LinearLayout interestEditText2;

  @NonNull
  public final ConstraintLayout mainContent;

  @NonNull
  public final ScrollView mainScrollview;

  @NonNull
  public final TextView personsName;

  @NonNull
  public final RecyclerView recyclerView;

  @NonNull
  public final Toolbar toolbar;

  @NonNull
  public final EditText writeText;

  @NonNull
  public final EditText writeText2;

  private ActivityAddInterestBinding(@NonNull ScrollView rootView, @NonNull ImageView addBtn,
      @NonNull ImageView addBtn2, @NonNull ImageView addInterestIcon, @NonNull ImageView backBtn,
      @NonNull TextView createMatch, @NonNull TextView emailEditText, @NonNull Button generateBtn,
      @NonNull LinearLayout interestEditText, @NonNull LinearLayout interestEditText2,
      @NonNull ConstraintLayout mainContent, @NonNull ScrollView mainScrollview,
      @NonNull TextView personsName, @NonNull RecyclerView recyclerView, @NonNull Toolbar toolbar,
      @NonNull EditText writeText, @NonNull EditText writeText2) {
    this.rootView = rootView;
    this.addBtn = addBtn;
    this.addBtn2 = addBtn2;
    this.addInterestIcon = addInterestIcon;
    this.backBtn = backBtn;
    this.createMatch = createMatch;
    this.emailEditText = emailEditText;
    this.generateBtn = generateBtn;
    this.interestEditText = interestEditText;
    this.interestEditText2 = interestEditText2;
    this.mainContent = mainContent;
    this.mainScrollview = mainScrollview;
    this.personsName = personsName;
    this.recyclerView = recyclerView;
    this.toolbar = toolbar;
    this.writeText = writeText;
    this.writeText2 = writeText2;
  }

  @Override
  @NonNull
  public ScrollView getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityAddInterestBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityAddInterestBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_add_interest, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityAddInterestBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.addBtn;
      ImageView addBtn = ViewBindings.findChildViewById(rootView, id);
      if (addBtn == null) {
        break missingId;
      }

      id = R.id.addBtn2;
      ImageView addBtn2 = ViewBindings.findChildViewById(rootView, id);
      if (addBtn2 == null) {
        break missingId;
      }

      id = R.id.add_interest_icon;
      ImageView addInterestIcon = ViewBindings.findChildViewById(rootView, id);
      if (addInterestIcon == null) {
        break missingId;
      }

      id = R.id.back_btn;
      ImageView backBtn = ViewBindings.findChildViewById(rootView, id);
      if (backBtn == null) {
        break missingId;
      }

      id = R.id.create_match;
      TextView createMatch = ViewBindings.findChildViewById(rootView, id);
      if (createMatch == null) {
        break missingId;
      }

      id = R.id.emailEditText;
      TextView emailEditText = ViewBindings.findChildViewById(rootView, id);
      if (emailEditText == null) {
        break missingId;
      }

      id = R.id.generate_btn;
      Button generateBtn = ViewBindings.findChildViewById(rootView, id);
      if (generateBtn == null) {
        break missingId;
      }

      id = R.id.interest_editText;
      LinearLayout interestEditText = ViewBindings.findChildViewById(rootView, id);
      if (interestEditText == null) {
        break missingId;
      }

      id = R.id.interest_editText2;
      LinearLayout interestEditText2 = ViewBindings.findChildViewById(rootView, id);
      if (interestEditText2 == null) {
        break missingId;
      }

      id = R.id.main_content;
      ConstraintLayout mainContent = ViewBindings.findChildViewById(rootView, id);
      if (mainContent == null) {
        break missingId;
      }

      ScrollView mainScrollview = (ScrollView) rootView;

      id = R.id.persons_name;
      TextView personsName = ViewBindings.findChildViewById(rootView, id);
      if (personsName == null) {
        break missingId;
      }

      id = R.id.recycler_view;
      RecyclerView recyclerView = ViewBindings.findChildViewById(rootView, id);
      if (recyclerView == null) {
        break missingId;
      }

      id = R.id.toolbar;
      Toolbar toolbar = ViewBindings.findChildViewById(rootView, id);
      if (toolbar == null) {
        break missingId;
      }

      id = R.id.write_text;
      EditText writeText = ViewBindings.findChildViewById(rootView, id);
      if (writeText == null) {
        break missingId;
      }

      id = R.id.write_text2;
      EditText writeText2 = ViewBindings.findChildViewById(rootView, id);
      if (writeText2 == null) {
        break missingId;
      }

      return new ActivityAddInterestBinding((ScrollView) rootView, addBtn, addBtn2, addInterestIcon,
          backBtn, createMatch, emailEditText, generateBtn, interestEditText, interestEditText2,
          mainContent, mainScrollview, personsName, recyclerView, toolbar, writeText, writeText2);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
