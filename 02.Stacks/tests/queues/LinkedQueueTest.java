package queues;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LinkedQueueTest {

    @Test
    void testEnqueueDequeue() {
        Queue<String> q = new LinkedQueue<>();
        q.enqueue("one");
        q.enqueue("two");
        q.enqueue("three");
        assertEquals("one", q.dequeue());
        assertEquals("two", q.dequeue());
        assertEquals("three", q.dequeue());
    }

    @Test
    void testPick() {
        Queue<String> q = new LinkedQueue<>();
        q.enqueue("one");
        q.enqueue("two");
        q.enqueue("three");
        assertEquals("one", q.pick()); q.dequeue();
        assertEquals("two", q.pick()); q.dequeue();
        assertEquals("three", q.pick()); q.dequeue();
    }

    @Test
    void testIsEmpty() {
        Queue<String> q = new LinkedQueue<>();
        assertTrue(q.isEmpty(), "Initial queue is not empty");
        q.enqueue("one");
        q.enqueue("two");
        q.enqueue("three");
        q.dequeue();
        q.dequeue();
        assertFalse(q.isEmpty(), "Non-empty queue is empty!");
        q.dequeue();
        assertTrue(q.isEmpty(), "Emptied queue is not empty");
    }

    @Test
    void testUnderflow() {
        Queue<String> q = new LinkedQueue<>();
        try {
            q.pick();
            fail("Empty queue pick");
        } catch (Queue.UnderflowException e) {}
        q.enqueue("one");
        q.enqueue("two");
        q.enqueue("three");
        for (int i = 0; i < 3; i++) {
            try {
                q.dequeue();
            } catch (Queue.UnderflowException e) {
                fail("underflow on non-empty");
            }
        }
        try {
            q.dequeue();
            fail("Dequeue queue pick");
        } catch (Queue.UnderflowException e) {}
    }
}