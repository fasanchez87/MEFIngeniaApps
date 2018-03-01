package com.ingeniapps.mef.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

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
import com.ingeniapps.mef.R;
import com.ingeniapps.mef.activity.DetalleRuta;
import com.ingeniapps.mef.adapter.RutaAdapter;
import com.ingeniapps.mef.beans.Ruta;
import com.ingeniapps.mef.vars.vars;
import com.ingeniapps.mef.sharedPreferences.gestionSharedPreferences;
import com.ingeniapps.mef.volley.ControllerSingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class gps extends Fragment
{
    private gestionSharedPreferences sharedPreferences;
    private ArrayList<Ruta> listadoRutas;
    public vars vars;
    private RecyclerView recycler_view_rutas;
    private RutaAdapter mAdapter;
    LinearLayoutManager mLayoutManager;
    RelativeLayout layoutEsperaHistorialRuta;
    RelativeLayout layoutMacroEsperaRutas;
    RelativeLayout layoutSinRutas;
    ImageView not_found_peliculas;
    private int pagina;
    Context context;
    private boolean solicitando=false;
    //VERSION DEL APP INSTALADA
    private String versionActualApp;
    private LinearLayout linear_rutas;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        sharedPreferences=new gestionSharedPreferences(getActivity().getApplicationContext());
        listadoRutas=new ArrayList<Ruta>();
        vars=new vars();
        context = getActivity();
        pagina=0;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        layoutSinRutas=(RelativeLayout)getActivity().findViewById(R.id.layoutSinRutas);
        layoutMacroEsperaRutas=(RelativeLayout)getActivity().findViewById(R.id.layoutMacroEsperaRutas);
        linear_rutas=(LinearLayout) getActivity().findViewById(R.id.linear_rutas);
        recycler_view_rutas=(RecyclerView) getActivity().findViewById(R.id.recycler_view_rutas);
        mLayoutManager = new LinearLayoutManager(getActivity());

        mAdapter = new RutaAdapter(getActivity(),listadoRutas, new RutaAdapter.OnItemClickListener()
        {
            @Override
            public void onItemClick(Ruta ruta)
            {
                Intent i=new Intent(getActivity(), DetalleRuta.class);
                i.putExtra("codRuta",ruta.getCodRuta());
                i.putExtra("desRuta",ruta.getDesRuta());
                i.putExtra("desOrigen",ruta.getDesOrigen());
                i.putExtra("desDestino",ruta.getDesDestino());
                startActivity(i);
            }
        });

        recycler_view_rutas.setHasFixedSize(true);
        recycler_view_rutas.setLayoutManager(mLayoutManager);
        recycler_view_rutas.setItemAnimator(new DefaultItemAnimator());
        recycler_view_rutas.setAdapter(mAdapter);

        //VERSION APP
        try
        {
            versionActualApp=getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
        }
        catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        WebServiceGetRutas();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_gps, container, false);
    }

    private void WebServiceGetRutas()
    {
        listadoRutas.clear();
        String _urlWebService = vars.ipServer.concat("/ws/getRutas");

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
                                JSONArray listaPeliculas = response.getJSONArray("rutas");

                                for (int i = 0; i < listaPeliculas.length(); i++)
                                {
                                    JSONObject jsonObject = (JSONObject) listaPeliculas.get(i);
                                    Ruta ruta = new Ruta();
                                    ruta.setCodRuta(jsonObject.getString("codRuta"));
                                    ruta.setType(jsonObject.getString("type"));
                                    ruta.setCodUsuario(jsonObject.getString("codUsuario"));
                                    ruta.setDesRuta(jsonObject.getString("desRuta"));
                                    ruta.setDesOrigen(jsonObject.getString("desOrigen"));
                                    ruta.setDesDestino(jsonObject.getString("desDestino"));
                                    ruta.setColRuta(jsonObject.getString("colRuta"));
                                    ruta.setPorRuta(jsonObject.getString("porRuta"));
                                    listadoRutas.add(ruta);
                                }

                                layoutMacroEsperaRutas.setVisibility(View.GONE);
                                linear_rutas.setVisibility(View.VISIBLE);
                            }

                            else
                            {
                                layoutMacroEsperaRutas.setVisibility(View.GONE);
                                linear_rutas.setVisibility(View.GONE);
                                layoutSinRutas.setVisibility(View.VISIBLE);
                            }
                        }
                        catch (JSONException e)
                        {

                            layoutMacroEsperaRutas.setVisibility(View.GONE);
                            linear_rutas.setVisibility(View.GONE);
                            layoutSinRutas.setVisibility(View.VISIBLE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(gps.this.getActivity(),R.style.AlertDialogTheme));
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


                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(gps.this.getActivity(),R.style.AlertDialogTheme));
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


                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(gps.this.getActivity(),R.style.AlertDialogTheme));
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


                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(gps.this.getActivity(),R.style.AlertDialogTheme));
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(gps.this.getActivity(),R.style.AlertDialogTheme));
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


                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(gps.this.getActivity(),R.style.AlertDialogTheme));
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(gps.this.getActivity(),R.style.AlertDialogTheme));
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
                headers.put("codUsuario", sharedPreferences.getString("codUsuario"));
                headers.put("MyToken", sharedPreferences.getString("MyToken"));
                return headers;
            }
        };

        ControllerSingleton.getInstance().addToReqQueue(jsonObjReq, "");
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }
}
