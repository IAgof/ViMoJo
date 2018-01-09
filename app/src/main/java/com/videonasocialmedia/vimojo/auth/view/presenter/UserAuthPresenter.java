package com.videonasocialmedia.vimojo.auth.view.presenter;

/**
 * Created by jliarte on 8/01/18.
 */

/**
 * Presenter for {@link com.videonasocialmedia.vimojo.auth.view.activity.UserAuthActivity}
 */
public class UserAuthPresenter {
  private final View userAuthActivityView;

  public UserAuthPresenter(View userAuthActivityView) {
    this.userAuthActivityView = userAuthActivityView;
  }

  public void switchToLoginView() {
    // TODO(jliarte): 9/01/18 set flag?
    userAuthActivityView.hideTermsCheckbox();
    userAuthActivityView.setAuthButtonSignInText();
    userAuthActivityView.setSignInFooterText();
  }

  public void switchToRegisterView() {
    // TODO(jliarte): 9/01/18 set flag?
    userAuthActivityView.showTermsCheckbox();
    userAuthActivityView.setAuthButtonRegisterText();
    userAuthActivityView.setRegisterFooterText();
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
  }
}
