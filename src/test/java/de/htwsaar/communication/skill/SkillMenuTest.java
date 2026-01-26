package de.htwsaar.communication.skill;

import de.htwsaar.application.SkillService;
import de.htwsaar.communication.InputReader;
import de.htwsaar.communication.OutputRenderer;
import de.htwsaar.MuteConsoleExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Collections;
import java.util.Optional;

import static org.easymock.EasyMock.*;

@ExtendWith(MuteConsoleExtension.class)
class SkillMenuTest {

    private SkillService serviceMock;
    private InputReader inMock;
    private OutputRenderer outMock;
    private SkillMenu skillMenu;

    @BeforeEach
    void setUp() {
        serviceMock = createNiceMock(SkillService.class);
        inMock = createNiceMock(InputReader.class);
        outMock = createNiceMock(OutputRenderer.class);
        skillMenu = new SkillMenu(serviceMock, inMock, outMock);
    }

    @Test
    void testShowAllSkills() {
        expect(inMock.readMenuNumber(6)).andReturn(1);
        expect(serviceMock.findAllSkills()).andReturn(Collections.emptyList());

        outMock.skillTable(anyObject());
        expectLastCall().once();

        expect(inMock.readMenuNumber(6)).andReturn(6);

        replay(serviceMock, inMock, outMock);
        skillMenu.show();
        verify(serviceMock, inMock, outMock);
    }

    @Test
    void testNavigateToSearchMenu() {
        expect(inMock.readMenuNumber(6)).andReturn(2);
        expect(inMock.readMenuNumber(3)).andReturn(3);
        expect(inMock.readMenuNumber(6)).andReturn(6);

        replay(inMock, serviceMock);
        skillMenu.show();
        verify(inMock);
    }

    @Test
    void testNavigateToAddMenu() {
        expect(inMock.readMenuNumber(6)).andReturn(3);
        expect(inMock.readLine()).andReturn("");
        expect(inMock.readMenuNumber(6)).andReturn(6);

        replay(inMock, serviceMock);
        skillMenu.show();
        verify(inMock);
    }

    @Test
    void testNavigateToChangeMenu() {
        expect(inMock.readMenuNumber(6)).andReturn(4);
        expect(inMock.readInt(anyString())).andReturn(0);
        expect(serviceMock.findSkillByID(0)).andReturn(Optional.empty());
        expect(inMock.readMenuNumber(6)).andReturn(6);

        replay(inMock, serviceMock);
        skillMenu.show();
        verify(inMock);
    }

    @Test
    void testNavigateToDeleteMenu() {
        expect(inMock.readMenuNumber(6)).andReturn(5);
        expect(inMock.readInt(anyString())).andReturn(0);
        expect(serviceMock.findSkillByID(0)).andReturn(Optional.empty());
        expect(inMock.readMenuNumber(6)).andReturn(6);

        replay(inMock, serviceMock);
        skillMenu.show();
        verify(inMock);
    }

    @Test
    void testReturnImmediately() {
        expect(inMock.readMenuNumber(6)).andReturn(6);

        replay(inMock, serviceMock);
        skillMenu.show();
        verify(inMock);
    }
}