package de.htwsaar.communication.assignment;

import de.htwsaar.application.EmployeeService;
import de.htwsaar.application.SkillService;
import de.htwsaar.communication.InputReader;
import de.htwsaar.communication.OutputRenderer;
import de.htwsaar.domain.Employee;
import de.htwsaar.MuteConsoleExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Collections;
import java.util.Optional;

import static org.easymock.EasyMock.*;

@ExtendWith(MuteConsoleExtension.class)
class EmployeeSkillAssignMenuTest {

    private OutputRenderer outMock;
    private InputReader inMock;
    private SkillService skillServiceMock;
    private EmployeeService empServiceMock;

    @BeforeEach
    void setUp() {
        outMock = createNiceMock(OutputRenderer.class);
        inMock = createMock(InputReader.class);
        skillServiceMock = createMock(SkillService.class);
        empServiceMock = createMock(EmployeeService.class);
    }

    /**
     * Creates a dummy Employee for Tests
     * @param id EmployeeID
     * @return   a Employee object
     */
    private Employee createTestEmployee(int id) {
        return new Employee(
                id, "John", "Doe", "john@company.com",
                "Dev", "Active", "2026-01-01", 40
        );
    }

    @Test
    void testAssignSkillSuccess() {
        int empId = 1;
        int skillId = 10;
        int level = 4;
        String date = "2026-01-01";
        Employee emp = createTestEmployee(empId);

        expect(inMock.readInt("Invalid employee ID.")).andReturn(empId);
        expect(empServiceMock.findEmployeeByID(empId)).andReturn(Optional.of(emp));
        expect(skillServiceMock.findAllSkills()).andReturn(Collections.emptyList());

        outMock.skillTable(Collections.emptyList());
        expectLastCall().once();

        expect(inMock.readInt("Invalid skill ID.")).andReturn(skillId);
        expect(inMock.readInt("Invalid level.")).andReturn(level);
        expect(inMock.readLine()).andReturn(date);

        skillServiceMock.assignSkill(empId, skillId, level, date);
        expectLastCall().once();

        outMock.info("Skill assigned.");
        expectLastCall().once();

        replay(outMock, inMock, skillServiceMock, empServiceMock);
        EmployeeSkillAssignMenu.show(outMock, inMock, empServiceMock, skillServiceMock);
        verify(outMock, inMock, skillServiceMock, empServiceMock);
    }

    @Test
    void testEmployeeNotFound() {
        int empId = 99;
        expect(inMock.readInt("Invalid employee ID.")).andReturn(empId);
        expect(empServiceMock.findEmployeeByID(empId)).andReturn(Optional.empty());

        outMock.error("Employee not found.");
        expectLastCall().once();

        replay(outMock, inMock, skillServiceMock, empServiceMock);
        EmployeeSkillAssignMenu.show(outMock, inMock, empServiceMock, skillServiceMock);
        verify(outMock, inMock, skillServiceMock, empServiceMock);
    }

    @Test
    void testInvalidEmployeeIdFormat() {
        expect(inMock.readInt("Invalid employee ID."))
                .andThrow(new IllegalArgumentException("Invalid employee ID."));

        outMock.error("Invalid employee ID.");
        expectLastCall().once();

        replay(outMock, inMock, skillServiceMock, empServiceMock);
        EmployeeSkillAssignMenu.show(outMock, inMock, empServiceMock, skillServiceMock);
        verify(outMock, inMock, skillServiceMock, empServiceMock);
    }

    @Test
    void testInvalidSkillIdFormat() {
        int empId = 1;
        Employee emp = createTestEmployee(empId);

        expect(inMock.readInt("Invalid employee ID.")).andReturn(empId);
        expect(empServiceMock.findEmployeeByID(empId)).andReturn(Optional.of(emp));
        expect(skillServiceMock.findAllSkills()).andReturn(Collections.emptyList());

        outMock.skillTable(Collections.emptyList());
        expectLastCall().once();

        expect(inMock.readInt("Invalid skill ID."))
                .andThrow(new IllegalArgumentException("Invalid skill ID."));

        outMock.error("Invalid skill ID.");
        expectLastCall().once();

        replay(outMock, inMock, skillServiceMock, empServiceMock);
        EmployeeSkillAssignMenu.show(outMock, inMock, empServiceMock, skillServiceMock);
        verify(outMock, inMock, skillServiceMock, empServiceMock);
    }

    @Test
    void testInvalidLevelFormat() {
        int empId = 1;
        Employee emp = createTestEmployee(empId);

        expect(inMock.readInt("Invalid employee ID.")).andReturn(empId);
        expect(empServiceMock.findEmployeeByID(empId)).andReturn(Optional.of(emp));
        expect(skillServiceMock.findAllSkills()).andReturn(Collections.emptyList());

        outMock.skillTable(Collections.emptyList());
        expectLastCall().once();

        expect(inMock.readInt("Invalid skill ID.")).andReturn(10);
        expect(inMock.readInt("Invalid level."))
                .andThrow(new IllegalArgumentException("Invalid level."));

        outMock.error("Invalid level.");
        expectLastCall().once();

        replay(outMock, inMock, skillServiceMock, empServiceMock);
        EmployeeSkillAssignMenu.show(outMock, inMock, empServiceMock, skillServiceMock);
        verify(outMock, inMock, skillServiceMock, empServiceMock);
    }

    @Test
    void testServiceErrorOnAssign() {
        int empId = 1;
        Employee emp = createTestEmployee(empId);

        expect(inMock.readInt("Invalid employee ID.")).andReturn(empId);
        expect(empServiceMock.findEmployeeByID(empId)).andReturn(Optional.of(emp));
        expect(skillServiceMock.findAllSkills()).andReturn(Collections.emptyList());

        outMock.skillTable(Collections.emptyList());
        expectLastCall().once();

        expect(inMock.readInt("Invalid skill ID.")).andReturn(10);
        expect(inMock.readInt("Invalid level.")).andReturn(5);
        expect(inMock.readLine()).andReturn("2026-01-01");

        skillServiceMock.assignSkill(anyInt(), anyInt(), anyInt(), anyString());
        expectLastCall().andThrow(new RuntimeException("Assignment failed"));

        outMock.error("Assignment failed");
        expectLastCall().once();

        replay(outMock, inMock, skillServiceMock, empServiceMock);
        EmployeeSkillAssignMenu.show(outMock, inMock, empServiceMock, skillServiceMock);
        verify(outMock, inMock, skillServiceMock, empServiceMock);
    }
}