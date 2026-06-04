package lottery.service;

import lottery.model.LotteryItem;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class LotteryService {
    private final List<LotteryItem> items = new CopyOnWriteArrayList<>();
    private final List<DrawRecord> history = new CopyOnWriteArrayList<>();

    // ---- Item management ----

    public List<LotteryItem> listItems() {
        return new ArrayList<>(items);
    }

    public LotteryItem addItem(LotteryItem item) {
        int idx = items.indexOf(item);
        if (idx >= 0) {
            items.set(idx, item);
        } else {
            items.add(item);
        }
        return item;
    }

    public boolean removeItem(String name) {
        return items.removeIf(i -> i.getName().equals(name));
    }

    public void clearItems() {
        items.clear();
    }

    // ---- Drawing ----

    public DrawResult drawSingle() {
        if (items.isEmpty()) {
            return new DrawResult(null, "奖池为空");
        }

        int totalWeight = items.stream().mapToInt(LotteryItem::getWeight).sum();
        int random = ThreadLocalRandom.current().nextInt(totalWeight);

        int cumulative = 0;
        LotteryItem winner = null;
        for (LotteryItem item : items) {
            cumulative += item.getWeight();
            if (random < cumulative) {
                winner = item;
                break;
            }
        }

        DrawRecord record = new DrawRecord(winner.getName());
        history.add(record);
        return new DrawResult(winner.getName(), "恭喜中奖！");
    }

    public DrawMultiResult drawMultiple(int count) {
        if (count <= 0) {
            return new DrawMultiResult(Collections.emptyList(), "抽奖次数无效");
        }
        if (items.isEmpty()) {
            return new DrawMultiResult(Collections.emptyList(), "奖池为空");
        }

        List<String> winners = new ArrayList<>();
        List<LotteryItem> pool = new ArrayList<>(items);

        for (int i = 0; i < count && !pool.isEmpty(); i++) {
            int totalWeight = pool.stream().mapToInt(LotteryItem::getWeight).sum();
            int random = ThreadLocalRandom.current().nextInt(totalWeight);

            int cumulative = 0;
            int winnerIdx = -1;
            for (int j = 0; j < pool.size(); j++) {
                cumulative += pool.get(j).getWeight();
                if (random < cumulative) {
                    winnerIdx = j;
                    break;
                }
            }
            String winnerName = pool.remove(winnerIdx).getName();
            winners.add(winnerName);
            history.add(new DrawRecord(winnerName));
        }

        return new DrawMultiResult(winners, "抽取完成");
    }

    // ---- History ----

    public List<DrawRecord> getHistory() {
        return new ArrayList<>(history);
    }

    public void clearHistory() {
        history.clear();
    }

    // ---- Inner types ----

    public static class DrawRecord {
        private final String winner;
        private final long timestamp;

        public DrawRecord(String winner) {
            this.winner = winner;
            this.timestamp = System.currentTimeMillis();
        }

        public String getWinner() { return winner; }
        public long getTimestamp() { return timestamp; }
    }

    public record DrawResult(String winner, String message) {}
    public record DrawMultiResult(List<String> winners, String message) {}
}
