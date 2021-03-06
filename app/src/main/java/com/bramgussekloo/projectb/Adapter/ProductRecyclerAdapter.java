package com.bramgussekloo.projectb.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bramgussekloo.projectb.R;
import com.bramgussekloo.projectb.fragments.HistoryFragment;
import com.bramgussekloo.projectb.fragments.HomeFragment;
import com.bramgussekloo.projectb.fragments.ReservationsFragment;
import com.bramgussekloo.projectb.models.Product;
import com.bumptech.glide.Glide;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import javax.annotation.Nullable;

public class ProductRecyclerAdapter extends RecyclerView.Adapter<ProductRecyclerAdapter.ViewHolder> {

    private List<Product> product_list;
    public Context context;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private int Quantity;
    private static final String TAG = "ProductRecyclerAdapter";
    private int QTT;


    private OnProductListener onProductListener;

    public ProductRecyclerAdapter(List<Product> product_list, OnProductListener onProductListener){

        this.product_list = product_list;
        this.onProductListener = onProductListener;

    }

    @NonNull
    @Override
    public ProductRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_list_item, parent, false);
        context = parent.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        return new ViewHolder(view, onProductListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final ProductRecyclerAdapter.ViewHolder viewHolder, final int i) {

        final String productId = product_list.get(i).ProductId;
        String title_data = product_list.get(i).getTitle();
        viewHolder.setTitleText(title_data);
        viewHolder.setNumInt(product_list.get(i).getQuantity());


        String image_url = product_list.get(i).getImage_url();
        final String currentUserId = firebaseAuth.getCurrentUser().getEmail().replace(".", "").replace("@", "");
        viewHolder.setBlogImage(image_url);

            //change productListReser button text
        firebaseFirestore.collection("Products/" + productId + "/reservation").document(currentUserId)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot.exists()){
                    viewHolder.productListReser.setText(R.string.product_reserve_canceled);
                }else {
                    viewHolder.productListReser.setText(R.string.product_reserve);

                }

            }
        });

        viewHolder.productListReser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //reservations
                firebaseFirestore.collection("Products").document(productId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            final Map<String, Object> ProductMap = task.getResult().getData();
                            Quantity = Integer.parseInt(ProductMap.get("quantity").toString());
                            Quantity = Quantity - 1;
                            ProductMap.put("quantity", Quantity);
                            firebaseFirestore.collection("Products/" + productId + "/reservation").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    String quantityText = viewHolder.intView.getText().toString();
                                    int quantityInt = Integer.parseInt(quantityText);
                                    Log.d("dsds", "onComplete() returned: " + quantityInt);
                                    if (!task.getResult().exists()&& quantityInt != 0){// check if user reserved certain product or not
                                        Map<String, Object> reservationsMap = new HashMap<>();
                                        reservationsMap.put("timestamp", FieldValue.serverTimestamp());
                                        reservationsMap.put("product",productId);
                                        Log.d("timestamp", java.util.Calendar.getInstance().getTime().toString());
                                        firebaseFirestore.collection("Products/" + productId + "/reservation").document(currentUserId).set(reservationsMap)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {;
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if(task.isSuccessful()){
                                                                FirebaseFirestore.getInstance().collection("Products").document(productId).set(ProductMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()){
                                                                            Toast.makeText(context, "Product is reserved", Toast.LENGTH_LONG).show();

                                                                        }
                                                                    }
                                                                });
                                                            } else {
                                                                String errorMessage = task.getException().getMessage();
                                                                Toast.makeText(context, "Error : " + errorMessage, Toast.LENGTH_LONG).show();
                                                            }
                                                        }
                                                });
                                    }else if (!task.getResult().exists()&& quantityInt == 0){
                                        Toast.makeText(context, "Sorry .. temporarily out of stock !" , Toast.LENGTH_LONG).show();
                                    }else {// delete a reservation
                                        firebaseFirestore.collection("Products/" + productId + "/reservation").document(currentUserId).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                firebaseFirestore.collection("Products").document(productId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if (task.isSuccessful()){
                                                            Map<String, Object> map = task.getResult().getData();
                                                            QTT = Integer.parseInt(map.get("quantity").toString());
                                                            QTT = QTT + 1;
                                                            map.put("quantity", QTT);
                                                            firebaseFirestore.collection("Products").document(productId).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()){
                                                                        Toast.makeText(context, "Product is deleted", Toast.LENGTH_SHORT).show();

                                                                    }
                                                                }
                                                            });
                                                        }
                                                    }
                                                });
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return product_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private View mView;
        private TextView descView;
        private ImageView productImageView;
        private TextView intView;
        private Button productListReser;
        OnProductListener onProductListener;
        private Button readMoreButton;



        private ViewHolder(@NonNull View itemView, OnProductListener onProductListener) {
            super(itemView);
            mView = itemView;
            productListReser = mView.findViewById(R.id.more_product_list_reservations);
            this.onProductListener = onProductListener;
            readMoreButton = mView.findViewById(R.id.more_product_list_read_more);
            intView = mView.findViewById(R.id.more_product_list_quantity);


            readMoreButton.setOnClickListener(this);

        }

        private void setTitleText(String titleText){
            descView = mView.findViewById(R.id.more_reservations_list_title);
            descView.setText(titleText);
        }

        private void setNumInt(int numInt){
            intView = mView.findViewById(R.id.more_product_list_quantity);
            intView.setText(Integer.toString(numInt));
        }


        private void setBlogImage(String downloadUri){
            productImageView = mView.findViewById(R.id.more_product_list_image);
            Glide.with(context).load(downloadUri).into(productImageView);
        }


        @Override
        public void onClick(View view) {
            onProductListener.onProductClick(getAdapterPosition());
        }
    }

    public interface OnProductListener{
        void onProductClick(int position);
    }

}
