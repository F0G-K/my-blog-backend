package org.example.myblogbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.myblogbackend.entity.Article;

@Mapper
public interface ArticleMapper extends BaseMapper<Article> {
}
