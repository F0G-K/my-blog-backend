package org.example.myblogbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.myblogbackend.entity.ArticleTag;

@Mapper
public interface ArticleTagMapper extends BaseMapper<ArticleTag> {
}
