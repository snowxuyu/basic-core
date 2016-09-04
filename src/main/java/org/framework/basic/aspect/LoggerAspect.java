package org.framework.basic.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: snowxuyu
 * Date: 2016/9/4
 * Time: 14:03
 * 日志切面
 */
@Component
@Aspect
public class LoggerAspect {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Pointcut
     * 定义Pointcut，Pointcut的名称为aspectjMethod()，此方法没有返回值和参数
     * 该方法就是一个标识，不进行调用
     * *  表示任意返回值， ..  表示任意方法  任意参数
     */
    @Pointcut("execution(* org.snow..web*..*(..)) || execution(* org.snow..service*..*(..)) || execution(* org.snow..dao*..*(..))")
    private void doLogger(){
        //切入点 空实现
    }

    /**
     * Before
     * 在核心业务执行前执行，不能阻止核心业务的调用。
     *
     * @param joinPoint
     */
    @Before("doLogger()")
    public void beforeAdvice(JoinPoint joinPoint) {
        logger.debug("-----beforeAdvice().invoke-----");
        Object targetObject = joinPoint.getTarget();
        Signature signature = joinPoint.getSignature();
        String signatureName = signature.getName();
        Object[] args = joinPoint.getArgs();
        logger.debug("before exec class:{},method:{},args:{}", targetObject, signatureName, args);
        logger.debug("-----End of beforeAdvice()------");

    }

    /**
     * After
     * 核心业务逻辑退出后（包括正常执行结束和异常退出），执行此Advice
     *
     * @param joinPoint
     */
    @After(value = "doLogger()")
    public void afterAdvice(JoinPoint joinPoint) {
        logger.debug("-----afterAdvice().invoke-----");
        Object targetObject = joinPoint.getTarget();
        Signature signature = joinPoint.getSignature();
        String signatureName = signature.getName();
        logger.debug("after exec class:{},method:{}", targetObject, signatureName);
        logger.debug("-----End of afterAdvice()------");
    }

    /**
     * Around
     * 手动控制调用核心业务逻辑，以及调用前和调用后的处理,
     * <p/>
     * 注意：当核心业务抛异常后，立即退出，转向AfterAdvice
     * 执行完AfterAdvice，再转到ThrowingAdvice
     *
     * @param pjp
     * @return
     * @throws Throwable
     */
    @Around(value = "doLogger()")
    public Object aroundAdvice(ProceedingJoinPoint pjp) throws Throwable {
        logger.debug("-----aroundAdvice().invoke-----");
        Object targetObject = pjp.getTarget();
        Signature signature = pjp.getSignature();
        String signatureName = signature.getName();
        Object[] args = pjp.getArgs();
        logger.debug("aroundAdvice exec class:{},method:{},args:{}", targetObject, signatureName, args);
        Object retVal = pjp.proceed();
        logger.debug("aroundAdvice exec class:{},method:{},args:{},retVal:{}", targetObject, signatureName, args,retVal);
        logger.debug("-----End of aroundAdvice()------");
        return retVal;
    }

    /**
     * AfterReturning
     * 核心业务逻辑调用正常退出后，不管是否有返回值，正常退出后，均执行此Advice
     * @param joinPoint
     */
    @AfterReturning(value = "doLogger()", returning = "retVal")
    public void afterReturningAdvice(JoinPoint joinPoint, String retVal) {
        logger.debug("-----afterReturningAdvice().invoke-----");
        Object targetObject = joinPoint.getTarget();
        Signature signature = joinPoint.getSignature();
        String signatureName = signature.getName();
        Object[] args = joinPoint.getArgs();
        logger.debug("afterReturning exec class:{},method:{},args:{},retVal:{}", targetObject, signatureName,args,retVal);
        logger.debug("-----End of afterReturningAdvice()------");
    }

    /**
     * 核心业务逻辑调用异常退出后，执行此Advice，处理错误信息
     * <p/>
     * 注意：执行顺序在Around Advice之后
     *
     * @param joinPoint
     * @param ex
     */
    @AfterThrowing(value = "doLogger()", throwing = "ex")
    public void afterThrowingAdvice(JoinPoint joinPoint, Exception ex) {
        logger.debug("-----afterThrowingAdvice().invoke-----");
        Object targetObject = joinPoint.getTarget();
        Signature signature = joinPoint.getSignature();
        String signatureName = signature.getName();
        logger.debug("afterThrowing exec class:{},method:{}", targetObject, signatureName);
        logger.error("afterThrowing error msg:" + ex.getMessage(), ex);

        logger.debug("-----End of afterThrowingAdvice()------");
    }

}
