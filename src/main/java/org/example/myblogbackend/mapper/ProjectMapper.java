package org.example.myblogbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.myblogbackend.entity.Project;

@Mapper
public interface ProjectMapper extends BaseMapper<Project> {
}
