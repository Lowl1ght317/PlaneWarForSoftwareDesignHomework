# 🔍 飞机大战项目代码审查报告

> 审查日期：2026-07-12
> 项目版本：v1.1.0

---

## 一、架构问题 🔴

### 1.1 GameController 过于臃肿（上帝类）

**`GameController.java` 长达 713 行**，承担了太多职责：输入处理、游戏循环调度、敌人生成、碰撞处理、道具掉落、关卡逻辑、游戏状态切换。严重违反单一职责原则。

**建议**：拆分为：
- `GameManager` — 关卡/模式切换
- `EnemySpawner` — 敌人和道具生成逻辑
- `CombatSystem` — 碰撞检测 + 伤害结算

### 1.2 视图常量重复定义

`GamePanel.java` 定义了按钮坐标常量（如 `MENU_STORY_X`, `PAUSE_RESUME_X`），但 `GameController.java` 的点击处理函数又硬编码了同样的坐标判断逻辑。修改按钮位置需要同时改两个文件。

**建议**：点击检测逻辑移入 `GamePanel` 或统一的 `UIManager` 类。

### 1.3 缺乏接口抽象

所有模型类都是具体类，没有使用接口。`CollisionDetector` 的参数都是具体类型，扩展性受限。

---

## 二、潜在 Bug 🟠

### 2.1 无限模式 BOSS 波次计数无效

`GameController.java:406-412`：

```java
int bossWave = 1;                          // ⚠️ 局部变量，每次调用都重置为 1
int bossThreshold = bossWave * 12000;
if (!bossSpawned && gameState.getScore() >= bossThreshold) {
    spawnSurvivalBoss();
    bossWave++;                            // ⚠️ 自增毫无意义，下次调用时又重置为 1
}
```

**后果**：BOSS 永远只在 12000 分时生成一次。按设计意图，应该每 12000 分生成一个更强力的 BOSS（24000、36000...），但这个逻辑永远不会触发。

**修复方向**：`bossWave` 应提升为 GameState 的成员变量。

### 2.2 生存模式关卡索引错误

`startSurvivalMode()` 设置 `gameState.setLevel(6)`，但在 `spawnEnemies()` 中：

```java
LevelConfig.getLevels()[Math.min(gameState.getLevel() - 1, 4)]
// 生存模式: level=6 → Math.min(5, 4) = 4 → 始终使用第5关配置
```

生存模式始终使用第5关的刷怪参数（间隔 400ms, 最大 12 个敌人），没有利用 level 变量做动态难度调整。

### 2.3 GameLoop running 标志缺少 volatile

`GameLoop.java`：

```java
private boolean running;   // ⚠️ 非 volatile，多线程可见性无保证
```

`stopGame()` 在 EDT 线程调用，`run()` 在游戏线程读取。没有 `volatile` 关键字，`running = false` 的修改可能永远不被游戏线程看到，导致无法停止。

---

## 三、代码质量问题 🟡

### 3.1 大量魔法数字

```java
// GameController.java:272-303 子弹生成偏移
Bullet bullet = new Bullet(player.getX() + player.getWidth() / 2 - 3, player.getY() - 15, true);
Bullet bulletLeft = new Bullet(player.getX() + 5, player.getY() - 10, true);
Bullet bulletRight = new Bullet(player.getX() + player.getWidth() - 11, player.getY() - 10, true);
bulletLeft.setSpeed(bulletLeft.getSpeed() + 3);

// GameController.java:347-353 BOSS 移动
long time = System.currentTimeMillis() / 500;
boss.setX((int)(GameFrame.WIDTH / 2 + Math.sin(time) * 150 - boss.getWidth() / 2));

// GameController.java:265 射击间隔
int fireInterval = (int)(200 / fireRateMultiplier);
```

**建议**：提取为命名常量或配置类。

### 3.2 重复代码（DRY 违反）

| 重复场景 | 位置 |
|---|---|
| `startSurvivalMode()` 和 `startLevel()` | 90% 相同代码 |
| `spawnBoss()` 和 `spawnSurvivalBoss()` | 几乎完全相同 |
| 按钮绘制（渐变 + 圆角矩形 + 文字） | `drawPausedScreen`、`drawGameOverScreen`、`drawMainMenu` 重复 12 次 |
| `CollisionDetector` 四个碰撞方法 | 全部是相同的 AABB 检测逻辑 |

### 3.3 每帧创建 Random 实例

```java
// GameController.java:469 & Enemy.java:78
Random random = new Random();  // ⚠️ 每帧调用多次
```

应使用单个共享的 `ThreadLocalRandom` 或 `static final Random` 实例。

