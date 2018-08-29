package com.lu.justin.tool.dao.dto;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

@Document(collection = "product_summary")
@CompoundIndex(unique = true, name = "date_time", def = "{'date':1, 'time':1}")
public class ProductSummaryDTO extends BaseDTO {

    // every minutes
//    @NotNull
//    LocalDate date;
//    @NotNull
//    LocalTime time;
    // product value
    BigDecimal value;

    //product transfer amount
    BigDecimal amount;
    int count;

    public ProductSummaryDTO() {
        this.value = BigDecimal.ZERO;
        this.amount = BigDecimal.ZERO;
        this.count = 0;
    }

    public ProductSummaryDTO(/*@NotNull LocalDate date, @NotNull LocalTime time,*/ BigDecimal value, BigDecimal amount, int count) {
//        this.date = date;
//        this.time = time;
        this.value = value;
        this.amount = amount;
        this.count = count;
        super.setBaseInfo();
    }

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        ProductSummaryDTO that = (ProductSummaryDTO) o;
//        return Objects.equals(date, that.date) && Objects.equals(time, that.time);
//    }
//
//    @Override
//    public int hashCode() {
//
//        return Objects.hash(LocalDateTime.of(date, time));
//    }
}
