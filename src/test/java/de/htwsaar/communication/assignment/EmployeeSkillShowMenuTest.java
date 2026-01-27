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
class EmployeeSkillShowMenuTest {

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
        return new AssignedSkill(id, "Java", "IT", 5, "2026-01-01");
    }

    @Test
    void testShowSkillsSuccess() {
        int empId = 1;
        List<AssignedSkill> skills = List.of(createTestSkill(10));

        expect(inMock.readInt("Invalid ID.")).andReturn(empId);
        expect(serviceMock.findSkillsOfEmployee(empId)).andReturn(skills);

        outMock.assignedSkillTable(skills);
        expectLastCall().once();

        replay(outMock, inMock, serviceMock);
        EmployeeSkillShowMenu.show(outMock, inMock, serviceMock);
        verify(outMock, inMock, serviceMock);
    }

    @Test
    void testShowEmptySkills() {
        int empId = 2;
        expect(inMock.readInt("Invalid ID.")).andReturn(empId);
        expect(serviceMock.findSkillsOfEmployee(empId)).andReturn(Collections.emptyList());

        outMock.assignedSkillTable(Collections.emptyList());
        expectLastCall().once();

        replay(outMock, inMock, serviceMock);
        EmployeeSkillShowMenu.show(outMock, inMock, serviceMock);
        verify(outMock, inMock, serviceMock);
    }

    @Test
    void testInvalidEmployeeID() {
        expect(inMock.readInt("Invalid ID.")).andThrow(new IllegalArgumentException("Invalid ID."));

        outMock.error("Invalid ID.");
        expectLastCall().once();

        replay(outMock, inMock, serviceMock);
        EmployeeSkillShowMenu.show(outMock, inMock, serviceMock);
        verify(outMock, inMock, serviceMock);
    }
}
