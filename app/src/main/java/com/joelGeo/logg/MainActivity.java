package com.joelGeo.logg;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

 AppCompatButton b1;
 EditText e1,e2;
 Button btnSignIn,addPlayer;
 FirebaseAuth auth;
 FirebaseDatabase database;
 GoogleSignInClient googleSignInClient;
 int RC_SIGN_IN =30;
 Dialog loading;

  @Override
 protected void onCreate(Bundle savedInstanceState) {
  super.onCreate(savedInstanceState);
  ///****************//
  loading=new Dialog((this));
  loading.setContentView(R.layout.loading);
  loading.setCancelable(false);
  loading.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);

  EdgeToEdge.enable(this);
  setContentView(R.layout.activity_main);
  SharedPreferences p = getSharedPreferences("Log",MODE_PRIVATE);
  String userName=p.getString("user",null);
  if(userName!=null)
  {
   Intent i=new Intent(getApplicationContext(), Project.class);
   startActivity(i);
  }
  btnSignIn=findViewById(R.id.google);
  auth=FirebaseAuth.getInstance();
  database=FirebaseDatabase.getInstance();
  GoogleSignInOptions gso= new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
          .requestIdToken(getString(R.string.default_web_client_id))
          .requestEmail()
          .build();

  googleSignInClient= GoogleSignIn.getClient(this,gso);
  btnSignIn.setOnClickListener(new View.OnClickListener() {
   @Override
   public void onClick(View view) {

      signIn();
   }
  });

  b1=(AppCompatButton) findViewById(R.id.log);
  e1=(EditText)findViewById((R.id.email));
  e2=(EditText)findViewById((R.id.pass));
  b1.setOnClickListener(new View.OnClickListener() {
   @Override
   public void onClick(View view) {
    String userName = e1.getText().toString();
    String passw = e2.getText().toString();
    if (userName.equals("admin") && passw.equals("1234")) {
     SharedPreferences p = getSharedPreferences("Log", MODE_PRIVATE);
     SharedPreferences.Editor editor = p.edit();
     editor.putString("user", "admin");
     editor.apply();
     Intent i = new Intent(getApplicationContext(), Project.class);
     startActivity(i);
    } else if (userName.equals("") || passw.equals("")) {
     Toast.makeText(getApplicationContext(), "Enter the field values !!", Toast.LENGTH_LONG).show();
    } else {
     Toast.makeText(getApplicationContext(), "Wrong Password or Username!!", Toast.LENGTH_LONG).show();
    }
   }
  });
 }

 private void signIn() {

   Intent intent = googleSignInClient.getSignInIntent();
    startActivityForResult(intent,RC_SIGN_IN);

  }

 @Override
 protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
  super.onActivityResult(requestCode, resultCode, data);
  Task<GoogleSignInAccount> task=GoogleSignIn.getSignedInAccountFromIntent(data);


     try {
      GoogleSignInAccount account= task.getResult(ApiException.class);
      firebaseAuth(account.getIdToken());
     } catch (ApiException e) {
         Toast.makeText(this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
     }



 }

 private void firebaseAuth(String idToken) {

   loading.show();

  AuthCredential credential= GoogleAuthProvider.getCredential(idToken,null);
  auth.signInWithCredential(credential)
          .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
           @Override
           public void onComplete(@NonNull Task<AuthResult> task) {

              Toast.makeText(MainActivity.this,"        Welcome   \n 'Account Created' ",Toast.LENGTH_LONG).show();
            FirebaseUser user= auth.getCurrentUser();
            HashMap<String,Object> map = new HashMap<>();
            map.put("name",user.getDisplayName());
            map.put("email",user.getEmail());
            map.put("profile",user.getPhotoUrl().toString());

            database.getReference().child("googleAuth").child(user.getUid())
                    .setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                     @Override
                     public void onSuccess(Void unused) {

                      loading.dismiss();

                      Intent intent =new Intent(MainActivity.this,Project.class);
                      startActivity(intent);
                      finish();

                     }
                    });
           }
          });

  }
}