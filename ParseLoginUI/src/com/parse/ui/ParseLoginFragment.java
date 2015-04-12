/*
 *  Copyright (c) 2014, Parse, LLC. All rights reserved.
 *
 *  You are hereby granted a non-exclusive, worldwide, royalty-free license to use,
 *  copy, modify, and distribute this software in source code or binary form for use
 *  in connection with the web services and APIs provided by Parse.
 *
 *  As with any software that integrates with the Parse platform, your use of
 *  this software is subject to the Parse Terms of Service
 *  [https://www.parse.com/about/terms]. This copyright notice shall be
 *  included in all copies or substantial portions of the software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 *  FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 *  COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 *  IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 *  CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package com.parse.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Random;


/**
 * Fragment for the user login screen.
 */
public class ParseLoginFragment extends ParseLoginFragmentBase {

  public interface ParseLoginFragmentListener {
    public void onSignUpClicked(String username, String password);

    public void onLoginHelpClicked();

    public void onLoginSuccess();
  }

  private static final String LOG_TAG = "ParseLoginFragment";
  private static final String USER_OBJECT_NAME_FIELD = "name";

  private View parseLogin;
  private Bitmap imageBitmap;
  private ImageView loginImage;
  private EditText usernameField;
  private EditText passwordField;
  private TextView parseLoginHelpButton;
  private Button parseLoginButton;
  private Button parseSignupButton;
  private ParseLoginFragmentListener loginFragmentListener;
  private ParseOnLoginSuccessListener onLoginSuccessListener;

  private ParseLoginConfig config;

  public static ParseLoginFragment newInstance(Bundle configOptions) {
    ParseLoginFragment loginFragment = new ParseLoginFragment();
    loginFragment.setArguments(configOptions);
    return loginFragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                           Bundle savedInstanceState) {
    config = ParseLoginConfig.fromBundle(getArguments(), getActivity());

    View v = inflater.inflate(R.layout.com_parse_ui_parse_login_fragment,
        parent, false);

    ImageView appLogo = (ImageView) v.findViewById(R.id.app_logo);
    parseLogin = v.findViewById(R.id.parse_login);
    loginImage = (ImageView) v.findViewById(R.id.loginImage);
    usernameField = (EditText) v.findViewById(R.id.login_username_input);
    passwordField = (EditText) v.findViewById(R.id.login_password_input);
    parseLoginHelpButton = (Button) v.findViewById(R.id.parse_login_help);
    parseLoginButton = (Button) v.findViewById(R.id.parse_login_button);
    parseSignupButton = (Button) v.findViewById(R.id.parse_signup_button);

    imageChanger();


    if (appLogo != null && config.getAppLogo() != null) {
      appLogo.setImageResource(config.getAppLogo());
    }
    if (allowParseLoginAndSignup()) {
      setUpParseLoginAndSignup();
    }

    return v;
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);

    if (activity instanceof ParseLoginFragmentListener) {
      loginFragmentListener = (ParseLoginFragmentListener) activity;
    } else {
      throw new IllegalArgumentException(
          "Activity must implemement ParseLoginFragmentListener");
    }

    if (activity instanceof ParseOnLoginSuccessListener) {
      onLoginSuccessListener = (ParseOnLoginSuccessListener) activity;
    } else {
      throw new IllegalArgumentException(
          "Activity must implemement ParseOnLoginSuccessListener");
    }

