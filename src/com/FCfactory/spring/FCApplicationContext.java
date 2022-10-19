package com.FCfactory.spring;

import java.io.File;
import java.net.URL;

public class FCApplicationContext {
    private Class config;

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

            System.out.println(file.isDirectory());
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
                        System.out.println(className);
                        className = className.replace("\\", ".");
                        Class<?> bean = null;
                        try {
                            //拿到类名
                            bean = classLoader.loadClass(className);

                            //判断该文件是否是bean（是否有component注解）
                            if (bean.isAnnotationPresent(Component.class)) {
                                //就是一个bean
                            }
                        } catch (ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }


                    }
                }
            }
        }
    }

    public Object getBean(String name){
        return null;
    }
}
