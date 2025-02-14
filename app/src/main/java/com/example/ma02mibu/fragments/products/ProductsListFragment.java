package com.example.ma02mibu.fragments.products;

import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;

import com.example.ma02mibu.FragmentTransition;
import com.example.ma02mibu.R;
import com.example.ma02mibu.activities.CloudStoreUtil;
import com.example.ma02mibu.adapters.ProductListAdapter;
import com.example.ma02mibu.adapters.ServiceListAdapter;
import com.example.ma02mibu.databinding.FragmentProductsListBinding;
import com.example.ma02mibu.fragments.packages.PackageDetailsFragment;
import com.example.ma02mibu.model.Employee;
import com.example.ma02mibu.model.Owner;
import com.example.ma02mibu.model.Package;
import com.example.ma02mibu.model.Product;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class ProductsListFragment extends ListFragment {
    private FragmentProductsListBinding binding;
    private ArrayList<Product> mProducts;
    private ArrayList<Product> mProductsBackup;
    private ArrayList<String> categories;
    private ArrayList<String> subCategories;
    private ProductListAdapter adapter;
    private boolean showHeading = true;
    private static ArrayList<Product> products = new ArrayList<>();
    private static final String ARG_PARAM = "param";
    private boolean isOwner = false;
    private String userId;
    private FirebaseAuth auth;
    public static ProductsListFragment newInstance(){
        ProductsListFragment fragment = new ProductsListFragment();
        return fragment;
    }

    public static ProductsListFragment newInstance(ArrayList<Product> products) {
        ProductsListFragment fragment = new ProductsListFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_PARAM, products);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if(user != null){
            userId = user.getUid();
        }
        super.onCreate(savedInstanceState);
        Log.i("ShopApp", "onCreate Products List Fragment");
        if (getArguments() != null) {
            mProducts = getArguments().getParcelableArrayList(ARG_PARAM);
            adapter = new ProductListAdapter(getActivity(), mProducts, getActivity(), false, null, isOwner);
            setListAdapter(adapter);
            showHeading = false;
        }
        else {
            CloudStoreUtil.selectProducts(new CloudStoreUtil.ProductCallback() {
                @Override
                public void onCallback(ArrayList<Product> retrievedProducts) {
                    if (retrievedProducts != null) {
                        mProducts = retrievedProducts;
                    } else {
                        mProducts = new ArrayList<>();
                    }
                    CloudStoreUtil.getOwner(userId, new CloudStoreUtil.OwnerCallback() {
                        @Override
                        public void onSuccess(Owner myItem) {
                            isOwner = true;
                            mProducts.removeIf(p -> !p.getOwnerUuid().equals(userId));
                            mProducts.removeIf(p -> p.isPending());
                            mProductsBackup = new ArrayList<>(mProducts);
                            adapter = new ProductListAdapter(getActivity(), mProducts, getActivity(), false, null, isOwner);
                            setListAdapter(adapter);
                        }
                        @Override
                        public void onFailure(Exception e) {
                            isOwner = false;
                            getEmployeesProducts();
                            adapter = new ProductListAdapter(getActivity(), mProducts, getActivity(), false, null, isOwner);
                            setListAdapter(adapter);
                            binding.newProductButton.setVisibility(View.GONE);
                        }
                    });
                }
            });
        }
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i("ShopApp", "onCreateView Products List Fragment");
        binding = FragmentProductsListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        if(!showHeading){
            binding.linear1.setVisibility(View.GONE);
        }
        Button searchButton = binding.searchButton;
        searchButton.setOnClickListener(v -> {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getActivity(), R.style.FullScreenBottomSheetDialog);
            View dialogView = getLayoutInflater().inflate(R.layout.product_search_dialog, null);
            bottomSheetDialog.setContentView(dialogView);
            Button submitSearchBtn = bottomSheetDialog.findViewById(R.id.submit_search_products);
            submitSearchBtn.setOnClickListener(f -> {
                EditText productName = bottomSheetDialog.findViewById(R.id.product_name_search);
                EditText productDescription = bottomSheetDialog.findViewById(R.id.description_search);
                EditText minPriceText = bottomSheetDialog.findViewById(R.id.min_price);
                EditText maxPriceText = bottomSheetDialog.findViewById(R.id.max_price);
                EditText productEventType = bottomSheetDialog.findViewById(R.id.event_type_search);
                EditText category = bottomSheetDialog.findViewById(R.id.product_category_search);
                EditText subcategory = bottomSheetDialog.findViewById(R.id.product_subcategory_search);
                String name = productName.getText().toString();
                String description = productDescription.getText().toString();
                String eventType = productEventType.getText().toString();
                Integer minPrice = Integer.parseInt(minPriceText.getText().toString().isEmpty() ? "0" : minPriceText.getText().toString());
                Integer maxPrice = Integer.parseInt(maxPriceText.getText().toString().isEmpty() ? "0" : maxPriceText.getText().toString());
                searchProducts(name, description, minPrice, maxPrice, eventType, category.getText().toString(), subcategory.getText().toString());
                bottomSheetDialog.dismiss();
            });
            Button resetBtn = bottomSheetDialog.findViewById(R.id.reset_search_products);
            resetBtn.setOnClickListener(f -> {
                resetProducts();
                bottomSheetDialog.dismiss();
            });
            bottomSheetDialog.show();
        });

        Button newProductButton = binding.newProductButton;
        newProductButton.setOnClickListener(v -> {
            FragmentTransition.to(NewProduct.newInstance(), getActivity(),
                    true, R.id.scroll_products_list, "newProductPage");
        });

        return root;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void searchProducts(String name, String description, Integer minPrice, Integer maxPrice, String eventType, String category, String subCategory){
        mProducts = new ArrayList<>(mProductsBackup);
        if (!name.isEmpty())
            mProducts.removeIf(p -> !p.getName().toLowerCase().contains(name.toLowerCase()));
        if (!description.isEmpty())
            mProducts.removeIf(p -> !p.getDescription().toLowerCase().contains(description.toLowerCase()));
        if (!category.isEmpty())
            mProducts.removeIf(p -> !p.getCategory().toLowerCase().contains(category.toLowerCase()));
        if (!subCategory.isEmpty())
            mProducts.removeIf(p -> !p.getSubCategory().toLowerCase().contains(subCategory.toLowerCase()));
        if(minPrice != 0)
            mProducts.removeIf(p -> minPrice > p.getNewPriceValue());
        if(maxPrice != 0)
            mProducts.removeIf(p -> maxPrice < p.getNewPriceValue());
        if(!eventType.isEmpty())
            mProducts.removeIf(p -> !p.containsEventType(eventType));
        adapter = new ProductListAdapter(getActivity(), mProducts, getActivity(), false, null, isOwner);
        setListAdapter(adapter);
    }
    private void resetProducts(){
        mProducts = new ArrayList<>(mProductsBackup);
        adapter = new ProductListAdapter(getActivity(), mProducts, getActivity(), false, null, isOwner);
        setListAdapter(adapter);
    }
    private void getEmployeesProducts(){
        CloudStoreUtil.getEmployee(userId, new CloudStoreUtil.EmployeeCallback() {
            @Override
            public void onSuccess(Employee myItem) {
                mProducts.removeIf(s -> !s.getOwnerUuid().equals(myItem.getOwnerRefId()));
            }
            @Override
            public void onFailure(Exception e) {

            }
        });
    }
}
