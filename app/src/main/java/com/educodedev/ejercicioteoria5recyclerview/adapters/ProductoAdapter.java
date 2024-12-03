package com.educodedev.ejercicioteoria5recyclerview.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.educodedev.ejercicioteoria5recyclerview.R;
import com.educodedev.ejercicioteoria5recyclerview.configuraciones.Constantes;
import com.educodedev.ejercicioteoria5recyclerview.models.ProductoModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.ProductoVH> {
    private List<ProductoModel> objects;
    private int fila;
    private Context context;
    private SharedPreferences spDatos;
    private Gson gson;

    public ProductoAdapter(List<ProductoModel> objects, int fila, Context context) {
        this.objects = objects;
        this.fila = fila;
        this.context = context;
        
        this.spDatos = context.getSharedPreferences(Constantes.DATOS,
                Context.MODE_PRIVATE);
        gson = new Gson();

       /*
       // CREAR EL JSON
        String json = gson.toJson(objects);
        //Create Update Delete

        // CREAR EL OBJETO

        Type tipo = new TypeToken<ArrayList<ProductoModel>>() {
        }.getType();

        ArrayList<ProductoModel> auxiliar = gson.fromJson(json, tipo);
                //Read
                */

    }

    @NonNull
    @Override
    public ProductoVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View productoView = LayoutInflater.from(context).inflate(fila, null);

        productoView.setLayoutParams(
                new RecyclerView.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));

        return new ProductoVH(productoView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductoVH holder, int position) {

        ProductoModel producto = objects.get(position);

        holder.lblNombre.setText(producto.getNombre());
        holder.txtCantidad.setText(String.valueOf(producto.getCantidad()));
        holder.btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmarBorrado(producto).show();
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modificarProducto(producto).show();
            }
        });

        holder.txtCantidad.addTextChangedListener(new TextWatcher() {
            boolean cero = false;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (!s.toString().isEmpty() && s.charAt(0) == '0') {
                    cero = true;
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                try {
                    if (cero && s.toString().length() > 1) {
                        holder.txtCantidad.setText(s.toString().substring(0, 1));
                        holder.txtCantidad.setSelection(1);
                        cero = false;
                    }

                    int cantidad = Integer.parseInt(s.toString());
                    producto.setCantidad(cantidad);

                } catch (NumberFormatException e) {
                    holder.txtCantidad.setText("0");
                }
            }
        });
    }


    @Override
    public int getItemCount() {

        return objects.size();
    }

    private androidx.appcompat.app.AlertDialog modificarProducto(ProductoModel producto) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
        builder.setTitle("Modificar producto.");
        builder.setCancelable(false);

        View productViewModel =
                LayoutInflater.from(context).inflate(R.layout.product_view_model, null);
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
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    int cantidad = Integer.parseInt(txtCantidad.getText().toString());
                    float precio = Float.parseFloat(txtPrecio.getText().toString());
                    float total = cantidad * precio;

                    NumberFormat numberFormat =
                            NumberFormat.getCurrencyInstance();
                    lblTotal.setText(numberFormat.format(total));
                } catch (NumberFormatException ignored) {
                }
            }
        };
        txtCantidad.addTextChangedListener(vigilante);
        txtPrecio.addTextChangedListener(vigilante);

        txtNombre.setText(producto.getNombre());
        txtCantidad.setText(String.valueOf(producto.getCantidad()));
        txtPrecio.setText(String.valueOf(producto.getPrecio()));

        builder.setNegativeButton("CANCELAR", null);
        builder.setPositiveButton("MODIFICAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String nombre = txtNombre.getText().toString();
                String cantidad = txtCantidad.getText().toString();
                String precio = txtPrecio.getText().toString();

                if (!nombre.isEmpty() && !cantidad.isEmpty() && !
                        precio.isEmpty()) {

                    producto.setNombre(txtNombre.getText().toString());
                    producto.setCantidad(Integer.parseInt(txtCantidad.getText().toString()));
                    producto.setPrecio(Float.parseFloat(txtPrecio.getText().toString()));
                    producto.actualizarTotal();

                    notifyItemChanged(objects.indexOf(producto));
                    guardarInformacion();

                } else {
                    Toast.makeText(context, "FALTAN DATOS.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return builder.create();
    }


    private AlertDialog confirmarBorrado(ProductoModel producto) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("¿SEGURO?");
        builder.setCancelable(true);

        TextView textView = new TextView(context);
        textView.setText(" Esta acción no se puede deshacer.");
        textView.setTextColor(Color.RED);
        textView.setTextSize(24);

        builder.setView(textView);

        builder.setNegativeButton("CANCELAR", null);
        builder.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int posicion = objects.indexOf(producto);
                objects.remove(producto);
                notifyItemRemoved(posicion);
                guardarInformacion();
            }


        });
        return builder.create();
    }

    public class ProductoVH extends RecyclerView.ViewHolder {

        TextView lblNombre;
        EditText txtCantidad;
        ImageButton btnEliminar;

        public ProductoVH(@NonNull View itemView) {
            super(itemView);
            lblNombre = itemView.findViewById(R.id.lblNopmbreProductViewHolder);
            txtCantidad = itemView.findViewById(R.id.txtCantidadProducViewHolder);
            btnEliminar = itemView.findViewById(R.id.btnEliminarProductViewHolder);

        }

    }

    private void guardarInformacion() {
        String productosJSON = gson.toJson(objects);
        SharedPreferences.Editor editor = spDatos.edit();
        editor.putString(Constantes.LISTA_PRODUCTOS, productosJSON);
        editor.apply();
    }
}
