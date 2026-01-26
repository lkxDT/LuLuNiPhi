package de.htwsaar.communication.skill;

import de.htwsaar.application.SkillService;
import de.htwsaar.communication.InputReader;
import de.htwsaar.communication.OutputRenderer;
import de.htwsaar.domain.Skill;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Optional;
import java.util.List;

import static org.easymock.EasyMock.*;

class SkillSearchMenuTest {

    private OutputRenderer outMock;
    private SkillService serviceMock;
    private InputReader inMock;

    @BeforeEach
    void setUp() {
        outMock = createNiceMock(OutputRenderer.class);
        serviceMock = createMock(SkillService.class);
        inMock = createMock(InputReader.class);
    }

    @Test
    void testSearchByIdNotFound() {
        expect(inMock.readMenuNumber(3)).andReturn(1);
        expect(inMock.readInt(anyString())).andReturn(999);
        expect(serviceMock.findSkillByID(999)).andReturn(Optional.empty());

        outMock.error("Skill not found.");
        expectLastCall().once();

        replay(outMock, serviceMock, inMock);
        SkillSearchMenu.show(outMock, inMock, serviceMock);
        verify(outMock, serviceMock, inMock);
    }

    @Test
    void testSearchByIdSuccess() {
        expect(inMock.readMenuNumber(3)).andReturn(1);
        expect(inMock.readInt(anyString())).andReturn(1);

        Skill foundSkill = new Skill(1, "Java", "Programming");
        expect(serviceMock.findSkillByID(1)).andReturn(Optional.of(foundSkill));

        outMock.skillTable(List.of(foundSkill));
        expectLastCall().once();

        replay(outMock, serviceMock, inMock);
        SkillSearchMenu.show(outMock, inMock, serviceMock);
        verify(outMock, serviceMock, inMock);
    }

    @Test
    void testExitMenuImmediately() {
        expect(inMock.readMenuNumber(3)).andReturn(3);

        replay(outMock, serviceMock, inMock);
        SkillSearchMenu.show(outMock, inMock, serviceMock);
        verify(outMock, serviceMock, inMock);
    }

    @Test
    void testSearchByNameSuccess() {
        expect(inMock.readMenuNumber(3)).andReturn(2);
        expect(inMock.readLine()).andReturn("Java");

        Skill s1 = new Skill(1, "Java Basic", "Intro");
        Skill s2 = new Skill(2, "Java Advanced", "Expert");
        List<Skill> results = List.of(s1, s2);

        expect(serviceMock.findSkillsByName("Java")).andReturn(results);

        outMock.skillTable(results);
        expectLastCall().once();

        replay(outMock, serviceMock, inMock);
        SkillSearchMenu.show(outMock, inMock, serviceMock);
        verify(outMock, serviceMock, inMock);
    }

    @Test
    void testSearchByNameEmptyListHandover() {
        expect(inMock.readMenuNumber(3)).andReturn(2);
        expect(inMock.readLine()).andReturn("Unknown");

        List<Skill> emptyList = Collections.emptyList();
        expect(serviceMock.findSkillsByName("Unknown")).andReturn(emptyList);

        outMock.skillTable(emptyList);
        expectLastCall().once();

        replay(outMock, serviceMock, inMock);
        SkillSearchMenu.show(outMock, inMock, serviceMock);
        verify(outMock, serviceMock, inMock);
    }

    @Test
    void testServiceThrowsException() {
        expect(inMock.readMenuNumber(3)).andReturn(1);
        expect(inMock.readInt(anyString())).andReturn(1);
        expect(serviceMock.findSkillByID(1)).andThrow(new RuntimeException("Database offline"));

        outMock.error(contains("Error"));
        expectLastCall().anyTimes();

        replay(outMock, serviceMock, inMock);
        try {
            SkillSearchMenu.show(outMock, inMock, serviceMock);
        } catch (Exception e) {
        }
        verify(outMock, serviceMock, inMock);
    }
}
