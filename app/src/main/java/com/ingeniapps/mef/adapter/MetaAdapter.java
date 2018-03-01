package com.ingeniapps.mef.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.ingeniapps.mef.beans.Meta;
import com.ingeniapps.mef.R;
import com.ingeniapps.mef.beans.Meta;

import java.util.ArrayList;

/**
 * Created by FABiO on 16/12/2016.
 */

public class MetaAdapter extends BaseAdapter
{
    private Context context;
    private ArrayList<Meta> listadoMetas;

    public MetaAdapter(Context context, ArrayList<Meta> listadoMetas)
    {
        this.context = context;
        this.listadoMetas = listadoMetas;
    }

    @Override
    public int getCount()
    {
        return listadoMetas.size();
    }

    @Override
    public Meta getItem(int position)
    {
        return listadoMetas.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup)
    {

        if (view == null)
        {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.layout_item_meta, viewGroup, false);
        }

        ImageView imagenEmpresa = (ImageView) view.findViewById(R.id.imagen_meta);
        TextView descripcionMeta = (TextView) view.findViewById(R.id.descripcionMeta);
        TextView textViewPorcentajeMeta = (TextView) view.findViewById(R.id.textViewPorcentajeMeta);

        final Meta meta = getItem(position);

        RequestOptions req = new RequestOptions();
        req.diskCacheStrategy(DiskCacheStrategy.RESOURCE);
        req.skipMemoryCache(true);

        descripcionMeta.setText(meta.getDesMeta());

        textViewPorcentajeMeta.setText(meta.getPorMeta());
        textViewPorcentajeMeta.setBackgroundColor(Color.parseColor(""+meta.getColMeta()));

        if (TextUtils.isEmpty(meta.getImgMeta().toString()))
        {
            imagenEmpresa.setImageResource(R.drawable.item_not_result);
        }

        else
        {
            Glide.with(context).
                    load(meta.getImgMeta()).
                    thumbnail(0.5f).into(imagenEmpresa);
        }

        return view;
    }

}
