# 飞机大战 (Plane War)

按照初级软件开发实作课程的要求，开发的一款基于 Java Swing 开发的经典飞机大战游戏，用于学习和练习 Java 编程。

## 游戏特性

### 🎮 游戏模式

- **闯关模式**: 5个关卡，难度逐步提升
- **无限模式**: 无尽挑战模式

### 📊 敌机系统

| 类型 | 尺寸 | 血量 | 分数 | 射击间隔 | 掉落率 |
|:---:|:---:|:---:|:---:|:---:|:---:|
| 普通敌机 | 50×50 | 1 | 50 | 4秒 | 20% |
| 快速敌机 | 50×50 | 1 | 100 | 2秒 | 30% |
| 精英敌机 | 50×50 | 2 | 200 | 1秒 | 45% |
| BOSS | 100×100 | 50 | 5000 | 0.8秒 | 90% |
| 最终BOSS | 120×120 | 100 | 10000 | 0.5秒 | 100% |

### 🎁 道具系统

| 道具 | 效果 | 持续时间 |
|:---:|:---:|:---:|
| ❤️ 生命 | 恢复1点生命值 | - |
| 🛡️ 护盾 | 获得无敌 | 2秒 |
| 🔫 三发 | 武器升级为三发子弹 | 10秒 |
| ⚡ 射速 | 射速翻倍 | 8秒 |
| 🚀 加速 | 移动速度翻倍 | 8秒 |
| 💥 超伤 | 伤害提升至3倍 | 15秒 |

### 🎯 关卡设计

1. **初出茅庐**: 击败15个敌人通关
2. **分数挑战**: 达到5000分通关
3. **精英围剿**: 击败25个敌人通关
4. **BOSS降临**: 击败BOSS通关
5. **最终决战**: 击败最终BOSS通关
6. **无尽挑战**: 无限模式，持续挑战

## 操作说明

### 键盘控制

| 按键 | 功能 |
|:---:|:---:|
| ↑ / W | 向上移动 |
| ↓ / S | 向下移动 |
| ← / A | 向左移动 |
| → / D | 向右移动 |
| 空格 | 开始游戏 |
| ESC | 暂停游戏 / 返回主菜单 |
| P | 暂停游戏 |
| 1 | 选择闯关模式 |
| 2 | 选择无限模式 |

### 鼠标控制

- 点击主菜单按钮选择游戏模式
- 点击关卡选择按钮进入对应关卡
- 点击暂停/结束菜单按钮进行操作

## 项目结构

```
plane_war/
├── image/                    # 游戏图片资源
│   ├── player.png            # 玩家战机
│   ├── normalenemy.png       # 普通敌机
│   ├── fastenemy.png         # 快速敌机
│   ├── eliteenemy.png        # 精英敌机
│   ├── BOSS1.png             # BOSS
│   ├── BOSS2.png             # 最终BOSS
│   ├── HP.png                # 生命值图标
│   ├── heal.png              # 生命道具
│   ├── shield.png            # 护盾道具
│   ├── 3way.png              # 三发道具
│   ├── fastshoot.png         # 射速道具
│   ├── boost.png             # 加速道具
│   └── HighDMG.png           # 超伤道具
├── src/
│   └── main/
│       └── java/
│           └── com/example/planewar/
│               ├── Main.java             # 游戏入口
│               ├── model/                # 数据模型层
│               │   ├── Player.java       # 玩家模型
│               │   ├── Enemy.java        # 敌机模型
│               │   ├── Bullet.java       # 子弹模型
│               │   ├── PowerUp.java      # 道具模型
│               │   ├── GameState.java    # 游戏状态
│               │   ├── GameMode.java     # 游戏模式枚举
│               │   └── LevelConfig.java  # 关卡配置
│               ├── view/                 # 视图层
│               │   ├── GameFrame.java    # 游戏窗口
│               │   └── GamePanel.java    # 游戏面板
│               ├── controller/           # 控制层
│               │   ├── GameController.java  # 游戏控制器（流程控制、状态管理）
│               │   ├── EnemySpawner.java    # 敌人/BOSS生成、道具掉落
│               │   ├── CombatSystem.java    # 碰撞检测、伤害结算
│               │   ├── GameLoop.java     # 游戏循环
│               │   ├── KeyController.java  # 键盘控制
│               │   └── MouseController.java # 鼠标控制
│               └── util/                 # 工具类
│                   ├── ImageLoader.java  # 图片加载
│                   ├── CollisionDetector.java # 碰撞检测
│                   └── HighScoreManager.java  # 最高分管理
├── highscore.dat             # 最高分存档
├── pom.xml                   # Maven配置
└── README.md                 # 项目说明
```

## 技术实现

- **语言**: Java 8+
- **框架**: Swing GUI
- **架构**: MVC (Model-View-Controller)
- **渲染**: Double Buffered Graphics2D
- **游戏循环**: 60 FPS Thread-based

## 运行方式

### 环境要求

- JDK 8 或更高版本
- Maven 3.6+ (可选)

### 使用 Maven 运行

```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="com.example.planewar.Main"
```

### 使用 JAR 运行

```bash
java -jar target/plane_war-1.0.0.jar
```

## 开发说明

项目采用标准的 MVC 架构设计：

- **Model**: 处理游戏数据和状态
- **View**: 负责游戏画面渲染
- **Controller**: 协调模型和视图，处理游戏逻辑

核心游戏逻辑包括：
- 碰撞检测系统
- 敌人生成
- 道具掉落与效果系统

