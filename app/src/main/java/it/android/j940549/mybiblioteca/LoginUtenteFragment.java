package it.android.j940549.mybiblioteca;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.fingerprint.FingerprintManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.Calendar;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.security.auth.x500.X500Principal;

import it.android.j940549.mybiblioteca.Activity_Gestore.GestoreNav;
import it.android.j940549.mybiblioteca.Activity_Utente.UtenteNav;
import it.android.j940549.mybiblioteca.Controller_DB.Cerca_pw_utente_in_DB;
import it.android.j940549.mybiblioteca.FingerprintDialog.FingerprintAuthenticationDialogFragment;
import it.android.j940549.mybiblioteca.Model.Utente;

import static it.android.j940549.mybiblioteca.Login_Ute_Ges_Activity.KEYNAME;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link LoginUtenteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginUtenteFragment extends Fragment {
    private static final String DIALOG_FRAGMENT_TAG = "myFragment";
    private static final String SECRET_MESSAGE = "Very secret message";
    public static final String DEFAULT_KEY_NAME = "default_key";
    //public static final String KEYNAME = "myBibliotecaUtente";
    private KeyStore mKeyStore;
    private KeyGenerator mKeyGenerator;
    private KeyguardManager keyguardManager = null;
    private FingerprintManager fingerprintManager=null;
    private SharedPreferences mSharedPreferences;
    private EditText editPassword;
    private EditText editnomeUser;
    private ImageButton fp_button;
    private CheckBox chkboxRicordami;
    private String pwcontrollo, user,sono_gestore;
    private String mParam1,mParam2;
    private Activity myActivity;
    private Utente utenteLogin;


    public LoginUtenteFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginUtenteFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginUtenteFragment newInstance(String param1, String param2) {

        LoginUtenteFragment fragment = new LoginUtenteFragment();
        Bundle args = new Bundle();
        args.putString("arg1", param1);
        args.putString("arg2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString("arg1");
            mParam2 = getArguments().getString("arg2");
        }
        myActivity=getActivity();
        utenteLogin=new Utente();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view=inflater.inflate(R.layout.fragment_login_utente, container, false);
        // Inflate the layout for this fragment



//editPasswordControll=new EditText(myActivity);
    editnomeUser = (EditText) view.findViewById(R.id.user);
    editPassword = (EditText) view.findViewById(R.id.password);
    chkboxRicordami = (CheckBox) view.findViewById(R.id.chkboxRicordami);
        Button buttonlogin= view.findViewById(R.id.login);
        buttonlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUtente(view);
            }
        });
