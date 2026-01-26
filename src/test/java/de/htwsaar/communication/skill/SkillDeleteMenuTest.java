package de.htwsaar.communication.skill;

import de.htwsaar.application.SkillService;
import de.htwsaar.communication.InputReader;
import de.htwsaar.communication.OutputRenderer;
import de.htwsaar.domain.Skill;
import de.htwsaar.MuteConsoleExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Optional;
import java.util.List;

import static org.easymock.EasyMock.*;

@ExtendWith(MuteConsoleExtension.class)
class SkillDeleteMenuTest {

    private OutputRenderer outMock;
    private InputReader inMock;
    private SkillService serviceMock;

    @BeforeEach
    void setUp() {
        outMock = createNiceMock(OutputRenderer.class);
        inMock = createMock(InputReader.class);
        serviceMock = createMock(SkillService.class);
    }

    @Test
    void testDeleteSuccess() {
        int id = 1;
        Skill skill = new Skill(id, "Java", "Programming");

        expect(inMock.readInt(anyString())).andReturn(id);
        expect(serviceMock.findSkillByID(id)).andReturn(Optional.of(skill));

        outMock.skillTable(List.of(skill));
        expectLastCall().once();

        expect(inMock.readLine()).andReturn("y");
        serviceMock.deleteSkillById(id);
        expectLastCall().once();

        outMock.info("Skill deleted.");
        expectLastCall().once();

        replay(outMock, inMock, serviceMock);
        SkillDeleteMenu.show(outMock, inMock, serviceMock);
        verify(outMock, inMock, serviceMock);
    }

    @Test
    void testDeleteCanceled() {
        int id = 1;
        Skill skill = new Skill(id, "Java", "Programming");

        expect(inMock.readInt(anyString())).andReturn(id);
        expect(serviceMock.findSkillByID(id)).andReturn(Optional.of(skill));

        expect(inMock.readLine()).andReturn("n");

        outMock.info("Delete canceled.");
        expectLastCall().once();

        replay(outMock, inMock, serviceMock);
        SkillDeleteMenu.show(outMock, inMock, serviceMock);
        verify(outMock, inMock, serviceMock);
    }

    @Test
    void testSkillNotFound() {
        int id = 99;

        expect(inMock.readInt(anyString())).andReturn(id);
        expect(serviceMock.findSkillByID(id)).andReturn(Optional.empty());

        outMock.error("Skill with ID " + id + " not found.");
        expectLastCall().once();

        replay(outMock, inMock, serviceMock);
        SkillDeleteMenu.show(outMock, inMock, serviceMock);
        verify(outMock, inMock, serviceMock);
    }

    @Test
    void testDeleteServiceError() {
        int id = 1;
        Skill skill = new Skill(id, "Java", "Programming");

        expect(inMock.readInt(anyString())).andReturn(id);
        expect(serviceMock.findSkillByID(id)).andReturn(Optional.of(skill));
        expect(inMock.readLine()).andReturn("y");

        serviceMock.deleteSkillById(id);
        expectLastCall().andThrow(new RuntimeException("Database error"));

        outMock.error("Database error");
        expectLastCall().once();

        replay(outMock, inMock, serviceMock);
        SkillDeleteMenu.show(outMock, inMock, serviceMock);
        verify(outMock, inMock, serviceMock);
    }
}