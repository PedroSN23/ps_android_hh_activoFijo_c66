package com.example.ps_android_hh_activofijo_c66.view.fragment;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ps_android_hh_activofijo_c66.R;
import com.example.ps_android_hh_activofijo_c66.model.clases.DatabaseConf;
import com.example.ps_android_hh_activofijo_c66.model.clases.Usuario;
import com.example.ps_android_hh_activofijo_c66.model.database.ConexionMysql;
import com.example.ps_android_hh_activofijo_c66.model.database.InterfazBD;
import com.example.ps_android_hh_activofijo_c66.view.adapter.CustomDividerItemDecoration;
import com.example.ps_android_hh_activofijo_c66.view.adapter.UsuariosAdapter;
import com.example.ps_android_hh_activofijo_c66.view.dialog.EditUserDialog;
import com.example.pp_android_handheld_library.model.resources.ColorEnum;
import com.google.android.material.snackbar.Snackbar;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import io.github.muddz.styleabletoast.StyleableToast;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class UsuariosFragment extends Fragment implements EditUserDialog.OnDialogButtonClickListener {
    private UsuariosAdapter usuariosAdapter;
    private RelativeLayout relativeLayoutUsuarios;
    private EditUserDialog editUserDialog;
    private ArrayList<Usuario> usuarios;
    private UsuariosFragment context;
    private InterfazBD InterfazBD;
    private RecyclerView listaUsuarios;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.content_usuarios, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = this;
        listaUsuarios = view.findViewById(R.id.listaUsuarios);
        Button agregarUsuario = view.findViewById(R.id.agregarUsuario);
        relativeLayoutUsuarios = view.findViewById(R.id.contenedorPrincipal);

        InterfazBD = new InterfazBD(requireContext());
        usuarios = InterfazBD.obtenerUsuarios();

        usuariosAdapter = new UsuariosAdapter(usuarios);
        listaUsuarios.setLayoutManager(new GridLayoutManager(requireContext(), 1));
        CustomDividerItemDecoration dividerItemDecoration = new CustomDividerItemDecoration(requireContext(), 2, ColorEnum.azulAstlix.getCode());
        listaUsuarios.addItemDecoration(dividerItemDecoration);
        listaUsuarios.setAdapter(usuariosAdapter);

        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            int position;

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @SuppressWarnings("deprecation")
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                position = viewHolder.getAdapterPosition();
                final Usuario item = usuariosAdapter.getData().get(position);

                if (item.getUsuario().equals("root")) {
                    usuariosAdapter.notifyItemChanged(position);
                } else {
                    if (direction == ItemTouchHelper.LEFT) {
                        AtomicBoolean deshacer = new AtomicBoolean(false);
                        usuariosAdapter.removeItem(position);

                        Snackbar snackbar = Snackbar.make(relativeLayoutUsuarios, "Usuario eliminado.", Snackbar.LENGTH_LONG);
                        snackbar.setAction("DESHACER", view -> {
                            usuariosAdapter.restoreItem(item, position);
                            listaUsuarios.scrollToPosition(position);
                            deshacer.set(true);
                        });

                        snackbar.setCallback(new Snackbar.Callback() {
                            @Override
                            public void onDismissed(Snackbar transientBottomBar, int event) {
                                super.onDismissed(transientBottomBar, event);
                                if (!deshacer.get()) {
                                    Log.d("cont", "onDismissed: " + item.getUsuario());
                                    //Eliminar usuario
                                    InterfazBD.eliminarUsuario(item.getUsuario());
                                }
                            }
                        });

                        snackbar.setActionTextColor(Color.YELLOW);
                        snackbar.show();

                    } else {
                        editUserDialog = new EditUserDialog(position);
                        editUserDialog.setListener(context);
                        editUserDialog.show(getChildFragmentManager(), "EditUserDialog");
                    }
                }
            }

            @SuppressWarnings("ConstantConditions")
            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftBackgroundColor(ContextCompat.getColor(getContext(), ColorEnum.sobrante.getCode()))
                        .addSwipeRightBackgroundColor(ContextCompat.getColor(getContext(), ColorEnum.faltante.getCode()))
                        .addSwipeLeftActionIcon(R.drawable.ic_trash).addSwipeRightActionIcon(R.drawable.edit)
                        .create()
                        .decorate();

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

        Usuario usuarioActivo = InterfazBD.obtenerPermiso();
        if (usuarioActivo.getRol() == 1 || usuarioActivo.getRol() == 2) {
            ItemTouchHelper helper = new ItemTouchHelper(callback);
            helper.attachToRecyclerView(listaUsuarios);
        }

        if (usuarioActivo.getRol() == 3) {
            agregarUsuario.setEnabled(false);
        }
        agregarUsuario.setOnClickListener(v -> {
            editUserDialog = new EditUserDialog("add");
            editUserDialog.setListener(context);
            editUserDialog.show(getChildFragmentManager(), "EditUserDialog");
        });
    }

    // Al guardar cambios de un usuario
    @Override
    public void editarUsuario(int position, String newUsuario, String newPass, String instruction, int rol) throws SQLException {
        String salt = BCrypt.gensalt();
        String newHashedPassword = BCrypt.hashpw(newPass, salt);
        String usuario = usuariosAdapter.getData().get(position).getUsuario();

        if (instruction != null) {
            if (instruction.equals("add")) {
                InterfazBD.insertarUsuario(newUsuario, newHashedPassword, salt, rol);
            } else {
                InterfazBD.modificarUsuario(usuario, newUsuario, newHashedPassword, salt, rol);
            }
        }

        usuarios = InterfazBD.obtenerUsuarios();
        usuariosAdapter = new UsuariosAdapter(usuarios);
        listaUsuarios.setLayoutManager(new GridLayoutManager(requireContext(), 1));
        listaUsuarios.setAdapter(usuariosAdapter);
        usuariosAdapter.notifyItemChanged(position);

        new StyleableToast.Builder(requireContext()).text("Cambios guardados")
                .textColor(ContextCompat.getColor(requireContext(), ColorEnum.white.getCode()))
                .backgroundColor(ContextCompat.getColor(requireContext(), ColorEnum.bien.getCode()))
                .show();

    }

    @Override
    public void cancelarEdicion(int position) {
        usuariosAdapter.notifyItemChanged(position);
    }
}