//    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    leggiUtenteinPreferenze();

        chkboxRicordami.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    salvaUtenteinPreferenze();
                }
                if(!isChecked){
                    salvaUtenteinPreferenze();
                }
            }
        });

    creaNewKeytoKeyStore();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

        fp_button= (ImageButton) view.findViewById(R.id.purchase_button);
        fp_button.setVisibility(View.VISIBLE);
        try {
            mKeyStore = KeyStore.getInstance("AndroidKeyStore");
        } catch (KeyStoreException e) {
            throw new RuntimeException("Failed to get an instance of KeyStore", e);
        }
        try {
            mKeyGenerator = KeyGenerator
                    .getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new RuntimeException("Failed to get an instance of KeyGenerator", e);
        }
        Cipher defaultCipher;

        try {
            defaultCipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7);

        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get an instance of Cipher", e);
        }
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());


        fingerprintManager = myActivity.getSystemService(FingerprintManager.class);


        createKey(DEFAULT_KEY_NAME, true);

        fp_button.setEnabled(true);
        fp_button.setOnClickListener(
                new LoginUtenteFragment.PurchaseButtonClickListener(defaultCipher, DEFAULT_KEY_NAME));
    }

        return view;
    }





    public boolean controllaSetUpFingerprint(){
        boolean setOn=false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            keyguardManager = myActivity.getSystemService(KeyguardManager.class);
        }
        if (!keyguardManager.isKeyguardSecure()) {
            // Show a message that the user hasn't set up a fingerprint or lock screen.
            Toast.makeText(myActivity,
               /*     "Secure lock screen hasn't set up.\n"
                            + "Go to 'Settings -> Security -> Fingerprint' to set up a fingerprint",*/
                    "il Blocco dello schermo non è settato.\n"
                            + "vai a 'Settings -> Security -> Fingerprint' ed impostalo a fingerprint",
                    Toast.LENGTH_LONG).show();
            fp_button.setEnabled(false);

            setOn=false;
        }else {

            // Now the protection level of USE_FINGERPRINT permission is normal instead of dangerous.
            // See http://developer.android.com/reference/android/Manifest.permission.html#USE_FINGERPRINT
            // The line below prevents the false positive inspection from Android Studio
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // noinspection ResourceType
                if (!fingerprintManager.hasEnrolledFingerprints()) {
                    fp_button.setEnabled(false);
                    // This happens when no fingerprints are registered.
                    Toast.makeText(myActivity,
//                        "Go to 'Settings -> Security -> Fingerprint' and register at least one fingerprint",
                            "Non hai registrato nessun fingerprint\n" +
                                    "vai a  'Settings -> Security -> Fingerprint' e registerane almeno uno",
                            Toast.LENGTH_LONG).show();
                    setOn = false;
                } else {
                    setOn = true;
                }
            }
        }
        return setOn;
    }
    private void leggiUtenteinPreferenze(){
        SharedPreferences sharedPref = myActivity.getPreferences(Context.MODE_PRIVATE);
        String utente= sharedPref.getString("nomeUtente", "");
        boolean check=sharedPref.getBoolean("chkbox", false);

        if(check||!utente.equals("")){
            editnomeUser.setText(utente);
            chkboxRicordami.setChecked(check);
            editPassword.requestFocus();
        }
        if(!check){
            editnomeUser.setText("");
            chkboxRicordami.setChecked(check);
        }

    }
    private void salvaUtenteinPreferenze() {

        String user = editnomeUser.getText().toString();
        Boolean cheked=false;
        if(chkboxRicordami.isChecked()){
            cheked=true;
        }else{
            cheked=false;
        }
        if(!user.equals("")&& cheked) {
            SharedPreferences sharedPref = myActivity.getPreferences(Context.MODE_PRIVATE);
            String utente= sharedPref.getString("nomeUtente", "");
            if(utente.equals("")) {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("nomeUtente", user);
                editor.putBoolean("chkbox",cheked);
                editor.commit();
            }
        }else if (user.equals("")&& cheked) {
            Toast.makeText(myActivity, "inserisci prima il nome Utente", Toast.LENGTH_SHORT).show();
            chkboxRicordami.setChecked(false);
        }else if (!user.equals("")&& !cheked){
            SharedPreferences sharedPref = myActivity.getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("nomeUtente", "");
            editor.putBoolean("chkbox",cheked);
            editor.commit();
        }else if(user.equals("")&& !cheked){
            SharedPreferences sharedPref = myActivity.getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("nomeUtente", "");
            editor.putBoolean("chkbox",cheked);
            editor.commit();
        }
    }

    public void creaNewKeytoKeyStore() {
        //String alias = aliasText.getText().toString();

        try {
            mKeyStore = KeyStore.getInstance("AndroidKeyStore");
            mKeyStore.load(null);
            Calendar start= Calendar.getInstance();
            Calendar end=Calendar.getInstance();
            end.add(Calendar.YEAR,3);
            Log.d("login no nuova key ",""+mKeyStore.containsAlias(KEYNAME));
            // Create new key if needed
            if (!mKeyStore.containsAlias(KEYNAME)) {
                Log.d("login si nuova key ",""+!mKeyStore.containsAlias(KEYNAME));
                KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(getContext())
                        .setAlias(KEYNAME)
                        .setSubject(new X500Principal("CN=Sample Name, O=Android Authority"))
                        .setSerialNumber(BigInteger.ONE)
                        .setStartDate(start.getTime())
                        .setEndDate(end.getTime())
                        .build();
                KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "AndroidKeyStore");
                generator.initialize(spec);

                KeyPair keyPair = generator.generateKeyPair();
            }else{
                KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) mKeyStore.getEntry(KEYNAME, null);
                Log.d("login key privata log",privateKeyEntry.toString());
                //prendiamo la chiave pubblica
                RSAPublicKey publicKey = (RSAPublicKey) privateKeyEntry.getCertificate().getPublicKey();
                Log.d("login key privata log",publicKey.toString());

            }
        } catch (Exception e) {
            Toast.makeText(myActivity, "Exception " + e.getMessage() + " occured", Toast.LENGTH_LONG).show();
            Log.e("myBiblioteca", Log.getStackTraceString(e));
        }

    }

    //  @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean initCipher(Cipher cipher, String keyName) throws KeyPermanentlyInvalidatedException {
        try {
            mKeyStore.load(null);
            SecretKey key = (SecretKey) mKeyStore.getKey(keyName, null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (KeyStoreException | CertificateException | UnrecoverableKeyException | IOException
                | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }
    }

    /**
     * Proceed the purchase operation
     *
     * @param withFingerprint {@code true} if the purchase was made by using a fingerprint
     * @param cryptoObject the Crypto object
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onPurchased(boolean withFingerprint,
                            @Nullable FingerprintManager.CryptoObject cryptoObject) {
        if (withFingerprint) {
            // If the user has authenticated with fingerprint, verify that using cryptography and
            // then show the confirmation message.
            assert cryptoObject != null;
            tryEncrypt(cryptoObject.getCipher());
        } else {
            // Authentication happened with backup password. Just show the confirmation message.
            // showConfirmation(null);
        }
    }



    /**
     * Tries to encrypt some data with the generated key in {@link #createKey} which is
     * only works if the user has just authenticated via fingerprint.
     */
    private void tryEncrypt(Cipher cipher) {
        try {
            byte[] encrypted = cipher.doFinal(SECRET_MESSAGE.getBytes());
            // showConfirmation(encrypted);
        } catch (BadPaddingException | IllegalBlockSizeException e) {
            Toast.makeText(myActivity, "Failed to encrypt the data with the generated key. "
                    + "Retry the purchase", Toast.LENGTH_LONG).show();
            Log.e("myBiblioteca", "Failed to encrypt the data with the generated key." + e.getMessage());
        }
    }

    /**
     * Creates a symmetric key in the Android Key Store which can only be used after the user has
     * authenticated with fingerprint.
     *
     * @param keyName the name of the key to be created
     * @param invalidatedByBiometricEnrollment if {@code false} is passed, the created key will not
     *                                         be invalidated even if a new fingerprint is enrolled.
     *                                         The default value is {@code true}, so passing
     *                                         {@code true} doesn't change the behavior
     *                                         (the key will be invalidated if a new fingerprint is
     *                                         enrolled.). Note that this parameter is only valid if
     *                                         the app works on Android N developer preview.
     *
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void createKey(String keyName, boolean invalidatedByBiometricEnrollment) {
        // The enrolling flow for fingerprint. This is where you ask the user to set up fingerprint
        // for your flow. Use of keys is necessary if you need to know if the set of
        // enrolled fingerprints has changed.
        try {
            mKeyStore.load(null);
            // Set the alias of the entry in Android KeyStore where the key will appear
            // and the constrains (purposes) in the constructor of the Builder

            KeyGenParameterSpec.Builder builder = new KeyGenParameterSpec.Builder(keyName,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    // Require the user to authenticate with a fingerprint to authorize every use
                    // of the key
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7);

            // This is a workaround to avoid crashes on devices whose API level is < 24
            // because KeyGenParameterSpec.Builder#setInvalidatedByBiometricEnrollment is only
            // visible on API level +24.
            // Ideally there should be a compat library for KeyGenParameterSpec.Builder but
            // which isn't available yet.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                builder.setInvalidatedByBiometricEnrollment(invalidatedByBiometricEnrollment);
            }
            mKeyGenerator.init(builder.build());
            mKeyGenerator.generateKey();
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException
                | CertificateException | IOException e) {
            throw new RuntimeException(e);
        }
    }



private class PurchaseButtonClickListener implements View.OnClickListener {

    Cipher mCipher;
    String mKeyName;


    PurchaseButtonClickListener(Cipher cipher, String keyName) {
        mCipher = cipher;
        mKeyName = keyName;
    }

    //  @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View view) {

        user = editnomeUser.getText().toString();
        if (!user.equals("")) {
            if (controllaSetUpFingerprint()) {

                Log.d("getpw onclick", user);
                // Set up the crypto object for later. The object will be authenticated by use
                // of the fingerprint.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    try {
                        if (initCipher(mCipher, mKeyName)) {

                            // Show the fingerprint dialog. The user has the option to use the fingerprint with
                            // crypto, or you can fall back to using a server-side verified password.
                            FingerprintAuthenticationDialogFragment fragment
                                    = new FingerprintAuthenticationDialogFragment();

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                fragment.setCryptoObject(new FingerprintManager.CryptoObject(mCipher));
                            }
                            boolean useFingerprintPreference = mSharedPreferences
                                    .getBoolean(getString(R.string.use_fingerprint_to_authenticate_key),
                                            true);
                            if (useFingerprintPreference) {
                                fragment.setStage(
                                        FingerprintAuthenticationDialogFragment.Stage.FINGERPRINT);
                            } else {
                                fragment.setStage(
                                        FingerprintAuthenticationDialogFragment.Stage.PASSWORD);
                            }
                            fragment.show(myActivity.getFragmentManager(), DIALOG_FRAGMENT_TAG);
                        } else {
                            // This happens if the lock screen has been disabled or or a fingerprint got
                            // enrolled. Thus show the dialog to authenticate with their password first
                            // and ask the user if they want to authenticate with fingerprints in the
                            // future
                            FingerprintAuthenticationDialogFragment fragment
                                    = new FingerprintAuthenticationDialogFragment();

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                fragment.setCryptoObject(new FingerprintManager.CryptoObject(mCipher));
                            }
                            fragment.setStage(
                                    FingerprintAuthenticationDialogFragment.Stage.NEW_FINGERPRINT_ENROLLED);
                            fragment.show(myActivity.getFragmentManager(), DIALOG_FRAGMENT_TAG);
                        }
                    } catch (KeyPermanentlyInvalidatedException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            Toast.makeText(myActivity, "ho bisogno del nome utente. \nSpunta \"ricordami\"", Toast.LENGTH_SHORT).show();
        }
    }
}









    public void loginUtente(View v) {

      String user = editnomeUser.getText().toString();
        String password = editPassword.getText().toString();
        Log.i("login", user);
        Log.i("login", password);

//        Crypto crypto=new Crypto(getContext());

  //      String PWcontrollo =crypto.encrypt(password);

        Log.i("login", password);

        if(!user.equals("")&&!password.equals("")) {

            Cerca_pw_utente_in_DB cerca_pw_utente_in_db = new Cerca_pw_utente_in_DB(getContext());
            cerca_pw_utente_in_db.execute(user,password);

        }else{
            Toast.makeText(myActivity, "inserisci i dati per il login", Toast.LENGTH_SHORT).show();
        }

    }






// TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        /*if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }*/
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */

/*
private class Cerca_pw_utente_in_DB extends AsyncTask<String, Object, String> {
        String username, password, is_staff, is_superuser;
        //Activity myActivity;
        ArrayList<Utente> listUtenti=new ArrayList<>();
        //Utente utenteLogin=new Utente();
        //EditText passwordLogin;

        public Cerca_pw_utente_in_DB(){

        }

        @Override
        protected void onPreExecute() {
            // Check network connection.
            if (isNetworkConnected() == false) {
                // Cancel request.
                Log.i("Esito_Ricerca", "Not connected to the internet");
                cancel(true);
                return;
            }
        }

        @Override
        protected String doInBackground(String ...param) {
            this.username = param[0];
            // Stop if cancelled
            if (isCancelled()) {
                return null;
            }
            String Url = "http://lisiangelovpn.ddns.net/mybiblioteca/check_login.php?username="+username;

            //String url_ricera= creaUrl_ricerca(parametri[0],parametri[1],parametri[2],parametri[3],parametri[4]);

            Log.i("Esito_Ricerca ::url ", Url);
            try {
                HttpURLConnection connection = null;
                // Build Connection.
                try {
                    URL url = new URL(Url);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setReadTimeout(5000); // 5 seconds
                    connection.setConnectTimeout(5000); // 5 seconds
                } catch (MalformedURLException e) {
                    // Impossible: The only two URLs used in the app are taken from string resources.
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    // Impossible: "GET" is a perfectly valid request method.
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                int responseCode = connection.getResponseCode();
                if (responseCode != 200) {
                    Log.i("Esito_Ricerca", "ricerca request failed. Response Code: " + responseCode);
                    connection.disconnect();
                    return null;
                }
                Log.i("Esito_Ricerca", "ricerca request succesfully. Response Code: " + responseCode);
                // Read data from response.
                StringBuilder builder = new StringBuilder();
                BufferedReader responseReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line = responseReader.readLine();
                while (line != null) {
                    builder.append(line);
                    line = responseReader.readLine();
                }
                String responseString = builder.toString();
                Log.i("Esito_Ricerca", "Response String: " + responseString);
                //JSONObject responseJson = new JSONObject(responseString);
                // Close connection and return response code.
                connection.disconnect();

                return responseString;

            } catch (SocketTimeoutException e) {
                Log.w("Esito_Ricerca", "Connection timed out. Returning null");
                return null;
            } catch (IOException e) {
                Log.d("Esito_Ricerca", "IOException when connecting to Google Books API.");
                e.printStackTrace();
                return null;
            }
            //return myDataset;
        }
        @Override
        protected void onPostExecute(String responseString) {
            // listUtenti.clear();
            Log.i("log_tag", "parsing data on postExec " + responseString.toString());

            try {

                //  Log.i("log_tag", "dato da parsare in json "+responseJson.toString());
                JSONArray jArray = new JSONArray(responseString);
                for (int i = 0; i < jArray.length(); i++) {
                    Log.i("log_tag", "ciclo parsing data on postExec .." + i);

                    JSONObject json_data = jArray.getJSONObject(i);
                    Utente utente= new Utente();
                    utente.setNrtessera(json_data.getString("id"));
                    utente.setNome(json_data.getString("first_name"));
                    utente.setCognome(json_data.getString("last_name"));
                    utente.setEmail(json_data.getString("email"));
                    utente.setIs_staff(json_data.getInt("is_staff"));
                    utente.setIs_superuser(json_data.getInt("is_superuser"));
                    utente.setPassword(json_data.getString("password"));

                    // Log.i("log_tag", "datobject inserito " + obj.getData() );

                    listUtenti.add(utente);
                }

                editPasswordControll.setText(listUtenti.get(0).getPassword());
                Log.i("log_tag", "results... " + listUtenti.size());
                Log.i("log_tag", "password......"+editPasswordControll.getText().toString());
            } catch (JSONException e) {
                Log.e("log_tag", "Error parsing data " + e.toString());

            }

        }

        protected boolean isNetworkConnected() {
            ConnectivityManager mConnectivityManager = null;
            // Instantiate mConnectivityManager if necessary
            if (mConnectivityManager == null) {
                mConnectivityManager = (ConnectivityManager) myActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
            }
            // Is device connected to the Internet?
            NetworkInfo networkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                return true;
            } else {
                return false;
            }
        }
    }
*/
}

