package com.lu.justin.tool.dao;

import com.lu.justin.tool.dao.dto.ProductSummaryDTO;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Repository
public interface ProductSummaryDAO extends CrudRepository<ProductSummaryDTO, Long> {

    ProductSummaryDTO findByDate(Date date);

    List<ProductSummaryDTO> findByDateGreaterThanEqualAndCountGreaterThanOrderByDateDesc(LocalDate date, int count);
}
