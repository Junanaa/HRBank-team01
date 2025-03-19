package com.project.hrbank.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.hrbank.dto.DepartmentDto;
import com.project.hrbank.entity.Department;
import com.project.hrbank.repository.DepartmentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

	private final DepartmentRepository departmentRepository;

	@Override
	@Transactional
	public DepartmentDto createDepartment(DepartmentDto dto) {
		if (departmentRepository.existsByName(dto.name())) {
			throw new IllegalArgumentException("Department with name " + dto.name() + " already exists.");
		}

		Department department = new Department();
		department.setName(dto.name());
		department.setDescription(dto.description());
		department.setEstablishedDate(dto.establishedDate());
		departmentRepository.save(department);

		return new DepartmentDto(
			department.getId(),
			department.getName(),
			department.getDescription(),
			department.getEstablishedDate(),
			0,
			department.getCreatedAt()
		);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<DepartmentDto> getDepartmentById(Long id) {
		return departmentRepository.findById(id)
			.map(department -> new DepartmentDto(
				department.getId(),
				department.getName(),
				department.getDescription(),
				department.getEstablishedDate(),
				getEmployeeCount(department.getId()), // Keep this if employeeCount is needed
				department.getCreatedAt()
			));
	}

	@Override
	@Transactional(readOnly = true)
	public Page<DepartmentDto> getAllDepartments(Pageable pageable) {
		return departmentRepository.findAll(pageable)
			.map(department -> new DepartmentDto(
				department.getId(),
				department.getName(),
				department.getDescription(),
				department.getEstablishedDate(),
				getEmployeeCount(department.getId()),
				department.getCreatedAt()
			));
	}

	@Override
	@Transactional
	public DepartmentDto updateDepartment(Long id, DepartmentDto dto) {
		Department department = departmentRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("Department not found"));

		if (!department.getName().equals(dto.name()) && departmentRepository.existsByName(dto.name())) {
			throw new IllegalArgumentException("Department name already exists");
		}

		department.setName(dto.name());
		department.setDescription(dto.description());
		department.setEstablishedDate(dto.establishedDate());
		departmentRepository.save(department);

		return new DepartmentDto(
			department.getId(),
			department.getName(),
			department.getDescription(),
			department.getEstablishedDate(),
			getEmployeeCount(department.getId()),
			department.getCreatedAt()
		);
	}

	@Override
	@Transactional
	public void deleteDepartment(Long id) {
		Department department = departmentRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("Department not found"));

		if (getEmployeeCount(id) > 0) {
			throw new IllegalStateException("Cannot delete department with existing employees");
		}

		departmentRepository.delete(department);
	}

	private long getEmployeeCount(Long departmentId) {
		// TODO: Replace with actual query from EmployeeRepository
		return 0;
	}
}
