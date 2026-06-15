package org.example.myblogbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.myblogbackend.dto.CountResult;
import org.example.myblogbackend.entity.ArticleTag;

import java.util.List;

@Mapper
public interface ArticleTagMapper extends BaseMapper<ArticleTag> {

    /** 各标签下「已发布且未删除」文章数。 */
    @Select("SELECT at.tag_id AS id, COUNT(*) AS count FROM article_tag at "
            + "JOIN article a ON a.id = at.article_id "
            + "WHERE a.is_deleted = 0 AND a.status = 1 GROUP BY at.tag_id")
    List<CountResult> countPublishedByTag();
}
