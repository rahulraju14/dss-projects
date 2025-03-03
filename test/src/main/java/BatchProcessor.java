import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class BatchProcessor {

    static BlockingQueue<List<String>> dataQueue = new LinkedBlockingQueue<>();
    static Map<String, List<List<String>>> taskExecuteMap = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        Runnable producerTask1 = producerTaskExecution(executorService, true);
        Runnable producerTask2 = producerTaskExecution(executorService, false);

        Future<?> producerFutureForUser1 = executorService.submit(producerTask1);
        Future<?> producerFutureForUser2 = executorService.submit(producerTask2);

        try {
            producerFutureForUser1.get();
            producerFutureForUser2.get();
        } catch (Exception ex) {
            log.error("Error while processing producerTask", ex);
        } finally {
            log.info("Executed finally block");
        }

        taskExecuteMap.forEach((threadName, producerResultHolder) -> {
            long totalRecordsProcessed = producerResultHolder.stream().flatMap(List::stream).count();
            log.info("Task completed by consumer thread: {} | Total Processed records: {}", threadName, totalRecordsProcessed);
            log.info("#####################################################");
        });

        executorService.shutdown();
    }

    private static Runnable producerTaskExecution(ExecutorService executorService, boolean failTask) {
        return () -> {
            log.info("-- Invoking producerThread: {}", Thread.currentThread().getName());
            int totalRecords = 40;
            int count = 0;
            int loopCount = 10;
            AtomicInteger processedRecord = new AtomicInteger(0);

            Future<?> consumerFuture = executorService.submit(consumerTaskExecution(totalRecords, processedRecord));

            try {
                while (count < loopCount) {
                    if (failTask && count == 6) {
                        throw new RuntimeException("Exception occurred by thread: {}".concat("--").concat(Thread.currentThread().getName()));
                    }

                    List<String> auditRecords = fetchRecords(count);
                    dataQueue.put(auditRecords);
                    count++;
                }
            } catch (Exception ex) {
                Thread.currentThread().interrupt();
                consumerFuture.cancel(Boolean.TRUE);
                log.error("Error occurred in producerTaskExecution : ", ex);
            }

            try {
                consumerFuture.get();
            } catch (ExecutionException | InterruptedException ex) {
                log.error("Error while processing consumer Task : ", ex);
            }

        };
    }

    private static Runnable consumerTaskExecution(int totalRecords, AtomicInteger processedRecord) {
        return () -> {
            log.info("--- Invoking consumer Thread......: {}", Thread.currentThread().getName());
            while (! Thread.currentThread().isInterrupted()) {
                try {
                    List<String> producerResult = dataQueue.take();
                    processFetchedRecords(producerResult, processedRecord);
                    log.info("-- Consumer thread processed: {}", Thread.currentThread().getName());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.error("Consumer Task Interrupted by thread: {}", Thread.currentThread().getName(), e);
                }

                if (processedRecord.get() >= totalRecords) {
                    break;
                }
            }
        };
    }

    private static List<String> fetchRecords(int count) {
        log.info("-- fetching record for count: {}", count);
        String producerThread = Thread.currentThread().getName();
        return List.of(producerThread.concat("---").concat(UUID.randomUUID().toString().substring(0, 4)),
                producerThread.concat("---").concat(UUID.randomUUID().toString().substring(0, 4)),
                producerThread.concat("---").concat(UUID.randomUUID().toString().substring(0, 4)),
                producerThread.concat("---").concat(UUID.randomUUID().toString().substring(0, 4)));
    }

    private static void processFetchedRecords(List<String> producerResult, AtomicInteger processedRecord) {
        processedRecord.addAndGet(producerResult.size());
        log.info("-- processing records....: {}", processedRecord.get());
        String consumerThread = Thread.currentThread().getName();
        taskExecuteMap.computeIfAbsent(consumerThread, k -> new ArrayList<>()).add(producerResult);
    }

}
