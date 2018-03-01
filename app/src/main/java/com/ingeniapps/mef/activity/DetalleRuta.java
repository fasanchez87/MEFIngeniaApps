package com.ingeniapps.mef.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ingeniapps.mef.R;
import com.ingeniapps.mef.adapter.MetaAdapter;
import com.ingeniapps.mef.beans.Meta;
import com.ingeniapps.mef.vars.vars;
import com.ingeniapps.mef.sharedPreferences.gestionSharedPreferences;
import com.ingeniapps.mef.volley.ControllerSingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DetalleRuta extends AppCompatActivity implements AdapterView.OnItemClickListener
{
    private gestionSharedPreferences sharedPreferences;
    private ArrayList<Meta> listadoMetas;
    public vars vars;
    private RecyclerView recycler_view_productos;
    private MetaAdapter metaAdapter;
    LinearLayoutManager mLayoutManager;
    RelativeLayout rlEsperaMetas;
    LinearLayout ll_espera_detalle_metas;
    ImageView not_found_productos;
    private int pagina;
    Context context;
    private boolean solicitando=false;
    //VERSION DEL APP INSTALADA
    private String versionActualApp;
    private static TextView ui_hot = null;
    private String codRuta;
    private String desRuta;
    private String desOrigen;
    private String desDestino;

    private GridView gridView;

    private NumberFormat numberFormat;

    private TextView ruta;
    private TextView descRuta;
    private TextView descOrigen;
    private TextView descDestino;


    public ImageView imagenDescripcionNoticia,botonIncrementarProducto,botonDecrementarProducto;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_ruta);
        sharedPreferences=new gestionSharedPreferences(DetalleRuta.this);
        listadoMetas=new ArrayList<Meta>();
        vars=new vars();
        context = this;
        pagina=0;
        numberFormat = NumberFormat.getNumberInstance(Locale.GERMAN);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //this line shows back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                finish();
            }
        });

        if (savedInstanceState == null)
        {
            Bundle extras = getIntent().getExtras();
            if (extras == null)
            {
                codRuta=null;
                desRuta=null;
                desOrigen=null;
                desDestino=null;
            }

            else
            {
                codRuta=extras.getString("codRuta");
                desRuta=extras.getString("desRuta");
                desOrigen=extras.getString("desOrigen");
                desDestino=extras.getString("desDestino");
            }
        }

        ruta=(TextView) findViewById(R.id.ruta);
        descDestino=(TextView) findViewById(R.id.desDestino);
        descOrigen=(TextView) findViewById(R.id.desOrigen);

        gridView = (GridView) findViewById(R.id.gridMetas);
        rlEsperaMetas=(RelativeLayout)findViewById(R.id.rlEsperaMetas);
        ll_espera_detalle_metas=(LinearLayout) findViewById(R.id.ll_espera_detalle_metas);
        //recycler_view_productos=(RecyclerView) getActivity().findViewById(R.id.recycler_view_productos);
        mLayoutManager = new LinearLayoutManager(this);
        //mAdapter = new ProductoAdapter(getActivity(),listadoProductos);
        //empresaAdapter = new EmpresaAdapter(getActivity(),listadoEmpresas);
        //gridView.setAdapter(empresaAdapter);
        gridView.setOnItemClickListener(this);

        //VERSION APP
        try
        {
            versionActualApp=getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        }
        catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }


    }

    @Override
    protected void onResume()
    {
        super.onResume();
        WebServiceGetMetas(codRuta);
    }

    private void showGrid(JSONArray listaEmpresas)
    {


        for (int i=0; i<listaEmpresas.length(); i++)
        {
            JSONObject jsonObject = null;
            try
            {
                jsonObject = (JSONObject) listaEmpresas.get(i);
                Meta meta = new Meta();
                meta.setImgMeta(jsonObject.getString("imgMeta"));
                meta.setCodRuta(jsonObject.getString("codRuta"));
                meta.setCodMeta(jsonObject.getString("codMeta"));
                meta.setDesMeta(jsonObject.getString("desMeta"));
                meta.setPorMeta(jsonObject.getString("porMeta"));
                meta.setColMeta(jsonObject.getString("colMeta"));
                listadoMetas.add(meta);
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

        //Creating GridViewAdapter Object
        MetaAdapter gridViewAdapter = new MetaAdapter(this,listadoMetas);
        //Adding adapter to gridview
        gridView.setAdapter(gridViewAdapter);
    }

    private void WebServiceGetMetas(final String codRuta)
    {
        listadoMetas.clear();
        String _urlWebService = vars.ipServer.concat("/ws/getMetas");

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, _urlWebService, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        try
                        {
                            if(response.getBoolean("status"))
                            {
                                ruta.setText(desRuta);
                                descOrigen.setText(desOrigen);
                                descDestino.setText(desDestino);
                                JSONArray listaEmpresas = response.getJSONArray("metas");
                                showGrid(listaEmpresas);
                                rlEsperaMetas.setVisibility(View.GONE);
                                ll_espera_detalle_metas.setVisibility(View.VISIBLE);
                            }

                            else
                            {
                                rlEsperaMetas.setVisibility(View.GONE);
                                ll_espera_detalle_metas.setVisibility(View.VISIBLE);
                            }
                        }
                        catch (JSONException e)
                        {
                            rlEsperaMetas.setVisibility(View.GONE);
                            ll_espera_detalle_metas.setVisibility(View.VISIBLE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(DetalleRuta.this,R.style.AlertDialogTheme));
                            builder
                                    .setMessage(e.getMessage().toString())
                                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id)
                                        {
                                        }
                                    }).show();

                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        if (error instanceof TimeoutError)
                        {
                            rlEsperaMetas.setVisibility(View.GONE);
                            ll_espera_detalle_metas.setVisibility(View.VISIBLE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(DetalleRuta.this,R.style.AlertDialogTheme));
                            builder
                                    .setMessage("Error de conexión, sin respuesta del servidor.")
                                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id)
                                        {
                                        }
                                    }).show();
                        }

                        else

                        if (error instanceof NoConnectionError)
                        {
                            rlEsperaMetas.setVisibility(View.GONE);
                            ll_espera_detalle_metas.setVisibility(View.VISIBLE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(DetalleRuta.this,R.style.AlertDialogTheme));
                            builder
                                    .setMessage("Error de conexión, sin respuesta del servidor.")
                                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id)
                                        {
                                        }
                                    }).show();
                        }

                        else

                        if (error instanceof AuthFailureError)
                        {
                            rlEsperaMetas.setVisibility(View.GONE);
                            ll_espera_detalle_metas.setVisibility(View.VISIBLE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(DetalleRuta.this,R.style.AlertDialogTheme));
                            builder
                                    .setMessage("Error de conexión, sin respuesta del servidor.")
                                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id)
                                        {
                                        }
                                    }).show();
                        }

                        else

                        if (error instanceof ServerError)
                        {
                            rlEsperaMetas.setVisibility(View.GONE);
                            ll_espera_detalle_metas.setVisibility(View.VISIBLE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(DetalleRuta.this,R.style.AlertDialogTheme));
                            builder
                                    .setMessage("Error de conexión, sin respuesta del servidor.")
                                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id)
                                        {
                                        }
                                    }).show();
                        }

                        else

                        if (error instanceof NetworkError)
                        {
                            rlEsperaMetas.setVisibility(View.GONE);
                            ll_espera_detalle_metas.setVisibility(View.VISIBLE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(DetalleRuta.this,R.style.AlertDialogTheme));
                            builder
                                    .setMessage("Error de conexión, sin respuesta del servidor.")
                                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id)
                                        {
                                        }
                                    }).show();
                        }

                        else

                        if (error instanceof ParseError)
                        {
                            rlEsperaMetas.setVisibility(View.GONE);
                            ll_espera_detalle_metas.setVisibility(View.VISIBLE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(DetalleRuta.this,R.style.AlertDialogTheme));
                            builder
                                    .setMessage("Error de conexión, sin respuesta del servidor.")
                                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id)
                                        {
                                        }
                                    }).show();
                        }
                    }
                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError
            {
                HashMap<String, String> headers = new HashMap <String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("WWW-Authenticate", "xBasic realm=".concat(""));
                headers.put("codRuta", codRuta);
                return headers;
            }
        };

        ControllerSingleton.getInstance().addToReqQueue(jsonObjReq, "");
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        Meta meta = (Meta) parent.getItemAtPosition(position);
        Intent intent = new Intent(this, DetalleTarea.class);
        intent.putExtra("codMeta",meta.getCodMeta());
        intent.putExtra("codRuta",meta.getCodRuta());
        intent.putExtra("codRuta",meta.getCodRuta());
        intent.putExtra("desMeta",meta.getDesMeta());
        intent.putExtra("imgMeta",meta.getImgMeta());
        startActivity(intent);
    }
}
