package com.FCfactory.spring;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FCApplicationContext {
    private Class config;
    private Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();

    //单例池
    private Map<String, Object> singletonMap = new HashMap<>();

    public FCApplicationContext(Class config) {
        this.config = config;
        //判断改类是否有ComponentScan注解
        if (config.isAnnotationPresent(ComponentScan.class)) {

            //获得该注解
            ComponentScan componentScanAnnotation = (ComponentScan) config.getAnnotation(ComponentScan.class);
            //获得该注解的内容
            String path = componentScanAnnotation.value();
            //处理路径
            path = path.replace(".", "/");

            //获得加载器
            ClassLoader classLoader = FCApplicationContext.class.getClassLoader();
            //获得包含路径的资源
            URL resource = classLoader.getResource(path);
            //E:\workspace2\writeSpringSimulation\springSimulation\out\production\springSimulation\com\FCfactory\service
            File file = new File(resource.getFile());

            //判断是否是目录
            if (file.isDirectory()) {
                //获取目录下所有文件
                File[] files = file.listFiles();
                for (File f : files) {
                    //遍历筛选需要的文件
                    String fileName = f.getAbsolutePath();
//E:\workspace2\writeSpringSimulation\springSimulation\out\production\springSimulation\com\FCfactory\service\UserService.class
                    if (fileName.endsWith(".class")) {


                        //通过反射拿到类
                        String className = fileName.substring(fileName.indexOf("com"), fileName.indexOf(".class"));
//                        com\FCfactory\service\AppConfig
//                        com\FCfactory\service\Test
//                        com\FCfactory\service\UserService

                        className = className.replace("\\", ".");
                        Class<?> bean = null;
                        try {
                            //利用反射 拿到类
                            bean = classLoader.loadClass(className);

                            //判断该类是否是bean（是否有component注解）
                            if (bean.isAnnotationPresent(Component.class)) {
                                //就是一个bean

                                //获得该bean的注解
                                Component componentAnnotation = bean.getAnnotation(Component.class);
                                //获得注解中的值（就是bean的名字）
                                String beanName = componentAnnotation.value();
                                //获得beanDefinition
                                BeanDefinition beanDefinition = new BeanDefinition();
                                //设置bean类型
                                beanDefinition.setType(bean);
                                //判断是否有scope注解（判断是单例还是多例）
                                if (bean.isAnnotationPresent(Scope.class)) {
                                    Scope scopeAnnotation = bean.getAnnotation(Scope.class);
                                    String scope = scopeAnnotation.value();
                                    beanDefinition.setScope(scope);
                                } else {
                                    //一定是单例
                                    beanDefinition.setScope("singleton");
                                }
                                //将beanDefinition存入集合保存
                                beanDefinitionMap.put(beanName, beanDefinition);

                            }
                        } catch (ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }

        //实例化所有的单例bean
        Set<String> beanNames = beanDefinitionMap.keySet();
        for (String beanName : beanNames) {
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            String scope = beanDefinition.getScope();
            if ("singleton".equals(scope)) {
                Object bean = createBean(beanName, beanDefinition);
                singletonMap.put(beanName, bean);
            }
        }

    }

    private Object createBean(String name, BeanDefinition beanDefinition) {
        Class clazz = beanDefinition.getType();
        try {
            Object bean = clazz.getConstructor().newInstance();
            return bean;
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

    }

    public Object getBean(String name) {
        BeanDefinition beanDefinition = beanDefinitionMap.get(name);
        if (beanDefinition == null) {
            throw new NullPointerException();
        } else {
            //存在bean
            if ("singleton".equals(beanDefinition.getScope())) {
                //是单例bean，先从单例池中找，没有的话新建
                Object bean = singletonMap.get(name);
                if (bean == null) {
                    bean = createBean(name, beanDefinition);
                    singletonMap.put(name, bean);
                }
                return bean;
            } else {
                //是多例bean
                Object bean = createBean(name, beanDefinition);
                return bean;
            }
        }
    }
}
