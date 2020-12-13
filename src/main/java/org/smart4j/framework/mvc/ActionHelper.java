package org.smart4j.framework.mvc;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.smart4j.framework.core.ClassHelper;
import org.smart4j.framework.mvc.annotation.Action;
import org.smart4j.framework.mvc.annotation.Request;
import org.smart4j.framework.util.ArrayUtil;
import org.smart4j.framework.util.CollectionUtil;
import org.smart4j.framework.util.JsonUtil;
import org.smart4j.framework.util.StringUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 初始化 Action 配置
 *
 * @author huangyong
 * @since 1.0
 */
@Slf4j
public class ActionHelper {

    /**
     * Action Map（HTTP 请求与 Action 方法的映射）
     */
    private static final Map<Requestor, Handler> actionMap = new LinkedHashMap<Requestor, Handler>();

    static {
        // 获取所有 Action 类，这里是不是重复了？可以再beanhelper那里获取，这样的话是遍历了两遍的。
        List<Class<?>> actionClassList = ClassHelper.getClassListByAnnotation(Action.class);
        if (CollectionUtil.isNotEmpty(actionClassList)) {
            // 定义两个 Action Map
            Map<Requestor, Handler> commonActionMap = new HashMap<Requestor, Handler>(); // 存放普通 Action Map
            Map<Requestor, Handler> regexpActionMap = new HashMap<Requestor, Handler>(); // 存放带有正则表达式的 Action Map
            // 遍历 Action 类
            for (Class<?> actionClass : actionClassList) {
                // 获取并遍历该 Action 类中所有的方法
                Method[] actionMethods = actionClass.getDeclaredMethods();
                if (ArrayUtil.isNotEmpty(actionMethods)) {
                    for (Method actionMethod : actionMethods) {
                        // 处理 Action 方法
                        handleActionMethod(actionClass, actionMethod, commonActionMap, regexpActionMap);
                    }
                }
            }
            // 初始化最终的 Action Map（将 Common 放在 Regexp 前面）
            actionMap.putAll(commonActionMap);
            actionMap.putAll(regexpActionMap);
            log.info("Action=="+ actionMap);
        }
    }

    private static void handleActionMethod(Class<?> actionClass, Method actionMethod, Map<Requestor, Handler> commonActionMap, Map<Requestor, Handler> regexpActionMap) {
        // 判断当前 Action 方法是否带有 Request 注解
        if (actionMethod.isAnnotationPresent(Request.Get.class)) {
            String requestPath = actionMethod.getAnnotation(Request.Get.class).value();
            putActionMap("GET", requestPath, actionClass, actionMethod, commonActionMap, regexpActionMap);
        } else if (actionMethod.isAnnotationPresent(Request.Post.class)) {
            String requestPath = actionMethod.getAnnotation(Request.Post.class).value();
            putActionMap("POST", requestPath, actionClass, actionMethod, commonActionMap, regexpActionMap);
        } else if (actionMethod.isAnnotationPresent(Request.Put.class)) {
            String requestPath = actionMethod.getAnnotation(Request.Put.class).value();
            putActionMap("PUT", requestPath, actionClass, actionMethod, commonActionMap, regexpActionMap);
        } else if (actionMethod.isAnnotationPresent(Request.Delete.class)) {
            String requestPath = actionMethod.getAnnotation(Request.Delete.class).value();
            putActionMap("DELETE", requestPath, actionClass, actionMethod, commonActionMap, regexpActionMap);
        }
    }

    private static void putActionMap(String requestMethod, String requestPath, Class<?> actionClass, Method actionMethod, Map<Requestor, Handler> commonActionMap, Map<Requestor, Handler> regexpActionMap) {
        // 判断 Request Path 中是否带有占位符
        if (requestPath.matches(".+\\{\\w+\\}.*")) {
            // 将请求路径中的占位符 {\w+} 转换为正则表达式 (\\w+)
            requestPath = StringUtil.replaceAll(requestPath, "\\{\\w+\\}", "(\\\\w+)");
            // 将 Requestor 与 Handler 放入 Regexp Action Map 中
            regexpActionMap.put(new Requestor(requestMethod, requestPath), new Handler(actionClass, actionMethod));
        } else {
            // 将 Requestor 与 Handler 放入 Common Action Map 中
            commonActionMap.put(new Requestor(requestMethod, requestPath), new Handler(actionClass, actionMethod));
        }
    }

    /**
     * 获取 Action Map
     */
    public static Map<Requestor, Handler> getActionMap() {
        return actionMap;
    }
}
