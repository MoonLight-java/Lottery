package lottery.controller;

import lottery.model.LotteryItem;
import lottery.service.LotteryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/lottery")
@CrossOrigin(origins = "*")
public class LotteryController {

    private final LotteryService service;

    public LotteryController(LotteryService service) {
        this.service = service;
    }

    // ---- Items ----

    @GetMapping("/items")
    public ResponseEntity<List<LotteryItem>> listItems() {
        return ResponseEntity.ok(service.listItems());
    }

    @PostMapping("/items")
    public ResponseEntity<Map<String, Object>> addItem(@RequestBody LotteryItem item) {
        try {
            service.addItem(item);
            return ResponseEntity.ok(Map.of("success", true, "message", "添加成功: " + item.getName()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @DeleteMapping("/items/{name}")
    public ResponseEntity<Map<String, Object>> removeItem(@PathVariable String name) {
        boolean removed = service.removeItem(name);
        if (removed) {
            return ResponseEntity.ok(Map.of("success", true, "message", "删除成功: " + name));
        }
        return ResponseEntity.ok(Map.of("success", false, "message", "未找到: " + name));
    }

    @DeleteMapping("/items")
    public ResponseEntity<Map<String, Object>> clearItems() {
        service.clearItems();
        return ResponseEntity.ok(Map.of("success", true, "message", "已清空所有奖品"));
    }

    // ---- Draw ----

    @PostMapping("/draw/single")
    public ResponseEntity<Map<String, Object>> drawSingle() {
        var result = service.drawSingle();
        if (result.winner() == null) {
            return ResponseEntity.ok(Map.of("success", false, "message", result.message()));
        }
        return ResponseEntity.ok(Map.of(
                "success", true,
                "winner", result.winner(),
                "message", result.message()
        ));
    }

    @PostMapping("/draw/multiple")
    public ResponseEntity<Map<String, Object>> drawMultiple(@RequestBody Map<String, Integer> body) {
        int count = body.getOrDefault("count", 1);
        var result = service.drawMultiple(count);
        return ResponseEntity.ok(Map.of(
                "success", !result.winners().isEmpty(),
                "winners", result.winners(),
                "message", result.message()
        ));
    }

    // ---- History ----

    @GetMapping("/history")
    public ResponseEntity<List<LotteryService.DrawRecord>> getHistory() {
        return ResponseEntity.ok(service.getHistory());
    }

    @DeleteMapping("/history")
    public ResponseEntity<Map<String, Object>> clearHistory() {
        service.clearHistory();
        return ResponseEntity.ok(Map.of("success", true, "message", "历史记录已清空"));
    }
}
