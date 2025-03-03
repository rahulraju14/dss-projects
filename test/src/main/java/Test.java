import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.text.DecimalFormat;
import java.util.stream.IntStream;


@Slf4j
public class Test {

    static Map<Integer, List<Integer>> workBookTracker = new ConcurrentHashMap<>();
    static final int maxRowCount = 1_000;
    private static BlockingQueue<List<String>> queue = new LinkedBlockingQueue<>();


    public static void main(String[] args) {
        Map<String, List<List<String>>> map = new HashMap<>();
        List<String> temp = new ArrayList<>();
        temp.add("r1");
        temp.add("r2");
        temp.add("r3");

        List<String> temp1 = new ArrayList<>();
        temp1.add("k1");
        temp1.add("k2");
        temp1.add("k3");
        temp1.add("k4");

        List<List<String>> list = new ArrayList<>();
        list.add(temp);
        list.add(temp1);

        map.put("test", list);

        map.forEach((k, v) -> {
            long count = v.stream().flatMap(List::stream).count();
            log.info("-- total count: {}", count);
        });


    }

    interface Greeter {
        void greet(String name);
    }

    public static void greetClients(Greeter greeter) {
        IntStream.range(0, 50).forEach(index -> {
            greeter.greet("Alex" + "-" + index);
            log.info("------------------------------");
        });
    }

    private static void test() {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        AtomicInteger totalCount = new AtomicInteger(0);

        Runnable processRecords = () -> {
            log.info("checking bucket content.....");
            try {
                while (true) {
                    List<String> bucket = queue.take();
                    bucket.forEach(e -> log.info("processing record: {} ", e));
                    totalCount.getAndAdd(bucket.size());
                    log.info("--- Total records processed till now: {} ", totalCount.get());
                    log.info("-------------------------------------------------------------");
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        };

        Runnable updateQueue = () -> {
            log.info("Adding records to queue");
            try {
                Thread.sleep(5000);
                for (int i = 0; i < 5; i++) {
                    queue.put(List.of("rec-".concat(UUID.randomUUID().toString()),
                            "rec-".concat(UUID.randomUUID().toString()),
                            "rec-".concat(UUID.randomUUID().toString())));
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        };

        executorService.submit(processRecords);
        executorService.submit(updateQueue);

        executorService.shutdown();
    }

    private static void task() {
        int totalRecords = 10_000;
        int chunkSize = totalRecords / 4;
        Counter counter = new Counter();
        for (int i = 0; i < totalRecords; i += chunkSize) {
            int end = Math.min(i + chunkSize, totalRecords);
            log.info("-- Processing chunk for startIndex: {} | endIndex: {}", i, end);
            counter.setStartIndex(counter.getLastIndex() > 0 ? counter.getLastIndex() : i);
            counter.setEndIndex(counter.getLastIndex() > 0 ? end - i : end);
            counter.setTaskComplete(false);
            counter.setUpdateHeader(true);
            processTask(counter);
            logInfo();
            log.info("-- Active Sheet Count: {} | lastIndex: {}", counter.getSheetCount(), counter.getLastIndex());
            log.info("===================================================================");
        }

        log.info("************************************************************************");
        long recordsImported = workBookTracker.values().stream().map(Collection::size)
                .peek(p -> log.info("List Size: {}", p))
                .reduce(0, Integer::sum);

        log.info("-- Total Records imported: {}", recordsImported);
    }

    @NoArgsConstructor
    @Setter
    @Getter
    static class Entity {
        private int rowNo;
    }

    public static void logInfo() {
        workBookTracker.forEach((k, v) -> {
            log.info("sheet: {} | rowCount: {}", k, v.size());
        });
    }

    public static void processTask(Counter counter) {
        while (!counter.isTaskComplete()) {
            createHeaderColumn(counter);
            fillWorkbook(counter);
        }
    }

    public static void fillWorkbook(Counter counter) {
        int endIndex = counter.getEndIndex();

        for (int rowCount = counter.getStartIndex(); rowCount < counter.getEndIndex(); rowCount++) {
            if (rowCount >= maxRowCount) {
                int calcEndIndex = endIndex - rowCount;
                counter.incrementSheetCount();
                counter.resetStartIndex();
                counter.setEndIndex(calcEndIndex);
                counter.setLastIndex(calcEndIndex);
                counter.setUpdateHeader(true);
                log.info("Recalculating startIndex: {} | endIndex: {} for sheet: {}", counter.getStartIndex(), counter.getEndIndex(),
                        counter.getSheetCount());
                break;
            } else {
                if (rowCount > 0) {
                    int activeSheetCount = counter.getSheetCount();
                    updateWorkBookTracker(rowCount, activeSheetCount);
                    if (rowCount == (endIndex - 1)) {
                        counter.setTaskComplete(true);
                    }
                }
            }
        }
    }

    public static void createHeaderColumn(Counter counter) {
        if (counter.isUpdateHeader()) {
            updateWorkBookTracker(0, counter.getSheetCount());
            counter.setUpdateHeader(false);
        }
    }

    public static void updateWorkBookTracker(int rowCount, int activeSheetCount) {
        if (workBookTracker.containsKey(activeSheetCount) && rowCount > 0) {
            workBookTracker.get(activeSheetCount).add(rowCount);
        } else {
            workBookTracker.put(activeSheetCount, new ArrayList<>() {{
                add(rowCount);
            }});
        }
    }

    @Getter
    @Setter
    static class Counter {
        private int sheetCount = 0;
        private int startIndex;
        private int endIndex;
        private int lastIndex = 0;
        private boolean updateHeader = false;
        private boolean isTaskComplete = true;

        public void resetCount() {
            this.sheetCount = 0;
        }

        public void incrementSheetCount() {
            this.sheetCount++;
        }

        public void resetStartIndex() {
            this.startIndex = 0;
        }

        public void resetEndIndex() {
            this.endIndex = 0;
        }

    }
}
