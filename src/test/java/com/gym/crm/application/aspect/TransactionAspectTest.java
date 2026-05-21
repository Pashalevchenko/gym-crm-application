package com.gym.crm.application.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TransactionAspectTest {

    private final SessionFactory sessionFactory = mock(SessionFactory.class);
    private final Session session = mock(Session.class);
    private final Transaction transaction = mock(Transaction.class);
    private final ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);

    private final TransactionAspect transactionAspect = new TransactionAspect(sessionFactory);

    @Test
    @DisplayName("Should proceed without starting new transaction when transaction is already active")
    void handleTransaction_whenTransactionAlreadyActive() throws Throwable {
        Object expected = new Object();

        when(sessionFactory.getCurrentSession()).thenReturn(session);
        when(session.getTransaction()).thenReturn(transaction);
        when(transaction.isActive()).thenReturn(true);
        when(joinPoint.proceed()).thenReturn(expected);

        Object actual = transactionAspect.handleTransaction(joinPoint);

        assertSame(expected, actual);
        verify(joinPoint).proceed();
        verify(session, never()).beginTransaction();
        verify(transaction, never()).commit();
        verify(transaction, never()).rollback();
    }

    @Test
    @DisplayName("Should begin and commit transaction when method succeeds")
    void handleTransaction_whenMethodSucceeds() throws Throwable {
        Object expected = new Object();

        when(sessionFactory.getCurrentSession()).thenReturn(session);
        when(session.getTransaction()).thenReturn(transaction);
        when(transaction.isActive()).thenReturn(false);
        when(session.beginTransaction()).thenReturn(transaction);
        when(joinPoint.proceed()).thenReturn(expected);

        Object actual = transactionAspect.handleTransaction(joinPoint);

        assertSame(expected, actual);
        verify(session).beginTransaction();
        verify(joinPoint).proceed();
        verify(transaction).commit();
        verify(transaction, never()).rollback();
    }

    @Test
    @DisplayName("Should rollback transaction when method throws exception")
    void handleTransaction_whenMethodThrowsException_shouldRollbackTransaction() throws Throwable {
        RuntimeException expectedException = new RuntimeException("Something went wrong");

        when(sessionFactory.getCurrentSession()).thenReturn(session);
        when(session.getTransaction()).thenReturn(transaction);
        when(transaction.isActive()).thenReturn(false);
        when(session.beginTransaction()).thenReturn(transaction);
        when(joinPoint.proceed()).thenThrow(expectedException);
        when(transaction.isActive()).thenReturn(false, true);

        RuntimeException actualException = assertThrows(RuntimeException.class, () -> transactionAspect.handleTransaction(joinPoint));

        assertEquals(expectedException, actualException);
        verify(session).beginTransaction();
        verify(joinPoint).proceed();
        verify(transaction).rollback();
        verify(transaction, never()).commit();
    }

    @Test
    @DisplayName("Should not rollback when transaction is not active after exception")
    void handleTransaction_whenMethodThrowsAndTransactionIsNotActive_shouldNotRollback() throws Throwable {
        RuntimeException expectedException = new RuntimeException("Something went wrong");

        when(sessionFactory.getCurrentSession()).thenReturn(session);
        when(session.getTransaction()).thenReturn(transaction);
        when(transaction.isActive()).thenReturn(false);
        when(session.beginTransaction()).thenReturn(transaction);
        when(joinPoint.proceed()).thenThrow(expectedException);
        when(transaction.isActive()).thenReturn(false, false);

        RuntimeException actualException = assertThrows(RuntimeException.class, () -> transactionAspect.handleTransaction(joinPoint));

        assertEquals(expectedException, actualException);
        verify(session).beginTransaction();
        verify(joinPoint).proceed();
        verify(transaction, never()).rollback();
        verify(transaction, never()).commit();
    }
}