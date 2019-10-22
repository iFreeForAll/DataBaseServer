package com.mixer.raw;

import java.util.HashMap;

/**
 * Index of the row that has its byte position
 *
 * @version   1.0 21 Oct 2019
 * @author    Oleg Khlebnikov
 */

public final class Index {
    private static Index index;
    /** row number, byte pos */
    private HashMap<Long, Long> rowIndex;
    private long totalRowNumber = 0;

    private Index() {
        this.rowIndex = new HashMap<>();
    }

    public static Index getInstance() {
        if (index == null) {
            index = new Index();
        }
        return index;
    }

    public void add(long bytePosition) {
        this.rowIndex.put(this.totalRowNumber, bytePosition);
        this.totalRowNumber++;
    }

    public long getBytePosition(long rowNumber) {
        return this.rowIndex.getOrDefault(rowNumber, -1L);
    }

    public void remove(int row) {
        this.rowIndex.remove(row);
        this.totalRowNumber--;
    }

    public long getTotalRowNumber() {
        return this.totalRowNumber;
    }

    public void clear() {
        this.totalRowNumber = 0;
        this.rowIndex.clear();
    }
}