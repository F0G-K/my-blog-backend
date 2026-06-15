package org.example.myblogbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.myblogbackend.dto.CountResult;
import org.example.myblogbackend.entity.Article;

import java.util.List;

@Mapper
public interface ArticleMapper extends BaseMapper<Article> {

    /** 各分类下「已发布且未删除」文章数。自定义 SQL 需手动加 is_deleted 过滤。 */
    @Select("SELECT category_id AS id, COUNT(*) AS count FROM article "
            + "WHERE is_deleted = 0 AND status = 1 GROUP BY category_id")
    List<CountResult> countPublishedByCategory();
}
