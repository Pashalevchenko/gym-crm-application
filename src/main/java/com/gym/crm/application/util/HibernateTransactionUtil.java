package com.gym.crm.application.util;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.function.Consumer;
import java.util.function.Function;

public class HibernateTransactionUtil {

    private final SessionFactory sessionFactory;

    public HibernateTransactionUtil(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void performWithinTransaction(Consumer<Session> action) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            try {
                action.accept(session);
                transaction.commit();
            } catch (RuntimeException exception) {
                rollback(transaction);
                throw exception;
            }
        }
    }

    public <T> T performReturningWithinTransaction(Function<Session, T> action) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            try {
                T result = action.apply(session);
                transaction.commit();

                return result;
            } catch (RuntimeException exception) {
                rollback(transaction);
                throw exception;
            }
        }
    }

    private void rollback(Transaction transaction) {
        if (transaction != null && transaction.isActive()) {
            transaction.rollback();
        }
    }
}