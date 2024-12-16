package com.music.repository;

import com.music.document.IntegratedSearch;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IntegratedSearchRepository extends ElasticsearchRepository<IntegratedSearch, String> {

}