### 3.4 字体跨平台兼容性

```java
g2d.setFont(new Font("Microsoft YaHei", Font.BOLD, 56));  // ⚠️ 仅 Windows 有此字体
```

在 Linux/macOS 上会 fallback 到默认字体。建议使用 `Font.SANS_SERIF` 或 `Font.DIALOG`。

---

## 四、性能问题 🟡

### 4.1 GameLoop 使用忙等待

`GameLoop.java:22-28`：

```java
if (elapsedTime >= TARGET_TIME) {
    controller.update();
    lastTime = currentTime;
}
Thread.sleep(1);  // ⚠️ 即使 update 刚执行完也 sleep 1ms
```

**建议**：使用可变睡眠时间：

```java
long sleepTime = TARGET_TIME - (System.currentTimeMillis() - lastTime);
if (sleepTime > 0) Thread.sleep(sleepTime);
```

### 4.2 暂停时 CPU 仍然高负载

虽然 `update()` 在暂停时直接 return，但 GameLoop 仍在以 60FPS 的速率调用 `update()` 和 `repaintGame()`，暂停时 CPU 使用率不会下降。

**建议**：暂停时在 GameLoop 中使用 `wait/notify` 挂起线程。

---

## 五、线程安全 🟠

### 5.1 无同步机制

游戏状态（enemies、bullets、powerUps 列表）被游戏线程修改，但按键事件在 EDT 线程上触发 `startGame()` 等方法。没有任何同步：

```java
public List<Enemy> getEnemies() { return enemies; }  // ⚠️ 直接暴露内部列表
```

**建议**：使用 `CopyOnWriteArrayList` 或对列表操作加锁。

### 5.2 异常处理过于宽松

`ImageLoader.java:19` & `HighScoreManager.java:13`：

```java
} catch (IOException e) {
    System.err.println("无法加载图片: " + filename);  // ⚠️ 只打印，没有日志框架
    return null;  // 调用者可能会遇到 NullPointerException
}
```

**建议**：引入 `java.util.logging` 或 SLF4J，并在调用处做 null 检查。

---

## 六、可维护性 🔵

### 6.1 完全没有单元测试

项目中没有任何测试代码，`pom.xml` 也没有测试依赖（如 JUnit）。

**建议**：将核心逻辑（碰撞检测、计分、关卡配置）与 Swing 解耦后编写 JUnit 测试。

### 6.2 关卡配置硬编码

`LevelConfig.getLevels()` 返回硬编码数组，修改关卡参数需要重新编译。

**建议**：改为从 JSON/XML/properties 文件加载。

### 6.3 .gitignore 不完整

当前缺失：
- `.idea/`（IDE 配置目录）
- `*.iml`（IntelliJ 模块文件）
- `*.log`（日志文件）

### 6.4 pom.xml 依赖版本

- `maven-compiler-plugin` 版本 `3.8.1` 较旧
- `maven-jar-plugin` 版本 `3.2.0` 较旧
- 无测试框架依赖

---

## 七、用户体验 🟢

| 缺失功能 | 说明 |
|---|---|
| 音效/背景音乐 | 完全没有音频系统 |
| 键位自定义 | 按键硬编码，无法修改 |
| 分辨率适配 | 固定 600×800 无缩放 |
| 难度选择 | 无简单/普通/困难选项 |
| 游戏内暂停 | 暂停后 CPU 仍在运行，浪费资源 |

---

## 八、改进优先级建议

| 优先级 | 改进项 | 影响 |
|---|---|---|
| **P0** | 修复 `bossWave` 局部变量 bug | 无尽模式 BOSS 机制失效 |
| **P0** | `running` 标志加 `volatile` | 潜在的线程死循环 |
| **P1** | 拆解 `GameController` | 代码可维护性 |
| **P1** | 消除重复代码 | 减少 bug 风险 |
| **P1** | 添加单元测试 | 回归保护 |
| **P2** | 提取魔法数字为常量 | 可读性 |
| **P2** | 优化 GameLoop（暂停时挂起） | 降低 CPU 消耗 |
| **P2** | 字体跨平台兼容 | 跨平台部署 |
| **P3** | 添加音效系统 | 游戏体验 |
| **P3** | 关卡配置外部化 | 灵活性 |
| **P3** | 补充 .gitignore 条目 | 仓库整洁 |

---

## 总结

这是一个**结构清晰、功能完整**的教学项目，MVC 分层合理，双缓冲渲染和游戏循环设计得当。上述问题主要集中在代码规模增长后的**可维护性**和几个**逻辑 bug**上。核心功能已实现得很好；如需继续迭代，建议优先处理 P0/P1 级别的改进。
