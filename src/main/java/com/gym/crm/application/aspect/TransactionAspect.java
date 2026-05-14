package com.gym.crm.application.aspect;

import com.gym.crm.application.aspect.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class TransactionAspect {

    private final SessionFactory sessionFactory;

    @Around("@annotation(com.gym.crm.application.aspect.annotation.Transactional)")
    public Object handleTransaction(ProceedingJoinPoint joinPoint) throws Throwable {
        Session session = sessionFactory.getCurrentSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            Object result = joinPoint.proceed();

            transaction.commit();
            return result;
        } catch (Throwable e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
    }
}