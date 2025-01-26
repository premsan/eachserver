package com.eachserver.db;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ReferenceTest {

    @Test
    public void testReferenceConstructor() {

        final Reference validReference =
                new Reference("demo.eachserver.com::Payment::b5a71dae-41c8-4166-a606-fcf4ed6b15d9");

        Assertions.assertEquals("demo.eachserver.com", validReference.getHost());
        Assertions.assertEquals("Payment", validReference.getType());
        Assertions.assertEquals("b5a71dae-41c8-4166-a606-fcf4ed6b15d9", validReference.getId());

        Assertions.assertThrows(RuntimeException.class, () -> new Reference("BAD REFERENCE"));
    }
}
