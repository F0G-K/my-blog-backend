package org.example.myblogbackend.common;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.function.Function;

/**
 * 统一分页返回:{ list, total, page, pageSize }。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> implements Serializable {

    private List<T> list;
    private long total;
    private long page;
    private long pageSize;

    /** 由 MyBatis-Plus 的 IPage 直接转换。 */
    public static <T> PageResult<T> of(IPage<T> page) {
        return new PageResult<>(page.getRecords(), page.getTotal(), page.getCurrent(), page.getSize());
    }

    /** 由 IPage 转换,同时把记录映射成对外 VO。 */
    public static <E, T> PageResult<T> of(IPage<E> page, Function<E, T> mapper) {
        List<T> records = page.getRecords().stream().map(mapper).toList();
        return new PageResult<>(records, page.getTotal(), page.getCurrent(), page.getSize());
    }
}
