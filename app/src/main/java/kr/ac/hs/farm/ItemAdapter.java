package kr.ac.hs.farm;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {
    private List<Item> itemList;
    private Context context;
    private ItemClickListener itemClickListener;

    // 클릭 인터페이스
    public interface ItemClickListener {
        void onFarmItemClick(Item item);
    }

    // 생성자에 listener 추가 (null 허용 가능)
    public ItemAdapter(List<Item> itemList, Context context, ItemClickListener listener) {
        this.itemList = itemList;
        this.context = context;
        this.itemClickListener = listener;
    }

    // 리스트 갱신 메서드
    public void updateList(List<Item> newList) {
        this.itemList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_inventory, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = itemList.get(position);
        holder.imageView.setImageResource(item.imageRes);

        if (!item.obtained) {
            holder.imageView.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
        } else {
            holder.imageView.clearColorFilter();
        }

        if (item.category.equals("먹이")) {
            holder.itemCount.setVisibility(View.VISIBLE);
            holder.itemCount.setText("x" + item.count);
        } else {
            holder.itemCount.setVisibility(View.GONE);
        }

        holder.imageView.setOnClickListener(v -> {
            if (item.category.equals("농장") && item.obtained) {
                if (itemClickListener != null) {
                    itemClickListener.onFarmItemClick(item);
                }
            } else if (item.category.equals("농장") && !item.obtained) {
                Toast.makeText(context, "아직 획득하지 않은 아이템입니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView itemCount;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.itemImage);
            itemCount = itemView.findViewById(R.id.itemCount);
        }
    }
}
