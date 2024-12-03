package com.educodedev.ejercicioteoria5recyclerview;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.educodedev.ejercicioteoria5recyclerview.adapters.ProductoAdapter;
import com.educodedev.ejercicioteoria5recyclerview.configuraciones.Constantes;
import com.educodedev.ejercicioteoria5recyclerview.models.ProductoModel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.educodedev.ejercicioteoria5recyclerview.databinding.ActivityMainBinding;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.NumberFormat;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private ArrayList<ProductoModel> listaCompra;
    private ProductoAdapter adapter;
    private RecyclerView.LayoutManager lm;
    private SharedPreferences spDatos;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        spDatos = getSharedPreferences(Constantes.DATOS, MODE_PRIVATE);
        gson = new Gson();

        listaCompra = new ArrayList<>();

        adapter = new ProductoAdapter(listaCompra,
                R.layout.product_view_holder,
                this);

        lm = new GridLayoutManager(this, 1);

        binding.contentMain.contenedor.setLayoutManager(lm);
        binding.contentMain.contenedor.setAdapter(adapter);

        leerDatos();

               binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                crearProducto().show();
            }
        });
    }

    private void leerDatos() {
        if(spDatos.contains(Constantes.LISTA_PRODUCTOS)){
            String productosJSON = spDatos.getString(Constantes.LISTA_PRODUCTOS, "[]");
            Type tipoDato = new TypeToken<ArrayList<ProductoModel>>(){}.getType();
            ArrayList<ProductoModel> auxiliarTemporal = gson.fromJson(productosJSON,tipoDato);

            listaCompra.clear();
            listaCompra.addAll(auxiliarTemporal);
            adapter.notifyItemRangeInserted(0,listaCompra.size());

        }

    }

    private AlertDialog crearProducto(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Agregar producto.");
        builder.setCancelable(false);

        View productViewModel =
                LayoutInflater.from(this).inflate(R.layout.product_view_model,null);
        TextView lblTotal =
                productViewModel.findViewById(R.id.lbTotalProductoViewModel);
        EditText txtNombre =
                productViewModel.findViewById(R.id.txtNombreProductoViewModel);
        EditText txtCantidad =
                productViewModel.findViewById(R.id.txtCantidadProductoViewModel);
        EditText txtPrecio =
                productViewModel.findViewById(R.id.txtPrecioProductoViewModel);

        builder.setView(productViewModel);

        TextWatcher vigilante = new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                try{
                    int cantidad = Integer.parseInt(txtCantidad.getText().toString());
                    float precio = Float.parseFloat(txtPrecio.getText().toString());
                    float total = cantidad * precio;

                    NumberFormat numberFormat =
                            NumberFormat.getCurrencyInstance();
                    lblTotal.setText(numberFormat.format(total));
                }catch (NumberFormatException ignored){
                }
            }
        };
        txtCantidad.addTextChangedListener(vigilante);
        txtPrecio.addTextChangedListener(vigilante);


        builder.setNegativeButton("CANCELAR", null);
        builder.setPositiveButton("AGREGAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String nombre = txtNombre.getText().toString();
                String cantidad = txtCantidad.getText().toString();
                String precio = txtPrecio.getText().toString();
                
                if (!nombre.isEmpty() && !cantidad.isEmpty() && !
                        precio.isEmpty()) {

                    ProductoModel producto = new ProductoModel(nombre,
                            Integer.parseInt(cantidad),
                    Float.parseFloat(precio));

                            listaCompra.add(0,producto);
                            adapter.notifyItemInserted(0);

                    guardarInformacion();

                } else {
                    Toast.makeText(MainActivity.this, "FALTAN DATOS.", Toast.LENGTH_SHORT).show();
                }
            }

            private void guardarInformacion() {

                String productosJSON = gson.toJson(listaCompra);
                SharedPreferences.Editor editor = spDatos.edit();
                editor.putString(Constantes.LISTA_PRODUCTOS, productosJSON);
                editor.apply();
            }
        });
                return builder.create();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(("LISTA"), listaCompra);
    }

    @Override  //recupera los datos
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        listaCompra.clear();
        listaCompra.addAll((ArrayList<ProductoModel>)
                savedInstanceState.getSerializable("LISTA"));
        adapter.notifyItemRangeInserted(0, listaCompra.size());
    }
}