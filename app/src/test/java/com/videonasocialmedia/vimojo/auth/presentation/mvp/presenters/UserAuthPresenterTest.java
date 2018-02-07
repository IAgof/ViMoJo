package com.videonasocialmedia.vimojo.auth.presentation.mvp.presenters;

import android.content.Context;
import android.support.annotation.NonNull;

import com.videonasocialmedia.vimojo.auth.presentation.mvp.views.UserAuthView;
import com.videonasocialmedia.vimojo.auth.presentation.view.utils.EmailPatternValidator;
import com.videonasocialmedia.vimojo.vimojoapiclient.auth.VimojoUserAuthenticator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class UserAuthPresenterTest {
  @Mock UserAuthView mockedUserAuthActivityView;
  @Mock Context mockedContext;
  @Mock VimojoUserAuthenticator mockedVimojoUserAuthenticator;
  @Mock EmailPatternValidator mockedEmailPatternValidator;

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

    presenter.onClickLogin(areFieldsVisible, email, password);

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
    doReturn(true).when(mockedEmailPatternValidator).emailValidates(email);

    presenter.onClickLogin(areFieldsVisible, email, password);

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

    presenter.onClickRegister(isVisible, username, email, password, acceptTerms);

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
    doReturn(true).when(mockedEmailPatternValidator).emailValidates(email);

    presenter.onClickRegister(isVisible, username, email, password, acceptTerms);

    verify(mockedUserAuthActivityView).showProgressAuthenticationDialog();
  }

  @NonNull
  private UserAuthPresenter getUserAuthPresenter() {
    return new UserAuthPresenter(mockedUserAuthActivityView, mockedContext,
        mockedVimojoUserAuthenticator, mockedEmailPatternValidator);
  }
}
