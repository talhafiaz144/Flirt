// Generated by view binder compiler. Do not edit!
package com.example.flirtandroid.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.flirtandroid.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class LogoutDialogBinding implements ViewBinding {
  @NonNull
  private final RelativeLayout rootView;

  @NonNull
  public final CheckBox checkbox;

  @NonNull
  public final TextView desTxt;

  @NonNull
  public final RelativeLayout noBtn;

  @NonNull
  public final TextView noText;

  @NonNull
  public final TextView tText;

  @NonNull
  public final CardView updateCardview;

  @NonNull
  public final RelativeLayout withSkipBtnLayout;

  @NonNull
  public final RelativeLayout yesBtn;

  @NonNull
  public final TextView yesText;

  private LogoutDialogBinding(@NonNull RelativeLayout rootView, @NonNull CheckBox checkbox,
      @NonNull TextView desTxt, @NonNull RelativeLayout noBtn, @NonNull TextView noText,
      @NonNull TextView tText, @NonNull CardView updateCardview,
      @NonNull RelativeLayout withSkipBtnLayout, @NonNull RelativeLayout yesBtn,
      @NonNull TextView yesText) {
    this.rootView = rootView;
    this.checkbox = checkbox;
    this.desTxt = desTxt;
    this.noBtn = noBtn;
    this.noText = noText;
    this.tText = tText;
    this.updateCardview = updateCardview;
    this.withSkipBtnLayout = withSkipBtnLayout;
    this.yesBtn = yesBtn;
    this.yesText = yesText;
  }

  @Override
  @NonNull
  public RelativeLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static LogoutDialogBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static LogoutDialogBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.logout_dialog, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static LogoutDialogBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.checkbox;
      CheckBox checkbox = ViewBindings.findChildViewById(rootView, id);
      if (checkbox == null) {
        break missingId;
      }

      id = R.id.des_txt;
      TextView desTxt = ViewBindings.findChildViewById(rootView, id);
      if (desTxt == null) {
        break missingId;
      }

      id = R.id.no_btn;
      RelativeLayout noBtn = ViewBindings.findChildViewById(rootView, id);
      if (noBtn == null) {
        break missingId;
      }

      id = R.id.no_text;
      TextView noText = ViewBindings.findChildViewById(rootView, id);
      if (noText == null) {
        break missingId;
      }

      id = R.id.t_text;
      TextView tText = ViewBindings.findChildViewById(rootView, id);
      if (tText == null) {
        break missingId;
      }

      id = R.id.update_cardview;
      CardView updateCardview = ViewBindings.findChildViewById(rootView, id);
      if (updateCardview == null) {
        break missingId;
      }

      id = R.id.with_skip_btn_layout;
      RelativeLayout withSkipBtnLayout = ViewBindings.findChildViewById(rootView, id);
      if (withSkipBtnLayout == null) {
        break missingId;
      }

      id = R.id.yes_btn;
      RelativeLayout yesBtn = ViewBindings.findChildViewById(rootView, id);
      if (yesBtn == null) {
        break missingId;
      }

      id = R.id.yes_text;
      TextView yesText = ViewBindings.findChildViewById(rootView, id);
      if (yesText == null) {
        break missingId;
      }

      return new LogoutDialogBinding((RelativeLayout) rootView, checkbox, desTxt, noBtn, noText,
          tText, updateCardview, withSkipBtnLayout, yesBtn, yesText);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
