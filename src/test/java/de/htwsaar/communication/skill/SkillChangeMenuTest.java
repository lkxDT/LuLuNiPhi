package de.htwsaar.communication.skill;

import de.htwsaar.application.SkillService;
import de.htwsaar.communication.InputReader;
import de.htwsaar.communication.OutputRenderer;
import de.htwsaar.domain.Skill;
import de.htwsaar.MuteConsoleExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Optional;

import static org.easymock.EasyMock.*;

@ExtendWith(MuteConsoleExtension.class)
class SkillChangeMenuTest {

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
    void testUpdateSuccessFull() {
        Skill existing = new Skill(1, "Java", "Dev");

        expect(inMock.readInt(anyString())).andReturn(1);
        expect(serviceMock.findSkillByID(1)).andReturn(Optional.of(existing));

        expect(inMock.readLine()).andReturn("Python");
        expect(inMock.readLine()).andReturn("Scripting");

        serviceMock.updateSkill(anyObject(Skill.class));
        expectLastCall().once();

        outMock.info("Skill updated.");
        expectLastCall().once();

        replay(outMock, inMock, serviceMock);
        SkillChangeMenu.show(outMock, inMock, serviceMock);
        verify(outMock, inMock, serviceMock);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "  ", "\t"})
    void testUpdateKeepValuesParameterized(String emptyInput) {
        Skill existing = new Skill(1, "Java", "Dev");

        expect(inMock.readInt(anyString())).andReturn(1);
        expect(serviceMock.findSkillByID(1)).andReturn(Optional.of(existing));
        expect(inMock.readLine()).andReturn(emptyInput);
        expect(inMock.readLine()).andReturn(emptyInput);

        serviceMock.updateSkill(recordMatcher(updated ->
                updated.getName().equals("Java") && updated.getCategory().equals("Dev")
        ));
        expectLastCall().once();

        outMock.info("Skill updated.");
        expectLastCall().once();

        replay(outMock, inMock, serviceMock);
        SkillChangeMenu.show(outMock, inMock, serviceMock);
        verify(outMock, inMock, serviceMock);
    }

    @Test
    void testSkillNotFound() {
        expect(inMock.readInt(anyString())).andReturn(99);
        expect(serviceMock.findSkillByID(99)).andReturn(Optional.empty());

        outMock.error("Skill not found.");
        expectLastCall().once();

        replay(outMock, inMock, serviceMock);
        SkillChangeMenu.show(outMock, inMock, serviceMock);
        verify(outMock, inMock, serviceMock);
    }

    @Test
    void testInvalidIdInput() {
        expect(inMock.readInt(anyString())).andThrow(new IllegalArgumentException("Not a number"));

        outMock.error("Not a number");
        expectLastCall().once();

        replay(outMock, inMock, serviceMock);
        SkillChangeMenu.show(outMock, inMock, serviceMock);
        verify(outMock, inMock, serviceMock);
    }

    @Test
    void testServiceErrorOnUpdate() {
        Skill existing = new Skill(1, "Java", "Dev");

        expect(inMock.readInt(anyString())).andReturn(1);
        expect(serviceMock.findSkillByID(1)).andReturn(Optional.of(existing));
        expect(inMock.readLine()).andReturn("New");
        expect(inMock.readLine()).andReturn("New");

        serviceMock.updateSkill(anyObject(Skill.class));
        expectLastCall().andThrow(new RuntimeException("Database error"));

        outMock.error("Database error");
        expectLastCall().once();

        replay(outMock, inMock, serviceMock);
        SkillChangeMenu.show(outMock, inMock, serviceMock);
        verify(outMock, inMock, serviceMock);
    }


    /** Creates a custom EasyMock argument matcher for Skill objects.
     * @param predicate the functional predicate defining the conditions the Skill object must meet
     * @return a placeholder value (always {@code null}) to be used in the mock expectation block
     */
    private static Skill recordMatcher(java.util.function.Predicate<Skill> predicate) {
        reportMatcher(new org.easymock.IArgumentMatcher() {
            @Override
            public boolean matches(Object argument) {
                return argument instanceof Skill && predicate.test((Skill) argument);
            }
            @Override
            public void appendTo(StringBuffer buffer) {
                buffer.append("skill matches predicate");
            }
        });
        return null;
    }
}
