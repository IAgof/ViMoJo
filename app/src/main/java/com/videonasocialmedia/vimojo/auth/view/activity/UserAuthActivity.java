package com.videonasocialmedia.vimojo.auth.view.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.auth.view.presenter.UserAuthPresenter;
import com.videonasocialmedia.vimojo.main.VimojoActivity;

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

  @Bind(R.id.email_field)
  EditText emailField;
  @Bind(R.id.password_field)
  EditText passwordField;
  @Bind(R.id.layoutProgressBarLogin)
  View layoutProgress;
  @Bind(R.id.progress_bar_login)
  View progressBarLogin;
  @Bind(R.id.image_login_confirm)
  ImageView imageLoginConfirm;
  @Bind(R.id.progress_text_view)
  TextView textViewLoginProgress;

  @Bind(R.id.register_button)
  Button registerButton;
  @Bind(R.id.login_button)
  Button loginButton;
  @Bind(R.id.layout_register_login_fields)
  LinearLayout registerLoginFieldsLinearLayout;
  @Bind(R.id.layout_register_login)
  LinearLayout registerLoginLinearLayout;
  @Bind(R.id.user_name_field)
  EditText userNameField;
  @Bind(R.id.check_box_Accept_Term)
  CheckBox checkBoxAcceptTerm;
  @Bind(R.id.user_auth_main_relative_layout)
  RelativeLayout mainRelativeLayout;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setTheme(R.style.VideonaThemeUserAuth);
    setContentView(R.layout.activity_user_auth);
    ButterKnife.bind(this);
    getActivityPresentersComponent().inject(this);
    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    hideRegisterLoginFields();
  }

  private void hideRegisterLoginFields() {
    registerLoginFieldsLinearLayout.setVisibility(View.GONE);
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


  @Override
  public void showInvalidMailError() {
    showMessage(R.string.error_invalid_email, loginButton);
  }

  @Override
  public void showPasswordFieldRequired() {
    showMessage(R.string.error_invalid_password, loginButton);
  }

  @Override
  public void showPasswordInvalidError() {
    showMessage(R.string.error_invalid_password, loginButton);
  }

  @Override
  public void showUserNameInvalid() {
    showMessage(R.string.error_invalid_username, loginButton);
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
  public void showSigninSuccess() {
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
    // TODO:(alvaro.martinez) 22/01/18 Implement register with user name
    //userNameField.setVisibility(View.VISIBLE);
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
  }

  @Override
  public void updateScreenBackground() {
    mainRelativeLayout.setBackground(getDrawable(R.drawable.activity_user_auth_background_action));
  }

  @Override
  public void hideTermsCheckbox() {
    checkBoxAcceptTerm.setVisibility(View.GONE);
  }

  @Override
  public void showTermsCheckbox() {
    checkBoxAcceptTerm.setVisibility(View.VISIBLE);
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

  @OnClick(R.id.register_button)
  public void onClickRegister() {
    if(!isShowedRegisterLoginFields()){
      userAuthPresenter.switchToRegisterMode();
    } else {
      String email = emailField.getText().toString();
      String password = passwordField.getText().toString();
      userAuthPresenter.performAuth(email, password, checkBoxAcceptTerm.isChecked());
    }
  }

  private boolean isShowedRegisterLoginFields() {
    return registerLoginFieldsLinearLayout.getVisibility() == View.VISIBLE;
  }

  @OnClick(R.id.login_button)
  public void onClickLogin() {
    if(!isShowedRegisterLoginFields()){
      userAuthPresenter.switchToSignInMode();
    } else {
      String email = emailField.getText().toString();
      String password = passwordField.getText().toString();
      userAuthPresenter.performAuth(email, password, checkBoxAcceptTerm.isChecked());
    }
  }
}
