package com.rupeek.hotelbooking.search.filters;

import com.rupeek.hotelbooking.domain.common.Money;
import com.rupeek.hotelbooking.domain.property.Property;
import com.rupeek.hotelbooking.domain.property.RoomType;
import com.rupeek.hotelbooking.search.PropertyFilter;
import com.rupeek.hotelbooking.search.SearchCriteria;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PriceRangeFilter implements PropertyFilter {
    @Override
    public boolean matches(
            Property property,
            SearchCriteria criteria
    ) {
        if (criteria.getMinPrice() == null
                && criteria.getMaxPrice() == null) {
            return true;
        }

        for (RoomType roomType : property.getRoomTypes()) {

            Money rate = roomType.getBasePricePerNight();

            if (criteria.getMinPrice() != null
                    && rate.getAmount().compareTo(
                            criteria.getMinPrice().getAmount()
                    ) < 0) {
                continue;
            }

            if (criteria.getMaxPrice() != null
                    && rate.getAmount().compareTo(
                            criteria.getMaxPrice().getAmount()
                    ) > 0) {
                continue;
            }

            return true;
        }

        return false;
    }
}
