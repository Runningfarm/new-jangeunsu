package kr.ac.hs.farm;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class Tab4Activity extends AppCompatActivity {

    private static final String PREFS_NAME = "FarmPrefs";
    private static final String KEY_FOOD_COUNT = "foodCount";

    private RecyclerView recyclerView;
    private ItemAdapter adapter;
    private List<Item> itemList;
    private TabLayout tabLayout;

    private ImageButton tab1Button, tab2Button, tab3Button, tab4Button, tab5Button, tab6Button;

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab4);

        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        tab1Button = findViewById(R.id.tab1Button);
        tab2Button = findViewById(R.id.tab2Button);
        tab3Button = findViewById(R.id.tab3Button);
        tab4Button = findViewById(R.id.tab4Button);
        tab5Button = findViewById(R.id.tab5Button);
        tab6Button = findViewById(R.id.tab6Button);

        tab1Button.setOnClickListener(view -> startActivity(new Intent(this, MainActivity.class)));
        tab2Button.setOnClickListener(view -> startActivity(new Intent(this, Tab2Activity.class)));
        tab3Button.setOnClickListener(view -> startActivity(new Intent(this, Tab3Activity.class)));
        tab4Button.setOnClickListener(view -> startActivity(new Intent(this, Tab4Activity.class)));
        tab5Button.setOnClickListener(view -> startActivity(new Intent(this, Tab5Activity.class)));
        tab6Button.setOnClickListener(view -> startActivity(new Intent(this, Tab6Activity.class)));

        tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("농장"));
        tabLayout.addTab(tabLayout.newTab().setText("먹이"));

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        itemList = new ArrayList<>();
        loadItems("농장");  // 초기엔 농장 탭 아이템 로드

        // 여기서 itemClickListener를 람다로 넘김 (필요 없으면 빈 람다 가능)
        adapter = new ItemAdapter(itemList, this, item -> {
            // 농장 아이템 클릭 시 행동 (필요시 구현)
            // 예) Toast.makeText(this, item.name + " 클릭됨", Toast.LENGTH_SHORT).show();
        });
        recyclerView.setAdapter(adapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String category = tab.getText().toString();
                loadItems(category);
                adapter.updateList(itemList);
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void loadItems(String category) {
        itemList.clear();

        if (category.equals("농장")) {
            int farmImageRes = R.drawable.farm_item;
            for (int i = 1; i <= 30; i++) {
                boolean obtained = (i % 2 == 0);
                // Item 생성자 순서: name, category, count, imageRes, obtained
                itemList.add(new Item("농장 아이템 " + i, "농장", 0, farmImageRes, obtained));
            }
        } else if (category.equals("먹이")) {
            int feedImageRes = R.drawable.feed_item;
            boolean obtained = true;
            int count = prefs.getInt(KEY_FOOD_COUNT, 3);  // 저장된 먹이 개수 불러오기
            itemList.add(new Item("먹이 아이템", "먹이", count, feedImageRes, obtained));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (tabLayout.getSelectedTabPosition() == 1) { // 먹이 탭이 선택된 경우
            loadItems("먹이");
            adapter.updateList(itemList);
        }
    }
}
