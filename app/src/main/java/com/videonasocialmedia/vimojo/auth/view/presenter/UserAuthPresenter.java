package com.videonasocialmedia.vimojo.auth.view.presenter;

/**
 * Created by jliarte on 8/01/18.
 */

import android.util.Patterns;

/**
 * Presenter for {@link com.videonasocialmedia.vimojo.auth.view.activity.UserAuthActivity}
 */
public class UserAuthPresenter {
  private final View userAuthActivityView;
  private boolean register = true;

  public UserAuthPresenter(View userAuthActivityView) {
    this.userAuthActivityView = userAuthActivityView;
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

  public void performAuth(String email, String password, boolean checkBoxAcceptTermChecked) {
    userAuthActivityView.resetErrorFields();
    if (emailValidates(email) && passwordValidates(password)
            && checkBoxValidates(checkBoxAcceptTermChecked)) {
//      perfrom
    }
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
  }
}
