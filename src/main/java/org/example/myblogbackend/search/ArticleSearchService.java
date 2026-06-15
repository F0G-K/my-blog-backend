package org.example.myblogbackend.search;

import lombok.RequiredArgsConstructor;
import org.example.myblogbackend.common.PageResult;
import org.example.myblogbackend.dto.SearchItem;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.highlight.Highlight;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightField;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/** ES 文章索引的写入 / 删除 / 检索(标题 + 正文 + 标签,带高亮)。 */
@Service
@RequiredArgsConstructor
public class ArticleSearchService {

    private final ArticleSearchRepository repository;
    private final ElasticsearchOperations operations;

    public void save(ArticleDoc doc) {
        repository.save(doc);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public PageResult<SearchItem> search(String keyword, long page, long pageSize) {
        if (keyword == null || keyword.isBlank()) {
            return new PageResult<>(List.of(), 0, page, pageSize);
        }
        Highlight highlight = new Highlight(List.of(
                new HighlightField("title"), new HighlightField("content")));
        NativeQuery query = NativeQuery.builder()
                .withQuery(q -> q.multiMatch(m -> m
                        .query(keyword)
                        .fields("title", "content", "tags")))
                .withHighlightQuery(new HighlightQuery(highlight, ArticleDoc.class))
                .withPageable(PageRequest.of((int) (page - 1), (int) pageSize))
                .build();

        SearchHits<ArticleDoc> hits = operations.search(query, ArticleDoc.class);
        List<SearchItem> items = hits.getSearchHits().stream().map(this::toItem).toList();
        return new PageResult<>(items, hits.getTotalHits(), page, pageSize);
    }

    private SearchItem toItem(SearchHit<ArticleDoc> hit) {
        ArticleDoc doc = hit.getContent();
        SearchItem item = new SearchItem();
        item.setId(doc.getId());
        item.setTitle(doc.getTitle());
        item.setSummary(doc.getSummary());
        item.setCoverUrl(doc.getCoverUrl());
        item.setCategoryName(doc.getCategoryName());
        item.setTags(doc.getTags());
        item.setViewCount(doc.getViewCount());
        item.setPublishedAt(doc.getPublishedAt());

        Map<String, List<String>> hl = hit.getHighlightFields();
        SearchItem.Highlight highlight = new SearchItem.Highlight();
        highlight.setTitle(hl.get("title"));
        highlight.setContent(hl.get("content"));
        item.setHighlight(highlight);
        return item;
    }
}
