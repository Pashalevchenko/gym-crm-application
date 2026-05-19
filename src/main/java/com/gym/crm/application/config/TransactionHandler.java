package com.gym.crm.application.config;

import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Component;
import java.util.function.Consumer;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class TransactionHandler {

    private final SessionFactory sessionFactory;

    public void performWithinTransaction(Consumer<Session> action) {
        Session session = sessionFactory.getCurrentSession();

        if (session.getTransaction().isActive()) {
            action.accept(session);

            return;
        }

        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            action.accept(session);
            transaction.commit();
        } catch (RuntimeException exception) {
            rollback(transaction);
            throw exception;
        }
    }

    public <T> T performReturningWithinTransaction(Function<Session, T> action) {
        Session session = sessionFactory.getCurrentSession();

        if (session.getTransaction().isActive()) {
            return action.apply(session);
        }

        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            T result = action.apply(session);
            transaction.commit();

            return result;
        } catch (RuntimeException exception) {
            rollback(transaction);
            throw exception;
        }
    }

    private void rollback(Transaction transaction) {
        if (transaction != null && transaction.isActive()) {
            transaction.rollback();
        }
    }
}