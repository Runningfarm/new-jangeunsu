// index.js
require("dotenv").config(); // .env 파일 읽기
const express = require("express"); // express 불러오기
const mongoose = require("mongoose"); // mongoose 불러오기
const User = require("./models/User");
const bcrypt = require("bcrypt");
const jwt = require("jsonwebtoken");
const app = express(); // app 객체 생성
const port = 3000; // 사용할 포트 번호

// JSON 형식 요청 받을 수 있게 설정
app.use(express.json());

// MongoDB 연결 시도
mongoose
  .connect(process.env.MONGO_URL)
  .then(() => {
    console.log("✅ MongoDB 연결 성공!");
  })
  .catch((err) => {
    console.error("❌ MongoDB 연결 실패:", err);
  });

// 날짜 구하는 함수(YYYY-MM-DD)
function getTodayStr() {
  const now = new Date();
  return now.toISOString().slice(0, 10);
}

// 퀘스트 배열로 내려줌
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

app.post("/auth/check-duplicate", async (req, res) => {
  const { id } = req.body;
  try {
    const exist = await User.findOne({ id });
    res.json({ duplicate: exist ? true : false });
  } catch (err) {
    res.status(500).json({ success: false, message: "서버 오류" });
  }
});

// 회원가입 API
app.post("/register", async (req, res) => {
  const { id, password, weight, name } = req.body;
  console.log("회원가입 요청 들어옴!", req.body);

  try {
    // 중복 체크
    const exist = await User.findOne({ id });
    console.log("중복 체크 결과:", exist);

    if (exist)
      return res
        .status(409)
        .json({ success: false, message: "이미 존재하는 아이디입니다." });

    // 비밀번호 암호화
    const hashedPassword = await bcrypt.hash(password, 10); // 10은 salt rounds
    console.log("암호화된 비번:", hashedPassword);

    // 유저 생성 및 저장
    const newUser = new User({
      id,
      password: hashedPassword,
      weight,
      name,
      questDate: getTodayStr(),
    });
    await newUser.save();
    console.log("회원 저장됨!");

    res.json({
      success: true,
      message: "회원가입 성공!",
      id: newUser.id,
    });
  } catch (err) {
    console.error("회원가입 에러:", err);
    res
      .status(500)
      .json({ success: false, message: "회원가입 실패", error: err });
  }
});

// 로그인 API
app.post("/login", async (req, res) => {
  console.log("로그인 요청 들어옴!", req.body);
  const { id, password } = req.body;

  try {
    // 1. 아이디  로 사용자 찾기
    const user = await User.findOne({ id });
    console.log("DB에서 찾은 유저:", user);

    // 2. 사용자 없음
    if (!user) {
      return res.status(404).json({
        success: false,
        message: "존재하지 않는 아이디입니다.",
      });
    }

    // 3. 비밀번호 비교 (지금은 암호화 안 했으니 그대로 비교)
    const isMatch = await bcrypt.compare(password, user.password);
    if (!isMatch) {
      return res.status(401).json({
        success: false,
        message: "비밀번호가 일치하지 않습니다.",
      });
    }

    // 4. 성공
    const token = jwt.sign(
      { id: user.id }, // 토큰에 담을 정보
      process.env.JWT_SECRET, // 비밀 키
      { expiresIn: "3h" } // 만료 시간 (1시간)
    );

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
  } catch (err) {
    res.status(500).json({ success: false, message: "서버 오류" });
  }
});

// 시간대별 퀘스트 확인하는 함수
function getTimeQuestType(date) {
  // date: Date 객체
  const hour = date.getHours();
  if (hour >= 6 && hour < 12) return "morning";
  if (hour >= 12 && hour < 22) return "afternoon";
  return "night";
}

// 러닝 속도(km/h)에 따라 METs 값을 반환하는 함수
function getMetsByPace(pace) {
  if (pace < 6) return 3.5; // 느리게 걷기 수준
  if (pace < 8) return 6.0; // 빠르게 걷기
  if (pace < 10) return 8.3; // 느린 달리기
  if (pace < 12) return 10.5; // 보통 달리기
  if (pace < 14) return 12.8; // 빠른 달리기
  return 15.0; // 매우 빠른 달리기
}

// 칼로리 계산: METs × 체중(kg) × 시간(시간 단위)
function calculateCalories(weight, time, pace) {
  const hour = time / 3600; // 초 → 시간(h)
  const mets = getMetsByPace(pace);
  const kcal = mets * weight * hour;
  return Math.round(kcal); // 소수점 반올림
}

