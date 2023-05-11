package com.application.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PageData<T> {
    private Integer pageIndex;
    private Integer pageSize;
    private int totalPages;
    private int totalRecords;
    private int beginIndex;
    private int endIndex;
    private List<T> data;

    public PageData(Page page) {
        this.setPageIndex(page.getPageable().getPageNumber() + 1);
        this.setPageSize(page.getPageable().getPageSize());
        this.setBeginIndex(Math.toIntExact(page.getPageable().getOffset()));
        this.setEndIndex(Math.toIntExact(page.getPageable().getOffset() + page.getNumberOfElements()));
        this.setTotalPages(page.getTotalPages());
        this.setTotalRecords(Math.toIntExact(page.getTotalElements()));
        this.setData(page.toList());
    }

    public static <T> PageData<T> of(Page page, List<T> list) {
        PageData<T> pageData = new PageData<>();
        pageData.setPageIndex(page.getPageable().getPageNumber() + 1);
        pageData.setPageSize(page.getPageable().getPageSize());
        pageData.setBeginIndex(Math.toIntExact(page.getPageable().getOffset()));
        pageData.setEndIndex(Math.toIntExact(page.getPageable().getOffset() + page.getNumberOfElements()));
        pageData.setTotalPages(page.getTotalPages());
        pageData.setTotalRecords(Math.toIntExact(page.getTotalElements()));
        pageData.setData(list);
        return pageData;
    }

    @Override
    public String toString() {
        return "Page{" +
                "pageIndex=" + pageIndex +
                ", pageSize=" + pageSize +
                ", totalPages=" + totalPages +
                ", totalRecords=" + totalRecords +
                ", beginIndex=" + beginIndex +
                ", endIndex=" + endIndex +
                ", data.size=" + (data == null ? null : data.size()) +
                '}';
    }
}
