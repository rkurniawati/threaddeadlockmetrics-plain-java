package org.gooddog;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.jvm.JvmThreadDeadlockMetrics;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.gooddog.diningphilosophers.DiningPhilosophers;
import org.gooddog.diningphilosophers.PhilosopherWithMonitorLock;
import org.gooddog.diningphilosophers.PhilosopherWithReentrantLock;
import org.slf4j.Logger;

public class Application {
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(Application.class);
    public static void main(String[] args) throws InterruptedException {
        MeterRegistry meterRegistry = new SimpleMeterRegistry();
        new JvmThreadDeadlockMetrics().bindTo(meterRegistry);

        startDiningPhilosophers(new DiningPhilosophers(PhilosopherWithMonitorLock.createPhilosophers(5), 3), meterRegistry);

        startDiningPhilosophers(new DiningPhilosophers(PhilosopherWithReentrantLock.createPhilosophers(7), 3), meterRegistry);

        logger.info("Done, to exit press Ctrl+C");
    }

    private static void startDiningPhilosophers(DiningPhilosophers diningPhilosophers, MeterRegistry meterRegistry) throws InterruptedException {
        logger.info("");
        logger.info("Deadlock metrics before starting dining philosophers");
        printDeadlockMetrics(meterRegistry);
        final double initial = meterRegistry.get("jvm.threads.deadlocked").gauge().value();
        logger.info("Starting dining philosophers");
        diningPhilosophers.startDinner();
        logger.info("Waiting for deadlock to occur..");

        // busy wait until deadlock occurs
        while (true) {
            double current = meterRegistry.get("jvm.threads.deadlocked").gauge().value();
            if (current > initial) {
                break;
            }
            Thread.sleep(1000);
        }

        logger.info("Deadlock metrics after deadlock(s) happen dining philosophers");
        printDeadlockMetrics(meterRegistry);
    }

    private static void printDeadlockMetrics(MeterRegistry meterRegistry) {
        meterRegistry.getMeters().stream().filter(meter -> meter.getId().getName().contains("deadlock")).forEach(m -> {
            Gauge g = (Gauge) m;
            logger.info(g.getId().getName() + " = " + g.value());
        });
    }
}
