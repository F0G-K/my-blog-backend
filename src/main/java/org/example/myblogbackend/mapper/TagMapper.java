package org.example.myblogbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.myblogbackend.entity.Tag;

@Mapper
public interface TagMapper extends BaseMapper<Tag> {
}
