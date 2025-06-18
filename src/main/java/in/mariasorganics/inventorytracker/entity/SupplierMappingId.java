package in.mariasorganics.inventorytracker.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplierMappingId implements Serializable {

    private Long itemId;
    private Long supplierId;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof SupplierMappingId that))
            return false;
        return Objects.equals(itemId, that.itemId) &&
                Objects.equals(supplierId, that.supplierId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemId, supplierId);
    }
}
