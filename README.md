<h1>Tab2Activity.java, Tab3Activity.java, activity_tab3.xml는 파일 밖에 나와있는거로 확인해주세요!</h1>	
<h3>하실때 Ctrl + F로 찾으면서 하시면 더 편하실거같습니다!</h3>
<br>
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

<h1>8/12 수정사항</h1>
index.js

```
// 날짜 비교 후 리셋
const today = getTodayStr();
if (user.questDate !== today) {
  // 날짜가 다르면 (단, 누적시간 퀘스트는 제외!)
  user.quests.forEach((q) => {
```
밑에 줄에
```
if (q.type === "time_total") return; // ← 누적시간 퀘스트는 초기화 금지
```
추가

```
// 총 누적 달리기 시간(초) 누적
    if (typeof time === "number" && time > 0) {
      user.totalRunTime = (user.totalRunTime || 0) + time;
    }
```
밑에 줄에
```
// 누적시간 퀘스트(type: "time_total") 진행/완료 처리 (단위: 초)
    user.quests.forEach((q) => {
      if (q.type === "time_total" && !q.completed) {
        q.progress += time; // 초 단위로 누적
        if (q.progress >= q.target) {
          q.progress = q.target;
          q.completed = true;
          q.completedAt = new Date();
        }
      }
    });
```
추가하고 5줄 밑에
```
res.json({
      success: true,
      message: "런닝 결과 저장+퀘스트 반영 완료!",
      quests: user.quests,
      totalDistance: user.totalDistance,
      totalFood: user.totalFood,
      totalCalories: user.totalCalories,
```
이 부분 밑에 줄에
```
totalRunTime: user.totalRunTime || 0,
```
추가


User.js
```
// 13번째: 60분 안에 10km 달리기
      {
        type: "10km_60min",
        target: 1,
        progress: 0,
        completed: false,
        reward: 15,
        distance: 10,
        timeLimit: 3600, // 1시간 = 3600초
      },
```
밑에 줄에
```
// 누적시간 퀘스트 (단위: 초)
      // 10시간
      {
        type: "time_total",
        target: 10 * 3600,
        progress: 0,
        completed: false,
        reward: 5,
        claimed: false,
      },
      // 30시간
      {
        type: "time_total",
        target: 30 * 3600,
        progress: 0,
        completed: false,
        reward: 10,
        claimed: false,
      },
      // 50시간
      {
        type: "time_total",
        target: 50 * 3600,
        progress: 0,
        completed: false,
        reward: 15,
        claimed: false,
      },
      // 100시간
      {
        type: "time_total",
        target: 100 * 3600,
        progress: 0,
        completed: false,
        reward: 25,
        claimed: false,
      },
```
추가


Tab2Activity.java

```
private void sendRunResultToServer() {
```
밑에 줄에
```
// 앱 터짐 방지
        if (polylineOptions == null) {
            polylineOptions = new PolylineOptions();
        }
```
추가

Tab3Activity.java
```
private ProgressBar[] progressBars = new ProgressBar[13];
```
을
```
private ProgressBar[] progressBars = new ProgressBar[17];
```
로 변경

```
private Button[] claimButtons = new Button[13];
```
을
```
private Button[] claimButtons = new Button[17];
```
로 변경

```
progressBars[12] = findViewById(R.id.progressQuest13);
```
밑에
```
progressBars[13] = findViewById(R.id.progressQuest14);
        progressBars[14] = findViewById(R.id.progressQuest15);
        progressBars[15] = findViewById(R.id.progressQuest16);
        progressBars[16] = findViewById(R.id.progressQuest17);
```
추가

```
claimButtons[12] = findViewById(R.id.btnClaim13);
```
밑에
```
claimButtons[13] = findViewById(R.id.btnClaim14);
        claimButtons[14] = findViewById(R.id.btnClaim15);
        claimButtons[15] = findViewById(R.id.btnClaim16);
        claimButtons[16] = findViewById(R.id.btnClaim17);
```
추가

```
private void updateQuestUI(List<QuestProgressResponse.Quest> quests) {
        for (int i = 0; i < Math.min(quests.size(), 13); i++) {
```
를
```
private void updateQuestUI(List<QuestProgressResponse.Quest> quests) {
        for (int i = 0; i < Math.min(quests.size(), 17); i++) {
```
로 변경


