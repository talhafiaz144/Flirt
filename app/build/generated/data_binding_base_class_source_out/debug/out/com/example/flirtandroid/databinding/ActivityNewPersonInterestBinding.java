// Generated by view binder compiler. Do not edit!
package com.example.flirtandroid.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.flirtandroid.R;
import com.google.android.material.chip.ChipGroup;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityNewPersonInterestBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final ImageView backBtn;

  @NonNull
  public final ChipGroup chipGroup;

  @NonNull
  public final LinearLayout copyTxt;

  @NonNull
  public final TextView createMatch;

  @NonNull
  public final LinearLayout favIcon;

  @NonNull
  public final ImageView favImg;

  @NonNull
  public final TextView feedbackBtn;

  @NonNull
  public final TextView firstInter;

  @NonNull
  public final LinearLayout parentLayout;

  @NonNull
  public final TextView presonsName;

  @NonNull
  public final Button regenerateBtn;

  @NonNull
  public final CardView tCardView;

  @NonNull
  public final TextView thomasJPeCopy;

  @NonNull
  public final Toolbar toolbar;

  private ActivityNewPersonInterestBinding(@NonNull ConstraintLayout rootView,
      @NonNull ImageView backBtn, @NonNull ChipGroup chipGroup, @NonNull LinearLayout copyTxt,
      @NonNull TextView createMatch, @NonNull LinearLayout favIcon, @NonNull ImageView favImg,
      @NonNull TextView feedbackBtn, @NonNull TextView firstInter,
      @NonNull LinearLayout parentLayout, @NonNull TextView presonsName,
      @NonNull Button regenerateBtn, @NonNull CardView tCardView, @NonNull TextView thomasJPeCopy,
      @NonNull Toolbar toolbar) {
    this.rootView = rootView;
    this.backBtn = backBtn;
    this.chipGroup = chipGroup;
    this.copyTxt = copyTxt;
    this.createMatch = createMatch;
    this.favIcon = favIcon;
    this.favImg = favImg;
    this.feedbackBtn = feedbackBtn;
    this.firstInter = firstInter;
    this.parentLayout = parentLayout;
    this.presonsName = presonsName;
    this.regenerateBtn = regenerateBtn;
    this.tCardView = tCardView;
    this.thomasJPeCopy = thomasJPeCopy;
    this.toolbar = toolbar;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityNewPersonInterestBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityNewPersonInterestBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_new_person_interest, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityNewPersonInterestBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.back_btn;
      ImageView backBtn = ViewBindings.findChildViewById(rootView, id);
      if (backBtn == null) {
        break missingId;
      }

      id = R.id.chip_group;
      ChipGroup chipGroup = ViewBindings.findChildViewById(rootView, id);
      if (chipGroup == null) {
        break missingId;
      }

      id = R.id.copy_txt;
      LinearLayout copyTxt = ViewBindings.findChildViewById(rootView, id);
      if (copyTxt == null) {
        break missingId;
      }

      id = R.id.create_match;
      TextView createMatch = ViewBindings.findChildViewById(rootView, id);
      if (createMatch == null) {
        break missingId;
      }

      id = R.id.fav_icon;
      LinearLayout favIcon = ViewBindings.findChildViewById(rootView, id);
      if (favIcon == null) {
        break missingId;
      }

      id = R.id.fav_img;
      ImageView favImg = ViewBindings.findChildViewById(rootView, id);
      if (favImg == null) {
        break missingId;
      }

      id = R.id.feedback_btn;
      TextView feedbackBtn = ViewBindings.findChildViewById(rootView, id);
      if (feedbackBtn == null) {
        break missingId;
      }

      id = R.id.first_inter;
      TextView firstInter = ViewBindings.findChildViewById(rootView, id);
      if (firstInter == null) {
        break missingId;
      }

      id = R.id.parent_layout;
      LinearLayout parentLayout = ViewBindings.findChildViewById(rootView, id);
      if (parentLayout == null) {
        break missingId;
      }

      id = R.id.presons_name;
      TextView presonsName = ViewBindings.findChildViewById(rootView, id);
      if (presonsName == null) {
        break missingId;
      }

      id = R.id.regenerate_btn;
      Button regenerateBtn = ViewBindings.findChildViewById(rootView, id);
      if (regenerateBtn == null) {
        break missingId;
      }

      id = R.id.t_card_view;
      CardView tCardView = ViewBindings.findChildViewById(rootView, id);
      if (tCardView == null) {
        break missingId;
      }

      id = R.id.thomas_j_pe_copy;
      TextView thomasJPeCopy = ViewBindings.findChildViewById(rootView, id);
      if (thomasJPeCopy == null) {
        break missingId;
      }

      id = R.id.toolbar;
      Toolbar toolbar = ViewBindings.findChildViewById(rootView, id);
      if (toolbar == null) {
        break missingId;
      }

      return new ActivityNewPersonInterestBinding((ConstraintLayout) rootView, backBtn, chipGroup,
          copyTxt, createMatch, favIcon, favImg, feedbackBtn, firstInter, parentLayout, presonsName,
          regenerateBtn, tCardView, thomasJPeCopy, toolbar);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
