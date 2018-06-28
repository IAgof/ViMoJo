package com.videonasocialmedia.vimojo.auth.presentation.view.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

import com.auth0.android.Auth0;
import com.auth0.android.authentication.AuthenticationAPIClient;
import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.authentication.storage.SecureCredentialsManager;
import com.auth0.android.authentication.storage.SharedPreferencesStorage;
import com.auth0.android.provider.AuthCallback;
import com.auth0.android.provider.WebAuthProvider;
import com.auth0.android.result.Credentials;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.auth.presentation.mvp.presenters.UserAuthPresenter;
import com.videonasocialmedia.vimojo.auth.presentation.mvp.views.UserAuthView;
import com.videonasocialmedia.vimojo.main.VimojoActivity;
import com.videonasocialmedia.vimojo.presentation.views.activity.PrivacyPolicyActivity;
import com.videonasocialmedia.vimojo.presentation.views.activity.TermsOfServiceActivity;

import javax.inject.Inject;

/**
 * Activity for creating and signing in users into Vimojo web platform.
 */
public class UserAuthActivity extends VimojoActivity implements UserAuthView {
  private static final String TAG = UserAuthActivity.class.getCanonicalName();
  @Inject
  UserAuthPresenter userAuthPresenter;

  @BindView(R.id.email_field)
  EditText emailField;
  @BindView(R.id.password_field)
  EditText passwordField;
  @BindView(R.id.layoutProgressBarLogin)
  View layoutProgress;
  @BindView(R.id.progress_bar_login)
  View progressBarLogin;
  @BindView(R.id.image_login_confirm)
  ImageView imageLoginConfirm;
  @BindView(R.id.progress_text_view)
  TextView textViewLoginProgress;

