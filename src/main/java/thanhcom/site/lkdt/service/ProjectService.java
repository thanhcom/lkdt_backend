package thanhcom.site.lkdt.service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import thanhcom.site.lkdt.entity.Project;
import thanhcom.site.lkdt.enums.ErrCode;
import thanhcom.site.lkdt.exception.AppException;
import thanhcom.site.lkdt.mapper.ProjectMapper;
import thanhcom.site.lkdt.repository.ProjectRepository;

import java.util.List;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class ProjectService {

    ProjectRepository projectRepository;

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public Project getProjectById(Long id) {
        return projectRepository.findById(id).orElse(null);
    }

    public List<Project> getProjectsByTransactionId(Long transactionId) {
        return projectRepository.findByTransactionId(transactionId);
    }

    public List<Project> getProjectsByComponentId(Long componentId) {
        return projectRepository.findByComponentId(componentId);
    }

    public Project createProject(Project project) {
        return projectRepository.save(project);
    }

    public Project updateProject(Long id,Project project) {
        Project existingProject = projectRepository.findById(id).orElseThrow(() -> new AppException(ErrCode.PROJECT_NOT_FOUND));
        existingProject.setName(project.getName());
        existingProject.setDescription(project.getDescription());
        return projectRepository.save(existingProject);

    }

    public void deleteProject(Long id) {
        projectRepository.deleteById(id);
    }


}
