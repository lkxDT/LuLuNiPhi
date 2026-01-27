package de.htwsaar.communication.assignment;

import de.htwsaar.application.SkillService;
import de.htwsaar.communication.InputReader;
import de.htwsaar.communication.OutputRenderer;
import de.htwsaar.domain.AssignedSkill;
import de.htwsaar.MuteConsoleExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Collections;
import java.util.List;

import static org.easymock.EasyMock.*;

@ExtendWith(MuteConsoleExtension.class)
class EmployeeSkillUpdateMenuTest {

    private OutputRenderer outMock;
    private InputReader inMock;
    private SkillService serviceMock;

    @BeforeEach
    void setUp() {
        outMock = createNiceMock(OutputRenderer.class);
        inMock = createMock(InputReader.class);
        serviceMock = createMock(SkillService.class);
    }

    /**
     * Creates a dummy AssignedSkill for Tests
     * @param id Skill ID
     * @return   an AssignedSkill object
     */
    private AssignedSkill createTestSkill(int id) {
        return new AssignedSkill(id, "Java", "IT", 4, "2026-01-01");
    }

    @Test
    void testUpdateSkillSuccess() {
        int empId = 1;
        int skillId = 10;
        AssignedSkill skill = createTestSkill(skillId);

        expect(inMock.readInt("Invalid ID.")).andReturn(empId);
        expect(serviceMock.findSkillsOfEmployee(empId)).andReturn(List.of(skill));

        outMock.assignedSkillTable(List.of(skill));
        expectLastCall().once();

        expect(inMock.readInt("Invalid skill ID.")).andReturn(skillId);
        expect(inMock.readLine()).andReturn("5");
        expect(inMock.readLine()).andReturn("2026-01-01");

        serviceMock.updateSkillAssignment(empId, skillId, 5, "2026-01-01");
        expectLastCall().once();

        outMock.info("Skill updated.");
        expectLastCall().once();

        replay(outMock, inMock, serviceMock);
        EmployeeSkillUpdateMenu.show(outMock, inMock, serviceMock);
        verify(outMock, inMock, serviceMock);
    }

    @Test
    void testUpdateSkillKeepCurrentValues() {
        int empId = 1;
        int skillId = 10;
        AssignedSkill skill = createTestSkill(skillId);

        expect(inMock.readInt("Invalid ID.")).andReturn(empId);
        expect(serviceMock.findSkillsOfEmployee(empId)).andReturn(List.of(skill));

        expect(inMock.readInt("Invalid skill ID.")).andReturn(skillId);
        expect(inMock.readLine()).andReturn("").times(2);

        serviceMock.updateSkillAssignment(empId, skillId, 4, "2026-01-01");
        expectLastCall().once();

        outMock.info("Skill updated.");
        expectLastCall().once();

        replay(outMock, inMock, serviceMock);
        EmployeeSkillUpdateMenu.show(outMock, inMock, serviceMock);
        verify(outMock, inMock, serviceMock);
    }

    @Test
    void testNoSkillsAssigned() {
        int empId = 1;
        expect(inMock.readInt("Invalid ID.")).andReturn(empId);
        expect(serviceMock.findSkillsOfEmployee(empId)).andReturn(Collections.emptyList());

        outMock.info("No skills assigned.");
        expectLastCall().once();

        replay(outMock, inMock, serviceMock);
        EmployeeSkillUpdateMenu.show(outMock, inMock, serviceMock);
        verify(outMock, inMock, serviceMock);
    }

    @Test
    void testSkillNotAssignedToEmployee() {
        int empId = 1;
        int skillId = 10;
        AssignedSkill skill = createTestSkill(20);

        expect(inMock.readInt("Invalid ID.")).andReturn(empId);
        expect(serviceMock.findSkillsOfEmployee(empId)).andReturn(List.of(skill));
        expect(inMock.readInt("Invalid skill ID.")).andReturn(skillId);

        outMock.error("Skill not assigned.");
        expectLastCall().once();

        replay(outMock, inMock, serviceMock);
        EmployeeSkillUpdateMenu.show(outMock, inMock, serviceMock);
        verify(outMock, inMock, serviceMock);
    }

    @Test
    void testInvalidEmployeeID() {
        expect(inMock.readInt("Invalid ID.")).andThrow(new IllegalArgumentException("Invalid ID."));

        outMock.error("Invalid ID.");
        expectLastCall().once();

        replay(outMock, inMock, serviceMock);
        EmployeeSkillUpdateMenu.show(outMock, inMock, serviceMock);
        verify(outMock, inMock, serviceMock);
    }

    @Test
    void testServiceErrorOnUpdate() {
        int empId = 1;
        int skillId = 10;
        AssignedSkill skill = createTestSkill(skillId);

        expect(inMock.readInt("Invalid ID.")).andReturn(empId);
        expect(serviceMock.findSkillsOfEmployee(empId)).andReturn(List.of(skill));
        expect(inMock.readInt("Invalid skill ID.")).andReturn(skillId);
        expect(inMock.readLine()).andReturn("").times(2);

        serviceMock.updateSkillAssignment(anyInt(), anyInt(), anyInt(), anyString());
        expectLastCall().andThrow(new RuntimeException("Database error"));

        outMock.error("Database error");
        expectLastCall().once();

        replay(outMock, inMock, serviceMock);
        EmployeeSkillUpdateMenu.show(outMock, inMock, serviceMock);
        verify(outMock, inMock, serviceMock);
    }
}
