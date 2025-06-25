package kr.ac.hs.farm;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "FarmPrefs";
    private static final String KEY_FOOD_COUNT = "foodCount";
    private static final String KEY_LEVEL = "level";
    private static final String KEY_EXPERIENCE = "experience";

    private ImageButton mailButton;
    private ImageButton characterButton;
    private LinearLayout characterMenu;
    private Button feedButton;
    private ImageButton exitButton;

    private ProgressBar levelProgressBar;
    private TextView levelText;
    private TextView foodCountText;

    private boolean isMenuVisible = false;

    private int foodCount = 3;   // 먹이 개수 초기값 (기본값)
    private int level = 1;       // 캐릭터 레벨 초기값
    private int experience = 0;  // 경험치 초기값

    private final int MAX_EXPERIENCE = 100;  // 한 레벨 올리는 데 필요한 경험치

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mailButton = findViewById(R.id.mailButton);
        characterButton = findViewById(R.id.characterButton);
        characterMenu = findViewById(R.id.characterMenu);
        feedButton = findViewById(R.id.feedButton);
        exitButton = findViewById(R.id.exitButton);

        levelProgressBar = findViewById(R.id.levelProgressBar);
        levelText = findViewById(R.id.levelText);

        // 먹이 개수 표시용 TextView (XML에 직접 추가 필요)
        foodCountText = findViewById(R.id.foodCountText);

        // 저장된 값 불러오기
        loadData();

        // 캐릭터 버튼 클릭 -> 메뉴 토글
        characterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleCharacterMenu();
            }
        });

        // 먹이주기 버튼 클릭
        feedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                giveFood();
            }
        });

        // 탭 버튼 클릭 시 각 액티비티 시작
        findViewById(R.id.tab1Button).setOnClickListener(view -> startActivity(new Intent(MainActivity.this, MainActivity.class)));
        findViewById(R.id.tab2Button).setOnClickListener(view -> startActivity(new Intent(MainActivity.this, Tab2Activity.class)));
        findViewById(R.id.tab3Button).setOnClickListener(view -> startActivity(new Intent(MainActivity.this, Tab3Activity.class)));
        findViewById(R.id.tab4Button).setOnClickListener(view -> startActivity(new Intent(MainActivity.this, Tab4Activity.class)));
        findViewById(R.id.tab5Button).setOnClickListener(view -> startActivity(new Intent(MainActivity.this, Tab5Activity.class)));
        findViewById(R.id.tab6Button).setOnClickListener(view -> startActivity(new Intent(MainActivity.this, Tab6Activity.class)));

        // 종료 버튼 클릭 시 종료 다이얼로그 표시
        exitButton.setOnClickListener(view -> showExitDialog());

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("reward")) {
            int reward = intent.getIntExtra("reward", 0);
            foodCount += reward;  // ✅ 기존 먹이에 추가
            saveData();           // ✅ 저장
            Toast.makeText(this, "보상으로 먹이 " + reward + "개를 받았습니다!", Toast.LENGTH_SHORT).show();
        }

        updateUI();
    }

    private void toggleCharacterMenu() {
        isMenuVisible = !isMenuVisible;
        characterMenu.setVisibility(isMenuVisible ? View.VISIBLE : View.GONE);
    }

    private void giveFood() {
        if (foodCount > 0) {
            foodCount--;
            experience += 20;  // 먹이 하나당 경험치 20 증가 (예시)
            Toast.makeText(this, "냥이에게 먹이를 줬어요! 남은 먹이: " + foodCount, Toast.LENGTH_SHORT).show();

            if (experience >= MAX_EXPERIENCE) {
                levelUp();
            }

            updateUI();
            saveData();  // 변경된 데이터 저장
        } else {
            Toast.makeText(this, "먹이가 부족합니다! 더 구입해 주세요.", Toast.LENGTH_SHORT).show();
        }
    }

    private void levelUp() {
        level++;
        experience = experience - MAX_EXPERIENCE;  // 남은 경험치는 다음 레벨 경험치로 전환
        Toast.makeText(this, "레벨업! 현재 레벨: " + level, Toast.LENGTH_SHORT).show();
    }

    private void updateUI() {
        // 레벨 텍스트 업데이트
        levelText.setText("LV " + level);

        // 경험치 프로그레스바 업데이트
        levelProgressBar.setMax(MAX_EXPERIENCE);
        levelProgressBar.setProgress(experience);

        // 먹이 개수 표시 (foodCountText는 XML에 직접 추가 필요)
        if (foodCountText != null) {
            foodCountText.setText("먹이: " + foodCount);
        }

        // 먹이 버튼 활성화 / 비활성화 처리
        if (foodCount <= 0) {
            feedButton.setEnabled(false);
            feedButton.setAlpha(0.5f);
        } else {
            feedButton.setEnabled(true);
            feedButton.setAlpha(1.0f);
        }
    }

    private void showExitDialog() {
        new AlertDialog.Builder(this)
                .setTitle("앱 종료")
                .setMessage("정말 종료하시겠습니까?")
                .setPositiveButton("종료", (dialog, which) -> {
                    saveData();  // 앱 종료 전 데이터 저장
                    finishAffinity();
                })  // 앱 완전 종료
                .setNegativeButton("취소", null) // 그냥 닫기
                .show();
    }

    // SharedPreferences에 데이터 저장
    private void saveData() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_FOOD_COUNT, foodCount);
        editor.putInt(KEY_LEVEL, level);
        editor.putInt(KEY_EXPERIENCE, experience);
        editor.apply();
    }

    // SharedPreferences에서 데이터 불러오기
    private void loadData() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        foodCount = prefs.getInt(KEY_FOOD_COUNT, 3);      // 기본값 3
        level = prefs.getInt(KEY_LEVEL, 1);                // 기본값 1
        experience = prefs.getInt(KEY_EXPERIENCE, 0);      // 기본값 0
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveData();  // 앱 일시정지 시 저장
    }
}