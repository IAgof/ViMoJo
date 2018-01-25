package com.videonasocialmedia.vimojo.auth.presentation.mvp.views;

/**
 * Created by alvaro on 23/01/18.
 */

import com.videonasocialmedia.vimojo.auth.presentation.mvp.presenters.UserAuthPresenter;

/**
 * View interface between
 * {@link com.videonasocialmedia.vimojo.auth.presentation.view.activity.UserAuthActivity}
 * and {@link UserAuthPresenter}
 */
public interface UserAuthView {
  void hideTermsCheckbox();

  void showTermsCheckbox();

  void showInvalidMailError();

  void showEmailFieldRequired();

  void showPasswordFieldRequired();

  void showPasswordInvalidError();

  void showUserNameInvalidError();

  void showUserNameFieldRequired();

  void showTermsNotAcceptedError();

  void resetErrorFields();

  void showProgressAuthenticationDialog();

  void hideProgressAuthenticationDialog();

  void showSigninSuccess();

  void showRegisterSuccess();

  void showErrorSignInWrongCredentials();

  void showDefaultError();

  void showNetworkError();

  void showErrorRegisterUserExists();

  void showErrorRegisterInvalidMail();

  void showErrorRegisterMissingParams();

  void showErrorRegisterInvalidPassword();

  void showUserNameField();

  void hideUserNameField();

  void hideRegisterButton();

  void hideLoginButton();

  void showLayoutRegisterLoginFields();

  void updateScreenBackground();

  void showInitRegisterOrLogin();

}
