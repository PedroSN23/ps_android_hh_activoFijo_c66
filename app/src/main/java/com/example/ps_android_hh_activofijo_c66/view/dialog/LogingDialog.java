package com.example.ps_android_hh_activofijo_c66.view.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.ps_android_hh_activofijo_c66.R;
import com.example.ps_android_hh_activofijo_c66.model.clases.Usuario;
import com.example.ps_android_hh_activofijo_c66.model.database.InterfazBD;
import com.example.pp_android_handheld_library.model.resources.ColorEnum;
import com.example.ps_android_hh_activofijo_c66.model.database.InterfazBD;

import org.mindrot.jbcrypt.BCrypt;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

import io.github.muddz.styleabletoast.StyleableToast;

public class LogingDialog extends AppCompatActivity {
    private static final long delay = 600000L; //600000L;
    public static final int ACTIVITY_CODE = 737;
    private final AtomicBoolean timerRunning = new AtomicBoolean(false);
    private final AtomicBoolean loginIsVisible = new AtomicBoolean(false);
    private Context context;
    private Timer timer;
    private boolean firstTimeRunning = true;
    private AlertDialog alertDialog;
    private Button button1;
    private Button button2;
    private final Pattern p = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
    }

    @Override
    public void onBackPressed() {
        exit(Activity.RESULT_OK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTIVITY_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                firstTimeRunning = true;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!firstTimeRunning) {
            createLogingDialog();
        }
        firstTimeRunning = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopTimer();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (!loginIsVisible.get()) {
            stopTimer();
            startTimer();
        }
        return super.dispatchTouchEvent(ev);
    }

    public void createLogingDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View promptsView = inflater.inflate(R.layout.dialog_autentificar_administrador, null);

        alertDialog = new AlertDialog.Builder(context).setView(promptsView).create();

        final EditText usuario = promptsView.findViewById(R.id.admUser);
        final EditText password = promptsView.findViewById(R.id.admPass);

        button1 = promptsView.findViewById(R.id.button1);
        button2 = promptsView.findViewById(R.id.button2);

        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setOnShowListener(dialogInterface -> {
            button1.setOnClickListener(view -> {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);


                if (verificarUsuario(usuario.getText().toString(), password.getText().toString())) {
                    //correcto
                } else {
                    new StyleableToast.Builder(this).text("Error de autenticación")
                            .textColor(ContextCompat.getColor(this, R.color.white))
                            .backgroundColor(ColorEnum.status_red.getCode())
                            .show();
                }
            });
            alertDialog.setCancelable(false);
            Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alertDialog.show();
        });

    }

    private boolean verificarUsuario(String usuario, String pass) {
        boolean resultado = false;

        InterfazBD interfazDb = new InterfazBD(this);
        Usuario user = interfazDb.obtenerUsuario(usuario);

        if (BCrypt.checkpw(pass, user.getPass())) {
            Log.d("pass", "¡La contraseña coincide!");
        } else {
            Log.d("pass", "¡La contraseña NO coincide!");
        }
        return resultado;
    }

    /*public void createLogingDialog() {
        if (!loginIsVisible.get()) {
            stopTimer();
            loginIsVisible.set(true);
            View promptsView = View.inflate(context, R.layout.dialog_autentificar_usuario, null);
            alertDialog = new AlertDialog.Builder(context)
                    .setView(promptsView)
                    .create();
            final EditText usuario = promptsView.findViewById(R.id.usrEt);
            if (usuarioglobal != null && usuarioglobal.getId() != -1) {
                usuario.setText(usuarioglobal.getUsuario());
                usuario.setEnabled(false);
            }
            final EditText password = promptsView.findViewById(R.id.passEt);
            button1 = promptsView.findViewById(R.id.butLog);
            button2 = promptsView.findViewById(R.id.butCan);
            alertDialog.setOnShowListener(dialogInterface -> {
                button1.setOnClickListener(view -> {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
                    String psw = BCrypt.hashpw(password.getText().toString(), "$2a$07$asxx54ahjppf45sd87a5a4dDDGsystemdev$");
                    if (this.usuarioglobal != null) {
                        if (usuarioglobal.getPassword().compareTo(psw) == 0) {
                            loginIsVisible.set(false);
                            usuarioglobal.resetTries();
                            alertDialog.dismiss();
                            startTimer();
                            return;
                        } else {
                            Toast.makeText(context, "Contraseña inválida", Toast.LENGTH_LONG).show();
                            if (usuarioglobal.addTries() >= 3) {
                                if (mainMenuClass) {
                                    finish();
                                    System.exit(0);
                                } else {
                                    loginIsVisible.set(false);
                                    alertDialog.dismiss();
                                    exit(Activity.RESULT_CANCELED);
                                }
                            }
                            return;
                        }
                    }
                    this.usuarioglobal = new Usuario(usuario.getText().toString(), psw);
                    if (usuarioglobal.getUsuario().length() > 0 && psw.length() > 0) {
                        if (usuarioglobal.getUsuario().compareTo("root") == 0 && psw.compareTo("$2a$07$asxx54ahjppf45sd87a5auxGdVEg3/9g.C9SF13y/o0oH3r9vFy6q") == 0) {
                            usuarioglobal.setId(0);
                            usuarioglobal.setNombre("Super Usuario");
                            usuarioglobal.setRol(0);
                            loginIsVisible.set(false);
                            alertDialog.dismiss();
                            startTimer();
                        } else {
                            button1.setEnabled(false);
                            button2.setEnabled(false);
                            new LoginUsuario(LogingDialog.this, usuarioglobal).execute();
                        }
                    } else {
                        Toast.makeText(context, "El usuario y la contraseña no deben estar vacìos", Toast.LENGTH_SHORT).show();
                    }
                });
                button2.setOnClickListener(view -> {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
                    if (mainMenuClass) {
                        finish();
                        System.exit(0);
                    } else {
                        loginIsVisible.set(false);
                        alertDialog.dismiss();
                        exit(Activity.RESULT_CANCELED);
                    }
                });
            });
            alertDialog.setCancelable(false);
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alertDialog.show();
        }
    }*/

    public void exit(int result) {
        Intent returnItent = new Intent();
        setResult(result, returnItent);
        finish();
    }

    public void startTimer() {
        if (!timerRunning.get()) {
            timerRunning.set(true);
            timer = new Timer();
            timer.schedule(new Inactividad(), delay);
        }
    }

    public void stopTimer() {
        if (timerRunning.get()) {
            timerRunning.set(false);
            timer.cancel();
            timer.purge();
        }
    }

    class Inactividad extends TimerTask {
        @Override
        public void run() {
            timerRunning.set(false);
            if (!loginIsVisible.get()) {
                LogingDialog.this.runOnUiThread(() -> {
                    Log.d("PPPP", "Inactividad");
                    createLogingDialog();
                });
            }
        }
    }
}
