package com.example.ps_android_hh_activofijo_c66.view.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.example.ps_android_hh_activofijo_c66.R;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;

public class EditUserDialog extends DialogFragment {
    private RadioButton adminCheckbox;
    private RadioButton operadorCheckbox;
    private int position;
    private String instruction;

    public EditUserDialog(int position) {
        this.position = position;
    }

    public EditUserDialog(String instruction) {
        this.instruction = instruction;
    }

    public interface OnDialogButtonClickListener {
        void editarUsuario(int position, String usuario, String newPassword, String instruction, int rol) throws SQLException;
        void cancelarEdicion(int position);
    }

    private OnDialogButtonClickListener mListener;

    public void setListener(OnDialogButtonClickListener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View promptsView = inflater.inflate(R.layout.dialog_editar_usuario, container, false);

        EditText usuario = promptsView.findViewById(R.id.usuarioEt);
        EditText password = promptsView.findViewById(R.id.passEt);
        TextView title = promptsView.findViewById(R.id.titleDialog);
        adminCheckbox = promptsView.findViewById(R.id.adminCheckbox);
        operadorCheckbox = promptsView.findViewById(R.id.operadorCheckbox);

        Button guardar = promptsView.findViewById(R.id.guardar);
        Button cancelar = promptsView.findViewById(R.id.cancelar);

        AtomicBoolean isPassVisible = new AtomicBoolean(false);

        // Si se abre el diálogo para añádir un usuario nuevo
        if (instruction != null) {
            if (instruction.equals("add")) {
                title.setText(R.string.agregar_usuario);
            }
        }

        password.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (password.getRight() - password.getCompoundDrawablePadding())) {
                    isPassVisible.set(!isPassVisible.get());
                    if (isPassVisible.get()) {
                        Drawable eye = ContextCompat.getDrawable(requireContext(), R.drawable.eye);
                        password.setCompoundDrawablesWithIntrinsicBounds(null, null, eye, null);
                        password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    } else {
                        Drawable eyeOff = ContextCompat.getDrawable(requireContext(), R.drawable.eye_off);
                        password.setCompoundDrawablesWithIntrinsicBounds(null, null, eyeOff, null);
                        password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    }
                    return true;
                }
            }
            return false;
        });

        // Guardar cambios
        guardar.setOnClickListener(view -> {
            if (mListener != null) {
                int rol;
                if (adminCheckbox.isChecked()) {
                    rol = 2; //Administrador
                } else {
                    rol = 3; //Operador
                }
                if (usuario.getText().toString().trim().isEmpty() || password.getText().toString().isEmpty()) {
                    Toast.makeText(requireContext(), "Campos vacíos", Toast.LENGTH_SHORT).show();
                } else {
                    if (instruction != null) { //Se agrega un usuario nuevo
                        try {
                            mListener.editarUsuario(position, usuario.getText().toString(), password.getText().toString(), "add", rol);
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    } else { //Se edita un usuario existente
                        try {
                            mListener.editarUsuario(position, usuario.getText().toString(), password.getText().toString(), "edit", rol);
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    dismiss();
                }
            }
        });
        // Cerrar diálogo de edición
        cancelar.setOnClickListener(v -> {
            dismiss();
            mListener.cancelarEdicion(position);
        });

        return promptsView;
    }
}


