package com.lu.justin.tool.dao;

import com.lu.justin.tool.dao.dto.ProductDTO;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ProductInfoDAO extends CrudRepository<ProductDTO, Long> {

    @Query(value = "from ProductDTO p where p.prdId in (?1)")
    List<ProductDTO> selectBySearcher(List<String> prdIds);

    @Query(value = "from ProductDTO p where p.validFrom >= ?1 and p.validTo <= ?2")
    List<ProductDTO> findByCondition(LocalDateTime from, LocalDateTime to);
}
