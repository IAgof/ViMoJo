package com.videonasocialmedia.vimojo.auth.presentation.mvp.presenters;

import android.content.Context;
import android.support.annotation.NonNull;

import com.videonasocialmedia.vimojo.auth.presentation.mvp.views.UserAuthView;
import com.videonasocialmedia.vimojo.vimojoapiclient.auth.VimojoUserAuthenticator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;


@RunWith(MockitoJUnitRunner.class)
public class UserAuthPresenterTest {

  @InjectMocks UserAuthPresenter injectedPresenter;

  @Mock UserAuthView mockedUserAuthActivityView;
  @Mock Context mockedContext;
  @Mock VimojoUserAuthenticator mockedVimojoUserAuthenticator;

  @Before
  public void injectMocks() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void switchToHomeModeResetViews() {
    UserAuthPresenter presenter = getUserAuthPresenter();

    presenter.switchToHomeMode();

    verify(mockedUserAuthActivityView).showInitRegisterOrLogin();
    verify(mockedUserAuthActivityView).resetErrorFields();
  }

  @Test
  public void onClickLoginGoToSignInModeIfLoginFieldsAreNotVisible() {
    UserAuthPresenter presenter = getUserAuthPresenter();
    boolean areFieldsVisible = false;
    String email = "a@a.a";
    String password = "1234567";
    boolean emailValidates = true;

    presenter.onClickLogin(areFieldsVisible, email, emailValidates, password);

    verify(mockedUserAuthActivityView).hideUserNameField();
    verify(mockedUserAuthActivityView).hideTermsCheckbox();
    verify(mockedUserAuthActivityView).hideRegisterButton();
    verify(mockedUserAuthActivityView).showLayoutRegisterLoginFields();
    verify(mockedUserAuthActivityView).updateScreenBackground();
  }

  @Test
  public void onClickLoginPerformLoginAuthIfLoginFieldsAreVisible() {
    UserAuthPresenter presenter = getUserAuthPresenter();
    boolean areFieldsVisible = true;
    String email = "a@a.a";
    String password = "1234567";
    boolean emailValidates = true;

    presenter.onClickLogin(areFieldsVisible, email, emailValidates, password);

    verify(mockedUserAuthActivityView).showProgressAuthenticationDialog();
  }

  @Test
  public void onClickRegisterGoToRegisterModeIfLoginViewsNotVisible() {
    UserAuthPresenter presenter = getUserAuthPresenter();
    boolean isVisible = false;
    String username = "A";
    String email = "a@a.a";
    String password = "1234567";
    boolean acceptTerms = true;
    boolean emailValidates = true;

    presenter.onClickRegister(isVisible, username, email, emailValidates, password, acceptTerms);

    verify(mockedUserAuthActivityView).showUserNameField();
    verify(mockedUserAuthActivityView).showTermsCheckbox();
    verify(mockedUserAuthActivityView).hideLoginButton();
    verify(mockedUserAuthActivityView).showLayoutRegisterLoginFields();
    verify(mockedUserAuthActivityView).updateScreenBackground();
  }

  @Test
  public void onClickRegisterPerformRegisterAuthIfRegisterFieldsAreVisible() {
    UserAuthPresenter presenter = getUserAuthPresenter();
    boolean isVisible = true;
    String username = "A";
    String email = "a@a.a";
    String password = "1234567";
    boolean acceptTerms = true;
    boolean emailValidates = true;

    presenter.onClickRegister(isVisible, username, email, emailValidates, password, acceptTerms);

    verify(mockedUserAuthActivityView).showProgressAuthenticationDialog();
  }

  @NonNull
  private UserAuthPresenter getUserAuthPresenter() {
    return new UserAuthPresenter(mockedUserAuthActivityView, mockedContext,
        mockedVimojoUserAuthenticator);
  }
}
