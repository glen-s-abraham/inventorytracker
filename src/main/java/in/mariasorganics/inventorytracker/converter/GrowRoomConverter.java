package in.mariasorganics.inventorytracker.converter;

import in.mariasorganics.inventorytracker.entity.GrowRoom;
import in.mariasorganics.inventorytracker.repository.GrowRoomRepository;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class GrowRoomConverter implements Converter<String, GrowRoom> {

    private final GrowRoomRepository repo;

    public GrowRoomConverter(GrowRoomRepository repo) {
        this.repo = repo;
    }

    @Override
    public GrowRoom convert(String source) {
        if (source == null || source.trim().isEmpty()) {
            return null; // ← simply ignore blank values
        }
        try {
            Long id = Long.parseLong(source);
            return repo.findById(id).orElse(null);
        } catch (NumberFormatException e) {
            return null; // ← also ignore non‑numeric garbage
        }
    }
}
