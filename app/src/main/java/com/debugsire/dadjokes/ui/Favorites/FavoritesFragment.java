package com.debugsire.dadjokes.ui.Favorites;

import android.graphics.Canvas;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.debugsire.dadjokes.Adapters.FavoritesAdapter;
import com.debugsire.dadjokes.Model.FavoritesModel;
import com.debugsire.dadjokes.R;
import com.debugsire.dadjokes.Realms.MyDownloads;
import com.debugsire.dadjokes.Realms.MyFavorites;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import io.realm.Realm;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class FavoritesFragment extends Fragment {
    private static final String TAG = "FavoritesFragment -- ";


    RecyclerView recyclerView;
    TextView count;
    LinearLayout noItems;
    //
    ArrayList<FavoritesModel> models;
    FavoritesAdapter adapter;
    FavoritesModel deletedModel;
    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int adapterPosition = viewHolder.getAdapterPosition();
            deletedModel = models.get(adapterPosition);
            models.remove(adapterPosition);
            adapter.notifyItemRemoved(adapterPosition);
            setCount();
            Snackbar snackbar = Snackbar.make(recyclerView, deletedModel.getJoke(), Snackbar.LENGTH_LONG)
                    .setAction("Undo", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            models.add(adapterPosition, deletedModel);
                            adapter.notifyItemInserted(adapterPosition);
                            setCount();
                        }
                    });
            snackbar.addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                @Override
                public void onDismissed(Snackbar transientBottomBar, int event) {
                    super.onDismissed(transientBottomBar, event);
                    if (event != 1) {
                        Realm.getDefaultInstance().executeTransactionAsync(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                MyFavorites first = Realm.getDefaultInstance().where(MyFavorites.class)
                                        .equalTo("key", deletedModel.getKey())
                                        .findFirst();
                                if (first != null) first.deleteFromRealm();
                            }
                        }, new Realm.Transaction.OnSuccess() {
                            @Override
                            public void onSuccess() {
                                EventBus.getDefault().post(deletedModel.getKey());
                            }
                        });
                    }
                }
            });
            snackbar.show();
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(getContext(), R.color.prim))
                    .addActionIcon(R.drawable.ic_round_delete_24)
                    .addSwipeLeftLabel("Remove from favorites")
                    .setSwipeLeftLabelTextSize(TypedValue.COMPLEX_UNIT_DIP, 18)
                    .setSwipeLeftLabelColor(ContextCompat.getColor(getContext(), R.color.white))
                    .create()
                    .decorate();
        }
    };

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_favorites, container, false);
        initCompos(root);
        initRv();
        loadRv();
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerView);
        return root;
    }

    private void initCompos(View root) {
        recyclerView = root.findViewById(R.id.rv);
        count = root.findViewById(R.id.tv_count);
        noItems = root.findViewById(R.id.ll_noItems);
    }

    private void initRv() {
        models = new ArrayList<>();
        adapter = new FavoritesAdapter(models, getLayoutInflater());
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
    }

    //
    //
    //
    //
    //
    //
    //

    public void loadRv() {
        models.clear();
        for (MyFavorites myFavorites :
                Realm.getDefaultInstance().where(MyFavorites.class)
                        .findAll()) {
            models.add(new FavoritesModel(myFavorites.getKey(),
                    myFavorites.getJoke()));
        }
        adapter.notifyDataSetChanged();
        setCount();
    }

    private void setCount() {
        count.setText(models.size() == 0 ? "" : models.size() == 1 ? "  1 Item" : models.size() + " Items");
        if (models.size() == 0) {
            noItems.setVisibility(View.VISIBLE);
        } else {
            noItems.setVisibility(View.GONE);
        }
    }
}