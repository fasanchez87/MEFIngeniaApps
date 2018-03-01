package com.ingeniapps.mef.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
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
import com.ingeniapps.mef.activity.DetalleTarea;
import com.ingeniapps.mef.beans.Ruta;
import com.ingeniapps.mef.beans.Tarea;
import com.ingeniapps.mef.sharedPreferences.gestionSharedPreferences;
import com.ingeniapps.mef.util.MyAnimationUtils;
import com.ingeniapps.mef.vars.vars;
import com.ingeniapps.mef.volley.ControllerSingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Ingenia Applications on 2/02/2018.
 */

public class TareaAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private Activity activity;
    private LayoutInflater inflater;
    private List<Tarea> listadoTareas;

    public final int TYPE_TAREA=0;
    public final int TYPE_LOAD=1;
    private gestionSharedPreferences sharedPreferences;
    private Context context;
    OnLoadMoreListener loadMoreListener;
    boolean isLoading=false, isMoreDataAvailable=true;
    vars vars;
    int previousPosition=0;

    private ProgressDialog progressDialog;


    public interface OnItemClickListener
    {
        void onItemClick(Tarea tarea);
    }

    private final TareaAdapter.OnItemClickListener listener;

    public TareaAdapter(Activity activity, ArrayList<Tarea> listadoTareas, TareaAdapter.OnItemClickListener listener)
    {
        this.activity=activity;
        this.listadoTareas=listadoTareas;
        vars=new vars();
        sharedPreferences=new gestionSharedPreferences(this.activity);
        this.listener=listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if(viewType==TYPE_TAREA)
        {
            return new TareaHolder(inflater.inflate(R.layout.tarea_row,parent,false));
        }
        else
        {
            return new LoadHolder(inflater.inflate(R.layout.tarea_row,parent,false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        if(position >= getItemCount()-1 && isMoreDataAvailable && !isLoading && loadMoreListener!=null)
        {
            isLoading = true;
            loadMoreListener.onLoadMore();
        }

        if(getItemViewType(position)==TYPE_TAREA)
        {
            ((TareaHolder)holder).bindData(listadoTareas.get(position));
        }

        previousPosition=position;
    }

    @Override
    public int getItemViewType(int position)
    {
        if(listadoTareas.get(position).getType().equals("tarea"))
        {
            return TYPE_TAREA;
        }
        else
        {
            return TYPE_LOAD;
        }
    }

    @Override
    public int getItemCount()
    {
        return listadoTareas.size();
    }

    public class TareaHolder extends RecyclerView.ViewHolder
    {
        public TextView nomTarea;
        public TextView fecTarea;
        public Switch switchTarea;

        public TareaHolder(View view)
        {
            super(view);
            nomTarea=(TextView) view.findViewById(R.id.nomTarea);
            fecTarea=(TextView) view.findViewById(R.id.fecTarea);
            switchTarea=(Switch) view.findViewById(R.id.switchTarea);
        }

        void bindData(final Tarea tarea)
        {
            nomTarea.setText(tarea.getDesTarea());
            fecTarea.setText(tarea.getFecTarea());

            switchTarea.setOnCheckedChangeListener(null);
            //if true, your checkbox will be selected, else unselected
            //checkCategoria.setChecked(checkCategoria.isSelected());
            switchTarea.setChecked(tarea.isIndCheck());
            switchTarea.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
            {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                {
                    tarea.setIndCheck(isChecked);

                    if(tarea.isIndCheck())
                    {
                        WebServiceSetEstado(tarea.getCodRuta(),tarea.getCodMeta(),tarea.getCodTarea(),sharedPreferences.getString("codUsuario"),"1");
                    }

                    else
                    {
                        WebServiceSetEstado(tarea.getCodRuta(),tarea.getCodMeta(),tarea.getCodTarea(),sharedPreferences.getString("codUsuario"),"0");
                    }
                }
            });

            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override public void onClick(View v)
                {
                    listener.onItemClick(tarea);
                }
            });
        }
    }

    static class LoadHolder extends RecyclerView.ViewHolder
    {
        public LoadHolder(View itemView)
        {
            super(itemView);
        }
    }

    public void setMoreDataAvailable(boolean moreDataAvailable)
    {
        isMoreDataAvailable = moreDataAvailable;
    }
    /* notifyDataSetChanged is final method so we can't override it
        call adapter.notifyDataChanged(); after update the list
        */
    public void notifyDataChanged()
    {
        notifyDataSetChanged();
        isLoading = false;
    }

    public interface OnLoadMoreListener
    {
        void onLoadMore();
    }

    public void setLoadMoreListener(OnLoadMoreListener loadMoreListener)
    {
        this.loadMoreListener = loadMoreListener;
    }

    public List<Tarea> getTareasList()
    {
        return listadoTareas;
    }


    private void WebServiceSetEstado(final String codRuta,final String codMeta,final String codTarea, final String codUsuario,final String codEstado)
    {
        progressDialog = new ProgressDialog(new ContextThemeWrapper(activity,R.style.AppCompatAlertDialogStyle));
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Guardando...");
        progressDialog.show();
        progressDialog.setCancelable(false);

        String _urlWebService = vars.ipServer.concat("/ws/setEstado");

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, _urlWebService, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        try
                        {
                            if(response.getBoolean("status"))
                            {
                                progressDialog.dismiss();
                            }

                            else
                            {
                                progressDialog.dismiss();
                            }
                        }
                        catch (JSONException e)
                        {
                            progressDialog.dismiss();

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(activity,R.style.AlertDialogTheme));
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
                            progressDialog.dismiss();

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(activity,R.style.AlertDialogTheme));
                            builder
                                    .setMessage("El tiempo de espera de la conexión ha finalizado, intenta de nuevo.")
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
                            progressDialog.dismiss();


                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(activity,R.style.AlertDialogTheme));
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
                            progressDialog.dismiss();

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(activity,R.style.AlertDialogTheme));
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
                            progressDialog.dismiss();

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(activity,R.style.AlertDialogTheme));
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
                            progressDialog.dismiss();


                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(activity,R.style.AlertDialogTheme));
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
                            progressDialog.dismiss();


                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(activity,R.style.AlertDialogTheme));
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
                headers.put("codTarea", codTarea);
                headers.put("codUsuario", codUsuario);
                headers.put("codEstado", codEstado);
                return headers;
            }
        };

        ControllerSingleton.getInstance().addToReqQueue(jsonObjReq, "");
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

}


