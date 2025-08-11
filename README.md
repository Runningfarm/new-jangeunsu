7/31 수정사항
파일 참고하시면서 하시면 될 것 같습니다
잘 모르시겠는 부분은 연락주세요

Tab2Activity.java
onCreate함수 안에
```java
protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab2);

        View rootView = findViewById(R.id.root_running);
        ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, insets) -> {
            Insets systemInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, systemInsets.top, 0, systemInsets.bottom);
            return insets;
        });

        SharedPreferences pref = getSharedPreferences("login", MODE_PRIVATE);
        weight = pref.getFloat("weight", 0f);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
```
이렇게 변경


loadQuestProgressFromServer()함수 안에	
```java
private void loadQuestProgressFromServer() {
        SharedPreferences pref = getSharedPreferences("login", MODE_PRIVATE);
        String id = pref.getString("id", null);

        if (id == null) return;

        ApiService api = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        String token = pref.getString("token", null); // 7/30
        Call<QuestProgressResponse> call = api.getQuestProgress("Bearer " + token); // 7/30
        call.enqueue(new Callback<QuestProgressResponse>() {
```
이렇게 변경		

activity_tab2.xml
63~71번째 줄에 text="3/5 완료"를 text=""로 변경		

User.js 수정함 파일 참고 	