  @BindView(R.id.register_button)
  Button registerButton;
  @BindView(R.id.login_button)
  Button loginButton;
  @BindView(R.id.auth0_button)
  Button auth0Button;
  @BindView(R.id.layout_register_login_fields)
  LinearLayout registerLoginFieldsLinearLayout;
  @BindView(R.id.layout_register_login)
  LinearLayout registerLoginLinearLayout;
  @BindView(R.id.user_name_field)
  EditText userNameField;
  @BindView(R.id.check_box_Accept_Term)
  CheckBox checkBoxAcceptTerm;
  @BindView(R.id.textView_Accept_Term)
  TextView textViewAcceptTerm;
  @BindView(R.id.linear_layout_accept_term)
  LinearLayout linearLayoutAcceptTerm;
  @BindView(R.id.user_auth_main_relative_layout)
  RelativeLayout mainRelativeLayout;
  @BindView(R.id.register_login_slogan_text_view)
  TextView sloganText;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setTheme(R.style.VideonaThemeUserAuth);
    setContentView(R.layout.activity_user_auth);
    ButterKnife.bind(this);
    getActivityPresentersComponent().inject(this);
    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    hideRegisterLoginFields();
    setStatusBarTransparent();
  }

  private void setStatusBarTransparent() {
    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
  }

  private void hideRegisterLoginFields() {
    registerLoginFieldsLinearLayout.setVisibility(View.GONE);
  }

  @Override
  public void onBackPressed(){
    if(isShowedRegisterLoginFields()) {
      userAuthPresenter.switchToHomeMode();
      return;
    }
    super.onBackPressed();
  }

  @Override
  public void onPause(){
    super.onPause();
    hideKeyboard(registerLoginFieldsLinearLayout);
  }


  @Override
  public void showInvalidMailError() {
    showMessage(R.string.error_invalid_email, loginButton);
  }

  @Override
  public void showEmailFieldRequired() {
    showMessage(R.string.email_field_required, loginButton);
  }

  @Override
  public void showPasswordFieldRequired() {
    showMessage(R.string.password_field_required, loginButton);
  }

  @Override
  public void showPasswordInvalidError() {
    showMessage(R.string.error_invalid_password, loginButton);
  }

  @Override
  public void showUserNameInvalidError() {
    showMessage(R.string.error_invalid_username, loginButton);
  }

  @Override
  public void showUserNameFieldRequired() {
    showMessage(R.string.user_name_field_required, registerButton);
  }

  @Override
  public void showTermsNotAcceptedError() {
    showMessage(R.string.error_no_accepted_terms, registerButton);
  }

  @Override
  public void resetErrorFields() {
    emailField.setText("");
    passwordField.setText("");
    userNameField.setText("");
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
  public void showSignInSuccess() {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        progressBarLogin.setVisibility(View.INVISIBLE);
        imageLoginConfirm.setVisibility(View.VISIBLE);
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
    setResult(RESULT_OK);
    finish();
  }

  @Override
  public void showRegisterSuccess() {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        progressBarLogin.setVisibility(View.INVISIBLE);
        imageLoginConfirm.setVisibility(View.VISIBLE);
        textViewLoginProgress.setText(R.string.success_register);
      }
    });
  }

  @Override
  public void showErrorSignInWrongCredentials() {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        showMessage(R.string.wrong_credentials_error, loginButton);
      }
    });
  }

  @Override
  public void showDefaultError() {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        showMessage(R.string.default_auth_error_message, registerButton);
      }
    });
  }

  @Override
  public void showNetworkError() {
    showMessage(R.string.network_error, registerButton);
  }

  @Override
  public void showErrorRegisterUserExists() {
    showMessage(R.string.error_already_exits, registerButton);
  }

  @Override
  public void showErrorRegisterInvalidMail() {
    showMessage(R.string.error_invalid_email, registerButton);
  }

  @Override
  public void showErrorRegisterMissingParams() {
    showMessage(R.string.error_field_required, registerButton);
  }

  @Override
  public void showErrorRegisterInvalidPassword() {
    showMessage(R.string.error_incorrect_password, registerButton);
  }

  @Override
  public void showUserNameField() {
    userNameField.setVisibility(View.VISIBLE);
  }

  @Override
  public void hideUserNameField() {
    userNameField.setVisibility(View.GONE);
  }

  @Override
  public void hideRegisterButton() {
    registerButton.setVisibility(View.GONE);
  }

  @Override
  public void hideLoginButton() {
    loginButton.setVisibility(View.GONE);
  }

  @Override
  public void showLayoutRegisterLoginFields() {
    registerLoginFieldsLinearLayout.setVisibility(View.VISIBLE);
    showKeyboard();
  }

  @Override
  public void updateScreenBackground() {
    mainRelativeLayout.setBackground(getDrawable(R.drawable.activity_user_auth_background_action));
    sloganText.setVisibility(View.GONE);
  }

  @Override
  public void showInitRegisterOrLogin() {
    hideRegisterLoginFields();
    registerButton.setVisibility(View.VISIBLE);
    loginButton.setVisibility(View.VISIBLE);
    mainRelativeLayout
        .setBackground(getDrawable(R.drawable.activity_user_auth_background_welcome));
    sloganText.setVisibility(View.VISIBLE);
  }

  @Override
  public void requestFocusEmailField() {
    emailField.requestFocus();
  }

  @Override
  public void requestFocusUserNameField() {
    userNameField.requestFocus();
  }

  @Override
  public void hideTermsCheckbox() {
    checkBoxAcceptTerm.setVisibility(View.GONE);
    textViewAcceptTerm.setVisibility(View.GONE);
    linearLayoutAcceptTerm.setVisibility(View.GONE);
  }

  @Override
  public void showTermsCheckbox() {
    checkBoxAcceptTerm.setMovementMethod(LinkMovementMethod.getInstance());
    textViewAcceptTerm.setText(createFooterTextForRegister());
    textViewAcceptTerm.setVisibility(View.VISIBLE);
    checkBoxAcceptTerm.setVisibility(View.VISIBLE);
    linearLayoutAcceptTerm.setVisibility(View.VISIBLE);
  }

  @OnClick(R.id.register_button)
  public void onClickRegister() {
    hideKeyboard(registerButton);
    String userName = userNameField.getText().toString();
    String email = emailField.getText().toString();
    String password = passwordField.getText().toString();
    userAuthPresenter.onClickRegister(isShowedRegisterLoginFields(), userName, email,
            password, checkBoxAcceptTerm.isChecked());
  }

  @OnClick(R.id.login_button)
  public void onClickLogin() {
    hideKeyboard(loginButton);
    String email = emailField.getText().toString();
    String password = passwordField.getText().toString();
    userAuthPresenter.onClickLogin(isShowedRegisterLoginFields(), email, password);
  }

  @OnClick(R.id.auth0_button)
  public void onClickAuth0() {
    //Auth0 account = new Auth0("{YOUR_CLIENT_ID}", "{YOUR_DOMAIN}");
    Auth0 account = new Auth0(this);
    //Configure the account in OIDC conformant mode
    account.setOIDCConformant(true);
    AuthenticationAPIClient apiClient = new AuthenticationAPIClient(account);
    SecureCredentialsManager manager = new SecureCredentialsManager(this, apiClient,
        new SharedPreferencesStorage(this));
    //Use the account in the API clients
    WebAuthProvider.init(account)
        .withScheme("https")
        .withScope("openid profile email")
        .withAudience(String.format("https://%s/userinfo", getString(R.string.com_auth0_domain)))
        .start(UserAuthActivity.this, new AuthCallback() {
          @Override
          public void onFailure(@NonNull final Dialog dialog) {
            runOnUiThread(new Runnable() {
              @Override
              public void run() {
                dialog.show();
              }
            });
          }

          @Override
          public void onFailure(final AuthenticationException exception) {
            runOnUiThread(new Runnable() {
              @Override
              public void run() {
                Toast.makeText(UserAuthActivity.this, "Error: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
              }
            });
          }

          @Override
          public void onSuccess(@NonNull final Credentials credentials) {
            runOnUiThread(new Runnable() {
              @Override
              public void run() {
                Log.d(TAG, "Logged in: " + credentials.getAccessToken());
                manager.saveCredentials(credentials);
              }
            });
          }
        });
  }

  private SpannableStringBuilder createFooterTextForRegister() {
    return new SpannableStringBuilder()
        .append(createSpannableNoClickable(getString(R.string.title_checkbox_termService),
        R.color.colorSecondary)).append(" ")
        .append(createIntentSpannable(getString(R.string.terms_of_service),
            TermsOfServiceActivity.class))
        .append(createSpannableNoClickable(" " + getString(R.string.and) + " ",
            R.color.colorSecondary))
        .append(createIntentSpannable(getString(R.string.privacy_policy),
            PrivacyPolicyActivity.class));
  }

  private Spannable createSpannableNoClickable(String stringResource, int colorResource) {
    Spannable spanText = new SpannableString(stringResource);
    spanText.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, colorResource)), 0,
        stringResource.length(), 0);
    return spanText;
  }

  private Spannable createIntentSpannable(String stringResource,
                                          final Class nextActivity) {
    Spannable spannableClickable = createSpannableNoClickable(stringResource,
        R.color.textColorLight);
    ClickableSpan clickableSpan = new ClickableSpan() {
      @Override
      public void onClick(View textView) {
        startActivity(new Intent(UserAuthActivity.this, nextActivity));
      }
    };
    spannableClickable.setSpan(clickableSpan, 0, spannableClickable.length(), 0);
    return spannableClickable;
  }

  private void showMessage(int stringResource, Button button) {
    Snackbar snackbar = Snackbar.make(button, stringResource, Snackbar.LENGTH_LONG);
    snackbar.show();
  }

  private void showProgress(final boolean show) {
    int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
    registerLoginLinearLayout.setVisibility(show ? View.INVISIBLE : View.VISIBLE);
    registerLoginLinearLayout.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1)
            .setListener(new AnimatorListenerAdapter() {
              @Override
              public void onAnimationEnd(Animator animation) {
                registerLoginLinearLayout.setVisibility(show ? View.INVISIBLE : View.VISIBLE);
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


  private boolean isShowedRegisterLoginFields() {
    return registerLoginFieldsLinearLayout.getVisibility() == View.VISIBLE;
  }

  private void showKeyboard() {
    InputMethodManager imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
  }

  private void hideKeyboard(View v) {
    InputMethodManager keyboard =
        (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
    keyboard.hideSoftInputFromWindow(v.getWindowToken(), 0);
  }
}
