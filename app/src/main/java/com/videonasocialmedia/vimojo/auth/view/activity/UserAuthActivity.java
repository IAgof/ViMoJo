package com.videonasocialmedia.vimojo.auth.view.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.auth.view.presenter.UserAuthPresenter;
import com.videonasocialmedia.vimojo.main.VimojoActivity;
import com.videonasocialmedia.vimojo.presentation.views.activity.PrivacyPolicyActivity;
import com.videonasocialmedia.vimojo.presentation.views.activity.TermsOfServiceActivity;

import javax.inject.Inject;

/**
 * Created by jliarte on 8/01/18.
 */

/**
 * Activity for creating and signing in users into Vimojo web platform.
 */
public class UserAuthActivity extends VimojoActivity implements UserAuthPresenter.View {
  @Inject
  UserAuthPresenter userAuthPresenter;

  @Bind(R.id.text_input_email)
  TextInputLayout textInputEmail;
  @Bind(R.id.email_field)
  EditText emailField;
  @Bind(R.id.password_text_input)
  TextInputLayout passwordTextInput;
  @Bind(R.id.password_field)
  EditText passwordField;
  @Nullable
  @Bind(R.id.check_box_Accept_Term)
  CheckBox checkBoxAcceptTerm;
  @Bind(R.id.footer_text_view)
  TextView footerTextView;
  @Bind(R.id.auth_button)
  Button authButton;
  @Bind(R.id.layout_login_form)
  View layoutLoginForm;
  @Bind(R.id.layoutProgressBarLogin)
  View layoutProgress;
  @Bind(R.id.progress_bar_login)
  View progressBarLogin;
  @Bind(R.id.image_login_confirm)
  ImageView imageLoginConfirm;
  @Bind(R.id.progress_text_view)
  TextView textViewLoginProgress;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_user_auth);
    ButterKnife.bind(this);
    getActivityPresentersComponent().inject(this);
    setupToolbar();
    setRegisterFooterText();
    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        onBackPressed();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  private void setupToolbar() {
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayShowTitleEnabled(false);
    ActionBar ab = getSupportActionBar();
    ab.setDisplayHomeAsUpEnabled(true);
  }

  @Override
  public void setRegisterFooterText() {
    footerTextView.setMovementMethod(LinkMovementMethod.getInstance());
    footerTextView.setText(createFooterTextForRegister());
  }

  @Override
  public void showInvalidMailError() {
    textInputEmail.setError(getString(R.string.error_invalid_email));
  }

  @Override
  public void showPasswordFieldRequired() {
    passwordTextInput.setError(getString(R.string.error_invalid_password));
  }

  @Override
  public void showPasswordInvalidError() {
    passwordTextInput.setError(getString(R.string.error_invalid_password));
  }

  @Override
  public void showTermsNotAcceptedError() {
    showMessage(R.string.error_no_accepted_terms, authButton);
  }

  @Override
  public void resetErrorFields() {
    textInputEmail.setError(null);
    passwordTextInput.setError(null);
  }

  @Override
  public void showProgressAuthenticationDialog() {
    showProgress(true);
  }

  @Override
  public void hideProgressAuthenticationDialog() {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        showProgress(false);
      }
    });
  }

  @Override
  public void showSigninSuccess() {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        progressBarLogin.setVisibility(View.INVISIBLE);
        imageLoginConfirm.setVisibility(View.VISIBLE);
        textViewLoginProgress.setTextColor(getResources().getColor(R.color.colorPrimary));
        textViewLoginProgress.setText(R.string.success_sign_in);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
          @Override
          public void run() {
            exitLoginActivity();
          }
        }, 3000);
      }
    });
  }

  private void exitLoginActivity() {
    finish();
  }

  @Override
  public void showRegisterSuccess() {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        progressBarLogin.setVisibility(View.INVISIBLE);
        imageLoginConfirm.setVisibility(View.VISIBLE);
        textViewLoginProgress.setTextColor(getResources().getColor(R.color.colorPrimary));
        textViewLoginProgress.setText(R.string.success_register);
      }
    });
  }

  @Override
  public void showErrorSignInWrongCredentials() {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        showMessage(R.string.wrong_credentials_error, authButton);
      }
    });
  }

  @Override
  public void showDefaultError() {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        showMessage(R.string.default_auth_error_message, authButton);
      }
    });
  }

  @Override
  public void showNetworkError() {
    showMessage(R.string.network_error, authButton);
  }

  @Override
  public void showErrorRegisterUserExists() {
    showMessage(R.string.error_already_exits, authButton);
  }

  @Override
  public void showErrorRegisterInvalidMail() {
    showMessage(R.string.error_invalid_email, authButton);
  }

  @Override
  public void showErrorRegisterMissingParams() {
    showMessage(R.string.error_field_required, authButton);
  }

  @Override
  public void showErrorRegisterInvalidPassword() {
    showMessage(R.string.error_incorrect_password, authButton);
  }

  @Override
  public void setSignInFooterText() {
    footerTextView.setMovementMethod(LinkMovementMethod.getInstance());
    footerTextView.setText(createFooterTextForSignin());
  }

  @Override
  public void hideTermsCheckbox() {
    checkBoxAcceptTerm.setVisibility(View.GONE);
  }

  @Override
  public void showTermsCheckbox() {
    checkBoxAcceptTerm.setVisibility(View.VISIBLE);
  }

  @Override
  public void setAuthButtonSignInText() {
    authButton.setText(R.string.sign_in);
  }

  @Override
  public void setAuthButtonRegisterText() {
    authButton.setText(R.string.action_register);
  }

  private SpannableStringBuilder createFooterTextForRegister() {
    SpannableStringBuilder footerText = new SpannableStringBuilder()
            .append(createSpannableNoClickable(getString(R.string.have_account),
                    R.color.colorSecondary)).append(" ")
            .append(createSigninSpannable(getString(R.string.sign_in), R.color.colorPrimary))
            .append(createSpannableNoClickable("\n\n", R.color.colorSecondary))
            .append(createIntentSpannable(getString(R.string.terms_of_service),
                    R.color.colorPrimary, TermsOfServiceActivity.class))
            .append(createSpannableNoClickable(" " + getString(R.string.and) + " ",
                    R.color.colorSecondary))
            .append(createIntentSpannable(getString(R.string.privacy_policy),
                    R.color.colorPrimary, PrivacyPolicyActivity.class));
    return footerText;
  }

  private SpannableStringBuilder createFooterTextForSignin() {
    SpannableStringBuilder footerText = new SpannableStringBuilder()
            .append(createSpannableNoClickable(
                    getString(R.string.first_string_link_for_create_account),
                    R.color.colorSecondary)).append(" ")
            .append(createRegisterSpannable(
                    getString(R.string.second_string_link_for_create_account),
                    R.color.colorPrimary));
    return footerText;
  }

  private Spannable createSpannableNoClickable(String stringResource, int colorResource) {
    Spannable spanText = new SpannableString(stringResource);
    spanText.setSpan(new ForegroundColorSpan(getResources()
            .getColor(colorResource)), 0, stringResource.length(), 0);
    return spanText;
  }

  private Spannable createSigninSpannable(String stringResource,
                                         int colorResource) {
    Spannable spannableClickable = createSpannableNoClickable(stringResource, colorResource);
    ClickableSpan clickableSpan = new ClickableSpan() {
      @Override
      public void onClick(View textView) {
        userAuthPresenter.switchToSignInMode();
        hideTermsCheckbox();
      }
    };
    spannableClickable.setSpan(clickableSpan, 0, spannableClickable.length(), 0);
    return spannableClickable;
  }

  private Spannable createRegisterSpannable(String stringResource, int colorResource) {
    Spannable spannableClickable = createSpannableNoClickable(stringResource, colorResource);
    ClickableSpan clickableSpan = new ClickableSpan() {
      @Override
      public void onClick(View textView) {
        userAuthPresenter.switchToRegisterMode();
      }
    };
    spannableClickable.setSpan(clickableSpan, 0, spannableClickable.length(), 0);
    return spannableClickable;
  }

  private Spannable createIntentSpannable(String stringResource, int colorResource,
                                         final Class nextActivity) {
    Spannable spannableClickable = createSpannableNoClickable(stringResource, colorResource);
    ClickableSpan clickableSpan = new ClickableSpan() {
      @Override
      public void onClick(View textView) {
        startActivity(new Intent(UserAuthActivity.this, nextActivity));
      }
    };
    spannableClickable.setSpan(clickableSpan, 0, spannableClickable.length(), 0);
    return spannableClickable;
  }

  /**
   * Performs auth calls.
   */
  @OnClick(R.id.auth_button)
  public void perform_auth() {
    String email = emailField.getText().toString();
    String password = passwordField.getText().toString();
    userAuthPresenter.performAuth(email, password, checkBoxAcceptTerm.isChecked());
  }

  private void showMessage(int stringResource, Button button) {
    Snackbar snackbar = Snackbar.make(button, stringResource, Snackbar.LENGTH_LONG);
    snackbar.show();
  }

  private void showProgress(final boolean show) {
    int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
    layoutLoginForm.setVisibility(show ? View.INVISIBLE : View.VISIBLE);
    layoutLoginForm.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1)
            .setListener(new AnimatorListenerAdapter() {
              @Override
              public void onAnimationEnd(Animator animation) {
                layoutLoginForm.setVisibility(show ? View.INVISIBLE : View.VISIBLE);
              }
            });
    layoutProgress.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
    layoutProgress.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0)
            .setListener(new AnimatorListenerAdapter() {
              @Override
              public void onAnimationEnd(Animator animation) {
                layoutProgress.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
              }
            });
  }
}
