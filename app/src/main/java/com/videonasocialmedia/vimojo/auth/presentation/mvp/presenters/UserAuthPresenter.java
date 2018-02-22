package com.videonasocialmedia.vimojo.auth.presentation.mvp.presenters;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.auth.AccountConstants;
import com.videonasocialmedia.vimojo.auth.presentation.mvp.views.UserAuthView;
import com.videonasocialmedia.vimojo.auth.presentation.view.utils.EmailPatternValidator;
import com.videonasocialmedia.vimojo.view.VimojoPresenter;
import com.videonasocialmedia.vimojo.vimojoapiclient.AuthApiClient;
import com.videonasocialmedia.vimojo.vimojoapiclient.VimojoApiException;
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
  private final EmailPatternValidator emailPatternValidator;
  @Inject
  AuthApiClient authApiClient;

  private final UserAuthView userAuthActivityView;
  private final Context context;
  private boolean register = true;

  /**
   * Creates a presenter instance.
   *
   * @param userAuthActivityView interface with auth view.
   * @param context the app context.
   * @param authApiClient api client for auth services.
   */
  public UserAuthPresenter(UserAuthView userAuthActivityView, Context context,
                           AuthApiClient authApiClient,
                           EmailPatternValidator emailPatternValidator) {
    this.userAuthActivityView = userAuthActivityView;
    this.context = context;
    this.contextReference = new WeakReference<>(context);
    this.emailPatternValidator = emailPatternValidator;
    this.authApiClient = authApiClient;
  }

  /**
   * Sets activity mode and view components to Sign In mode.
   */
  private void switchToSignInMode() {
    this.register = false;
    userAuthActivityView.hideUserNameField();
    userAuthActivityView.hideTermsCheckbox();
    userAuthActivityView.hideRegisterButton();
    userAuthActivityView.showLayoutRegisterLoginFields();
    userAuthActivityView.requestFocusEmailField();
    userAuthActivityView.updateScreenBackground();
  }

  /**
   * Sets activity mode and view components to Register mode.
   */
  private void switchToRegisterMode() {
    this.register = true;
    userAuthActivityView.showUserNameField();
    userAuthActivityView.showTermsCheckbox();
    userAuthActivityView.hideLoginButton();
    userAuthActivityView.showLayoutRegisterLoginFields();
    userAuthActivityView.requestFocusUserNameField();
    userAuthActivityView.updateScreenBackground();
  }

  private boolean userNameValidates(String userName) {
    if (isEmptyField(userName)) {
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
    if (!emailPatternValidator.emailValidates(email)) {
      userAuthActivityView.showInvalidMailError();
      return false;
    }
    return true;
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
  private void performLoginAuth(final String email, final String password) {
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
  private void performRegisterAuth(final String userName, final String email, final String password,
                                   final boolean checkBoxAcceptTermChecked) {
    if (userNameValidates(userName) && emailValidates(email) && passwordValidates(password)
        && checkBoxValidates(checkBoxAcceptTermChecked)) {
      userAuthActivityView.showProgressAuthenticationDialog();
      callRegisterService(userName, email, password, checkBoxAcceptTermChecked);
    }
  }

  private void callRegisterService(final String userName, final String email, final String password,
                                   final boolean checkBoxAcceptTermChecked) {
    ListenableFuture<User> userFuture = executeUseCaseCall(new Callable<User>() {
      @Override
      public User call() throws Exception {
        return authApiClient.register(userName, email, password,
            checkBoxAcceptTermChecked);
      }
    });
    Futures.addCallback(userFuture, new FutureCallback<User>() {
      @Override
      public void onSuccess(User result) {
        userAuthActivityView.showRegisterSuccess();
        callSignInService(email, password);
      }

      @Override
      public void onFailure(@NonNull Throwable registerException) {
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
        return authApiClient.signIn(email, password);
      }
    });
    Futures.addCallback(tokenFuture, new FutureCallback<AuthToken>() {
      @Override
      public void onSuccess(AuthToken authToken) {
        userAuthActivityView.showSignInSuccess();
        registerAccount(email, password, authToken.getToken(), authToken.getId());
      }

      @Override
      public void onFailure(@NonNull Throwable signInException) {
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
      case AuthApiClient.REGISTER_ERROR_USER_ALREADY_EXISTS:
        userAuthActivityView.showErrorRegisterUserExists();
        break;
      case AuthApiClient.REGISTER_ERROR_MISSING_REQUEST_PARAMETERS:
        userAuthActivityView.showErrorRegisterMissingParams();
        break;
      case AuthApiClient.REGISTER_ERROR_INTERNAL_SERVER_ERROR:
      case VimojoApiException.UNKNOWN_ERROR:
      default:
        userAuthActivityView.showDefaultError();
        break;
    }
  }

  private void parseSignInErrors(VimojoApiException signInException) {
    String cause = signInException.getApiErrorCode();
    switch (cause) {
      case AuthApiClient.SIGNIN_ERROR_PASSWORD_MISSING:
      case AuthApiClient.SIGNIN_ERROR_USER_MISSING:
      case AuthApiClient.SIGNIN_ERROR_USER_NOT_FOUND:
      case AuthApiClient.SIGNIN_ERROR_WRONG_PASSWORD:
        userAuthActivityView.showErrorSignInWrongCredentials();
        break;
      case VimojoApiException.NETWORK_ERROR:
        userAuthActivityView.showNetworkError();
        break;
      case AuthApiClient.SIGNIN_ERROR_INTERNAL_SERVER_ERROR:
      case VimojoApiException.UNKNOWN_ERROR:
      default:
        userAuthActivityView.showDefaultError();
        break;
    }
  }

  private void registerAccount(String email, String password, String authToken, String id) {
    Account account = new Account(email, AccountConstants.VIMOJO_ACCOUNT_TYPE);
    AccountManager am = AccountManager.get(getContext());
    final Bundle extraData = new Bundle();
    extraData.putString(AccountConstants.USER_ID, id);
    am.addAccountExplicitly(account, password, extraData);
    am.setAuthToken(account, AccountConstants.VIMOJO_AUTH_TOKEN_TYPE, authToken);
  }

  private Context getContext() {
    return contextReference.get();
  }

  public void switchToHomeMode() {
    userAuthActivityView.showInitRegisterOrLogin();
    userAuthActivityView.resetErrorFields();
  }

  public void onClickRegister(boolean isVisible, String userName, String email,
                              String password, boolean checkedAcceptTerms) {
    if (!isVisible) {
      switchToRegisterMode();
    } else {
      performRegisterAuth(userName, email, password, checkedAcceptTerms);
    }
  }

  public void onClickLogin(boolean isVisible, String email, String password) {
    if (!isVisible) {
      switchToSignInMode();
    } else {
      performLoginAuth(email, password);
    }
  }
}
