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
class EmployeeSkillRemoveMenuTest {

    private OutputRenderer outMock;
    private InputReader inMock;
    private SkillService serviceMock;

    @BeforeEach
    void setUp() {
        outMock = createNiceMock(OutputRenderer.class);
        inMock = createMock(InputReader.class);
        serviceMock = createMock(SkillService.class);
    }

    private AssignedSkill createTestSkill(int id) {
        return new AssignedSkill(id, "Java", "IT", 5, "2026-01-01");
    }

    @Test
    void testRemoveSkillSuccess() {
        int empId = 1;
        int skillId = 10;
        AssignedSkill skill = createTestSkill(skillId);

        expect(inMock.readInt("Invalid ID.")).andReturn(empId);
        expect(serviceMock.findSkillsOfEmployee(empId)).andReturn(List.of(skill));

        outMock.assignedSkillTable(List.of(skill));
        expectLastCall().once();

        expect(inMock.readInt("Invalid skill ID.")).andReturn(skillId);
        expect(inMock.readLine()).andReturn("y");

        serviceMock.removeSkillFromEmployee(empId, skillId);
        expectLastCall().once();

        outMock.info("Skill removed from employee.");
        expectLastCall().once();

        replay(outMock, inMock, serviceMock);
        EmployeeSkillRemoveMenu.show(outMock, inMock, serviceMock);
        verify(outMock, inMock, serviceMock);
    }

    @Test
    void testRemoveSkillCanceled() {
        int empId = 1;
        int skillId = 10;
        AssignedSkill skill = createTestSkill(skillId);

        expect(inMock.readInt("Invalid ID.")).andReturn(empId);
        expect(serviceMock.findSkillsOfEmployee(empId)).andReturn(List.of(skill));

        outMock.assignedSkillTable(List.of(skill));
        expectLastCall().once();

        expect(inMock.readInt("Invalid skill ID.")).andReturn(skillId);
        expect(inMock.readLine()).andReturn("n");

        outMock.info("Removal canceled.");
        expectLastCall().once();

        replay(outMock, inMock, serviceMock);
        EmployeeSkillRemoveMenu.show(outMock, inMock, serviceMock);
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
        EmployeeSkillRemoveMenu.show(outMock, inMock, serviceMock);
        verify(outMock, inMock, serviceMock);
    }

    @Test
    void testSkillNotAssignedToEmployee() {
        int empId = 1;
        int skillId = 99;
        AssignedSkill skill = createTestSkill(10);

        expect(inMock.readInt("Invalid ID.")).andReturn(empId);
        expect(serviceMock.findSkillsOfEmployee(empId)).andReturn(List.of(skill));

        outMock.assignedSkillTable(List.of(skill));
        expectLastCall().once();

        expect(inMock.readInt("Invalid skill ID.")).andReturn(skillId);

        outMock.error("Skill not assigned.");
        expectLastCall().once();

        replay(outMock, inMock, serviceMock);
        EmployeeSkillRemoveMenu.show(outMock, inMock, serviceMock);
        verify(outMock, inMock, serviceMock);
    }

    @Test
    void testInvalidEmployeeIdFormat() {
        expect(inMock.readInt("Invalid ID.")).andThrow(new IllegalArgumentException("Invalid ID."));

        outMock.error("Invalid ID.");
        expectLastCall().once();

        replay(outMock, inMock, serviceMock);
        EmployeeSkillRemoveMenu.show(outMock, inMock, serviceMock);
        verify(outMock, inMock, serviceMock);
    }

    @Test
    void testServiceErrorOnRemoval() {
        int empId = 1;
        int skillId = 10;
        AssignedSkill skill = createTestSkill(skillId);

        expect(inMock.readInt("Invalid ID.")).andReturn(empId);
        expect(serviceMock.findSkillsOfEmployee(empId)).andReturn(List.of(skill));

        outMock.assignedSkillTable(List.of(skill));
        expectLastCall().once();

        expect(inMock.readInt("Invalid skill ID.")).andReturn(skillId);
        expect(inMock.readLine()).andReturn("y");

        serviceMock.removeSkillFromEmployee(empId, skillId);
        expectLastCall().andThrow(new RuntimeException("Database error"));

        outMock.error("Database error");
        expectLastCall().once();

        replay(outMock, inMock, serviceMock);
        EmployeeSkillRemoveMenu.show(outMock, inMock, serviceMock);
        verify(outMock, inMock, serviceMock);
    }
}