index.js
app.get("/quest/progress", verifyToken, async (req, res) => { 윗줄에	
```java
// 날짜 구하는 함수(YYYY-MM-DD)
function getTodayStr() {
  const now = new Date();
  return now.toISOString().slice(0, 10);
}
```
추가

그리고 /quest/progress는 이렇게 수정
```java
app.get("/quest/progress", verifyToken, async (req, res) => {
  try {
    const user = await User.findOne({ id: req.user.id });
    if (!user)
      return res.status(404).json({ success: false, message: "유저 없음" });

    // 날짜 비교 후 리셋
    const today = getTodayStr();
    if (user.questDate !== today) {
      // 날짜가 다르면 모든 퀘스트 progress, completed 초기화
      user.quests.forEach((q) => {
        q.progress = 0;
        q.completed = false;
        q.completedAt = undefined;
	q.claimed = false;
      });
      user.questDate = today;
      await user.save();
    }

    res.json({
      success: true,
      quests: user.quests, // 전체 배열 내려줌
    });
  } catch (err) {
    res.status(500).json({ success: false, message: "서버 오류" });
  }
});		
```

app.post("/run/complete", verifyToken, async (req, res) => { 밑에
칼로리 처리 부분과 먹이 부분 사이에 
```java
// 40분 안에 5km 달리기
    let quest5km40 = user.quests.find((q) => q.type === "5km_40min");
    if (quest5km40 && !quest5km40.completed) {
      if (distance >= 5 && time <= 2400) {
        quest5km40.progress = 1;
        quest5km40.completed = true;
        quest5km40.completedAt = new Date();
      }
    }

    // 80분 안에 10km 달리기
    let quest10km80 = user.quests.find((q) => q.type === "10km_80min");
    if (quest10km80 && !quest10km80.completed) {
      if (distance >= 10 && time <= 4800) {
        quest10km80.progress = 1;
        quest10km80.completed = true;
        quest10km80.completedAt = new Date();
      }
    }

    // 30분 안에 5km 달리기
    let quest5km30 = user.quests.find((q) => q.type === "5km_30min");
    if (quest5km30 && !quest5km30.completed) {
      if (distance >= 5 && time <= 1800) {
        quest5km30.progress = 1;
        quest5km30.completed = true;
        quest5km30.completedAt = new Date();
      }
    }

    // 60분 안에 10km 달리기
    let quest10km60 = user.quests.find((q) => q.type === "10km_60min");
    if (quest10km60 && !quest10km60.completed) {
      if (distance >= 10 && time <= 3600) {
        quest10km60.progress = 1;
        quest10km60.completed = true;
        quest10km60.completedAt = new Date();
      }
    }
```
넣기		

app.post("/quest/claim", verifyToken, async (req, res) => { 밑에
const quest = user.quests[index]; 밑에
```java
// 1. 완료 안 됐으면 불가
    if (!quest || !quest.completed) {
      return res
        .status(400)
        .json({ success: false, message: "완료되지 않은 퀘스트" });
    }
    // 2. 이미 받은 보상(중복 지급 방지)
    if (quest.claimed) {
      return res
        .status(400)
        .json({ success: false, message: "이미 보상받은 퀘스트" });
    }
    // 3. 보상 지급!
    const reward = typeof quest.reward === "number" ? quest.reward : 10;
    user.totalFood += reward;
    quest.claimed = true;

    await user.save();
```
이렇게 수정		

app.post("/register", async (req, res) => { 안에  questDate: getTodayStr(), 추가
```java
// 유저 생성 및 저장
    const newUser = new User({
      id,
      password: hashedPassword,
      weight,
      name,
      questDate: getTodayStr(), // 추가!
    });		
```


Tab3Activity.java
30, 31번째줄 [9]를 둘 다 [13]으로 변경		

```
	progressBars[9] = findViewById(R.id.progressQuest10);
        progressBars[10] = findViewById(R.id.progressQuest11);
        progressBars[11] = findViewById(R.id.progressQuest12);
        progressBars[12] = findViewById(R.id.progressQuest13);


	claimButtons[9] = findViewById(R.id.btnClaim10);
        claimButtons[10] = findViewById(R.id.btnClaim11);
        claimButtons[11] = findViewById(R.id.btnClaim12);
        claimButtons[12] = findViewById(R.id.btnClaim13);
```
추가		

166번째 줄 퀘스트 보상 수령 실패가 아닌 퀘스트 중복 보상 받기 불가로 text변경

178번쨰 줄 for (int i = 0; i < Math.min(quests.size(), 13); i++) { 이렇게 변경 		

activity_tab3.xml 
```xml
<!-- 10번 퀘스트 -->
                    <androidx.cardview.widget.CardView
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:layout_marginBottom="12dp"
                        app:cardCornerRadius="14dp"
                        app:cardElevation="6dp"
                        android:background="#FFFFFF">

                        <LinearLayout
                            android:layout_width="match_parent"
                            android:layout_height="wrap_content"
                            android:orientation="horizontal"
                            android:padding="16dp"
                            android:gravity="center_vertical">

                            <ImageView
                                android:layout_width="40dp"
                                android:layout_height="40dp"
                                android:layout_marginEnd="16dp"
                                android:src="@drawable/trophy"
                                android:contentDescription="보상 이미지" />

                            <LinearLayout
                                android:layout_width="0dp"
                                android:layout_height="wrap_content"
                                android:layout_weight="1"
                                android:orientation="vertical">

                                <TextView
                                    android:layout_width="wrap_content"
                                    android:layout_height="wrap_content"
                                    android:text="5km 40분안에 완료 퀘스트"
                                    android:textColor="#5D7755"
                                    android:textSize="16sp"
                                    android:textStyle="bold"
                                    android:fontFamily="@font/gowundodum_regular" />

                                <FrameLayout
                                    android:layout_width="match_parent"
                                    android:layout_height="wrap_content"
                                    android:layout_marginTop="8dp">

                                    <ProgressBar
                                        android:id="@+id/progressQuest10"
                                        style="@android:style/Widget.DeviceDefault.Light.ProgressBar.Horizontal"
                                        android:layout_width="match_parent"
                                        android:layout_height="12dp"
                                        android:max="100"
                                        android:progress="30"
                                        android:progressDrawable="@drawable/progress_green_custom" />

                                    <ImageView
                                        android:id="@+id/boxReward10"
                                        android:layout_width="24dp"
                                        android:layout_height="24dp"
                                        android:layout_gravity="end|center_vertical"
                                        android:src="@drawable/box_locked"
                                        android:contentDescription="보상 상자" />
                                </FrameLayout>
                            </LinearLayout>

                            <Button
                                android:id="@+id/btnClaim10"
                                android:layout_width="wrap_content"
                                android:layout_height="wrap_content"
                                android:text="보상받기"
                                android:enabled="false"
                                android:textColor="#5D7755"
                                android:backgroundTint="#FFF7D1"
                                android:textStyle="bold"
                                android:layout_marginStart="12dp"
                                android:elevation="2dp" />
                        </LinearLayout>
                    </androidx.cardview.widget.CardView>


                    <!-- 11번 퀘스트 -->
                    <androidx.cardview.widget.CardView
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:layout_marginBottom="12dp"
                        app:cardCornerRadius="14dp"
                        app:cardElevation="6dp"
                        android:background="#FFFFFF">

                        <LinearLayout
                            android:layout_width="match_parent"
                            android:layout_height="wrap_content"
                            android:orientation="horizontal"
                            android:padding="16dp"
                            android:gravity="center_vertical">

                            <ImageView
                                android:layout_width="40dp"
                                android:layout_height="40dp"
                                android:layout_marginEnd="16dp"
                                android:src="@drawable/trophy"
                                android:contentDescription="보상 이미지" />

                            <LinearLayout
                                android:layout_width="0dp"
                                android:layout_height="wrap_content"
                                android:layout_weight="1"
                                android:orientation="vertical">

                                <TextView
                                    android:layout_width="wrap_content"
                                    android:layout_height="wrap_content"
                                    android:text="10km 80분안에 완료 퀘스트"
                                    android:textColor="#5D7755"
                                    android:textSize="16sp"
                                    android:textStyle="bold"
                                    android:fontFamily="@font/gowundodum_regular" />

                                <FrameLayout
                                    android:layout_width="match_parent"
                                    android:layout_height="wrap_content"
                                    android:layout_marginTop="8dp">

                                    <ProgressBar
                                        android:id="@+id/progressQuest11"
                                        style="@android:style/Widget.DeviceDefault.Light.ProgressBar.Horizontal"
                                        android:layout_width="match_parent"
                                        android:layout_height="12dp"
                                        android:max="100"
                                        android:progress="30"
                                        android:progressDrawable="@drawable/progress_green_custom" />

                                    <ImageView
                                        android:id="@+id/boxReward11"
                                        android:layout_width="24dp"
                                        android:layout_height="24dp"
                                        android:layout_gravity="end|center_vertical"
                                        android:src="@drawable/box_locked"
                                        android:contentDescription="보상 상자" />
                                </FrameLayout>
                            </LinearLayout>

                            <Button
                                android:id="@+id/btnClaim11"
                                android:layout_width="wrap_content"
                                android:layout_height="wrap_content"
                                android:text="보상받기"
                                android:enabled="false"
                                android:textColor="#5D7755"
                                android:backgroundTint="#FFF7D1"
                                android:textStyle="bold"
                                android:layout_marginStart="12dp"
                                android:elevation="2dp" />
                        </LinearLayout>
                    </androidx.cardview.widget.CardView>


                    <!-- 12번 퀘스트 -->
                    <androidx.cardview.widget.CardView
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:layout_marginBottom="12dp"
                        app:cardCornerRadius="14dp"
                        app:cardElevation="6dp"
                        android:background="#FFFFFF">

                        <LinearLayout
                            android:layout_width="match_parent"
                            android:layout_height="wrap_content"
                            android:orientation="horizontal"
                            android:padding="16dp"
                            android:gravity="center_vertical">

                            <ImageView
                                android:layout_width="40dp"
                                android:layout_height="40dp"
                                android:layout_marginEnd="16dp"
                                android:src="@drawable/trophy"
                                android:contentDescription="보상 이미지" />

                            <LinearLayout
                                android:layout_width="0dp"
                                android:layout_height="wrap_content"
                                android:layout_weight="1"
                                android:orientation="vertical">

                                <TextView
                                    android:layout_width="wrap_content"
                                    android:layout_height="wrap_content"
                                    android:text="5km 30분안에 완료 퀘스트"
                                    android:textColor="#5D7755"
                                    android:textSize="16sp"
                                    android:textStyle="bold"
                                    android:fontFamily="@font/gowundodum_regular" />

                                <FrameLayout
                                    android:layout_width="match_parent"
                                    android:layout_height="wrap_content"
                                    android:layout_marginTop="8dp">

                                    <ProgressBar
                                        android:id="@+id/progressQuest12"
                                        style="@android:style/Widget.DeviceDefault.Light.ProgressBar.Horizontal"
                                        android:layout_width="match_parent"
                                        android:layout_height="12dp"
                                        android:max="100"
                                        android:progress="30"
                                        android:progressDrawable="@drawable/progress_green_custom" />

                                    <ImageView
                                        android:id="@+id/boxReward12"
                                        android:layout_width="24dp"
                                        android:layout_height="24dp"
                                        android:layout_gravity="end|center_vertical"
                                        android:src="@drawable/box_locked"
                                        android:contentDescription="보상 상자" />
                                </FrameLayout>
                            </LinearLayout>

                            <Button
                                android:id="@+id/btnClaim12"
                                android:layout_width="wrap_content"
                                android:layout_height="wrap_content"
                                android:text="보상받기"
                                android:enabled="false"
                                android:textColor="#5D7755"
                                android:backgroundTint="#FFF7D1"
                                android:textStyle="bold"
                                android:layout_marginStart="12dp"
                                android:elevation="2dp" />
                        </LinearLayout>
                    </androidx.cardview.widget.CardView>


                    <!-- 13번 퀘스트 -->
                    <androidx.cardview.widget.CardView
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:layout_marginBottom="12dp"
                        app:cardCornerRadius="14dp"
                        app:cardElevation="6dp"
                        android:background="#FFFFFF">

                        <LinearLayout
                            android:layout_width="match_parent"
                            android:layout_height="wrap_content"
                            android:orientation="horizontal"
                            android:padding="16dp"
                            android:gravity="center_vertical">

                            <ImageView
                                android:layout_width="40dp"
                                android:layout_height="40dp"
                                android:layout_marginEnd="16dp"
                                android:src="@drawable/trophy"
                                android:contentDescription="보상 이미지" />

                            <LinearLayout
                                android:layout_width="0dp"
                                android:layout_height="wrap_content"
                                android:layout_weight="1"
                                android:orientation="vertical">

                                <TextView
                                    android:layout_width="wrap_content"
                                    android:layout_height="wrap_content"
                                    android:text="10km 60분안에 완료 퀘스트"
                                    android:textColor="#5D7755"
                                    android:textSize="16sp"
                                    android:textStyle="bold"
                                    android:fontFamily="@font/gowundodum_regular" />

                                <FrameLayout
                                    android:layout_width="match_parent"
                                    android:layout_height="wrap_content"
                                    android:layout_marginTop="8dp">

                                    <ProgressBar
                                        android:id="@+id/progressQuest13"
                                        style="@android:style/Widget.DeviceDefault.Light.ProgressBar.Horizontal"
                                        android:layout_width="match_parent"
                                        android:layout_height="12dp"
                                        android:max="100"
                                        android:progress="30"
                                        android:progressDrawable="@drawable/progress_green_custom" />

                                    <ImageView
                                        android:id="@+id/boxReward13"
                                        android:layout_width="24dp"
                                        android:layout_height="24dp"
                                        android:layout_gravity="end|center_vertical"
                                        android:src="@drawable/box_locked"
                                        android:contentDescription="보상 상자" />
                                </FrameLayout>
                            </LinearLayout>

                            <Button
                                android:id="@+id/btnClaim13"
                                android:layout_width="wrap_content"
                                android:layout_height="wrap_content"
                                android:text="보상받기"
                                android:enabled="false"
                                android:textColor="#5D7755"
                                android:backgroundTint="#FFF7D1"
                                android:textStyle="bold"
                                android:layout_marginStart="12dp"
                                android:elevation="2dp" />
                        </LinearLayout>
                    </androidx.cardview.widget.CardView>
```
퀘스트 10,11,12,13 추가

#
<h1>8/11 수정사항</h1>

User.js
```
const userSchema = new mongoose.Schema({
  id: { type: String, required: true, unique: true },
  password: { type: String, required: true },
  weight: { type: Number }, // kg
  name: { type: String },
  totalDistance: { type: Number, default: 0 },
  totalFood: { type: Number, default: 0 },
  totalCalories: { type: Number, default: 0 },
```
바로 밑에 줄에
```
totalRunTime: { type: Number, default: 0 },
```
추가

index.js
```
//먹이
user.totalFood += Math.floor(distance * 10);
```
밑에 줄에
``` 
 // 총 누적 달리기 시간(초) 누적
    if (typeof time === "number" && time > 0) {
      user.totalRunTime = (user.totalRunTime || 0) + time;
    }
```
추가

app.get("/myfarm", verifyToken, async (req, res) => { 안에
```
res.json({
      success: true,
      message: "농장 정보 가져오기 성공!",
      id: user.id,
      weight: user.weight,
      totalDistance: user.totalDistance,
      totalFood: user.totalFood,
```
바로 밑에 줄에
```
totalRunTime: user.totalRunTime || 0,
```
추가

app.post("/login", async (req, res) => { 안에
```
res.json({
      success: true,
      message: "로그인 성공!",
      token,
      id: user.id,
      name: user.name,
      weight: user.weight,
      totalDistance: user.totalDistance,
      totalFood: user.totalFood,
      questsCompleted: user.questsCompleted,
    });
```
에서 totalFood: user.totalFood,와 questsCompleted: user.questsCompleted, 사이에
```
totalRunTime: user.totalRunTime || 0,
```
추가

LoginResponse.java
```
private long totalRunTime;
```
```
public long getTotalRunTime() {
        return totalRunTime;
    }
```
추가

EditProfileActivity.java
```
import android.widget.Toast;
```
밑에 줄에
```
import android.widget.TextView;
```
추가

```
private Button buttonUpdate;
```
밑에 줄에
```
private TextView tvTotalRunTimeProfile;
```
추가

```
buttonUpdate = findViewById(R.id.buttonUpdate);
```
밑에 줄에
```
tvTotalRunTimeProfile = findViewById(R.id.tvTotalRunTimeProfile);
```
추가

```
float weight = pref.getFloat("weight", 0f);
```
밑에 줄에
```
long totalRunSecs = pref.getLong("total_run_time_seconds", 0L);
        tvTotalRunTimeProfile.setText(formatSecondsToHMS(totalRunSecs));
```
추가


onCreate 함수 바깥쪽에 제일 밑에
```
private String formatSecondsToHMS(long seconds) {
        long h = seconds / 3600;
        long m = (seconds % 3600) / 60;
        long s = seconds % 60;
        return String.format("%02d:%02d:%02d", h, m, s);
    }
```
추가

Tab2Activity.java
```
stopRunning();
```
밑에 줄에
```
long prev = pref.getLong("total_run_time_seconds", 0L);
                        long add = elapsedTime / 1000L;   // 이번 러닝 소요 시간(초)
                        pref.edit().putLong("total_run_time_seconds", prev + add).apply();
```
추가


