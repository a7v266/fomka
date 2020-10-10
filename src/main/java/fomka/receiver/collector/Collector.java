package fomka.receiver.collector;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class Collector {

    private final File outputFile;
    private final AtomicBoolean merged = new AtomicBoolean();
    private final Map<Integer, Path> parts = new ConcurrentSkipListMap<>();
    private int size;

    public Collector(String fileName, Path downloadPath) {
        this.outputFile = downloadPath.resolve(fileName).toFile();
    }

    public void addSize(int size) {
        if (this.size > 0) {
            throw new CollectorSizeAlreadySetException();
        }
        this.size = size;
    }

    public void addPart(Integer partNumber, Path partPath) throws IOException {
        parts.put(partNumber, partPath);
        if (size == parts.size() && merged.compareAndSet(false, true)) {
            try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
                for (Map.Entry<Integer, Path> entry : parts.entrySet()) {
                    byte[] data = Files.readAllBytes(entry.getValue());
                    int coverLength = findCoverLength(data);
                    outputStream.write(decode(data), coverLength, data.length - coverLength);
                }
            }
        }
    }

    private int findCoverLength(byte[] data) throws IllegalFormatException {
        byte marker = (byte) 0xFF;
        byte eoi = (byte) 0xD9;
        for (int index = 0; index < data.length; index++) {
            if (data[index] == marker && data[index + 1] == eoi) {
                return index + 2;
            }
        }
        throw new IllegalFormatException();
    }

    private byte[] decode(byte[] data) {
        for (int index = 0; index < data.length; index++) {
            data[index] = (byte) ~data[index];
        }
        return data;
    }
}
