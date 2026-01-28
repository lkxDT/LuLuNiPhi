package de.htwsaar.MockRepository;

import de.htwsaar.domain.AssignedSkill;
import de.htwsaar.repository.EmployeeSkillRepository;
import org.jooq.DSLContext;

import java.util.ArrayList;
import java.util.List;

public class MockEmployeeSkillRepository extends EmployeeSkillRepository {

    private static class MockAssignment {
        int empId;
        int skillId;
        int level;
        String sinceDate;

        MockAssignment(int empId, int skillId, int level, String sinceDate) {
            this.empId = empId;
            this.skillId = skillId;
            this.level = level;
            this.sinceDate = sinceDate;
        }
    }

    private final List<MockAssignment> assignments = new ArrayList<>();

    public MockEmployeeSkillRepository() {
        super((DSLContext) null);
    }

    @Override
    public void assign(int employeeId, int skillId, int level, String sinceDate) {
        assignments.add(new MockAssignment(employeeId, skillId, level, sinceDate));
    }

    @Override
    public List<AssignedSkill> findSkillsByEmployeeId(int employeeId) {
        List<AssignedSkill> result = new ArrayList<>();
        for (MockAssignment a : assignments) {
            if (a.empId == employeeId) {
                result.add(new AssignedSkill(
                        a.skillId,
                        "Mock Skill " + a.skillId,
                        "Category",
                        a.level,
                        a.sinceDate
                ));
            }
        }
        return result;
    }

    @Override
    public boolean existsAssignment(int employeeId, int skillId) {
        return assignments.stream()
                .anyMatch(a -> a.empId == employeeId && a.skillId == skillId);
    }

    @Override
    public void updateAssignment(int employeeId, int skillId, int level, String sinceDate) {
        assignments.removeIf(a -> a.empId == employeeId && a.skillId == skillId);
        assignments.add(new MockAssignment(employeeId, skillId, level, sinceDate));
    }

    @Override
    public void removeAssignment(int employeeId, int skillId) {
        assignments.removeIf(a -> a.empId == employeeId && a.skillId == skillId);
    }
}
