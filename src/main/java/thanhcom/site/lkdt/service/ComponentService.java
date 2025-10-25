package thanhcom.site.lkdt.service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import thanhcom.site.lkdt.dto.request.ComponentRequest;
import thanhcom.site.lkdt.entity.Component;
import thanhcom.site.lkdt.enums.ErrCode;
import thanhcom.site.lkdt.exception.AppException;
import thanhcom.site.lkdt.mapper.ComponentMapper;
import thanhcom.site.lkdt.repository.ComponentRepository;

import java.util.List;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class ComponentService {

    ComponentMapper componentMapper;
    ComponentRepository componentRepository;
    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;

    public List<Component> getAllComponents(){
        return componentRepository.findAll();
    }

    public List<Component> getComponentsByType(String type) {
        return componentRepository.findAll().stream()
                .filter(component -> component.getType() != null
                        && component.getType().toLowerCase().contains(type.toLowerCase()))
                .toList();
    }


    public List<Component> getComponentsByManufacturer(String manufacturer){
        return componentRepository.findAll().stream()
                .filter(component -> component.getManufacturer() != null && component.getManufacturer().equalsIgnoreCase(manufacturer))
                .toList();
    }

    public List<Component> getComponentsByName(String name){
        return componentRepository.findAll().stream()
                .filter(component -> component.getName() != null && component.getName().equalsIgnoreCase(name))
                .toList();
    }


    public Component getComponentById(Long id){
        return componentRepository.findById(id).orElseThrow(() -> new AppException(ErrCode.COMPONENT_NOTFOUND));
    }

    public Component saveComponent(Component component){
        return componentRepository.save(component);
    }

    public Component updateComponentById(Long id, ComponentRequest updatedComponent){
        componentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrCode.COMPONENT_NOTFOUND));
        Component existingComponent;
        existingComponent = componentMapper.ReqToEntity(updatedComponent);
        existingComponent.setId(id);
        return componentRepository.save(existingComponent);
    }

    public void DeleteComponent(Long id){
        componentRepository.deleteById(id);
    }


}
