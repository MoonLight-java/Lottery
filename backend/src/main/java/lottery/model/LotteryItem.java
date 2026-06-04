package lottery.model;

import java.util.Objects;

public class LotteryItem {
    private String name;
    private int weight;

    public LotteryItem() {}

    public LotteryItem(String name, int weight) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("名称不能为空");
        }
        if (weight <= 0) {
            throw new IllegalArgumentException("权重必须大于0: " + weight);
        }
        this.name = name.trim();
        this.weight = weight;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getWeight() { return weight; }
    public void setWeight(int weight) { this.weight = weight; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LotteryItem that)) return false;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return String.format("%s (权重: %d)", name, weight);
    }
}
