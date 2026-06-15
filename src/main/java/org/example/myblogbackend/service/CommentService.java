package org.example.myblogbackend.service;

import org.example.myblogbackend.common.PageResult;
import org.example.myblogbackend.dto.AdminCommentItem;
import org.example.myblogbackend.dto.CommentForm;
import org.example.myblogbackend.dto.CommentVO;

import java.util.List;

public interface CommentService {

    /** 公开:某文章的已审核评论(顶级嵌套二级 replies)。 */
    List<CommentVO> listByArticle(Long articleId);

    /** 公开:提交评论(XSS 净化、待审核)。clientIp 供限流(批次 2)。 */
    void submit(CommentForm form, String clientIp);

    // ---- 管理 ----
    PageResult<AdminCommentItem> adminPage(String status, long page, long pageSize);

    void changeStatus(Long id, String status);

    void delete(Long id);
}
