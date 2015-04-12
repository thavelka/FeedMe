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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Fragment for the user signup screen.
 */
public class ParseSignupFragment extends ParseLoginFragmentBase implements OnClickListener {
  public static final String USERNAME = "com.parse.ui.ParseSignupFragment.USERNAME";
  public static final String PASSWORD = "com.parse.ui.ParseSignupFragment.PASSWORD";

  private ImageView loginImage;
  private EditText usernameField;
  private EditText passwordField;
  private EditText confirmPasswordField;
  private EditText emailField;

  private Button createAccountButton;
  private ParseOnLoginSuccessListener onLoginSuccessListener;

  private Spinner mLocationSpinner;

  private ParseLoginConfig config;
  private int minPasswordLength;

  private static final String LOG_TAG = "ParseSignupFragment";
  private static final int DEFAULT_MIN_PASSWORD_LENGTH = 6;
  private static final String USER_OBJECT_NAME_FIELD = "name";

  private List<ParseObject> mLocations;

  public static ParseSignupFragment newInstance(Bundle configOptions, String username, String password) {
    ParseSignupFragment signupFragment = new ParseSignupFragment();
    Bundle args = new Bundle(configOptions);
    args.putString(ParseSignupFragment.USERNAME, username);
    args.putString(ParseSignupFragment.PASSWORD, password);
    signupFragment.setArguments(args);
    return signupFragment;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                           Bundle savedInstanceState) {

    Bundle args = getArguments();
    config = ParseLoginConfig.fromBundle(args, getActivity());

    minPasswordLength = DEFAULT_MIN_PASSWORD_LENGTH;
    if (config.getParseSignupMinPasswordLength() != null) {
      minPasswordLength = config.getParseSignupMinPasswordLength();
    }

    String username = (String) args.getString(USERNAME);
    String password = (String) args.getString(PASSWORD);

    View v = inflater.inflate(R.layout.com_parse_ui_parse_signup_fragment,
        parent, false);
    loginImage = (ImageView) v.findViewById(R.id.loginImage);
    ImageView appLogo = (ImageView) v.findViewById(R.id.app_logo);
    usernameField = (EditText) v.findViewById(R.id.signup_username_input);
    passwordField = (EditText) v.findViewById(R.id.signup_password_input);
    confirmPasswordField = (EditText) v
        .findViewById(R.id.signup_confirm_password_input);
    emailField = (EditText) v.findViewById(R.id.signup_email_input);
    mLocationSpinner = (Spinner) v.findViewById(R.id.locationSpinner);
    createAccountButton = (Button) v.findViewById(R.id.create_account);

    imageChanger();

    new GetLocations().execute();

    usernameField.setText(username);
    passwordField.setText(password);

    if (appLogo != null && config.getAppLogo() != null) {
      appLogo.setImageResource(config.getAppLogo());
    }

    if (config.isParseLoginEmailAsUsername()) {
      usernameField.setHint(R.string.com_parse_ui_email_input_hint);
      usernameField.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
      if (emailField != null) {
        emailField.setVisibility(View.GONE);
      }
    }

    if (config.getParseSignupSubmitButtonText() != null) {
      createAccountButton.setText(config.getParseSignupSubmitButtonText());
    }
    createAccountButton.setOnClickListener(this);

    return v;
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
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
  public void onClick(View v) {
    String username = usernameField.getText().toString();
    String password = passwordField.getText().toString();
    String passwordAgain = confirmPasswordField.getText().toString();

    String email = null;
    if (config.isParseLoginEmailAsUsername()) {
      email = usernameField.getText().toString();
    } else if (emailField != null) {
      email = emailField.getText().toString();
    }

    if (username.length() == 0) {
      if (config.isParseLoginEmailAsUsername()) {
        showToast(R.string.com_parse_ui_no_email_toast);
      } else {
        showToast(R.string.com_parse_ui_no_username_toast);
      }
    } else if (password.length() == 0) {
      showToast(R.string.com_parse_ui_no_password_toast);
    } else if (password.length() < minPasswordLength) {
      showToast(getResources().getQuantityString(
          R.plurals.com_parse_ui_password_too_short_toast,
          minPasswordLength, minPasswordLength));
    } else if (passwordAgain.length() == 0) {
      showToast(R.string.com_parse_ui_reenter_password_toast);
    } else if (!password.equals(passwordAgain)) {
      showToast(R.string.com_parse_ui_mismatch_confirm_password_toast);
      confirmPasswordField.selectAll();
      confirmPasswordField.requestFocus();
    } else if (email != null && email.length() == 0) {
        showToast(R.string.com_parse_ui_no_email_toast);
    } else if (mLocationSpinner.getSelectedItemPosition() == 0) {
        showToast(R.string.no_city_toast);
    } else {
      ParseUser user = new ParseUser();

      // Set standard fields
      user.setUsername(username);
      user.setPassword(password);
      user.setEmail(email);

      int spinnerPosition = mLocationSpinner.getSelectedItemPosition();
      ParseObject userLocation = mLocations.get(spinnerPosition - 1);
      user.put("location", ParseObject.createWithoutData("Location", userLocation.getObjectId()));
      user.put("score", 0);
      user.put("show", true);
      user.put("isAdmin", false);


      loadingStart();
      user.signUpInBackground(new SignUpCallback() {

        @Override
        public void done(ParseException e) {
          if (isActivityDestroyed()) {
            return;
          }

          if (e == null) {
            loadingFinish();
            signupSuccess();
          } else {
            loadingFinish();
            if (e != null) {
              debugLog(getString(R.string.com_parse_ui_login_warning_parse_signup_failed) +
                  e.toString());
              switch (e.getCode()) {
                case ParseException.INVALID_EMAIL_ADDRESS:
                  showToast(R.string.com_parse_ui_invalid_email_toast);
                  break;
                case ParseException.USERNAME_TAKEN:
                  showToast(R.string.com_parse_ui_username_taken_toast);
                  break;
                case ParseException.EMAIL_TAKEN:
                  showToast(R.string.com_parse_ui_email_taken_toast);
                  break;
                default:
                  showToast(R.string.com_parse_ui_signup_failed_unknown_toast);
              }
            }
          }
        }
      });
    }
  }

  @Override
  protected String getLogTag() {
    return LOG_TAG;
  }

  private void signupSuccess() {
      Toast.makeText(getActivity(), "Account created", Toast.LENGTH_SHORT).show();
      onLoginSuccessListener.onLoginSuccess();
  }

    private class GetLocations extends AsyncTask<Void, Void, List<ParseObject>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<ParseObject> doInBackground(Void... params) {
            ParseQuery query = ParseQuery.getQuery("Location");
            try {
                mLocations = query.find();
                return mLocations;
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(final List<ParseObject> locations) {
            super.onPostExecute(locations);
            addItemsToSpinner(locations);

        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

    }

    public void addItemsToSpinner(List<ParseObject> locations) {

        List<String> list = new ArrayList<>();
        list.add(getString(R.string.spinnerPrompt)); // First string in array is prompt
        for (ParseObject i : locations) {
            String locationName = i.getString("city") + ", " + i.get("state");
            list.add(locationName);
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getActivity(),
                R.layout.spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mLocationSpinner.setAdapter(dataAdapter);
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

