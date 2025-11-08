package thanhcom.site.lkdt.controller;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import thanhcom.site.lkdt.dto.ProjectDto;
import thanhcom.site.lkdt.entity.Project;
import thanhcom.site.lkdt.mapper.ProjectMapper;
import thanhcom.site.lkdt.responseApi.ResponseApi;
import thanhcom.site.lkdt.service.ProjectService;

import java.util.List;

@RestController
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@RequestMapping("/project")
public class ProjectController {
    ProjectService projectService;
    ProjectMapper projectMapper;
    @GetMapping("/all")
    public ResponseEntity<?> getAllProjects(){
        ResponseApi<List<ProjectDto>> responseApi = new ResponseApi<>();
        responseApi.setMessenger("Get all projects successfully");
        responseApi.setData(projectMapper.toEntityList(projectService.getAllProjects()));
        responseApi.setResponseCode(3000);
        return ResponseEntity.ok(responseApi);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProjectById(@PathVariable Long id) {
        ResponseApi<ProjectDto> responseApi = new ResponseApi<>();
        responseApi.setMessenger("Get project by id successfully");
        responseApi.setData(projectMapper.toEntity(projectService.getProjectById(id)));
        responseApi.setResponseCode(3000);
        return ResponseEntity.ok(responseApi);
    }

    @GetMapping("/transaction/{id}")
    public ResponseEntity<?> getProjectWithTransactions(@PathVariable Long id) {
        ResponseApi<List<ProjectDto>> responseApi = new ResponseApi<>();
        List<Project> project = projectService.getProjectsByTransactionId(id);
        List<ProjectDto> projectDto = projectMapper.toEntityList(project);
        responseApi.setMessenger("Get List project with transactions successfully");
        responseApi.setData(projectDto);
        responseApi.setResponseCode(3000);
        return ResponseEntity.ok(responseApi);
    }

    @GetMapping("/component/{id}")
    public ResponseEntity<?> getProjectWithComponents(@PathVariable Long id) {
        ResponseApi<List<ProjectDto>> responseApi = new ResponseApi<>();
        List<Project> project = projectService.getProjectsByComponentId(id);
        List<ProjectDto> projectDto = projectMapper.toEntityList(project);
        responseApi.setMessenger("Get List project with components successfully");
        responseApi.setData(projectDto);
        responseApi.setResponseCode(3000);
        return ResponseEntity.ok(responseApi);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createProject(@RequestBody ProjectDto projectDto) {
        ResponseApi<ProjectDto> responseApi = new ResponseApi<>();
        Project project = projectMapper.toDto(projectDto);
        Project createdProject = projectService.createProject(project);
        responseApi.setMessenger("Create project successfully");
        responseApi.setData(projectMapper.toEntity(createdProject));
        responseApi.setResponseCode(3000);
        return ResponseEntity.ok(responseApi);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateProject(@PathVariable Long id, @RequestBody ProjectDto projectDto) {
        ResponseApi<ProjectDto> responseApi = new ResponseApi<>();
        Project project = projectMapper.toDto(projectDto);
        Project updatedProject = projectService.updateProject(id, project);
        responseApi.setMessenger("Update project successfully");
        responseApi.setData(projectMapper.toEntity(updatedProject));
        responseApi.setResponseCode(3000);
        return ResponseEntity.ok(responseApi);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteProject(@PathVariable Long id) {
        ResponseApi<Void> responseApi = new ResponseApi<>();
        projectService.deleteProject(id);
        responseApi.setMessenger("Delete project successfully");
        responseApi.setResponseCode(3000);
        return ResponseEntity.ok(responseApi);
    }
}
