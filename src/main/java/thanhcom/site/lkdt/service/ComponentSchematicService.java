package thanhcom.site.lkdt.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import thanhcom.site.lkdt.entity.ComponentSchematic;
import thanhcom.site.lkdt.repository.ComponentSchematicRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ComponentSchematicService {
    ComponentSchematicRepository componentSchematicRepository;

    public ComponentSchematic createComponentSchematic(ComponentSchematic componentSchematic) {
        log.info("Creating new ComponentSchematic: {}", componentSchematic);
        return componentSchematicRepository.save(componentSchematic);
    }

    public ComponentSchematic getComponentSchematicById(Long id) {
        log.info("Fetching ComponentSchematic with id: {}", id);
        return componentSchematicRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ComponentSchematic not found with id: " + id));
    }

    public List<ComponentSchematic> getAllComponentSchematics() {
        log.info("Fetching all ComponentSchematics");
        return componentSchematicRepository.findAll();
    }

    public List<ComponentSchematic> getComponentSchematicsByComponentId(Long componentId) {
        log.info("Fetching ComponentSchematics with componentId: {}", componentId);
        return componentSchematicRepository.findByComponentId(componentId);
    }

    public ComponentSchematic updateComponentSchematic(Long id, ComponentSchematic updatedComponentSchematic) {
        log.info("Updating ComponentSchematic with id: {}", id);
        ComponentSchematic existingComponentSchematic = getComponentSchematicById(id);
        // Update fields as necessary
        existingComponentSchematic.setSchematicName(updatedComponentSchematic.getSchematicName());
        existingComponentSchematic.setDescription(updatedComponentSchematic.getDescription());
        existingComponentSchematic.setSchematicFile(updatedComponentSchematic.getSchematicFile());
        existingComponentSchematic.setSchematicImage(updatedComponentSchematic.getSchematicImage());
        // Add other fields to update here
        return componentSchematicRepository.save(existingComponentSchematic);
    }

    public void deleteComponentSchematic(Long id) {
        log.info("Deleting ComponentSchematic with id: {}", id);
        componentSchematicRepository.deleteById(id);
    }


}
