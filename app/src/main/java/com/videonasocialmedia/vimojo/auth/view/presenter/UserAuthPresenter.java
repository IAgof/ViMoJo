package com.videonasocialmedia.vimojo.auth.view.presenter;

/**
 * Created by jliarte on 8/01/18.
 */

import android.util.Patterns;

import com.videonasocialmedia.vimojo.auth.domain.usecase.VimojoUserAuthenticator;
import com.videonasocialmedia.vimojo.vimojoapiclient.model.AuthToken;

import javax.inject.Inject;

/**
 * Presenter for {@link com.videonasocialmedia.vimojo.auth.view.activity.UserAuthActivity}
 */
public class UserAuthPresenter {
  @Inject
  VimojoUserAuthenticator vimojoUserAuthenticator;

  private final View userAuthActivityView;
  private boolean register = true;
  private VimojoUserAuthenticator.RegisterListener registerListener;
  private VimojoUserAuthenticator.SignInListener signInListener;
  private String email;
  private String password;
  private boolean checkBoxAcceptTermChecked;

  public UserAuthPresenter(final View userAuthActivityView,
                           VimojoUserAuthenticator vimojoUserAuthenticator) {
    this.userAuthActivityView = userAuthActivityView;
    this.vimojoUserAuthenticator = vimojoUserAuthenticator;
    createRegisterListener(userAuthActivityView);
    createSignInListener(userAuthActivityView);
  }

  private void createSignInListener(final View userAuthActivityView) {
    signInListener = new VimojoUserAuthenticator.SignInListener() {
      @Override
      public void onSignInError(VimojoUserAuthenticator.SignInErrorCauses signInErrorCause) {
        userAuthActivityView.hideProgressAuthenticationDialog();
        switch (signInErrorCause) {
          case CREDENTIALS_UNKNOWN:
            userAuthActivityView.showErrorLoginUnknownCredentials();
            break;
          case NETWORK_ERROR:
            userAuthActivityView.showNetworkError();
            break;
          case UNKNOWN_ERROR:
          default:
            userAuthActivityView.showDefaultError();
            break;
        }
      }

      @Override
      public void onSignInSuccess(AuthToken authToken) {
//        unlockLoginFeatures();
        userAuthActivityView.showSigninSuccess();
        // TODO(jliarte): 11/01/18 use user, pwd, etc fields?
        createAccount(email, password, authToken.getToken());
      }
    };
  }

  private void createRegisterListener(final View userAuthActivityView) {
    registerListener = new VimojoUserAuthenticator.RegisterListener() {
      @Override
      public void onRegisterError(VimojoUserAuthenticator.RegisterErrorCauses cause) {
        userAuthActivityView.hideProgressAuthenticationDialog();
        switch (cause) {
          case NETWORK_ERROR:
            userAuthActivityView.showNetworkError();
            break;
          case USER_ALREADY_EXISTS:
            userAuthActivityView.showErrorRegisterUserExists();
            //loginUser.login(email, password, this);
            break;
          case INVALID_EMAIL:
            userAuthActivityView.showErrorRegisterInvalidMail();
            break;
          case MISSING_REQUEST_PARAMETERS:
            userAuthActivityView.showErrorRegisterMissingParams();
            break;
          case INVALID_PASSWORD:
            userAuthActivityView.showErrorRegisterInvalidPassword();
            break;
          case UNKNOWN_ERROR:
          default:
            userAuthActivityView.showDefaultError();
            break;
        }
      }

      @Override
      public void onRegisterSuccess() {
        userAuthActivityView.showRegisterSuccess();
        // TODO(jliarte): 11/01/18 use fields?
        vimojoUserAuthenticator.signIn(email, password, signInListener);
      }
    };
  }

  public void switchToLoginView() {
    this.register = false;
    userAuthActivityView.hideTermsCheckbox();
    userAuthActivityView.setAuthButtonSignInText();
    userAuthActivityView.setSignInFooterText();
  }

  public void switchToRegisterView() {
    this.register = true;
    userAuthActivityView.showTermsCheckbox();
    userAuthActivityView.setAuthButtonRegisterText();
    userAuthActivityView.setRegisterFooterText();
  }

  private boolean emailValidates(String email) {
    boolean emailValidates = Patterns.EMAIL_ADDRESS.matcher(email).matches();
    if (!emailValidates) {
      userAuthActivityView.showInvalidMailError();
    }
    return emailValidates;
  }

  private boolean passwordValidates(String password) {
    if (isEmptyField(password)) {
      userAuthActivityView.showPasswordFieldRequired();
      return false;
    }
    if (!isPasswordValid(password)) {
      userAuthActivityView.showPasswordInvalidError();
      return false;
    }
    return true;
  }

  private boolean checkBoxValidates(boolean checkBoxAcceptTermChecked) {
    if (register && !checkBoxAcceptTermChecked) {
      userAuthActivityView.showTermsNotAcceptedError();
      return false;
    }
    return true;
  }

  private boolean isEmptyField(String field) {
    return field == null || field.length() == 0;
  }

  private boolean isPasswordValid(String password) {
    // TODO:(alvaro.martinez) 15/06/16 will there be a rule for passwords?
    return password.length() >= 6;
  }

  public void performAuth(final String email, final String password, boolean checkBoxAcceptTermChecked) {
    userAuthActivityView.resetErrorFields();
    if (emailValidates(email) && passwordValidates(password)
            && checkBoxValidates(checkBoxAcceptTermChecked)) {
      userAuthActivityView.showProgressAuthenticationDialog();
      this.email = email;
      this.password = password;
      this.checkBoxAcceptTermChecked = checkBoxAcceptTermChecked;
      if (register) {
        vimojoUserAuthenticator.register(email, password, checkBoxAcceptTermChecked, registerListener);
      } else {
        vimojoUserAuthenticator.signIn(email, password, signInListener);
      }
    }
  }

  public void createAccount(String email, String password, String authToken) {
//    Account account = new Account(email, "YOUR ACCOUNT TYPE");
//    AccountManager am = AccountManager.get(this);
//    am.addAccountExplicitly(account, password, null);
//    am.setAuthToken(account, "full_access", authToken);
  }

  /**
   * View interface between {@link com.videonasocialmedia.vimojo.auth.view.activity.UserAuthActivity}
   * and {@link UserAuthPresenter}
   */
  public interface View {
    void hideTermsCheckbox();

    void showTermsCheckbox();

    void setAuthButtonSignInText();

    void setAuthButtonRegisterText();

    void setSignInFooterText();

    void setRegisterFooterText();

    void showInvalidMailError();

    void showPasswordFieldRequired();

    void showPasswordInvalidError();

    void showTermsNotAcceptedError();

    void resetErrorFields();

    void showProgressAuthenticationDialog();

    void hideProgressAuthenticationDialog();

    void showSigninSuccess();

    void showRegisterSuccess();

    void showErrorLoginUnknownCredentials();

    void showDefaultError();

    void showNetworkError();

    void showErrorRegisterUserExists();

    void showErrorRegisterInvalidMail();

    void showErrorRegisterMissingParams();

    void showErrorRegisterInvalidPassword();
  }
}
