package de.htwsaar.communication.skill;

import de.htwsaar.application.SkillService;
import de.htwsaar.communication.InputReader;
import de.htwsaar.communication.OutputRenderer;
import de.htwsaar.domain.Skill;
import de.htwsaar.MuteConsoleExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.easymock.EasyMock.*;

@ExtendWith(MuteConsoleExtension.class)
class SkillAddMenuTest {

    private OutputRenderer outMock;
    private InputReader inMock;
    private SkillService serviceMock;

    @BeforeEach
    void setUp() {
        outMock = createNiceMock(OutputRenderer.class);
        inMock = createMock(InputReader.class);
        serviceMock = createMock(SkillService.class);
    }

    @ParameterizedTest
    @CsvSource({
            "Java, Programming",
            " SQL , Database ",
            "C++, ''"
    })
    void testCreateSkillParameterized(String name, String category) {
        Skill createdSkill = new Skill(1, name, category);

        expect(inMock.readLine()).andReturn(name);
        expect(inMock.readLine()).andReturn(category);

        expect(serviceMock.createSkill(name, category)).andReturn(createdSkill);

        outMock.info("Skill created.");
        outMock.skillTable(List.of(createdSkill));

        replay(outMock, inMock, serviceMock);
        SkillAddMenu.show(outMock, inMock, serviceMock);
        verify(outMock, inMock, serviceMock);
    }

    @Test
    void testCreateSkillServiceError() {
        expect(inMock.readLine()).andReturn("Invalid");
        expect(inMock.readLine()).andReturn("Skill");

        expect(serviceMock.createSkill("Invalid", "Skill"))
                .andThrow(new RuntimeException("Name already exists"));

        outMock.error("Name already exists");
        expectLastCall().once();

        replay(outMock, inMock, serviceMock);
        SkillAddMenu.show(outMock, inMock, serviceMock);
        verify(outMock, inMock, serviceMock);
    }
}