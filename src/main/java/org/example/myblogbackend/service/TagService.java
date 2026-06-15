package org.example.myblogbackend.service;

import org.example.myblogbackend.dto.IdVO;
import org.example.myblogbackend.dto.TagForm;
import org.example.myblogbackend.dto.TagVO;

import java.util.List;

public interface TagService {

    List<TagVO> list();

    IdVO create(TagForm form);

    void delete(Long id);
}
