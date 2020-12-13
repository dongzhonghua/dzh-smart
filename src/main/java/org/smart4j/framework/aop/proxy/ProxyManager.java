package org.smart4j.framework.aop.proxy;

import java.lang.reflect.Method;
import java.util.List;

import org.smart4j.framework.util.JsonUtil;

import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

/**
 * 代理管理器
 *
 * @author huangyong
 * @since 2.0
 */
@Slf4j
public class ProxyManager {

    @SuppressWarnings("unchecked")
    public static <T> T createProxy(final Class<?> targetClass, final List<Proxy> proxyList) {
        MethodInterceptor callback = new MethodInterceptor() {
            @Override
            public Object intercept(Object targetObject, Method targetMethod, Object[] methodParams,
                    MethodProxy methodProxy) throws Throwable {

                ProxyChain proxyChain =
                        new ProxyChain(targetClass, targetObject, targetMethod, methodProxy, methodParams, proxyList);
                log.info("调用代理方法" + targetClass.getName() + "#" + targetMethod.getName()
                        + JsonUtil.toJSON(methodParams)); // 这里为什么在执行方法的时候才会运行到这一步呢，不应该在初始化的时候就已经是这样的吗？
                return proxyChain.doProxyChain();

            }
        };
        return (T) Enhancer.create(targetClass, callback);
    }
}
