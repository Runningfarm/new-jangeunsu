package kr.ac.hs.farm;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class Tab5Activity extends AppCompatActivity {

    private TextView pointText;
    private TextView remainCountText;
    private ImageButton watchAdImageButton;
    private RecyclerView shopRecyclerView;
    private Button onePickButton, tenPickButton;

    // 하단 탭 버튼 (ImageButton으로 수정)
    private ImageButton tab1Button, tab2Button, tab3Button, tab4Button, tab5Button, tab6Button;

    private int point = 1000; // 예시 포인트 초기값
    private int remainCount = 5; // 남은 횟수 초기값

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab5); // 실제 레이아웃 파일명

        // 기본 UI 요소
        pointText = findViewById(R.id.pointText);
        remainCountText = findViewById(R.id.remainCountText);
        watchAdImageButton = findViewById(R.id.watchAdImageButton);
        shopRecyclerView = findViewById(R.id.shopRecyclerView);
        onePickButton = findViewById(R.id.onePickButton);
        tenPickButton = findViewById(R.id.tenPickButton);

        // 하단 탭 버튼 연결 (ImageButton으로 수정됨)
        tab1Button = findViewById(R.id.tab1Button);
        tab2Button = findViewById(R.id.tab2Button);
        tab3Button = findViewById(R.id.tab3Button);
        tab4Button = findViewById(R.id.tab4Button);
        tab5Button = findViewById(R.id.tab5Button);
        tab6Button = findViewById(R.id.tab6Button);

        // 포인트 및 횟수 UI 초기화
        updatePointText();
        updateRemainCountText();

        watchAdImageButton.setOnClickListener(v -> {
            if (remainCount > 0) {
                remainCount--;
                updateRemainCountText();
                Toast.makeText(Tab5Activity.this, "광고 시청 완료! 남은 횟수: " + remainCount, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(Tab5Activity.this, "남은 횟수가 없습니다!", Toast.LENGTH_SHORT).show();
            }
        });

        shopRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        shopRecyclerView.setAdapter(new ShopAdapter());

        onePickButton.setOnClickListener(v -> {
            Toast.makeText(this, "1개 뽑기 버튼 클릭", Toast.LENGTH_SHORT).show();
            // TODO: 가챠 1개 뽑기 기능 구현
        });

        tenPickButton.setOnClickListener(v -> {
            Toast.makeText(this, "10개 뽑기 버튼 클릭", Toast.LENGTH_SHORT).show();
            // TODO: 가챠 10개 뽑기 기능 구현
        });

        // 하단 탭 버튼 클릭 처리
        tab1Button.setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));
        tab2Button.setOnClickListener(v -> startActivity(new Intent(this, Tab2Activity.class)));
        tab3Button.setOnClickListener(v -> startActivity(new Intent(this, Tab3Activity.class)));
        tab4Button.setOnClickListener(v -> startActivity(new Intent(this, Tab4Activity.class)));
        tab5Button.setOnClickListener(v -> startActivity(new Intent(this, Tab5Activity.class)));
        tab6Button.setOnClickListener(v -> startActivity(new Intent(this, Tab6Activity.class)));
    }

    private void updatePointText() {
        pointText.setText("💰 " + point);
    }

    private void updateRemainCountText() {
        remainCountText.setText("남은 횟수: " + remainCount + "/5");
    }

    // 임시 어댑터 클래스
    private static class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.ShopViewHolder> {

        @Override
        public ShopViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_1, parent, false);
            return new ShopViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ShopViewHolder holder, int position) {
            // TODO: 아이템 바인딩
        }

        @Override
        public int getItemCount() {
            return 0; // 현재 아이템 없음
        }

        static class ShopViewHolder extends RecyclerView.ViewHolder {
            public ShopViewHolder(View itemView) {
                super(itemView);
            }
        }
    }
}
