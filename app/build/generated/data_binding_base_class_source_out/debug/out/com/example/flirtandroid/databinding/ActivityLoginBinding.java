// Generated by view binder compiler. Do not edit!
package com.example.flirtandroid.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.flirtandroid.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityLoginBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final LinearLayout appleBtn;

  @NonNull
  public final LinearLayout asAGuest;

  @NonNull
  public final ImageView email;

  @NonNull
  public final FrameLayout emailContainer;

  @NonNull
  public final ImageView eyeIcon;

  @NonNull
  public final LinearLayout googleBtn;

  @NonNull
  public final TextView logIn;

  @NonNull
  public final TextView loginAsGuest;

  @NonNull
  public final Button loginBtn;

  @NonNull
  public final ImageView myImageview;

  @NonNull
  public final TextView myTextview;

  @NonNull
  public final TextView myTextview3;

  @NonNull
  public final TextView or;

  @NonNull
  public final LinearLayout orLinear;

  @NonNull
  public final FrameLayout passwordContainer;

  @NonNull
  public final EditText passwordEditText;

  @NonNull
  public final EditText usernameEditText;

  private ActivityLoginBinding(@NonNull ConstraintLayout rootView, @NonNull LinearLayout appleBtn,
      @NonNull LinearLayout asAGuest, @NonNull ImageView email, @NonNull FrameLayout emailContainer,
      @NonNull ImageView eyeIcon, @NonNull LinearLayout googleBtn, @NonNull TextView logIn,
      @NonNull TextView loginAsGuest, @NonNull Button loginBtn, @NonNull ImageView myImageview,
      @NonNull TextView myTextview, @NonNull TextView myTextview3, @NonNull TextView or,
      @NonNull LinearLayout orLinear, @NonNull FrameLayout passwordContainer,
      @NonNull EditText passwordEditText, @NonNull EditText usernameEditText) {
    this.rootView = rootView;
    this.appleBtn = appleBtn;
    this.asAGuest = asAGuest;
    this.email = email;
    this.emailContainer = emailContainer;
    this.eyeIcon = eyeIcon;
    this.googleBtn = googleBtn;
    this.logIn = logIn;
    this.loginAsGuest = loginAsGuest;
    this.loginBtn = loginBtn;
    this.myImageview = myImageview;
    this.myTextview = myTextview;
    this.myTextview3 = myTextview3;
    this.or = or;
    this.orLinear = orLinear;
    this.passwordContainer = passwordContainer;
    this.passwordEditText = passwordEditText;
    this.usernameEditText = usernameEditText;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityLoginBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityLoginBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_login, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityLoginBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.apple_btn;
      LinearLayout appleBtn = ViewBindings.findChildViewById(rootView, id);
      if (appleBtn == null) {
        break missingId;
      }

      id = R.id.as_a_guest;
      LinearLayout asAGuest = ViewBindings.findChildViewById(rootView, id);
      if (asAGuest == null) {
        break missingId;
      }

      id = R.id.email;
      ImageView email = ViewBindings.findChildViewById(rootView, id);
      if (email == null) {
        break missingId;
      }

      id = R.id.emailContainer;
      FrameLayout emailContainer = ViewBindings.findChildViewById(rootView, id);
      if (emailContainer == null) {
        break missingId;
      }

      id = R.id.eyeIcon;
      ImageView eyeIcon = ViewBindings.findChildViewById(rootView, id);
      if (eyeIcon == null) {
        break missingId;
      }

      id = R.id.google_btn;
      LinearLayout googleBtn = ViewBindings.findChildViewById(rootView, id);
      if (googleBtn == null) {
        break missingId;
      }

      id = R.id.log_in;
      TextView logIn = ViewBindings.findChildViewById(rootView, id);
      if (logIn == null) {
        break missingId;
      }

      id = R.id.login_as_guest;
      TextView loginAsGuest = ViewBindings.findChildViewById(rootView, id);
      if (loginAsGuest == null) {
        break missingId;
      }

      id = R.id.login_btn;
      Button loginBtn = ViewBindings.findChildViewById(rootView, id);
      if (loginBtn == null) {
        break missingId;
      }

      id = R.id.my_imageview;
      ImageView myImageview = ViewBindings.findChildViewById(rootView, id);
      if (myImageview == null) {
        break missingId;
      }

      id = R.id.my_textview;
      TextView myTextview = ViewBindings.findChildViewById(rootView, id);
      if (myTextview == null) {
        break missingId;
      }

      id = R.id.my_textview3;
      TextView myTextview3 = ViewBindings.findChildViewById(rootView, id);
      if (myTextview3 == null) {
        break missingId;
      }

      id = R.id.or;
      TextView or = ViewBindings.findChildViewById(rootView, id);
      if (or == null) {
        break missingId;
      }

      id = R.id.or_linear;
      LinearLayout orLinear = ViewBindings.findChildViewById(rootView, id);
      if (orLinear == null) {
        break missingId;
      }

      id = R.id.passwordContainer;
      FrameLayout passwordContainer = ViewBindings.findChildViewById(rootView, id);
      if (passwordContainer == null) {
        break missingId;
      }

      id = R.id.passwordEditText;
      EditText passwordEditText = ViewBindings.findChildViewById(rootView, id);
      if (passwordEditText == null) {
        break missingId;
      }

      id = R.id.usernameEditText;
      EditText usernameEditText = ViewBindings.findChildViewById(rootView, id);
      if (usernameEditText == null) {
        break missingId;
      }

      return new ActivityLoginBinding((ConstraintLayout) rootView, appleBtn, asAGuest, email,
          emailContainer, eyeIcon, googleBtn, logIn, loginAsGuest, loginBtn, myImageview,
          myTextview, myTextview3, or, orLinear, passwordContainer, passwordEditText,
          usernameEditText);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
