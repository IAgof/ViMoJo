package com.videonasocialmedia.vimojo.auth.presentation.view.utils;

import android.util.Patterns;

import javax.inject.Inject;

public class EmailPatternValidator {
  @Inject
  public EmailPatternValidator() {
  }

  public boolean emailValidates(String email) {
    return Patterns.EMAIL_ADDRESS.matcher(email).matches();
  }
}