activity_tab3.xml
퀘스트 4개 추가
```
<!-- 14번 퀘스트 -->
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
                                    android:text="누적 10시간 완료 퀘스트"
                                    android:textColor="#5D7755"
                                    android:textSize="16sp"
                                    android:textStyle="bold"
                                    android:fontFamily="@font/gowundodum_regular" />

                                <FrameLayout
                                    android:layout_width="match_parent"
                                    android:layout_height="wrap_content"
                                    android:layout_marginTop="8dp">

                                    <ProgressBar
                                        android:id="@+id/progressQuest14"
                                        style="@android:style/Widget.DeviceDefault.Light.ProgressBar.Horizontal"
                                        android:layout_width="match_parent"
                                        android:layout_height="12dp"
                                        android:max="100"
                                        android:progress="30"
                                        android:progressDrawable="@drawable/progress_green_custom" />

                                    <ImageView
                                        android:id="@+id/boxReward14"
                                        android:layout_width="24dp"
                                        android:layout_height="24dp"
                                        android:layout_gravity="end|center_vertical"
                                        android:src="@drawable/box_locked"
                                        android:contentDescription="보상 상자" />
                                </FrameLayout>
                            </LinearLayout>

                            <Button
                                android:id="@+id/btnClaim14"
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


                    <!-- 15번 퀘스트 -->
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
                                    android:text="누적 30시간 완료 퀘스트"
                                    android:textColor="#5D7755"
                                    android:textSize="16sp"
                                    android:textStyle="bold"
                                    android:fontFamily="@font/gowundodum_regular" />

                                <FrameLayout
                                    android:layout_width="match_parent"
                                    android:layout_height="wrap_content"
                                    android:layout_marginTop="8dp">

                                    <ProgressBar
                                        android:id="@+id/progressQuest15"
                                        style="@android:style/Widget.DeviceDefault.Light.ProgressBar.Horizontal"
                                        android:layout_width="match_parent"
                                        android:layout_height="12dp"
                                        android:max="100"
                                        android:progress="30"
                                        android:progressDrawable="@drawable/progress_green_custom" />

                                    <ImageView
                                        android:id="@+id/boxReward15"
                                        android:layout_width="24dp"
                                        android:layout_height="24dp"
                                        android:layout_gravity="end|center_vertical"
                                        android:src="@drawable/box_locked"
                                        android:contentDescription="보상 상자" />
                                </FrameLayout>
                            </LinearLayout>

                            <Button
                                android:id="@+id/btnClaim15"
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


                    <!-- 16번 퀘스트 -->
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
                                    android:text="누적 50시간 완료 퀘스트"
                                    android:textColor="#5D7755"
                                    android:textSize="16sp"
                                    android:textStyle="bold"
                                    android:fontFamily="@font/gowundodum_regular" />

                                <FrameLayout
                                    android:layout_width="match_parent"
                                    android:layout_height="wrap_content"
                                    android:layout_marginTop="8dp">

                                    <ProgressBar
                                        android:id="@+id/progressQuest16"
                                        style="@android:style/Widget.DeviceDefault.Light.ProgressBar.Horizontal"
                                        android:layout_width="match_parent"
                                        android:layout_height="12dp"
                                        android:max="100"
                                        android:progress="30"
                                        android:progressDrawable="@drawable/progress_green_custom" />

                                    <ImageView
                                        android:id="@+id/boxReward16"
                                        android:layout_width="24dp"
                                        android:layout_height="24dp"
                                        android:layout_gravity="end|center_vertical"
                                        android:src="@drawable/box_locked"
                                        android:contentDescription="보상 상자" />
                                </FrameLayout>
                            </LinearLayout>

                            <Button
                                android:id="@+id/btnClaim16"
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


                    <!-- 17번 퀘스트 -->
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
                                    android:text="누적 100시간 완료 퀘스트"
                                    android:textColor="#5D7755"
                                    android:textSize="16sp"
                                    android:textStyle="bold"
                                    android:fontFamily="@font/gowundodum_regular" />

                                <FrameLayout
                                    android:layout_width="match_parent"
                                    android:layout_height="wrap_content"
                                    android:layout_marginTop="8dp">

                                    <ProgressBar
                                        android:id="@+id/progressQuest17"
                                        style="@android:style/Widget.DeviceDefault.Light.ProgressBar.Horizontal"
                                        android:layout_width="match_parent"
                                        android:layout_height="12dp"
                                        android:max="100"
                                        android:progress="30"
                                        android:progressDrawable="@drawable/progress_green_custom" />

                                    <ImageView
                                        android:id="@+id/boxReward17"
                                        android:layout_width="24dp"
                                        android:layout_height="24dp"
                                        android:layout_gravity="end|center_vertical"
                                        android:src="@drawable/box_locked"
                                        android:contentDescription="보상 상자" />
                                </FrameLayout>
                            </LinearLayout>

                            <Button
                                android:id="@+id/btnClaim17"
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

<h1>8/22 수정사항</h1>
index.js

```
app.get("/myfarm", verifyToken, async (req, res) => {
안에
res.json({
      success: true,
      message: "농장 정보 가져오기 성공!",
      id: user.id,
      weight: user.weight,
      totalDistance: user.totalDistance,
      totalFood: user.totalFood,
      totalRunTime: user.totalRunTime || 0,
```
이 부분
```
res.json({
      success: true,
      message: "농장 정보 가져오기 성공!",
      id: user.id,
      weight: user.weight,
      totalDistance: user.totalDistance || 0,
      totalFood: user.totalFood,
      totalCalories: user.totalCalories || 0,
      totalRunTime: user.totalRunTime || 0,
```
이렇게 수정


app.post("/run/complete", verifyToken, async (req, res) => {
안에
```
res.json({
      success: true,
      message: "런닝 결과 저장+퀘스트 반영 완료!",
      quests: user.quests,
      totalDistance: user.totalDistance || 0,
      totalFood: user.totalFood,
      totalCalories: user.totalCalories || 0,
      totalRunTime: user.totalRunTime || 0,
```
이렇게 수정


app.post("/login", async (req, res) => {
안에
```
res.json({
      success: true,
      message: "로그인 성공!",
      token,
      id: user.id,
      name: user.name,
      weight: user.weight,
      totalDistance: user.totalDistance|| 0,
      totalFood: user.totalFood,
      totalCalories: user.totalCalories || 0,
      totalRunTime: user.totalRunTime || 0,
      questsCompleted: user.questsCompleted,
```
이렇게 수정


activity_edit_profile.xml
```
<!-- [신규] 총 누적 달리기 시간 카드 -->
        <LinearLayout
            android:id="@+id/totalTimeCardProfile"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="vertical"
            android:background="#EAF6E9"
            android:padding="16dp"
            android:elevation="2dp"
            android:layout_marginBottom="16dp"
            android:layout_marginTop="4dp"
            android:backgroundTint="#EAF6E9">

            <TextView
                android:id="@+id/tvTotalLabelProfile"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="총 누적 달리기 시간"
                android:textColor="#5D7755"
                android:textSize="14sp"
                android:textStyle="bold"
                android:fontFamily="@font/nanumgothic_regular" />

            <TextView
                android:id="@+id/tvTotalRunTimeProfile"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_marginTop="6dp"
                android:text="00:00:00"
                android:textColor="#347EBF"
                android:textSize="22sp"
                android:textStyle="bold"
                android:fontFamily="@font/nanumgothic_regular" />
        </LinearLayout>
```
밑에
```
<!-- [신규] 총 누적 거리 카드 -->
        <LinearLayout
            android:id="@+id/totalDistanceCardProfile"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="vertical"
            android:background="#EAF6E9"
            android:padding="16dp"
            android:elevation="2dp"
            android:layout_marginBottom="16dp"
            android:backgroundTint="#EAF6E9">

            <TextView
                android:id="@+id/tvDistanceLabelProfile"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="총 누적 거리"
                android:textColor="#5D7755"
                android:textSize="14sp"
                android:textStyle="bold"
                android:fontFamily="@font/nanumgothic_regular" />

            <TextView
                android:id="@+id/tvTotalDistanceProfile"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_marginTop="6dp"
                android:text="0.00 km"
                android:textColor="#347EBF"
                android:textSize="22sp"
                android:textStyle="bold"
                android:fontFamily="@font/nanumgothic_regular" />
        </LinearLayout>

        <!-- [신규] 총 누적 칼로리 카드 -->
        <LinearLayout
            android:id="@+id/totalCaloriesCardProfile"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="vertical"
            android:background="#EAF6E9"
            android:padding="16dp"
            android:elevation="2dp"
            android:layout_marginBottom="16dp"
            android:backgroundTint="#EAF6E9">

            <TextView
                android:id="@+id/tvCaloriesLabelProfile"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="총 누적 칼로리"
                android:textColor="#5D7755"
                android:textSize="14sp"
                android:textStyle="bold"
                android:fontFamily="@font/nanumgothic_regular" />

            <TextView
                android:id="@+id/tvTotalCaloriesProfile"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_marginTop="6dp"
                android:text="0 kcal"
                android:textColor="#347EBF"
                android:textSize="22sp"
                android:textStyle="bold"
                android:fontFamily="@font/nanumgothic_regular" />
        </LinearLayout>
```
추가


LoginResponse.java
```
private double totalDistance;
private int totalCalories;
```
추가
```
public double getTotalDistance() { return totalDistance; }
public int getTotalCalories() { return totalCalories; }
```
추가


EditProfileActivity.java
```
import retrofit2.Response;
```
밑에
```
import retrofit2.http.Header;
```
추가
<br>
```
private TextView tvTotalRunTimeProfile;
```
밑에
```
private TextView tvTotalDistanceProfile;
private TextView tvTotalCaloriesProfile;
```
추가
<br>
```
tvTotalRunTimeProfile = findViewById(R.id.tvTotalRunTimeProfile);
```
밑에
```
tvTotalDistanceProfile  = findViewById(R.id.tvTotalDistanceProfile);
tvTotalCaloriesProfile  = findViewById(R.id.tvTotalCaloriesProfile);
```
추가
```
long totalRunSecs = pref.getLong("total_run_time_seconds", 0L);
tvTotalRunTimeProfile.setText(formatSecondsToHMS(totalRunSecs));
```
이 부분을
```
long totalRunSecs = pref.getLong("total_run_time_seconds", 0L);
        float totalDistance = pref.getFloat("total_distance", 0f);
        long totalCalories = pref.getLong("total_calories", 0L);
        tvTotalRunTimeProfile.setText(formatSecondsToHMS(totalRunSecs));
        tvTotalDistanceProfile.setText(totalDistance + " km");
        tvTotalCaloriesProfile.setText(totalCalories + " kcal");
```
이렇게 수정


User.js
```
// 100시간
      {
        type: "time_total",
        target: 100 * 3600,
        progress: 0,
        completed: false,
        reward: 25,
        claimed: false,
      },
```
밑에
```
// 누적거리 퀘스트
      // 100km
      {
        type: "distance_total",
        target: 100,
        progress: 0,
        completed: false,
        reward: 15,
      },
      // 500km
      {
        type: "distance_total",
        target: 500,
        progress: 0,
        completed: false,
        reward: 30,
      },
      // 1000km
      {
        type: "distance_total",
        target: 1000,
        progress: 0,
        completed: false,
        reward: 50,
      },
      // 누적 칼로리 퀘스트
      // 10,000 kcal
      {
        type: "calorie_total",
        target: 10000,
        progress: 0,
        completed: false,
        reward: 20,
      },
      // 50,000 kcal
      {
        type: "calorie_total",
        target: 50000,
        progress: 0,
        completed: false,
        reward: 40,
      },
      // 100,000 kcal
      {
        type: "calorie_total",
        target: 100000,
        progress: 0,
        completed: false,
        reward: 70,
      },
```
추가


index.js
```
// 총 누적 달리기 시간(초) 누적
    if (typeof time === "number" && time > 0) {
      user.totalRunTime = (user.totalRunTime || 0) + time;
    }
```
밑에
```
// 누적시간 퀘스트(type: "time_total") 진행/완료 처리 (단위: 초)
    user.quests.forEach((q) => {
      if (q.type === "time_total" && !q.completed) {
        q.progress += user.totalRunTime;
        if (q.progress >= q.target) {
          q.progress = q.target;
          q.completed = true;
          q.completedAt = new Date();
        }
      }
    });

    // 누적 거리 퀘스트 진행도
    user.quests.forEach((q) => {
      if (q.type === "distance_total" && !q.completed) {
        q.progress = user.totalDistance;
        if (q.progress >= q.target) {
          q.progress = q.target;
          q.completed = true;
          q.completedAt = new Date();
        }
      }
    });

    // 누적 칼로리 퀘스트 진행도
    user.quests.forEach((q) => {
      if (q.type === "calorie_total" && !q.completed) {
        q.progress = user.totalCalories;
        if (q.progress >= q.target) {
          q.progress = q.target;
          q.completed = true;
          q.completedAt = new Date();
        }
      }
    });
```
이렇게 수정 및 추가

Tab3Activity.java
```
private ProgressBar[] progressBars = new ProgressBar[17];
```
를
```
private ProgressBar[] progressBars = new ProgressBar[23];
```
로 수정

```
private Button[] claimButtons = new Button[17];
```
를
```
private Button[] claimButtons = new Button[23];
```
로 수정

```
progressBars[17] = findViewById(R.id.progressQuest18);
        progressBars[18] = findViewById(R.id.progressQuest19);
        progressBars[19] = findViewById(R.id.progressQuest20);
        progressBars[20] = findViewById(R.id.progressQuest21);
        progressBars[21] = findViewById(R.id.progressQuest22);
        progressBars[22] = findViewById(R.id.progressQuest23);
```
추가

```
claimButtons[17] = findViewById(R.id.btnClaim18);
        claimButtons[18] = findViewById(R.id.btnClaim19);
        claimButtons[19] = findViewById(R.id.btnClaim20);
        claimButtons[20] = findViewById(R.id.btnClaim21);
        claimButtons[21] = findViewById(R.id.btnClaim22);
        claimButtons[22] = findViewById(R.id.btnClaim23);
```
추가

```
for (int i = 0; i < Math.min(quests.size(), 17); i++) {
```
를
```
for (int i = 0; i < Math.min(quests.size(), 23); i++) {
```
로 수정


activity_tab3.xml
퀘스트 6개 추가
```
 <!-- 18번 퀘스트 -->
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
                                    android:text="누적 100km 완료 퀘스트"
                                    android:textColor="#5D7755"
                                    android:textSize="16sp"
                                    android:textStyle="bold"
                                    android:fontFamily="@font/gowundodum_regular" />

                                <FrameLayout
                                    android:layout_width="match_parent"
                                    android:layout_height="wrap_content"
                                    android:layout_marginTop="8dp">

                                    <ProgressBar
                                        android:id="@+id/progressQuest18"
                                        style="@android:style/Widget.DeviceDefault.Light.ProgressBar.Horizontal"
                                        android:layout_width="match_parent"
                                        android:layout_height="12dp"
                                        android:max="100"
                                        android:progress="30"
                                        android:progressDrawable="@drawable/progress_green_custom" />

                                    <ImageView
                                        android:id="@+id/boxReward18"
                                        android:layout_width="24dp"
                                        android:layout_height="24dp"
                                        android:layout_gravity="end|center_vertical"
                                        android:src="@drawable/box_locked"
                                        android:contentDescription="보상 상자" />
                                </FrameLayout>
                            </LinearLayout>

                            <Button
                                android:id="@+id/btnClaim18"
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

                    <!-- 19번 퀘스트 -->
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
                                    android:text="누적 500km 완료 퀘스트"
                                    android:textColor="#5D7755"
                                    android:textSize="16sp"
                                    android:textStyle="bold"
                                    android:fontFamily="@font/gowundodum_regular" />

                                <FrameLayout
                                    android:layout_width="match_parent"
                                    android:layout_height="wrap_content"
                                    android:layout_marginTop="8dp">

                                    <ProgressBar
                                        android:id="@+id/progressQuest19"
                                        style="@android:style/Widget.DeviceDefault.Light.ProgressBar.Horizontal"
                                        android:layout_width="match_parent"
                                        android:layout_height="12dp"
                                        android:max="100"
                                        android:progress="30"
                                        android:progressDrawable="@drawable/progress_green_custom" />

                                    <ImageView
                                        android:id="@+id/boxReward19"
                                        android:layout_width="24dp"
                                        android:layout_height="24dp"
                                        android:layout_gravity="end|center_vertical"
                                        android:src="@drawable/box_locked"
                                        android:contentDescription="보상 상자" />
                                </FrameLayout>
                            </LinearLayout>

                            <Button
                                android:id="@+id/btnClaim19"
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

                    <!-- 20번 퀘스트 -->
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
                                    android:text="누적 1000km 완료 퀘스트"
                                    android:textColor="#5D7755"
                                    android:textSize="16sp"
                                    android:textStyle="bold"
                                    android:fontFamily="@font/gowundodum_regular" />

                                <FrameLayout
                                    android:layout_width="match_parent"
                                    android:layout_height="wrap_content"
                                    android:layout_marginTop="8dp">

                                    <ProgressBar
                                        android:id="@+id/progressQuest20"
                                        style="@android:style/Widget.DeviceDefault.Light.ProgressBar.Horizontal"
                                        android:layout_width="match_parent"
                                        android:layout_height="12dp"
                                        android:max="100"
                                        android:progress="30"
                                        android:progressDrawable="@drawable/progress_green_custom" />

                                    <ImageView
                                        android:id="@+id/boxReward20"
                                        android:layout_width="24dp"
                                        android:layout_height="24dp"
                                        android:layout_gravity="end|center_vertical"
                                        android:src="@drawable/box_locked"
                                        android:contentDescription="보상 상자" />
                                </FrameLayout>
                            </LinearLayout>

                            <Button
                                android:id="@+id/btnClaim20"
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

                    <!-- 21번 퀘스트 -->
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
                                    android:text="누적 10,000kcal 완료 퀘스트"
                                    android:textColor="#5D7755"
                                    android:textSize="16sp"
                                    android:textStyle="bold"
                                    android:fontFamily="@font/gowundodum_regular" />

                                <FrameLayout
                                    android:layout_width="match_parent"
                                    android:layout_height="wrap_content"
                                    android:layout_marginTop="8dp">

                                    <ProgressBar
                                        android:id="@+id/progressQuest21"
                                        style="@android:style/Widget.DeviceDefault.Light.ProgressBar.Horizontal"
                                        android:layout_width="match_parent"
                                        android:layout_height="12dp"
                                        android:max="100"
                                        android:progress="30"
                                        android:progressDrawable="@drawable/progress_green_custom" />

                                    <ImageView
                                        android:id="@+id/boxReward21"
                                        android:layout_width="24dp"
                                        android:layout_height="24dp"
                                        android:layout_gravity="end|center_vertical"
                                        android:src="@drawable/box_locked"
                                        android:contentDescription="보상 상자" />
                                </FrameLayout>
                            </LinearLayout>

                            <Button
                                android:id="@+id/btnClaim21"
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

                    <!-- 22번 퀘스트 -->
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
                                    android:text="누적 50,000kcal 완료 퀘스트"
                                    android:textColor="#5D7755"
                                    android:textSize="16sp"
                                    android:textStyle="bold"
                                    android:fontFamily="@font/gowundodum_regular" />

                                <FrameLayout
                                    android:layout_width="match_parent"
                                    android:layout_height="wrap_content"
                                    android:layout_marginTop="8dp">

                                    <ProgressBar
                                        android:id="@+id/progressQuest22"
                                        style="@android:style/Widget.DeviceDefault.Light.ProgressBar.Horizontal"
                                        android:layout_width="match_parent"
                                        android:layout_height="12dp"
                                        android:max="100"
                                        android:progress="30"
                                        android:progressDrawable="@drawable/progress_green_custom" />

                                    <ImageView
                                        android:id="@+id/boxReward22"
                                        android:layout_width="24dp"
                                        android:layout_height="24dp"
                                        android:layout_gravity="end|center_vertical"
                                        android:src="@drawable/box_locked"
                                        android:contentDescription="보상 상자" />
                                </FrameLayout>
                            </LinearLayout>

                            <Button
                                android:id="@+id/btnClaim22"
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

                    <!-- 23번 퀘스트 -->
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
                                    android:text="누적 100,000kcal 완료 퀘스트"
                                    android:textColor="#5D7755"
                                    android:textSize="16sp"
                                    android:textStyle="bold"
                                    android:fontFamily="@font/gowundodum_regular" />

                                <FrameLayout
                                    android:layout_width="match_parent"
                                    android:layout_height="wrap_content"
                                    android:layout_marginTop="8dp">

                                    <ProgressBar
                                        android:id="@+id/progressQuest23"
                                        style="@android:style/Widget.DeviceDefault.Light.ProgressBar.Horizontal"
                                        android:layout_width="match_parent"
                                        android:layout_height="12dp"
                                        android:max="100"
                                        android:progress="30"
                                        android:progressDrawable="@drawable/progress_green_custom" />

                                    <ImageView
                                        android:id="@+id/boxReward23"
                                        android:layout_width="24dp"
                                        android:layout_height="24dp"
                                        android:layout_gravity="end|center_vertical"
                                        android:src="@drawable/box_locked"
                                        android:contentDescription="보상 상자" />
                                </FrameLayout>
                            </LinearLayout>

                            <Button
                                android:id="@+id/btnClaim23"
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

build.gradle.kts(Module :app)
```
implementation ("com.squareup.okhttp3:logging-interceptor:4.12.0")
```
추가


