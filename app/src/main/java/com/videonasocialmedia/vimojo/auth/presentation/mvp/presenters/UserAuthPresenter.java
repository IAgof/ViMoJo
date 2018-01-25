package com.videonasocialmedia.vimojo.auth.presentation.mvp.presenters;

/**
 * Created by jliarte on 8/01/18.
 */

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.util.Patterns;
import android.view.View;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.auth.AccountConstants;
import com.videonasocialmedia.vimojo.auth.presentation.mvp.views.UserAuthView;
import com.videonasocialmedia.vimojo.view.VimojoPresenter;
import com.videonasocialmedia.vimojo.vimojoapiclient.VimojoApiException;
import com.videonasocialmedia.vimojo.vimojoapiclient.auth.VimojoUserAuthenticator;
import com.videonasocialmedia.vimojo.vimojoapiclient.model.AuthToken;
import com.videonasocialmedia.vimojo.vimojoapiclient.model.User;

import java.lang.ref.WeakReference;
import java.util.concurrent.Callable;

import javax.inject.Inject;

/**
 * Presenter for {@link com.videonasocialmedia.vimojo.auth.presentation.view.activity.UserAuthActivity}
 */
public class UserAuthPresenter extends VimojoPresenter {
  private final WeakReference<Context> contextReference;
  @Inject
  VimojoUserAuthenticator vimojoUserAuthenticator;

  private UserAuthView userAuthActivityView;
  private boolean register = true;

  /**
   * Creates a presenter instance.
   *
   * @param userAuthActivityView interface with auth view.
   * @param context the app context.
   * @param vimojoUserAuthenticator api client for auth services.
   */
  public UserAuthPresenter(UserAuthView userAuthActivityView, Context context,
                           VimojoUserAuthenticator vimojoUserAuthenticator) {
    this.userAuthActivityView = userAuthActivityView;
    this.contextReference = new WeakReference<>(context);
    this.vimojoUserAuthenticator = vimojoUserAuthenticator;
  }

  /**
   * Sets activity mode and view components to Sign In mode.
   */
  public void switchToSignInMode() {
    this.register = false;
    userAuthActivityView.hideUserNameField();
    userAuthActivityView.hideTermsCheckbox();
    userAuthActivityView.hideRegisterButton();
    userAuthActivityView.showLayoutRegisterLoginFields();
    userAuthActivityView.updateScreenBackground();
  }

  /**
   * Sets activity mode and view components to Register mode.
   */
  public void switchToRegisterMode() {
    this.register = true;
    userAuthActivityView.showUserNameField();
    userAuthActivityView.showTermsCheckbox();
    userAuthActivityView.hideLoginButton();
    userAuthActivityView.showLayoutRegisterLoginFields();
    userAuthActivityView.updateScreenBackground();
  }

  private boolean userNameValidates(String userName){
    if(isEmptyField(userName)) {
      userAuthActivityView.showUserNameFieldRequired();
      return false;
    }
    return true;
  }

