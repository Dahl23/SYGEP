package com.sygep.util;

import java.util.Collections;
import java.util.List;

public final class Pagination {

    public static final int DEFAULT_PAGE_SIZE = 5;

    private Pagination() {
    }

    public static <T> List<T> page(List<T> items, int page, int pageSize) {
        if (items == null || items.isEmpty()) {
            return Collections.emptyList();
        }
        int safePage = Math.max(1, page);
        int from = (safePage - 1) * pageSize;
        if (from >= items.size()) {
            return Collections.emptyList();
        }
        int to = Math.min(from + pageSize, items.size());
        return items.subList(from, to);
    }

    public static int totalPages(int size, int pageSize) {
        if (size <= 0) {
            return 1;
        }
        return (int) Math.ceil((double) size / pageSize);
    }
}