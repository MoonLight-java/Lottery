# Lottery — 抽奖系统

前后端分离的抽奖系统，支持奖品管理、加权随机抽奖、老虎机动画和历史记录。

## 技术栈

| 层级 | 技术 |
|------|------|
| 后端 | Spring Boot 3.2.5, Java 17, Maven |
| 前端 | Vue 3.5, Vite 6 |

## 项目结构

```
Lottery/
├── backend/                         # Spring Boot 后端
│   └── src/main/java/lottery/
│       ├── LotteryApplication.java       # 启动入口
│       ├── model/LotteryItem.java        # 奖品实体（名称 + 权重）
│       ├── service/LotteryService.java   # 业务逻辑（奖池管理、加权随机、历史记录）
│       └── controller/LotteryController.java  # REST API
└── frontend/                        # Vue 3 + Vite 前端
    └── src/
        ├── App.vue                      # 三栏布局容器
        ├── api/lottery.js               # 后端 API 封装
        └── components/
            ├── ItemPanel.vue            # 左侧：奖品管理（增删查）
            ├── DrawMachine.vue          # 中部：老虎机动画抽奖
            └── HistoryPanel.vue         # 右侧：抽奖历史记录
```

## 快速启动

### 环境要求

- **JDK 17+**
- **Maven 3.6+**
- **Node.js 18+**

### 1. 启动后端（端口 8080）

```bash
cd backend
mvn spring-boot:run
```

### 2. 启动前端（端口 3000）

```bash
cd frontend
npm install
npm run dev
```

浏览器打开 `http://localhost:3000`，前端开发服务器已配置 `/api` 代理到 `localhost:8080`。

Windows 下也可直接双击 `run.bat` 一键启动两个服务。

## API 端点

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/lottery/items` | 列出所有奖品 |
| POST | `/api/lottery/items` | 添加/更新奖品 `{name, weight}` |
| DELETE | `/api/lottery/items/{name}` | 删除单个奖品 |
| DELETE | `/api/lottery/items` | 清空奖品池 |
| POST | `/api/lottery/draw/single` | 单次抽奖 |
| POST | `/api/lottery/draw/multiple` | 多次抽奖（不放回）`{count}` |
| GET | `/api/lottery/history` | 抽奖历史 |
| DELETE | `/api/lottery/history` | 清空历史 |

## 抽奖算法

累积权重法：生成 `[0, totalWeight)` 随机数，遍历奖品累加权重，首个 `cumulative > random` 即中奖。多次抽奖为不放回模式，每轮移除中奖者后重新计算权重。

## 注意事项

- 奖品数据和抽奖历史均存于**内存**中（`CopyOnWriteArrayList`），应用重启即清空
- 生产部署时建议将前端 `dist/` 部署到 Nginx，并配置反向代理到后端 8080 端口
- **无需数据库**，无需额外配置即可运行