## 素材来源

- **图片**: 通过豆包生成。

## 版本历史

- v1.2.3: 架构重构与职责分离
  - **架构拆分**: 将 GameController(713行)拆分为三个职责单一的类：
    - `EnemySpawner` — 敌人/BOSS生成、道具掉落、敌机射击
    - `CombatSystem` — 碰撞检测、伤害结算、道具效果
    - `GameController` — 流程控制、玩家输入、状态管理
  - **视图逻辑内聚**: 按钮点击检测逻辑从 GameController 移入 GamePanel，消除坐标重复定义
  - **MenuAction类**: 创建统一的菜单操作封装类，替代硬编码的坐标判断
  - **代码简化**: GameController 精简至约 360 行，可读性和可维护性大幅提升

- v1.2.2: 代码质量与异常处理优化
  - **常量提取**: 将普通敌机子弹偏移量(3)提取为 ENEMY_BULLET_OFFSET_X 常量
  - **空指针保护**: GameController.update() 添加 player 空指针检查，防止状态切换时崩溃
  - **代码重构**: CollisionDetector 提取通用 checkAABBCollision() 方法，消除 4 个重复检测逻辑
  - **日志改进**: ImageLoader 和 HighScoreManager 使用 java.util.logging 替代 System.err，增加详细日志
  - **异常处理**: ImageLoader 添加文件不存在和图片解码失败的单独处理

- v1.2.1: 代码健壮性与设计修正
  - **BUG修复**: BOSS移动从真实时间改为游戏时间（暂停后不再瞬移）
  - **BUG修复**: drawHUD添加player空指针保护，避免状态切换时崩溃
  - **设计修正**: Level 4/5关卡击杀BOSS直接触发通关（替代纯分数判定）
  - **代码质量**: Player计时器过期逻辑从getter提取到update()方法（消除getter副作用）
  - **代码清理**: 删除GameState.lives死代码、PowerUpType.Color未使用字段
  - **封装改进**: Enemy.setDestroyed()改为destroy()方法，防止外部绕过血量系统
  - **性能优化**: LevelConfig数组缓存为static final，避免每帧分配新对象
  - **UI改进**: 生存模式HUD显示当前波次和最高分，替代误导的关卡5目标
  - **可维护性**: BOSS子弹偏移量、BOSS刷新击杀阈值等提取为命名常量

- v1.2: 代码质量与架构优化
  - **BUG修复**: 修复无限模式BOSS波次计数无效问题（bossWave改为GameState成员变量）
  - **线程安全**: GameLoop running标志添加volatile关键字
  - **动态难度**: 生存模式实现动态难度（刷怪间隔、敌人数量、类型比例随波次递增）
  - **代码重构**: 合并spawnBoss/spawnSurvivalBoss为单一方法
  - **常量提取**: 子弹偏移量、BOSS移动参数、生存模式参数等魔法数字提取为命名常量
  - **性能优化**: GameLoop使用可变睡眠时间；暂停时线程挂起降低CPU消耗
  - **资源优化**: Random实例改为静态final单例，避免每帧创建
  - **跨平台兼容**: 字体从Microsoft YaHei改为Font.SANS_SERIF
  - **构建优化**: 更新maven-compiler-plugin(3.8.1→3.11.0)和maven-jar-plugin(3.2.0→3.3.0)
  - **测试支持**: 添加JUnit 4依赖
  - **gitignore完善**: 添加.idea/、*.iml、*.log等忽略规则

- v1.1: 游戏平衡性调整
  - 玩家基础速度从8降低到6，加速道具倍率从2倍调整为1.5倍
  - 敌人射击间隔上调：快速2s→3s、精英1s→1.5s、BOSS 0.8s→1.2s、最终BOSS 0.5s→0.8s
  - 敌人弹速从4降低到3
  - 敌人进入屏幕后立即发射第一颗子弹，不再等待射击间隔

- v1.0.0: 正式版本，包含完整的游戏功能

- v0.7: 完善与优化
  - 修复无尽模式关卡定向错误
  - 统一道具系统与图片资源
  - 伤害道具去重（保留SUPER_DAMAGE）
  - 整体道具爆率上调
  - BOSS血量提升
  - 代码排版优化

- v0.6: BOSS与道具扩展
  - BOSS战系统实现（BOSS和最终BOSS）
  - 道具类型扩展（加速、超伤）
  - 无尽模式BOSS生成逻辑
  - 关卡目标多元化（分数、击杀数）

- v0.5: 界面与游戏模式
  - 鼠标点击界面交互
  - UI重新设计与美化（黑色背景）
  - 闯关模式实现（5个关卡）
  - 无限模式作为单独选项

- v0.4: 资源与掉落系统
  - 图片资源适配与管理
  - 掉落物系统实现（生命、护盾、武器）
  - 增加出怪数量与难度平衡
  - 道具爆率调整

- v0.3: 敌机与武器系统
  - 敌机发射子弹功能
  - 敌机速度统一与射击间隔差异化（普通4s、快速2s、精英1s）
  - 武器升级系统（三发子弹）
  - 关卡难度递增机制

- v0.2: 游戏体验优化
  - 敌机分级与不同属性
  - 玩家无敌状态与闪烁效果
  - 游戏状态管理（开始/暂停/结束）
  - 最高分本地保存

- v0.1: 基础游戏功能
  - 玩家战机控制与自动射击
  - 三种基础敌机类型
  - 碰撞检测系统
  - 计分系统
