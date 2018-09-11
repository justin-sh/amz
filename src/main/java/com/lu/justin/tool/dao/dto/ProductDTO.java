package com.lu.justin.tool.dao.dto;

import com.lu.justin.tool.util.Money;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

@Document(collection = "products")
public class ProductDTO extends BaseDTO {

    @Indexed(unique = true)
    String prdId;
    String category;
    String name;
    boolean isTransferred;
    boolean isOverdueTransferred;
    Money interest;
    int investPeriod;
    Money value;
    Money amount;
    String nextDate;
    Money fee = Money.fromCNY(BigDecimal.ZERO);

    Date validFrom;
    Date validTo;

    private ProductDTO() {

    }

    public String getPrdId() {
        return prdId;
    }

    public String getCategory() {
        return category;
    }

    public String getName() {
        return name;
    }

    public boolean isTransferred() {
        return isTransferred;
    }

    public boolean isOverdueTransferred() {
        return isOverdueTransferred;
    }

    public BigDecimal getInterest() {
        return interest.toCNY();
    }

    public int getInvestPeriod() {
        return investPeriod;
    }

    public BigDecimal getValue() {
        return value.toCNY();
    }

    public BigDecimal getAmount() {
        return amount.toCNY();
    }

    public String getNextDate() {
        return nextDate;
    }

    public BigDecimal getFee() {
        return fee.toCNY();
    }

    public Date getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(Date validFrom) {
        this.validFrom = (Date) validFrom.clone();
    }

    public Date getValidTo() {
        return validTo;
    }

    public void setValidTo(Date validTo) {
        this.validTo = (Date) validTo.clone();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductDTO that = (ProductDTO) o;
        return Objects.equals(prdId, that.prdId);
    }

    @Override
    public int hashCode() {

        return Objects.hash(prdId);
    }

    public static class Builder {

        private ProductDTO productDTO;

        public Builder() {
            productDTO = new ProductDTO();
            productDTO.setBaseInfo();
        }

        public ProductDTO build() {
            return productDTO;
        }

        public Builder prdId(String prdId) {
            productDTO.prdId = prdId;
            return this;
        }

        public Builder category(String category) {
            productDTO.category = category;
            return this;
        }

        public Builder name(String name) {
            productDTO.name = name;
            return this;
        }

        public Builder isTransferred(boolean isTransfered) {
            productDTO.isTransferred = isTransfered;
            return this;
        }

        public Builder isOverdue(boolean isOverdue) {
            productDTO.isOverdueTransferred = isOverdue;
            return this;
        }

        public Builder interest(String interest) {
            productDTO.interest = Money.fromCNY(new BigDecimal(interest));
            return this;
        }

        public Builder investPeriod(String investPeriod) {
            productDTO.investPeriod = Integer.parseInt(investPeriod);
            return this;
        }

        public Builder value(String value) {
            productDTO.value = Money.fromCNY(new BigDecimal(value));
            return this;
        }

        public Builder amount(String amount) {
            productDTO.amount = Money.fromCNY(new BigDecimal(amount));
            return this;
        }

        public Builder nextDate(String nextDate) {
            productDTO.nextDate = nextDate;
            return this;
        }

        public Builder fee(String fee) {
            productDTO.fee = Money.fromCNY(new BigDecimal(fee));
            return this;
        }

        public Builder validFrom(Date validFrom) {
            productDTO.validFrom = (Date) validFrom.clone();
            return this;
        }

        public Builder validTo(Date validTo) {
            productDTO.validTo = (Date) validTo.clone();
            return this;
        }
    }
}
