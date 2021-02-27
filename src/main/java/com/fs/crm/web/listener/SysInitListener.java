package com.fs.crm.web.listener;

import com.fs.crm.settings.domain.DicType;
import com.fs.crm.settings.domain.DicValue;
import com.fs.crm.settings.service.DicService;
import com.fs.crm.settings.service.impl.DicServiceImpl;
import com.fs.crm.utils.ServiceFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.*;

public class SysInitListener implements ServletContextListener {

    /*
    该方法用来监听上下文域对象，当服务器启动，上下文域对象创建完毕后，立即执行该方法
    event:该参数能取得监听的对象，监听的是什么对象，就可以通过该参数取得什么对象
    现在监听的是上下文域对象，所以通过该参数可以取得上下文域对象
     */
    @Override
    public void contextInitialized(ServletContextEvent event) {
        //System.out.println("上下文域对象创建了");

        System.out.println("服务器缓存处理数据字典开始");
        ServletContext application = event.getServletContext();

        DicService dicService = (DicService) ServiceFactory.getService(new DicServiceImpl());

        /*
        map.put("appellationList",dicValueList1)
        map.put("appellationList",dicValueList1)
        map.put("appellationList",dicValueList1)
        ...
         */
        Map<String, List<DicValue>> map = dicService.getAll();
        //将map解析为上下文域对象中保存的键值对
        Set<String> keySet = map.keySet();
        for (String key : keySet) {
            application.setAttribute(key,map.get(key));
        }

        System.out.println("服务器缓存处理数据字典结束");

        //---------------------------------------------------------------------
        //数据字典处理完毕后,处理Stage2Possibility.properties文件
    /*

           处理Stage2Possibility.properties文件步骤:
            解析改文件,将该属性文件中的键值对关系处理成为java中键值对关系(map)

            Map<String(阶段stage),String(可能性possibility)>pMap = ....
            pMap.put("01资质审查",10)
            pMap.put("02需求分析",25);
            ......
            pMap保存值后，放在服务器缓存中
            application.setAttribute("pMap",pMap)l

     */

        //解析properties文件
        Map<String,String> pMap = new HashMap<>();

        ResourceBundle resourceBundle = ResourceBundle.getBundle("Stage2Possibility");

        Enumeration<String> enumeration = resourceBundle.getKeys();

        while(enumeration.hasMoreElements()){

            //阶段
            String key = enumeration.nextElement();

            //可能性
            String value = resourceBundle.getString(key);

            pMap.put(key,value);

        }

        //将pMap保存到服务器缓存中
        application.setAttribute("pMap",pMap);
    }



}
