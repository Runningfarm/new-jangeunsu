package kr.ac.hs.farm;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

public class RunningResult extends AppCompatActivity {

    private TextView tvTime, tvDistance, tvKcal, tvPace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_runningresult);

        // TextView 연결
        tvTime = findViewById(R.id.tvTime);
        tvDistance = findViewById(R.id.tvDistance);
        tvKcal = findViewById(R.id.tvKcal);
        tvPace = findViewById(R.id.tvPace);

        // Intent에서 데이터 받아오기
        Intent intent = getIntent();
        String time = intent.getStringExtra("time");
        String distance = intent.getStringExtra("distance");
        String kcal = intent.getStringExtra("kcal");
        String pace = intent.getStringExtra("pace");

        Log.d("RunningResult", "받은 time=" + time);
        Log.d("RunningResult", "받은 distance=" + distance);
        Log.d("RunningResult", "받은 kcal=" + kcal);
        Log.d("RunningResult", "받은 pace=" + pace);

        // 받아온 데이터 화면에 표시
        if (time != null) tvTime.setText(time);
        if (distance != null) tvDistance.setText(distance);
        if (kcal != null) tvKcal.setText(kcal);
        if (pace != null) tvPace.setText(pace);

        // 버튼 연결 및 클릭시 토스트 메시지 띄우고 Tab3Activity로 이동
        Button btnQuestReward = findViewById(R.id.btnQuestReward);
        btnQuestReward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(RunningResult.this, "퀘스트화면으로 이동!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RunningResult.this, Tab3Activity.class);
                startActivity(intent);
                finish(); // 현재 액티비티 종료
            }
        });
    }
}