// 퀘스트 user.quests[0~2]는 거리, user.quests[3~5]는 오전/오후/야간으로 시간, kcal 칼로리
app.post("/run/complete", verifyToken, async (req, res) => {
  console.log(
    "런닝 완료 요청:",
    req.body,
    "유저:",
    req.user.id,
    "서버 시간:",
    new Date()
  );
  let { distance, time, kcal, pace } = req.body;
  distance = Number(distance);
  time = Number(time);
  kcal = Number(kcal);
  pace = Number(pace);

  try {
    const user = await User.findOne({ id: req.user.id });
    if (!user)
      return res.status(404).json({ success: false, message: "유저 없음" });

    // 거리 퀘스트 진행도 올리기
    if (typeof distance === "number" && distance > 0) {
      user.totalDistance += distance;
      // 1km/3km/5km 퀘스트 처리
      for (let i = 0; i <= 2; i++) {
        let q = user.quests[i];
        if (!q.completed) {
          q.progress += distance;
          if (q.progress >= q.target) {
            q.progress = q.target;
            q.completed = true;
            q.completedAt = new Date();
          }
        }
      }
    }

    // 오전/오후/야간 퀘스트
    if (typeof time === "number" && time > 0) {
      // 오전(4번), 오후(5번), 야간(6번) 체크 (index: 3~5)
      const now = new Date();
      const timeType = getTimeQuestType(now); // "morning", "afternoon", "night"
      const idx = user.quests.findIndex((q) => q.type === timeType); // 타입으로 인덱스 찾기

      if (idx !== -1) {
        let quest = user.quests[idx];
        if (!quest.completed) {
          quest.progress += 1;
          console.log("퀘스트 증가! now progress=", quest.progress);
          if (quest.progress >= quest.target) {
            quest.progress = quest.target;
            quest.completed = true;
            quest.completedAt = new Date();
          }
        }
      }
    }

    // 칼로리 처리
    if (
      typeof user.weight === "number" &&
      typeof pace === "number" &&
      typeof time === "number"
    ) {
      kcal = calculateCalories(user.weight, time, pace);
    }
    user.totalCalories += kcal;

    user.quests.forEach((q) => {
      if (q.type === "kcal" && !q.completed) {
        q.progress += kcal;
        if (q.progress >= q.target) {
          q.progress = q.target;
          q.completed = true;
          q.completedAt = new Date();
        }
      }
    });

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

    //먹이
    user.totalFood += Math.floor(distance * 10);

    await user.save();
    console.log("user.save() 이후 user.quests:", user.quests);

    res.json({
      success: true,
      message: "런닝 결과 저장+퀘스트 반영 완료!",
      quests: user.quests,
      totalDistance: user.totalDistance,
      totalFood: user.totalFood,
      totalCalories: user.totalCalories,
    });
  } catch (err) {
    res.status(500).json({ success: false, message: "서버 오류", err });
  }
});

app.post("/quest/claim", verifyToken, async (req, res) => {
  const { questNumber } = req.body;

  try {
    const user = await User.findOne({ id: req.user.id });
    if (!user) {
      return res.status(404).json({ success: false, message: "유저 없음" });
    }

    const index = questNumber - 1;
    const quest = user.quests[index];

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

    res.json({
      success: true,
      message: `보상 ${reward}개 지급 완료!`,
      totalFood: user.totalFood,
      reward,
    });
  } catch (err) {
    res.status(500).json({ success: false, message: "서버 오류", err });
  }
});

// 🔐 JWT 인증 미들웨어
function verifyToken(req, res, next) {
  // Authorization: Bearer 토큰값
  const authHeader = req.headers.authorization;

  // 토큰 없으면 막기
  if (!authHeader || !authHeader.startsWith("Bearer ")) {
    return res
      .status(401)
      .json({ success: false, message: "토큰 없음 또는 형식 오류" });
  }

  const token = authHeader.split(" ")[1];

  try {
    // 토큰 유효성 검사
    const decoded = jwt.verify(token, process.env.JWT_SECRET);
    req.user = decoded; // req.user에 id 저장
    next(); // 통과
  } catch (err) {
    return res
      .status(403)
      .json({ success: false, message: "유효하지 않은 토큰" });
  }
}

// 🔒 로그인한 사용자만 접근 가능한 API
app.get("/myfarm", verifyToken, async (req, res) => {
  try {
    // verifyToken 함수가 성공하면 req.user 안에 id가 들어 있음!
    const id = req.user.id;

    // 데이터베이스에서 해당 유저 정보 가져오기
    const user = await User.findOne({ id });

    // 유저가 없으면 404 오류
    if (!user) {
      return res.status(404).json({ success: false, message: "사용자 없음" });
    }

    // 유저가 있으면 정보 응답
    res.json({
      success: true,
      message: "농장 정보 가져오기 성공!",
      id: user.id,
      weight: user.weight,
      totalDistance: user.totalDistance,
      totalFood: user.totalFood,
    });
  } catch (err) {
    console.error("❌ 에러 발생:", err);
    res.status(500).json({
      success: false,
      message: "서버 오류",
    });
  }
});

// 탈퇴 API (회원 삭제)
app.delete("/user/delete", verifyToken, async (req, res) => {
  try {
    const id = req.user.id;

    const deleted = await User.findOneAndDelete({ id });

    if (!deleted) {
      return res
        .status(404)
        .json({ success: false, message: "사용자를 찾을 수 없습니다." });
    }

    res.json({
      success: true,
      message: "회원 탈퇴가 완료되었습니다.",
    });
  } catch (err) {
    console.error("❌ 탈퇴 중 에러:", err);
    res.status(500).json({ success: false, message: "서버 오류", error: err });
  }
});

// 회원정보 수정 API
app.post("/user/update", async (req, res) => {
  const { id, name, password, weight } = req.body;

  try {
    const user = await User.findOne({ id });

    if (!user) {
      return res.status(404).json({ success: false, message: "사용자 없음" });
    }

    // 비밀번호 새로 암호화
    const hashedPassword = await bcrypt.hash(password, 10);

    // 값 업데이트
    user.name = name;
    user.password = hashedPassword;
    user.weight = weight;

    await user.save();

    res.json({
      success: true,
      message: "회원정보 수정 완료",
    });
  } catch (err) {
    console.error("❌ 수정 중 에러:", err);
    res.status(500).json({ success: false, message: "서버 오류", error: err });
  }
});

// 서버 실행
app.listen(3000, "192.168.45.19", () => {
  //자기 아이피주소 넣기 CMD에서 ipconfig치면 알 수 있음
  console.log(`🚀 서버 실행 중`);
});
