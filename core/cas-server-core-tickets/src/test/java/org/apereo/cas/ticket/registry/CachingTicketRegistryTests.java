package org.apereo.cas.ticket.registry;

import org.apereo.cas.logout.LogoutManager;
import org.apereo.cas.util.crypto.CipherExecutor;

import org.junit.jupiter.api.RepeatedTest;

import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test case to test the {@code CachingTicketRegistry} based on test cases to test all
 * Ticket Registries.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
public class CachingTicketRegistryTests extends BaseTicketRegistryTests {

    @Override
    public TicketRegistry getNewTicketRegistry() {
        return new CachingTicketRegistry(mock(LogoutManager.class));
    }

    @RepeatedTest(1)
    public void verifyOtherConstructor() {
        assertNotNull(new DefaultTicketRegistry(new ConcurrentHashMap<>(10, 10, 5), CipherExecutor.noOp()));
    }
}
