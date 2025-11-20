<template>
  <div class="troll-container">
    <!-- 游戏入口按钮 -->
    <div v-if="!showGame && !showLoading" class="game-entry">
      <el-button @click="startNewGame" type="primary" class="troll-option">
        点击开始小游戏
      </el-button>
    </div>

    <div class="troll-box">
      <router-link to="/login">
        <h1 class="troll-title">你真的记不起账号了吗？</h1>
      </router-link>
      <p class="troll-subtitle">或许以下选项可以帮助你找到人生的方向</p>
      <div class="options">
        <el-button
            type="primary"
            class="troll-option"
            @click="mockAction('找回密码')"
        >
          找回密码
        </el-button>
        <el-button
            type="primary"
            class="troll-option"
            @click="mockAction('找回账户')"
        >
          找回账户
        </el-button>
        <el-button
            type="primary"
            class="troll-option"
            @click="mockAction('找回人生')"
        >
          找回人生
        </el-button>
        <el-button
            type="success"
            class="troll-option"
            @click="mockAction('找回智商')"
        >
          找回智商
        </el-button>
        <el-button
            type="danger"
            class="troll-option"
            @click="mockAction('找回小猫')"
        >
          找回小猫
        </el-button>
        <el-button
            type="warning"
            class="troll-option"
            @click="mockAction('找回人生哲学')"
        >
          找回人生哲学
        </el-button>
        <!-- 神秘按钮 -->
        <el-button
            type="info"
            class="troll-option"
            @click="mockAction('秘密奖励')"
        >
          点击我，获取秘密奖励
        </el-button>
      </div>

      <div class="bomb-section" v-if="showBomb">
        <p class="countdown">问题解决倒计时：{{ countdown }} 秒</p>
        <p v-if="showExplosion" class="explosion-message">BOOM！炸弹爆炸了！</p>
        <div v-if="countdown <= 0">
          <el-button @click="startNewGame" type="primary" class="troll-option">重新开始游戏</el-button>
        </div>
      </div>

      <div v-if="message" class="troll-message">
        <p>{{ message }}</p>
      </div>

      <div class="remember-password">
        <a href="https://www.baidu.com/s?wd=如何记住密码" target="_blank">
          给你最后的机会重置密码吧！
        </a>
        <a href="https://www.baidu.com/s?wd=他根本没爱过你" target="_blank">
          给你最后的机会重置账号吧！
        </a>
        <a href="http://www.minesweeper.cn/" target="_blank">
          好emo....没事，我有办法！
        </a>
      </div>

      <div class="user-statistics">
        <p>你已经点击了 {{ clickCount }} 次按钮！</p>
      </div>

      <!-- 动态迷宫 -->
      <div class="maze-container" v-if="showMaze">
        <p class="maze-instruction">找到正确的路径，才能赢得奖励！</p>
        <div class="maze" @click="checkMazePath('left')">
          <div class="maze-wall left"></div>
        </div>
        <div class="maze" @click="checkMazePath('right')">
          <div class="maze-wall right"></div>
        </div>
      </div>

      <!-- 加载动画 -->
      <div v-if="showLoading" class="loading-spinner">
        <p>正在加载...请稍候，恶搞才刚刚开始！</p>
        <div class="spinner"></div>
      </div>

      <!-- 恶搞小游戏 -->
      <div v-if="showGame" class="game-container">
        <p>猜数字游戏：我想了一个数字，请猜它是多少（1-10）</p>
        <input v-model="guessedNumber" type="number" min="1" max="10" />
        <button @click="checkNumberGuess">提交</button>
        <p v-if="gameMessage" class="game-message">{{ gameMessage }}</p>
      </div>
    </div>
  </div>
</template>

<script>
import { ref } from "vue";
import { ElMessage } from "element-plus";

