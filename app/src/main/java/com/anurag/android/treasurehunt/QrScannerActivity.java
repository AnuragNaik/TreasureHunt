package com.anurag.android.treasurehunt;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.math.BigInteger;


public class QrScannerActivity extends Activity {
    TextView qrTextView;
    public static final int PERMISSION_CAMERA = 1;
    Button buttonProceed;
    EditText passwordText;
    int currentPosition;
    int finalPostion;
    SharedPreferences preferences;
    int startPosition;
    private static BigInteger ClientprivateKey=new BigInteger("21719");
    private static BigInteger ClientpublicKey=new BigInteger("59");
    private static BigInteger Clientmodulus= new BigInteger("37001");
    SharedPreferences.Editor editor;
    ImageView seeProgressImage;
    int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        preferences = getSharedPreferences("shared_preference", MODE_PRIVATE);
        editor = preferences.edit();
        currentPosition = preferences.getInt("current_position",0);
        startPosition = preferences.getInt("start_position",0);
        finalPostion = preferences.getInt("final_position",0);
        count = preferences.getInt("count", 0);

        setContentView(R.layout.activity_qr_scanner);
        passwordText = (EditText) findViewById(R.id.password);

        buttonProceed = (Button) findViewById(R.id.proceed_admin);
        qrTextView = (TextView) findViewById(R.id.content_main_scan_qr_output_amount);
        qrTextView.setText(preferences.getString("recent_clue", "Your hint will be coming here after scanning!"));
        qrTextView.setMovementMethod(new ScrollingMovementMethod());
        seeProgressImage = (ImageView) findViewById(R.id.see_progress);
        seeProgressImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QrScannerActivity.this, ProgressListActivity.class);
                startActivity(intent);
            }
        });

        buttonProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(passwordText.getText()!=null && !qrTextView.getText().toString().equals("Your hint will be coming here after scanning!")){

                    if(passwordText.getText().toString().equals("24710")) {

                        if (currentPosition + 1 == 5 && startPosition != 0) {
                            currentPosition = 0;//jump to beginning
                        } else {
                            currentPosition = (currentPosition + 1 + 6) % 6;
                        }
                        editor.putString("recent_clue", "Your hint will be coming here after scanning!");
                        int count = preferences.getInt("count", 0);
                        editor.putInt("count", count + 1);
                        editor.putInt("current_position", currentPosition);
                        editor.apply();
                        count = preferences.getInt("count", 0);
                        if (count == 5) {
                            currentPosition = 5;
                            editor.putInt("current_position", currentPosition);
                            editor.apply();
                        }
                        if(count >= 6) {
                            //we are at 5th arena, move to final arena
                            Intent intent = new Intent(QrScannerActivity.this, FinalActivity.class);
                            startActivity(intent);
                        } else{
                            startActivity(new Intent(QrScannerActivity.this, ProgressListActivity.class));
                        }
                    }else{
                        Toast.makeText(QrScannerActivity.this, "Incorrect Password!", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(QrScannerActivity.this, "Please follow the procedure!!", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Not Allowed!", Toast.LENGTH_SHORT).show();
        return;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void scan_qr_button_onClick(View view) {

        if (checkPermission(PERMISSION_CAMERA)) {
            scanBarcode();
        } else {
            requestForPermission(PERMISSION_CAMERA);
        }

    }

    private void scanBarcode() {
        new IntentIntegrator(this).initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                String QrOutput = preProcess(result.getContents());
                if (QrOutput != null) {
                    Log.d("MainActivity", "Scanned");
                    qrTextView.setText(QrOutput);
                    editor.putString("recent_clue", QrOutput);
                    editor.apply();
                } else {
                    Log.d("MainActivity", "Cancelled scan");
                }
            }
        }
    }

    public String preProcess(String encryptedString) {
        String string = DecryptData(encryptedString, ClientpublicKey, Clientmodulus);
        if(count == 5 && String.valueOf(string.charAt(0)).equals(String.valueOf(5))){
            editor.putInt("admin_approve", currentPosition);
            editor.apply();
            return string.substring(1);
        }

        if (String.valueOf(string.charAt(0)).equals(String.valueOf(5)) && count != 5) {
            //wrong attempt to scan
            Toast.makeText(QrScannerActivity.this, "Please complete previous tasks!", Toast.LENGTH_SHORT).show();
            return null;
        } else if (String.valueOf(string.charAt(0)).equals(String.valueOf(currentPosition))) {
            //correct attempt to scan
            editor.putInt("admin_approve", currentPosition);
            editor.apply();
            return string.substring(1);
        } else {
            Toast.makeText(QrScannerActivity.this, "Please complete previous tasks!", Toast.LENGTH_LONG).show();
            return null;
        }
    }



    // Permissions
    public boolean checkPermission(int permission) {

        if (ActivityCompat.checkSelfPermission(this, getPermission(permission)) != PackageManager.PERMISSION_GRANTED) {
            return false;
        } else {
            return true;
        }

    }


    public void requestForPermission(final int permission) {

        // BEGIN_INCLUDE(camera_permission_request)
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                getPermission(permission))) {


            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

            // Setting Dialog Message
            alertDialog.setMessage("Permission is needed to perform action...");

            // Setting Positive "Yes" Button
            alertDialog.setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                    ActivityCompat.requestPermissions(QrScannerActivity.this,
                            new String[]{getPermission(permission)},
                            permission);

                }
            });

            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // Write your code here to invoke NO event
                    dialog.dismiss();
                }
            });

            // Showing Alert Message
            alertDialog.show();
        } else {

            ActivityCompat.requestPermissions(this, new String[]{getPermission(permission)}, permission);

        }
        // END_INCLUDE(camera_permission_request)
    }


    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {


        if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Camera permission has been granted, preview can be displayed

            scanBarcode();

        } else {

            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

            // Setting Dialog Message
            alertDialog.setMessage("Permission not granted...");

            // Setting Positive "Yes" Button
            alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();

                }
            });
            // Showing Alert Message
            alertDialog.show();
        }

    }

    public String getPermission(int permis) {

        String permission = null;

        switch (permis) {
            case PERMISSION_CAMERA:
                permission = Manifest.permission.CAMERA;
                return permission;

        }
        return permission;
    }

    public static String EncryptData(String MyMessage,BigInteger privatekey,BigInteger modulus) {

        String EncryptedMessage = "";
        BigInteger mg;
        BigInteger encrypt;
        for(int i=0;i< MyMessage.length();i++) {
            if(i>0)
                EncryptedMessage+=" ";
            mg = intToBigint((int) MyMessage.charAt(i));
            encrypt = encrypt(mg,privatekey,modulus);
            EncryptedMessage+= encrypt.toString();
        }
        return EncryptedMessage;
    }

    public static String  DecryptData(String MyMessage,BigInteger publicKey,BigInteger modulus)
    {

        String DecryptedMessage = "";
        BigInteger mg;
        BigInteger decrypt;
        String[] tokens = MyMessage.split(" ");
        int ascii;
        char ch;
        for(int i=0;i< tokens.length; i++)
        {
            mg = intToBigint(Integer.parseInt(tokens[i]));
            decrypt = decrypt(mg,publicKey,modulus);
            ascii = decrypt.intValue();
            ch = (char) ascii;
            DecryptedMessage+=  ch;

        }

        return DecryptedMessage;
    }

    static BigInteger encrypt(BigInteger message,BigInteger privatekey,BigInteger modulus) {
        return message.modPow(privatekey, modulus);
    }

    static BigInteger decrypt(BigInteger encrypted,BigInteger publickey,BigInteger modulus) {
        return encrypted.modPow(publickey, modulus);
    }

    static BigInteger intToBigint(int x) {
        return BigInteger.valueOf(x);
    }

}