  private boolean emailValidates(String email) {
    if (isEmptyField(email)) {
      userAuthActivityView.showEmailFieldRequired();
      return false;
    }
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

  /**
   * Perform auth calls to platform login service.
   *
   * @param email email for the platform user account.
   * @param password password for the platform user account.
   */
  public void performLoginAuth(final String email, final String password) {
    if (emailValidates(email) && passwordValidates(password)) {
      userAuthActivityView.showProgressAuthenticationDialog();
      callSignInService(email, password);
    }
  }

  /**
   * Perform auth calls to platform register service.
   *
   * @param userName user name for the platform user account
   * @param email email for the platform user account.
   * @param password password for the platform user account.
   * @param checkBoxAcceptTermChecked user acceptance of privacy and policy terms.
   */
  public void performRegisterAuth(final String userName, final String email, final String password,
                                  final boolean checkBoxAcceptTermChecked) {
    if (userNameValidates(userName) && emailValidates(email) && passwordValidates(password)
        && checkBoxValidates(checkBoxAcceptTermChecked)){
      userAuthActivityView.showProgressAuthenticationDialog();
      callRegisterService(userName, email, password, checkBoxAcceptTermChecked);
    }
  }

  private void callRegisterService(final String userName, final String email, final String password,
                                   final boolean checkBoxAcceptTermChecked) {
    ListenableFuture<User> userFuture = executeUseCaseCall(new Callable<User>() {
      @Override
      public User call() throws Exception {
        return vimojoUserAuthenticator.register(userName, email, password,
            checkBoxAcceptTermChecked);
      }
    });
    Futures.addCallback(userFuture, new FutureCallback<User>() {
      @Override
      public void onSuccess(User result) {
        userAuthActivityView.showRegisterSuccess();
        // save id user.getId.

        callSignInService(email, password);
      }

      @Override
      public void onFailure(Throwable registerException) {
        userAuthActivityView.hideProgressAuthenticationDialog();
        if (registerException instanceof VimojoApiException) {
          parseRegisterErrors((VimojoApiException) registerException);
        } else {
          userAuthActivityView.showDefaultError();
        }
      }
    });
  }

  private void callSignInService(final String email, final String password) {
    ListenableFuture<AuthToken> tokenFuture = executeUseCaseCall(new Callable<AuthToken>() {
      @Override
      public AuthToken call() throws Exception {
        return vimojoUserAuthenticator.signIn(email, password);
      }
    });
    Futures.addCallback(tokenFuture, new FutureCallback<AuthToken>() {
      @Override
      public void onSuccess(AuthToken authToken) {
        userAuthActivityView.showSigninSuccess();
        registerAccount(email, password, authToken.getToken());
      }

      @Override
      public void onFailure(Throwable signInException) {
        // TODO(jliarte): 15/01/18 implement this method
        userAuthActivityView.hideProgressAuthenticationDialog();
        if (signInException instanceof VimojoApiException) {
          parseSignInErrors((VimojoApiException) signInException);
        } else {
          userAuthActivityView.showDefaultError();
        }
      }
    });
  }

  private void parseRegisterErrors(VimojoApiException registerException) {
    String cause = registerException.getApiErrorCode();
    switch (cause) {
      case VimojoApiException.NETWORK_ERROR:
        userAuthActivityView.showNetworkError();
        break;
      case VimojoUserAuthenticator.REGISTER_ERROR_USER_ALREADY_EXISTS:
        userAuthActivityView.showErrorRegisterUserExists();
        break;
      case VimojoUserAuthenticator.REGISTER_ERROR_MISSING_REQUEST_PARAMETERS:
        userAuthActivityView.showErrorRegisterMissingParams();
        break;
      case VimojoUserAuthenticator.REGISTER_ERROR_INTERNAL_SERVER_ERROR:
      case VimojoApiException.UNKNOWN_ERROR:
      default:
        userAuthActivityView.showDefaultError();
        break;
    }
  }

  private void parseSignInErrors(VimojoApiException signInException) {
    String cause = signInException.getApiErrorCode();
    switch (cause) {
      case VimojoUserAuthenticator.SIGNIN_ERROR_PASSWORD_MISSING:
      case VimojoUserAuthenticator.SIGNIN_ERROR_USER_MISSING:
      case VimojoUserAuthenticator.SIGNIN_ERROR_USER_NOT_FOUND:
      case VimojoUserAuthenticator.SIGNIN_ERROR_WRONG_PASSWORD:
        userAuthActivityView.showErrorSignInWrongCredentials();
        break;
      case VimojoApiException.NETWORK_ERROR:
        userAuthActivityView.showNetworkError();
        break;
      case VimojoUserAuthenticator.SIGNIN_ERROR_INTERNAL_SERVER_ERROR:
      case VimojoApiException.UNKNOWN_ERROR:
      default:
        userAuthActivityView.showDefaultError();
        break;
    }
  }

  private void registerAccount(String email, String password, String authToken) {
    Account account = new Account(email, AccountConstants.VIMOJO_ACCOUNT_TYPE);
    AccountManager am = AccountManager.get(getContext());
    am.addAccountExplicitly(account, password, null);
    am.setAuthToken(account, AccountConstants.VIMOJO_AUTH_TOKEN_TYPE, authToken);
  }

  private Context getContext() {
    return contextReference.get();
  }

  public void switchToHomeMode() {
    userAuthActivityView.showInitRegisterOrLogin();
    userAuthActivityView.resetErrorFields();
  }
}
