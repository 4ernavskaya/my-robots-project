package log;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LogBuffer implements Iterable<LogEntry> {
    private final LogEntry[] buffer;
    private final int capacity;
    private int head = 0;
    private int tail = 0;
    private int size = 0;
    private final ReadWriteLock rwLock = new ReentrantReadWriteLock(true);


    public LogBuffer(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        this.capacity = capacity;
        this.buffer = new LogEntry[capacity];
    }


    public void add(LogEntry entry) {
        Objects.requireNonNull(entry, "LogEntry cannot be null");
        rwLock.writeLock().lock();
        try {
            buffer[tail] = entry;
            tail = (tail + 1) % capacity;
            if (size == capacity) {
                head = (head + 1) % capacity;
            } else {
                size++;
            }
        } finally {
            rwLock.writeLock().unlock();
        }
    }


    public List<LogEntry> getRange(int startFrom, int count) {
        rwLock.readLock().lock();
        try {
            if (startFrom < 0 || startFrom >= size) {
                return Collections.emptyList();
            }
            int actualCount = Math.min(count, size - startFrom);
            List<LogEntry> result = new ArrayList<>(actualCount);
            for (int i = 0; i < actualCount; i++) {
                int physicalIndex = (head + startFrom + i) % capacity;
                result.add(buffer[physicalIndex]);
            }
            return Collections.unmodifiableList(result);
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public List<LogEntry> all() {
        return getRange(0, size);
    }


    public int size() {
        rwLock.readLock().lock();
        try {
            return size;
        } finally {
            rwLock.readLock().unlock();
        }
    }


    public int capacity() {
        return capacity;
    }


    @Override
    public Iterator<LogEntry> iterator() {
        rwLock.readLock().lock();
        try {
            List<LogEntry> snapshot = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                snapshot.add(buffer[(head + i) % capacity]);
            }
            return snapshot.iterator();
        } finally {
            rwLock.readLock().unlock();
        }
    }


    public void clear() {
        rwLock.writeLock().lock();
        try {
            Arrays.fill(buffer, null);
            head = 0;
            tail = 0;
            size = 0;
        } finally {
            rwLock.writeLock().unlock();
        }
    }
}