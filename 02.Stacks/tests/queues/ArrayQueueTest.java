package queues;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

class ArrayQueueTest {

    @Test
    void testEnqueueDequeue() {
        Queue<String> q = new ArrayQueue<>(10);
        q.enqueue("one");
        q.enqueue("two");
        q.enqueue("three");
        assertEquals("one", q.dequeue());
        assertEquals("two", q.dequeue());
        assertEquals("three", q.dequeue());
    }

    @Test
    void testPick() {
        Queue<String> q = new ArrayQueue<>(10);
        q.enqueue("one");
        q.enqueue("two");
        q.enqueue("three");
        assertEquals("one", q.pick()); q.dequeue();
        assertEquals("two", q.pick()); q.dequeue();
        assertEquals("three", q.pick()); q.dequeue();
    }

    @Test
    void testIsEmpty() {
        Queue<String> q = new ArrayQueue<>(10);
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
        Queue<String> q = new ArrayQueue<>(10);
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

    @Test
    void testOverflow() {
        Queue<String> q = new ArrayQueue<>(10);
        for (int i = 0; i < 10; i++) {
            try {
                q.enqueue("Str " + (i+1));
            } catch (Queue.OverflowException e) {
                fail("overflow on non-full");
            }
        }
        try {
            q.enqueue("Str 11");
            fail("Full queue enqueued");
        } catch (Queue.OverflowException e) {}
    }
}