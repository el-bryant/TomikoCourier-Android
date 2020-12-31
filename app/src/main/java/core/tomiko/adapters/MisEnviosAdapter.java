package core.tomiko.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import core.tomiko.R;
import core.tomiko.entity.MisEnvios;

/**
 * By: El Bryant
 */

public class MisEnviosAdapter extends RecyclerView.Adapter<MisEnviosAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<MisEnvios> mMisEnvios;

    public MisEnviosAdapter (Context context, ArrayList<MisEnvios> misEnvios) {
        mContext = context;
        mMisEnvios = misEnvios;
    }

    @NonNull
    @Override
    public MisEnviosAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.card_envios, parent, false);
        return new MisEnviosAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MisEnvios misEnvios = mMisEnvios.get(position);
        final String idEnvio = misEnvios.getId_envio();
        final String direccionEntrega = misEnvios.getDireccionEntrega();
        final String direccionRecojo = misEnvios.getDireccionRecojo();
        final String fechaSolicitado = misEnvios.getFechaSolicitado();
        final String fechaEntregado = misEnvios.getFechaEntregado();
        final String estado = misEnvios.getEstado();
        holder.tvidEnvio.setText(idEnvio);
        holder.tvDireccionEntrega.setText(direccionEntrega);
        holder.tvDireccionRecojo.setText(direccionRecojo);
        holder.tvFechaSolicitado.setText(fechaSolicitado);
        if (fechaEntregado != null && !fechaEntregado.equals("") && !fechaEntregado.equals("null")) {
            holder.tvFechaEntregado.setText(fechaEntregado);
        } else {
            holder.tvFechaEntregado.setText("-");
        }
        switch (estado) {
            case "entregado":
                holder.tvEstadoEnvio.setTextColor(Color.rgb(10, 145, 15));
                holder.tvEstadoEnvio.setText("ENTREGADO");
                break;
            case "recibido":
                holder.tvEstadoEnvio.setTextColor(Color.rgb(48, 59, 71));
                holder.tvEstadoEnvio.setText("RECIBIDO");
                break;
            case "solicitado":
                holder.tvEstadoEnvio.setTextColor(Color.rgb(239, 125, 0));
                holder.tvEstadoEnvio.setText("SOLICITADO");
                break;
            case "enviado":
                holder.tvEstadoEnvio.setTextColor(Color.rgb(145, 10, 150));
                holder.tvEstadoEnvio.setText("ENVIADO");
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mMisEnvios.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvidEnvio, tvFechaSolicitado, tvFechaEntregado, tvDireccionRecojo, tvDireccionEntrega, tvEstadoEnvio;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvidEnvio = (TextView) itemView.findViewById(R.id.tvidEnvio);
            tvFechaSolicitado = (TextView) itemView.findViewById(R.id.tvFechaSolicitado);
            tvFechaEntregado = (TextView) itemView.findViewById(R.id.tvFechaEntregado);
            tvDireccionRecojo = (TextView) itemView.findViewById(R.id.tvDireccionRecojo);
            tvDireccionEntrega = (TextView) itemView.findViewById(R.id.tvDireccionEntrega);
            tvEstadoEnvio = (TextView) itemView.findViewById(R.id.tvEstadoEnvio);
        }
    }
}
