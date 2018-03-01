package com.ingeniapps.mef.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.bumptech.glide.Glide;
import com.ingeniapps.mef.R;
import com.ingeniapps.mef.beans.Ruta;
import com.ingeniapps.mef.util.MyAnimationUtils;
import com.ingeniapps.mef.vars.vars;
import com.ingeniapps.mef.sharedPreferences.gestionSharedPreferences;
import com.ingeniapps.mef.volley.ControllerSingleton;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ingenia Applications on 2/02/2018.
 */

public class RutaAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private Activity activity;
    private LayoutInflater inflater;
    private List<Ruta> listadoRutas;

    public final int TYPE_RUTA=0;
    public final int TYPE_LOAD=1;
    private gestionSharedPreferences sharedPreferences;
    private Context context;
    OnLoadMoreListener loadMoreListener;
    boolean isLoading=false, isMoreDataAvailable=true;
    vars vars;
    int previousPosition=0;

    public interface OnItemClickListener
    {
        void onItemClick(Ruta ruta);
    }

    private final RutaAdapter.OnItemClickListener listener;

    public RutaAdapter(Activity activity, ArrayList<Ruta> listadoRutas, RutaAdapter.OnItemClickListener listener)
    {
        this.activity=activity;
        this.listadoRutas=listadoRutas;
        vars=new vars();
        sharedPreferences=new gestionSharedPreferences(this.activity);
        this.listener=listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if(viewType==TYPE_RUTA)
        {
            return new RutaHolder(inflater.inflate(R.layout.ruta_row_layout,parent,false));
        }
        else
        {
            return new LoadHolder(inflater.inflate(R.layout.ruta_row_layout,parent,false));
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

        if(getItemViewType(position)==TYPE_RUTA)
        {
            ((RutaHolder)holder).bindData(listadoRutas.get(position));
        }
        previousPosition=position;

    }

    @Override
    public int getItemViewType(int position)
    {
        if(listadoRutas.get(position).getType().equals("ruta"))
        {
            return TYPE_RUTA;
        }
        else
        {
            return TYPE_LOAD;
        }
    }

    @Override
    public int getItemCount()
    {
        return listadoRutas.size();
    }

    public class RutaHolder extends RecyclerView.ViewHolder
    {
        public TextView desRuta;
        public TextView textViewPorcentajeRuta;

        public RutaHolder(View view)
        {
            super(view);
            desRuta=(TextView) view.findViewById(R.id.desRuta);
            textViewPorcentajeRuta=(TextView) view.findViewById(R.id.textViewPorcentajeRuta);
        }

        void bindData(final Ruta ruta)
        {
            desRuta.setText(ruta.getDesRuta());
            desRuta.setText(ruta.getDesRuta());

            textViewPorcentajeRuta.setText(ruta.getPorRuta());
            textViewPorcentajeRuta.setBackgroundColor(Color.parseColor(""+ruta.getColRuta()));

            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override public void onClick(View v)
                {
                    listener.onItemClick(ruta);
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

    public List<Ruta> getPeliculaList()
    {
        return listadoRutas;
    }

}


