package spharos.msg.domain.search.repository;

import java.util.List;
import org.springframework.stereotype.Repository;
import spharos.msg.domain.search.dto.SearchResponse.SearchInputDto;
import spharos.msg.domain.search.dto.SearchResponse.SearchProductDtos;

@Repository
public interface SearchRepository {

    SearchProductDtos searchAllProduct(String keyword, int index);

    List<SearchInputDto> searchAllKeyword(String keyword);
}
