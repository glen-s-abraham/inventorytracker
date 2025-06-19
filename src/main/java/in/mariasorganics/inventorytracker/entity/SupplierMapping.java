package in.mariasorganics.inventorytracker.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplierMapping {

  @EmbeddedId
  private SupplierMappingId id;

  @ManyToOne
  @MapsId("itemId")
  @JoinColumn(name = "item_id")
  private InventoryItem item;

  @ManyToOne
  @MapsId("supplierId")
  @JoinColumn(name = "supplier_id")
  private Supplier supplier;

  private int leadTimeInDays;
  private boolean preferred;
}
