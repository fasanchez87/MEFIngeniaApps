package com.ingeniapps.mef.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.View;
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
import com.bumptech.glide.Glide;
import com.ingeniapps.mef.R;
import com.ingeniapps.mef.adapter.MetaAdapter;
import com.ingeniapps.mef.adapter.TareaAdapter;
import com.ingeniapps.mef.beans.Meta;
import com.ingeniapps.mef.beans.Ruta;
import com.ingeniapps.mef.beans.Tarea;
import com.ingeniapps.mef.fragments.gps;
import com.ingeniapps.mef.sharedPreferences.gestionSharedPreferences;
import com.ingeniapps.mef.vars.vars;
import com.ingeniapps.mef.volley.ControllerSingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DetalleTarea extends AppCompatActivity
{
    private gestionSharedPreferences sharedPreferences;
    private ArrayList<Tarea> listadoTareas;
    public com.ingeniapps.mef.vars.vars vars;
    private RecyclerView recycler_view_tareas_detalle;
    private MetaAdapter tareaAdapter;
    LinearLayoutManager mLayoutManager;
    RelativeLayout rlEsperaTareas;
    LinearLayout ll_tareas;
    Context context;
    private boolean solicitando=false;
    //VERSION DEL APP INSTALADA
    private String versionActualApp;
    private static TextView ui_hot = null;
    private String codRuta;
    private String codMeta;
    private String desMeta;
    private String imgMeta;
    private TareaAdapter mAdapter;


    private NumberFormat numberFormat;

    private TextView desMetaDetalleTarea;
    private ImageView imgMetaDetalleTarea;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_tarea);
        sharedPreferences=new gestionSharedPreferences(this);
        listadoTareas=new ArrayList<Tarea>();
        vars=new vars();
        context = this;
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
                codMeta=null;
                desMeta=null;
                imgMeta=null;
            }

            else
            {
                codRuta=extras.getString("codRuta");
                codMeta=extras.getString("codMeta");
                desMeta=extras.getString("desMeta");
                imgMeta=extras.getString("imgMeta");
            }
        }

        desMetaDetalleTarea=(TextView) findViewById(R.id.desMetaDetalleTarea);
        imgMetaDetalleTarea=(ImageView) findViewById(R.id.imgMetaDetalleTarea);

        rlEsperaTareas=(RelativeLayout)findViewById(R.id.rlEsperaTareas);
        ll_tareas=(LinearLayout) findViewById(R.id.ll_tareas);
        recycler_view_tareas_detalle=(RecyclerView) findViewById(R.id.recycler_view_tareas_detalle);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new TareaAdapter(this,listadoTareas, new TareaAdapter.OnItemClickListener()
        {
            @Override
            public void onItemClick(Tarea tarea)
            {
                /*Intent i=new Intent(this, DetalleRuta.class);
                i.putExtra("codRuta",ruta.getCodRuta());
                i.putExtra("desRuta",ruta.getDesRuta());
                i.putExtra("desOrigen",ruta.getDesOrigen());
                i.putExtra("desDestino",ruta.getDesDestino());
                startActivity(i);*/


            }
        });

        recycler_view_tareas_detalle.setHasFixedSize(true);
        recycler_view_tareas_detalle.setLayoutManager(mLayoutManager);
        recycler_view_tareas_detalle.setItemAnimator(new DefaultItemAnimator());
        recycler_view_tareas_detalle.setAdapter(mAdapter);

        //VERSION APP
        try
        {
            versionActualApp=getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        }
        catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }

        WebServiceGetTareas(codRuta,codMeta);
    }



    private void WebServiceGetTareas(final String codRuta, final String codMeta)
    {
        listadoTareas.clear();
        String _urlWebService = vars.ipServer.concat("/ws/getTareas");

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

                                desMetaDetalleTarea.setText(desMeta);

                                if (TextUtils.isEmpty(imgMeta))
                                {
                                    imgMetaDetalleTarea.setImageResource(R.drawable.item_not_result);
                                }

                                else
                                {
                                    Glide.with(context).
                                            load(imgMeta).
                                            thumbnail(0.5f).into(imgMetaDetalleTarea);
                                }

                                JSONArray listaTareas = response.getJSONArray("tareas");

                                for (int i = 0; i < listaTareas.length(); i++)
                                {
                                    JSONObject jsonObject = (JSONObject) listaTareas.get(i);
                                    Tarea tarea = new Tarea();
                                    tarea.setCodRuta(jsonObject.getString("codRuta"));
                                    tarea.setCodMeta(jsonObject.getString("codMeta"));
                                    tarea.setCodTarea(jsonObject.getString("codTarea"));
                                    tarea.setType(jsonObject.getString("type"));
                                    tarea.setDesTarea(jsonObject.getString("desTarea"));
                                    tarea.setFecTarea(jsonObject.getString("fecha"));
                                    tarea.setIndCheck(jsonObject.getBoolean("indCheck"));
                                    listadoTareas.add(tarea);
                                }

                                rlEsperaTareas.setVisibility(View.GONE);
                                ll_tareas.setVisibility(View.VISIBLE);
                            }

                            else
                            {
                                rlEsperaTareas.setVisibility(View.GONE);
                                ll_tareas.setVisibility(View.VISIBLE);
                            }
                        }
                        catch (JSONException e)
                        {

                            rlEsperaTareas.setVisibility(View.GONE);
                            ll_tareas.setVisibility(View.VISIBLE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(DetalleTarea.this,R.style.AlertDialogTheme));
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
                        mAdapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        if (error instanceof TimeoutError)
                        {
                            rlEsperaTareas.setVisibility(View.GONE);
                            ll_tareas.setVisibility(View.VISIBLE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(DetalleTarea.this,R.style.AlertDialogTheme));
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

                            rlEsperaTareas.setVisibility(View.GONE);
                            ll_tareas.setVisibility(View.VISIBLE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(DetalleTarea.this,R.style.AlertDialogTheme));
                            builder
                                    .setMessage("Por favor, conectese a la red.")
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
                            rlEsperaTareas.setVisibility(View.GONE);
                            ll_tareas.setVisibility(View.VISIBLE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(DetalleTarea.this,R.style.AlertDialogTheme));
                            builder
                                    .setMessage("Error de autentificación en la red, favor contacte a su proveedor de servicios.")
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
                            rlEsperaTareas.setVisibility(View.GONE);
                            ll_tareas.setVisibility(View.VISIBLE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(DetalleTarea.this,R.style.AlertDialogTheme));
                            builder
                                    .setMessage("Error server, sin respuesta del servidor.")
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
                            rlEsperaTareas.setVisibility(View.GONE);
                            ll_tareas.setVisibility(View.VISIBLE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(DetalleTarea.this,R.style.AlertDialogTheme));
                            builder
                                    .setMessage("Error de red, contacte a su proveedor de servicios.")
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

                            rlEsperaTareas.setVisibility(View.GONE);
                            ll_tareas.setVisibility(View.VISIBLE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(DetalleTarea.this,R.style.AlertDialogTheme));
                            builder
                                    .setMessage("Error de conversión Parser, contacte a su proveedor de servicios.")
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
                headers.put("codMeta", codMeta);
                return headers;
            }
        };

        ControllerSingleton.getInstance().addToReqQueue(jsonObjReq, "");
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }


}