export default {
  name: "ByeBye",
  data() {
    return {
      showExplosion: false,
      clickCount: 0,
      showBomb: false,
      countdown: 10,
      message: "",
      showMaze: false,
      showLoading: false,
      showGame: false,
      guessedNumber: null,
      gameMessage: "",
      correctNumber: Math.floor(Math.random() * 10) + 1,
    };
  },
  mounted() {
    this.showLoadingScreen();
  },
  beforeDestroy() {},
  methods: {
    mockAction(action) {
      this.resetBombCountdown();
      this.clickCount++;
      switch (action) {
        case "找回密码":
          this.message = "密码找不回来了，建议重新注册一个吧！";
          break;
        case "找回账户":
          this.message = "抱歉，您的账户可能已经飞走了~";
          break;
        case "找回人生":
          this.message = "人生不是找回的，而是重新开始的！";
          break;
        case "找回智商":
          this.message = "智商？找回了吗？似乎没什么效果哦...";
          break;
        case "找回小猫":
          this.message = "小猫？它又跑到哪里去了？";
          break;
        case "找回人生哲学":
          this.message = "人生的哲学似乎还在迷茫中...";
          break;
        case "秘密奖励":
          this.message = "恭喜你获得了一个神秘奖励！这个奖励就是：继续玩这个游戏！";
          break;
        default:
          this.message = "再试试吧，也许有惊喜？";
      }
      this.startBombCountdown();
      this.showMaze = Math.random() > 0.5; // 随机显示迷宫
    },
    startBombCountdown() {
      this.showBomb = true;
      this.showExplosion = false;
      this.countdown = 10;
      this.interval = setInterval(() => {
        if (this.countdown > 0) {
          this.countdown--;
        } else {
          clearInterval(this.interval);
          this.showExplosion = true;
          ElMessage({
            message: "倒计时结束：什么也没发生吧？",
            type: "info",
          });
          this.showBomb = false;
        }
      }, 1000);
    },
    resetBombCountdown() {
      clearInterval(this.interval);
      this.countdown = 5;
      this.showExplosion = false;
    },
    startNewGame() {
      this.clickCount = 0;
      this.message = "";
      this.correctNumber = Math.floor(Math.random() * 10) + 1; // 重置数字
      this.showMaze = false;
      this.showGame = true; // 显示游戏
      this.showLoading = true; // 显示加载动画
      setTimeout(() => {
        this.showLoading = false; // 加载完毕后隐藏
      }, 3000);
    },
    checkMazePath(path) {
      if (path === 'left') {
        ElMessage({
          message: "你走错了，回到起点！",
          type: "error",
        });
      } else {
        ElMessage({
          message: "恭喜你走对了，继续前进！",
          type: "success",
        });
      }
    },
    showLoadingScreen() {
      this.showLoading = true;
      setTimeout(() => {
        this.showLoading = false;
      }, 3000);
    },
    checkNumberGuess() {
      if (this.guessedNumber === this.correctNumber) {
        this.gameMessage = "恭喜你，猜对了！";
      } else {
        this.gameMessage = "错了，再试试吧！";
      }
    },
  },
};
</script>

<style scoped>
.troll-container {
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
  height: 100vh;
  background-color: #b3e5fc;
  position: relative;
}

.troll-box {
  text-align: center;
  padding: 30px;
  border-radius: 20px;
  background-color: rgba(255, 255, 255, 0.9);
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.1);
  width: 70%;
}

.troll-title {
  font-size: 36px;
  font-weight: bold;
  background: linear-gradient(90deg, #0288d1, #81d4fa);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.troll-subtitle {
  font-size: 18px;
  margin-top: 10px;
  color: #0288d1;
}

.options {
  margin-top: 20px;
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
}

.troll-option {
  background-color: #0288d1;
  color: #fff;
  font-size: 18px;
  padding: 12px 24px;
  border-radius: 8px;
  transition: all 0.3s ease;
  margin: 5px;
}

.troll-option:hover {
  background-color: #0277bd;
}

.game-entry {
  margin-top: 20px;
}

.bomb-section {
  margin-top: 20px;
  text-align: center;
}

.countdown {
  font-size: 24px;
  color: #e91e63;
}

.explosion-message {
  font-size: 36px;
  color: red;
  font-weight: bold;
}

.game-container {
  margin-top: 20px;
  font-size: 20px;
}

.game-message {
  margin-top: 10px;
  color: #4caf50;
  font-size: 24px;
}

.loading-spinner {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.spinner {
  border: 10px solid #f3f3f3;
  border-top: 10px solid #0288d1;
  border-radius: 50%;
  width: 50px;
  height: 50px;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.user-statistics {
  font-size: 18px;
  margin-top: 20px;
  color: #0288d1;
}

.maze-container {
  margin-top: 20px;
  text-align: center;
}

.maze-wall {
  width: 100px;
  height: 100px;
  background-color: #0288d1;
  margin: 10px;
}

.maze-wall.left {
  float: left;
}

.maze-wall.right {
  float: right;
}

.remember-password {
  margin-top: 20px;
  font-size: 18px;
  color: #0288d1;
}

.remember-password a {
  text-decoration: underline;
  color: #01579b;
  font-weight: bold;
}
</style>