    if (activity instanceof ParseOnLoadingListener) {
      onLoadingListener = (ParseOnLoadingListener) activity;
    } else {
      throw new IllegalArgumentException(
          "Activity must implemement ParseOnLoadingListener");
    }
  }

  @Override
  protected String getLogTag() {
    return LOG_TAG;
  }

  private void setUpParseLoginAndSignup() {
    parseLogin.setVisibility(View.VISIBLE);

    if (config.isParseLoginEmailAsUsername()) {
      usernameField.setHint(R.string.com_parse_ui_email_input_hint);
      usernameField.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
    }

    if (config.getParseLoginButtonText() != null) {
      parseLoginButton.setText(config.getParseLoginButtonText());
    }

    parseLoginButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        String username = usernameField.getText().toString();
        String password = passwordField.getText().toString();

        if (username.length() == 0) {
          if (config.isParseLoginEmailAsUsername()) {
            showToast(R.string.com_parse_ui_no_email_toast);
          } else {
            showToast(R.string.com_parse_ui_no_username_toast);
          }
        } else if (password.length() == 0) {
          showToast(R.string.com_parse_ui_no_password_toast);
        } else {
          loadingStart(true);
          ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
              if (isActivityDestroyed()) {
                return;
              }

              if (user != null) {
                loadingFinish();
                loginSuccess();
              } else {
                loadingFinish();
                if (e != null) {
                  debugLog(getString(R.string.com_parse_ui_login_warning_parse_login_failed) +
                      e.toString());
                  if (e.getCode() == ParseException.OBJECT_NOT_FOUND) {
                    if (config.getParseLoginInvalidCredentialsToastText() != null) {
                      showToast(config.getParseLoginInvalidCredentialsToastText());
                    } else {
                      showToast(R.string.com_parse_ui_parse_login_invalid_credentials_toast);
                    }
                    passwordField.selectAll();
                    passwordField.requestFocus();
                  } else {
                    showToast(R.string.com_parse_ui_parse_login_failed_unknown_toast);
                  }
                }
              }
            }
          });
        }
      }
    });

    if (config.getParseSignupButtonText() != null) {
      parseSignupButton.setText(config.getParseSignupButtonText());
    }

    parseSignupButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        String username = usernameField.getText().toString();
        String password = passwordField.getText().toString();

       loginFragmentListener.onSignUpClicked(username, password);
      }
    });

    if (config.getParseLoginHelpText() != null) {
      parseLoginHelpButton.setText(config.getParseLoginHelpText());
    }

    parseLoginHelpButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        loginFragmentListener.onLoginHelpClicked();
      }
    });
  }


  private boolean allowParseLoginAndSignup() {
    if (!config.isParseLoginEnabled()) {
      return false;
    }

    if (usernameField == null) {
      debugLog(R.string.com_parse_ui_login_warning_layout_missing_username_field);
    }
    if (passwordField == null) {
      debugLog(R.string.com_parse_ui_login_warning_layout_missing_password_field);
    }
    if (parseLoginButton == null) {
      debugLog(R.string.com_parse_ui_login_warning_layout_missing_login_button);
    }
    if (parseSignupButton == null) {
      debugLog(R.string.com_parse_ui_login_warning_layout_missing_signup_button);
    }
    if (parseLoginHelpButton == null) {
      debugLog(R.string.com_parse_ui_login_warning_layout_missing_login_help_button);
    }

    boolean result = (usernameField != null) && (passwordField != null)
        && (parseLoginButton != null) && (parseSignupButton != null)
        && (parseLoginHelpButton != null);

    if (!result) {
      debugLog(R.string.com_parse_ui_login_warning_disabled_username_password_login);
    }
    return result;
  }

  private void loginSuccess() {
    onLoginSuccessListener.onLoginSuccess();
  }

  private void imageChanger() {
      ParseQuery<ParseObject> query = ParseQuery.getQuery("Restaurant");
      query.findInBackground(new FindCallback<ParseObject>() {
          @Override
          public void done(List<ParseObject> objects, ParseException e) {
              Random rand = new Random();
              int  n = rand.nextInt(objects.size());
              ParseObject randRest = objects.get(n);

              final BlurredAsynctask task = new BlurredAsynctask(getActivity(), loginImage, 10);

              task.execute(randRest.getString("imageUrl"));
          }
      });

  }

    public class BlurredAsynctask extends AsyncTask<String, Void, Bitmap> {

        private Context context;

        private ImageView iv;

        private int radius;

        public BlurredAsynctask(Context context, ImageView iv, int radius) {

            this.context = context;

            this.iv = iv;

            this.radius = radius;

        }

        @Override

        protected Bitmap doInBackground(String... params) {

            URL url = null;

            try {

                url = new URL(params[0]);

            } catch (MalformedURLException e) {


                e.printStackTrace();

                url = null;

            }

            try {

                if (url != null) {

                    Bitmap image = BitmapFactory.decodeStream(url.openConnection()

                            .getInputStream());

                    return image;

                } else {

                    return null;

                }

            } catch (IOException e) {

                e.printStackTrace();

                return null;

            }

        }

        @Override

        protected void onPostExecute(Bitmap result) {

            super.onPostExecute(result);

            if (result != null) {

                Bitmap bm = CreateBlurredImage(result, radius);

                iv.setImageBitmap(bm);

            }

        }

        private Bitmap CreateBlurredImage(final Bitmap bm, int radius) {

            Bitmap blurredBitmap;

            RenderScript rs = RenderScript.create(context);

            ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs,

                    Element.U8_4(rs));

            Allocation input;

            input = Allocation.createFromBitmap(rs, bm,

                    Allocation.MipmapControl.MIPMAP_FULL, Allocation.USAGE_SCRIPT);

            script.setRadius(radius);

            script.setInput(input);

            blurredBitmap = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(), Bitmap.Config.ARGB_8888);

            Allocation output;

            output = Allocation.createTyped(rs, input.getType());

            script.forEach(output);

            output.copyTo(blurredBitmap);

            script.destroy();

            return blurredBitmap;

        }

    }
}
