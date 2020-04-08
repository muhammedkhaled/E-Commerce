package com.example.e_commerce;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e_commerce.model.AdminOrders;

import java.util.List;

public class AdminOrdersAdapter extends RecyclerView.Adapter<AdminOrdersAdapter.OrdersViewHolder> {

    private List<AdminOrders> adminOrdersList;
    private OnOrderClick mOnOrderClick;
    private int mPosition;
    private String type = "";

    public void setType(String type) {
        this.type = type;
    }

    public void setAdminOrdersList(List<AdminOrders> adminOrdersList) {
        this.adminOrdersList = adminOrdersList;
        notifyDataSetChanged();
    }

    public AdminOrdersAdapter(OnOrderClick mOnOrderClick) {
        this.mOnOrderClick = mOnOrderClick;
    }

    public AdminOrders getOrderObject(){
       return adminOrdersList.get(mPosition);
    }

    @NonNull
    @Override
    public OrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.orders_layout, parent, false);
        return new OrdersViewHolder(view, mOnOrderClick);
    }

    @Override
    public void onBindViewHolder(@NonNull final OrdersViewHolder holder, int position) {
        final AdminOrders adminOrder = adminOrdersList.get(position);

        mPosition = holder.getAdapterPosition();

        holder.userName.setText("User name: " + adminOrder.getuName());
        holder.userPhone.setText("User phone: " + adminOrder.getuPhone());

        holder.recName.setText("Rec name: " + adminOrder.getrName());
        holder.recPhone.setText("Rec phone: " + adminOrder.getrPhone());
        holder.totalPrice.setText("Total Amount: " + adminOrder.getTotalAmount());
        holder.shippingAddress.setText("Address: " + adminOrder.getAddress());
        holder.dateTime.setText("Ordered at: " + adminOrder.getTime() + " - " + adminOrder.getDate());
        holder.orderState.setText("State: " + adminOrder.getState());

        if (type.equals("User")){
            if (adminOrder.getState().equals("not shipped") ){
                holder.shipOrderBtn.setText("Edit Order Info");
                holder.rejectOrderBtn.setText("Delete Order");
                holder.shipOrderBtn.setVisibility(View.VISIBLE);
                holder.rejectOrderBtn.setVisibility(View.VISIBLE);
            }else if (adminOrder.getState().equals("Order Canceled")){
                holder.shipOrderBtn.setText("Edit Order Info");
                holder.rejectOrderBtn.setText("Delete Order");
                holder.shipOrderBtn.setVisibility(View.GONE);
                holder.rejectOrderBtn.setVisibility(View.VISIBLE);
            }
            else {
                holder.shipOrderBtn.setVisibility(View.GONE);
                holder.rejectOrderBtn.setVisibility(View.GONE);
            }
        }

        /*        holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        CharSequence options[] = new CharSequence[]{
                                "Yes",
                                "No"
                        };

                        AlertDialog.Builder builder =
                                new AlertDialog.Builder(AdminNewOrdersActivity.this);
                        builder.setTitle("Have you shipped this order products");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    String uID = getRef(i).getKey();
                                    removeOrder(uID);
                                }
                                if (which == 1) {
        //                                            finish();
                                }
                            }
                        });

                        builder.show();


                    }
                });*/
            }

    @Override
    public int getItemCount() {
        if (adminOrdersList == null){
            return 0;
        }else {
            return adminOrdersList.size();
        }
    }

    public static class OrdersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView userName, userPhone, recName, recPhone, totalPrice, dateTime, shippingAddress, orderState;
        Button showOrdersBtn, shipOrderBtn, rejectOrderBtn;
        OnOrderClick mOnOrderClick;

        OrdersViewHolder(@NonNull View itemView, OnOrderClick mOnOrderClick) {
            super(itemView);

            userName = itemView.findViewById(R.id.order_user_name);
            userPhone = itemView.findViewById(R.id.order_user_phone);

            recName = itemView.findViewById(R.id.order_receiver_name);
            recPhone = itemView.findViewById(R.id.receiver_phone_number);
            totalPrice = itemView.findViewById(R.id.order_total_price);
            dateTime = itemView.findViewById(R.id.order_date_time);
            shippingAddress = itemView.findViewById(R.id.order_address_city);
            orderState = itemView.findViewById(R.id.order_state);

            showOrdersBtn = itemView.findViewById(R.id.show_all_products_btn);
            shipOrderBtn = itemView.findViewById(R.id.ship_order_btn);
            rejectOrderBtn = itemView.findViewById(R.id.reject_order_btn);

            this.mOnOrderClick = mOnOrderClick;

            showOrdersBtn.setOnClickListener(this);
            shipOrderBtn.setOnClickListener(this);
            rejectOrderBtn.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mOnOrderClick.onBtnClick(v, getAdapterPosition());
        }
    }

    public interface OnOrderClick{
        void onBtnClick(View view, int position);
    }
}
